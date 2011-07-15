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
package com.google.greaze.definition.internal.utils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Map of a {@link Type} to its name
 *
 * @author Inderjeet Singh
 */
public final class TypeNameBiMap {
  private static TypeNameBiMap instance;

  public static synchronized TypeNameBiMap getInstance() {
    if (instance == null) {
      instance = new TypeNameBiMap();
    }
    return instance;
  }

  private Map<String, Type> nameToType = new HashMap<String, Type>();
  private Map<Type, String> typeToName = new HashMap<Type, String>();

  private TypeNameBiMap() {}

  public synchronized Type getType(String typeName) {
    return nameToType.get(typeName);
  }

  public synchronized String getTypeName(Type typeOfId) {
    String name = typeToName.get(typeOfId);
    if (name == null) {
      name = GreazeTypeUtils.getFullTypeName(typeOfId);
      nameToType.put(name, typeOfId);
      typeToName.put(typeOfId, name);
    }
    return name;
  }

}
