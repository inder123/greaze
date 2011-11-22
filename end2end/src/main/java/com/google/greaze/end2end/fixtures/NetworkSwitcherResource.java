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
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.rest.Id;
import com.google.greaze.definition.rest.ResourceIdFactory;
import com.google.greaze.definition.rest.RestCallSpec;
import com.google.greaze.definition.rest.RestCallSpecMap;
import com.google.greaze.definition.rest.RestRequestBase;
import com.google.greaze.definition.rest.RestResponse;
import com.google.greaze.definition.rest.RestResponseBase;
import com.google.greaze.definition.rest.WebContext;
import com.google.greaze.rest.server.ResponseBuilderMap;
import com.google.greaze.rest.server.RestResponseBaseBuilder;
import com.google.greaze.rest.server.RestResponseSender;
import com.google.greaze.server.fixtures.HttpServletRequestFake;
import com.google.greaze.server.fixtures.HttpServletResponseFake;
import com.google.greaze.server.inject.GreazeServerModule;
import com.google.gson.Gson;

/**
 * Connects a RequestReceiver to HttpURLConnection
 * 
 * @author Inderjeet Singh
 */
public class NetworkSwitcherResource extends NetworkSwitcherWebService {

  private final ResponseBuilderMap responseBuilders;
  private final RestCallSpecMap restCallSpecMap;

  /**
   * @param responseBuilder Rest response builder for the resource
   * @param restCallSpecMap A map of call paths to RestCallSpecs
   * @param serverGson Gson instance used for server-side JSON serialization/deserialization
   * @param servicePaths All the paths for the resources available on the server. Same as
   *   servicePaths parameter for
   *   {@link GreazeServerModule#GreazeServerModule(String, Collection, String)}
   * @param resourcePrefix the resource prefix after the path to Servlet. For example, /resource
   *   for /myshop/resource/1.0/order. Same as resourcePrefix parameter for
   *   {@link GreazeServerModule#GreazeServerModule(String, Collection, String)}
   */
  public NetworkSwitcherResource(ResponseBuilderMap responseBuilders,
      RestCallSpecMap restCallSpecMap, Gson serverGson,
      Collection<CallPath> servicePaths, String resourcePrefix) {
    super(serverGson, servicePaths, resourcePrefix);
    this.responseBuilders = responseBuilders;
    this.restCallSpecMap = restCallSpecMap;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  protected void switchNetwork(HttpURLConnectionFake conn) {
    HttpServletRequest req = new HttpServletRequestFake()
      .setRequestMethod(conn.getRequestMethod())
      .setServletPath(conn.getURL().getPath())
      .setInputStream(conn.getForwardForInput())
      .setHeaders(conn.getHeaders());
    CallPath callPath = gsm.getCallPath(req);
    RestCallSpec spec = gsm.getRestCallSpec(restCallSpecMap, callPath);
    ResourceIdFactory<Id<?>> idFactory = gsm.getIDFactory(spec);
    RestRequestBase<Id<?>, ?> request = gsm.getRestRequest(serverGson, spec, callPath, req, idFactory);
    RestResponse.Builder<?> response = new RestResponse.Builder(spec.getResponseSpec());
    WebContext context = gsm.getWebContext(req, spec, serverGson);
    RestResponseBaseBuilder responseBuilder = gsm.getResponseBuilder(spec, responseBuilders);
    responseBuilder.buildResponse(context, request, response);
    RestResponseBase webServiceResponse = response.build();
    RestResponseSender responseSender = new RestResponseSender(serverGson);
    OutputStream reverseForOutput = conn.getReverseForOutput();
    HttpServletResponseFake res = new HttpServletResponseFake(reverseForOutput);
    responseSender.send(res, webServiceResponse);
    res.flushBuffer();
  }
}
