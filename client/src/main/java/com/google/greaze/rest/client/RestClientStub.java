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
package com.google.greaze.rest.client;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.definition.rest.Id;
import com.google.greaze.definition.rest.ResourceId;
import com.google.greaze.definition.rest.RestCallSpec;
import com.google.greaze.definition.rest.RestRequest;
import com.google.greaze.definition.rest.RestRequestBase;
import com.google.greaze.definition.rest.RestResource;
import com.google.greaze.definition.rest.RestResourceBase;
import com.google.greaze.definition.rest.RestResponse;
import com.google.greaze.definition.rest.RestResponseBase;
import com.google.greaze.webservice.client.ServerConfig;
import com.google.greaze.webservice.client.WebServiceClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A stub to access the rest service
 * 
 * @author inder
 */
public class RestClientStub extends WebServiceClient {

  public RestClientStub(ServerConfig serverConfig) {
    super(serverConfig);
  }
  
  private <I extends ResourceId> URL getWebServiceUrl(RestCallSpec callSpec, ResourceId id) {
    StringBuilder url = new StringBuilder(buildBasePath(callSpec));
    if (id != null && id.getValue() != null) {
      url.append('/').append(id.getValue());
    }
    try {
      return new URL(url.toString());
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public <R extends RestResource<R>> RestResponse<R> getResponse(
      RestCallSpec callSpec, RestRequest<R> request) {
    return (RestResponse<R>) getResponse(callSpec, (RestRequestBase<Id<R>, R>)request);
  }

  public <I extends ResourceId, R extends RestResourceBase<I, R>> RestResponseBase<I, R> getResponse(
      RestCallSpec callSpec, RestRequestBase<I, R> request) {
    Gson gson = new GsonBuilder().setVersion(callSpec.getVersion()).create();
    return getResponse(callSpec, request, gson);
  }

  public <R extends RestResource<R>> RestResponse<R> getResponse(
      RestCallSpec callSpec, RestRequest<R> request, Gson gson) {
    return (RestResponse<R>) getResponse(callSpec, (RestRequestBase<Id<R>, R>) request, gson);
  }

  public <I extends ResourceId, R extends RestResourceBase<I, R>> RestResponseBase<I, R> getResponse(
      RestCallSpec callSpec, RestRequestBase<I, R> request, Gson gson) {
    HttpURLConnection conn = null;
    try {
      URL webServiceUrl = getWebServiceUrl(callSpec, request.getId());
      conn = openConnection(webServiceUrl);
      return getResponse(callSpec, request, gson, conn);
    } finally {
      closeIgnoringErrors(conn);
    }
  }

  /**
   * Use this method if you want to manage the HTTP Connection yourself. This is useful when you
   * want to use HTTP pipelining.
   */
  public <R extends RestResource<R>> RestResponse<R> getResponse(
      RestCallSpec callSpec, RestRequest<R> request, Gson gson, HttpURLConnection conn) {
    return (RestResponse<R>) getResponse(callSpec, (RestRequestBase<Id<R>, R>) request, gson, conn);
  }

  /**
   * Use this method if you want to manage the HTTP Connection yourself. This is useful when you
   * want to use HTTP pipelining.
   */
  public <I extends ResourceId, R extends RestResourceBase<I, R>> RestResponseBase<I, R> getResponse(
      RestCallSpec callSpec, RestRequestBase<I, R> request, Gson gson, HttpURLConnection conn) {
    try {
      URL webServiceUrl = getWebServiceUrl(callSpec, request.getId());
      logger.log(Level.INFO, "Opening connection to " + webServiceUrl);
      RestRequestSender requestSender = new RestRequestSender(gson);
      requestSender.send(conn, request);
      RestResponseBaseReceiver<I, R> responseReceiver =
        new RestResponseBaseReceiver<I, R>(gson, callSpec.getResponseSpec());
      return responseReceiver.receive(conn);
    } catch (IllegalArgumentException e) {
      throw new WebServiceSystemException(e);
    }
  }
}