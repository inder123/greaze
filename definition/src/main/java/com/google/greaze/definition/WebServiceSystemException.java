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

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.ExecutionException;

import com.google.gson.JsonParseException;

/**
 * Base class for all exceptions thrown by the Web service to indicate a system error condition. 
 * This should never be thrown to indicate bad user input.
 *
 * @author Inderjeet Singh
 */
@SuppressWarnings("serial")
public class WebServiceSystemException extends RuntimeException {

  protected final ErrorReason reason;

  public WebServiceSystemException(IOException e) {
    super(e);
    this.reason = getReasonForException(e);
  }

  public WebServiceSystemException(IllegalArgumentException e) {
    this(ErrorReason.PRECONDITION_FAILED, e.getMessage(), e);
  }

  public WebServiceSystemException(NullPointerException e) {
    this(ErrorReason.PRECONDITION_FAILED, e.getMessage(), e);
  }

  public WebServiceSystemException(JsonParseException e) {
    this(ErrorReason.BAD_REQUEST, e);
  }

  public WebServiceSystemException(InterruptedException e) {
    this(ErrorReason.UNEXPECTED_RETRYABLE_ERROR, e);
  }

  public WebServiceSystemException(ExecutionException e) {
    this(ErrorReason.UNEXPECTED_RETRYABLE_ERROR, e);
  }

  public WebServiceSystemException(ErrorReason reason, String msg) {
    super(msg);
    this.reason = reason;
  }

  public WebServiceSystemException(ErrorReason reason, Exception cause) {
    super(cause);
    this.reason = reason;
  }
  
  public WebServiceSystemException(ErrorReason reason, String msg, Exception cause) {
    super(msg, cause);
    this.reason = reason;
  }

  public ErrorReason getReason() {
    return reason;
  }

  private ErrorReason getReasonForException(IOException e) {
    // Look deeper into the IOException and figure out the correct reason
    if (e instanceof ConnectException) {
      return ErrorReason.LOCAL_NETWORK_FAILURE;
    }
    return ErrorReason.UNEXPECTED_RETRYABLE_ERROR;
  }
}
