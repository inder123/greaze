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

/**
 * A simple object that holds onto a pair of object references, first and second.
 *
 * @author Inderjeet Singh
 *
 * @param <FIRST>
 * @param <SECOND>
 */
public final class GreazePair<FIRST, SECOND> {

  public final FIRST first;
  public final SECOND second;

  public GreazePair(FIRST first, SECOND second) {
    this.first = first;
    this.second = second;
  }

  public static <F, S> GreazePair<F, S> create(F first, S second) {
    return new GreazePair<F, S>(first, second);
  }

  @Override
  public int hashCode() {
    return 17 * ((first != null) ? first.hashCode() : 0)
        + 17 * ((second != null) ? second.hashCode() : 0);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof GreazePair<?, ?>)) {
      return false;
    }

    GreazePair<?, ?> that = (GreazePair<?, ?>) o;
    return equal(this.first, that.first) && equal(this.second, that.second);
  }

  private static boolean equal(Object a, Object b) {
    return a == b || (a != null && a.equals(b));
  }

  @Override
  public String toString() {
    return String.format("{%s,%s}", first, second);
  }
}
