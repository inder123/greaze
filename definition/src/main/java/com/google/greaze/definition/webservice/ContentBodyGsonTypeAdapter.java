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
import com.google.greaze.definition.internal.utils.GreazePreconditions;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

@SuppressWarnings({"rawtypes", "unchecked"})
abstract class ContentBodyGsonTypeAdapter<CB extends ContentBody> extends TypeAdapter<CB> {
  private final TypeAdapter simpleBodyAdapter;
  private final LazyAdapterMap adapters;
  private final ContentBodySpec spec;

  ContentBodyGsonTypeAdapter(Gson gson, ContentBodySpec spec) {
    this.simpleBodyAdapter = gson.getAdapter(TypeToken.get(spec.getSimpleBodyType()));
    this.adapters = new LazyAdapterMap(gson, spec);
    this.spec = spec;
  }

  public abstract ContentBody.Builder createBuilder();

  @Override
  public CB read(JsonReader reader) throws IOException {
    ContentBody.Builder builder = createBuilder();
    ContentBodySpec spec = builder.getSpec();
    GreazePreconditions.checkArgument(this.spec.equals(spec));
    switch(spec.getContentBodyType()) {
      case SIMPLE:
        builder.setSimpleBody(simpleBodyAdapter.read(reader));
        break;
      case LIST:
        reader.beginArray();
        while (reader.peek() != JsonToken.END_ARRAY) {
          builder.addToListBody(simpleBodyAdapter.read(reader));
        }
        reader.endArray();
        break;
      case MAP:
        reader.beginObject();
        while (reader.peek() != JsonToken.END_OBJECT) {
          String key = reader.nextName();
          TypeAdapter adapter = adapters.getAdapter(key);
          Object value = adapter.read(reader);
          builder.put(key, value);
        }
        reader.endObject();
        break;
      default:
        throw new UnsupportedOperationException();
    }
    return (CB) builder.build();
  }

  @Override
  public void write(JsonWriter writer, CB value) throws IOException {
    ContentBody src = (ContentBody) value;
    ContentBodySpec bodySpec = src.getSpec();
    GreazePreconditions.checkArgument(this.spec.equals(bodySpec));
    switch(bodySpec.getContentBodyType()) {
      case SIMPLE:
        simpleBodyAdapter.write(writer, src.getSimpleBody());
        break;
      case LIST:
        writer.beginArray();
        for(Object entry : src.getListBody()) {
          simpleBodyAdapter.write(writer, entry);
        }
        writer.endArray();
        break;
      case MAP:
        writer.beginObject();
        for(Map.Entry<String, Object> entry : src.entrySet()) {
          String key = entry.getKey();
          TypeAdapter adapter = adapters.getAdapter(key);
          writer.name(key);
          adapter.write(writer, entry.getValue());
        }
        writer.endObject();
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }

  private final class LazyAdapterMap {
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