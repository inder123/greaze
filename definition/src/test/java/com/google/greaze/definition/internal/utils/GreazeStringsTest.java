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
package com.google.greaze.definition.internal.utils;

import junit.framework.TestCase;

/**
 * Unit tests for {@link GreazeStrings}
 *
 * @author Inderjeet Singh
 */
public class GreazeStringsTest extends TestCase {

  public void testIndexOf() {
    assertEquals(0, GreazeStrings.indexOf(0, "fooBar", 'f', 'b'));
    assertEquals(3, GreazeStrings.indexOf(0, "fooBar", 'F', 'B'));
    assertEquals(-1, GreazeStrings.indexOf(0, "fooBar", 'F', 'b'));
    assertEquals(1, GreazeStrings.indexOf(0, "fooBar", 'o', 'o', 'B'));
  }
}
