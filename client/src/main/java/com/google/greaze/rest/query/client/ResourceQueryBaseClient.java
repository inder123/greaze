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
package com.google.greaze.rest.query.client;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.UrlParams;
import com.google.greaze.definition.rest.ResourceId;
import com.google.greaze.definition.rest.RestResourceBase;
import com.google.greaze.definition.rest.query.ResourceQueryBase;
import com.google.greaze.definition.rest.query.ResourceQueryParams;
import com.google.greaze.definition.webservice.RequestBody;
import com.google.greaze.definition.webservice.RequestSpec;
import com.google.greaze.definition.webservice.ResponseBody;
import com.google.greaze.definition.webservice.WebServiceCallSpec;
import com.google.greaze.definition.webservice.WebServiceRequest;
import com.google.greaze.definition.webservice.WebServiceResponse;
import com.google.greaze.webservice.client.WebServiceClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.List;

/**
 * A client to invoke {@link ResourceQueryBase}s associated with a REST resource
 * 
 * @author Inderjeet Singh
 *
 * @param <I> ID type of the REST resource
 * @param <R> type of the REST resource
 * @param <Q> Query parameters
 */
public class ResourceQueryBaseClient<
    I extends ResourceId, R extends RestResourceBase<I, R>, Q extends ResourceQueryParams>
        implements ResourceQueryBase<I, R, Q> {

  private final WebServiceClient stub;
  private final WebServiceCallSpec callSpec;
  private final Gson gson;
  private final Type resourceType;
  private final Type queryType;

  /**
   * @param stub stub containing server info to access the rest client
   * @param callPath relative path to the resource
   */
  public ResourceQueryBaseClient(WebServiceClient stub, CallPath callPath,
      Type queryType, GsonBuilder gsonBuilder, Type resourceType) {
    this(stub, ResourceQueryParams.generateCallSpec(callPath, resourceType, queryType),
      queryType, gsonBuilder, resourceType);
  }

  protected ResourceQueryBaseClient(WebServiceClient stub, WebServiceCallSpec callSpec,
      Type queryType, GsonBuilder gsonBuilder, Type resourceType) {
    this.stub = stub;
    this.callSpec = callSpec;
    this.gson = gsonBuilder
      .registerTypeAdapter(RequestBody.class,
          new RequestBody.GsonTypeAdapter(callSpec.getRequestSpec().getBodySpec()))
      .registerTypeAdapter(ResponseBody.class,
          new ResponseBody.GsonTypeAdapter(callSpec.getResponseSpec().getBodySpec()))
      .create();
    this.queryType = queryType;
    this.resourceType = resourceType;
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public List<R> query(Q query) {
    RequestSpec requestSpec = callSpec.getRequestSpec();
    HeaderMap requestHeaders = new HeaderMap.Builder(requestSpec.getHeadersSpec()).build();
    RequestBody requestBody = new RequestBody.Builder(requestSpec.getBodySpec())
      .build();
    UrlParams urlParams = new UrlParams.Builder(requestSpec.getUrlParamsSpec(), query).build();
    WebServiceRequest request =
      new WebServiceRequest(HttpMethod.GET, requestHeaders, urlParams, requestBody);
    WebServiceResponse response = stub.getResponse(callSpec, request, gson);
    ResponseBody body = response.getBody();
    return (List<R>)body.getListBody();
  }

  @Override
  public Type getResourceType() {
    return resourceType;
  }

  @Override
  public Type getQueryType() {
    return queryType;
  }
}
