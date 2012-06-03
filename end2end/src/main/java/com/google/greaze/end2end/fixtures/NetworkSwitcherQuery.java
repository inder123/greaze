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

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableList;
import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.rest.RestCallSpecMap;
import com.google.greaze.definition.rest.RestResource;
import com.google.greaze.definition.rest.query.ResourceQuery;
import com.google.greaze.definition.rest.query.ResourceQueryParams;
import com.google.greaze.rest.server.ResponseBuilderMap;
import com.google.greaze.server.GreazeDispatcherServlet;
import com.google.greaze.server.dispatcher.ResourceQueryDispatcher;
import com.google.greaze.server.fixtures.HttpServletRequestFake;
import com.google.greaze.server.fixtures.HttpServletResponseFake;
import com.google.greaze.server.inject.GreazeServerModule;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Connects a RequestReceiver to HttpURLConnection for a resource query.
 * 
 * @author Inderjeet Singh
 */
public class NetworkSwitcherQuery<R extends RestResource<R>, Q extends ResourceQueryParams>
    extends NetworkSwitcherWebService {

  private static final String SERVLET_BASE_PATH = "/fake";
  private final CallPath queryCallPath;

  public NetworkSwitcherQuery(ResourceQuery<R, Q> queryHandler,
      Provider<GsonBuilder> serverGsonBuilder, CallPath queryCallPath) {
    this(buildInjector(queryHandler, serverGsonBuilder, queryCallPath), queryCallPath);
  }

  public NetworkSwitcherQuery(Injector injector, CallPath queryCallPath) {
    super(new GreazeDispatcherServlet(injector, queryCallPath.getBasePath(), null));
    this.queryCallPath = queryCallPath;
  }

  @Override
  protected void switchNetwork(HttpURLConnectionFake conn) throws IOException {
    URL url = conn.getURL();
    HttpServletRequest req = new HttpServletRequestFake()
      .setUrl(url)
      .setRequestMethod(conn.getRequestMethod())
      .setHeaders(conn.getHeaders())
      .setServletPath(SERVLET_BASE_PATH + queryCallPath.getBasePath() + url.getPath())
      .setInputStream(conn.getForwardForInput());
    OutputStream reverseForOutput = conn.getReverseForOutput();
    HttpServletResponseFake res = new HttpServletResponseFake(reverseForOutput, conn);
    serviceRequest(req, res);
  }

  private static <R extends RestResource<R>, Q extends ResourceQueryParams> Injector buildInjector(
      final ResourceQuery<R, Q> queryHandler, final Provider<GsonBuilder> serverGsonBuilder,
      final CallPath queryCallPath) {
    GreazeServerModule gsm = new GreazeServerModule(
        SERVLET_BASE_PATH, ImmutableList.of(queryCallPath), queryCallPath.getBasePath());

    @SuppressWarnings("unused")
    Module module = new AbstractModule() {
      @Override
      protected void configure() {
      }

      @Singleton
      @Provides
      public ResponseBuilderMap getResponseBuilderMap() {
        return new ResponseBuilderMap.Builder()
          .build();
      }

      @Singleton
      @Provides
      public RestCallSpecMap getRestCallSpecMap() {
        return new RestCallSpecMap.Builder()
          .build();
      }

      @Singleton
      @Provides
      public ResourceQueryDispatcher getResourceQueryDispatcher() {
        return new ResourceQueryDispatcher(serverGsonBuilder) {
          public void service(HttpServletRequest req, HttpServletResponse res,
              String queryName, CallPath callPath) {
            super.service(req, res, queryName, callPath, queryHandler);
          }
        };
      }
    };
    return Guice.createInjector(gsm, module);
  }
}
