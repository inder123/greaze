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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.greaze.definition.ContentBodySpec;
import com.google.greaze.definition.internal.utils.Streams;

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

  protected void switchNetwork(HttpURLConnectionFake conn) throws IOException {
    Streams.copy(conn.getForwardForInput(), conn.getReverseForOutput(), true, true);
  }

  /**
   * Note that this HttpURLConnection only support single value headers. If you add
   * a subsequent header, it overwrites the previous value.
   */
  public final class HttpURLConnectionFake extends HttpURLConnection {

    private final Map<String, String> headers = new HashMap<String, String>();
    
    private final ByteArrayPipedStream forward;
    private final ByteArrayPipedStream reverse;

    public HttpURLConnectionFake(URL url) {
      super(url);
      this.forward = new ByteArrayPipedStream();
      this.reverse = new ByteArrayPipedStream();
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
      return reverse.getInputStream();
    }

    public InputStream getForwardForInput() {
      return forward.getInputStream();
    }

    public OutputStream getReverseForOutput() {
      return reverse;
    }

    @Override
    public OutputStream getOutputStream() {
      return forward;
    }

    @Override
    public void setRequestProperty(String key, String value) {
      headers.put(key, value);
    }
    
    @Override
    public void addRequestProperty(String key, String value) {
      headers.put(key, value);
    }

    public Map<String, String> getHeaders() {
      return headers;
    }

    @Override
    public void connect() throws IOException {
      switchNetwork(this);
    }
  }
}

