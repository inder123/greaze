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
package com.google.greaze.rest.query.server;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.UntypedKey;
import com.google.greaze.definition.internal.utils.FieldNavigator;
import com.google.greaze.definition.webservice.WebServiceCallSpec;
import com.google.greaze.definition.webservice.WebServiceCallSpec.Builder;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Class with common code for server-side query support
 *
 * @author Inderjeet Singh
 */
public final class QueryHelperServer {
  public static WebServiceCallSpec generateQueryCallSpec(CallPath callPath, Type resourceType,
    Type resourceQueryParamsType) {
    Builder urlParamSpecBuilder = new WebServiceCallSpec.Builder(callPath)
          .setListBody(resourceType)
          .supportsHttpMethod(HttpMethod.GET);
    registerTypeMembers(resourceQueryParamsType, urlParamSpecBuilder);
    return urlParamSpecBuilder.build();
  }

  private static void registerTypeMembers(Type type, Builder urlParamSpecBuilder) {
    FieldNavigator navigator = new FieldNavigator(type);
    for (Field f : navigator.getFields()) {
      urlParamSpecBuilder.addUrlParam(new UntypedKey(f.getName(), f.getGenericType()));
    }
  }
}
