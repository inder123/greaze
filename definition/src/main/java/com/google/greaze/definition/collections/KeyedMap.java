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
package com.google.greaze.definition.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.greaze.definition.rest.Id;

/**
 * A map that is keyed with Ids
 *
 * @author Inderjeet Singh
 */
public class KeyedMap<R, V> {
  protected final Map<String, V> map = new HashMap<String, V>();

  public synchronized V get(Id<R> key) {
    return map.get(key.getValue());
  }

  public synchronized V put(Id<R> key, V value) {
    return map.put(key.getValue(), value);
  }

  public synchronized V put(String key, V value) {
    return map.put(key, value);
  }

  public synchronized void remove(Id<R> key) {
    map.remove(key.getValue());
  }

  public synchronized boolean containsKey(Id<R> key) {
    return key == null ? null : map.containsKey(key.getValue());
  }

  public synchronized boolean containsKey(String key) {
    return key == null ? null : map.containsKey(key);
  }

  @Override
  public String toString() {
    return map.toString();
  }

  public void clear() {
    map.clear();
  }

  public boolean containsValue(V value) {
    return map.containsValue(value);
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }

  public int size() {
    return map.size();
  }

  public Collection<V> values() {
    return map.values();
  }

  @Override
  public int hashCode() {
    return map.hashCode();
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    KeyedMap other = (KeyedMap)obj;
    return map.equals(other.map);
  }

  public static <R1, V1> KeyedMap<R1, V1> mapOf(Id<R1> key, V1 value) {
    KeyedMap<R1, V1> map = new KeyedMap<R1, V1>();
    if (key != null) {
      map.put(key, value);
    }
    return map;
  }
}
