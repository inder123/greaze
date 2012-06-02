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

import junit.framework.TestCase;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.CallPathParser;
import com.google.greaze.definition.rest.RestResourceImpl;
import com.google.greaze.definition.rest.WebContext;
import com.google.greaze.rest.client.fixtures.RestClientStubClientSideFake;
import com.google.gson.GsonBuilder;

/**
 * Unit tests for {@link ResourceDepotBaseClient}
 *
 * @author Inderjeet Singh
 */
public class ResourceDepotClientTest extends TestCase {
  private ResourceDepotClient<MyResource> client;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    RestClientStub stub = new RestClientStubClientSideFake();
    client = new ResourceDepotClient<MyResource>(
        stub, MyResource.CALL_PATH, MyResource.class, new GsonBuilder(), false);
  }

  public void testPost() throws Exception {
    MyResource sent = new MyResource(10);
    MyResource received = client.post(sent, new WebContext());
    assertEquals(sent.value, received.value);
  }

  @SuppressWarnings("serial")
private static class MyResource extends RestResourceImpl<MyResource> {
    public static final CallPath CALL_PATH =
      new CallPathParser("/rest", false, "/myresource").parse("/rest/myresource");

    int value;

    // For Gson
    @SuppressWarnings("unused")
    public MyResource() {
      this(0);
    }

    public MyResource(int value) {
      this.value = value;
    }
  }
}
