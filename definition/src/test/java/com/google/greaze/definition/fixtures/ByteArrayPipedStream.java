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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * A {@link ByteArrayOutputStream} that provides an {@InputStream} that is
 * backed by the buffer of {@link ByteArrayOutputStream}
 *
 * @author Inderjeet Singh
 */
public final class ByteArrayPipedStream extends ByteArrayOutputStream {
  public InputStream getInputStream() {
    return new ByteArrayPipedInputStream();
  }

  private byte[] getBuffer() {
    return buf;
  }

  private int getTotalCount() {
    return count;
  }

  /**
   * ByteArrayInputStream that reads off the same buffer as the ByteArrayOutputStream.
   */
  private final class ByteArrayPipedInputStream extends InputStream {
    private int readPosition = 0;

    @Override
    public int read() {
      return readPosition < getTotalCount() ? (getBuffer()[readPosition++] & 0xff) : -1;
    }

    @Override
    public int read(byte b[]) {
      return read(b, 0, b.length);
    }

    @Override
    public int read(byte b[], int off, int len) {
      if (readPosition >= getTotalCount()) {
        return -1;
      }
      if (readPosition + len > getTotalCount()) {
        len = getTotalCount() - readPosition;
      }
      if (len <= 0) {
        return 0;
      }
      System.arraycopy(getBuffer(), readPosition, b, off, len);
      readPosition += len;
      return len;
    }

    @Override
    public int available() {
      return getTotalCount() - readPosition;
    }

    @Override
    public long skip(long n) {
      if (readPosition + n > getTotalCount()) {
        n = getTotalCount() - readPosition;
      }
      if (n < 0) {
        return 0;
      }
      readPosition += n;
      return n;
    }
  }
}
