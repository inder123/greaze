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

import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.WebServiceSystemException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Map;

/**
 * URL parameters for an HTTP request
 *
 * @author Inderjeet Singh
 */
final class UrlParamStringBuilder {
  private final Gson gson;
  private boolean first = true;
  StringBuilder queryParamStr = new StringBuilder();

  UrlParamStringBuilder(Gson gson) {
    this.gson = gson;
  }

  UrlParamStringBuilder add(HeaderMap urlParams) {
    HeaderMapSpec spec = urlParams.getSpec();
    for (Map.Entry<String, Object> entry : urlParams.entrySet()) {
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
      throw new WebServiceSystemException(e);
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
