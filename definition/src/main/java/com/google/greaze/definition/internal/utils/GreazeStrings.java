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

public final class GreazeStrings {
  public static boolean isEmpty(String str) {
    return str == null || "".equals(str.trim());
  }

  private GreazeStrings() {
    // Not instantiable
  }

  /**
   * Returns the index of the first character in the specified string that matches any of the
   * specified characters.
   */
  public static int indexOf(int startingIndex, String str, char... chars) {
    char[] strchars =  str.toCharArray();
    for (int i = startingIndex; i < strchars.length; ++i) {
      char c = strchars[i];
      for (char target : chars) {
        if (target == c) {
          return i;
        }
      }
    }
    return -1;
  }

  public static boolean equals(String str1, String str2) {
    // str1 == str2 takes care of the case when both are null.
    // After the first check, only one of them could be null which would
    // mean that the strings are unequal.
    return str1 == str2 ||
      (str1 != null && str2 != null && str1.equals(str2));  
  }

  public static boolean firstStartsWithSecond(String first, String second) {
    // first == second takes care of the case when both are null.
    // After the first check, only one of them could be null which would
    // mean that the strings are unequal.
    return first == second ||
      (first != null && second != null && first.startsWith(second));  
  }
}
