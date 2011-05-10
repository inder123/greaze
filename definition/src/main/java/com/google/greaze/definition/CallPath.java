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

/**
 * Encapsulation of a Web service path that is sent by the client.
 * 
 * @author inder
 */
public final class CallPath {

  /** Visible for testing only */
  static final double IGNORE_VERSION = -1D;
  public static final CallPath NULL_PATH = new CallPath(IGNORE_VERSION, null, null);

  private final String path;
  private final double version;
  private final String resourceId;

  public CallPath(double version, String pathWithoutVersionAndResourceId, String resourceId) {
    this.version = version;
    this.path = pathWithoutVersionAndResourceId;
    this.resourceId = resourceId;
  }

  public String get() {
    return path;
  }

  public double getVersion() {
    return version;
  }

  public String getResourceId() {
    return resourceId;
  }

  @Override
  public int hashCode() {
    return path.hashCode();
  }

  public boolean matches(CallPath callPath) {
    return path.startsWith(callPath.get());
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
      && resourceId == other.resourceId
      && equal(path, other.path);
  }

  private static boolean equal(String s1, String s2) {
    return s1 == s2 || (s1 != null && s2 != null && s1.equals(s2));
  }

  @Override
  public String toString() {
    return String.format("path:%s, version:%2.f, resourceId: %d", path, version, resourceId);
  }
}
