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

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.fixtures.NetworkSwitcher;
import com.google.greaze.definition.rest.RestResource;
import com.google.greaze.definition.rest.query.ResourceQuery;
import com.google.greaze.definition.rest.query.ResourceQueryParams;
import com.google.greaze.webservice.client.ServerConfig;
import com.google.greaze.webservice.client.WebServiceClient;
import com.google.gson.GsonBuilder;
import com.google.inject.Injector;
import com.google.inject.Provider;

/**
 * A test fixture for {@link WebServiceClient}
 *
 * @author Inderjeet Singh
 */
public class ResourceQueryClientFake<R extends RestResource<R>, Q extends ResourceQueryParams>
    extends WebServiceClient {

  private final NetworkSwitcher networkSwitcher;
  public ResourceQueryClientFake(ResourceQuery<R, Q> responseBuilder,
      Provider<GsonBuilder> serverGsonBuilder, CallPath queryPath) {
    super(new ServerConfig("http://localhost"));
    networkSwitcher = new NetworkSwitcherQuery<R, Q>(responseBuilder, serverGsonBuilder, queryPath);
  }

  public ResourceQueryClientFake(Injector injector, CallPath queryPath) {
    super(new ServerConfig("http://localhost"));
    networkSwitcher = new NetworkSwitcherQuery<R, Q>(injector, queryPath);
  }

  @Override
  protected HttpURLConnection openConnection(URL url) {
    return networkSwitcher.get(url);
  }
}
