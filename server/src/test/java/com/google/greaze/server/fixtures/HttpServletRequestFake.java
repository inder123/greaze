/*
 * Copyright (C) 2008-2010 Google Inc.
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

import com.google.greaze.definition.internal.utils.GreazeStrings;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * A test fixture for {@link HttpServletRequest}
 *
 * @author Inderjeet Singh
 */
@SuppressWarnings({"rawtypes", "deprecation"})
public class HttpServletRequestFake implements HttpServletRequest {
  private String method;
  private Map<String, String[]> urlParams = new HashMap<String, String[]>();
  private String servletPath;
  private ServletInputStream inputStream;

  public HttpServletRequestFake setRequestMethod(String method) {
    this.method = method;
    return this;
  }

  public HttpServletRequestFake setServletPath(String path) {
    this.servletPath = path;
    if (servletPath.contains("?")) {
      setUrlParams(servletPath.substring(servletPath.indexOf('?')));
    }
    return this;
  }

  public HttpServletRequestFake setUrlParam(String name, String value) {
    String[] current = urlParams.get(name);
    int arraySize = current == null ? 0 : current.length;
    String[] revised = new String[arraySize + 1];
    for (int i = 0; i < arraySize; ++i) {
      revised[i] = current[i];
    }
    revised[arraySize] = value;
    urlParams.put(name, revised);
    return this;
  }

  public HttpServletRequestFake setInputStream(InputStream inputStream) {
    this.inputStream = new ServletInputStreamPiped(inputStream);
    return this;
  }

  public HttpServletRequestFake setUrlParams(String urlParamsString) {
    if (GreazeStrings.isEmpty(urlParamsString)) {
      return this;
    }
    if (urlParamsString.startsWith("?")) {
      urlParamsString = urlParamsString.substring(1);
    }
    try {
      String[] params = urlParamsString.split("&");
      for (String param : params) {
        String[] split = param.split("=");
        String name = split[0];
        String value = URLDecoder.decode(split[1], "UTF-8");
        setUrlParam(name, value);
      }
      return this;
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Object getAttribute(String name) {
    return null;
  }

  @Override
  public Enumeration getAttributeNames() {
    return null;
  }

  @Override
  public String getCharacterEncoding() {
    return null;
  }

  @Override
  public void setCharacterEncoding(String env) {
  }

  @Override
  public int getContentLength() {
    return 0;
  }

  @Override
  public String getContentType() {
    return null;
  }

  @Override
  public ServletInputStream getInputStream() {
    return inputStream;
  }

  @Override
  public String getParameter(String name) {
    return null;
  }

  @Override
  public Enumeration getParameterNames() {
    return null;
  }

  @Override
  public String[] getParameterValues(String name) {
    return urlParams.get(name);
  }

  @Override
  public Map getParameterMap() {
    return urlParams;
  }

  @Override
  public String getProtocol() {
    return "http";
  }

  @Override
  public String getScheme() {
    return null;
  }

  @Override
  public String getServerName() {
    return null;
  }

  @Override
  public int getServerPort() {
    return 80;
  }

  @Override
  public BufferedReader getReader() {
    return null;
  }

  @Override
  public String getRemoteAddr() {
    return null;
  }

  @Override
  public String getRemoteHost() {

    return null;
  }

  @Override
  public void setAttribute(String name, Object o) {
  }

  @Override
  public void removeAttribute(String name) {
  }

  @Override
  public Locale getLocale() {
    return null;
  }

  @Override
  public Enumeration getLocales() {
    return null;
  }

  @Override
  public boolean isSecure() {
    return false;
  }

  @Override
  public RequestDispatcher getRequestDispatcher(String path) {
    return null;
  }

  @Override
  public String getRealPath(String path) {
    return null;
  }

  @Override
  public int getRemotePort() {
    return 0;
  }

  @Override
  public String getLocalName() {
    return null;
  }

  @Override
  public String getLocalAddr() {
    return null;
  }

  @Override
  public int getLocalPort() {
    return 0;
  }

  @Override
  public String getAuthType() {
    return null;
  }

  @Override
  public Cookie[] getCookies() {
    return null;
  }

  @Override
  public long getDateHeader(String name) {
    return 0;
  }

  @Override
  public String getHeader(String name) {
    return null;
  }

  @Override
  public Enumeration getHeaders(String name) {
    return null;
  }

  @Override
  public Enumeration getHeaderNames() {
    return null;
  }

  @Override
  public int getIntHeader(String name) {
    return 0;
  }

  @Override
  public String getMethod() {
    return method;
  }

  @Override
  public String getPathInfo() {
    return null;
  }

  @Override
  public String getPathTranslated() {
    return null;
  }

  @Override
  public String getContextPath() {
    return null;
  }

  @Override
  public String getQueryString() {
    return null;
  }

  @Override
  public String getRemoteUser() {
    return null;
  }

  @Override
  public boolean isUserInRole(String role) {
    return false;
  }

  @Override
  public Principal getUserPrincipal() {
    return null;
  }

  @Override
  public String getRequestedSessionId() {
    return null;
  }

  @Override
  public String getRequestURI() {
    return null;
  }

  @Override
  public StringBuffer getRequestURL() {
    return null;
  }

  @Override
  public String getServletPath() {
    return servletPath;
  }

  @Override
  public HttpSession getSession(boolean create) {
    return null;
  }

  @Override
  public HttpSession getSession() {
    return null;
  }

  @Override
  public boolean isRequestedSessionIdValid() {
    return false;
  }

  @Override
  public boolean isRequestedSessionIdFromCookie() {
    return false;
  }

  @Override
  public boolean isRequestedSessionIdFromURL() {
    return false;
  }

  @Override
  public boolean isRequestedSessionIdFromUrl() {
    return false;
  }
}