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
package com.google.greaze.definition.webservice;

import com.google.greaze.definition.ContentBody;
import com.google.greaze.definition.ContentBodySpec;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * A helper class to share common code to serialize and deserialize a content body
 *
 * @author Inderjeet Singh
 */
final class GsonHelper {

  static JsonElement serialize(ContentBody src, Type typeOfSrc, 
      JsonSerializationContext context) {
    ContentBodySpec bodySpec = src.getSpec();
    switch(bodySpec.getContentBodyType()) {
      case SIMPLE:
        return context.serialize(src.getSimpleBody());
      case LIST:
        JsonArray array = new JsonArray();
        for(Object entry : src.getListBody()) {
          array.add(context.serialize(entry, bodySpec.getSimpleBodyType()));
        }
        return array;
      case MAP:
        JsonObject map = new JsonObject();
        for(Map.Entry<String, Object> entry : src.entrySet()) {
          String key = entry.getKey();
          Type entryType = bodySpec.getTypeFor(key);
          JsonElement value = context.serialize(entry.getValue(), entryType);
          map.add(key, value);        
        }
        return map;
      default:
        throw new UnsupportedOperationException();
    }
  }

  @SuppressWarnings("unchecked")
  static <T extends ContentBody, TB extends ContentBody.Builder> T deserialize(JsonElement json,
      Type typeOfT, JsonDeserializationContext context, TB builder) throws JsonParseException {
    ContentBodySpec spec = builder.getSpec();
    switch(spec.getContentBodyType()) {
      case SIMPLE:
        builder.setSimpleBody(
          context.deserialize(json, spec.getSimpleBodyType()));
        break;
      case LIST:
        for (JsonElement element : json.getAsJsonArray()) {
          builder.addToListBody(
            context.<Object>deserialize(element, spec.getSimpleBodyType()));
        }
        break;
      case MAP:
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
          String key = entry.getKey();
          Type entryType = spec.getTypeFor(key);
          Object value = context.deserialize(entry.getValue(), entryType);
          builder.put(key, value);
        }
        break;
      default:
        throw new UnsupportedOperationException();
    }
    return (T) builder.build();
  }
}
