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
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.ImmutableList;
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
  private final Type resourceType;
  private final Collection<CallPath> servicePaths;
  public NetworkSwitcherResource(RestResponseBuilder<R> responseBuilder, Type resourceType,
      Gson gson, CallPath resourceCallPath) {
    super(gson);
    this.responseBuilder = responseBuilder;
    this.resourceType = resourceType;
    this.servicePaths = ImmutableList.of(resourceCallPath);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void switchNetwork(HttpURLConnectionFake conn) {
    HttpServletRequest req = new HttpServletRequestFake()
      .setRequestMethod(conn.getRequestMethod())
      .setServletPath(conn.getURL().getPath())
      .setInputStream(conn.getForwardForInput());
    CallPath callPath = gsm.getCallPath(req, servicePaths);
    RestCallSpec spec = ResourceDepotBaseClient.generateRestCallSpec(callPath, resourceType);
    ResourceIdFactory<Id<?>> idFactory = gsm.getIDFactory(spec);
    RestRequestBase<Id<R>, R> request = gsm.getRestRequest(gson, spec, callPath, req, idFactory);
    RestResponse.Builder<R> response = new RestResponse.Builder<R>(spec.getResponseSpec());
    responseBuilder.buildResponse(request, response);
    RestResponseBase webServiceResponse = response.build();
    RestResponseSender responseSender = new RestResponseSender(gson);
    OutputStream reverseForOutput = conn.getReverseForOutput();
    HttpServletResponseFake res = new HttpServletResponseFake(reverseForOutput);
    responseSender.send(res, webServiceResponse);
    res.flushBuffer();
  }
}
