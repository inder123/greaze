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
package com.google.greaze.definition.rest;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * {@link TypeAdapterFactory} for {@link Id}
 *
 * @author inder
 */
public final class IdGsonTypeAdapterFactory implements TypeAdapterFactory {
  public <T> TypeAdapter<T> create(Gson context, TypeToken<T> type) {
    if (type.getRawType() != Id.class) {
      return null;
    }
    return new TypeAdapter<T>() {
      @SuppressWarnings("unchecked")
      public T read(JsonReader reader) throws IOException {
        return (T) Id.get(reader.nextString());
      }
      @SuppressWarnings("rawtypes")
      @Override
      public void write(JsonWriter writer, T value) throws IOException {
        if (value == null) {
          writer.nullValue();
        } else {
          writer.value(((Id) value).getValue());
        }
      }
    };
  }
}