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

import com.google.common.base.Preconditions;
import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.CallPathParser;
import com.google.greaze.definition.ErrorReason;
import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.definition.rest.IdGsonTypeAdapterFactory;
import com.google.greaze.definition.rest.ResourceUrlPaths;
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
  private static final String BAD_EMPLOYEE = "Evil Employee";

  private GsonBuilder gsonBuilder;
  private Repository<Employee> employees;
  private QueryHandlerEmployeeByName queryHandler;
  private ResourceQueryClient<Employee, QueryEmployeeByName> queryClient;
  private ResourceUrlPaths urlPaths;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    this.gsonBuilder = new GsonBuilder().registerTypeAdapterFactory(new IdGsonTypeAdapterFactory());
    this.employees = new RepositoryInMemory<Employee>();
    this.queryHandler = new QueryHandlerEmployee(employees);

    CallPath queryPath =
        new CallPathParser("/rest", true, "/employee").parse("/rest/1.2/employee");
    this.urlPaths = new ResourceUrlPaths("http://localhost/fake/service/rest",
        "/fake", "/service", "/rest");
    ResourceQueryClientFake<Employee, QueryEmployeeByName> stub =
      new ResourceQueryClientFake<Employee, QueryEmployeeByName>(queryHandler, gsonBuilder,
          urlPaths, queryPath, null);
    this.queryClient = new ResourceQueryClient<Employee, QueryEmployeeByName>(
        stub, queryPath, QueryEmployeeByName.class, gsonBuilder, Employee.class, false);

    employees.put(new Employee(null, "foo"));
    employees.put(new Employee(null, "foo"));
    employees.put(new Employee(null, "bar"));
  }

  public void testParamsRoundTripWithoutVersion() throws Exception {
    CallPath queryPath =
      new CallPathParser("/rest", false, "/employee").parse("/rest/employee");
    ResourceQueryClientFake<Employee, QueryEmployeeByName> stub =
      new ResourceQueryClientFake<Employee, QueryEmployeeByName>(
          queryHandler, gsonBuilder, urlPaths, queryPath, null);
    ResourceQueryClient<Employee, QueryEmployeeByName> queryClient =
        new ResourceQueryClient<Employee, QueryEmployeeByName>(
            stub, queryPath, QueryEmployeeByName.class, gsonBuilder, Employee.class, false);

    QueryEmployeeByName queryByName = new QueryEmployeeByName("foo");
    List<Employee> results = queryClient.query(queryByName, new WebContext());
    assertEquals(2, results.size());
    for (Employee employee : results) {
      assertEquals("foo", employee.getName());
    }
  }

  public void testParamsRoundTripWithVersion() throws Exception {
    List<Employee> results = queryClient.query(new QueryEmployeeByName("foo"), new WebContext());
    assertEquals(2, results.size());
    for (Employee employee : results) {
      assertEquals("foo", employee.getName());
    }
  }

  public void testParamsRoundTripInlined() throws Exception {
    CallPath queryPath =
        new CallPathParser("/rest", true, "/employee").parse("/rest/1.2/employee");
    ResourceQueryClientFake<Employee, QueryEmployeeByName> stub =
      new ResourceQueryClientFake<Employee, QueryEmployeeByName>(
          queryHandler, gsonBuilder, urlPaths, queryPath, null);
    ResourceQueryClient<Employee, QueryEmployeeByName> queryClient =
        new ResourceQueryClient<Employee, QueryEmployeeByName>(
            stub, queryPath, QueryEmployeeByName.class, gsonBuilder, Employee.class, true);

    QueryEmployeeByName queryByName = new QueryEmployeeByName("foo");
    List<Employee> results = queryClient.query(queryByName, new WebContext());
    assertEquals(2, results.size());
    for (Employee employee : results) {
      assertEquals("foo", employee.getName());
    }
  }

  public void testQueryWithAServerError() {
    try {
      queryClient.query(new QueryEmployeeByName(BAD_EMPLOYEE), new WebContext());
      fail();
    } catch (WebServiceSystemException expected) {
      assertEquals(ErrorReason.BAD_REQUEST, expected.getReason());
    }
  }

  private static final class QueryHandlerEmployee extends QueryHandlerEmployeeByName {

    public QueryHandlerEmployee(Repository<Employee> employees) {
      super(employees);
    }

    @Override
    public List<Employee> query(QueryEmployeeByName query, WebContext context) {
      Preconditions.checkArgument(!query.getName().equals(BAD_EMPLOYEE));
      return super.query(query, context);
    }
  }
}
