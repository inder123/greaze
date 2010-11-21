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
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.greaze.definition.rest.HasId;
import com.google.greaze.definition.rest.ResourceId;
import com.google.greaze.definition.rest.ResourceIdFactory;

/**
 * This class provides a type-safe map to access values associated with Ids
 *
 * @author inder
 *
 * @param <T> the type of the objects being kept in the map
 */
public class IdMapBase<I extends ResourceId, T extends HasId<I>> {
  public static final Logger LOG = Logger.getLogger(IdMapBase.class.getName());
  public static final long ID_START_VALUE = 1L;
  protected final Map<I, T> map;
  private volatile long nextAvailableId;
  private final ResourceIdFactory<I> idFactory;

  /**
   * Use create(Type) instead of constructor
   */
  protected IdMapBase(Class<? super I> classOfI, Type typeOfId) {
    map = new ConcurrentHashMap<I, T>();
    nextAvailableId = ID_START_VALUE;
    this.idFactory = new ResourceIdFactory<I>(classOfI, typeOfId);
  }

  public T get(I id) {
    return map.get(id);
  }

  public T put(T obj) {
    map.put(obj.getId(), obj);
    return obj;
  }

  public void delete(I id) {
    T removed = map.remove(id);
    if (removed == null) {
      LOG.log(Level.WARNING, "Attempted to delete non-existent id: {0}", id);
    }
  }

  public boolean exists(I id) {
    return map.containsKey(id);
  }

  public synchronized I getNextId() {
    long id = nextAvailableId++;
    return idFactory.createId(id);
  }

  public static <II extends ResourceId, S extends HasId<II>> IdMapBase<II, S> create(Class<? super II> classOfII, Type typeOfId) {
    return new IdMapBase<II, S>(classOfII, typeOfId);
  }
}
