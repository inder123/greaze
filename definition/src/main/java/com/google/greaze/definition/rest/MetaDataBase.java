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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.greaze.definition.TypedKey;
import com.google.greaze.definition.internal.utils.$GreazeTypes;
import com.google.greaze.definition.internal.utils.GreazePreconditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

/**
 * Metadata associated with a repository for a rest resource. Metadata is of two types: persistent
 * and transient. All metadata is persistent by default, and must be a name-value pair of strings.
 * Transient metadata can be an arbitrary key-value pair of objects and is available through
 * {@link #getFromTransient(Object)}, {@link #putInTransient(Object, Object)},
 * and {@link #removeFromTransient(Object)} methods.
 *
 * @author inder
 *
 * @param <R> The resource
 */
public class MetaDataBase<I extends ResourceId, R extends RestResourceBase<I, R>> {

  private final Map<String, String> map;
  private final transient Map<Object, Object> mapTransient;

  public static <II extends ResourceId, RS extends RestResourceBase<II, RS>> MetaDataBase<II, RS> create() {
    return new MetaDataBase<II, RS>();
  }

  public MetaDataBase() {
    this(new HashMap<String, String>());
  }

  protected MetaDataBase(Map<String, String> values) {
    this.map = values == null ? new HashMap<String, String>() : values;
    this.mapTransient = new HashMap<Object, Object>();
  }

  public String getString(String key) {
    return map.get(key);
  }

  public void putString(String key, String value) {
    map.put(key, value);
  }

  public void clear() {
    map.clear();
    mapTransient.clear();
  }

  public boolean getBoolean(String key) {
    String value = map.get(key);
    return value == null ? false : Boolean.parseBoolean(value);
  }

  public void putBoolean(String key, boolean value) {
    map.put(key, String.valueOf(value));
  }

  public void remove(String key) {
    map.remove(key);
  }

  public <T> void remove(TypedKey<T> key) {
    map.remove(key.getName());
  }

  public Object getFromTransient(Object key) {
    return mapTransient.get(key);
  }

  @SuppressWarnings("unchecked")
  public <T> T getFromTransient(TypedKey<T> key) {
    return (T) mapTransient.get(key.getName());
  }

  public void putInTransient(Object key, Object value) {
    mapTransient.put(key, value);
  }

  public <T> void putInTransient(TypedKey<T> key, T value) {
    mapTransient.put(key.getName(), value);
  }

  public void removeFromTransient(Object key) {
    mapTransient.remove(key);
  }

  @Override
  public String toString() {
    return new StringBuilder().append(map).append(',').append(mapTransient).toString();
  }

  /**
   * Gson Type adapter for {@link MetaDataBase}. The serialized representation on wire is just a
   * Map<String, String>
   */
  public static final class GsonTypeAdapter implements JsonSerializer<MetaDataBase<?, ?>>,
    JsonDeserializer<MetaDataBase<?, ?>>{

    private static final Type MAP_TYPE =
      $GreazeTypes.newParameterizedTypeWithOwner(null, Map.class, String.class, String.class);

    @Override
    public MetaDataBase<?, ?> deserialize(JsonElement json, Type typeOfT,
        JsonDeserializationContext context) throws JsonParseException {
      Map<String, String> map = context.deserialize(json, MAP_TYPE);
      return createInstance(typeOfT, map);
    }

    @Override
    public JsonElement serialize(MetaDataBase<?, ?> src, Type typeOfSrc,
        JsonSerializationContext context) {
      return context.serialize(src.map, MAP_TYPE);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static MetaDataBase<?, ?> createInstance(Type typeOfT, Map<String, String> map) {
      Class<?> metaDataClass;
      if (typeOfT instanceof Class) {
        metaDataClass = (Class) typeOfT;
      } else if (typeOfT instanceof ParameterizedType) {
        metaDataClass = (Class<?>) ((ParameterizedType) typeOfT).getRawType();
      } else {
        throw new JsonSyntaxException("Expecting MetaData, found: " + typeOfT);
      }
      GreazePreconditions.checkArgument(MetaDataBase.class.isAssignableFrom(metaDataClass));
      return MetaData.class.isAssignableFrom(metaDataClass) ?
          new MetaData(map) : new MetaDataBase(map);
    }
  }
}
