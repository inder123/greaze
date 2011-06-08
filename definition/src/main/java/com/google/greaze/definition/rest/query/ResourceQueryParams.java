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
package com.google.greaze.definition.rest.query;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.rest.WebContextSpec;
import com.google.greaze.definition.webservice.WebServiceCallSpec;
import com.google.greaze.definition.webservice.WebServiceCallSpec.Builder;

import java.lang.reflect.Type;

/**
 * This interface is implemented by all the query parameter types used for a {@link ResourceQueryBase}
 * 
 * @author Inderjeet Singh
 */
public class ResourceQueryParams {

  // Keep this in sync with the name of the variable queryName
  public static final String QUERY_NAME = "queryName";

  private final String queryName;

  public ResourceQueryParams(String queryName) {
    this.queryName = queryName;
  }

  public String getQueryName() {
    return queryName;
  }

  public static WebServiceCallSpec generateCallSpec(CallPath callPath, Type resourceType,
    Type resourceQueryParamsType, WebContextSpec webContextSpec) {
    Builder builder = new WebServiceCallSpec.Builder(callPath)
      .addAllRequestParams(webContextSpec.getRequestHeaderSpec())
      .setListBody(resourceType)
      .setUrlParams(resourceQueryParamsType)
      .supportsHttpMethod(HttpMethod.GET);
    if (callPath.hasVersion()) {
      builder.setVersion(callPath.getVersion());
    }
    return builder
      .build();
  }
}
