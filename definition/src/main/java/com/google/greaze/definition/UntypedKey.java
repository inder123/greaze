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
package com.google.greaze.definition;

import java.lang.reflect.Type;

import com.google.greaze.definition.internal.utils.GreazePreconditions;

/**
 * A typed key for use in a {@link ParamMap} or a {@link ParamMapSpec}.
 *
 * @author Inderjeet Singh
 */
public final class UntypedKey {
  private final String name;
  private final Type typeOfT;

  public UntypedKey(String name, Type classOfT) {
    GreazePreconditions.checkNotNull(name);
    GreazePreconditions.checkNotNull(classOfT);

    this.name = name;
    this.typeOfT = classOfT;
  }

  public String getName() {
    return name;
  }

  public Type getTypeOfT() {
    return typeOfT;
  }

  
  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    UntypedKey other = (UntypedKey) obj;
    return name.equals(other.name) && typeOfT.equals(other.typeOfT);
  }

  @Override
  public String toString() {
    return String.format("{name:%s, typeOfT:%s}", name, typeOfT);
  }
}
