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

  private final IdMapBase<I, R> resources;
  protected final Type typeOfResource;

  /**
   * @param rawClassOfI class for the Id type. For example, ValueBasedId.class
   * @param typeOfResource class of the resource. For example, Order.class
   */
  public RepositoryInMemoryBase(Class<? super I> rawClassOfI, Type typeOfResource) {
    this.resources = IdMapBase.create(rawClassOfI, typeOfResource);
    new MetaDataMapBase<I, R>();
    this.typeOfResource = typeOfResource;
  }

  /**
   * @param rawClassOfI class for the Id type. For example, ValueBasedId.class
   * @param typeOfResource class of the resource. For example, Order.class
   * @param store for the storage of resources. Note that the metadata is NOT stored in the storage
   *   but in the local memory.
   */
  public RepositoryInMemoryBase(
      Class<? super I> rawClassOfI, Type typeOfResource, Map<String, R> store) {
    this.typeOfResource = typeOfResource;
    this.resources = IdMapBase.create(rawClassOfI, typeOfResource, store);
    new MetaDataMapBase<I, R>();
  }

  @Override
  public R get(I resourceId) {
    return resources.get(resourceId);
  }

  @Override
  public R put(R resource) {
    if (!resource.hasId()) {
      // insert semantics
      assignId(resource);
    }
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

  @Override
  public I assignId(R resource) {
    if (!resource.hasId()) {
      I id = resources.getNextId();
      resource.setId(id);
    }
    return resource.getId();
  }

  @Override
  public long size() {
    return resources.size();
  }
}
