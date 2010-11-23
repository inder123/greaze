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
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

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

    public Builder setSimpleBody(Object body) {
      return (Builder) super.setSimpleBody(body);
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

  /**
   * Gson type adapter for {@link ResponseBody}. 
   */
  public static final class GsonTypeAdapter implements JsonSerializer<ResponseBody>, 
    JsonDeserializer<ResponseBody> {

    private final ResponseBodySpec spec;

    public GsonTypeAdapter(ResponseBodySpec spec) {
      this.spec = spec;
    }
    
    @Override
    public JsonElement serialize(ResponseBody src, Type typeOfSrc, 
        JsonSerializationContext context) {
      return GsonHelper.serialize(src, context);
    }

    @Override
    public ResponseBody deserialize(JsonElement json, Type typeOfT, 
        JsonDeserializationContext context) throws JsonParseException {
      return GsonHelper.deserialize(json, context, new ResponseBody.Builder(spec));
    }
  }
}
