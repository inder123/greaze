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
package com.google.greaze.end2end.resources;

import junit.framework.TestCase;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.CallPathParser;
import com.google.greaze.definition.ErrorReason;
import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.definition.rest.Id;
import com.google.greaze.definition.rest.IdGsonTypeAdapterFactory;
import com.google.greaze.definition.rest.ResourceUrlPaths;
import com.google.greaze.definition.rest.RestCallSpec;
import com.google.greaze.definition.rest.RestCallSpecMap;
import com.google.greaze.definition.rest.WebContext;
import com.google.greaze.end2end.definition.Employee;
import com.google.greaze.end2end.fixtures.RestClientStubFake;
import com.google.greaze.rest.client.ResourceDepotBaseClient;
import com.google.greaze.rest.client.ResourceDepotClient;
import com.google.greaze.rest.client.RestClientStub;
import com.google.greaze.rest.server.Repository;
import com.google.greaze.rest.server.RepositoryInMemory;
import com.google.greaze.rest.server.ResponseBuilderMap;
import com.google.greaze.rest.server.RestResponseBuilder;
import com.google.gson.GsonBuilder;

/**
 * Functional tests for passing resource query parameters
 *
 * @author Inderjeet Singh
 */
public class ResourceDepotFunctionalTest extends TestCase {

  private static final String RESOURCE_PREFIX = "/rest";
  private static final CallPath RESOURCE_PATH =
    new CallPathParser(RESOURCE_PREFIX, false, "/employee").parse(RESOURCE_PREFIX + "/employee");
  private static final Id<Employee> ERROR_ID = Id.get("ErrorId12");

  private ResourceDepotClient<Employee> client;
  private Repository<Employee> employees;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    this.employees = new RepositoryInMemory<Employee>();
    RestResponseBuilder<Employee> responseBuilder = new ResponseBuilderEmployee(employees);
    RestCallSpec employeeRestCallSpec =
      ResourceDepotBaseClient.generateRestCallSpec(RESOURCE_PATH, Employee.class, null);
    RestCallSpecMap restCallSpecMap = new RestCallSpecMap.Builder()
      .set(RESOURCE_PATH, employeeRestCallSpec)
      .build();
    ResponseBuilderMap responseBuilders = new ResponseBuilderMap.Builder()
      .set(Employee.class, responseBuilder)
      .build();
    GsonBuilder gsonBuilder = new GsonBuilder()
      .registerTypeAdapterFactory(new IdGsonTypeAdapterFactory());
    ResourceUrlPaths urlPaths = new ResourceUrlPaths("http://localhost/fake/service/rest",
        "/fake", "/service", RESOURCE_PREFIX);
    RestClientStub stub = new RestClientStubFake(responseBuilders,
        restCallSpecMap, gsonBuilder, ImmutableList.of(RESOURCE_PATH), urlPaths, null);
    this.client = new ResourceDepotClient<Employee>(
        stub, RESOURCE_PATH, Employee.class, new GsonBuilder(), false);
  }

  public void testGet() throws Exception {
    Id<Employee> id = Id.get("1");
    employees.put(new Employee(id, "bob"));
    Employee e = client.get(id, new WebContext());
    assertEquals("bob", e.getName());
  }

  public void testServerErrorOnGet() throws Exception {
    try {
      client.get(ERROR_ID, new WebContext());
      fail();
    } catch (WebServiceSystemException expected) {
      assertEquals(ErrorReason.BAD_REQUEST, expected.getReason());
    }
  }

  public void testPost() throws Exception {
    Employee e = client.post(new Employee("bob"), new WebContext());
    assertEquals("bob", e.getName());
    assertTrue(Id.isValid(e.getId()));
  }

  public void testPut() throws Exception {
    Employee bob = client.post(new Employee("bob"), new WebContext());
    assertEquals("bob", bob.getName());
    Employee sam = client.put(new Employee(bob.getId(), "sam"), new WebContext());
    assertEquals("sam", sam.getName());
    assertEquals(bob.getId(), sam.getId());
  }

  public void testDelete() throws Exception {
    Employee bob = client.post(new Employee("bob"), new WebContext());
    assertEquals("bob", bob.getName());
    client.delete(bob.getId(), new WebContext());
    assertNull(client.get(bob.getId(), new WebContext()));
  }

  private static final class ResponseBuilderEmployee extends RestResponseBuilder<Employee> {
    public ResponseBuilderEmployee(Repository<Employee> employees) {
      super(employees);
    }
    @Override
    public Employee get(Id<Employee> resourceId, WebContext context) {
      Preconditions.checkArgument(!resourceId.equals(ERROR_ID));
      return super.get(resourceId, context);
    }
  }
}
