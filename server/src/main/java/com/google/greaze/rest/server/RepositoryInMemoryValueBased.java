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

import com.google.greaze.definition.rest.RestResourceValueBased;
import com.google.greaze.definition.rest.ValueBasedId;

/**
 * An in-memory map of rest resources
 *
 * @author Inderjeet Singh
 *
 * @param <R> Type variable for the resource
 */
public class RepositoryInMemoryValueBased<R extends RestResourceValueBased<R>>
    extends RepositoryInMemory<ValueBasedId<R>, R>
    implements RepositoryValueBased<R> {

  /**
   * @param classOfResource class of the resource. For example, Order.class
   */
  public RepositoryInMemoryValueBased(Class<? super R> classOfResource) {
    super(ValueBasedId.class, classOfResource);
  }
}
