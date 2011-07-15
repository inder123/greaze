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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * Utility methods for operating on Types
 *
 * @author Inderjeet Singh
 */
public final class GreazeTypeUtils {
  @SuppressWarnings("unchecked")
  public static String getSimpleTypeName(Type type) {
    if (type == null) {
      return "null";
    }
    if (type instanceof Class) {
      return ((Class)type).getSimpleName();
    } else if (type instanceof ParameterizedType) {
      ParameterizedType pType = (ParameterizedType) type;
      StringBuilder sb = new StringBuilder(getSimpleTypeName(pType.getRawType()));
      sb.append('<');
      boolean first = true;
      for (Type argumentType : pType.getActualTypeArguments()) {
        if (first) {
          first = false;
        } else {
          sb.append(',');
        }
        sb.append(getSimpleTypeName(argumentType));
      }
      sb.append('>');
      return sb.toString();
    } else if (type instanceof WildcardType) {
      return "?";
    }
    return type.toString();
  }

  @SuppressWarnings("unchecked")
  public static String getFullTypeName(Type type) {
    if (type == null) {
      return "null";
    }
    if (type instanceof Class) {
      return ((Class)type).getName();
    } else if (type instanceof ParameterizedType) {
      ParameterizedType pType = (ParameterizedType) type;
      StringBuilder sb = new StringBuilder(getFullTypeName(pType.getRawType()));
      sb.append('<');
      boolean first = true;
      for (Type argumentType : pType.getActualTypeArguments()) {
        if (first) {
          first = false;
        } else {
          sb.append(',');
        }
        sb.append(getFullTypeName(argumentType));
      }
      sb.append('>');
      return sb.toString();
    } else if (type instanceof WildcardType) {
      return "?";
    }
    return type.toString();
  }
  private GreazeTypeUtils() {}
}
