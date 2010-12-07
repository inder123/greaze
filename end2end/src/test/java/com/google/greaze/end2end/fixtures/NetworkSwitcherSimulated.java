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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.ContentBodySpec;
import com.google.greaze.definition.fixtures.NetworkSwitcher;
import com.google.greaze.definition.rest.Id;
import com.google.greaze.definition.rest.ResourceIdFactory;
import com.google.greaze.definition.rest.RestCallSpec;
import com.google.greaze.definition.rest.RestRequestBase;
import com.google.greaze.definition.rest.RestResource;
import com.google.greaze.definition.rest.RestResponse;
import com.google.greaze.rest.client.ResourceDepotBaseClient;
import com.google.greaze.rest.server.RestResponseBuilder;
import com.google.greaze.server.fixtures.HttpServletRequestFake;
import com.google.greaze.server.inject.GreazeServerModule;
import com.google.gson.Gson;

/**
 * Connects a RequestReceiver to HttpURLConnection
 * 
 * @author Inderjeet Singh
 */
public class NetworkSwitcherSimulated<R extends RestResource<R>> implements NetworkSwitcher {

  private final RestResponseBuilder<R> responseBuilder;
  private final GreazeServerModule gsm;
  private final Type resourceType;
  private final Gson gson;
  public NetworkSwitcherSimulated(RestResponseBuilder<R> responseBuilder, Type resourceType, Gson gson) {
    this.responseBuilder = responseBuilder;
    this.resourceType = resourceType;
    this.gson = gson;
    this.gsm = new GreazeServerModule("/fake");
  }

  @SuppressWarnings("unchecked")
  void switchNetwork() {
    HttpServletRequest req = new HttpServletRequestFake();
    CallPath callPath = gsm.getCallPath(req);
    RestCallSpec spec = ResourceDepotBaseClient.generateRestCallSpec(callPath, resourceType);
    ResourceIdFactory<Id<?>> idFactory = gsm.getIDFactory(spec);
    RestRequestBase<Id<R>, R> request = gsm.getRestRequest(gson, spec, callPath, req, idFactory);
    RestResponse.Builder<R> resBuilder = null;
    responseBuilder.buildResponse(request, resBuilder);
  }

  @Override
  public HttpURLConnection get(URL url) {
    return new HttpURLConnectionFake(url);
  }
  final class HttpURLConnectionFake extends HttpURLConnection {

    private final ByteArrayOutputStream out;
    public HttpURLConnectionFake(URL url) {
      super(url);
      out = new ByteArrayOutputStream();
    }

    @Override
    public void disconnect() {
    }

    @Override
    public boolean usingProxy() {
      return false;
    }

    @Override
    public String getContentType() {
      return ContentBodySpec.JSON_CONTENT_TYPE;
    }
    
    @Override
    public InputStream getInputStream() {
      return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public OutputStream getOutputStream() {
      return out;
    }

    @Override
    public void connect() {
      switchNetwork();
    }
  }
}