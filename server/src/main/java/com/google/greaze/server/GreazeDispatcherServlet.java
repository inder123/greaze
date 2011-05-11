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

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.ErrorReason;
import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.server.dispatcher.RequestType;
import com.google.greaze.server.dispatcher.ResourceQueryDispatcher;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * An dispatcher servlet that receives JSON web-service requests
 *
 * @author Inderjeet Singh
 */
@Singleton
@SuppressWarnings("serial")
public class GreazeDispatcherServlet extends HttpServlet {
  private final Injector injector;
  private final String resourcePrefix;

  @Inject
  public GreazeDispatcherServlet(Injector injector, @Named("resource-prefix") String resourcePrefix) {
    this.injector = injector;
    this.resourcePrefix = resourcePrefix;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
    try {
      CallPath callPath = injector.getInstance(CallPath.class);
      if (callPath.equals(CallPath.NULL_PATH)) {
        throw new WebServiceSystemException(
            ErrorReason.INVALID_CALLPATH, req.getServletPath());
      }
      String queryName = RequestType.getQueryName(req.getParameterMap());
      RequestType requestType = RequestType.getRequestType(callPath, queryName, resourcePrefix);
      switch (requestType) {
      case RESOURCE_ACCESS:
        injector.getInstance(ResourceDepotDispatcher.class).service(res);
        break;
      case RESOURCE_QUERY:
        injector.getInstance(ResourceQueryDispatcher.class).service(req, res, queryName, callPath);
        break;
      case WEBSERVICE:
        injector.getInstance(WebServiceDispatcher.class).service(req, res);
        break;
      default:
        throw new UnsupportedOperationException();
      }
    } catch (WebServiceSystemException e) {
      res.setHeader(ErrorReason.HTTP_RESPONSE_HEADER_NAME, e.getReason().toString());
      res.sendError(e.getReason().getResponseCode(), e.getLocalizedMessage());
    }
  }
}
