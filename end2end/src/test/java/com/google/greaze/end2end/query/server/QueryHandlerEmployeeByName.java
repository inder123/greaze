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
package com.google.greaze.end2end.query.server;

import java.lang.reflect.Type;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.greaze.definition.rest.Id;
import com.google.greaze.definition.rest.WebContext;
import com.google.greaze.definition.rest.WebContextSpec;
import com.google.greaze.definition.rest.query.ResourceQuery;
import com.google.greaze.end2end.definition.Employee;
import com.google.greaze.end2end.definition.QueryEmployeeByName;
import com.google.greaze.rest.server.Repository;
import com.google.inject.Inject;

/**
 * Server-side query handler for {@link QueryEmployeeByName}
 *
 * @author Inderjeet Singh
 */
public class QueryHandlerEmployeeByName implements ResourceQuery<Employee, QueryEmployeeByName> {

  private final Repository<Employee> employees;

  @Inject
  public QueryHandlerEmployeeByName(Repository<Employee> employees) {
    this.employees = employees;
  }

  @Override
  public List<Employee> query(QueryEmployeeByName query, WebContext context) {
    List<Employee> results = Lists.newArrayList();
    for (int i = 0; i < employees.size(); ++i) {
      Id<Employee> id = Id.get(String.valueOf(i));
      Employee employee = employees.get(id);
      if (employee != null && employee.getName().equals(query.getName())) {
        results.add(employee);
      }
    }
    return results;
  }

  @Override
  public Type getResourceType() {
    return Employee.class;
  }

  @Override
  public Type getQueryType() {
    return QueryEmployeeByName.class;
  }

  @Override
  public WebContextSpec getWebContextSpec() {
    return new WebContextSpec();
  }
}
