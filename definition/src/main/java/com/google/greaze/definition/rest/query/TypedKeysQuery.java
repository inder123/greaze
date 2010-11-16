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
package com.google.greaze.definition.rest.query;

import java.lang.reflect.Type;

import com.google.greaze.definition.TypedKey;
import com.google.greaze.definition.UntypedKey;
import com.google.greaze.definition.rest.ID;
import com.google.greaze.definition.rest.RestResource;

/**
 * List of {@link TypedKey}s associated with REST queries
 *
 * @author Inderjeet Singh
 *
 * @param <I> ID type of the REST resource
 * @param <R> The type of the REST resource
 */
public class TypedKeysQuery<I extends ID, R extends RestResource<I, R>> {

  /**
   * This key is used to specify the URL parameter for the queryName
   */
  public static final TypedKey<String> QUERY_NAME = new TypedKey<String>("query", String.class);

  /**
   * This key is used to specify the URL parameter for the queryValue
   */
  public static final TypedKey<String> QUERY_VALUE_AS_JSON = new TypedKey<String>("queryValueAsJson", String.class);

  public static UntypedKey getKeyForResourceList(Type typeOfListOfR) {
    return new UntypedKey("resourceList", typeOfListOfR);
  }
}
