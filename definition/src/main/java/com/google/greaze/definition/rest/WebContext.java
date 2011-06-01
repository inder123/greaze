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
package com.google.greaze.definition.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.TypedKey;

/**
 * The context consist of certain headers pulled out for convenience.
 * For example, authorization headers, or an identification key.
 * Each greaze service needs to define its concrete implementation of WebContext
 *
 * @author Inderjeet Singh
 */
public class WebContext {
  public static final class Builder {
    private final Map<String, Object> map = new HashMap<String, Object>();
    public <T> Builder put(TypedKey<T> key, T value) {
      map.put(key.getName(), value);
      return this;
    }
    public WebContext build() {
      return new WebContext(map);
    }
  }
  private final Map<String, Object> map;

  public WebContext() {
    this(new HashMap<String, Object>());
  }

  private WebContext(Map<String, Object> map) {
    this.map = map;
  }

  @SuppressWarnings("unchecked")
  public <T> T get(TypedKey<T> key) {
    return (T) map.get(key.getName());
  }

  /**
   * Populates the specified builder with all the entries present in this context
   */
  public void populate(HeaderMap.Builder builder) {
    for (Entry<String, Object> entry : map.entrySet()) {
      builder.put(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public String toString() {
    return map.toString();
  }

}
