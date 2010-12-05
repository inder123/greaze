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

import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.UrlParams;
import com.google.greaze.definition.UrlParamsSpec;
import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.definition.internal.utils.FieldNavigator;
import com.google.greaze.definition.internal.utils.GreazePreconditions;
import com.google.greaze.definition.internal.utils.GreazeStrings;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * An analog of UrlParamStringBuilder that converts URL parameters into a {@link HeaderMap}
 *
 * @author Inderjeet Singh
 */
public final class UrlParamsExtractor {

  private final UrlParamsSpec spec;
  private final Gson gson;

  public UrlParamsExtractor(UrlParamsSpec spec, Gson gson) {
    this.spec = spec;
    this.gson = gson;
  }

  public UrlParams extractUrlParams(HttpServletRequest request) {
    UrlParams.Builder paramsBuilder = new UrlParams.Builder(spec);
    if (spec.hasParamsObject()) {
      Type paramType = spec.getType();
      FieldNavigator navigator = new FieldNavigator(paramType);
      StringBuilder json = new StringBuilder('{');
      for (Field f : navigator.getFields()) {
        String name = f.getName();
        Type type = f.getGenericType();
        String[] urlParamValues = request.getParameterValues(name);
        if (urlParamValues != null) {
          GreazePreconditions.checkArgument(urlParamValues.length <= 1,
            "Greaze supports only one URL parameter value per name. For %s, found: %s",
            name, urlParamValues);
          if (urlParamValues.length == 1) {
            Object value = parseUrlParamValue(urlParamValues[0], type, gson);
            json.append(name).append(":'").append(value).append("'");
          }
        }
        json.append('}');
        Object obj = gson.fromJson(json.toString(), paramType);
        paramsBuilder = new UrlParams.Builder(spec, obj);
      }
    } else {
      paramsBuilder = new UrlParams.Builder(spec);
    }
    if (spec.hasParamsMap()) {
      for (Map.Entry<String, Type> param : spec.getMapSpec().entrySet()) {
        String name = param.getKey();
        Type type = param.getValue();
        String[] urlParamValues = request.getParameterValues(name);
        if (urlParamValues != null) {
          GreazePreconditions.checkArgument(urlParamValues.length <= 1,
            "Greaze supports only one URL parameter value per name. For %s, found: %s",
            name, urlParamValues);
          if (urlParamValues.length == 1 && !GreazeStrings.isEmpty(urlParamValues[0])) {
            Object value = parseUrlParamValue(urlParamValues[0], type, gson);
            paramsBuilder.put(name, value);
          }
        }
      }
    }
    return paramsBuilder.build();
  }

  /** Visible for testing only */
  @SuppressWarnings("unchecked")
  static <T> T parseUrlParamValue(String urlParamValue, Type type, Gson gson) {
    Object value;
    try {
      String json = URLDecoder.decode(urlParamValue, "UTF-8");
      try {
        value = gson.fromJson(json, type);
      } catch (JsonSyntaxException e) {
        // Probably an unquoted string, so try that 
        json = '"' + json + '"';
        value = gson.fromJson(json, type);
      }
    } catch (UnsupportedEncodingException e) {
      throw new WebServiceSystemException(e);
    }
    return (T) value;
  }
}
