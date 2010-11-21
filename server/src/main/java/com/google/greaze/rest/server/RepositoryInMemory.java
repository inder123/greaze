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

import com.google.common.base.Preconditions;
import com.google.greaze.definition.rest.ResourceId;
import com.google.greaze.definition.rest.MetaDataBase;
import com.google.greaze.definition.rest.RestResourceBase;

/**
 * An in-memory map of rest resources
 *
 * @author inder
 *
 * @param <R> Type variable for the resource
 */
public class RepositoryInMemory<I extends ResourceId, R extends RestResourceBase<I, R>> implements RepositoryBase<I, R> {
  private static final String METADATA_KEY_IS_FRESHLY_ASSIGNED_ID = "isFreshlyAssignedId";

  private final IdMapBase<I, R> resources;
  private final MetaDataMapBase<I, R> metaDataMap;

  /**
   * @param rawClassOfI class for the Id type. For example, ValueBasedId.class
   * @param classOfResource class of the resource. For example, Order.class
   */
  public RepositoryInMemory(Class<? super I> rawClassOfI, Class<? super R> classOfResource) {
    this.resources = IdMapBase.create(rawClassOfI, classOfResource);
    this.metaDataMap = new MetaDataMapBase<I, R>();
  }

  @Override
  public R get(I resourceId) {
    return resources.get(resourceId);
  }

  public boolean isFreshlyAssignedId(I resourceId) {
    MetaDataBase<I, R> metaData = metaDataMap.get(resourceId);
    if (metaData == null) {
      return false;
    }
    return metaData.getBoolean(METADATA_KEY_IS_FRESHLY_ASSIGNED_ID);
  }

  @Override
  public R put(R resource) {
    if (!resource.hasId()) {
      // insert semantics
      assignId(resource);
    } else {
      I id = resource.getId();
      if (!isFreshlyAssignedId(id)) {
        // update semantics
        Preconditions.checkState(resources.exists(resource.getId()));
      }
    }
    resource = resources.put(resource);
    metaDataMap.get(resource.getId()).remove(METADATA_KEY_IS_FRESHLY_ASSIGNED_ID);
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
    if (resource.getId() == null) {
      I id = resources.getNextId();
      resource.setId(id);
      metaDataMap.get(id).putBoolean(METADATA_KEY_IS_FRESHLY_ASSIGNED_ID, true);
    }
    return resource.getId();
  }
}
