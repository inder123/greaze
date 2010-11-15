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
package com.google.greaze.definition.rest;

import java.lang.reflect.Type;

/**
 * A factory to create {@link ValueBasedId}s
 *
 * @author inder
 *
 * @param <I>
 */
public class IDFactory<I extends ID> {
  private final Class<? super I> classOfI;
  private final Type typeOfId;

  public IDFactory(Class<? super I> classOfI, Type typeOfId) {
    this.classOfI = classOfI;
    this.typeOfId = typeOfId;
  }

  @SuppressWarnings("unchecked")
  public I createId(long value) {
    if (classOfI.isAssignableFrom(ValueBasedId.class)) {
      return (I)ValueBasedId.get(value, typeOfId);
    } 
    throw new UnsupportedOperationException();
  }
}
