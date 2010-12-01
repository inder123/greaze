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
package com.google.greaze.webservice.client;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.webservice.RequestBody;
import com.google.greaze.definition.webservice.RequestBodySpec;
import com.google.greaze.definition.webservice.WebServiceCallSpec;
import com.google.greaze.definition.webservice.WebServiceRequest;
import com.google.gson.Gson;

import junit.framework.TestCase;

import java.net.URL;

/**
 * Unit tests for {@link WebServiceClient}
 *
 * @author Inderjeet Singh
 */
public class WebServiceClientTest extends TestCase {

  private static final String SERVER_URL = "http://localhost";
  private static final String CALL_PATH = "/resource";
  private Gson gson;
  private WebServiceClient client;
  private WebServiceCallSpec callSpec;
  private HeaderMap requestHeaders;
  private RequestBody requestBody;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    this.gson = new Gson();
    ServerConfig serverConfig = new ServerConfig(SERVER_URL);
    this.client = new WebServiceClient(serverConfig);

    CallPath callPath = new CallPath(CALL_PATH);
    callSpec = new WebServiceCallSpec.Builder(callPath).build();
    HeaderMapSpec headerSpec = new HeaderMapSpec.Builder().build();
    requestHeaders = new HeaderMap.Builder(headerSpec).build();
    RequestBodySpec requestBodySpec = new RequestBodySpec.Builder().build();
    requestBody = new RequestBody.Builder(requestBodySpec).build();
  }

  public void testNoUrlParams() {
    HeaderMapSpec urlParamSpec = new HeaderMapSpec.Builder().build();
    HeaderMap urlParams = new HeaderMap.Builder(urlParamSpec).build();
    WebServiceRequest request =
      new WebServiceRequest(HttpMethod.GET, requestHeaders, urlParams, requestBody);
    URL url = client.getWebServiceUrl(callSpec, request, gson);
    assertEquals(SERVER_URL + CALL_PATH, url.toExternalForm());
  }

  public void testOneUrlParam() {
    HeaderMapSpec urlParamSpec = new HeaderMapSpec.Builder().put("foo", String.class).build();
    HeaderMap urlParams = new HeaderMap.Builder(urlParamSpec)
      .put("foo", "bar bar").build();
    WebServiceRequest request =
      new WebServiceRequest(HttpMethod.GET, requestHeaders, urlParams, requestBody);
    URL url = client.getWebServiceUrl(callSpec, request, gson);
    assertEquals(SERVER_URL + CALL_PATH + "?foo=bar+bar", url.toExternalForm());
  }
}
