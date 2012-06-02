/*
 * Copyright (C) 2012 Greaze Authors.
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
package com.google.greaze.server.fixtures;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Creates an {@link Enumeration} backed with an iterator. This is useful to
 * add support to legacy methods that require {@link Enumeration}.
 *
 * @author Inderjeet Singh
 */
public final class Iterator2Enumeration<T> implements Enumeration<T> {
  private final Iterator<T> iterator;

  Iterator2Enumeration(Iterator<T> iterator) {
    this.iterator = iterator;
  }

  @Override
  public boolean hasMoreElements() {
    return iterator.hasNext();
  }

  @Override
  public T nextElement() {
    return iterator.next();
  }
}