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
package com.google.greaze.server.internal.utils;

import java.lang.reflect.Type;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.greaze.definition.TypedKey;
import com.google.greaze.definition.internal.utils.GreazeStrings;
import com.google.greaze.definition.rest.WebContext;
import com.google.greaze.definition.rest.WebContextSpec;
import com.google.gson.Gson;

/**
 * A utility class to extract {@link WebContext} from an {@link HttpServletRequest} headers.
 *
 * @author Inderjeet Singh
 */
public final class WebContextExtractor {
  private final WebContextSpec spec;
  private final Gson gson;
  public WebContextExtractor(WebContextSpec spec, Gson gson) {
    this.spec = spec;
    this.gson = gson;
  }

  public WebContext extract(HttpServletRequest request) {
    WebContext.Builder builder = new WebContext.Builder();
    if (spec != null) {
      for (Map.Entry<String, Type> entry : spec.getRequestHeaderSpec().entrySet()) {
        String keyName = entry.getKey();
        String header = request.getHeader(keyName);
        if (GreazeStrings.isNotEmpty(header)) {
          Type type = entry.getValue();
          TypedKey<Object> key = new TypedKey<Object>(keyName, type);
          Object value = gson.fromJson(header, type);
          builder.put(key, value);
        }
      }
    }
    return builder.build();
  }
}
