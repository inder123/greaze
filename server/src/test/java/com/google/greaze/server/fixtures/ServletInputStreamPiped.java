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
package com.google.greaze.server.fixtures;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;

/**
 * A {@link ServletInputStream} that serves from a {@link InputStream}
 *
 * @author Inderjeet Singh
 */
public class ServletInputStreamPiped extends ServletInputStream {
  private final InputStream in;
  public ServletInputStreamPiped(InputStream in) {
    this.in = in;
  }

  @Override
  public int read() throws IOException {
    return in.read();
  }
  
  @Override
  public int read(byte b[]) throws IOException {
    return in.read(b);
  }

  @Override
  public int read(byte b[], int off, int len) throws IOException {
    return in.read(b, off, len);
  }
  @Override
  public void close() throws IOException {
    in.close();
  }

  @Override
  public int available() throws IOException {
    return in.available();
  }

  @Override
  public synchronized void mark(int readlimit) {
    in.mark(readlimit);
  }

  @Override
  public boolean markSupported() {
    return in.markSupported();
  }

  @Override
  public synchronized void reset() throws IOException {
    in.reset();
  }

  @Override
  public long skip(long n) throws IOException {
    return in.skip(n);
  }
}
