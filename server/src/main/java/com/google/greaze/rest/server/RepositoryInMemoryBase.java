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

import java.util.Map;

import com.google.greaze.definition.rest.ResourceId;
import com.google.greaze.definition.rest.RestResourceBase;
import com.google.greaze.rest.server.collections.IdMapBase;

/**
 * An in-memory map of rest resources
 *
 * @author inder
 *
 * @param <R> Type variable for the resource
 */
public class RepositoryInMemoryBase<I extends ResourceId, R extends RestResourceBase<I, R>>
    implements RepositoryBase<I, R> {

  protected final IdMapBase<I, R> resources;

  /**
   * @param rawClassOfI class for the Id type. For example, ValueBasedId.class
   * @param typeOfResource class of the resource. For example, Order.class
   */
  public RepositoryInMemoryBase(Class<? super I> rawClassOfI) {
    this.resources = IdMapBase.create(rawClassOfI);
    new MetaDataMapBase<I, R>();
  }

  /**
   * @param rawClassOfI class for the Id type. For example, ValueBasedId.class
   * @param store for the storage of resources. Note that the metadata is NOT stored in the storage
   *   but in the local memory.
   */
  public RepositoryInMemoryBase(Class<? super I> rawClassOfI, Map<String, R> store) {
    this.resources = IdMapBase.create(rawClassOfI, store);
    new MetaDataMapBase<I, R>();
  }

  @Override
  public R get(I resourceId) {
    return resources.get(resourceId);
  }

  @Override
  public R put(R resource) {
    assignIdIfNeeded(resource);
    resource = resources.put(resource);
    return resource;
  }

  @Override
  public void delete(I resourceId) {
    resources.delete(resourceId);
  }

  @Override
  public boolean exists(I resourceId) {
    return resources.exists(resourceId);
  }

  @Override
  public I getNextId() {
    return resources.getNextId();
  }

  /**
   * Ensures that the specified resource has a valid id that will be used when it is saved
   */
  protected I assignIdIfNeeded(R resource) {
    if (!resource.hasId()) {
      I id = getNextId();
      resource.setId(id);
    }
    return resource.getId();
  }

  @Override
  public long size() {
    return resources.size();
  }
}
