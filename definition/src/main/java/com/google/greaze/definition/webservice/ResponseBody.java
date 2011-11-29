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
import java.util.List;
import java.util.Map;

import com.google.greaze.definition.ContentBody;
import com.google.greaze.definition.TypedKey;
import com.google.greaze.definition.UntypedKey;
import com.google.gson.reflect.TypeToken;

/**
 * body of the response. This is written out as JSON to be sent out to the client. 
 * This class omits the default constructor for use by Gson. Instead the user must use
 * {@link ResponseBody.GsonTypeAdapter}
 *
 * @author Inderjeet Singh
 */
public final class ResponseBody extends ContentBody {

  public static class Builder extends ContentBody.Builder {    

    public Builder(ResponseBodySpec spec) {
      super(spec);
    }

    @Override
    public ResponseBodySpec getSpec() {
      return (ResponseBodySpec) spec;
    }

    @Override
    public Builder setSimpleBody(Object body) {
      return (Builder) super.setSimpleBody(body);
    }

    @Override
    public Builder setListBody(List<Object> list) {
      return (Builder) super.setListBody(list);
    }

    @Override
    public Builder put(String paramName, Object content) {
      return (Builder) super.put(paramName, content);
    }

    @Override
    public Builder put(String paramName, Object content, Type typeOfContent) {
      return (Builder) super.put(paramName, content, typeOfContent);
    }

    @Override
    public <T> Builder put(TypedKey<T> paramKey, T param) {
      return (Builder) super.put(paramKey, param);
    }

    @Override
    public Builder put(UntypedKey paramKey, Object param) {
      return (Builder) super.put(paramKey, param);
    }

    @Override
    public ResponseBody build() {
      return new ResponseBody(getSpec(), simpleBody, listBody, contents);
    }    
  }

  private ResponseBody(ResponseBodySpec spec, Object simpleBody,
      List<Object> listBody, Map<String, Object> mapBody) {
    super(spec, simpleBody, listBody, mapBody);
  }
  
  @Override
  public ResponseBodySpec getSpec() {
    return (ResponseBodySpec) spec;
  }

  public static final class GsonTypeAdapterFactory extends GsonAdapterFactoryBase<ResponseBody, ResponseBodySpec> {
    public GsonTypeAdapterFactory(ResponseBodySpec spec) {
      super(spec, TypeToken.get(ResponseBody.class));
    }
    public ContentBody.Builder createBuilder() {
      return new ResponseBody.Builder(spec);
    }
  }
}
