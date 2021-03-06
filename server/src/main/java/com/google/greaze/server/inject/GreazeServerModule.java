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
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.CallPathParser;
import com.google.greaze.definition.LogConfig;
import com.google.greaze.definition.rest.Id;
import com.google.greaze.definition.rest.ResourceIdFactory;
import com.google.greaze.definition.rest.RestCallSpec;
import com.google.greaze.definition.rest.RestCallSpecMap;
import com.google.greaze.definition.rest.RestRequestBase;
import com.google.greaze.definition.rest.WebContext;
import com.google.greaze.rest.server.ResponseBuilderMap;
import com.google.greaze.rest.server.RestRequestBaseReceiver;
import com.google.greaze.rest.server.RestResponseBaseBuilder;
import com.google.greaze.server.GreazeDispatcherServlet;
import com.google.greaze.server.internal.utils.WebContextExtractor;
import com.google.gson.GsonBuilder;
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

  private final String greazeDispatcherServletPath;
  private final Collection<CallPath> servicePaths;
  private final String resourcePrefix;
  private final Logger log = Logger.getLogger(GreazeServerModule.class.getSimpleName());

  /**
   * @param greazeDispatcherServletPath The path to the Greaze Dispatcher Servlet
   *   (after excluding the context path).
   *   For example, /myshop for http://localhost/serverContext/myshop/resources/order
   * @param servicePaths a list of paths corresponding to the supported services. For example,
   *   order callPath for /myshop/resource/1.0/order
   * @param resourcePrefix The resource prefix after the pathToServlet. For example, /resource for
   *   /myshop/resource/1.0/order
   */
  public GreazeServerModule(String greazeDispatcherServletPath,
      Collection<CallPath> servicePaths, String resourcePrefix) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(greazeDispatcherServletPath));
    Preconditions.checkArgument(!Strings.isNullOrEmpty(resourcePrefix));
    Preconditions.checkArgument(servicePaths != null && !servicePaths.isEmpty());
    this.greazeDispatcherServletPath = greazeDispatcherServletPath;
    this.servicePaths = servicePaths;
    this.resourcePrefix = resourcePrefix;
  }

  @Override
  protected void configureServlets() {
    serve(greazeDispatcherServletPath + "/*").with(GreazeDispatcherServlet.class);
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
    // Since we map GuiceServlet to * the entire path after context-path becomes Servlet-Path
    String servletPath = request.getServletPath();
    if (LogConfig.FINE) log.fine("Received ServletPath: " + servletPath);
    String incomingPath =  servletPath.substring(greazeDispatcherServletPath.length());
    for (CallPath servicePath : servicePaths) {
      CallPathParser callPathParser = servicePath.toParser();
      try {
        CallPath incomingCallPath = callPathParser.parse(incomingPath);
        if (incomingCallPath.matches(servicePath)) {
          if (LogConfig.FINE) {
            log.fine(String.format(
                "Matched: Incoming path: %s, servicePath: %s", incomingPath, servicePath));
          }
          return callPathParser.parse(incomingPath);
        }
      } catch (CallPathParser.ParseException e) {
      }
    }
    return CallPath.NULL_PATH;
  }

  @RequestScoped
  @Provides
  public RestCallSpec getRestCallSpec(RestCallSpecMap restCallSpecMap, CallPath callPath) {
    RestCallSpec restCallSpec = restCallSpecMap.get(callPath);
    return restCallSpec.createCopy(callPath);
  }

  @RequestScoped
  @SuppressWarnings("rawtypes")
  @Provides
  public RestResponseBaseBuilder getResponseBuilder(
      RestCallSpec callSpec, ResponseBuilderMap responseBuilders) {
    return responseBuilders.get(callSpec.getResourceType());
  }

  @Singleton
  @Provides
  public ResourceIdFactory<Id<?>> getIDFactory() {
    return new ResourceIdFactory<Id<?>>(Id.class);
  }

  @RequestScoped
  @SuppressWarnings({"rawtypes", "unchecked"})
  @Provides
  public RestRequestBase getRestRequest(GsonBuilder gsonBuilder, RestCallSpec callSpec,
      CallPath callPath, HttpServletRequest request, ResourceIdFactory<Id<?>> idFactory) {
      RestRequestBaseReceiver requestReceiver =
          new RestRequestBaseReceiver(gsonBuilder, callSpec.getRequestSpec());
      return requestReceiver.receive(request, idFactory.createId(callPath.getResourceId()));
  }

  @SuppressWarnings("rawtypes")
  @RequestScoped
  @Provides
  public WebContext getWebContext(RestRequestBase request, RestCallSpec spec) {
    WebContextExtractor extractor = new WebContextExtractor(spec.getWebContextSpec());
    return extractor.extract(request.getHeaders());
  }
}
