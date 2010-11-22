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
package com.google.greaze.rest.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.greaze.client.internal.utils.ConnectionPreconditions;
import com.google.greaze.definition.ContentBodySpec;
import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.definition.rest.ResourceId;
import com.google.greaze.definition.rest.RestResourceBase;
import com.google.greaze.definition.rest.RestResponseBase;
import com.google.greaze.definition.rest.RestResponseSpec;
import com.google.gson.Gson;

/**
 * Receives a response coming on an {@link HttpURLConnection}.
 * 
 * @author inder
 */
public final class RestResponseReceiver<I extends ResourceId, R extends RestResourceBase<I, R>> {
  private final Gson gson;
  private final RestResponseSpec spec;
  private final Logger logger;
  private final Level logLevel;

  public RestResponseReceiver(Gson gson, RestResponseSpec spec) {
    this(gson, spec, null);
  }
  public RestResponseReceiver(Gson gson, RestResponseSpec spec, Level logLevel) {
    this.gson = gson;
    this.spec = spec;
    this.logger = logLevel == null ? null : Logger.getLogger(RestResponseReceiver.class.getName());
    this.logLevel = logLevel;
  }
  
  public RestResponseBase<I, R> receive(HttpURLConnection conn) {
    try {
      HeaderMapSpec paramSpec = spec.getHeadersSpec();
      Type bodyType = spec.getResourceType();
      // read response
      HeaderMap responseParams = readResponseHeaders(conn, paramSpec);
      R responseBody = readResponseBody(conn, bodyType);
      return new RestResponseBase<I, R>(spec, responseParams, responseBody);
    } catch (IOException e) {
      throw new WebServiceSystemException(e);
    }
  }

  private HeaderMap readResponseHeaders(HttpURLConnection conn, HeaderMapSpec paramsSpec) {    
    HeaderMap.Builder paramsBuilder = new HeaderMap.Builder(paramsSpec);    
    for (Map.Entry<String, Type> entry : paramsSpec.entrySet()) {
      String paramName = entry.getKey();
      String json = conn.getHeaderField(paramName);
      if (json != null) {
        if (logger != null) {
          logger.log(logLevel, String.format("Response Header: %s:%s\n", paramName, json));
        }
        Type typeOfT = paramsSpec.getTypeFor(paramName);
        Object value = gson.fromJson(json, typeOfT);
        paramsBuilder.put(paramName, value, typeOfT);
      }
    }
    return paramsBuilder.build();
  }

  @SuppressWarnings("unchecked")
  private R readResponseBody(
      HttpURLConnection conn, Type resourceType) throws IOException {
    String connContentType = conn.getContentType();
    ConnectionPreconditions.checkArgument(
      connContentType.contains(ContentBodySpec.JSON_CONTENT_TYPE), conn);
    Reader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    R body = (R) gson.fromJson(reader, resourceType);
    return body;
  }
}
