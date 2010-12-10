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

import com.google.greaze.definition.ContentBodySpec;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * A test fixture for {@link HttpServletResponse}
 *
 * @author Inderjeet Singh
 */
public class HttpServletResponseFake implements HttpServletResponse {

  private final ServletOutputStream sos;
  private final PrintWriter writer;
  private String contentType;
  
  public HttpServletResponseFake(OutputStream out) {
    this.sos = new ServletOutputStreamPiped(out);
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
  }

  @Override
  public int getBufferSize() {
    return 0;
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
  }

  @Override
  public Locale getLocale() {
    return null;
  }

  @Override
  public void addCookie(Cookie cookie) {
  }

  @Override
  public boolean containsHeader(String name) {
    return false;
  }

  @Override
  public String encodeURL(String url) {
    return null;
  }

  @Override
  public String encodeRedirectURL(String url) {
    return null;
  }

  @SuppressWarnings("deprecation")
  @Override
  public String encodeUrl(String url) {
    return null;
  }

  @SuppressWarnings("deprecation")
  @Override
  public String encodeRedirectUrl(String url) {
    return null;
  }

  @Override
  public void sendError(int sc, String msg) {
  }

  @Override
  public void sendError(int sc) {
  }

  @Override
  public void sendRedirect(String location) {
  }

  @Override
  public void setDateHeader(String name, long date) {
  }

  @Override
  public void addDateHeader(String name, long date) {
  }

  @Override
  public void setHeader(String name, String value) {
  }

  @Override
  public void addHeader(String name, String value) {
  }

  @Override
  public void setIntHeader(String name, int value) {
  }

  @Override
  public void addIntHeader(String name, int value) {
  }

  @Override
  public void setStatus(int sc) {
  }

  @SuppressWarnings("deprecation")
  @Override
  public void setStatus(int sc, String sm) {
  }
}