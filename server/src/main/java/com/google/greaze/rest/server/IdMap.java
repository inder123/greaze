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
package com.google.greaze.rest.server;

import com.google.greaze.definition.rest.HasId;
import com.google.greaze.definition.rest.Id;

import java.lang.reflect.Type;

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
  protected IdMap(Type typeOfId) {
    super(Id.class, typeOfId);
  }

  public static <S extends HasId<Id<S>>> IdMap<S> create(Type typeOfId) {
    return new IdMap<S>(typeOfId);
  }
}
