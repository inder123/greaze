/*
 * Copyright (C) 2011 Google Inc.
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

package com.google.greaze.metrics;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import com.google.common.base.Preconditions;
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
 * End to end benchmark for Greaze
 *
 * @author Inderjeet Singh
 */
public class End2EndBenchmark extends SimpleBenchmark {

  private static final CallPath RESOURCE_PATH =
    new CallPathParser("/rest", false, "/employee").parse("/rest/employee");
  private ResourceDepotClient<Employee> client;
  private Repository<Employee> employees;

  public static void main(String[] args) {
    Runner.main(End2EndBenchmark.class, args);
  }
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    Gson gson = new GsonBuilder()
      .registerTypeAdapter(Id.class, new Id.GsonTypeAdapter())
      .create();
    this.employees = new RepositoryInMemory<Employee>(Employee.class);
    RestResponseBuilder<Employee> responseBuilder = new RestResponseBuilder<Employee>(employees);
    RestClientStub stub = new RestClientStubFake<Employee>(responseBuilder, Employee.class, gson,
        new CallPathParser(null, false, "/employee"));
    this.client = new ResourceDepotClient<Employee>(stub, RESOURCE_PATH, Employee.class, gson);
  }

  public void timeGet(int reps) throws Exception {
    for (int i=0; i<reps; ++i) {
      Id<Employee> id = Id.get(String.valueOf(i), Employee.class);
      employees.put(new Employee(id, "bob"));
      Employee e = client.get(id);
      Preconditions.checkArgument("bob".equals(e.getName()));
    }
  }

  public void timePost(int reps) throws Exception {
    for (int i=0; i<reps; ++i) {
      Employee e = client.post(new Employee("bob"));
      Preconditions.checkArgument("bob".equals(e.getName()));
      Preconditions.checkArgument(Id.isValid(e.getId()));
    }
  }

  public void timePut(int reps) throws Exception {
    for (int i=0; i<reps; ++i) {
      Employee bob = client.post(new Employee("bob"));
      Preconditions.checkArgument("bob".equals(bob.getName()));
      Employee sam = client.put(new Employee(bob.getId(), "sam"));
      Preconditions.checkArgument("sam".equals(sam.getName()));
      Preconditions.checkArgument(bob.getId().equals(sam.getId()));
    }
  }

  public void timeDelete(int reps) throws Exception {
    for (int i=0; i<reps; ++i) {
      Employee bob = client.post(new Employee("bob"));
      Preconditions.checkArgument("bob".equals(bob.getName()));
      client.delete(bob.getId());
      Preconditions.checkArgument(client.get(bob.getId()) == null);
    }
  }
}
