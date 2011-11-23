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
import com.google.greaze.definition.UrlParams;
import com.google.greaze.definition.UrlParamsSpec;
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
      .registerTypeAdapterFactory(new Id.GsonTypeAdapterFactory())
      .create();
    this.urlParamBuilder = new UrlParamStringBuilder(gson);
  }

  public void testNoParams() {
    UrlParamsSpec spec = new UrlParamsSpec.Builder().build();
    UrlParamsExtractor extractor = new UrlParamsExtractor(spec, gson);
    HttpServletRequestFake request = new HttpServletRequestFake().setRequestMethod("GET");
    UrlParams urlParams = extractor.extractUrlParams(request);
    assertEquals(0, urlParams.getParamsMap().entrySet().size());
  }

  public void testOneParam() {
    UrlParamsSpec spec = new UrlParamsSpec.Builder().put("foo", String.class).build();
    UrlParams urlParameters = new UrlParams.Builder(spec)
      .put("foo", "bar bar").build();
    String urlParamsString = urlParamBuilder.add(urlParameters).build();
    UrlParamsExtractor extractor = new UrlParamsExtractor(spec, gson);
    HttpServletRequestFake request = new HttpServletRequestFake()
      .setRequestMethod("GET")
      .setUrlParams(urlParamsString);
    UrlParams urlParams = extractor.extractUrlParams(request);
    assertEquals("bar bar", urlParams.getParamsMap().get("foo"));
  }
}
