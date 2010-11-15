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
package com.google.greaze.example.server;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableMap;
import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.rest.query.ResourceQuery;
import com.google.greaze.definition.rest.query.ResourceQueryParams;
import com.google.greaze.definition.rest.query.TypedKeysQuery;
import com.google.greaze.definition.webservice.ResponseBody;
import com.google.greaze.definition.webservice.ResponseBodySpec;
import com.google.greaze.definition.webservice.WebServiceResponse;
import com.google.greaze.example.query.definition.Queries;
import com.google.greaze.example.query.definition.QueryOrdersByItemName;
import com.google.greaze.webservice.server.ResponseSender;
import com.google.gson.Gson;
import com.google.inject.Inject;

public class ResourceQueryDispatcher {
  private static final Logger log = Logger.getLogger(ResourceQueryDispatcher.class.getSimpleName());

  private final Map<Queries, Type> queryTypes = ImmutableMap.<Queries, Type>builder()
  .put(Queries.FIND_ORDERS_BY_ITEM_NAME, QueryOrdersByItemName.class)
  .build();

  private final Map<Queries, ResourceQuery<?, ?, ?>> queryHandlers =
    ImmutableMap.<Queries, ResourceQuery<?, ?, ?>>builder()
    .build();

  private final Gson gson;

  @Inject
  public ResourceQueryDispatcher(Gson gson) {
    this.gson = gson;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public void service(HttpServletRequest req, HttpServletResponse res, String queryName) {
    Queries query = Queries.getQuery(queryName);
    ResourceQuery resourceQuery = queryHandlers.get(query);
    String jsonValue = req.getParameter(TypedKeysQuery.QUERY_VALUE_AS_JSON.getName());
    log.log(Level.INFO, "Received query: {0} with value: {1}", new Object[]{queryName, jsonValue});
    ResourceQueryParams queryParams = gson.fromJson(jsonValue, queryTypes.get(query));
    List results = resourceQuery.query(queryParams);
    HeaderMapSpec headerSpec = new HeaderMapSpec.Builder().build();
    HeaderMap responseHeaders = new HeaderMap.Builder(headerSpec).build();
    ResponseBodySpec bodySpec = new ResponseBodySpec.Builder()
    .put(TypedKeysQuery.RESOURCE_LIST)
    .build();
    ResponseBody responseBody = new ResponseBody.Builder(bodySpec)
    .put(TypedKeysQuery.RESOURCE_LIST, results)
    .build();
    WebServiceResponse response = new WebServiceResponse(responseHeaders, responseBody);
    ResponseSender responseSender = new ResponseSender(gson);
    responseSender.send(res, response);
  }
}
