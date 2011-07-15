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
package com.google.greaze.definition.webservice;

import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.UrlParamsSpec;
import com.google.greaze.definition.internal.utils.GreazePreconditions;

/**
 * Specification for a {@link WebServiceRequest}.
 * 
 * @author inder
 */
public class RequestSpec {

  private final HeaderMapSpec headersSpec;
  private final UrlParamsSpec urlParamsSpec;
  private final RequestBodySpec bodySpec;
  
  public RequestSpec(HeaderMapSpec headersSpec, UrlParamsSpec urlParamSpec,
      RequestBodySpec bodySpec) {
    GreazePreconditions.checkNotNull(headersSpec);
    GreazePreconditions.checkNotNull(urlParamSpec);
    GreazePreconditions.checkNotNull(bodySpec);
    
    this.headersSpec = headersSpec;
    this.urlParamsSpec = urlParamSpec;
    this.bodySpec = bodySpec;
  }
  
  public HeaderMapSpec getHeadersSpec() {
    return headersSpec;
  }
 
  public UrlParamsSpec getUrlParamsSpec() {
    return urlParamsSpec;
  }
 
  public RequestBodySpec getBodySpec() {
    return bodySpec;
  }
  
  @Override
  public String toString() {
    return String.format("headers:%s, urlParams:%s, body: %s",
        headersSpec, urlParamsSpec, bodySpec);
  }
}
