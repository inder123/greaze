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

import junit.framework.TestCase;

/**
 * Unit tests for {@link HttpServletRequestFake}
 *
 * @author Inderjeet Singh
 */
public class HttpServletRequestFakeTest extends TestCase {
  public void testContextPath() {
    HttpServletRequestFake req = new HttpServletRequestFake()
      .setUrl("http://abcd:9091/fake/abcd?abc=10");
    assertEquals("/fake", req.getContextPath());
    req = new HttpServletRequestFake()
      .setUrl("https://abcd/fake2/abcd?abc=10");
    assertEquals("/fake2", req.getContextPath());
  }

  public void testRequestURI() {
    HttpServletRequestFake req = new HttpServletRequestFake()
      .setUrl("http://abcd:9091/fake/abcd?abc=10");
    assertEquals("/fake/abcd", req.getRequestURI());
    req = new HttpServletRequestFake()
      .setUrl("https://abcd/fake2/abcd?abc=10");
    assertEquals("/fake2/abcd", req.getRequestURI());
  }

  public void testRequestURL() {
    HttpServletRequestFake req = new HttpServletRequestFake()
      .setUrl("http://abcd:9091/fake/abcd?abc=10");
    assertEquals("http://abcd:9091/fake/abcd", req.getRequestURL().toString());
    req = new HttpServletRequestFake()
      .setUrl("https://abcd/fake2/abcd?abc=10");
    assertEquals("https://abcd/fake2/abcd", req.getRequestURL().toString());
  }
}
