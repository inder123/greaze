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
import java.util.logging.Level;

import com.google.greaze.client.internal.utils.ConnectionPreconditions;
import com.google.greaze.definition.ContentBodySpec;
import com.google.greaze.definition.ErrorReason;
import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.definition.rest.ResourceId;
import com.google.greaze.definition.rest.RestResourceBase;
import com.google.greaze.definition.rest.RestResponseBase;
import com.google.greaze.definition.rest.RestResponseSpec;
import com.google.greaze.webservice.client.ResponseReceiver;
import com.google.gson.Gson;

/**
 * Receives a response coming on an {@link HttpURLConnection}.
 * 
 * @author Inderjeet Singh
 */
public class RestResponseBaseReceiver<I extends ResourceId, R extends RestResourceBase<I, R>>
    extends ResponseReceiver {
  private static final int BUF_SIZE = 4096;

  public RestResponseBaseReceiver(Gson gson, RestResponseSpec spec) {
    this(gson, spec, null);
  }
  public RestResponseBaseReceiver(Gson gson, RestResponseSpec spec, Level logLevel) {
    super(gson, spec, logLevel);
  }

  private RestResponseSpec getSpec() {
    return (RestResponseSpec) spec;
  }

  @Override
  public RestResponseBase<I, R> receive(HttpURLConnection conn) {
    try {
      HeaderMapSpec paramSpec = getSpec().getHeadersSpec();
      Type bodyType = getSpec().getResourceType();
      // read response
      HeaderMap responseParams = readResponseHeaders(conn, paramSpec);
      R responseBody = readResponseBody(conn, bodyType);
      return new RestResponseBase<I, R>(getSpec(), responseParams, responseBody);
    } catch (IOException e) {
      ErrorReason reason = ErrorReason.fromValue(conn, e);
      throw new WebServiceSystemException(reason, e);
    }
  }

  // We could reuse the base classes method, readResponseBody. However, that requires that
  // a RequestBody.GsonTypeAdapter is registered. We avoid that registration for REST resources
  // with this implementation
  @SuppressWarnings("unchecked")
  private R readResponseBody(
      HttpURLConnection conn, Type resourceType) throws IOException {
    String connContentType = conn.getContentType();
    ConnectionPreconditions.checkArgument(connContentType != null && 
      connContentType.contains(ContentBodySpec.JSON_CONTENT_TYPE), conn);
    Reader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()), BUF_SIZE);
    R body = (R) gson.fromJson(reader, resourceType);
    return body;
  }
}
