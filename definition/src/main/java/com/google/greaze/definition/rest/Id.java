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

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.regex.Pattern;

import com.google.greaze.definition.internal.utils.GreazePreconditions;

/**
 * An id for a rest resource
 *
 * @author inder
 *
 * @param <R> type variable for the rest resource
 */
public final class Id<R> implements ResourceId, Comparable<Id<R>>, Serializable {

  private static final long serialVersionUID = -8713010965374609900L;

  private static Pattern ID_PATTERN = Pattern.compile("[a-zA-Z0-9_\\.\\-]+"); 

  private String value;

  private Id(String value) {
    // Assert that Id does not have any of the banned characters
    GreazePreconditions.checkArgument(Id.isValidValue(value));
    this.value = value;
  }

  @Override
  public String getValue() {
    return value;
  }

  public static <T> Id<T> getNullValue(Class<T> idClass) {
    return Id.get(null, idClass);
  }

  public static <T> Id<T> getNullValue(Type idType) {
    return Id.get(null, idType);
  }

  public static String getValue(Id<?> id) {
    return id == null ? null : id.getValue();
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  private static boolean isValidValue(String value) {
    return value == null || ID_PATTERN.matcher(value).matches();
  }

  public static boolean isValid(Id<?> id) {
    return id != null && id.value != null;
  }

  public String getShortValue() {
    int length = value.length();
    return length > 4 ? value.substring(length - 4) : value;
  }

  public static String getShortValue(Id<?> id) {
    return isValid(id) ? id.getShortValue() : "null";
  }

  /**
   * A more efficient comparison method for ids that take into account of ids being nullable.
   * Since the method is parameterized and both ids are of the same type, this method compares
   * only id values, not their types. Note that this shortcut doesn't work if you pass raw ids
   * to this method
   */
  public static <T> boolean equals(/* @Nullable */ Id<T> id1,
      /* @Nullable */ Id<T> id2) {
    if ((id1 == null && id2 != null) || (id1 != null && id2 == null)) {
      return false;
    }
    if (id1 == null && id2 == null) {
      return true;
    }
    return id1.value == id2.value || (id1.value != null && id1.value.equals(id2.value));
  }

  @Override
  public boolean equalsByValue(ResourceId other) {
    if (this == other) return true;
    if (other == null) return false;
    String otherValue = other.getValue();
    return value == otherValue || value.equals(otherValue);
  }

  @Override  
  public boolean equals(Object obj) {
    // TODO: Use instanceOf for equality instead of equals (to better support JPA)
    // TODO: get rid of equalsByValue and change the semantics for equality to be with value only.
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    @SuppressWarnings("unchecked")
    Id<R> other = (Id<R>)obj;
    if (!value.equals(other.value)) {
      return false;
    }
    // Shortcut, see if the raw types are the same. This is not exact match semantically, but 
    // paramterized type matching is more pain than worth
    return value.equals(other.value) && this.getClass().isInstance(obj);
//    if (typeOfId == null) {
//      if (other.typeOfId != null) return false;
//    } else if (!equivalentTypes(typeOfId, other.typeOfId)) return false;
//    return true;
  }

  /**
   * Returns true for equivalentTypes(Class<?>, Class)
   * Visible for testing only 
   */
  @SuppressWarnings("rawtypes")
  static boolean equivalentTypes(Type type1, Type type2) {
    if (type1 instanceof ParameterizedType && type2 instanceof Class) {
      return areEquivalentTypes((ParameterizedType)type1, (Class)type2);
    } else if (type2 instanceof ParameterizedType && type1 instanceof Class) {
      return areEquivalentTypes((ParameterizedType)type2, (Class)type1);
    }
    return type1.equals(type2);
  }

  /**
   * Visible for testing only
   */
  @SuppressWarnings("rawtypes")
  static boolean areEquivalentTypes(ParameterizedType type, Class clazz) {
    Class rawClass = (Class) type.getRawType();
    if (!clazz.equals(rawClass)) {
      return false;
    }
    for (Type typeVariable : type.getActualTypeArguments()) {
      if (typeVariable instanceof WildcardType) {
        continue;
      }
      // This is a real parameterized type, not just ?
      return false;
    }
    return true;
  }

  public static <RS> Id<RS> get(String value) {
    return new Id<RS>(value);
  }

  public static <RS> Id<RS> get(String value, Type typeOfId) {
    return new Id<RS>(value);
  }

  @Override
  public String toString() {
    return getShortValue(this);
  }

  @Override
  public int compareTo(Id<R> o) {
    GreazePreconditions.checkNotNull(o);
    GreazePreconditions.checkArgument(isValid(this));
    GreazePreconditions.checkArgument(isValid(o));
    return value.compareTo(o.value);
  }
}
