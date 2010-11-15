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
package com.google.greaze.example.server;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.greaze.definition.CallPath;
import com.google.greaze.server.dispatcher.RequestType;

/**
 * An example servlet that receives JSON web-service requests
 *
 * @author inder
 */
@SuppressWarnings("serial")
public class MainServlet extends HttpServlet {
  private final ResourceDepotDispatcher resourceDispatcher;
  private final WebServiceDispatcher wsDispatcher;

  public MainServlet() {
    this.resourceDispatcher = new ResourceDepotDispatcher();
    this.wsDispatcher = new WebServiceDispatcher();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void service(HttpServletRequest req, HttpServletResponse res) {
    String servletPath = req.getServletPath();
    int index = "/wsexampleserver".length();
    CallPath callPath = new CallPath(servletPath.substring(index));
    String queryName = RequestType.getQueryName(req.getParameterMap());
    RequestType requestType = RequestType.getRequestType(callPath, queryName, "/rest");
    switch (requestType) {
      case RESOURCE_ACCESS:
        resourceDispatcher.service(req, res, callPath);
      case RESOURCE_QUERY:
        break;
      case WEBSERVICE:
        wsDispatcher.service(req, res);
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }
}
