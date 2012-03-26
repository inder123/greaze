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
package com.google.greaze.rest.server.collections;

import java.util.Map;

import com.google.greaze.definition.rest.HasId;
import com.google.greaze.definition.rest.Id;

/**
 * This class provides a type-safe map to access values associated with Ids
 *
 * @author Inderjeet Singh
 *
 * @param <T> the type of the objects being kept in the map
 */
public class IdMap<T extends HasId<Id<T>>> extends IdMapBase<Id<T>, T> {

  /**
   * Use create(Type) instead of constructor
   */
  protected IdMap() {
    super(Id.class);
  }

  /**
   * Use create(Type) instead of constructor
   */
  protected IdMap(Map<String, T> store) {
    super(store, Id.class);
  }

  public static <S extends HasId<Id<S>>> IdMap<S> create() {
    return new IdMap<S>();
  }

  public static <S extends HasId<Id<S>>> IdMap<S> create(Map<String, S> store) {
    return new IdMap<S>(store);
  }
}
