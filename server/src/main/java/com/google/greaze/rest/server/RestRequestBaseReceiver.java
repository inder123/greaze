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
package com.google.greaze.rest.server;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.UrlParams;
import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.definition.rest.ResourceId;
import com.google.greaze.definition.rest.RestRequestBase;
import com.google.greaze.definition.rest.RestRequestSpec;
import com.google.greaze.definition.rest.RestResourceBase;
import com.google.greaze.webservice.server.RequestData;
import com.google.greaze.webservice.server.RequestReceiver;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

/**
 * Receives and parses a REST request at the server side on a {@link HttpServletRequest}.  
 * 
 * @author Inderjeet Singh
 */
public class RestRequestBaseReceiver<I extends ResourceId, R extends RestResourceBase<I, R>>
    extends RequestReceiver {
  public RestRequestBaseReceiver(GsonBuilder gsonBuilder, RestRequestSpec spec) {
    super(gsonBuilder, spec);
  }

  protected RestRequestSpec getSpec() {
    return (RestRequestSpec) spec;
  }

  public RestRequestBase<I, R> receive(HttpServletRequest request, I resourceId) {
    try {
      RequestData requestData = RequestData.create(request, spec, gson);
      @SuppressWarnings("unchecked")
      R requestBody = requestData.hasRequestBody()
        ? (R) requestData.getRequestBody().getSimpleBody() : null;
      return createRequest(requestData.getMethod(), requestData.getHeaders(),
          requestData.getUrlParams(), resourceId, requestBody, requestData.isInlined());
    } catch (IOException e) {
      throw new WebServiceSystemException(e);
    } catch (JsonParseException e) {
      // Not a Web service request
      throw new WebServiceSystemException(e);
    }
  }

  protected RestRequestBase<I, R> createRequest(HttpMethod method, HeaderMap requestHeaders,
      UrlParams urlParams, I resourceId, R requestBody, boolean inlined) {
    return new RestRequestBase<I, R>(method, requestHeaders, urlParams,
        resourceId, requestBody, getSpec().getResourceType(), inlined);
  }
}
