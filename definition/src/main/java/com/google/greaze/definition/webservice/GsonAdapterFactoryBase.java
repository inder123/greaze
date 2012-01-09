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
package com.google.greaze.definition.webservice;

import java.lang.reflect.Type;

import com.google.greaze.definition.ContentBody;
import com.google.greaze.definition.ContentBodySpec;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

abstract class GsonAdapterFactoryBase<CB extends ContentBody, CBS extends ContentBodySpec>
    implements TypeAdapterFactory {
  protected final CBS spec;
  protected final Type bodyClass;
  public GsonAdapterFactoryBase(CBS spec, TypeToken<CB> bodyClass) {
    this.spec = spec;
    this.bodyClass = bodyClass.getType();
  }

  public abstract ContentBody.Builder createBuilder();

  @SuppressWarnings("unchecked")
  @Override
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    return bodyClass == type.getRawType()
        ? (TypeAdapter<T>) create(gson) // runtime check guarantees 'T == ContentBody'
        : null; 
  }

  private TypeAdapter<ContentBody> create(Gson gson) {
    switch(spec.getContentBodyType()) {
      case SIMPLE:
        return new GsonAdapterSimpleBody<ContentBody>(gson, spec) {
          @Override
          public ContentBody.Builder createBuilder() {
            return GsonAdapterFactoryBase.this.createBuilder();
          }
        };
      case LIST:
        return new GsonAdapterListBody<ContentBody>(gson, spec) {
          @Override
          public ContentBody.Builder createBuilder() {
            return GsonAdapterFactoryBase.this.createBuilder();
          }
        };
      case MAP:
        return new GsonAdapterMapBody<ContentBody>(gson, spec) {
          @Override
          public ContentBody.Builder createBuilder() {
            return GsonAdapterFactoryBase.this.createBuilder();
          }
        };
      default:
        throw new IllegalArgumentException("unexpected: " + spec.getContentBodyType());
    }
  }
}