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
package com.google.greaze.end2end.fixtures;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.rest.Id;
import com.google.greaze.definition.rest.ResourceIdFactory;
import com.google.greaze.definition.rest.RestCallSpec;
import com.google.greaze.definition.rest.RestRequestBase;
import com.google.greaze.definition.rest.RestResource;
import com.google.greaze.definition.rest.RestResponse;
import com.google.greaze.rest.client.ResourceDepotBaseClient;
import com.google.greaze.rest.server.RestResponseBuilder;
import com.google.greaze.server.fixtures.HttpServletRequestFake;
import com.google.gson.Gson;

import java.lang.reflect.Type;

import javax.servlet.http.HttpServletRequest;

/**
 * Connects a RequestReceiver to HttpURLConnection
 * 
 * @author Inderjeet Singh
 */
public class NetworkSwitcherResource<R extends RestResource<R>> extends NetworkSwitcherWebService {

  private final RestResponseBuilder<R> responseBuilder;
  private final Type resourceType;
  public NetworkSwitcherResource(RestResponseBuilder<R> responseBuilder, Type resourceType, Gson gson) {
    super(gson);
    this.responseBuilder = responseBuilder;
    this.resourceType = resourceType;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void switchNetwork(HttpURLConnectionFake conn) {
    HttpServletRequest req = new HttpServletRequestFake()
      .setRequestMethod(conn.getRequestMethod())
      .setServletPath(conn.getURL().getPath())
      .setInputStream(conn.getInputStream());
    CallPath callPath = gsm.getCallPath(req);
    RestCallSpec spec = ResourceDepotBaseClient.generateRestCallSpec(callPath, resourceType);
    ResourceIdFactory<Id<?>> idFactory = gsm.getIDFactory(spec);
    RestRequestBase<Id<R>, R> request = gsm.getRestRequest(gson, spec, callPath, req, idFactory);
    RestResponse.Builder<R> resBuilder = new RestResponse.Builder<R>(spec.getResponseSpec());
    responseBuilder.buildResponse(request, resBuilder);
  }
}
