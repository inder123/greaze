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
package com.google.greaze.webservice.server;

import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.UrlParamsSpec;
import com.google.greaze.definition.webservice.RequestBodySpec;
import com.google.greaze.definition.webservice.RequestSpec;
import com.google.greaze.definition.webservice.WebServiceRequest;
import com.google.greaze.server.fixtures.HttpServletRequestFake;
import com.google.gson.Gson;

import junit.framework.TestCase;

import javax.servlet.http.HttpServletRequest;


/**
 * Unit tests for {@link RequestReceiver}
 *
 * @author Inderjeet Singh
 */
public class RequestReceiverTest extends TestCase {

  private Gson gson;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    this.gson = new Gson();
  }
  
  public void testUrlParams() {
    HeaderMapSpec headersSpec = new HeaderMapSpec.Builder().build();
    UrlParamsSpec urlParamSpec = new UrlParamsSpec.Builder().put("foo", String.class).build();
    RequestBodySpec bodySpec = new RequestBodySpec.Builder().build();
    RequestSpec spec = new RequestSpec(headersSpec, urlParamSpec, bodySpec);
    RequestReceiver receiver = new RequestReceiver(gson, spec);
    HttpServletRequest req = new HttpServletRequestFake()
      .setRequestMethod("GET")
      .setUrlParam("foo", "bar");
    WebServiceRequest request = receiver.receive(req);
    assertEquals("bar", request.getUrlParameters().getParamsMap().get("foo"));
  }
}
