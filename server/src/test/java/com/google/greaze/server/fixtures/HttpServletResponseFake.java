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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Preconditions;
import com.google.greaze.definition.ContentBodySpec;
import com.google.greaze.definition.fixtures.NetworkSwitcherPiped.HttpURLConnectionFake;

/**
 * A test fixture for {@link HttpServletResponse}
 * 
 * @author Inderjeet Singh
 */
public final class HttpServletResponseFake implements HttpServletResponse {

  private final ServletOutputStream sos;
  private final PrintWriter writer;
  private String contentType;
  private final Map<String, String> headers = new HashMap<String, String>();
  private Locale locale;
  /** Doesn't really matter as we just use {@link #sos} */
  private int bufferSize = 1024;
  private HttpURLConnectionFake conn;

  public HttpServletResponseFake(OutputStream out, HttpURLConnectionFake conn) {
    this.sos = new ServletOutputStreamPiped(out);
    this.conn = conn;
    this.writer = new PrintWriter(sos, true);
    this.contentType = ContentBodySpec.JSON_CONTENT_TYPE;
  }

  @Override
  public String getCharacterEncoding() {
    return ContentBodySpec.JSON_CHARACTER_ENCODING;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public ServletOutputStream getOutputStream() {
    return sos;
  }

  @Override
  public PrintWriter getWriter() {
    return writer;
  }

  @Override
  public void setCharacterEncoding(String charset) {
  }

  @Override
  public void setContentLength(int len) {
  }

  @Override
  public void setContentType(String type) {
    this.contentType = type;
  }

  @Override
  public void setBufferSize(int size) {
    this.bufferSize = size;
  }

  @Override
  public int getBufferSize() {
    return bufferSize;
  }

  @Override
  public void flushBuffer() {
    writer.flush();
  }

  @Override
  public void resetBuffer() {
  }

  @Override
  public boolean isCommitted() {
    return false;
  }

  @Override
  public void reset() {
  }

  @Override
  public void setLocale(Locale loc) {
    this.locale = loc;
  }

  @Override
  public Locale getLocale() {
    return locale;
  }

  @Override
  public void addCookie(Cookie cookie) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsHeader(String name) {
    return headers.containsKey(name);
  }

  @Override
  public String encodeURL(String url) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String encodeRedirectURL(String url) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String encodeUrl(String url) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String encodeRedirectUrl(String url) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void sendError(int sc, String msg) {
    setStatus(sc, msg);
  }

  @Override
  public void sendError(int sc) {
    sendError(sc, null);
  }

  @Override
  public void sendRedirect(String location) {
  }

  @Override
  public void setDateHeader(String name, long date) {
    headers.put(name, new Date(date).toString());
  }

  @Override
  public void addDateHeader(String name, long date) {
    // We will treat add as set for now
    Preconditions.checkArgument(!headers.containsKey(name));
    headers.put(name, new Date(date).toString());
  }

  @Override
  public void setHeader(String name, String value) {
    headers.put(name, value);
  }

  @Override
  public void addHeader(String name, String value) {
    // We will treat add as set for now
    Preconditions.checkArgument(!headers.containsKey(name));
    headers.put(name, value);
  }

  @Override
  public void setIntHeader(String name, int value) {
    headers.put(name, String.valueOf(value));
  }

  @Override
  public void addIntHeader(String name, int value) {
    // We will treat add as set for now
    Preconditions.checkArgument(!headers.containsKey(name));
    headers.put(name, String.valueOf(value));
  }

  @Override
  public void setStatus(int sc) {
    setStatus(sc, null);
  }

  @Override
  public void setStatus(int sc, String sm) {
    conn.setHttpResponseCode(sc);
    if (sm != null) {
      writer.append(sm);
    }
  }
}