/*
 * Copyright (C) 2008 Google Inc.
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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.greaze.definition.internal.utils.$GreazeTypes;

/**
 * Base class for the specification of a {@link ContentBody}.
 * 
 * @author inder
 */
public class ContentBodySpec implements ParamMapSpec {

  public static final String JSON_CONTENT_TYPE = "application/json";
  public static final String JSON_CHARACTER_ENCODING = "utf-8";
  
  private final Map<String, Type> paramsSpec;
  private final ContentBodyType contentBodyType;
  private final Type simpleBodyType;
  private final Type bodyJavaType;

  protected ContentBodySpec(ContentBodyType contentBodyType, Map<String, Type> paramsSpec,
                            Type simpleBodyType) {
    if (paramsSpec == null) {
      paramsSpec = new LinkedHashMap<String, Type>();
    }
    this.paramsSpec = Collections.unmodifiableMap(paramsSpec);
    this.contentBodyType = contentBodyType;
    this.simpleBodyType = simpleBodyType;
    if (contentBodyType == null) {
      this.bodyJavaType = simpleBodyType;
    } else {
      switch (contentBodyType) {
        case SIMPLE:
          this.bodyJavaType = simpleBodyType; 
          break;
        case LIST:
          this.bodyJavaType = $GreazeTypes.newParameterizedTypeWithOwner(
              null, List.class, simpleBodyType);
          break;
        case MAP:
          this.bodyJavaType = $GreazeTypes.newParameterizedTypeWithOwner(
              null, Map.class, String.class, simpleBodyType);
          break;
        default:
          throw new UnsupportedOperationException();
      }
    }
  }
  
  @Override
  public Type getTypeFor(String paramName) {
    return paramsSpec.get(paramName);
  }
  
  @Override
  public boolean checkIfCompatible(String paramName, Type type) {
    return type.equals(getTypeFor(paramName));
  }
  
  @Override
  public boolean checkIfCompatible(String paramName, Object object) {
    return checkIfCompatible(paramName, object.getClass());
  }
  
  @Override
  public Set<Map.Entry<String, Type>> entrySet() {
    return paramsSpec.entrySet();
  }

  public ContentBodyType getContentBodyType() {
    return contentBodyType;
  }

  @Override
  public int size() {
    return paramsSpec.size();
  }
  
  public String getContentType() {
    return JSON_CONTENT_TYPE;
  }

  /**
   * Returns the Java type corresponding to the body. For example, If contentBodyType is
   * {@link ContentBodyType#SIMPLE}, it returns {@link #getSimpleBodyType()}. If contentBodyType
   * is {@link ContentBodyType#LIST}, it returns parameterized type for List of SimpleBodyType.
   * if contentBodyType is {@link ContentBodyType#MAP}, it returns parameterized type for a Map
   * of String keys with SimpleBodyType values.
   */
  public Type getBodyJavaType() {
    return bodyJavaType;
  }

  public Type getSimpleBodyType() {
    return simpleBodyType;
  }
  
  public String getCharacterEncoding() {
    return JSON_CHARACTER_ENCODING;
  }
  
  @Override
  public String toString() {
    return Util.toStringMapKeys(paramsSpec);
  }
}
