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
import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.UrlParams;
import com.google.greaze.definition.webservice.RequestBody;
import com.google.greaze.definition.webservice.RequestBodySpec;
import com.google.greaze.definition.webservice.WebServiceRequest;

import java.lang.reflect.Type;

/**
 * The data associated with a Web service request. This includes HTTP request header parameters 
 * (form and URL parameters), and request body.
 *
 * @author Inderjeet Singh
 */
public class RestRequestBase<I extends ResourceId, R extends RestResourceBase<I, R>>
    extends WebServiceRequest {
  private final I id;

  /**
   * @param inlined set this to true to indicate that the request and response headers should be
   *   sent in the body itself.
   */
  public RestRequestBase(HttpMethod method, HeaderMap requestHeaders, UrlParams urlParams,
      I resourceId, R requestBody, Type resourceType, boolean inlined) {
    super(method, requestHeaders, urlParams, createBody(requestBody, resourceType),
      new RestRequestSpec(requestHeaders.getSpec(), resourceType), inlined);
    this.id = resourceId;
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

  @SuppressWarnings("unchecked")
  public R getResource() {
    return (R) body.getSimpleBody();
  }
}
