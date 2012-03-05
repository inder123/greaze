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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;

import com.google.greaze.client.internal.utils.ConnectionPreconditions;
import com.google.greaze.definition.ContentBodySpec;
import com.google.greaze.definition.ErrorReason;
import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.LogConfig;
import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.definition.internal.utils.Streams;
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

  public RestResponseBaseReceiver(Gson gson, RestResponseSpec spec) {
    super(gson, spec);
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
    StringWriter writer = new StringWriter();
    Streams.copy(new InputStreamReader(conn.getInputStream()), writer, true, true);
    String json = writer.getBuffer().toString();
    if (LogConfig.INFO) logger.info("Response Body: " + json);
    R body = (R) gson.fromJson(json, resourceType);
    return body;
  }
}
