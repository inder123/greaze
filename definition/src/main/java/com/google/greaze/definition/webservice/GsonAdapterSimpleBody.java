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

import com.google.greaze.definition.ContentBody;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

@SuppressWarnings({"rawtypes", "unchecked"})
abstract class GsonAdapterSimpleBody<CB extends ContentBody> extends TypeAdapter<CB> {
  private final TypeAdapter simpleBodyAdapter;

  GsonAdapterSimpleBody(TypeAdapter simpleBodyAdapter) {
    this.simpleBodyAdapter = simpleBodyAdapter;
  }

  public abstract ContentBody.Builder createBuilder();

  @Override
  public CB read(JsonReader reader) throws IOException {
    ContentBody.Builder builder = createBuilder();
    builder.setSimpleBody(simpleBodyAdapter.read(reader));
    return (CB) builder.build();
  }

  @Override
  public void write(JsonWriter writer, CB value) throws IOException {
    ContentBody src = (ContentBody) value;
    simpleBodyAdapter.write(writer, src.getSimpleBody());
  }
}