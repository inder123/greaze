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

import java.util.Map;


/**
 * Metadata associated with a repository for a rest resource. Metadata is of two types: persistent
 * and transient. All metadata is persistent by default, and must be a name-value pair of strings.
 * Transient metadata can be an arbitrary key-value pair of objects and is available through
 * {@link #getFromTransient(Object)}, {@link #putInTransient(Object, Object)},
 * and {@link #removeFromTransient(Object)} methods.
 *
 * @author Inderjeet Singh
 *
 * @param <R> The resource
 */
public final class MetaData<R extends RestResource<R>> extends MetaDataBase<Id<R>, R> {

  public static <RS extends RestResource<RS>> MetaData<RS> create() {
    return new MetaData<RS>();
  }

  public MetaData() {
  }

  MetaData(Map<String, String> values) {
    super(values);
  }
}
