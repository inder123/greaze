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

import com.google.gson.Gson;

import junit.framework.TestCase;

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
  
  public void testParseUrlParamString() {
    String str = UrlParamsExtractor.parseUrlParamValue("foo+bar", String.class, gson);
    assertEquals("foo bar", str);
  }

  public void testParseUrlParamStringWithOneWord() {
    String str = UrlParamsExtractor.parseUrlParamValue("foo", String.class, gson);
    assertEquals("foo", str);
  }

  public void testParseUrlParamInteger() {
    int value = (Integer) UrlParamsExtractor.parseUrlParamValue("1", Integer.class, gson);
    assertEquals(1, value);
  }

  public void testParseUrlParamEncodedString() {
    String value = UrlParamsExtractor.parseUrlParamValue("foo%2c+bar%2f", String.class, gson);
    assertEquals("foo, bar/", value);
  }
}
