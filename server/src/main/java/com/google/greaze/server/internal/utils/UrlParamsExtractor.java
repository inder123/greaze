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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.UrlParams;
import com.google.greaze.definition.UrlParamsSpec;
import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.definition.internal.utils.FieldNavigator;
import com.google.greaze.definition.internal.utils.GreazePreconditions;
import com.google.greaze.definition.internal.utils.GreazeStrings;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
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

  public UrlParams extractUrlParams(HttpServletRequest request) {
    return extractUrlParams(createParameterMap(request));
  }

  public UrlParams extractUrlParams(Map<String, String> requestParams) {
    if (requestParams.isEmpty()) {
      return new UrlParams.Builder(spec).build();
    }
    try {
      UrlParams.Builder paramsBuilder = new UrlParams.Builder(spec);
      if (spec.hasParamsObject()) {
        Type paramType = spec.getType();
        FieldNavigator navigator = new FieldNavigator(paramType);
        StringWriter json = new StringWriter();
        final JsonWriter jsonWriter = new JsonWriter(json);
        jsonWriter.beginObject();
        ValueReceiver receiver = new ValueReceiver() {
          @SuppressWarnings({"unchecked", "rawtypes"})
          @Override
          public void put(String name, Type type, Object value) throws IOException {
            jsonWriter.name(name);
            if (type instanceof TypeVariable || type == Object.class) {
              // We can not use Gson to extract the value, so just use the specified value.
              jsonWriter.value((String)value);
            } else {
              TypeAdapter adapter = gson.getAdapter(TypeToken.get(type));
              adapter.write(jsonWriter, value);
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

  private void extractUrlParam(String name, Type type, Map<String, String> requestParams,
      ValueReceiver receiver) throws IOException {
    String urlParamValue = requestParams.get(name);
    if (GreazeStrings.isNotEmpty(urlParamValue)) {
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

  /**
   * Extracts the parameters as a Map<String, String>. Throws error if a url parameter
   * is repeated.
   */
  private static Map<String, String> createParameterMap(HttpServletRequest request) {
    Map<String, String> urlParamsStringMap = new HashMap<String, String>();
    @SuppressWarnings("unchecked")
    Map<String, String[]> requestParameterMap = request.getParameterMap();
    for (Map.Entry<String, String[]> param : requestParameterMap.entrySet()) {
      String paramName = param.getKey();
      String[] urlParamValues = param.getValue();
      if (urlParamValues != null) {
        if (urlParamValues.length == 1) {
          urlParamsStringMap.put(paramName, urlParamValues[0]);
        } else if (urlParamValues.length > 1) {
          String value = urlParamValues[0];
          for (String v : urlParamValues) {
            GreazePreconditions.checkArgument(GreazeStrings.equals(value, v),
                "Found multiple values for " + paramName + ":" + urlParamValues
                + ". Greaze supports only one URL parameter value per name.");
          }
        }
      }
    }
    return urlParamsStringMap;
  }

  /** Visible for testing only */
  @SuppressWarnings("unchecked")
  static <T> T parseUrlParamValue(String json, Type type, Gson gson) {
    if (type == String.class) {
      return (T) json;
    }
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
