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

import com.google.common.collect.Lists;
import com.google.greaze.definition.rest.ValueBasedId;
import com.google.greaze.definition.rest.query.ResourceQuery;
import com.google.greaze.example.definition.model.LineItem;
import com.google.greaze.example.definition.model.Order;
import com.google.greaze.example.query.definition.QueryOrdersByItemName;
import com.google.greaze.rest.server.Repository;
import com.google.inject.Inject;

/**
 * sample implementation of a query handler for {@link QUeryOrdersByItemName} query
 *
 * @author Inderjeet Singh
 */
public class QueryHandlerOrdersByItemName
    implements ResourceQuery<ValueBasedId<Order>, Order, QueryOrdersByItemName> {

  private final Repository<ValueBasedId<Order>, Order> orders;

  @Inject
  public QueryHandlerOrdersByItemName(Repository<ValueBasedId<Order>, Order> orders) {
    this.orders = orders;
  }

  @Override
  public List<Order> query(QueryOrdersByItemName query) {
    long repoSize = orders.getNextId().getValue();
    List<Order> results = Lists.newArrayList();
    for (int i = 0; i < repoSize; ++i) {
      ValueBasedId<Order> orderId = ValueBasedId.get(i, Order.class);
      Order order = orders.get(orderId);
      for (LineItem item : order.getPostedCart().getLineItems()) {
        if (item.getName().equals(query.getItemName())) {
          results.add(order);
        }
      }
    }
    return results;
  }

  @Override
  public Type getResourceType() {
    return Order.class;
  }

  @Override
  public Type getQueryType() {
    return QueryOrdersByItemName.class;
  }
}
