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
package com.google.greaze.definition;

import com.google.greaze.definition.internal.utils.FieldNavigator;
import com.google.greaze.definition.internal.utils.GreazePreconditions;

import java.lang.reflect.Field;

/**
 * URL parameters
 *
 * @author Inderjeet Singh
 */
public class UrlParams {

  public static class Builder {
    private UrlParamsSpec spec;
    private HeaderMap.Builder map;
    private Object params;

    public Builder(UrlParamsSpec spec) {
      this(spec, null);
    }

    public Builder(UrlParamsSpec spec, Object params) {
      GreazePreconditions.checkNotNull(spec);
      this.spec = spec;
      map = new HeaderMap.Builder(spec.getMapSpec());
      this.params = params;
    }

    public Builder put(String name, Object value) {
      map.put(name, value);
      return this;
    }
    public UrlParams build() {
      return new UrlParams(spec, params, map.build());
    }
  }

  private final UrlParamsSpec spec;
  private final Object params;
  private final HeaderMap map;

  private UrlParams(UrlParamsSpec spec, Object params, HeaderMap map) {
    this.spec = spec;
    this.params = params;
    this.map = map;
  }
  
  public UrlParamsSpec getSpec() {
    return spec;
  }

  public Object getParamsObject() {
    return params;
  }

  public HeaderMap getParamsMap() {
    return map;
  }

  protected void populateQueryFieldsInUrlParams(HeaderMap.Builder urlParams) {
    FieldNavigator navigator = new FieldNavigator(spec.getType());
    for (Field f : navigator.getFields()) {
      try {
        urlParams.put(f.getName(), f.get(params));
      } catch (IllegalArgumentException e) {
        throw new WebServiceSystemException(e);
      } catch (IllegalAccessException e) {
        throw new WebServiceSystemException(e);
      }
    }
  }
}
