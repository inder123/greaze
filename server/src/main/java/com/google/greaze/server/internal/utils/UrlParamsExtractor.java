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

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.UrlParams;
import com.google.greaze.definition.UrlParamsSpec;
import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.definition.internal.utils.FieldNavigator;
import com.google.greaze.definition.internal.utils.GreazePreconditions;
import com.google.greaze.definition.internal.utils.GreazePrimitives;
import com.google.greaze.definition.internal.utils.GreazeStrings;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;

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

  interface NameValueMap {
    public String getParameterValue(String name);
  }

  public UrlParams extractUrlParams(final HttpServletRequest request) {
    NameValueMap requestParams = new NameValueMap() {
      @Override
      public String getParameterValue(String name) {
        String[] urlParamValues = request.getParameterValues(name);
        if (urlParamValues != null) {
          GreazePreconditions.checkArgument(urlParamValues.length <= 1,
            "Greaze supports only one URL parameter value per name. For %s, found: %s",
            name, urlParamValues);
          if (urlParamValues.length == 1) {
            return urlParamValues[0];
          }
        }
        return null;
      }
    };
    return extractUrlParams(requestParams);
  }

  /**
   * Visible for testing only
   */
  @SuppressWarnings("unchecked")
  UrlParams extractUrlParams(NameValueMap requestParams) {
    try {
      UrlParams.Builder paramsBuilder = new UrlParams.Builder(spec);
      if (spec.hasParamsObject()) {
        Type paramType = spec.getType();
        FieldNavigator navigator = new FieldNavigator(paramType);
        StringWriter json = new StringWriter();
        final JsonWriter jsonWriter = new JsonWriter(json);
        jsonWriter.beginObject();
        ValueReceiver receiver = new ValueReceiver() {
          @Override
          public void put(String name, Type type, Object value) throws IOException {
            jsonWriter.name(name);
            if (type instanceof TypeVariable || type == Object.class) {
              // We can not use Gson to extract the value, so just use the specified value.
              jsonWriter.value((String)value);
            } else {
              String valueAsString = gson.toJson(value, type);
              if (GreazePrimitives.isPrimitive(type)) {
                if (GreazePrimitives.isFloatingPointType(type)) {
                  jsonWriter.value(Double.parseDouble(valueAsString));
                } else { // Must be a integral number type
                  jsonWriter.value(Long.parseLong(valueAsString));
                }
              } else {
                // Strip extra quotes from the end
                valueAsString = valueAsString.substring(1, valueAsString.length()-1);
                jsonWriter.value(valueAsString);
              }
            }
          }
        };
        for (Field f : navigator.getFields()) {
          extractUrlParam(f.getName(), f.getGenericType(), requestParams, receiver);
        }
        jsonWriter.endObject();
        jsonWriter.close();
        Object obj = gson.fromJson(json.toString(), paramType);
        paramsBuilder = new UrlParams.Builder(spec, obj);
      } else {
        paramsBuilder = new UrlParams.Builder(spec);
      }
      final UrlParams.Builder builder = paramsBuilder;
      if (spec.hasParamsMap()) {
        ValueReceiver receiver = new ValueReceiver() {
          @Override
          public void put(String name, Type type, Object value) {
            builder.put(name, value);
          }
        };
        for (Map.Entry<String, Type> param : spec.getMapSpec().entrySet()) {
          extractUrlParam(param.getKey(), param.getValue(), requestParams, receiver);
        }
      }
      return paramsBuilder.build();
    } catch (MalformedJsonException e) {
      throw new JsonSyntaxException(e);
    } catch (IOException e) {
      throw new JsonIOException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private void extractUrlParam(String name, Type type, NameValueMap requestParams,
      ValueReceiver receiver) throws IOException {
    String urlParamValue = requestParams.getParameterValue(name);
    if (!GreazeStrings.isEmpty(urlParamValue)) {
      urlParamValue = decodeUrlParam(urlParamValue);
      if (type instanceof TypeVariable || type == Object.class) {
        // We can not use Gson to extract the value, so just use the specified value.
        receiver.put(name, type, urlParamValue);
      } else {
        Object value = parseUrlParamValue(urlParamValue, type, gson);
        receiver.put(name, type, value);
      }
    }
  }
  
  private interface ValueReceiver {
    void put(String name, Type type, Object value) throws IOException;
  }

  /** Visible for testing only */
  @SuppressWarnings("unchecked")
  static <T> T parseUrlParamValue(String json, Type type, Gson gson) {
    Object value;
    try {
      value = gson.fromJson(json, type);
    } catch (JsonSyntaxException e) {
      // Probably an unquoted string, so try that 
      json = '"' + json + '"';
      value = gson.fromJson(json, type);
    }
    return (T) value;
  }

   static String decodeUrlParam(String urlParamValue) {
    try {
      return URLDecoder.decode(urlParamValue, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new WebServiceSystemException(e);
    }
  }
}
