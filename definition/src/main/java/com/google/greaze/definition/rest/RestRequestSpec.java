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
import com.google.greaze.definition.webservice.RequestBodySpec;
import com.google.greaze.definition.webservice.RequestSpec;

import java.lang.reflect.Type;

/**
 * Specification for a {@link RestRequestBase}.
 * 
 * @author inder
 */
public final class RestRequestSpec extends RequestSpec {
  private final Type resourceType;

  public RestRequestSpec(HeaderMapSpec headersSpec, Type resourceType) {
    super(headersSpec, buildUrlParamSpec(), buildBodySpec());
    this.resourceType = resourceType;
  }

  public RestRequestSpec(HeaderMapSpec headersSpec, RequestBodySpec bodySpec,
                         Type resourceType) {
    super(headersSpec, buildUrlParamSpec(), bodySpec);
    this.resourceType = resourceType;
  }

  private static HeaderMapSpec buildUrlParamSpec() {
    return new HeaderMapSpec.Builder().build();
  }

  private static RequestBodySpec buildBodySpec() {
    return new RequestBodySpec.Builder(ContentBodyType.SIMPLE)
      .build();
  }

  public Type getResourceType() {
    return resourceType;
  }
  
  @Override
  public String toString() {
    return String.format("resourceType:%s", resourceType);
  }
}
