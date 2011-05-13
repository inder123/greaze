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
package com.google.greaze.server.internal.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.Map;

import junit.framework.TestCase;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.UrlParams;
import com.google.greaze.definition.UrlParamsSpec;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Unit tests for {@link UrlParamsExtractor}
 *
 * @author Inderjeet Singh
 */
public class UrlParamsExtractorTest extends TestCase {
  private Gson gson;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    this.gson = new Gson();
  }
  
  public void testParseUrlParamStringWithOneWord() {
    String str = UrlParamsExtractor.parseUrlParamValue("foo", String.class, gson);
    assertEquals("foo", str);
  }

  public void testParseUrlParamInteger() {
    int value = (Integer) UrlParamsExtractor.parseUrlParamValue("1", Integer.class, gson);
    assertEquals(1, value);
  }

  public void testUrlParams() {
    UrlParamsSpec spec = new UrlParamsSpec.Builder()
      .put("key1", String.class)
      .put("key2", Integer.class)
      .put("key3", Integer.class)
      .build();
    UrlParamsExtractor extractor = new UrlParamsExtractor(spec, gson);
    UrlParams urlParams = extractor.extractUrlParams(new Params("key1=foo%2c+bar%2f&key2=23&key4=1"));
    HeaderMap map = urlParams.getParamsMap();
    assertEquals("foo, bar/", map.get("key1"));
    assertEquals(23, map.get("key2"));
    assertNull(map.get("key3"));
    assertNull(map.get("key4"));
  }

  @SuppressWarnings("unchecked")
  public void testUrlParamsUntypedValue() {
    TypeVariable typeVariableType = GenericType.getTypeVariableType();
    UrlParamsSpec spec = new UrlParamsSpec.Builder()
      .put("key1", String.class)
      .put("key2", Object.class)
      .put("key3", typeVariableType)
      .build();
    UrlParamsExtractor extractor = new UrlParamsExtractor(spec, gson);
    UrlParams urlParams = extractor.extractUrlParams(new Params("key1=foo&key2=2&key3=bar"));
    HeaderMap map = urlParams.getParamsMap();
    assertEquals("foo", map.get("key1"));
    assertEquals("2", map.get("key2"));
    assertEquals("bar", map.get("key3"));
  }

  public void testUrlParamsWithParamsObject() {
    UrlParamsSpec spec = new UrlParamsSpec.Builder()
      .put("key1", String.class)
      .setType(MySelectionFields.class)
      .put("key2", Integer.class)
      .build();
    UrlParamsExtractor extractor = new UrlParamsExtractor(spec, gson);
    UrlParams urlParams = extractor.extractUrlParams(new Params("key1=foo+bar&value=3&name=bob"));
    assertEquals("foo bar", urlParams.getParamsMap().get("key1"));
    MySelectionFields actual = (MySelectionFields) urlParams.getParamsObject();
    assertEquals(3, actual.value);
    assertEquals("bob", actual.name);
    assertNull(actual.occupation);
  }

  private static class Params implements UrlParamsExtractor.NameValueMap {
    private final Map<String, String> params = Maps.newHashMap();

    Params(String src) {
      for (String pair : src.split("&")) {
        String[] parts = pair.split("=");
        Preconditions.checkArgument(parts.length == 2);
        params.put(parts[0], parts[1]);
      }
    }

    @Override
    public String getParameterValue(String name) {
      return params.get(name);
    }
  }

  private static class MySelectionFields {
    int value;
    String name;
    String occupation;
  }

  private static class GenericType<T> {

    @SuppressWarnings("unchecked")
    public static <R> TypeVariable getTypeVariableType() {
      ParameterizedType type = (ParameterizedType) new TypeToken<GenericType<R>>(){}.getType();
      return (TypeVariable) type.getActualTypeArguments()[0];
    }
  }
}
