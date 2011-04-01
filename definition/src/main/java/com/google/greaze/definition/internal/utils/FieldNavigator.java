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
package com.google.greaze.definition.internal.utils;

import com.google.gson.internal.$Types;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility to extract all fields of a class including super-classes
 *
 * @author Inderjeet Singh
 */
public final class FieldNavigator {

  private final Class<?> clazz;

  public FieldNavigator(Type type) {
    this.clazz = $Types.getRawType(type);
  }

  public List<Field> getFields() {
    List<Field> fields = new ArrayList<Field>();
    for (Class<?> curr = clazz; curr != null && !curr.equals(Object.class);
        curr = curr.getSuperclass()) {
      if (!curr.isSynthetic()) {
        navigateClassFields(curr, fields);
      }
    }
    return fields;
  }

  private void navigateClassFields(Class<?> clazz, List<Field> fieldsList) {
    Field[] fields = clazz.getDeclaredFields();
    AccessibleObject.setAccessible(fields, true);
    for (Field f : fields) {
      fieldsList.add(f);
    }
  }
}
