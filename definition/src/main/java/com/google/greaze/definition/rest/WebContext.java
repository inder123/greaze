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

import com.google.greaze.definition.TypedKey;
import com.google.greaze.definition.TypedKeyMap;

/**
 * The context consist of certain headers pulled out for convenience.
 * For example, authorization headers, or an identification key.
 * Each greaze service needs to define its concrete implementation of WebContext
 *
 * @author Inderjeet Singh
 */
public class WebContext {
  private final TypedKeyMap map;

  public WebContext() {
    this(new TypedKeyMap());
  }

  public WebContext(TypedKeyMap map) {
    this.map = map;
  }

  public <T> T get(TypedKey<T> key) {
    return map.get(key);
  }
}
