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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableList;
import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.fixtures.NetworkSwitcherPiped;
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
import com.google.inject.servlet.InstallableGuiceContext;

/**
 * Connects a RequestReceiver to HttpURLConnection
 * 
 * @author Inderjeet Singh
 */
public class NetworkSwitcherQuery<R extends RestResource<R>, Q extends ResourceQueryParams>
    extends NetworkSwitcherPiped {

  private static final String SERVLET_BASE_PATH = "/fake";
  private final CallPath queryCallPath;
  private final GreazeServerModule gsm;
  private final GreazeDispatcherServlet dispatcher;

  public NetworkSwitcherQuery(final ResourceQuery<R, Q> queryHandler, 
      final Provider<GsonBuilder> serverGsonBuilder, CallPath queryCallPath) {
    this.gsm = new GreazeServerModule(
        SERVLET_BASE_PATH, ImmutableList.of(queryCallPath), queryCallPath.getBasePath());
    this.queryCallPath = queryCallPath;

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
    Injector injector = Guice.createInjector(gsm, module);
    this.dispatcher = new GreazeDispatcherServlet(injector, queryCallPath.getBasePath(), null);
  }

  @Override
  protected void switchNetwork(HttpURLConnectionFake conn) throws IOException {
    HttpServletRequest req = new HttpServletRequestFake()
      .setRequestMethod(conn.getRequestMethod())
      .setHeaders(conn.getHeaders())
      .setServletPath(SERVLET_BASE_PATH + queryCallPath.getBasePath() + conn.getURL().getPath())
      .setUrlParams(conn.getURL().getQuery())
      .setInputStream(conn.getForwardForInput());
    OutputStream reverseForOutput = conn.getReverseForOutput();
    HttpServletResponseFake res = new HttpServletResponseFake(reverseForOutput, conn);
    InstallableGuiceContext.install(req, res);
    dispatcher.service(req, res);
    res.flushBuffer();
  }
}