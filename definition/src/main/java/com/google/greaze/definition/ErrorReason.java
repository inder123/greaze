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

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * A collection of reasons for failure of a Web service request
 *
 * @author Inderjeet Singh
 */
public enum ErrorReason {
  BAD_REQUEST(HttpURLConnection.HTTP_BAD_REQUEST),
  INVALID_CALLPATH(HttpURLConnection.HTTP_NOT_IMPLEMENTED),
  UNEXPECTED_RETRYABLE_ERROR(HttpURLConnection.HTTP_UNAVAILABLE),
  UNEXPECTED_PERMANENT_ERROR(HttpURLConnection.HTTP_INTERNAL_ERROR),
  SERVER_UNAVAILABLE(HttpURLConnection.HTTP_UNAVAILABLE),
  PRECONDITION_FAILED(HttpURLConnection.HTTP_UNAVAILABLE),
  /**
   * This error reason indicates that the local connection on the client side is
   * not working correctly. For example, a mobile phone client has lost all data connections.
   */
  LOCAL_NETWORK_FAILURE(HttpURLConnection.HTTP_BAD_GATEWAY);
  
  private final int responseCode;
  /**
   * The name of the HTTP response header that carries the error reason enum value
   */
  public static final String HTTP_RESPONSE_HEADER_NAME = "ErrorReason";
  private ErrorReason(int responseCode) {
    this.responseCode = responseCode;
  }

  public int getResponseCode() {
    return responseCode;
  }

  private static ErrorReason getProbableCause(int responseCode, IOException e) {
    switch (responseCode) {
      case HttpURLConnection.HTTP_UNAVAILABLE:
        return UNEXPECTED_RETRYABLE_ERROR;
      default:
        return UNEXPECTED_PERMANENT_ERROR;
    }
  }

  public static ErrorReason fromValue(HttpURLConnection conn, IOException e) {
    String errorReason = conn.getHeaderField(ErrorReason.HTTP_RESPONSE_HEADER_NAME);
    ErrorReason reason = valueOf(errorReason);
    if (reason == null) {
      try {
        reason = getProbableCause(conn.getResponseCode(), e);
      } catch (IOException e2) {
      }
    }
    return reason;
  }

  public boolean isRetryableError() {
    return this == UNEXPECTED_RETRYABLE_ERROR || this == SERVER_UNAVAILABLE
      || this == LOCAL_NETWORK_FAILURE; 
  }
}
