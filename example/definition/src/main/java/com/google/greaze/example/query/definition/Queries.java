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

import com.google.greaze.definition.internal.utils.GreazeStrings;

public enum Queries {
  FIND_ORDERS_BY_ITEM_NAME("find_orders_by_item_name");

  private final String queryName;

  private Queries(String queryName) {
    this.queryName = queryName;
  }

  public String getQueryName() {
    return queryName;
  }

  public static Queries getQuery(String queryName) {
    if (queryName == null || GreazeStrings.isEmpty(queryName)) {
      return null;
    }
    if ("find_orders_by_item_name".equals(queryName)) {
      return FIND_ORDERS_BY_ITEM_NAME;
    }
    return null;
  }
  
}
