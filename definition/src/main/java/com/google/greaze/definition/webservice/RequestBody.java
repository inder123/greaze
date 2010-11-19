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
import com.google.greaze.definition.ParamMap;
import com.google.greaze.definition.TypedKey;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

  public static class Builder extends ParamMap.Builder<RequestBodySpec> {    
    
    private Object simpleBody;
    private List<Object> listBody = new ArrayList<Object>();

    public Builder(RequestBodySpec spec) {
      super(spec);
    }

    public Builder setSimpleBody(Object body) {
      this.simpleBody = body;
      return this;
    }

    public Builder addToListBody(Object element) {
      this.listBody.add(element);
      return this;
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

    public RequestBody build() {
      return new RequestBody(spec, simpleBody, listBody, contents);
    }    
  }

  private final Object simpleBody;
  private final List<Object> listBody;

  private RequestBody(RequestBodySpec spec, Object simpleBody,
      List<Object> listBody, Map<String, Object> contents) {
    super(spec, contents);
    this.simpleBody = simpleBody;
    this.listBody = listBody;
  }
  
  @Override
  public RequestBodySpec getSpec() {
    return (RequestBodySpec) spec;
  }  

  public Object getSimpleBody() {
    return simpleBody;
  }

  public Object getBodyAsList() {
    return listBody;
  }

  /**
   * Gson type adapter for {@link RequestBody}. 
   */
  public static final class GsonTypeAdapter implements JsonSerializer<RequestBody>, 
    JsonDeserializer<RequestBody>, InstanceCreator<RequestBody> {

    private final RequestBodySpec spec;

    public GsonTypeAdapter(RequestBodySpec spec) {
      this.spec = spec;
    }
    
    @Override
    public JsonElement serialize(RequestBody src, Type typeOfSrc, 
        JsonSerializationContext context) {
      switch(spec.getContentBodyType()) {
        case SIMPLE:
          return context.serialize(src.getSimpleBody());
        case LIST:
          JsonArray array = new JsonArray();
          for(Object entry : src.listBody) {
            array.add(context.serialize(entry, spec.getSimpleBodyType()));
          }
          return array;
        case MAP:
          JsonObject map = new JsonObject();
          for(Map.Entry<String, Object> entry : src.entrySet()) {
            String key = entry.getKey();
            Type entryType = src.getSpec().getTypeFor(key);
            JsonElement value = context.serialize(entry.getValue(), entryType);
            map.add(key, value);        
          }
          return map;
        default:
          throw new UnsupportedOperationException();
      }
    }

    @Override
    public RequestBody deserialize(JsonElement json, Type typeOfT, 
        JsonDeserializationContext context) throws JsonParseException {
      RequestBody.Builder builder = new RequestBody.Builder(spec);
      switch(spec.getContentBodyType()) {
        case SIMPLE:
          builder.setSimpleBody(
            context.deserialize(json, spec.getSimpleBodyType()));
          break;
        case LIST:
          for (JsonElement element : json.getAsJsonArray()) {
            builder.addToListBody(
              context.<Object>deserialize(element, spec.getSimpleBodyType()));
          }
          break;
        case MAP:
          for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
            String key = entry.getKey();
            Type entryType = spec.getTypeFor(key);
            Object value = context.deserialize(entry.getValue(), entryType);
            builder.put(key, value);
          }
          break;
        default:
          throw new UnsupportedOperationException();
      }
      return builder.build();
    }

    @Override
    public RequestBody createInstance(Type type) {
      return new RequestBody.Builder(spec).build();
    }
  }
}
