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

import javax.servlet.http.HttpServletRequest;

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
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;

/**
 * Guice module for common tasks for a Web service
 *
 * @author Inderjeet Singh
 */
public class GreazeServerModule extends ServletModule {

  private final String pathToServlet;

  public GreazeServerModule(String pathToServlet) {
    this.pathToServlet = pathToServlet;
  }

  @Override
  protected void configureServlets() {
    serve(pathToServlet + "/*").with(GreazeDispatcherServlet.class);
  }

  @RequestScoped
  @Provides
  public CallPath getCallPath(HttpServletRequest request, CallPathParser pathParser) {
    String servletPath = request.getServletPath();
    int index = pathToServlet.length();
    return pathParser.parse(servletPath.substring(index));
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
  @SuppressWarnings({"rawtypes", "unchecked"})
  @Provides
  public RestRequestBase getRestRequest(Gson gson, RestCallSpec callSpec, CallPath callPath,
      HttpServletRequest request, ResourceIdFactory<Id<?>> idFactory) {
    RestRequestBaseReceiver requestReceiver =
      new RestRequestBaseReceiver(gson, callSpec.getRequestSpec());
    return requestReceiver.receive(request, idFactory.createId(callPath.getResourceId()));
  }
}