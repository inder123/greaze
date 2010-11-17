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
package com.google.greaze.example.query.definition;

import com.google.greaze.definition.rest.query.ResourceQueryParams;

/**
 * A query for orders by item name
 *
 * @author Inderjeet Singh
 */
public class QueryOrdersByItemName implements ResourceQueryParams {
  private final String itemName;

  public QueryOrdersByItemName() {
    this(null);
  }

  public QueryOrdersByItemName(String itemName) {
    this.itemName = itemName;
  }
  
  public String getItemName() {
    return itemName;
  }

  @Override
  public String getQueryName() {
    return "orders_by_item_name";
  }
}