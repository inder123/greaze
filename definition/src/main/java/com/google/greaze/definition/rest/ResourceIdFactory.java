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
 * A factory to create {@link Id}s
 *
 * @author inder
 *
 * @param <I>
 */
public class ResourceIdFactory<I extends ResourceId> {
  private final Class<? super I> classOfI;
  private final Type typeOfId;

  public ResourceIdFactory(Class<? super I> classOfI, Type typeOfId) {
    this.classOfI = classOfI;
    this.typeOfId = typeOfId;
  }

  @SuppressWarnings("unchecked")
  public I createId(String value) {
    if (classOfI.isAssignableFrom(Id.class)) {
      return (I)Id.get(value, typeOfId);
    } 
    throw new UnsupportedOperationException();
  }
}
