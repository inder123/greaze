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
import com.google.greaze.definition.rest.ResourceUrlPaths;
import com.google.greaze.definition.rest.RestCallSpecMap;
import com.google.greaze.definition.rest.RestResource;
import com.google.greaze.definition.rest.query.ResourceQuery;
import com.google.greaze.definition.rest.query.ResourceQueryParams;
import com.google.greaze.rest.server.ResponseBuilderMap;
import com.google.greaze.server.GreazeDispatcherServlet;
import com.google.greaze.server.dispatcher.ResourceQueryDispatcher;
import com.google.greaze.server.filters.GreazeFilterChain;
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

  private final ResourceUrlPaths urlPaths;

  public NetworkSwitcherQuery(ResourceQuery<R, Q> queryHandler,
      Provider<GsonBuilder> serverGsonBuilder, ResourceUrlPaths urlPaths, CallPath queryCallPath,
      GreazeFilterChain filters) {
    this(buildInjector(queryHandler, serverGsonBuilder, urlPaths, queryCallPath, filters), urlPaths);
  }

  public NetworkSwitcherQuery(Injector injector, ResourceUrlPaths urlPaths) {
    super(new GreazeDispatcherServlet(injector, urlPaths.getResourcePrefix(),
        injector.getInstance(GreazeFilterChain.class)));
    this.urlPaths = urlPaths;
  }

  @Override
  protected void switchNetwork(HttpURLConnectionFake conn) throws IOException {
    HttpServletRequest req = new HttpServletRequestFake()
      .setResourceUrlPaths(conn.getURL(), urlPaths)
      .setRequestMethod(conn.getRequestMethod())
      .setHeaders(conn.getHeaders())
      .setInputStream(conn.getForwardForInput());
    OutputStream reverseForOutput = conn.getReverseForOutput();
    HttpServletResponseFake res = new HttpServletResponseFake(reverseForOutput, conn);
    serviceRequest(req, res);
  }

  private static <R extends RestResource<R>, Q extends ResourceQueryParams> Injector buildInjector(
      final ResourceQuery<R, Q> queryHandler, final Provider<GsonBuilder> serverGsonBuilder,
      ResourceUrlPaths urlPaths, CallPath queryCallPath, final GreazeFilterChain filters) {
    @SuppressWarnings("unused")
    Module module = new AbstractModule() {
      @Override
      protected void configure() { }

      @Singleton
      @Provides
      public ResponseBuilderMap getResponseBuilderMap() {
        return new ResponseBuilderMap.Builder().build();
      }

      @Singleton
      @Provides
      public RestCallSpecMap getRestCallSpecMap() {
        return new RestCallSpecMap.Builder().build();
      }

      @Singleton
      @Provides
      public GreazeFilterChain getFilters() {
        return filters == null ? new GreazeFilterChain() : filters;
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
    GreazeServerModule gsm = new GreazeServerModule(
        urlPaths.getServletPath(), ImmutableList.of(queryCallPath), urlPaths.getResourcePrefix());
    return Guice.createInjector(gsm, module);
  }
}
