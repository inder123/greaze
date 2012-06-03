/*
 * Copyright (C) 2008-2010 Google Inc. Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable
 * law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package com.google.greaze.server.fixtures;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.greaze.definition.internal.utils.GreazeStrings;
import com.google.greaze.definition.rest.ResourceUrlPaths;

/**
 * A test fixture for {@link HttpServletRequest}
 * 
 * @author Inderjeet Singh
 */
@SuppressWarnings("rawtypes")
public final class HttpServletRequestFake implements HttpServletRequest {
  private String contextPath;
  private String method;
  private final Map<String, Object> attributes = new HashMap<String, Object>();
  private Map<String, String[]> urlParams = new HashMap<String, String[]>();
  private String servletPath;
  private ServletInputStream inputStream;
  private Map<String, String> headers;
  private URL url;
  private String pathInfo;

  public HttpServletRequestFake setResourceUrlPaths(URL actualUrl, ResourceUrlPaths urlPaths) {
    this.url = actualUrl;
    this.contextPath = urlPaths.getContextPath();
    setUrlParams(url.getQuery());
    setServletPath(urlPaths.getServletPath());
    this.pathInfo = urlPaths.getPathInfo(actualUrl);
    return this;
  }

  public HttpServletRequestFake setRequestMethod(String method) {
    this.method = method;
    return this;
  }

  public HttpServletRequestFake setServletPath(String path) {
    this.servletPath = path;
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

  public HttpServletRequestFake setHeaders(Map<String, String> headers) {
    this.headers = headers;
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
  public ServletInputStream getInputStream() throws IOException {
    if (inputStream == null) {
      throw new EOFException();
    }
    return inputStream;
  }

  @Override
  public String getParameter(String name) {
    String[] params = urlParams.get(name);
    return params == null || params.length == 0 ? null : params[0];
  }

  @Override
  public Enumeration getParameterNames() {
    return new Iterator2Enumeration<String>(urlParams.keySet().iterator());
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
  public Object getAttribute(String name) {
    return attributes.get(name);
  }

  @Override
  public Enumeration getAttributeNames() {
    return new Iterator2Enumeration<String>(attributes.keySet().iterator());
  }

  @Override
  public void setAttribute(String name, Object attribute) {
    attributes.put(name, attribute);
  }

  @Override
  public void removeAttribute(String name) {
    attributes.remove(name);
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

  @SuppressWarnings("deprecation")
  @Override
  public long getDateHeader(String name) {
    String header = getHeader(name);
    return header == null ? -1 : new Date(header).getTime();
  }

  @Override
  public String getHeader(String name) {
    return headers == null ? null : headers.get(name);
  }

  @Override
  public Enumeration getHeaders(String name) {
    if (headers == null) {
      return null;
    }
    String value = headers.get(name);
    Vector<String> list = new Vector<String>();
    list.add(value);
    return list.elements();
  }

  @Override
  public Enumeration getHeaderNames() {
    if (headers == null) {
      return null;
    }
    return new Iterator2Enumeration<String>(headers.keySet().iterator());
  }

  @Override
  public int getIntHeader(String name) {
    return Integer.parseInt(getHeader(name));
  }

  @Override
  public String getMethod() {
    return method;
  }

  @Override
  public String getPathInfo() {
    return pathInfo;
  }

  @Override
  public String getPathTranslated() {
    return null;
  }

  @Override
  public String getContextPath() {
    return contextPath;
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
    // For http://localhost:90/fake/abcd/efg?a=10 returns /fake/abcd/efg
    StringBuffer requestUrl = getRequestURL();
    String requestUri = requestUrl.substring(requestUrl.indexOf("//") + 2);
    requestUri = requestUri.substring(requestUri.indexOf("/"));
    return requestUri;
  }

  @Override
  public StringBuffer getRequestURL() {
    // For http://localhost:90/fake/abcd/efg?a=10 returns http://localhost:90/fake/abcd/efg
    String requestUrl = url.toExternalForm();
    String query = url.getQuery();
    if (query != null) {
      int queryStartIndex = requestUrl.length() - query.length();
      requestUrl = requestUrl.substring(0, queryStartIndex-1);
    }
    return new StringBuffer(requestUrl);
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