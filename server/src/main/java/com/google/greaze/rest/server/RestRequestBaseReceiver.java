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

import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.UrlParams;
import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.definition.rest.ResourceId;
import com.google.greaze.definition.rest.RestRequestBase;
import com.google.greaze.definition.rest.RestRequestSpec;
import com.google.greaze.definition.rest.RestResourceBase;
import com.google.greaze.server.internal.utils.UrlParamsExtractor;
import com.google.greaze.webservice.server.RequestReceiver;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;

/**
 * Receives and parses a REST request at the server side on a {@link HttpServletRequest}.  
 * 
 * @author Inderjeet Singh
 */
public class RestRequestBaseReceiver<I extends ResourceId, R extends RestResourceBase<I, R>>
    extends RequestReceiver {
  private static final int BUF_SIZE = 4096;

  public RestRequestBaseReceiver(Gson gson, RestRequestSpec spec) {
    super(gson, spec);
  }

  protected RestRequestSpec getSpec() {
    return (RestRequestSpec) spec;
  }

  public RestRequestBase<I, R> receive(HttpServletRequest request, I resourceId) {
    try {
      HeaderMap requestParams = buildRequestParams(request);
      UrlParamsExtractor urlParamsExtractor = new UrlParamsExtractor(spec.getUrlParamsSpec(), gson);
      UrlParams urlParams = urlParamsExtractor.extractUrlParams(request);
      R requestBody = buildRequestBody(request);

      HttpMethod method = HttpMethod.getMethod(request.getMethod());
      String simulatedMethod = request.getHeader(HttpMethod.SIMULATED_METHOD_HEADER);
      if (simulatedMethod != null && !simulatedMethod.equals("")) {
        method = HttpMethod.getMethod(simulatedMethod);
      }
      return createRequest(method, requestParams, urlParams, resourceId, requestBody);
    } catch (IOException e) {
      throw new WebServiceSystemException(e);
    } catch (JsonParseException e) {
      // Not a Web service request
      throw new WebServiceSystemException(e);
    }
  }

  protected RestRequestBase<I, R> createRequest(HttpMethod method, HeaderMap requestHeaders,
      UrlParams urlParams, I resourceId, R requestBody) {
    return new RestRequestBase<I, R>(
        method, requestHeaders, urlParams, resourceId, requestBody, getSpec().getResourceType());
  }

  // We could reuse the base classes method, buildRequestBody. However, that requires that
  // a RequestBody.GsonTypeAdapter is registered. We avoid that registration for REST resources
  // with this implementation
  @SuppressWarnings("unchecked")
  private R buildRequestBody(HttpServletRequest request) throws IOException {
    Reader reader = new BufferedReader(new InputStreamReader(request.getInputStream()), BUF_SIZE);
    R requestBody = (R) gson.fromJson(reader, getSpec().getResourceType());
    return requestBody;
  }
}
