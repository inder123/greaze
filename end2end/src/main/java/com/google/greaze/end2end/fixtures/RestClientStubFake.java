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
import com.google.greaze.definition.rest.RestCallSpecMap;
import com.google.greaze.rest.client.RestClientStub;
import com.google.greaze.rest.server.ResponseBuilderMap;
import com.google.greaze.server.filters.GreazeFilterChain;
import com.google.greaze.server.inject.GreazeServerModule;
import com.google.greaze.webservice.client.ServerConfig;
import com.google.gson.GsonBuilder;
import com.google.inject.Injector;

/**
 * A fake for use in tests for {@link RestClientStub}
 *
 * @author Inderjeet Singh
 */
public class RestClientStubFake extends RestClientStub {

  private final NetworkSwitcher networkSwitcher;

  /**
   * @param responseBuilders Rest response builder for the resource
   * @param restCallSpecMap A map of call paths to RestCallSpecs
   * @param serverGson Gson instance used for server-side JSON serialization/deserialization
   * @param servicePaths All the paths for the resources available on the server. Same as
   *   servicePaths parameter for
   *   {@link GreazeServerModule#GreazeServerModule(String, Collection, String)}
   * @param resourcePrefix the resource prefix after the path to Servlet. For example, /resource
   *   for /myshop/resource/1.0/order. Same as resourcePrefix parameter for
   *   {@link GreazeServerModule#GreazeServerModule(String, Collection, String)}
   * @param filters the filter chain to be optionall installed. It is ok to pass null.
   */
  public RestClientStubFake(ResponseBuilderMap responseBuilders, RestCallSpecMap restCallSpecMap,
      GsonBuilder serverGson, Collection<CallPath> servicePaths, String resourcePrefix,
      GreazeFilterChain filters) {
    super(new ServerConfig("http://localhost/fake" + resourcePrefix));
    this.networkSwitcher = new NetworkSwitcherResource(
        responseBuilders, restCallSpecMap, serverGson, servicePaths, resourcePrefix, filters);
  }

  public RestClientStubFake(Injector injector, String resourcePrefix, GreazeFilterChain filters) {
    super(new ServerConfig("http://localhost/fake" + resourcePrefix));
    this.networkSwitcher = new NetworkSwitcherResource(injector, resourcePrefix, filters);
  }

  public String getServiceBaseUrl() {
    return "http://localhost/fake";
  }

  @Override
  public HttpURLConnection openConnection(URL url) {
    return networkSwitcher.get(url);
  }
}
