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
 */package com.google.greaze.end2end.definition;

import com.google.greaze.definition.rest.query.ResourceQueryParams;

/**
 * Test fixture for a query on {@link Employee} resource
 *
 * @author Inderjeet Singh
 */
public class QueryByName extends ResourceQueryParams {
  private final String name;
  public QueryByName() {
    this(null);
  }
  public QueryByName(String itemName) {
    super("query_by_name");
    this.name = itemName;
  }
}