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

import com.google.greaze.definition.ContentBodyType;
import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.webservice.RequestBody;
import com.google.greaze.definition.webservice.RequestBodySpec;
import com.google.greaze.definition.webservice.WebServiceRequest;

import java.lang.reflect.Type;

/**
 * The data associated with a Web service request. This includes HTTP request header parameters 
 * (form and URL parameters), and request body. 
 * 
 * @author inder
 */
public class RestRequest<I extends ID, R extends RestResource<I, R>> extends WebServiceRequest {
  private final I id;
  private final R resource;
  
  public RestRequest(HttpMethod method, HeaderMap requestHeaders,
      I resourceId, R requestBody, Type resourceType) {
    super(method, requestHeaders, createUrlParams(),
      createBody(requestBody, resourceType),
      new RestRequestSpec(requestHeaders.getSpec(), resourceType));
    this.id = resourceId;
    this.resource = requestBody;
  }

  private static HeaderMap createUrlParams() {
    HeaderMapSpec spec = new HeaderMapSpec.Builder().build();
    return new HeaderMap.Builder(spec).build();
  }

  private static<R> RequestBody createBody(R resource, Type resourceType) {
    RequestBodySpec spec = new RequestBodySpec(ContentBodyType.SIMPLE, null, resourceType);
    return new RequestBody.Builder(spec)
        .setSimpleBody(resource)
        .build();
  }

  @Override
  public RestRequestSpec getSpec() {
    return (RestRequestSpec)spec;
  }

  public I getId() {
    return id;
  }

  public R getResource() {
    return resource;
  }
}
