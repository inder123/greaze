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

import com.google.common.collect.ImmutableList;
import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.fixtures.NetworkSwitcher;
import com.google.greaze.definition.rest.ResourceUrlPaths;
import com.google.greaze.definition.rest.RestResource;
import com.google.greaze.definition.rest.query.ResourceQuery;
import com.google.greaze.definition.rest.query.ResourceQueryParams;
import com.google.greaze.server.filters.GreazeFilterChain;
import com.google.greaze.server.inject.GreazeServerModule;
import com.google.greaze.webservice.client.ServerConfig;
import com.google.greaze.webservice.client.WebServiceClient;
import com.google.gson.GsonBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * A test fixture for {@link WebServiceClient}
 *
 * @author Inderjeet Singh
 */
public class ResourceQueryClientFake<R extends RestResource<R>, Q extends ResourceQueryParams>
    extends WebServiceClient {

  private final NetworkSwitcher networkSwitcher;

  public ResourceQueryClientFake(ResourceQuery<R, Q> responseBuilder,
      GsonBuilder serverGsonBuilder, ResourceUrlPaths urlPaths, CallPath queryPath,
      GreazeFilterChain filters) {
    this(buildInjector(responseBuilder, serverGsonBuilder, urlPaths, queryPath, filters), urlPaths);
  }

  public ResourceQueryClientFake(Injector injector, ResourceUrlPaths urlPaths) {
    super(new ServerConfig(urlPaths.getResourceBaseUrl()));
    networkSwitcher = new NetworkSwitcherRest(injector, urlPaths);
  }

  @Override
  protected HttpURLConnection openConnection(URL url) {
    return networkSwitcher.get(url);
  }

  private static <R extends RestResource<R>, Q extends ResourceQueryParams> Injector buildInjector(
      ResourceQuery<R, Q> queryHandler, GsonBuilder serverGsonBuilder,
      ResourceUrlPaths urlPaths, CallPath queryCallPath, GreazeFilterChain filters) {
    GreazeServerModule gsm = new GreazeServerModule(
        urlPaths.getServletPath(), ImmutableList.of(queryCallPath), urlPaths.getResourcePrefix());
    Module module = new RestModuleBuilder()
      .setResourceQueryHandler(queryHandler)
      .setGreazeFilterChain(filters)
      .setServerGsonBuilder(serverGsonBuilder)
      .build();
    return Guice.createInjector(gsm, module);
  }
}
