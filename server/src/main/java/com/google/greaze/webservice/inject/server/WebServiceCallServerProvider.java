/*
 * Copyright (C) 2008 Google Inc.
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
package com.google.greaze.webservice.inject.server;

import javax.servlet.http.HttpServletRequest;

import com.google.greaze.definition.webservice.WebServiceCall;
import com.google.greaze.definition.webservice.WebServiceCallSpec;
import com.google.greaze.definition.webservice.WebServiceRequest;
import com.google.greaze.definition.webservice.WebServiceResponse;
import com.google.greaze.webservice.server.RequestReceiver;
import com.google.greaze.webservice.server.WebServiceCallServerBuilder;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Guice provider for {@link WebServiceCall} to be used by a server-side implementation.
 *
 * @author inder
 */
public final class WebServiceCallServerProvider implements Provider<WebServiceCallServerBuilder> {
  private final Gson gson;
  private final WebServiceCallSpec callSpec;
  private final HttpServletRequest request;

  @Inject
  public WebServiceCallServerProvider(Gson gson, HttpServletRequest request, 
      WebServiceCallSpec callSpec) {
    this.callSpec = callSpec;
    this.gson = gson;
    this.request = request;
  }

  @Override
  public WebServiceCallServerBuilder get() {
    RequestReceiver receiver = new RequestReceiver(gson, callSpec.getRequestSpec());
    WebServiceRequest wsRequest = receiver.receive(request);           
    
    WebServiceResponse.Builder responseBuilder = 
      new WebServiceResponse.Builder(callSpec.getResponseSpec());
    return new WebServiceCallServerBuilder(callSpec, wsRequest, responseBuilder);
  }
}