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
package com.google.greaze.rest.client.fixtures;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.greaze.definition.ContentBodySpec;
import com.google.greaze.definition.fixtures.ByteArrayPipedStream;

/**
 * A mock HttpUrlConnection for use in unit tests that doesn't go out to the network.
 * Instead it is used to send and receive arbitrary data.
 *
 * @author Inderjeet Singh
 */
public class HttpUrlConnectionMock extends HttpURLConnection {

  private final Map<String, String> headers = new HashMap<String, String>();

  private final ByteArrayPipedStream forward;
  private final ByteArrayPipedStream reverse;

  public HttpUrlConnectionMock() {
    super(createDummyUrl());
    this.forward = new ByteArrayPipedStream();
    this.reverse = new ByteArrayPipedStream();
  }

  private static URL createDummyUrl() {
    try {
      return new URL("http://localhost");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public String getBodyAsString() {
    return new String(forward.toByteArray());
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
  public void connect() {
  }
}

