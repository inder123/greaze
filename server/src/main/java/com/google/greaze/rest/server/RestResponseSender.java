/*
 * Copyright (C) 2008-2010 Google Inc.
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
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import com.google.greaze.definition.ContentBodySpec;
import com.google.greaze.definition.rest.ResourceId;
import com.google.greaze.definition.rest.RestResourceBase;
import com.google.greaze.definition.rest.RestResponseBase;
import com.google.greaze.definition.webservice.WebServiceResponse;
import com.google.greaze.webservice.server.ResponseSender;
import com.google.gson.Gson;

/**
 * Sends a JSON web service response on {@link HttpServletResponse}.
 * 
 * @author Inderjeet Singh
 */
public class RestResponseSender<I extends ResourceId, R extends RestResourceBase<I, R>>
    extends ResponseSender {
  private static final Logger logger = Logger.getLogger(RestResponseSender.class.getCanonicalName());

  public RestResponseSender(Gson gson) {
    super(gson);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void send(HttpServletResponse conn, WebServiceResponse response) {
    if (response instanceof RestResponseBase) {
      send(conn, (RestResponseBase<I, R>)response);
    } else {
      super.send(conn, response);
    }
  }

  public void send(HttpServletResponse conn, RestResponseBase<I, R> response) {
    try {
      sendHeaders(conn, response.getHeaders());
      sendBody(conn, response.getResource());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
 
  // We could reuse the base classes method, sendBody. However, that requires that
  // a ResponseBody.GsonTypeAdapter is registered. We avoid that registration for REST resources
  // with this implementation
  private void sendBody(HttpServletResponse conn, R responseBody) throws IOException {
    conn.setContentType(ContentBodySpec.JSON_CONTENT_TYPE);
    conn.setCharacterEncoding(ContentBodySpec.JSON_CHARACTER_ENCODING);
    String json = gson.toJson(responseBody);
    logger.fine("RESPONSE BODY:" + json);
    conn.getWriter().append(json);
  }
}
