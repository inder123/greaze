/*
 * Copyright (C) 2011 Google Inc.
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

import java.util.List;

import com.google.common.collect.Lists;
import com.google.greaze.definition.rest.HasId;
import com.google.greaze.definition.rest.Id;

/**
 * Helper methods on {@link Repository} classes
 *
 * @author Inderjeet Singh
 */
public final class Repositories {

  /**
   * loads the resources corresponding to the specified ids
   *
   * @param <R> The type of resource
   * @param ids the ids for which resources need to be loaded
   * @param repo the repository from where to load resources
   * @return an ordered list of resources corresponding to the ids. If a resource is not found
   *   for an id, null is placed in the list.
   */
  public static <R extends HasId<Id<R>>> List<R> load(List<Id<R>> ids, Repository<R> repo) {
    List<R> resources = Lists.newArrayList();
    for (Id<R> id : ids) {
      R player = repo.get(id);
      resources.add(player);
    }
    return resources;
  }
}
