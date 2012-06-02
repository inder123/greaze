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
package com.google.greaze.server.dispatcher;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Preconditions;
import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.rest.WebContext;
import com.google.greaze.definition.rest.WebContextSpec;
import com.google.greaze.definition.rest.query.ResourceQueryBase;
import com.google.greaze.definition.rest.query.ResourceQueryParams;
import com.google.greaze.definition.rest.query.ResourceQueryUtils;
import com.google.greaze.definition.webservice.RequestSpec;
import com.google.greaze.definition.webservice.ResponseBody;
import com.google.greaze.definition.webservice.ResponseBodyGsonTypeAdapterFactory;
import com.google.greaze.definition.webservice.ResponseBodySpec;
import com.google.greaze.definition.webservice.WebServiceCallSpec;
import com.google.greaze.definition.webservice.WebServiceRequest;
import com.google.greaze.definition.webservice.WebServiceResponse;
import com.google.greaze.server.internal.utils.WebContextExtractor;
import com.google.greaze.webservice.server.RequestReceiver;
import com.google.greaze.webservice.server.ResponseSender;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Provider;

/**
 * A class to service incoming resource query requests using corresponding query handlers.
 *
 * @author Inderjeet Singh
 */
public class ResourceQueryDispatcher {
  protected final Provider<GsonBuilder> gsonBuilder;

  public ResourceQueryDispatcher(Provider<GsonBuilder> gsonBuilder) {
    this.gsonBuilder = gsonBuilder;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public void service(HttpServletRequest req, HttpServletResponse res,
      String queryName, CallPath callPath, ResourceQueryBase resourceQuery) {
    Preconditions.checkNotNull(resourceQuery);
    WebContextSpec webContextSpec = resourceQuery.getWebContextSpec();
    WebServiceCallSpec spec = ResourceQueryUtils.generateCallSpec(callPath,
        resourceQuery.getResourceType(), resourceQuery.getQueryType(),
        webContextSpec);
    RequestSpec requestSpec = spec.getRequestSpec();
    RequestReceiver requestReceiver = new RequestReceiver(gsonBuilder.get(), requestSpec);
    WebServiceRequest webServiceRequest = requestReceiver.receive(req);

    ResourceQueryParams queryParams =
      (ResourceQueryParams) webServiceRequest.getUrlParameters().getParamsObject();
    WebContext context = new WebContextExtractor(webContextSpec).extract(webServiceRequest.getHeaders());
    List results = resourceQuery.query(queryParams, context);
    HeaderMapSpec headerSpec = new HeaderMapSpec.Builder().build();
    HeaderMap responseHeaders = new HeaderMap.Builder(headerSpec).build();
    ResponseBodySpec bodySpec = new ResponseBodySpec.Builder()
      .setListBody(resourceQuery.getResourceType())
      .build();
    ResponseBody responseBody = new ResponseBody.Builder(bodySpec)
      .setListBody(results)
      .build();
    WebServiceResponse response = new WebServiceResponse(responseHeaders, responseBody);
    Gson gson = gsonBuilder.get()
        .registerTypeAdapterFactory(new ResponseBodyGsonTypeAdapterFactory(bodySpec))
        .create();
    ResponseSender responseSender = new ResponseSender(gson);
    responseSender.send(res, response);
  }

  public void service(HttpServletRequest req, HttpServletResponse res, String queryName,
      CallPath callPath) {
  }
}
