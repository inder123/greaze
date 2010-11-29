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

import junit.framework.TestCase;

import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.gson.Gson;

public class WebServiceClientTest extends TestCase {

  private Gson gson;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    this.gson = new Gson();
  }

  public void testNoUrlParams() {
    HeaderMapSpec spec = new HeaderMapSpec.Builder().build();
    HeaderMap urlParameters = new HeaderMap.Builder(spec).build();
    String url = WebServiceClient.buildQueryParamString(gson, urlParameters);
    assertEquals("", url);
  }

  public void testOneUrlParam() {
    HeaderMapSpec spec = new HeaderMapSpec.Builder().put("foo", String.class).build();
    HeaderMap urlParameters = new HeaderMap.Builder(spec)
      .put("foo", "bar bar").build();
    String url = WebServiceClient.buildQueryParamString(gson, urlParameters);
    assertEquals("?foo=bar+bar", url);
  }

  /** Tests for {@link WebServiceClient#stripQuotesIfString(String)} */
  public void testStripsQuotes() {
    assertEquals("foo.bar", WebServiceClient.stripQuotesIfString("\"foo.bar\""));
  }
}
