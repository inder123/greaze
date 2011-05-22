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

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.greaze.definition.rest.HasId;
import com.google.greaze.definition.rest.Id;

/**
 * A class with utility methods related to {@link Repository}
 *
 * @author Inderjeet Singh
 */
public final class Repositories {

  /**
   * Loads the resources corresponding to the specified ids from the specified repository.
   *
   * @param <R> the type of {@link Repository}
   * @param ids A list of ids for the resource
   * @param repo A repository for the resources
   * @return A list of resources corresponding to the specified ids. The resources are
   *   returned in the order of the ids. If an id doesn't have a corresponding resource,
   *   null is returned instead. 
   */
  public static <R extends HasId<Id<R>>> List<R> load(Collection<Id<R>> ids, Repository<R> repo) {
    List<R> resources = Lists.newArrayList();
    for (Id<R> resourceId : ids) {
      resources.add(repo.get(resourceId));
    }
    return resources;
  }
}
