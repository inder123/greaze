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
package com.google.greaze.definition.webservice;

import com.google.greaze.definition.ContentBodySpec;
import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.TypedKey;

/**
 * The data associated with a Web service request. This includes HTTP request header parameters 
 * (form and URL parameters), and {@link RequestBody}. 
 * 
 * @author inder
 */
public class WebServiceRequest {
  protected final HttpMethod method;
  protected final HeaderMap headers;
  protected final HeaderMap urlParams;
  protected final RequestBody body;
  protected final RequestSpec spec;
  
  public WebServiceRequest(HttpMethod method, HeaderMap requestHeaders,
                           HeaderMap urlParams, RequestBody requestBody) {
    this(method, requestHeaders, urlParams, requestBody,
      new RequestSpec(requestHeaders.getSpec(), urlParams.getSpec(),
        requestBody.getSpec()));
    
  }
  public WebServiceRequest(HttpMethod method, HeaderMap requestHeaders,
                           HeaderMap urlParams, RequestBody requestBody,
                           RequestSpec requestSpec) {
    this.method = method;
    this.body = requestBody;
    this.headers = requestHeaders;
    this.urlParams = urlParams;
    this.spec = requestSpec;
  }

  public HttpMethod getMethod() {
    return method;
  }

  public RequestSpec getSpec() {
    return spec;
  }

  public HttpMethod getHttpMethod() {
    return method;
  }

  public RequestBody getBody() {
    return body;
  }

  public HeaderMap getHeaders() {
    return headers;
  }

  public HeaderMap getUrlParameters() {
    return urlParams;
  }

  public String getContentType() {
    return ContentBodySpec.JSON_CONTENT_TYPE;
  }
  
  public <T> T getHeader(TypedKey<T> headerKey) {
    return headers.get(headerKey);
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("{");
    sb.append(method).append(",");
    sb.append(",headers:").append(headers);
    sb.append(",urlParams:").append(urlParams);
    sb.append(",body:").append(body);
    sb.append("}");
    return sb.toString();
  }
}
