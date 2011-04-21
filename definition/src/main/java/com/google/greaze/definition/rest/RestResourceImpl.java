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

/**
 * A base class to implement a REST resource that implements common id related methods.
 *
 * @author Inderjeet Singh
 *
 * @param <R> The intended resource
 */
public class RestResourceImpl<R> implements RestResource<R> {

  protected Id<R> id;

  public RestResourceImpl() { }

  public RestResourceImpl(Id<R> id) {
    this.id = id;
  }

  @Override
  public Id<R> getId() {
    return id;
  }

  @Override
  public void setId(Id<R> id) {
    this.id = id;
  }

  @Override
  public boolean hasId() {
    return Id.isValid(id);
  }
}
