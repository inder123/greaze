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

import java.util.Map;

import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.UrlParams;
import com.google.gson.Gson;

/**
 * URL parameters for an HTTP request. This class is made public only for testing.
 *
 * @author Inderjeet Singh
 */
public final class UrlParamStringBuilder {
  private final UrlParamsToStringMapConverter converter;

  public UrlParamStringBuilder(Gson gson) {
    this.converter = new UrlParamsToStringMapConverter(gson);
  }

  public UrlParamStringBuilder add(UrlParams urlParams) {
    converter.add(urlParams);
    return this;
  }

  public UrlParamStringBuilder add(HeaderMap urlParamsMap) {
    converter.add(urlParamsMap);
    return this;
  }

  public String build() {
    StringBuilder queryParamStr = new StringBuilder();
    boolean first = true;
    for (Map.Entry<String, String> param : converter.getMap().entrySet()) {
      if (first) {
        first = false;
        queryParamStr.append('?');
      } else {
        queryParamStr.append('&');
      }
      queryParamStr.append(param.getKey()).append('=').append(param.getValue());
    }
    return queryParamStr.toString();
  }
}
