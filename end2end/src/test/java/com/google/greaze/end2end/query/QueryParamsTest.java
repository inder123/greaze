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


import com.google.greaze.definition.CallPath;
import com.google.greaze.end2end.definition.Employee;
import com.google.greaze.end2end.definition.QueryEmployeeByName;
import com.google.greaze.rest.query.client.ResourceQueryClient;
import com.google.greaze.rest.server.Repository;
import com.google.greaze.rest.server.RepositoryInMemory;
import com.google.greaze.rest.server.RestResponseBuilder;
import com.google.greaze.webservice.client.WebServiceClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import junit.framework.TestCase;

import java.util.List;

/**
 * Functional tests for passing resource query parameters
 *
 * @author Inderjeet Singh
 */
public class QueryParamsTest extends TestCase {

  private static final CallPath QUERY_PATH = new CallPath("/rest/query");
  private ResourceQueryClient<Employee, QueryEmployeeByName> queryClient;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.create();
    Repository<Employee> employees = new RepositoryInMemory<Employee>(Employee.class);
    RestResponseBuilder<Employee> responseBuilder = new RestResponseBuilder<Employee>(employees);
    WebServiceClient stub = new WebServiceClientFake<Employee>(responseBuilder, Employee.class, gson);
    queryClient = new ResourceQueryClient<Employee, QueryEmployeeByName>(stub, QUERY_PATH,
        QueryEmployeeByName.class, gsonBuilder, Employee.class);
  }

  public void testParamsRoundTrip() throws Exception {
    QueryEmployeeByName query = new QueryEmployeeByName("foo");
    List<Employee> results = queryClient.query(query);
    assertTrue(results.size() > 0);
    for (Employee employee : results) {
      assertEquals("foo", employee.getName());
    }
  }
}
