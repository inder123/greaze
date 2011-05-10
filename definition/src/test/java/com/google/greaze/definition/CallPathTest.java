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
package com.google.greaze.definition;

import junit.framework.TestCase;

/**
 * Unit test for {@link CallPath}
 *
 * @author inder
 */
public class CallPathTest extends TestCase {

  public void testVersionIsSkipped() {
    CallPathParser parser = new CallPathParser("/", true, "/rest/service1");
    CallPath path = parser.parse("/1.0/rest/service1");
    assertEquals("/rest/service1", path.getServicePath());
    assertEquals(1D, path.getVersion());
    assertEquals(null, path.getResourceId());
  }

  public void testVersionNotPresent() {
    CallPathParser parser = new CallPathParser("/rest", false, "/service1");
    CallPath path = parser.parse("/rest/service1");
    assertEquals("/service1", path.getServicePath());
    assertEquals(CallPath.IGNORE_VERSION, path.getVersion());
    assertEquals(null, path.getResourceId());
  }
  
  public void testResourceIdPresent() {
    CallPathParser parser = new CallPathParser("/rest", false, "/service");
    CallPath path = parser.parse("/rest/service/3");
    assertEquals("/service", path.getServicePath());
    assertEquals("3", path.getResourceId());
  }

  public void testResourceIdWithEndSlashPresent() {
    CallPathParser parser = new CallPathParser("/rest", false, "/service");
    CallPath path = parser.parse("/rest/service/3/");
    assertEquals("/service", path.getServicePath());
    assertEquals("3", path.getResourceId());
  }

  public void testVersionAndResourceIdPresent() {
    CallPathParser parser = new CallPathParser("/rest", true, "/service53");
    CallPath path = parser.parse("/rest/3.1/service53/323222");
    assertEquals(3.1D, path.getVersion());
    assertEquals("/service53", path.getServicePath());
    assertEquals("323222", path.getResourceId());
  }

  public void testNullPath() {
    CallPathParser parser = new CallPathParser("/rest", true, "/service");
    CallPath path = parser.parse(null);
    assertEquals(CallPath.NULL_PATH, path);
  }

  public void testEmptyPath() {
    CallPathParser parser = new CallPathParser("/rest", true, "/service");
    CallPath path = parser.parse("");
    assertEquals(CallPath.NULL_PATH, path);
  }

  public void testWhiteSpacePath() {
    CallPathParser parser = new CallPathParser("/rest", true, "/service");
    CallPath path = parser.parse("\r\n");
    assertEquals(CallPath.NULL_PATH, path);
  }
}
