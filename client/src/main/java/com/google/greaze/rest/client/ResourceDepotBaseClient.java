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

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.rest.ResourceDepotBase;
import com.google.greaze.definition.rest.ResourceId;
import com.google.greaze.definition.rest.RestCallSpec;
import com.google.greaze.definition.rest.RestRequestBase;
import com.google.greaze.definition.rest.RestRequestSpec;
import com.google.greaze.definition.rest.RestResourceBase;
import com.google.greaze.definition.rest.RestResponseBase;
import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * A client class to access a rest resource
 *
 * @author Inderjeet Singh
 */
public class ResourceDepotBaseClient<I extends ResourceId, R extends RestResourceBase<I, R>>
    implements ResourceDepotBase<I, R> {
  private final RestClientStub stub;
  private final RestCallSpec callSpec;
  private final Type resourceType;
  private final Gson gson;

  /**
   * @param stub stub containing server info to access the rest client
   * @param callPath relative path to the resource
   * @param resourceType Class for the resource. Such as Cart.class
   */
  public ResourceDepotBaseClient(RestClientStub stub, CallPath callPath,
      Type resourceType, Gson gson) {
    this(stub, resourceType, generateRestCallSpec(callPath, resourceType), gson);
  }

  protected ResourceDepotBaseClient(RestClientStub stub, Type resourceType,
      RestCallSpec callSpec, Gson gson) {
    this.stub = stub;
    this.callSpec = callSpec;
    this.resourceType = resourceType;
    this.gson = gson;
  }

  private static <T> RestCallSpec generateRestCallSpec(CallPath callPath, Type resourceType) {
    return new RestCallSpec.Builder(callPath, resourceType).build();
  }

  @Override
  public R get(I resourceId) {
    RestRequestSpec requestSpec = callSpec.getRequestSpec();
    HeaderMap requestHeaders = new HeaderMap.Builder(requestSpec.getHeadersSpec()).build();
    HeaderMap urlParams = new HeaderMap.Builder(requestSpec.getUrlParamsSpec()).build();
    RestRequestBase<I, R> request = new RestRequestBase<I, R>(
        HttpMethod.GET, requestHeaders, urlParams, resourceId, null, resourceType);
    RestResponseBase<I, R> response = stub.getResponse(callSpec, request, gson);
    return response.getResource();
  }

  @Override
  public R post(R resource) {
    RestRequestSpec requestSpec = callSpec.getRequestSpec();
    HeaderMap requestHeaders = new HeaderMap.Builder(requestSpec.getHeadersSpec()).build();
    HeaderMap urlParams = new HeaderMap.Builder(requestSpec.getUrlParamsSpec()).build();
    RestRequestBase<I, R> request = new RestRequestBase<I, R>(
        HttpMethod.POST, requestHeaders, urlParams, resource.getId(), resource, resourceType);
    RestResponseBase<I, R> response = stub.getResponse(callSpec, request, gson);
    return response.getResource();
  }

  @Override
  public R put(R resource) {
    RestRequestSpec requestSpec = callSpec.getRequestSpec();
    HeaderMap requestHeaders = new HeaderMap.Builder(requestSpec.getHeadersSpec()).build();
    HeaderMap urlParams = new HeaderMap.Builder(requestSpec.getUrlParamsSpec()).build();
    RestRequestBase<I, R> request = new RestRequestBase<I, R>(
        HttpMethod.PUT, requestHeaders, urlParams, resource.getId(), resource, resourceType);
    RestResponseBase<I, R> response = stub.getResponse(callSpec, request, gson);
    return response.getResource();
  }

  @Override
  public void delete(I resourceId) {
    RestRequestSpec requestSpec = callSpec.getRequestSpec();
    HeaderMap requestHeaders = new HeaderMap.Builder(requestSpec.getHeadersSpec()).build();
    HeaderMap urlParams = new HeaderMap.Builder(requestSpec.getUrlParamsSpec()).build();
    RestRequestBase<I, R> request = new RestRequestBase<I, R>(
        HttpMethod.DELETE, requestHeaders, urlParams, resourceId, null, resourceType);
    stub.getResponse(callSpec, request, gson);
  }
}
