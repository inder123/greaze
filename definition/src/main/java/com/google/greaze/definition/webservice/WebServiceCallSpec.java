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

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.TypedKey;
import com.google.greaze.definition.UntypedKey;
import com.google.greaze.definition.internal.utils.GreazePreconditions;

/**
 * Specification for a Json web service call. The call includes the relative path where the call 
 * is available, the specification of requests, and responses. 
 * 
 * @author Inderjeet Singh
 */
public class WebServiceCallSpec {
  
  public static final WebServiceCallSpec NULL_SPEC = new Builder(new CallPath("")).build();
  
  public static class Builder {
	private final CallPath callPath;
	private final Set<HttpMethod> supportedHttpMethods;
    private final HeaderMapSpec.Builder urlParamsSpecBuilder;
    private final HeaderMapSpec.Builder reqParamsSpecBuilder;
    private final RequestBodySpec.Builder reqBodySpecBuilder;
    private final HeaderMapSpec.Builder resParamsSpecBuilder;
    private final ResponseBodySpec.Builder resBodySpecBuilder;
    private double version;
    
    public Builder(CallPath callPath) {
      this.callPath = callPath;
      supportedHttpMethods = new LinkedHashSet<HttpMethod>();
      urlParamsSpecBuilder = new HeaderMapSpec.Builder();
      reqParamsSpecBuilder = new HeaderMapSpec.Builder();
      resParamsSpecBuilder = new HeaderMapSpec.Builder();
      reqBodySpecBuilder = new RequestBodySpec.Builder();
      resBodySpecBuilder = new ResponseBodySpec.Builder();
      this.version = -1D; 
    }
    
    public Builder setSimpleBody(Type simpleBodyType) {
      reqBodySpecBuilder.setSimpleBody(simpleBodyType);
      resBodySpecBuilder.setSimpleBody(simpleBodyType);
      return this;
    }
    
    public Builder setListBody(Type listElementType) {
      reqBodySpecBuilder.setListBody(listElementType);
      resBodySpecBuilder.setListBody(listElementType);
      return this;
    }
    
    public Builder setMapBody() {
      reqBodySpecBuilder.setMapBody();
      resBodySpecBuilder.setMapBody();
      return this;
    }
    
    public Builder setVersion(double version) {
      this.version = version;
      return this;
    }

    /**
     * If this method is not invoked, then it is assumed that the WebServiceCall supports all
     * methods specified in {@link HttpMethod#values()}.
     * 
     * @param httpMethods list of methods that this call supports.
     * @return self to follow the Builder pattern.
     */
    public Builder supportsHttpMethod(HttpMethod... httpMethods) {
      supportedHttpMethods.addAll(Arrays.asList(httpMethods));
      return this;
    }
    
    public <T> Builder addRequestParam(TypedKey<T> param) {
      reqParamsSpecBuilder.put(param.getName(), param.getClassOfT());
      return this;
    }

    public Builder addUrlParam(TypedKey<String> param) {
      urlParamsSpecBuilder.put(param.getName(), param.getClassOfT());
      return this;
    }

    public Builder addUrlParam(UntypedKey param) {
      urlParamsSpecBuilder.put(param.getName(), param.getTypeOfT());
      return this;
    }

    public <T> Builder addRequestBodyParam(TypedKey<T> param) {
      reqBodySpecBuilder.put(param);
      return this;
    }

    public <T> Builder addResponseParam(TypedKey<T> param) {
      resParamsSpecBuilder.put(param.getName(), param.getClassOfT());
      return this;
    }

    public <T> Builder addResponseBodyParam(TypedKey<T> param) {
      resBodySpecBuilder.put(param);
      return this;
    }

    public <T> Builder addResponseBodyParam(UntypedKey param) {
      resBodySpecBuilder.put(param);
      return this;
    }

    public WebServiceCallSpec build() {      
      if (supportedHttpMethods.isEmpty()) {
        supportedHttpMethods.addAll(Arrays.asList(HttpMethod.values()));
      }
      RequestSpec requestSpec = new RequestSpec(
          reqParamsSpecBuilder.build(), urlParamsSpecBuilder.build(), reqBodySpecBuilder.build());
      ResponseSpec responseSpec = 
        new ResponseSpec(resParamsSpecBuilder.build(), resBodySpecBuilder.build());
      WebServiceCallSpec callSpec = new WebServiceCallSpec(supportedHttpMethods, callPath, 
          requestSpec, responseSpec, version);
      return callSpec;
    }
  }
  
  protected final Set<HttpMethod> supportedHttpMethods;
  protected final CallPath path;
  protected final ResponseSpec responseSpec;
  protected final RequestSpec requestSpec;
  protected final double version;
  
  protected WebServiceCallSpec(Set<HttpMethod> supportedHttpMethods, CallPath path, 
      RequestSpec requestSpec, ResponseSpec responseSpec, double version) {
    GreazePreconditions.checkArgument(!supportedHttpMethods.isEmpty());
    GreazePreconditions.checkNotNull(path);
    
    this.supportedHttpMethods = supportedHttpMethods;
    this.path = path;
    this.requestSpec = requestSpec;
    this.responseSpec = responseSpec;
    this.version = version;
  }

  public CallPath getPath() {
    return path;
  }

  public Set<HttpMethod> getSupportedHttpMethods() {
    return supportedHttpMethods;
  }

  public ResponseSpec getResponseSpec() {
    return responseSpec;
  }
  
  public RequestSpec getRequestSpec() {
    return requestSpec;
  }
  
  public double getVersion() {
    return version;
  }

  @Override
  public String toString() {
    return String.format(
      "path: %s, version: %.2f, requestSpec: %s, responseSpec: %s",
      path, version, requestSpec, responseSpec);
  }
}
