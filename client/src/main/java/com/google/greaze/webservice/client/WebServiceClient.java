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

import com.google.greaze.client.internal.utils.UrlParamStringBuilder;
import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.definition.webservice.ResponseBody;
import com.google.greaze.definition.webservice.WebServiceCallSpec;
import com.google.greaze.definition.webservice.WebServiceRequest;
import com.google.greaze.definition.webservice.WebServiceResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class used by clients to access a Gson Web service.
 * 
 * @author inder
 */
public class WebServiceClient {
  protected final ServerConfig config;
  protected final Logger logger;
  protected final Level logLevel;

  public WebServiceClient(ServerConfig serverConfig) {
    this(serverConfig, null);
  }

  public WebServiceClient(ServerConfig serverConfig, Level logLevel) {
    this.config = serverConfig;
    this.logger = logLevel == null ? null : Logger.getLogger(WebServiceClient.class.getName());
    this.logLevel = logLevel;
  }
  
  /** Visible for testing only */
  URL getWebServiceUrl(WebServiceCallSpec callSpec, WebServiceRequest request, Gson gson) {
    String baseUrl = new String(config.getServiceBaseUrl() + callSpec.getPath().get());
    try {
      String urlParamString = new UrlParamStringBuilder(gson)
        .add(request.getUrlParameters())
        .build();
      return new URL(baseUrl + urlParamString);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public WebServiceResponse getResponse(WebServiceCallSpec callSpec, WebServiceRequest request) {
    Gson gson = new GsonBuilder()
        .registerTypeAdapter(ResponseBody.class,
            new ResponseBody.GsonTypeAdapter(callSpec.getResponseSpec().getBodySpec()))
        .create();
    return getResponse(callSpec, request, gson);
  }

  public WebServiceResponse getResponse(
      WebServiceCallSpec callSpec, WebServiceRequest request, Gson gson) {
    HttpURLConnection conn = null;
    try {
      URL webServiceUrl = getWebServiceUrl(callSpec, request, gson);
      if (logger != null) {
        logger.log(logLevel, "Opening connection to " + webServiceUrl);
      }
      conn = openConnection(webServiceUrl);
      RequestSender requestSender = new RequestSender(gson, logLevel);
      requestSender.send(conn, request);
      ResponseReceiver responseReceiver =
        new ResponseReceiver(gson, callSpec.getResponseSpec(), logLevel);
      return responseReceiver.receive(conn);
    } catch (IllegalArgumentException e) {
      throw new WebServiceSystemException(e);
    } finally {
      closeIgnoringErrors(conn);
    }
  }

  protected HttpURLConnection openConnection(URL url) {
    try {
      return (HttpURLConnection) url.openConnection();
    } catch (IOException e) {
      throw new WebServiceSystemException(e);
    }
  }

  @Override
  public String toString() {
    return String.format("config:%s", config);
  }

  protected static void closeIgnoringErrors(HttpURLConnection conn) {
    if (conn != null) {
      conn.disconnect();
    }
  }
}
