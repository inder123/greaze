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
import com.google.greaze.definition.fixtures.NetworkSwitcherPiped;
import com.google.greaze.definition.rest.RestResource;
import com.google.greaze.definition.rest.query.ResourceQuery;
import com.google.greaze.definition.rest.query.ResourceQueryParams;
import com.google.greaze.server.dispatcher.RequestType;
import com.google.greaze.server.dispatcher.ResourceQueryDispatcher;
import com.google.greaze.server.fixtures.HttpServletRequestFake;
import com.google.greaze.server.fixtures.HttpServletResponseFake;
import com.google.greaze.server.inject.GreazeServerModule;
import com.google.gson.GsonBuilder;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

/**
 * Connects a RequestReceiver to HttpURLConnection
 * 
 * @author Inderjeet Singh
 */
public class NetworkSwitcherQuery<R extends RestResource<R>, Q extends ResourceQueryParams>
    extends NetworkSwitcherPiped {

  /**
   * 
   */
  private static final String SERVLET_BASE_PATH = "/fake";
  private final ResourceQuery<R, Q> query;
  private final GreazeServerModule gsm;
  private final String restPrefix;
  private ResourceQueryDispatcher dispatcher;

  public NetworkSwitcherQuery(ResourceQuery<R, Q> query, GsonBuilder gsonBuilder,
      String restPrefix) {
    this.gsm = new GreazeServerModule(SERVLET_BASE_PATH);
    this.query = query;
    this.restPrefix = restPrefix;
    this.dispatcher = new ResourceQueryDispatcher(gsonBuilder);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  protected void switchNetwork(HttpURLConnectionFake conn) {
    HttpServletRequest req = buildRequest(conn);
    CallPath callPath = gsm.getCallPath(req);
    String queryName = RequestType.getQueryName(req.getParameterMap());
    OutputStream reverseForOutput = conn.getReverseForOutput();
    HttpServletResponseFake res = new HttpServletResponseFake(reverseForOutput);
    dispatcher.service(req, res, queryName, callPath, query);
    res.flushBuffer();
  }

  protected HttpServletRequest buildRequest(HttpURLConnectionFake conn) {
    HttpServletRequest req = new HttpServletRequestFake()
      .setRequestMethod(conn.getRequestMethod())
      .setServletPath(SERVLET_BASE_PATH + conn.getURL().getPath())
      .setUrlParams(conn.getURL().getQuery())
      .setInputStream(conn.getForwardForInput());
    return req;
  }
}