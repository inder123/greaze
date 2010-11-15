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
package com.google.greaze.webservice.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.definition.webservice.ResponseBody;
import com.google.greaze.definition.webservice.ResponseBodyGsonConverter;
import com.google.greaze.definition.webservice.WebServiceCallSpec;
import com.google.greaze.definition.webservice.WebServiceRequest;
import com.google.greaze.definition.webservice.WebServiceResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Main class used by clients to access a Gson Web service.
 * 
 * @author inder
 */
public class WebServiceClient {
  private final ServerConfig config;
  private final Logger logger;
  private final Level logLevel;

  public WebServiceClient(ServerConfig serverConfig) {
    this(serverConfig, null);
  }

  public WebServiceClient(ServerConfig serverConfig, Level logLevel) {
    this.config = serverConfig;
    this.logger = logLevel == null ? null : Logger.getLogger(WebServiceClient.class.getName());
    this.logLevel = logLevel;
  }
  
  private URL getWebServiceUrl(WebServiceCallSpec callSpec, WebServiceRequest request, Gson gson) {
    String baseUrl = new String(config.getServiceBaseUrl() + callSpec.getPath().get());
    try {
      return new URL(baseUrl + buildQueryParamString(gson, request.getUrlParameters()));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  /** Visible for testing only */
  static String buildQueryParamString(Gson gson, HeaderMap urlParameters) {
    StringBuilder url = new StringBuilder();
    HeaderMapSpec spec = urlParameters.getSpec();
    boolean first = true;
    try {
      for (Map.Entry<String, Object> entry : urlParameters.entrySet()) {
        if (first) {
          first = false;
          url.append('?');
        } else {
          url.append('&');
        }
        String paramName = entry.getKey();
        Type type = spec.getTypeFor(paramName);
        Object value = entry.getValue();
        String valueAsJson = gson.toJson(value, type);
        String paramValue = stripQuotesIfString(valueAsJson);
        url.append(paramName).append('=').append(URLEncoder.encode(paramValue, "UTF-8"));
      }
      return url.toString();
    } catch (UnsupportedEncodingException e) {
      throw new WebServiceSystemException(e);
    }
  }

  /** Visible for testing only */
  static String stripQuotesIfString(String valueAsJson) {
    String paramValue = valueAsJson.startsWith("\"") ?
        valueAsJson.substring(1, valueAsJson.length() -1) : valueAsJson;
    return paramValue;
  }

  public WebServiceResponse getResponse(WebServiceCallSpec callSpec, WebServiceRequest request) {
    Gson gson = new GsonBuilder()
        .registerTypeAdapter(ResponseBody.class,
            new ResponseBodyGsonConverter(callSpec.getResponseSpec().getBodySpec()))
        .create();
    return getResponse(callSpec, request, gson);
  }

  public WebServiceResponse getResponse(
      WebServiceCallSpec callSpec, WebServiceRequest request, Gson gson) {
    try {
      URL webServiceUrl = getWebServiceUrl(callSpec, request, gson);
      if (logger != null) {
        logger.log(logLevel, "Opening connection to " + webServiceUrl);
      }
      HttpURLConnection conn = (HttpURLConnection) webServiceUrl.openConnection();
      RequestSender requestSender = new RequestSender(gson, logLevel);
      requestSender.send(conn, request);
      ResponseReceiver responseReceiver =
        new ResponseReceiver(gson, callSpec.getResponseSpec(), logLevel);
      return responseReceiver.receive(conn);
    } catch (IOException e) {
      throw new WebServiceSystemException(e);
    } catch (IllegalArgumentException e) {
      throw new WebServiceSystemException(e);
    }
  }
  
  @Override
  public String toString() {
    return String.format("config:%s", config);
  }
}
