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

import java.lang.reflect.Type;
import java.util.List;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.ContentBodyType;
import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.UntypedKey;
import com.google.greaze.definition.rest.ResourceId;
import com.google.greaze.definition.rest.RestResourceBase;
import com.google.greaze.definition.rest.query.ResourceQuery;
import com.google.greaze.definition.rest.query.ResourceQueryParams;
import com.google.greaze.definition.rest.query.TypedKeysQuery;
import com.google.greaze.definition.webservice.RequestBody;
import com.google.greaze.definition.webservice.RequestSpec;
import com.google.greaze.definition.webservice.ResponseBody;
import com.google.greaze.definition.webservice.WebServiceCallSpec;
import com.google.greaze.definition.webservice.WebServiceRequest;
import com.google.greaze.definition.webservice.WebServiceResponse;
import com.google.greaze.webservice.client.WebServiceClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A client to invoke {@link ResourceQuery}s associated with a REST resource
 * 
 * @author Inderjeet Singh
 *
 * @param <I> ID type of the REST resource
 * @param <R> type of the REST resource
 * @param <Q> Query parameters
 */
public class ResourceQueryClient<
    I extends ResourceId, R extends RestResourceBase<I, R>, Q extends ResourceQueryParams>
        implements ResourceQuery<I, R, Q> {

  private final WebServiceClient stub;
  private final WebServiceCallSpec callSpec;
  private final Gson gson;
  private final Type resourceType;
  private final Type queryType;
  private final UntypedKey keyForResourceList;

  /**
   * @param stub stub containing server info to access the rest client
   * @param callPath relative path to the resource
   */
  public ResourceQueryClient(WebServiceClient stub, CallPath callPath,
      Type queryType, GsonBuilder gsonBuilder, Type resourceType, Type typeOfListOfR) {
    this(stub, generateCallSpec(callPath, typeOfListOfR), queryType, gsonBuilder,
        resourceType, typeOfListOfR);
  }

  protected ResourceQueryClient(WebServiceClient stub, WebServiceCallSpec callSpec,
      Type queryType, GsonBuilder gsonBuilder, Type resourceType, Type typeOfListOfR) {
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
    this.keyForResourceList = TypedKeysQuery.getKeyForResourceList(typeOfListOfR);
  }

  private static WebServiceCallSpec generateCallSpec(CallPath callPath, Type typeOfListOfR) {
    UntypedKey keyForResourceList = TypedKeysQuery.getKeyForResourceList(typeOfListOfR);
    return new WebServiceCallSpec.Builder(ContentBodyType.LIST, callPath)
        .supportsHttpMethod(HttpMethod.GET)
        .addUrlParam(TypedKeysQuery.QUERY_NAME)
        .addUrlParam(TypedKeysQuery.QUERY_VALUE_AS_JSON)
        .addResponseBodyParam(keyForResourceList)
        .build();
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public List<R> query(Q query) {
    RequestSpec requestSpec = callSpec.getRequestSpec();
    HeaderMap requestHeaders = new HeaderMap.Builder(requestSpec.getHeadersSpec()).build();
    RequestBody requestBody = new RequestBody.Builder(requestSpec.getBodySpec())
      .build();
    String queryUrlParamValue = gson.toJson(query, queryType);
    HeaderMap urlParams = new HeaderMap.Builder(requestSpec.getUrlParamsSpec())
      .put(TypedKeysQuery.QUERY_NAME, query.getQueryName())
      .put(TypedKeysQuery.QUERY_VALUE_AS_JSON, queryUrlParamValue)
      .build();
    WebServiceRequest request =
      new WebServiceRequest(HttpMethod.GET, requestHeaders, urlParams, requestBody);
    WebServiceResponse response = stub.getResponse(callSpec, request, gson);
    ResponseBody body = response.getBody();
    List list = body.get(keyForResourceList);
    return list;
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
