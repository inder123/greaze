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
package com.google.greaze.rest.server;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.greaze.definition.rest.ResourceId;
import com.google.greaze.definition.rest.RestCallSpec;
import com.google.greaze.definition.rest.RestResourceBase;

/**
 * A map of {@link RestCallSpec}, {@link RestResponseBaseBuilder} to help figure out which
 * {@link RestResponseBaseBuilder} to use for a {@link RestCallSpec}.
 *
 * @author Inderjeet Singh
 */
public final class ResponseBuilderMap {
  public static final class Builder {
    private final Map<Type, RestResponseBaseBuilder<?, ?>> map =
      new HashMap<Type, RestResponseBaseBuilder<?, ?>>();
    
    public <I extends ResourceId, R extends RestResourceBase<I, R>> Builder set(
        Type resourceType, RestResponseBaseBuilder<I, R> responseBuilder) {
      map.put(resourceType, responseBuilder);
      return this;
    }

    public ResponseBuilderMap build() {
      return new ResponseBuilderMap(map);
    }
  }

  private final Map<Type, RestResponseBaseBuilder<?, ?>> map;

  public ResponseBuilderMap(Map<Type, RestResponseBaseBuilder<?, ?>> map) {
    this.map = map;
  }
  
  public RestResponseBaseBuilder<?, ?> get(Type resourceType) {
    return map.get(resourceType);
  }
}
