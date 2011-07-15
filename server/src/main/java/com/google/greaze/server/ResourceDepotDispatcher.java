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
package com.google.greaze.server;

import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Preconditions;
import com.google.greaze.definition.rest.RestCallSpec;
import com.google.greaze.definition.rest.RestRequestBase;
import com.google.greaze.definition.rest.RestResponseBase;
import com.google.greaze.definition.rest.WebContext;
import com.google.greaze.rest.server.RestResponseBaseBuilder;
import com.google.greaze.rest.server.RestResponseSender;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * A dispatcher for all the REST requests
 *
 * @author Inderjeet Singh
 */
public final class ResourceDepotDispatcher {
  private final Injector injector;

  @Inject
  public ResourceDepotDispatcher(Injector injector) {
    this.injector = injector;
  }

  @SuppressWarnings("unchecked")
  public void service(HttpServletResponse res) {
    RestCallSpec callSpec = injector.getInstance(RestCallSpec.class);
    Preconditions.checkNotNull(callSpec);
    Gson gson = injector.getInstance(Gson.class);
    RestRequestBase<?, ?> restRequest = injector.getInstance(RestRequestBase.class);
    RestResponseBase.Builder response = new RestResponseBase.Builder(callSpec.getResponseSpec());
    RestResponseBaseBuilder responseBuilder = injector.getInstance(RestResponseBaseBuilder.class);
    WebContext context = injector.getInstance(WebContext.class);
    responseBuilder.buildResponse(context, restRequest, response);
    RestResponseBase webServiceResponse = response.build();
    RestResponseSender responseSender = new RestResponseSender(gson);
    responseSender.send(res, webServiceResponse);
  }
}
