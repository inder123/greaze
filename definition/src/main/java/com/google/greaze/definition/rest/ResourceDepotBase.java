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
 * A place to access a REST resource for GET, PUT, POST, DELETE
 *
 * @author Inderjeet Singh
 *
 * @param <I> Id type of the resource
 * @param <R> The resource
 */
public interface ResourceDepotBase<I extends ResourceId, R extends RestResourceBase<I, R>> {

  public R get(I resourceId, WebContext context);

  public R post(R resource, WebContext context);

  public R put(R resource, WebContext context);

  public void delete(I resourceId, WebContext context);
}
