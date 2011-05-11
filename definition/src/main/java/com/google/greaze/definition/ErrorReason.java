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

/**
 * A collection of reasons for failure of a Web service request
 *
 * @author Inderjeet Singh
 */
public enum ErrorReason {
  BAD_REQUEST(HttpResponseCodes.BAD_REQUEST),
  VERSION_MISMATCH(HttpResponseCodes.BAD_REQUEST),
  INVALID_CALLPATH(HttpResponseCodes.BAD_REQUEST),
  UNEXPECTED_RETRYABLE_ERROR(HttpResponseCodes.SERVICE_UNAVAILABLE),
  UNEXPECTED_PERMANENT_ERROR(HttpResponseCodes.INTERNAL_SERVER_ERROR),
  SERVER_UNAVAILABLE(HttpResponseCodes.SERVICE_UNAVAILABLE),
  PRECONDITION_FAILED(HttpResponseCodes.SERVICE_UNAVAILABLE);
  
  private final int responseCode;

  private ErrorReason(int responseCode) {
    this.responseCode = responseCode;
  }

  public int getResponseCode() {
    return responseCode;
  }
}
