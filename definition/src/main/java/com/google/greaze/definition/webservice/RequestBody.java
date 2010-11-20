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

import com.google.greaze.definition.ContentBody;
import com.google.greaze.definition.TypedKey;
import com.google.greaze.definition.UntypedKey;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Definition of the request body of a {@link WebServiceCall}. The request body is what is sent out
 * in the output stream of the request (for example, with 
 * {@link java.net.HttpURLConnection#getOutputStream()}) , and is read by the 
 * javax.servlet.http.HttpServletRequest#getInputStream().
 * This class omits the default constructor for use by Gson. Instead the user must use
 * {@link RequestBody.GsonTypeAdapter}
 * 
 * @author Inderjeet Singh
 */
public final class RequestBody extends ContentBody {

  public static class Builder extends ContentBody.Builder {    
    
    private Object simpleBody;
    private List<Object> listBody = new ArrayList<Object>();

    public Builder(RequestBodySpec spec) {
      super(spec);
    }

    @Override
    public RequestBodySpec getSpec() {
      return (RequestBodySpec) spec;
    }

    @Override
    public Builder setSimpleBody(Object body) {
      return (Builder) super.setSimpleBody(body);
    }

    @Override
    public Builder addToListBody(Object element) {
      return (Builder) super.addToListBody(element);
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
    public RequestBody build() {
      return new RequestBody(getSpec(), simpleBody, listBody, contents);
    }    
  }

  private RequestBody(RequestBodySpec spec, Object simpleBody,
      List<Object> listBody, Map<String, Object> mapBody) {
    super(spec, simpleBody, listBody, mapBody);
  }
  
  @Override
  public RequestBodySpec getSpec() {
    return (RequestBodySpec) spec;
  }  

  /**
   * Gson type adapter for {@link RequestBody}. 
   */
  public static final class GsonTypeAdapter implements JsonSerializer<RequestBody>, 
    JsonDeserializer<RequestBody> {

    private final RequestBodySpec spec;

    public GsonTypeAdapter(RequestBodySpec spec) {
      this.spec = spec;
    }
    
    @Override
    public JsonElement serialize(RequestBody src, Type typeOfSrc, 
        JsonSerializationContext context) {
      return GsonHelper.serialize(src, typeOfSrc, context);
    }

    @Override
    public RequestBody deserialize(JsonElement json, Type typeOfT, 
        JsonDeserializationContext context) throws JsonParseException {
      return GsonHelper.deserialize(json, typeOfT, context, new RequestBody.Builder(spec));
    }
  }
}
