/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.greaze.server.inject;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.CallPathParser;
import com.google.greaze.definition.rest.Id;
import com.google.greaze.definition.rest.ResourceIdFactory;
import com.google.greaze.definition.rest.RestCallSpec;
import com.google.greaze.definition.rest.RestCallSpecMap;
import com.google.greaze.definition.rest.RestRequestBase;
import com.google.greaze.rest.server.RestRequestBaseReceiver;
import com.google.greaze.server.GreazeDispatcherServlet;
import com.google.gson.Gson;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;

/**
 * Guice module for common tasks for a Web service
 *
 * @author Inderjeet Singh
 */
public class GreazeServerModule extends ServletModule {

  private final String pathToServlet;
  private final Collection<CallPath> servicePaths;
  private final String resourcePrefix;

  /**
   * @param pathToServlet The path to the servlet. For example, /myshop for /myshop/resources/order
   * @param servicePaths a list of paths corresponding to the supported services. For example,
   *   /resource/order for /myshop/resource/order
   * @param resourcePrefix The resource prefix after the pathToServlet. For example, /resource for
   *   /myshop/resource/order
   */
  public GreazeServerModule(String pathToServlet,
      Collection<CallPath> servicePaths, String resourcePrefix) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(pathToServlet));
    Preconditions.checkArgument(!Strings.isNullOrEmpty(resourcePrefix));
    Preconditions.checkArgument(servicePaths != null && !servicePaths.isEmpty());
    this.pathToServlet = pathToServlet;
    this.servicePaths = servicePaths;
    this.resourcePrefix = resourcePrefix;
  }

  @Override
  protected void configureServlets() {
    serve(pathToServlet + "/*").with(GreazeDispatcherServlet.class);
  }

  @Named("resource-prefix")
  @Provides
  @Singleton
  public String getResourcePrefix() {
    return resourcePrefix;
  }

  @RequestScoped
  @Provides
  public CallPath getCallPath(HttpServletRequest request) {
    String servletPath = request.getServletPath();
    int index = pathToServlet.length() + resourcePrefix.length();
    String incomingPath = servletPath.substring(index);
    for (CallPath servicePath : servicePaths) {
      String pathToService = servicePath.get();
      if (incomingPath.startsWith(pathToService)) {
        // Build path with incomingPath and servicePath combo
        CallPathParser pathParser = new CallPathParser(null, servicePath.hasVersion(), pathToService);
        return pathParser.parse(incomingPath);
      }
    }
    return null;
  }

  @RequestScoped
  @Provides
  public RestCallSpec getRestCallSpec(RestCallSpecMap restCallSpecMap, CallPath callPath) {
    RestCallSpec restCallSpec = restCallSpecMap.get(callPath);
    return restCallSpec.createCopy(callPath);
  }

  @RequestScoped
  @Provides
  public ResourceIdFactory<Id<?>> getIDFactory(RestCallSpec callSpec) {
    return new ResourceIdFactory<Id<?>>(Id.class, callSpec.getResourceType());
  }

  @RequestScoped
  @SuppressWarnings("unchecked")
  @Provides
  public RestRequestBase getRestRequest(Gson gson, RestCallSpec callSpec, CallPath callPath,
      HttpServletRequest request, ResourceIdFactory<Id<?>> idFactory) {
    RestRequestBaseReceiver requestReceiver =
      new RestRequestBaseReceiver(gson, callSpec.getRequestSpec());
    return requestReceiver.receive(request, idFactory.createId(callPath.getResourceId()));
  }
}