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
import com.google.gson.reflect.TypeToken;

/**
 * {@link com.google.gson.TypeAdapterFactory} for {@link ResponseBody}.
 *
 * @author Inderjeet Singh
 */
public final class ResponseBodyGsonTypeAdapterFactory
    extends GsonAdapterFactoryBase<ResponseBody, ResponseBodySpec> {

  public ResponseBodyGsonTypeAdapterFactory(ResponseBodySpec spec) {
    super(spec, TypeToken.get(ResponseBody.class));
  }

  public ContentBody.Builder createBuilder() {
    return new ResponseBody.Builder(spec);
  }
}
