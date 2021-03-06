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

import java.lang.reflect.Type;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.rest.Id;
import com.google.greaze.definition.rest.ResourceDepot;
import com.google.greaze.definition.rest.RestCallSpec;
import com.google.greaze.definition.rest.RestResource;
import com.google.greaze.definition.rest.WebContextSpec;
import com.google.gson.GsonBuilder;

/**
 * A client class to access a rest resource
 *
 * @author Inderjeet Singh
 */
public class ResourceDepotClient<R extends RestResource<R>>
    extends ResourceDepotBaseClient<Id<R>, R>
    implements ResourceDepot<R> {

  /**
   * @param stub stub containing server info to access the rest client
   * @param callPath relative path to the resource
   * @param resourceType Class for the resource. Such as Cart.class
   * @param inlined set this to true to indicate that the request and response headers should be
   *   sent in the body itself.
   */
  public ResourceDepotClient(RestClientStub stub, CallPath callPath,
      Type resourceType, GsonBuilder gsonBuilder, boolean inlined) {
    super(stub, callPath, resourceType, null, gsonBuilder, inlined);
  }

  public ResourceDepotClient(RestClientStub stub, CallPath callPath,
      Type resourceType, WebContextSpec webContextSpec, GsonBuilder gsonBuilder, boolean inlined) {
    super(stub, callPath, resourceType, webContextSpec, gsonBuilder, inlined);
  }

  public ResourceDepotClient(RestClientStub stub, Type resourceType,
      RestCallSpec callSpec, GsonBuilder gsonBuilder, boolean inlined) {
    super(stub, resourceType, callSpec, gsonBuilder, inlined);
  }
}
