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
package com.google.greaze.definition.rest.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.greaze.definition.rest.HasId;
import com.google.greaze.definition.rest.Id;

/**
 * Utility methods for {@link Id}
 *
 * @author Inderjeet Singh
 */
public final class IdUtils {
  public static <R extends HasId<Id<R>>> List<Id<R>> toIdList(Collection<R> resources) {
    List<Id<R>> idList = new ArrayList<Id<R>>();
    for (R resource : resources) {
      idList.add(resource.getId());
    }
    return idList;
  }

  public static <R extends HasId<Id<R>>> Set<Id<R>> toIdSet(Collection<R> resources) {
    Set<Id<R>> idList = new TreeSet<Id<R>>();
    for (R resource : resources) {
      idList.add(resource.getId());
    }
    return idList;
  }

  private IdUtils() {}
}
