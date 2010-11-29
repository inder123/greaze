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
package com.google.greaze.rest.query.client;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.rest.RestResource;
import com.google.greaze.definition.rest.Id;
import com.google.greaze.definition.rest.query.ResourceQueryBase;
import com.google.greaze.definition.rest.query.ResourceQueryParams;
import com.google.greaze.definition.webservice.WebServiceCallSpec;
import com.google.greaze.webservice.client.WebServiceClient;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * A client to invoke {@link ResourceQueryBase}s associated with a REST resource
 * 
 * @author Inderjeet Singh
 *
 * @param <R> type of the REST resource
 * @param <Q> Query parameters
 */
public class ResourceQueryClient<R extends RestResource<R>, Q extends ResourceQueryParams>
    extends ResourceQueryBaseClient<Id<R>, R, Q> {

  /**
   * @param stub stub containing server info to access the rest client
   * @param callPath relative path to the resource
   */
  public ResourceQueryClient(WebServiceClient stub, CallPath callPath,
      Type queryType, GsonBuilder gsonBuilder, Type resourceType) {
    super(stub, callPath, queryType, gsonBuilder, resourceType);
  }

  protected ResourceQueryClient(WebServiceClient stub, WebServiceCallSpec callSpec,
      Type queryType, GsonBuilder gsonBuilder, Type resourceType) {
    super(stub, callSpec, queryType, gsonBuilder, resourceType);
  }
}
