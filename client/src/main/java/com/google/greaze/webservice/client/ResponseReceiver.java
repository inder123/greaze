/*
 * Copyright (C) 2008 Google Inc.
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.logging.Logger;

import com.google.greaze.client.internal.utils.ConnectionPreconditions;
import com.google.greaze.definition.ContentBodyType;
import com.google.greaze.definition.ErrorReason;
import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.LogConfig;
import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.definition.internal.utils.GreazeStrings;
import com.google.greaze.definition.internal.utils.Streams;
import com.google.greaze.definition.webservice.ResponseBody;
import com.google.greaze.definition.webservice.ResponseBodySpec;
import com.google.greaze.definition.webservice.ResponseSpec;
import com.google.greaze.definition.webservice.WebServiceResponse;
import com.google.gson.Gson;

/**
 * Receives a response coming on an {@link HttpURLConnection}.
 * 
 * @author inder
 */
public class ResponseReceiver {
  protected final Gson gson;
  protected final ResponseSpec spec;
  protected static final Logger logger = Logger.getLogger(ResponseReceiver.class.getName());

  public ResponseReceiver(Gson gson, ResponseSpec spec) {
    this.gson = gson;
    this.spec = spec;
  }
  
  public WebServiceResponse receive(HttpURLConnection conn) {
    try {
      handleResponseCode(conn);
      HeaderMapSpec paramSpec = spec.getHeadersSpec();
      ResponseBodySpec bodySpec = spec.getBodySpec();
      // read response
      HeaderMap responseParams = readResponseHeaders(conn, paramSpec);
      ResponseBody responseBody = readResponseBody(conn, bodySpec);
      return new WebServiceResponse(responseParams, responseBody);
    } catch (IOException e) {
      throw new WebServiceSystemException(e);
    }
  }

  protected void handleResponseCode(HttpURLConnection conn) throws WebServiceSystemException, IOException {
    // First check response code
    int responseCode = conn.getResponseCode();
    String errorReason = conn.getHeaderField(ErrorReason.HTTP_RESPONSE_HEADER_NAME);
    if (responseCode >= 400 || GreazeStrings.isNotEmpty(errorReason)) {
      ErrorReason reason = GreazeStrings.isEmpty(errorReason)
          ? ErrorReason.fromHttpResponseCode(responseCode)
              : ErrorReason.valueOf(errorReason);
      String msg = Streams.readAsString(conn.getInputStream());
      throw new WebServiceSystemException(reason, "Server input: " + msg);
    }
  }

  protected HeaderMap readResponseHeaders(HttpURLConnection conn, HeaderMapSpec paramsSpec) {
    HeaderMap.Builder paramsBuilder = new HeaderMap.Builder(paramsSpec);    
    for (Map.Entry<String, Type> entry : paramsSpec.entrySet()) {
      String paramName = entry.getKey();
      String json = conn.getHeaderField(paramName);
      if (json != null) {
        if (LogConfig.INFO) logger.info("Response Header: " + paramName + ":" + json);
        Type typeOfT = paramsSpec.getTypeFor(paramName);
        Object value = gson.fromJson(json, typeOfT);
        paramsBuilder.put(paramName, value, typeOfT);
      }
    }
    return paramsBuilder.build();
  }

  protected ResponseBody readResponseBody(HttpURLConnection conn, ResponseBodySpec bodySpec) 
      throws IOException {
    if (bodySpec.size() == 0 && bodySpec.getContentBodyType() == ContentBodyType.MAP) {
      return new ResponseBody.Builder(bodySpec).build();
    }
    String connContentType = conn.getContentType();
    ConnectionPreconditions.checkArgument(connContentType.contains(bodySpec.getContentType()), conn);
    StringWriter writer = new StringWriter();
    Streams.copy(new InputStreamReader(conn.getInputStream()), writer, true, true);
    String json = writer.getBuffer().toString();
    if (LogConfig.INFO) logger.info("Response Body: " + json);
    ResponseBody body = gson.fromJson(json, ResponseBody.class);
    if (body == null) {
      body = new ResponseBody.Builder(spec.getBodySpec()).build();
    }
    return body;
  }
}
