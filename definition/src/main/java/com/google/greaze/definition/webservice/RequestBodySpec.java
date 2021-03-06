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
package com.google.greaze.definition.webservice;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.greaze.definition.ContentBodySpec;
import com.google.greaze.definition.ContentBodyType;
import com.google.greaze.definition.TypedKey;

/**
 * Specification of a {@link RequestBody}.
 *
 * @author Inderjeet Singh
 */
public final class RequestBodySpec extends ContentBodySpec {

  public static class Builder {
    private final Map<String, Type> paramsSpec = new LinkedHashMap<String, Type>();
    private ContentBodyType contentBodyType;
    private Type simpleBodyType;

    public Builder setSimpleBody(Type simpleBodyType) {
      this.contentBodyType = ContentBodyType.SIMPLE;
      this.simpleBodyType = simpleBodyType;
      return this;
    }

    public Builder setListBody(Type listElementType) {
      this.contentBodyType = ContentBodyType.LIST;
      this.simpleBodyType = listElementType;
      return this;
    }

    public Builder setMapBody() {
      this.contentBodyType = ContentBodyType.MAP;
      return this;
    }

    public <T> Builder put(TypedKey<T> param) {
      paramsSpec.put(param.getName(), param.getTypeOfT());
      return this;
    }

    public RequestBodySpec build() {
      return new RequestBodySpec(contentBodyType, paramsSpec, simpleBodyType);
    }
  }

  /**
   * @param contentBodyType the type of content expected in the body
   * @param paramsSpec pass null if ContentBodyType is {@link ContentBodyType#SIMPLE}
   */
  public RequestBodySpec(ContentBodyType contentBodyType,
                         Map<String, Type> paramsSpec, Type simpleBodyType) {
    super(contentBodyType, paramsSpec, simpleBodyType);
  }
}
