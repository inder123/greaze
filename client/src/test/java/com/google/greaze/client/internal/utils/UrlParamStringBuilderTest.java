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
package com.google.greaze.client.internal.utils;

import com.google.greaze.client.internal.utils.UrlParamStringBuilder;
import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.rest.Id;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import junit.framework.TestCase;

/**
 * Unit tests for {@link UrlParamStringBuilder}
 *
 * @author Inderjeet Singh
 */
public class UrlParamStringBuilderTest extends TestCase {
  private UrlParamStringBuilder upBuilder;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    Gson gson = new GsonBuilder()
      .registerTypeAdapter(Id.class, new Id.GsonTypeAdapter())
      .create();
    this.upBuilder = new UrlParamStringBuilder(gson);
  }

  public void testNoUrlParams() {
    String urlParams = upBuilder.build();
    assertEquals("", urlParams);
  }

  public void testOneUrlParam() {
    HeaderMapSpec spec = new HeaderMapSpec.Builder().put("foo", String.class).build();
    HeaderMap urlParameters = new HeaderMap.Builder(spec)
      .put("foo", "bar bar").build();
    String url = upBuilder.add(urlParameters).build();
    assertEquals("?foo=bar+bar", url);
  }

  public void testObjectUrlParam() {
    HeaderMapSpec spec = new HeaderMapSpec.Builder().put("foo", MyParams.class).build();
    HeaderMap urlParameters = new HeaderMap.Builder(spec)
      .put("foo", new MyParams("10", "bar bar")).build();
    String url = upBuilder.add(urlParameters).build();
    assertEquals("?id=10&name=bar+bar", url);
  }

  public void testMixedUrlParams() {
    HeaderMapSpec spec = new HeaderMapSpec.Builder()
      .put("queryName", String.class)
      .put("otherParams", MyParams.class)
      .build();
    HeaderMap urlParameters = new HeaderMap.Builder(spec)
      .put("queryName", "my param query")
      .put("otherParams", new MyParams("12", "a_is;/"))
      .build();
    String url = upBuilder.add(urlParameters).build();
    assertTrue(url.contains("queryName=my+param+query"));
    assertTrue(url.contains("id=12"));
    assertTrue(url.contains("name=a_is%3B%2F"));
  }

  /** Tests for {@link UrlParamStringBuilder#stripQuotesIfString(String)} */
  public void testStripsQuotes() {
    assertEquals("foo.bar", UrlParamStringBuilder.stripQuotesIfString("\"foo.bar\""));
  }

  @SuppressWarnings("unused")
  private static class MyParams {
    final Id<MyParams> id;
    final String name;
    MyParams(String idValue, String name) {
      this.id = Id.get(idValue, MyParams.class);
      this.name = name;
    }
  }
}
