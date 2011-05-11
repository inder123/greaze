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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Map;

import com.google.greaze.definition.ErrorReason;
import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.UrlParams;
import com.google.greaze.definition.UrlParamsSpec;
import com.google.greaze.definition.WebServiceSystemException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * URL parameters for an HTTP request. This class is made public only for testing.
 *
 * @author Inderjeet Singh
 */
public final class UrlParamStringBuilder {
  private final Gson gson;
  private boolean first = true;
  StringBuilder queryParamStr = new StringBuilder();

  public UrlParamStringBuilder(Gson gson) {
    this.gson = gson;
  }

  public UrlParamStringBuilder add(UrlParams urlParams) {
    UrlParamsSpec spec = urlParams.getSpec();
    if (spec.hasParamsMap()) {
      add(urlParams.getParamsMap());
    }
    if (spec.hasParamsObject()) {
      addComposite(null, urlParams.getParamsObject(), spec.getType());
    }
    return this;
  }

  public UrlParamStringBuilder add(HeaderMap urlParamsMap) {
    HeaderMapSpec spec = urlParamsMap.getSpec();
    for (Map.Entry<String, Object> entry : urlParamsMap.entrySet()) {
      Object value = entry.getValue();
      String paramName = entry.getKey();
      Type type = spec.getTypeFor(paramName);
      addComposite(paramName, value, type);
    }
    return this;
  }

  private UrlParamStringBuilder addComposite(String paramName, Object obj, Type type) {
    JsonElement element = gson.toJsonTree(obj, type);
    if (element.isJsonArray()) {
      throw new UnsupportedOperationException(); 
    }
    if (element.isJsonNull()) {
      return this;
    }
    if (element.isJsonPrimitive()) {
      addStringParam(paramName, element.getAsString());
    } else { // is JsonObject, so add individual members as values
      JsonObject json = element.getAsJsonObject();
      for (Map.Entry<String, JsonElement> member: json.entrySet()) {
        JsonElement valueElement = member.getValue();
        String valueAsJson = valueElement.isJsonPrimitive() ? valueElement.getAsString()
            : gson.toJson(valueElement);
        addStringParam(member.getKey(), valueAsJson);
      }
    }
    return this;
  }

  private void addStringParam(String paramName, String value) {
    if (first) {
      first = false;
      queryParamStr.append('?');
    } else {
      queryParamStr.append('&');
    }
    String paramValue = stripQuotesIfString(value);
    try {
      queryParamStr.append(paramName).append('=').append(URLEncoder.encode(paramValue, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new WebServiceSystemException(ErrorReason.UNEXPECTED_PERMANENT_ERROR, e);
    }
  }

  public String build() {
    return queryParamStr.toString();
  }

  /** Visible for testing only */
  static String stripQuotesIfString(String valueAsJson) {
    String paramValue = valueAsJson.startsWith("\"") ?
        valueAsJson.substring(1, valueAsJson.length() -1) : valueAsJson;
    return paramValue;
  }
}
