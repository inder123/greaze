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
 * A collection of response codes matching those contained in HttpServletResponse.
 * We do not depend on HttpServletResponse to avoid dependency on servlet jar.
 *
 * @author Inderjeet Singh
 */
public final class HttpResponseCodes {
  /**
   * Same as HttpServletResponse.SC_BAD_REQUEST (400)
   */
  public static final int BAD_REQUEST = 400;

  /**
   * Same as HttpServletResponse.SC_INTERNAL_SERVER_ERROR (500)
   */
  public static final int INTERNAL_SERVER_ERROR = 500;

  /**
   * Same as HttpServletResponse.SC_SERVICE_UNAVAILABLE (503)
   */
  public static final int SERVICE_UNAVAILABLE = 503;
}
