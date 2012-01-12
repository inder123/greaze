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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.greaze.client.internal.utils.UrlParamStringBuilder;
import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.definition.webservice.WebServiceCallSpec;
import com.google.greaze.definition.webservice.WebServiceRequest;
import com.google.greaze.definition.webservice.WebServiceResponse;
import com.google.gson.Gson;

/**
 * Main class used by clients to access a Gson Web service.
 * 
 * @author inder
 */
public class WebServiceClient {
  protected final ServerConfig config;
  protected static final Logger logger = Logger.getLogger(WebServiceClient.class.getName());

  public WebServiceClient(ServerConfig serverConfig) {
    this.config = serverConfig;
  }
  
  /** Visible for testing only */
  URL getWebServiceUrl(WebServiceCallSpec callSpec, WebServiceRequest request, Gson gson) {
    String baseUrl = buildBasePath(callSpec);
    try {
      String urlParamString = new UrlParamStringBuilder(gson)
        .add(request.getUrlParameters())
        .build();
      return new URL(baseUrl + urlParamString);
    } catch (MalformedURLException e) {
      throw new WebServiceSystemException(e);
    }
  }

  protected String buildBasePath(WebServiceCallSpec callSpec) {
    StringBuilder url = new StringBuilder(config.getServiceBaseUrl());
    if (callSpec.hasVersion()) {
      url.append('/').append(callSpec.getVersion());
    }
    url.append(callSpec.getPath().getServicePath());
    return url.toString();
  }
  
  public WebServiceResponse getResponse(
      WebServiceCallSpec callSpec, WebServiceRequest request, Gson gson) {
    HttpURLConnection conn = null;
    try {
      URL webServiceUrl = getWebServiceUrl(callSpec, request, gson);
      logger.log(Level.INFO, "Opening connection to " + webServiceUrl);
      conn = openConnection(webServiceUrl);
      RequestSender requestSender = new RequestSender(gson);
      requestSender.send(conn, request);
      ResponseReceiver responseReceiver = new ResponseReceiver(gson, callSpec.getResponseSpec());
      return responseReceiver.receive(conn);
    } catch (NullPointerException e) {
      throw new WebServiceSystemException(e);
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
