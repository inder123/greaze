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

import java.io.OutputStream;
import java.lang.reflect.Type;

import javax.servlet.http.HttpServletRequest;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.rest.Id;
import com.google.greaze.definition.rest.ResourceIdFactory;
import com.google.greaze.definition.rest.RestCallSpec;
import com.google.greaze.definition.rest.RestRequestBase;
import com.google.greaze.definition.rest.RestResource;
import com.google.greaze.definition.rest.RestResponse;
import com.google.greaze.definition.rest.RestResponseBase;
import com.google.greaze.rest.client.ResourceDepotBaseClient;
import com.google.greaze.rest.server.RestResponseBuilder;
import com.google.greaze.rest.server.RestResponseSender;
import com.google.greaze.server.fixtures.HttpServletRequestFake;
import com.google.greaze.server.fixtures.HttpServletResponseFake;
import com.google.gson.Gson;

/**
 * Connects a RequestReceiver to HttpURLConnection
 * 
 * @author Inderjeet Singh
 */
public class NetworkSwitcherResource<R extends RestResource<R>> extends NetworkSwitcherWebService {

  private final RestResponseBuilder<R> responseBuilder;
  private final Type serverResourceType;

  /**
   * @param responseBuilder Rest response builder for the resource
   * @param serverResourceType The Java type for the resource as seen by the server
   * @param serverGson Gson instance used for server-side JSON serialization/deserialization
   * @param resourceCallPath The path where the resource is made available.
   *   For example, /resource/order
   */
  public NetworkSwitcherResource(RestResponseBuilder<R> responseBuilder, Type serverResourceType,
      Gson serverGson, CallPath resourceCallPath) {
    super(serverGson, resourceCallPath);
    this.responseBuilder = responseBuilder;
    this.serverResourceType = serverResourceType;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void switchNetwork(HttpURLConnectionFake conn) {
    HttpServletRequest req = new HttpServletRequestFake()
      .setRequestMethod(conn.getRequestMethod())
      .setServletPath(conn.getURL().getPath())
      .setInputStream(conn.getForwardForInput());
    CallPath callPath = gsm.getCallPath(req);
    RestCallSpec spec = ResourceDepotBaseClient.generateRestCallSpec(callPath, serverResourceType);
    ResourceIdFactory<Id<?>> idFactory = gsm.getIDFactory(spec);
    RestRequestBase<Id<R>, R> request = gsm.getRestRequest(serverGson, spec, callPath, req, idFactory);
    RestResponse.Builder<R> response = new RestResponse.Builder<R>(spec.getResponseSpec());
    responseBuilder.buildResponse(request, response);
    RestResponseBase webServiceResponse = response.build();
    RestResponseSender responseSender = new RestResponseSender(serverGson);
    OutputStream reverseForOutput = conn.getReverseForOutput();
    HttpServletResponseFake res = new HttpServletResponseFake(reverseForOutput);
    responseSender.send(res, webServiceResponse);
    res.flushBuffer();
  }
}
