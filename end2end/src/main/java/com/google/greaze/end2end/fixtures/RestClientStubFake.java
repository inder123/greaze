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

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.fixtures.NetworkSwitcher;
import com.google.greaze.definition.rest.RestResource;
import com.google.greaze.rest.client.RestClientStub;
import com.google.greaze.rest.server.RestResponseBuilder;
import com.google.greaze.webservice.client.ServerConfig;
import com.google.gson.Gson;

/**
 * A fake for use in tests for {@link RestClientStub}
 *
 * @author Inderjeet Singh
 */
public class RestClientStubFake<R extends RestResource<R>> extends RestClientStub {

  private final NetworkSwitcher networkSwitcher;
  public RestClientStubFake(RestResponseBuilder<R> responseBuilder, Type resourceType, Gson gson,
      CallPath resourcePath) {
    super(new ServerConfig("http://localhost/fake" + resourcePath.getPathPrefix()));
    this.networkSwitcher =
      new NetworkSwitcherResource<R>(responseBuilder, resourceType, gson, resourcePath);
  }

  @Override
  protected HttpURLConnection openConnection(URL url) {
    return networkSwitcher.get(url);
  }
}
