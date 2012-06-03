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
package com.google.greaze.definition.rest;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.greaze.definition.internal.utils.GreazeStrings;

/**
 * Describes various path components of a resource URL.
 *
 * @author Inderjeet Singh
 */
public final class ResourceUrlPaths {

  private final URL url;
  private final String contextPath;
  private final String servletPath;
  private final String resourcePrefix;

  /**
   * @param baseUrl http://localhost/my-server/myservice/resource for a URL
   *   http://localhost/my-server/myservice/resource/1.0/order/23311?ab=cd
   * @param contextPath /my-server for a URL
   *   http://localhost/my-server/myservice/resource/1.0/order/23311?ab=cd
   * @param servletPath /myservice
   *   http://localhost/my-server/myservice/resource/1.0/order/23311?ab=cd
   * @param resourcePrefix /resource
   *   http://localhost/my-server/myservice/resource/1.0/order/23311?ab=cd
   */
  public ResourceUrlPaths(String baseUrl, String contextPath, String servletPath,
      String resourcePrefix) {
    try {
      this.url = new URL(baseUrl);
      this.contextPath = contextPath;
      this.servletPath = servletPath;
      this.resourcePrefix = resourcePrefix;
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public String getContextPath() {
    return contextPath;
  }

  public String getServletPath() {
    return servletPath;
  }

  public String getResourcePrefix() {
    return resourcePrefix;
  }

  public String getContextUrl() {
    String urlValue = url.toExternalForm();
    int index = urlValue.indexOf(servletPath + resourcePrefix);
    return urlValue.substring(0, index);
  }

  public String getResourceBaseUrl() {
    return getContextUrl() + servletPath + resourcePrefix;
  }

  public String getPathInfo(URL url) {
    String urlValue = url.toExternalForm();
    // Remove Query params
    String query = url.getQuery();
    if (GreazeStrings.isNotEmpty(query)) {
      urlValue = urlValue.substring(0, urlValue.indexOf(query)-1);
    }
    String contextAndServletPath = contextPath + servletPath;
    int index = urlValue.indexOf(contextAndServletPath) + contextAndServletPath.length();
    return urlValue.substring(index);
  }
}
