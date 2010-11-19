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
import com.google.greaze.definition.rest.RestResourceValueBased;
import com.google.greaze.definition.rest.ValueBasedId;
import com.google.greaze.definition.rest.query.ResourceQuery;
import com.google.greaze.definition.rest.query.ResourceQueryParams;
import com.google.greaze.definition.webservice.WebServiceCallSpec;
import com.google.greaze.webservice.client.WebServiceClient;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * A client to invoke {@link ResourceQuery}s associated with a REST resource
 * 
 * @author Inderjeet Singh
 *
 * @param <R> type of the REST resource
 * @param <Q> Query parameters
 */
public class ResourceQueryClientValueBased<
        R extends RestResourceValueBased<R>, Q extends ResourceQueryParams>
    extends ResourceQueryClient<ValueBasedId<R>, R, Q> {

  /**
   * @param stub stub containing server info to access the rest client
   * @param callPath relative path to the resource
   */
  public ResourceQueryClientValueBased(WebServiceClient stub, CallPath callPath,
      Type queryType, GsonBuilder gsonBuilder, Type resourceType, Type typeOfListOfR) {
    super(stub, callPath, queryType, gsonBuilder, resourceType, typeOfListOfR);
  }

  protected ResourceQueryClientValueBased(WebServiceClient stub, WebServiceCallSpec callSpec,
      Type queryType, GsonBuilder gsonBuilder, Type resourceType, Type typeOfListOfR) {
    super(stub, callSpec, queryType, gsonBuilder, resourceType, typeOfListOfR);
  }
}
