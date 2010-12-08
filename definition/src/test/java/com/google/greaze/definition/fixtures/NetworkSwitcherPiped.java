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

import com.google.greaze.definition.ContentBodySpec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Pipes input into the output stream of HttpURLConnection
 * 
 * @author Inderjeet Singh
 */
public class NetworkSwitcherPiped implements NetworkSwitcher {

  @Override
  public HttpURLConnection get(URL url) {
    return new HttpURLConnectionFake(url);
  }

  protected void switchNetwork(HttpURLConnectionFake conn) {
  }

  public final class HttpURLConnectionFake extends HttpURLConnection {

    private final ByteArrayOutputStream out;
    public HttpURLConnectionFake(URL url) {
      super(url);
      out = new ByteArrayOutputStream();
    }

    @Override
    public void disconnect() {
    }

    @Override
    public boolean usingProxy() {
      return false;
    }

    @Override
    public String getContentType() {
      return ContentBodySpec.JSON_CONTENT_TYPE;
    }
    
    @Override
    public InputStream getInputStream() {
      return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public OutputStream getOutputStream() {
      return out;
    }

    @Override
    public void connect() {
      switchNetwork(this);
    }
  }
}

