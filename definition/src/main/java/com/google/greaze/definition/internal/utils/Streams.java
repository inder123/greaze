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
package com.google.greaze.definition.internal.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public final class Streams {

  public static void copy(String str, OutputStream dst, boolean closeOutput) throws IOException {
    byte[] bytes = str.getBytes("UTF-8");
    copy(new ByteArrayInputStream(bytes), dst, true, closeOutput);    
  }

  /**
   * Copy contents of src to dst. Exhausts src completely, and closes both streams.
   */
  public static void copy(InputStream src, OutputStream dst, boolean closeInput,
                          boolean closeOutput) throws IOException {
    // Another way to implement this logic would be to just call the following:
    // copy(new InputStreamReader(src), new OutputStreamWriter(dst), closeInput, closeOutput);
    try {
      byte[] buf = new byte[2048];
      int count;
      while ((count = src.read(buf)) != -1) {
        dst.write(buf, 0, count);
      }
    } finally {
      if (closeInput) src.close();
      if (closeOutput) dst.close();
    }
  }

  /**
   * Copy contents of src to dst. Exhausts src completely, and closes both streams.
   */
  public static void copy(Reader src, Writer dst, boolean closeInput,
      boolean closeOutput) throws IOException {
    try {
      char[] buf = new char[2048];
      int count;
      while ((count = src.read(buf)) != -1) {
        dst.write(buf, 0, count);
      }
    } finally {
      if (closeInput) src.close();
      if (closeOutput) dst.close();
    }
  }

  public static void closeIgnoringErrors(OutputStream closeable) {
    try {
      if (closeable != null) {
        closeable.flush();
        closeable.close();
      }
    } catch (Exception e) {
    }
  }

  private Streams() {
    // Prevent instantiation
  }
}
