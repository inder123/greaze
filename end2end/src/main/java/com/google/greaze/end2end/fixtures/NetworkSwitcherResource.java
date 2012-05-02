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
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.rest.RestCallSpecMap;
import com.google.greaze.rest.server.ResponseBuilderMap;
import com.google.greaze.server.GreazeDispatcherServlet;
import com.google.greaze.server.filters.GreazeFilterChain;
import com.google.greaze.server.fixtures.HttpServletRequestFake;
import com.google.greaze.server.fixtures.HttpServletResponseFake;
import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.InstallableGuiceContext;

/**
 * Connects a RequestReceiver to HttpURLConnection
 * 
 * @author Inderjeet Singh
 */
public class NetworkSwitcherResource extends NetworkSwitcherWebService {

  private final GreazeDispatcherServlet dispatcher;

  /**
   * @param responseBuilders Rest response builder for the resource
   * @param restCallSpecMap A map of call paths to RestCallSpecs
   * @param serverGson Gson instance used for server-side JSON serialization/deserialization
   * @param servicePaths All the paths for the resources available on the server. Same as
   *   servicePaths parameter for
   *   {@link GreazeServerModule#GreazeServerModule(String, Collection, String)
   * @param resourcePrefix the resource prefix after the path to Servlet. For example, /resource
   *   for /myshop/resource/1.0/order. Same as resourcePrefix parameter for
   *   {@link GreazeServerModule#GreazeServerModule(String, Collection, String)
   */
  public NetworkSwitcherResource(final ResponseBuilderMap responseBuilders,
      final RestCallSpecMap restCallSpecMap, final Gson serverGson,
      Collection<CallPath> servicePaths, String resourcePrefix, GreazeFilterChain filters) {
    super(serverGson, servicePaths, resourcePrefix);

    @SuppressWarnings("unused")
    Module module = new AbstractModule() {
      @Override
      protected void configure() {
      }
      @Singleton
      @Provides
      public ResponseBuilderMap getResponseBuilderMap() {
        return responseBuilders;
      }
      @Singleton
      @Provides
      public RestCallSpecMap getRestCallSpecMap() {
        return restCallSpecMap;
      }
      @Singleton
      @Provides
      public Gson getGson() {
        return serverGson;
      }
    };
    Injector injector = Guice.createInjector(gsm, module);
    this.dispatcher = new GreazeDispatcherServlet(injector, resourcePrefix, filters);
  }

  @Override
  protected void switchNetwork(HttpURLConnectionFake conn) throws IOException {
    final HttpServletRequest req = new HttpServletRequestFake()
      .setRequestMethod(conn.getRequestMethod())
      .setServletPath(conn.getURL().getPath())
      .setInputStream(conn.getForwardForInput())
      .setHeaders(conn.getHeaders());
    OutputStream reverseForOutput = conn.getReverseForOutput();
    HttpServletResponseFake res = new HttpServletResponseFake(reverseForOutput);
    InstallableGuiceContext.install(req, res);
    dispatcher.service(req, res);
    res.flushBuffer();
  }
}
