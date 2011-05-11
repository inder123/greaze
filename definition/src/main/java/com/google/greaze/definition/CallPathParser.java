/*
 * Copyright (C) 2011 Google Inc.
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

public final class CallPathParser {
  public enum ParseFailureType {
    INVALID_PATH,
    INVALID_BASE_PATH,
    INVALID_VERSION,
    INVALID_SERVICE_NAME
  }

  @SuppressWarnings("serial")
  public static final class ParseException extends RuntimeException {
    private final ParseFailureType failureType;
    public ParseException(ParseFailureType failureType) {
      this.failureType = failureType;
    }
    public ParseFailureType getFailureType() {
      return failureType;
    }
  }
  private final String basePath;
  private final boolean hasVersion;
  private final String serviceName;

  /**
   * Configures the parsing of a web-service URL. For example,
   * for parsing http://my.server.com/services/2.0/order/1223223322
   * basePath is http://my.server.com/services, hasVersion is set to true, and serviceName is
   * set to order
   */
  public CallPathParser(String basePath, boolean hasVersion, String serviceName) {
    this.basePath = basePath;
    this.hasVersion = hasVersion;
    this.serviceName = serviceName;
  }

  /**
   * Parses the incoming path. Returns CallPath.NULL_PATH if the parsing fails.
   */
  public CallPath parse(String callPath) {
    if (GreazeStrings.isEmpty(callPath)) {
      throw new ParseException(ParseFailureType.INVALID_PATH);
    }
    if (!GreazeStrings.isEmpty(basePath)) {
      if (!callPath.startsWith(basePath)) {
        throw new ParseException(ParseFailureType.INVALID_BASE_PATH);
      }
      callPath = callPath.substring(basePath.length());
    }
    double version = CallPath.IGNORE_VERSION;
    if (hasVersion) {
      try {
        int beginIndex = callPath.charAt(0) == '/' ? 1 : 0;
        int endIndex = GreazeStrings.indexOf(beginIndex, callPath, '/');
        version = Double.parseDouble(callPath.substring(beginIndex, endIndex));
        callPath = callPath.substring(endIndex);
      } catch (NumberFormatException nfe) {
        throw new ParseException(ParseFailureType.INVALID_VERSION);
      }
    }
    if (!GreazeStrings.isEmpty(serviceName)) {
      if (!callPath.startsWith(serviceName)) {
        throw new ParseException(ParseFailureType.INVALID_SERVICE_NAME);
      }
      callPath = callPath.substring(serviceName.length());
    }
    String resourceId = extraceResourceId(callPath);
    return new CallPath(basePath, version, serviceName, resourceId);
  }

  private String extraceResourceId(String path) {
    if (GreazeStrings.isEmpty(path)) {
      return null;
    }
    int beginIndex = path.startsWith("/") ? 1 : 0;
    int endIndex = GreazeStrings.indexOf(beginIndex, path, '?', '/');
    if (endIndex == -1) {
      endIndex = path.length();
    }
    return path.substring(beginIndex, endIndex);
  }
}
