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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.greaze.definition.TypedKey;
import com.google.greaze.definition.rest.ResourceId;
import com.google.greaze.definition.rest.MetaDataBase;
import com.google.greaze.definition.rest.RestResourceBase;

/**
 * A map of resources to their MetaData. If you create a subclass of this class, you should
 * override {@link #createMetaData()} to ensure that the metadata objects are created for
 * the subclass type.
 *
 * @author inder
 *
 * @param <R> the rest resource for which the metadata is being stored
 */
public class MetaDataMap<I extends ResourceId, R extends RestResourceBase<I, R>> {
  protected final Map<I, MetaDataBase<I, R>> map;

  public MetaDataMap() {
    this.map = new HashMap<I, MetaDataBase<I, R>>();
  }

  public MetaDataBase<I, R> get(I resourceId) {
    MetaDataBase<I, R> metaData = map.get(resourceId);
    if (metaData == null) {
      metaData = createMetaData();
      map.put(resourceId, metaData);
    }
    return metaData;
  }

  @Override
  public String toString() {
    return String.format("%s", map);
  }

  public <T> List<I> findByTypedKey(TypedKey<T> key, T value, int maxCount) {
    List<I> result = new ArrayList<I>();
    for (Map.Entry<I, MetaDataBase<I, R>> entry : map.entrySet()) {
      T retrieved = entry.getValue().getFromTransient(key);
      if (isEqual(value, retrieved)) {
        result.add(entry.getKey());
        if (--maxCount == 0) {
          break;
        }
      }
    }
    return result;
  }

  public static <T> boolean isEqual(T t1, T t2) {
    if (t1 == null && t2 == null) {
      return true;
    }
    if (t1 == null || t2 == null) {
      return false;
    }
    return t1.equals(t2);
  }

  /**
   * Override this method in subclasses to ensure that the metadata is created for the subclass
   * type
   */
  protected MetaDataBase<I, R> createMetaData() {
    return MetaDataBase.create();
  }
}
