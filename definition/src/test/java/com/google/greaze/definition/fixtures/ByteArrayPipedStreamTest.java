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
package com.google.greaze.definition.fixtures;

import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Unit tests for {@link ByteArrayPipedStream}
 *
 * @author Inderjeet Singh
 */
public class ByteArrayPipedStreamTest extends TestCase {

  private PrintWriter out;
  private BufferedReader in;
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    ByteArrayPipedStream baps = new ByteArrayPipedStream();
    this.out = new PrintWriter(baps);
    this.in = new BufferedReader(new InputStreamReader(baps.getInputStream()));
  }

  public void testWriteTwoLines() throws Exception {
    out.append("foo foo\nbar bar");
    out.close();
    assertEquals("foo foo", in.readLine());
    assertEquals("bar bar", in.readLine());
  }
  public void testWriteTwoLinesWithByteArrayInputStream() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintWriter out = new PrintWriter(baos);
    out.append("foo foo\nbar bar");
    out.close();
    BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(baos.toByteArray())));
    assertEquals("foo foo", in.readLine());
    assertEquals("bar bar", in.readLine());
  }
}
