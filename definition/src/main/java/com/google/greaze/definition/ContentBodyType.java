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

/**
 * Enum for describing what is contained in a {@link ContentBody}
 *
 * @author Inderjeet Singh
 */
public enum ContentBodyType {
  /** Body is a single element. For example, a REST resource */
  SIMPLE,

  /**
   * Body is a list of multiple elements of the same type.
   * For example a REST query response
   */
  LIST,

  /**
   * Body consists of multiple elements put together in a map of name-value
   * pairs. For example, an arbitrary web-service
   */
  MAP;
}
