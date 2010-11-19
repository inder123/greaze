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

import com.google.greaze.definition.ContentBodyType;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.webservice.ResponseBodySpec;
import com.google.greaze.definition.webservice.ResponseSpec;

import java.lang.reflect.Type;

/**
 * Specification for a {@link RestResponse}.
 * 
 * @author Inderjeet Singh
 */
public final class RestResponseSpec extends ResponseSpec {
  private final Type resourceType;

  public RestResponseSpec(HeaderMapSpec headersSpec, Type resourceType) {
    super(headersSpec, buildBodySpec());
    this.resourceType = resourceType;
  }

  private static ResponseBodySpec buildBodySpec() {
    return new ResponseBodySpec.Builder(ContentBodyType.SIMPLE).build();
  }

  public Type getResourceType() {
    return resourceType;
  }
  
  @Override
  public String toString() {
    return String.format("{resourceType:%s}", resourceType);
  }
}
