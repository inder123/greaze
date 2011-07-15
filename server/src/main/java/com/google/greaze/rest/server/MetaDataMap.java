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

import com.google.greaze.definition.rest.MetaData;
import com.google.greaze.definition.rest.RestResource;
import com.google.greaze.definition.rest.Id;

/**
 * A map of resources to their MetaData
 *
 * @author Inderjeet Singh
 *
 * @param <R> the rest resource for which the metadata is being stored
 */
public class MetaDataMap<R extends RestResource<R>> extends MetaDataMapBase<Id<R>, R> {

  @Override
  public MetaData<R> get(Id<R> resourceId) {
    return (MetaData<R>)super.get(resourceId);
  }

  @Override
  protected MetaData<R> createMetaData() {
    return new MetaData<R>();
  }
}
