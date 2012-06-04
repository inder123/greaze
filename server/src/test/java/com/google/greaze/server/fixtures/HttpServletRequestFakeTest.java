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
package com.google.greaze.server.fixtures;

import java.net.URL;

import junit.framework.TestCase;

import com.google.greaze.definition.rest.ResourceUrlPaths;

/**
 * Unit tests for {@link HttpServletRequestFake}
 *
 * @author Inderjeet Singh
 */
public class HttpServletRequestFakeTest extends TestCase {
  private HttpServletRequestFake req1;
  private HttpServletRequestFake req2;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    ResourceUrlPaths urlPaths1 = new ResourceUrlPaths(
        "http://localhost:8080/fake/abcd/resource/",
        "/fake", "/abcd", "/resource");
    this.req1 = new HttpServletRequestFake()
      .setResourceUrlPaths(new URL("http://localhost:8080/fake/abcd/resource/1.0/order/232?abc=def"),
          urlPaths1);
    ResourceUrlPaths urlPaths2 = new ResourceUrlPaths(
        "https://localhost/fake2/abcd/resource/",
        "/fake2", "/abcd", "/resource");
    this.req2 = new HttpServletRequestFake()
      .setResourceUrlPaths(new URL("https://localhost/fake2/abcd/resource/1.0/order/121?abc=def"),
          urlPaths2);
  }
  public void testContextPath() {
    assertEquals("/fake", req1.getContextPath());
    assertEquals("/fake2", req2.getContextPath());
  }

  public void testRequestURI() {
    assertEquals("/fake/abcd/resource/1.0/order/232", req1.getRequestURI());
    assertEquals("/fake2/abcd/resource/1.0/order/121", req2.getRequestURI());
  }

  public void testRequestURL() {
    assertEquals("http://localhost:8080/fake/abcd/resource/1.0/order/232", req1.getRequestURL().toString());
    assertEquals("https://localhost/fake2/abcd/resource/1.0/order/121", req2.getRequestURL().toString());
  }

  public void testServletPath() {
    assertEquals("/abcd/resource/1.0/order/232", req1.getServletPath());
    assertEquals("/abcd/resource/1.0/order/121", req2.getServletPath());
  }
}
