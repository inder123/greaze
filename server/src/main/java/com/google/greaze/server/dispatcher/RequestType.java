/*
 * Copyright (C) 2010 Google Inc.
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
package com.google.greaze.server.dispatcher;

import com.google.common.base.Preconditions;
import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.rest.query.ResourceQueryParams;

import java.util.Map;

/**
 * Enum indicating the type of the HttpServletRequest
 *
 * @author Inderjeet Singh
 */
public enum RequestType {
  RESOURCE_ACCESS,
  RESOURCE_QUERY,
  WEBSERVICE;

  /**
   * @param callPath
   * @param queryName name of the query
   * @param resourcePrefix the prefix used to identify resource requests.
   *   For example, /rest or /resource
   * @return the type of the request
   */
  public static RequestType getRequestType(CallPath callPath,
      String queryName, String resourcePrefix) {
    String path = callPath.get();
    if (!path.startsWith(resourcePrefix)) {
      return WEBSERVICE;
    }
    if (queryName == null) {
      return RESOURCE_ACCESS;
    }
    return RESOURCE_QUERY;
  }

  public static String getQueryName(Map<String, String[]> requestParameterMap) {
    String[] queryNames = requestParameterMap.get(ResourceQueryParams.QUERY_NAME);
    Preconditions.checkArgument(queryNames == null || queryNames.length <= 1);
    return queryNames == null || queryNames.length == 0 ? null : queryNames[0];
  }
}
