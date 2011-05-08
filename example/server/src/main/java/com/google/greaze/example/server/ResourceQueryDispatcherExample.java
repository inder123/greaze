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

import com.google.common.collect.ImmutableMap;
import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.rest.Id;
import com.google.greaze.definition.rest.MetaData;
import com.google.greaze.definition.rest.MetaDataBase;
import com.google.greaze.definition.rest.query.ResourceQuery;
import com.google.greaze.example.definition.model.Order;
import com.google.greaze.example.query.definition.Queries;
import com.google.greaze.example.service.definition.SampleJsonService;
import com.google.greaze.rest.server.Repository;
import com.google.greaze.server.dispatcher.ResourceQueryDispatcher;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResourceQueryDispatcherExample extends ResourceQueryDispatcher {

  private final Map<Queries, ResourceQuery<?, ?>> queryHandlers;

  @Inject
  public ResourceQueryDispatcherExample(Repository<Order> orders) {
    super(getGsonBuilder());
    queryHandlers = ImmutableMap.<Queries, ResourceQuery<?, ?>>builder()
      .put(Queries.FIND_ORDERS_BY_ITEM_NAME, new QueryHandlerOrdersByItemName(orders))
      .build();

  }

  private static GsonBuilder getGsonBuilder() {
    return new GsonBuilder()
      .setVersion(SampleJsonService.CURRENT_VERSION)
      .registerTypeAdapter(Id.class, new Id.GsonTypeAdapter())
      .registerTypeAdapter(MetaData.class, new MetaDataBase.GsonTypeAdapter());
  }

  @Override
  public void service(HttpServletRequest req, HttpServletResponse res,
      String queryName, CallPath callPath) {
    Queries query = Queries.getQuery(queryName);
    ResourceQuery<?, ?> resourceQuery = queryHandlers.get(query);
    super.service(req, res, queryName, callPath, resourceQuery);
  }
}
