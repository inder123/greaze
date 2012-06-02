/*
 * Copyright (C) 2012 Greaze Authors.
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
package com.google.greaze.definition.webservice;

import java.util.Map;

import com.google.greaze.definition.HttpMethod;

/**
 * Representation of a full web-service request as an object that can be sent inlined
 * as request body or on any other channel.
 *
 * @author Inderjeet Singh
 */
public final class WebServiceRequestInlined {

  private final HttpMethod method;
  private final Map<String, String> headers;
  private final Map<String, String> urlParams;
  private final RequestBody body;
  public WebServiceRequestInlined(HttpMethod method, Map<String, String> headers,
      Map<String, String> urlParams, RequestBody body) {
    this.method = method;
    this.headers = headers;
    this.urlParams = urlParams;
    this.body = body;
  }

  public HttpMethod getMethod() {
    return method;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public Map<String, String> getUrlParams() {
    return urlParams;
  }

  public RequestBody getBody() {
    return body;
  }
}
