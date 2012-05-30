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
package com.google.greaze.definition;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Body of a request or response. The body contains a map of name-value pairs.
 * There is a {@link ContentBodySpec} associated with the body as well and only the name-value
 * pairs consistent with the specification are permitted.
 *
 * @author Inderjeet Singh
 */
public class ContentBody extends ParamMap {

  public static class Builder extends ParamMap.Builder<ContentBodySpec> {
    protected Object simpleBody;
    protected List<Object> listBody = new ArrayList<Object>();

    public Builder(ContentBodySpec spec) {
      super(spec);
    }

    public ContentBodySpec getSpec() {
      return spec;
    }

    public Builder setSimpleBody(Object body) {
      this.simpleBody = body;
      return this;
    }

    public Builder setListBody(List<Object> list) {
      this.listBody = list;
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

    public ContentBody build() {
      return new ContentBody(spec, simpleBody, listBody, contents);
    }
  }

  private final Object simpleBody;
  private final List<Object> listBody;

  public ContentBody(ContentBodySpec spec, Object simpleBody,
      List<Object> listBody, Map<String, Object> mapBody) {
    super(spec, mapBody);
    this.simpleBody = simpleBody;
    this.listBody = listBody;
  }

  @Override
  public ContentBodySpec getSpec() {
    return (ContentBodySpec) spec;
  }

  public String getContentType() {
    return getSpec().getContentType();
  }

  public Object getSimpleBody() {
    return simpleBody;
  }

  public List<Object> getListBody() {
    return listBody;
  }

  public Map<String, Object> getMapBody() {
    return contents;
  }

  public String getCharacterEncoding() {
    return getSpec().getCharacterEncoding();
  }
}
