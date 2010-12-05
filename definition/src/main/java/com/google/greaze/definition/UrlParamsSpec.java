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

/**
 * Specification for URL parameters for a Web service call
 *
 * @author Inderjeet Singh
 */
public class UrlParamsSpec {

  public static class Builder {
    private final HeaderMapSpec.Builder mapSpecBuilder = new HeaderMapSpec.Builder();
    private final Type type;

    public Builder() {
      this(null);
    }
    public Builder(Type type) {
      this.type = type;
    }

    public Builder put(String name, Type type) {
      mapSpecBuilder.put(name, type);
      return this;
    }

    public UrlParamsSpec build() {
      return new UrlParamsSpec(type, mapSpecBuilder.build());
    }
  }
  private final Type type;
  private final HeaderMapSpec spec;

  public UrlParamsSpec(Type type, HeaderMapSpec spec) {
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
