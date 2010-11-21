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
package com.google.greaze.rest.server;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import com.google.greaze.definition.ContentBodySpec;
import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.rest.ResourceId;
import com.google.greaze.definition.rest.RestResourceBase;
import com.google.greaze.definition.rest.RestResponse;
import com.google.gson.Gson;

/**
 * Sends a JSON web service response on {@link HttpServletResponse}.
 * 
 * @author inder
 */
public final class RestResponseSender<I extends ResourceId, R extends RestResourceBase<I, R>> {
  private static final Logger logger = Logger.getLogger(RestResponseSender.class.getCanonicalName());

  private Gson gson;

  public RestResponseSender(Gson gson) {
    this.gson = gson;
  }
  
  public void send(HttpServletResponse conn, RestResponse<I, R> response) {
    try {
      sendHeaders(conn, response.getHeaders());
      sendBody(conn, response.getBody());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
 
  private void sendHeaders(HttpServletResponse conn, HeaderMap responseParams) {
    HeaderMapSpec spec = responseParams.getSpec();
    for (Map.Entry<String, Object> param : responseParams.entrySet()) {
      String paramName = param.getKey();
      Object paramValue = param.getValue();
      Type paramType = spec.getTypeFor(paramName);
      String json = gson.toJson(paramValue, paramType);
      logger.fine("RESPONSE HEADER:{" + paramName + ", " + json + "}");
      conn.addHeader(paramName, json);
    }
  }

  private void sendBody(HttpServletResponse conn, R responseBody) throws IOException {
    conn.setContentType(ContentBodySpec.JSON_CONTENT_TYPE);
    conn.setCharacterEncoding(ContentBodySpec.JSON_CHARACTER_ENCODING);
    String json = gson.toJson(responseBody);
    logger.fine("RESPONSE BODY:" + json);
    conn.getWriter().append(json);
  }
}
