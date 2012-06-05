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

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.greaze.definition.ContentBody;
import com.google.greaze.definition.ContentBodySpec;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

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
  public <T> TypeAdapter<T> create(final Gson gson, TypeToken<T> type) {
    if (bodyClass != type.getRawType()) return null;
    return (TypeAdapter<T>) create(gson);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private TypeAdapter<ContentBody> create(final Gson gson) {
    final TypeAdapter simpleBodyAdapter = gson.getAdapter(TypeToken.get(spec.getSimpleBodyType()));
    switch(spec.getContentBodyType()) {
      case SIMPLE:
        return new TypeAdapter<ContentBody>() {
          @Override
          public ContentBody read(JsonReader reader) throws IOException {
            return createBuilder()
                .setSimpleBody(simpleBodyAdapter.read(reader))
                .build();
          }
          public void write(JsonWriter writer, ContentBody value) throws IOException {
            ContentBody src = (ContentBody) value;
            simpleBodyAdapter.write(writer, src.getSimpleBody());
          }
        };
      case LIST:
        return new TypeAdapter<ContentBody>() {
          @Override
          public ContentBody read(JsonReader reader) throws IOException {
            ContentBody.Builder builder = createBuilder();
            reader.beginArray();
            while (reader.hasNext()) {
              builder.addToListBody(simpleBodyAdapter.read(reader));
            }
            reader.endArray();
            return (ContentBody) builder.build();
          }
          @Override
          public void write(JsonWriter writer, ContentBody value) throws IOException {
            ContentBody src = (ContentBody) value;
            writer.beginArray();
            for(Object entry : src.getListBody()) {
              simpleBodyAdapter.write(writer, entry);
            }
            writer.endArray();
          }
        };
      case MAP:
        return new TypeAdapter<ContentBody>() {
          private final LazyAdapterMap adapters = new LazyAdapterMap(gson, spec);
          @Override
          public ContentBody read(JsonReader reader) throws IOException {
            ContentBody.Builder builder = createBuilder();
            reader.beginObject();
            while (reader.hasNext()) {
              String key = reader.nextName();
              TypeAdapter adapter = adapters.getAdapter(key);
              Object value = adapter.read(reader);
              builder.put(key, value);
            }
            reader.endObject();
            return (CB) builder.build();
          }
          @Override
          public void write(JsonWriter writer, ContentBody value) throws IOException {
            ContentBody src = (ContentBody) value;
            writer.beginObject();
            for(Map.Entry<String, Object> entry : src.entrySet()) {
              String key = entry.getKey();
              TypeAdapter adapter = adapters.getAdapter(key);
              writer.name(key);
              adapter.write(writer, entry.getValue());
            }
            writer.endObject();
          }
        };
      default:
        throw new IllegalArgumentException("unexpected: " + spec.getContentBodyType());
    }
  }

  @SuppressWarnings("rawtypes")
  private static final class LazyAdapterMap {
    private final Gson gson;
    private final ContentBodySpec spec;
    private final Map<String, TypeAdapter> map = new HashMap<String, TypeAdapter>();
    LazyAdapterMap(Gson gson, ContentBodySpec spec) {
      this.gson = gson;
      this.spec = spec;
    }
    TypeAdapter getAdapter(String paramName) {
      TypeAdapter adapter = map.get(paramName);
      if (adapter == null) {
        Type entryType = spec.getTypeFor(paramName);
        adapter = gson.getAdapter(TypeToken.get(entryType));
        map.put(paramName, adapter);
      }
      return adapter;
    }
  }
}