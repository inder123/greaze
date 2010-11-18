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
package com.google.greaze.rest.client;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.rest.RestResource;
import com.google.greaze.definition.rest.ValueBasedId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import junit.framework.TestCase;

/**
 * Unit tests for {@link ResourceDepotClient}
 * 
 * @author Inderjeet Singh
 */
public class ResourceDepotClientTest extends TestCase {
  private Gson gson;
  private ResourceDepotClient<ValueBasedId<MyResource>, MyResource> client;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    gson = new GsonBuilder()
        .create();
    RestClientStub stub = new RestClientStubFake();
    client = new ResourceDepotClient<ValueBasedId<MyResource>, MyResource>(
        stub, MyResource.CALL_PATH, MyResource.class, gson);
  }

  public void testPost() throws Exception {
    MyResource sent = new MyResource(10);
    MyResource received = client.post(sent);
    assertEquals(sent.value, received.value);
  }

  private static class MyResource
      implements RestResource<ValueBasedId<MyResource>, MyResource> {
    public static final CallPath CALL_PATH = new CallPath("/rest/myresource");

    private ValueBasedId<MyResource> id;
    int value;

    // For Gson
    @SuppressWarnings("unused")
    public MyResource() {
      this(0);
    }

    public MyResource(int value) {
      this.value = value;
    }

    @Override
    public ValueBasedId<MyResource> getId() {
      return id;
    }

    @Override
    public void setId(ValueBasedId<MyResource> id) {
      this.id = id;
    }

    @Override
    public boolean hasId() {
      return ValueBasedId.isValid(id);
    }
  }
}
