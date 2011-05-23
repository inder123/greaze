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
package com.google.greaze.rest.server.collections;

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
  public V get(Id<R> key) {
    return map.get(key.getValue());
  }

  public synchronized void put(Id<R> key, V value) {
    map.put(key.getValue(), value);
  }

  public synchronized void remove(Id<R> key) {
    map.remove(key.getValue());
  }

  @Override
  public String toString() {
    return map.toString();
  }
}
