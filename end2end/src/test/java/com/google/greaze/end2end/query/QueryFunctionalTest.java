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
package com.google.greaze.end2end.query;

import java.util.List;

import junit.framework.TestCase;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.CallPathParser;
import com.google.greaze.definition.rest.Id;
import com.google.greaze.definition.rest.WebContext;
import com.google.greaze.end2end.definition.Employee;
import com.google.greaze.end2end.definition.QueryEmployeeByName;
import com.google.greaze.end2end.fixtures.ResourceQueryClientFake;
import com.google.greaze.end2end.query.server.QueryHandlerEmployeeByName;
import com.google.greaze.rest.query.client.ResourceQueryClient;
import com.google.greaze.rest.server.Repository;
import com.google.greaze.rest.server.RepositoryInMemory;
import com.google.gson.GsonBuilder;

/**
 * Functional tests for passing resource query parameters
 *
 * @author Inderjeet Singh
 */
public class QueryFunctionalTest extends TestCase {

  private GsonBuilder gsonBuilder;
  private Repository<Employee> employees;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    this.gsonBuilder = new GsonBuilder()
      .registerTypeAdapter(Id.class, new Id.GsonTypeAdapter());
    this.employees = new RepositoryInMemory<Employee>(Employee.class);
  }

  public void testParamsRoundTripWithoutVersion() throws Exception {
    CallPath queryPath =
      new CallPathParser("/rest", false, "/employee").parse("/rest/employee");
    doParamsRoundTrip(queryPath);
  }

  public void testParamsRoundTripWithVersion() throws Exception {
    CallPath queryPath =
      new CallPathParser("/rest", true, "/employee").parse("/rest/1.2/employee");
    doParamsRoundTrip(queryPath);
  }

  private void doParamsRoundTrip(CallPath queryPath) {
    QueryHandlerEmployeeByName query = new QueryHandlerEmployeeByName(employees);
    ResourceQueryClientFake<Employee, QueryEmployeeByName> stub =
      new ResourceQueryClientFake<Employee, QueryEmployeeByName>(query, gsonBuilder, queryPath);
    ResourceQueryClient<Employee, QueryEmployeeByName> queryClient =
      new ResourceQueryClient<Employee, QueryEmployeeByName>(
        stub, queryPath, QueryEmployeeByName.class, gsonBuilder, Employee.class);

    employees.put(new Employee(null, "foo"));
    employees.put(new Employee(null, "foo"));
    employees.put(new Employee(null, "bar"));
    QueryEmployeeByName queryByName = new QueryEmployeeByName("foo");
    List<Employee> results = queryClient.query(queryByName, new WebContext());
    assertEquals(2, results.size());
    for (Employee employee : results) {
      assertEquals("foo", employee.getName());
    }
  }
}
