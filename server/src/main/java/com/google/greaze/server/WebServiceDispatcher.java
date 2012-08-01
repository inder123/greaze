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
package com.google.greaze.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.greaze.definition.webservice.RequestSpec;
import com.google.greaze.definition.webservice.ResponseBodyGsonTypeAdapterFactory;
import com.google.greaze.definition.webservice.ResponseSpec;
import com.google.greaze.definition.webservice.WebServiceCallSpec;
import com.google.greaze.definition.webservice.WebServiceRequest;
import com.google.greaze.definition.webservice.WebServiceResponse;
import com.google.greaze.webservice.server.RequestReceiver;
import com.google.greaze.webservice.server.ResponseSender;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Injector;

/**
 * A dispatcher for all the procedural calls
 *
 * @author Inderjeet Singh
 */
public abstract class WebServiceDispatcher {

  private final Injector injector;

  public WebServiceDispatcher(Injector injector) {
    this.injector = injector;
  }

  public void service(HttpServletRequest req, HttpServletResponse res) {
    WebServiceCallSpec spec = injector.getInstance(WebServiceCallSpec.class);
    RequestSpec requestSpec = spec.getRequestSpec();
    ResponseSpec responseSpec = spec.getResponseSpec();
    GsonBuilder gsonBuilder = injector.getInstance(GsonBuilder.class);
    RequestReceiver requestReceiver = new RequestReceiver(gsonBuilder, requestSpec);
    WebServiceRequest webServiceRequest = requestReceiver.receive(req);

    Gson gson = injector.getInstance(GsonBuilder.class)
        .registerTypeAdapterFactory(new ResponseBodyGsonTypeAdapterFactory(responseSpec.getBodySpec()))
        .create();
    ResponseSender responseSender = new ResponseSender(gson);
    WebServiceResponse response = buildResponse(responseSpec, webServiceRequest);
    responseSender.send(res, response);
  }

  protected abstract WebServiceResponse buildResponse(ResponseSpec responseSpec,
      WebServiceRequest webServiceRequest);
}
