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

import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.UrlParamsSpec;
import com.google.greaze.definition.webservice.RequestBodySpec;
import com.google.greaze.definition.webservice.RequestSpec;

import java.lang.reflect.Type;

/**
 * Specification for a {@link RestRequestBase}.
 *
 * @author Inderjeet Singh
 */
public final class RestRequestSpec extends RequestSpec {
  private final Type resourceType;

  public RestRequestSpec(HeaderMapSpec headersSpec, Type resourceType) {
    this(headersSpec, buildUrlParamSpec(), buildBodySpec(resourceType), resourceType);
  }

  public RestRequestSpec(HeaderMapSpec headersSpec, UrlParamsSpec paramsSpec,
      RequestBodySpec bodySpec, Type resourceType) {
    super(headersSpec, paramsSpec, bodySpec);
    this.resourceType = resourceType;
  }

  private static UrlParamsSpec buildUrlParamSpec() {
    return new UrlParamsSpec.Builder().build();
  }

  private static RequestBodySpec buildBodySpec(Type resourceType) {
    return new RequestBodySpec.Builder().setSimpleBody(resourceType).build();
  }

  public Type getResourceType() {
    return resourceType;
  }

  @Override
  public String toString() {
    return "resourceType:" + resourceType;
  }
}
