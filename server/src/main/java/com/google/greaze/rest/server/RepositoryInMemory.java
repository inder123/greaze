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

import java.lang.reflect.Type;

import com.google.greaze.definition.rest.Id;
import com.google.greaze.definition.rest.RestResource;

/**
 * An in-memory map of rest resources
 *
 * @author Inderjeet Singh
 *
 * @param <R> Type variable for the resource
 */
public class RepositoryInMemory<R extends RestResource<R>> extends RepositoryInMemoryBase<Id<R>, R>
    implements Repository<R> {

  /**
   * @param typeOfResource class of the resource. For example, Order.class
   */
  public RepositoryInMemory(Type typeOfResource) {
    super(Id.class, typeOfResource);
  }
}
