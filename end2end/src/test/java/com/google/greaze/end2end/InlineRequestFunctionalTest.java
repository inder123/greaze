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
package com.google.greaze.end2end;

import junit.framework.TestCase;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.CallPathParser;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.TypedKey;
import com.google.greaze.definition.rest.Id;
import com.google.greaze.definition.rest.IdGsonTypeAdapterFactory;
import com.google.greaze.definition.rest.ResourceUrlPaths;
import com.google.greaze.definition.rest.RestCallSpec;
import com.google.greaze.definition.rest.RestCallSpecMap;
import com.google.greaze.definition.rest.RestRequestBase;
import com.google.greaze.definition.rest.RestResponseBase;
import com.google.greaze.definition.rest.WebContext;
import com.google.greaze.definition.rest.WebContextSpec;
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
public class InlineRequestFunctionalTest extends TestCase {

  private static final TypedKey<String> HEADER1 = new TypedKey<String>("header1", String.class);
  private static final TypedKey<String> HEADER2 = new TypedKey<String>("header2", String.class);

  private ResourceDepotClient<Employee> client;
  private Repository<Employee> employees;
  private WebContext context;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    String resourcePrefix = "/rest";
    CallPath resourcePath = new CallPathParser(
        resourcePrefix, false, "/employee").parse(resourcePrefix + "/employee");
    ResourceUrlPaths urlPaths = new ResourceUrlPaths("http://localhost/fake/service/rest",
        "/fake", "/service", resourcePrefix);

    this.employees = new RepositoryInMemory<Employee>();
    RestResponseBuilder<Employee> responseBuilder = new ResponseBuilderEmployee(employees);
    HeaderMapSpec contextHeaderSpec = new HeaderMapSpec.Builder()
      .put(HEADER1.getName(), String.class)
      .put(HEADER2.getName(), String.class)
      .build();
  WebContextSpec webContextSpec = new WebContextSpec(contextHeaderSpec);
    RestCallSpec callSpec =
      ResourceDepotBaseClient.generateRestCallSpec(resourcePath, Employee.class, webContextSpec);
    RestCallSpecMap restCallSpecMap = new RestCallSpecMap.Builder()
      .set(resourcePath, callSpec)
      .build();
    ResponseBuilderMap responseBuilders = new ResponseBuilderMap.Builder()
      .set(Employee.class, responseBuilder)
      .build();
    GsonBuilder gsonBuilder = new GsonBuilder()
      .registerTypeAdapterFactory(new IdGsonTypeAdapterFactory());
    RestClientStub stub = new RestClientStubFake(responseBuilders,
        restCallSpecMap, gsonBuilder, ImmutableList.of(resourcePath), urlPaths, null);
    this.client = new ResourceDepotClient<Employee>(
        stub, Employee.class, callSpec, new GsonBuilder(), true);
    this.context = new WebContext.Builder()
      .put(HEADER1, "value1")
      .put(HEADER2, "value2")
      .build();
  }

  public void testGet() throws Exception {
    Id<Employee> id = Id.get("1");
    employees.put(new Employee(id, "bob"));
    Employee e = client.get(id, context);
    assertEquals("bob", e.getName());
  }

  public void testPost() throws Exception {
    Employee e = client.post(new Employee("bob"), context);
    assertEquals("bob", e.getName());
    assertTrue(Id.isValid(e.getId()));
  }

  public void testPut() throws Exception {
    Employee bob = client.post(new Employee("bob"), context);
    assertEquals("bob", bob.getName());
    Employee sam = client.put(new Employee(bob.getId(), "sam"), context);
    assertEquals("sam", sam.getName());
    assertEquals(bob.getId(), sam.getId());
  }

  public void testDelete() throws Exception {
    Employee bob = client.post(new Employee("bob"), context);
    assertEquals("bob", bob.getName());
    client.delete(bob.getId(), context);
    assertNull(client.get(bob.getId(), context));
  }

  private static final class ResponseBuilderEmployee extends RestResponseBuilder<Employee> {
    public ResponseBuilderEmployee(Repository<Employee> employees) {
      super(employees);
    }
    @Override
    public void buildResponse(WebContext context, RestRequestBase<Id<Employee>, Employee> request,
        RestResponseBase.Builder<Id<Employee>, Employee> responseBuilder) {
      String header1 = context.get(HEADER1);
      String header2 = context.get(HEADER2);
      Preconditions.checkArgument(header1 != null && header1.equals("value1"), "missing header1");
      Preconditions.checkArgument(header2 != null && header2.equals("value2"), "missing header2");
      super.buildResponse(context, request, responseBuilder);
    }
  }
}
