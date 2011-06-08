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

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.TypedKey;
import com.google.greaze.definition.internal.utils.GreazePreconditions;
import com.google.greaze.definition.webservice.WebServiceCallSpec;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Specification for a REST service
 *
 * @author inder
 */
public final class RestCallSpec extends WebServiceCallSpec {
  public static class Builder {
    private final CallPath callPath;
    private final Set<HttpMethod> supportedHttpMethods = new LinkedHashSet<HttpMethod>();
    private final HeaderMapSpec.Builder reqParamsSpecBuilder = new HeaderMapSpec.Builder();
    private final HeaderMapSpec.Builder resParamsSpecBuilder = new HeaderMapSpec.Builder();
    private WebContextSpec webContextSpec;
    private final Type resourceType;
    private double version;
    
    public Builder(CallPath callPath, Type resourceType) {
      this.callPath = callPath;
      supportedHttpMethods.addAll(HttpMethod.ALL_METHODS);
      this.resourceType = resourceType;
      this.version = -1D; 
    }

    public Builder disableHttpMethod(HttpMethod httpMethod) {
      supportedHttpMethods.remove(httpMethod);
      return this;
    }

    public Builder setVersion(double version) {
      this.version = version;
      return this;
    }

    public <T> Builder addRequestParam(TypedKey<T> param) {
      reqParamsSpecBuilder.put(param.getName(), param.getTypeOfT());
      return this;
    }

    public <T> Builder addResponseParam(TypedKey<T> param) {
      resParamsSpecBuilder.put(param.getName(), param.getTypeOfT());
      return this;
    }

    public Builder setWebContextSpec(WebContextSpec webContextSpec) {
      GreazePreconditions.checkNull(this.webContextSpec);
      this.webContextSpec = webContextSpec;
      if (webContextSpec != null) {
        for (Entry<String,Type> entry : webContextSpec.getRequestHeaderSpec().entrySet()) {
          reqParamsSpecBuilder.put(entry.getKey(), entry.getValue());
        }
      }
      return this;
    }

    public RestCallSpec build() {
      if (supportedHttpMethods.isEmpty()) {
        supportedHttpMethods.addAll(Arrays.asList(HttpMethod.values()));
      }
      RestRequestSpec requestSpec = 
        new RestRequestSpec(reqParamsSpecBuilder.build(), resourceType);
      RestResponseSpec responseSpec =
        new RestResponseSpec(resParamsSpecBuilder.build(), resourceType);
      return new RestCallSpec(supportedHttpMethods, callPath, 
          requestSpec, webContextSpec, responseSpec, resourceType, version);
    }
  }

  private final Type resourceType;
  private final WebContextSpec webContextSpec;

  private RestCallSpec(Set<HttpMethod> supportedHttpMethods, CallPath path,
      RestRequestSpec requestSpec, WebContextSpec webContextSpec, RestResponseSpec responseSpec,
      Type resourceType, double version) {
    super(supportedHttpMethods, path, requestSpec, responseSpec, version);
    this.webContextSpec = webContextSpec;
    this.resourceType = resourceType;
  }

  @Override
  public RestResponseSpec getResponseSpec() {
    return (RestResponseSpec) responseSpec;
  }

  @Override
  public RestRequestSpec getRequestSpec() {
    return (RestRequestSpec) requestSpec;
  }

  public WebContextSpec getWebContextSpec() {
    return webContextSpec;
  }

  public Type getResourceType() {
    return resourceType;
  }

  @Override
  public String toString() {
    return String.format("resourceType: %s, %s", path, super.toString());
  }

  public RestCallSpec createCopy(CallPath callPath) {
    return new RestCallSpec(supportedHttpMethods, callPath, getRequestSpec(), webContextSpec,
        getResponseSpec(), resourceType, version);
  }
}
