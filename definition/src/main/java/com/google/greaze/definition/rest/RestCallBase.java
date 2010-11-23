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
package com.google.greaze.definition.rest;

/**
 * The data associated with a Rest Web service call. This includes http request header parameters
 * (form and URL parameters), request body, response header parameters, and resource response body. 
 * 
 * @author inder
 */
public class RestCallBase<I extends ResourceId, R extends RestResourceBase<I, R>> {
  
  protected final RestCallSpec callSpec;
  protected final RestRequestBase<I, R> request;
  protected final RestResponseBase<I, R> response;
  
  public RestCallBase(RestCallSpec callSpec, RestRequestBase<I, R> request,
      RestResponseBase<I, R> response) {
    this.callSpec = callSpec;
    this.request = request;
    this.response = response;
  }

  public RestCallSpec getSpec() {
    return callSpec;
  }
  
  public RestRequestBase<I, R> getRequest() {
    return request;
  }

  public RestResponseBase<I, R> getResponse() {
    return response;
  }
}