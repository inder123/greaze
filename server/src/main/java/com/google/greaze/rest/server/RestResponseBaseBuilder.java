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
package com.google.greaze.rest.server;

import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.rest.ResourceId;
import com.google.greaze.definition.rest.RestRequestBase;
import com.google.greaze.definition.rest.RestResourceBase;
import com.google.greaze.definition.rest.RestResponseBase;
import com.google.greaze.definition.rest.WebContext;

public class RestResponseBaseBuilder<I extends ResourceId, R extends RestResourceBase<I, R>> {
  protected final RepositoryBase<I, R> resources;

  public RestResponseBaseBuilder(RepositoryBase<I, R> resources) {
    this.resources = resources;
  }

  public void buildResponse(WebContext context, RestRequestBase<I, R> request,
      RestResponseBase.Builder<I, R> responseBuilder) {
    HttpMethod method = request.getMethod();
    R responseBody = null;
    switch (method) {
      case GET:
        responseBody = get(request.getId(), context);
        break;
      case POST:
        responseBody = post(request.getResource(), context);
        break;
      case DELETE:
        delete(request.getId(), context);
        break;
      case PUT:
        responseBody = put(request.getResource(), context);
        break;
      default:
        throw new IllegalStateException("Unexpected method: " + method);
    }
    responseBuilder.setBody(responseBody);
  }

  public R get(I resourceId, WebContext context) {
    return resources.get(resourceId);
  }

  public R post(R resource, WebContext context) {
    return resources.put(resource);
  }

  public void delete(I resourceId, WebContext context) {
    resources.delete(resourceId);
  }

  public R put(R resource, WebContext context) {
    return resources.put(resource);
  }
}
