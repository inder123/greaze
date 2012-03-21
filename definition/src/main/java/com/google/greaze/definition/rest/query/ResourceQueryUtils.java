/*
 * Copyright (C) 2012 Greaze Authors
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
import java.lang.reflect.Type;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.rest.WebContextSpec;
import com.google.greaze.definition.webservice.WebServiceCallSpec;
import com.google.greaze.definition.webservice.WebServiceCallSpec.Builder;

/**
 * Helper class query related operations
 *
 * @author Inderjeet Singh
 */
public final class ResourceQueryUtils {

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

  private ResourceQueryUtils() {}
}
