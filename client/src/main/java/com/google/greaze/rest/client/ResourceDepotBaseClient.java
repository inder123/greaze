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

import java.lang.reflect.Type;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.UrlParams;
import com.google.greaze.definition.UrlParamsSpec;
import com.google.greaze.definition.rest.ResourceDepotBase;
import com.google.greaze.definition.rest.ResourceId;
import com.google.greaze.definition.rest.RestCallSpec;
import com.google.greaze.definition.rest.RestCallSpec.Builder;
import com.google.greaze.definition.rest.IdGsonTypeAdapterFactory;
import com.google.greaze.definition.rest.RestRequestBase;
import com.google.greaze.definition.rest.RestRequestSpec;
import com.google.greaze.definition.rest.RestResourceBase;
import com.google.greaze.definition.rest.RestResponseBase;
import com.google.greaze.definition.rest.WebContext;
import com.google.greaze.definition.rest.WebContextSpec;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
  private final boolean inlined;

  /**
   * @param stub stub containing server info to access the rest client
   * @param callPath relative path to the resource
   * @param resourceType Class for the resource. Such as Cart.class
   * @param inlined set this to true to indicate that the request and response headers should be
   *   sent in the body itself.
   */
  public ResourceDepotBaseClient(RestClientStub stub, CallPath callPath,
      Type resourceType, WebContextSpec webContextSpec, GsonBuilder gsonBuilder, boolean inlined) {
    this(stub, resourceType, generateRestCallSpec(callPath, resourceType, webContextSpec),
        gsonBuilder, inlined);
  }

  protected ResourceDepotBaseClient(RestClientStub stub, Type resourceType,
      RestCallSpec callSpec, GsonBuilder gsonBuilder, boolean inlined) {
    this.stub = stub;
    this.callSpec = callSpec;
    this.resourceType = resourceType;
    this.gson = callSpec.addTypeAdapters(gsonBuilder)
        .registerTypeAdapterFactory(new IdGsonTypeAdapterFactory())
        .create();
    this.inlined = inlined;
  }

  public static RestCallSpec generateRestCallSpec(
      CallPath callPath, Type resourceType, WebContextSpec webContextSpec) {
    Builder builder = new RestCallSpec.Builder(callPath, resourceType)
      .setWebContextSpec(webContextSpec);
    if (callPath.hasVersion()) {
      builder.setVersion(callPath.getVersion());
    }
    return builder.build();
  }

  @Override
  public R get(I resourceId, WebContext context) {
    RestRequestSpec requestSpec = callSpec.getRequestSpec();
    HeaderMap.Builder requestHeadersBuilder = new HeaderMap.Builder(requestSpec.getHeadersSpec());
    if (context != null) {
      context.populate(requestHeadersBuilder);
    }
    HeaderMap requestHeaders = requestHeadersBuilder.build();
    UrlParamsSpec urlParamsSpec = callSpec.getRequestSpec().getUrlParamsSpec();
    UrlParams urlParams = new UrlParams.Builder(urlParamsSpec).build();
    RestRequestBase<I, R> request = new RestRequestBase<I, R>(
        HttpMethod.GET, requestHeaders, urlParams, resourceId, null, resourceType, inlined);
    RestResponseBase<I, R> response = stub.getResponse(callSpec, request, gson);
    return response.getResource();
  }

  @Override
  public R post(R resource, WebContext context) {
    RestRequestSpec requestSpec = callSpec.getRequestSpec();
    HeaderMap.Builder requestHeadersBuilder = new HeaderMap.Builder(requestSpec.getHeadersSpec());
    if (context != null) {
      context.populate(requestHeadersBuilder);
    }
    HeaderMap requestHeaders = requestHeadersBuilder.build();
    UrlParamsSpec urlParamsSpec = callSpec.getRequestSpec().getUrlParamsSpec();
    UrlParams urlParams = new UrlParams.Builder(urlParamsSpec).build();
    RestRequestBase<I, R> request = new RestRequestBase<I, R>(
        HttpMethod.POST, requestHeaders, urlParams, resource.getId(), resource, resourceType, inlined);
    RestResponseBase<I, R> response = stub.getResponse(callSpec, request, gson);
    return response.getResource();
  }

  @Override
  public R put(R resource, WebContext context) {
    RestRequestSpec requestSpec = callSpec.getRequestSpec();
    HeaderMap.Builder requestHeadersBuilder = new HeaderMap.Builder(requestSpec.getHeadersSpec());
    if (context != null) {
      context.populate(requestHeadersBuilder);
    }
    HeaderMap requestHeaders = requestHeadersBuilder.build();
    UrlParamsSpec urlParamsSpec = callSpec.getRequestSpec().getUrlParamsSpec();
    UrlParams urlParams = new UrlParams.Builder(urlParamsSpec).build();
    RestRequestBase<I, R> request = new RestRequestBase<I, R>(
        HttpMethod.PUT, requestHeaders, urlParams, resource.getId(), resource, resourceType, inlined);
    RestResponseBase<I, R> response = stub.getResponse(callSpec, request, gson);
    return response.getResource();
  }

  @Override
  public void delete(I resourceId, WebContext context) {
    RestRequestSpec requestSpec = callSpec.getRequestSpec();
    HeaderMap.Builder requestHeadersBuilder = new HeaderMap.Builder(requestSpec.getHeadersSpec());
    if (context != null) {
      context.populate(requestHeadersBuilder);
    }
    HeaderMap requestHeaders = requestHeadersBuilder.build();
    UrlParamsSpec urlParamsSpec = callSpec.getRequestSpec().getUrlParamsSpec();
    UrlParams urlParams = new UrlParams.Builder(urlParamsSpec).build();
    RestRequestBase<I, R> request = new RestRequestBase<I, R>(
        HttpMethod.DELETE, requestHeaders, urlParams, resourceId, null, resourceType, inlined);
    stub.getResponse(callSpec, request, gson);
  }
}
