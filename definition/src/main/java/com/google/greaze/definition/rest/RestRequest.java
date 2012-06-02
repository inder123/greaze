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

import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.UrlParams;

import java.lang.reflect.Type;

/**
 * The data associated with a Web service request. This includes HTTP request
 * header parameters (form and URL parameters), and request body.
 * 
 * @author Inderjeet Singh
 */
public final class RestRequest<R extends RestResource<R>> extends RestRequestBase<Id<R>, R> {

  public RestRequest(HttpMethod method, HeaderMap requestHeaders, UrlParams urlParams,
      Id<R> resourceId, R requestBody, Type resourceType, boolean inlined) {
    super(method, requestHeaders, urlParams, resourceId, requestBody, resourceType, inlined);
  }
}
