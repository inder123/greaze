/*
 * Copyright (C) 2008 Google Inc.
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
package com.google.greaze.definition;

import com.google.greaze.definition.internal.utils.GreazeStrings;

/**
 * Encapsulation of a Web service path that is sent by the client.
 * 
 * @author inder
 */
public final class CallPath {

  /** Visible for testing only */
  static final double IGNORE_VERSION = -1D;
  public static final CallPath NULL_PATH = new CallPath(null, IGNORE_VERSION, null, null);

  private final String basePath;
  private final double version;
  private final String servicePath;
  private final String resourceId;

  /**
   * For /resource/1.0/order/123, basePath is /resource, version is 1.0, servicePath is /order
   * and resourceId is &quot;123&quot;
   * @param basePath the starting prefix in the path.
   *   For example, for /resource/1.0/order/123, basePath is /resource
   * @param version the embedded version number in the call path.
   *   For example, for /resource/1.0/order/123, version is 1.0
   * @param servicePath The path to service without base path, version and resource id.
   *   For example, for /resource/1.0/order/123, servicePath is /order
   * @param resourceId the resource id, if present.
   *   For example, for /resource/1.0/order/123, resourceId is &quot;123&quot;
   */
  public CallPath(String basePath, double version, String servicePath, String resourceId) {
    this.basePath = basePath;
    this.version = version;
    this.servicePath = servicePath;
    this.resourceId = resourceId;
  }

  public String getBasePath() {
    return basePath;
  }

  public String getServicePath() {
    return servicePath;
  }

  public boolean hasVersion() {
    return version != IGNORE_VERSION;
  }

  public double getVersion() {
    return version;
  }

  public String getResourceId() {
    return resourceId;
  }

  public String getFullPath() {
    StringBuilder sb = new StringBuilder();
    if (basePath != null) {
      sb.append(basePath);
    }
    if (hasVersion()) {
      sb.append('/').append(String.valueOf(version));
    }
    if (servicePath != null) {
      sb.append(servicePath);
    }
    if (resourceId != null) {
      sb.append('/').append(resourceId);
    }
    return sb.toString();
  }

  /**
   * Returns the path without the service or the resource id
   * @return
   */
  public String getPathPrefix() {
    StringBuilder sb = new StringBuilder();
    if (basePath != null) {
      sb.append(basePath);
    }
    if (hasVersion()) {
      sb.append('/').append(String.valueOf(version));
    }
    return sb.toString();
  }

  @Override
  public int hashCode() {
    int hash = basePath == null ? 0 : basePath.hashCode();
    hash += servicePath == null ? 0 : 3*basePath.hashCode(); 
    return hash;
  }

  public boolean matches(CallPath other) {
    return version == other.version
      && GreazeStrings.equals(basePath, other.basePath)
      && GreazeStrings.firstStartsWithSecond(servicePath, other.servicePath);
  }

  public CallPathParser toParser() {
    return new CallPathParser(basePath, hasVersion(), servicePath);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    CallPath other = (CallPath)obj;
    return getClass() == obj.getClass()
      && version == other.version
      && GreazeStrings.equals(basePath, other.basePath)
      && GreazeStrings.equals(servicePath, other.servicePath)
      && GreazeStrings.equals(resourceId, other.resourceId);
  }

  @Override
  public String toString() {
    return String.format("basePath: %s, version:%2.f, servicePath:%s, resourceId: %d",
        basePath, version, servicePath, resourceId);
  }
}
