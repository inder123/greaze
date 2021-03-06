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
package com.google.greaze.definition;

import java.lang.reflect.Type;

import com.google.greaze.definition.webservice.WebServiceRequest;

/**
 * Specification for URL parameters for a Web service call.
 *
 * @author Inderjeet Singh
 */
public class UrlParamsSpec {

  public static class Builder {
    private final HeaderMapSpec.Builder mapSpecBuilder;
    private Type type;

    public Builder() {
      mapSpecBuilder = new HeaderMapSpec.Builder();
      mapSpecBuilder.put(WebServiceRequest.INLINE_URL_PARAM, Boolean.class);
    }
    /**
     * Add all fields of the specified type as name-value pairs in the spec
     */
    public Builder setType(Type type) {
      this.type = type;
      return this;
    }

    public Builder put(String name, Type type) {
      mapSpecBuilder.put(name, type);
      return this;
    }

    public Builder putAll(UrlParamsSpec spec) {
      if (spec != null) {
        mapSpecBuilder.putAll(spec.getMapSpec());
      }
      return this;
    }

    public UrlParamsSpec build() {
      return new UrlParamsSpec(type, mapSpecBuilder.build());
    }
  }

  private final Type type;
  private final HeaderMapSpec spec;

  private UrlParamsSpec(/** Nullable */Type type, /** Nullable */HeaderMapSpec spec) {
    this.type = type;
    this.spec = spec;
  }

  public Type getType() {
    return type;
  }

  public HeaderMapSpec getMapSpec() {
    return spec;
  }

  public boolean hasParamsObject() {
    return type != null;
  }

  public boolean hasParamsMap() {
    return spec != null && spec.size() > 0;
  }
}
