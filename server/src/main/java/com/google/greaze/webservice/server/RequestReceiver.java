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

import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.definition.webservice.RequestBody;
import com.google.greaze.definition.webservice.RequestBodySpec;
import com.google.greaze.definition.webservice.RequestSpec;
import com.google.greaze.definition.webservice.WebServiceRequest;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Receives and parses a request at the server side on a {@link HttpServletRequest}.  
 * 
 * @author Inderjeet Singh
 */
public class RequestReceiver {

  protected final Gson gson;
  protected final RequestSpec spec;

  public RequestReceiver(Gson gson, RequestSpec spec) {
    this.gson = gson;
    this.spec = spec;
  }
  
  public WebServiceRequest receive(HttpServletRequest request) {
    try {
      HeaderMap requestParams = buildRequestParams(request);
      UrlParamsExtractor urlParamsExtractor = new UrlParamsExtractor(spec.getUrlParamsSpec(), gson);
      HeaderMap urlParams = urlParamsExtractor.extractUrlParams(request);
      RequestBody requestBody = buildRequestBody(request);
      
      HttpMethod method = HttpMethod.getMethod(request.getMethod());
      return new WebServiceRequest(method, requestParams, urlParams, requestBody);
    } catch (IOException e) {
      throw new WebServiceSystemException(e);
    } catch (JsonParseException e) {
      // Not a Web service request
      throw new WebServiceSystemException(e);
    }
  }
  
  protected HeaderMap buildRequestParams(HttpServletRequest request) {
    HeaderMapSpec paramsSpec = this.spec.getHeadersSpec();
    HeaderMap.Builder paramsBuilder = new HeaderMap.Builder(paramsSpec);
    for (Map.Entry<String, Type> param : paramsSpec.entrySet()) {
      String name = param.getKey();
      Type type = param.getValue();
      String header = request.getHeader(name);
      if (header != null && !header.equals("")) { 
        Object value = gson.fromJson(header, type);
        paramsBuilder.put(name, value);
      }
    }
    return paramsBuilder.build();
  }

  private RequestBody buildRequestBody(HttpServletRequest request) throws IOException {
    RequestBodySpec bodySpec = spec.getBodySpec();
    if (bodySpec.size() == 0) {
      return createEmptyRequestBody(bodySpec);
    }
    Reader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
    RequestBody requestBody = gson.fromJson(reader, RequestBody.class);
    return requestBody;
  }

  private RequestBody createEmptyRequestBody(RequestBodySpec bodySpec) {
    return new RequestBody.Builder(bodySpec).build();
  }
}
