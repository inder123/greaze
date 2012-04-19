/*
 * Copyright (C) 2012 Greaze Authors.
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
package com.google.greaze.webservice.client;

import junit.framework.TestCase;

import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.UrlParams;
import com.google.greaze.definition.webservice.RequestBody;
import com.google.greaze.definition.webservice.RequestSpec;
import com.google.greaze.definition.webservice.WebServiceRequest;
import com.google.greaze.rest.client.fixtures.HttpUrlConnectionMock;
import com.google.gson.Gson;

/**
 * Unit test for {@link RequestSender}
 *
 * @author Inderjeet Singh
 */
public class RequestSenderTest extends TestCase {

  private RequestSender sender;
  private HttpUrlConnectionMock conn;

  protected void setUp() throws Exception {
    super.setUp();
    sender = new RequestSender(new Gson());
    this.conn = new HttpUrlConnectionMock();
  }

  public void testStringAndPrimitiveValueHeader() {
    HeaderMapSpec requestHeaderSpec = new HeaderMapSpec.Builder()
      .put("X-Name", String.class)
      .put("X-Salary", Integer.class)
      .build();
    HeaderMap requestHeaders = new HeaderMap.Builder(requestHeaderSpec)
      .put("X-Name", "bob")
      .put("X-Salary", 10000)
      .build();
    UrlParams urlParams = null;
    RequestBody requestBody = null;
    RequestSpec requestSpec = null;
    WebServiceRequest request =
        new WebServiceRequest(HttpMethod.GET, requestHeaders, urlParams, requestBody, requestSpec);
    sender.send(conn, request);
    assertEquals("bob", conn.getHeaders().get("X-Name"));
    assertEquals("10000", conn.getHeaders().get("X-Salary"));
  }

  public void testJsonValueHeader() {
    HeaderMapSpec requestHeaderSpec = new HeaderMapSpec.Builder()
      .put("X-Number", ComplexNumber.class)
      .build();
    HeaderMap requestHeaders = new HeaderMap.Builder(requestHeaderSpec)
      .put("X-Number", new ComplexNumber(10, 3))
      .build();
    UrlParams urlParams = null;
    RequestBody requestBody = null;
    RequestSpec requestSpec = null;
    WebServiceRequest request =
        new WebServiceRequest(HttpMethod.GET, requestHeaders, urlParams, requestBody, requestSpec);
    sender.send(conn, request);
    assertEquals("{\"real\":10,\"imaginary\":3}", conn.getHeaders().get("X-Number"));
  }

  private static class ComplexNumber {
    @SuppressWarnings("unused")
    int real, imaginary;
    public ComplexNumber(int real, int imaginary) {
      this.real = real;
      this.imaginary = imaginary;
    }
  }
}
