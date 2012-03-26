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

import com.google.greaze.definition.internal.utils.GreazePreconditions;

/**
 * A factory to create {@link Id}s
 *
 * @author inder
 *
 * @param <I>
 */
public class ResourceIdFactory<I extends ResourceId> {

  public ResourceIdFactory(Class<? super I> classOfI) {
    GreazePreconditions.checkArgument(classOfI.isAssignableFrom(Id.class));
  }

  @SuppressWarnings("unchecked")
  public I createId(String value) {
    return (I)Id.get(value);
  }
}
