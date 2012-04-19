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
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.LogConfig;
import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.definition.internal.utils.Streams;
import com.google.greaze.definition.webservice.RequestBody;
import com.google.greaze.definition.webservice.RequestBodySpec;
import com.google.greaze.definition.webservice.WebServiceRequest;
import com.google.gson.Gson;

/**
 * Class to send Web service requests on a {@link HttpURLConnection}.
 * 
 * @author inder
 */
public class RequestSender {
  private static final boolean SIMULATE_POST_WITH_PUT = true;
  private final Gson gson;
  private static final Logger logger = Logger.getLogger(RequestSender.class.getName());

  public RequestSender(Gson gson) {
    this.gson = gson;
  }
  
  public void send(HttpURLConnection conn, WebServiceRequest request) {
    try {
      HttpMethod method = request.getHttpMethod();
      if (SIMULATE_POST_WITH_PUT && method == HttpMethod.PUT) {
        method = HttpMethod.POST;
        setHeader(conn, HttpMethod.SIMULATED_METHOD_HEADER, HttpMethod.PUT.toString(), true);
      }
      if (LogConfig.INFO) logger.info(method + " to " + conn.getURL());
      conn.setRequestMethod(method.toString());
      setHeader(conn, "Content-Type", request.getContentType(), true);
      addRequestParams(conn, request.getHeaders());
      // Assume conservatively that the response will need to be read.
      // This is done here instead of in the response receiver because this property must be set
      // before sending any data on the connection.
      conn.setDoInput(true);
      if (method != HttpMethod.GET) {
        RequestBody requestBody = request.getBody();
        String requestBodyContents = bodyToJson(requestBody);
        if (LogConfig.INFO) logger.log(Level.INFO, "Request Body: " + requestBodyContents);
        // Android Java VM ignore Content-Length if setDoOutput is not set
        conn.setDoOutput(true);
        String contentLength = String.valueOf(requestBodyContents.length());
        setHeader(conn, "Content-Length", contentLength, true);
        Streams.copy(requestBodyContents, conn.getOutputStream(), false);
      }
      // Initiate the sending of the request.
      conn.connect();
    } catch (SocketException e) {
      throw new WebServiceSystemException(e);
    } catch (IOException e) {
      throw new WebServiceSystemException(e);
    }
  }

  private String bodyToJson(RequestBody requestBody) {
    RequestBodySpec spec = requestBody.getSpec();
    switch (spec.getContentBodyType()) {
      case SIMPLE:
        return gson.toJson(requestBody.getSimpleBody(), spec.getBodyJavaType());
      case LIST:
        return gson.toJson(requestBody.getListBody(), spec.getBodyJavaType());
      case MAP:
        return gson.toJson(requestBody, spec.getBodyJavaType());
      default:
        throw new UnsupportedOperationException();
    }
  }

  private void addRequestParams(HttpURLConnection conn, HeaderMap requestParams) {
    HeaderMapSpec spec = requestParams.getSpec();
    for (Map.Entry<String, Object> entry : requestParams.entrySet()) {
      String paramName = entry.getKey();
      Type type = spec.getTypeFor(paramName);
      Object value = entry.getValue();
      String json = gson.toJson(value, type);
      // remove extra quotes
      if (json.startsWith("\"")) {
        json = json.substring(1, json.length()-1);
      }
      setHeader(conn, paramName, json, false);
    }
  }

  private void setHeader(HttpURLConnection conn, String name, String value, boolean overwrite) {
    if (LogConfig.INFO) logger.log(Level.INFO, String.format("Request Header: %s:%s", name, value));
    if (overwrite) {
      conn.setRequestProperty(name, value);
    } else {
      conn.addRequestProperty(name, value);
    }
  }  
}
