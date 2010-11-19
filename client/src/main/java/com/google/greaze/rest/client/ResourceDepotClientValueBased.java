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
package com.google.greaze.rest.client;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.rest.RestCallSpec;
import com.google.greaze.definition.rest.RestResource;
import com.google.greaze.definition.rest.ValueBasedId;
import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * A client class to access a rest resource
 *
 * @author Inderjeet Singh
 */
public class ResourceDepotClientValueBased<R extends RestResource<ValueBasedId<R>, R>>
    extends ResourceDepotClient<ValueBasedId<R>, R> {

  /**
   * @param stub stub containing server info to access the rest client
   * @param callPath relative path to the resource
   * @param resourceType Class for the resource. Such as Cart.class
   */
  public ResourceDepotClientValueBased(RestClientStub stub, CallPath callPath,
                                       Type resourceType, Gson gson) {
    super(stub, callPath, resourceType, gson);
  }

  protected ResourceDepotClientValueBased(RestClientStub stub, Type resourceType,
                                          RestCallSpec callSpec, Gson gson) {
    super(stub, resourceType, callSpec, gson);
  }
}
