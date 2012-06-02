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
package com.google.greaze.webservice.server;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.definition.webservice.RequestBodyGsonTypeAdapterFactory;
import com.google.greaze.definition.webservice.RequestSpec;
import com.google.greaze.definition.webservice.WebServiceRequest;
import com.google.greaze.server.internal.utils.UrlParamsExtractor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

/**
 * Receives and parses a request at the server side on a {@link HttpServletRequest}.
 *
 * @author Inderjeet Singh
 */
public class RequestReceiver {

  protected final Gson gson;
  protected final RequestSpec spec;
  protected final UrlParamsExtractor urlParamsExtractor;

  public RequestReceiver(GsonBuilder gsonBuilder, RequestSpec spec) {
    this.gson = gsonBuilder
        .registerTypeAdapterFactory(new RequestBodyGsonTypeAdapterFactory(spec.getBodySpec()))
        .create();
    this.spec = spec;
    this.urlParamsExtractor = new UrlParamsExtractor(spec.getUrlParamsSpec(), gson);
  }

  public WebServiceRequest receive(HttpServletRequest request) {
    try {
      RequestData requestData = RequestData.create(request, spec, gson);
      return requestData.get();
    } catch (IOException e) {
      throw new WebServiceSystemException(e);
    } catch (JsonParseException e) {
      // Not a Web service request
      throw new WebServiceSystemException(e);
    }
  }
}
