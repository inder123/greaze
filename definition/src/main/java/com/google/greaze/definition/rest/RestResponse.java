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
import com.google.greaze.definition.TypedKey;

/**
 * The data associated with a REST Web service response. This includes http response header
 * parameters, and the response body. 
 * 
 * @author inder
 */
public class RestResponse<R extends RestResource<R>> extends RestResponseBase<Id<R>, R> {
  
  public static class Builder<RS extends RestResource<RS>> {
    private final HeaderMap.Builder headers;
    private RS body;
    private final RestResponseSpec spec;
    
    public Builder(RestResponseSpec spec) {
      this.spec = spec;
      headers = new HeaderMap.Builder(spec.getHeadersSpec());
    }
    
    public <T> Builder<RS> putHeader(TypedKey<T> paramName, T content) {
      headers.put(paramName.getName(), content, paramName.getClassOfT());
      return this;
    }
    
    public Builder<RS> setBody(RS body) {
      this.body = body;
      return this;
    }

    public RestResponse<RS> build() {
      return new RestResponse<RS>(spec, headers.build(), body);
    }
  }
  
  public RestResponse(RestResponseSpec spec, HeaderMap headers, R body) {
    super(spec, headers, body);
  }
}
