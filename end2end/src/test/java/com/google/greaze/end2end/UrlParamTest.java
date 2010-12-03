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
package com.google.greaze.end2end;

import com.google.greaze.client.internal.utils.UrlParamStringBuilder;
import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.rest.Id;
import com.google.greaze.server.fixtures.HttpServletRequestFake;
import com.google.greaze.server.internal.utils.UrlParamsExtractor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import junit.framework.TestCase;

/**
 * End to end tests for ensuring that URL parameters are handled correctly
 *
 * @author Inderjeet Singh
 */
public class UrlParamTest extends TestCase {
  private Gson gson;
  private UrlParamStringBuilder urlParamBuilder;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    gson = new GsonBuilder()
      .registerTypeAdapter(Id.class, new Id.GsonTypeAdapter())
      .create();
    this.urlParamBuilder = new UrlParamStringBuilder(gson);
  }

  public void testNoParams() {
    String urlParamString = urlParamBuilder.build();
    HeaderMapSpec spec = new HeaderMapSpec.Builder().build();
    UrlParamsExtractor extractor = new UrlParamsExtractor(spec, gson);
    HttpServletRequestFake request = new HttpServletRequestFake().setRequestMethod("GET");
    HeaderMap urlParams = extractor.extractUrlParams(request);
    assertEquals(0, urlParams.entrySet().size());
  }

  public void testOneParam() {
    HeaderMapSpec spec = new HeaderMapSpec.Builder().put("foo", String.class).build();
    HeaderMap urlParameters = new HeaderMap.Builder(spec)
      .put("foo", "bar bar").build();
    String urlParamsString = urlParamBuilder.add(urlParameters).build();
    UrlParamsExtractor extractor = new UrlParamsExtractor(spec, gson);
    HttpServletRequestFake request = new HttpServletRequestFake()
      .setRequestMethod("GET")
      .setUrlParams(urlParamsString);
    HeaderMap urlParams = extractor.extractUrlParams(request);
    assertEquals("bar bar", urlParams.get("foo"));
  }
}
