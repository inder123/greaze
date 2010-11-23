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

import java.lang.reflect.Type;

import com.google.greaze.definition.ContentBodyType;
import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.TypedKey;
import com.google.greaze.definition.webservice.ResponseBody;
import com.google.greaze.definition.webservice.ResponseBodySpec;
import com.google.greaze.definition.webservice.WebServiceResponse;

/**
 * The data associated with a REST Web service response. This includes http response header
 * parameters, and the response body. 
 * 
 * @author inder
 */
public class RestResponseBase<I extends ResourceId, R extends RestResourceBase<I, R>>
    extends WebServiceResponse {
  
  private final R resource;
  
  public static class Builder<II extends ResourceId, RS extends RestResourceBase<II, RS>> {
    private final HeaderMap.Builder headers;
    private RS body;
    private final RestResponseSpec spec;
    
    public Builder(RestResponseSpec spec) {
      this.spec = spec;
      headers = new HeaderMap.Builder(spec.getHeadersSpec());
    }
    
    public <T> Builder<II, RS> putHeader(TypedKey<T> paramName, T content) {
      headers.put(paramName.getName(), content, paramName.getClassOfT());
      return this;
    }
    
    public Builder<II, RS> setBody(RS body) {
      this.body = body;
      return this;
    }

    public RestResponseBase<II, RS> build() {
      return new RestResponseBase<II, RS>(spec, headers.build(), body);
    }
  }
  
  public RestResponseBase(RestResponseSpec spec, HeaderMap responseHeaders, R resource) {
    super(spec, responseHeaders, createBody(resource, spec.getResourceType()));
    this.resource = resource;
  }
  
  private static<R> ResponseBody createBody(R resource, Type resourceType) {
    ResponseBodySpec spec = new ResponseBodySpec(ContentBodyType.SIMPLE, null, resourceType);
    return new ResponseBody.Builder(spec)
        .setSimpleBody(resource)
        .build();
  }

  public RestResponseSpec getSpec() {
    return (RestResponseSpec) super.getSpec();
  }

  public R getResource() {
    return resource;
  }
}