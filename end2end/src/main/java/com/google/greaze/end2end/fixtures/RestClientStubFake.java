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

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.fixtures.NetworkSwitcher;
import com.google.greaze.definition.rest.ResourceUrlPaths;
import com.google.greaze.definition.rest.RestCallSpecMap;
import com.google.greaze.rest.client.RestClientStub;
import com.google.greaze.rest.server.ResponseBuilderMap;
import com.google.greaze.server.filters.GreazeFilterChain;
import com.google.greaze.server.inject.GreazeServerModule;
import com.google.greaze.webservice.client.ServerConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;

/**
 * A fake for use in tests for {@link RestClientStub}
 *
 * @author Inderjeet Singh
 */
public class RestClientStubFake extends RestClientStub {

  private final NetworkSwitcher networkSwitcher;
  private final ResourceUrlPaths urlPaths;

  /**
   * @param responseBuilders Rest response builder for the resource
   * @param restCallSpecMap A map of call paths to RestCallSpecs
   * @param serverGson Gson instance used for server-side JSON serialization/deserialization
   * @param servicePaths All the paths for the resources available on the server. Same as
   *   servicePaths parameter for
   *   {@link GreazeServerModule#GreazeServerModule(String, Collection, String)}
   * @param filters the filter chain to be optional installed. It is ok to pass null.
   */
  public RestClientStubFake(ResponseBuilderMap responseBuilders, RestCallSpecMap restCallSpecMap,
      GsonBuilder serverGson, Collection<CallPath> servicePaths, ResourceUrlPaths urlPaths,
      GreazeFilterChain filters) {
    this(buildInjector(
        responseBuilders, restCallSpecMap, serverGson, servicePaths, urlPaths, filters), urlPaths);
  }

  public RestClientStubFake(Injector injector, ResourceUrlPaths urlPaths) {
    super(new ServerConfig(urlPaths.getResourceBaseUrl()));
    this.urlPaths = urlPaths;
    this.networkSwitcher = new NetworkSwitcherRest(injector, urlPaths);
  }

  public String getServiceBaseUrl() {
    return urlPaths.getContextUrl() + urlPaths.getServletPath();
  }

  @Override
  public HttpURLConnection openConnection(URL url) {
    return networkSwitcher.get(url);
  }

  private static Injector buildInjector(final ResponseBuilderMap responseBuilders,
      final RestCallSpecMap restCallSpecMap, final GsonBuilder serverGson,
      Collection<CallPath> servicePaths, ResourceUrlPaths urlPaths,
      final GreazeFilterChain filters) {
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
      public GreazeFilterChain getFilters() {
        return filters == null ? new GreazeFilterChain() : filters;
      }

      @RequestScoped
      @Provides
      public GsonBuilder getGsonBuilder() {
        return serverGson;
      }

      @RequestScoped
      @Provides
      public Gson getGson() {
        return serverGson.create();
      }
    };
    GreazeServerModule gsm = new GreazeServerModule(
        urlPaths.getServletPath(), servicePaths, urlPaths.getResourcePrefix());
    return Guice.createInjector(gsm, module);
  }
}
