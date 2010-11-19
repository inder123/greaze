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
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.greaze.definition.ContentBodySpec;
import com.google.greaze.definition.ContentBodyType;
import com.google.greaze.definition.TypedKey;
import com.google.greaze.definition.UntypedKey;

/**
 * Specification of a {@link ResponseBody}.
 * 
 * @author inder
 */
public final class ResponseBodySpec extends ContentBodySpec {
  
  public static class Builder {
    private final Map<String, Type> paramsSpec = new LinkedHashMap<String, Type>();
    private final ContentBodyType contentBodyType;

    public Builder(ContentBodyType contentBodyType) {
      this.contentBodyType = contentBodyType;
    }

    public <T> Builder put(TypedKey<T> param) {
      paramsSpec.put(param.getName(), param.getClassOfT());
      return this;
    }
    
    public <T> Builder put(UntypedKey param) {
      paramsSpec.put(param.getName(), param.getTypeOfT());
      return this;
    }
    
    public ResponseBodySpec build() {
      ResponseBodySpec spec = new ResponseBodySpec(contentBodyType, paramsSpec);
      return spec;
    }    
  }
  
  public ResponseBodySpec(ContentBodyType contentBodyType, Map<String, Type> paramsSpec) {
    super(contentBodyType, paramsSpec);
  }
}
