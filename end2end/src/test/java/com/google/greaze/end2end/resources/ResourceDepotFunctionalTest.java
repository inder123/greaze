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

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.CallPathParser;
import com.google.greaze.definition.rest.Id;
import com.google.greaze.end2end.definition.Employee;
import com.google.greaze.end2end.fixtures.RestClientStubFake;
import com.google.greaze.rest.client.ResourceDepotClient;
import com.google.greaze.rest.client.RestClientStub;
import com.google.greaze.rest.server.Repository;
import com.google.greaze.rest.server.RepositoryInMemory;
import com.google.greaze.rest.server.RestResponseBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Functional tests for passing resource query parameters
 *
 * @author Inderjeet Singh
 */
public class ResourceDepotFunctionalTest extends TestCase {

  private static final CallPath RESOURCE_PATH =
    new CallPathParser("/rest", false, "/employee").parse("/rest/employee");
  private ResourceDepotClient<Employee> client;
  private Repository<Employee> employees;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    Gson gson = new GsonBuilder()
      .registerTypeAdapter(Id.class, new Id.GsonTypeAdapter())
      .create();
    this.employees = new RepositoryInMemory<Employee>(Employee.class);
    RestResponseBuilder<Employee> responseBuilder = new RestResponseBuilder<Employee>(employees);
    RestClientStub stub =
      new RestClientStubFake<Employee>(responseBuilder, Employee.class, gson, RESOURCE_PATH);
    this.client = new ResourceDepotClient<Employee>(stub, RESOURCE_PATH, Employee.class, gson);
  }

  public void testGet() throws Exception {
    Id<Employee> id = Id.get("1", Employee.class);
    employees.put(new Employee(id, "bob"));
    Employee e = client.get(id);
    assertEquals("bob", e.getName());
  }

  public void testPost() throws Exception {
    Employee e = client.post(new Employee("bob"));
    assertEquals("bob", e.getName());
    assertTrue(Id.isValid(e.getId()));
  }

  public void testPut() throws Exception {
    Employee bob = client.post(new Employee("bob"));
    assertEquals("bob", bob.getName());
    Employee sam = client.put(new Employee(bob.getId(), "sam"));
    assertEquals("sam", sam.getName());
    assertEquals(bob.getId(), sam.getId());
  }

  public void testDelete() throws Exception {
    Employee bob = client.post(new Employee("bob"));
    assertEquals("bob", bob.getName());
    client.delete(bob.getId());
    assertNull(client.get(bob.getId()));
  }
}
