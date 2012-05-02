/*
 * Copyright (C) 2012 Greaze Authors.
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

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.ErrorReason;
import com.google.greaze.definition.LogConfig;
import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.server.dispatcher.RequestType;
import com.google.greaze.server.dispatcher.ResourceQueryDispatcher;
import com.google.greaze.server.filters.GreazeFilter;
import com.google.inject.Injector;

/**
 * A {@link GreazeFilter} that services the request for resource access, query or a webservice.
 *
 * @author Inderjeet Singh
 */
final class RequestServicingFilter implements GreazeFilter {

  private static final Logger log = Logger.getLogger(RequestServicingFilter.class.getSimpleName());
  private Injector injector;
  private String resourcePrefix;

  @Override
  public void init(Injector injector, String resourcePrefix) {
    this.injector = injector;
    this.resourcePrefix = resourcePrefix;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean service(HttpServletRequest req, HttpServletResponse res)
      throws WebServiceSystemException {
    try {
      CallPath callPath = injector.getInstance(CallPath.class);
      if (callPath.equals(CallPath.NULL_PATH)) {
        throw new WebServiceSystemException(
            ErrorReason.INVALID_CALLPATH, req.getServletPath());
      }
      String queryName = RequestType.getQueryName(req.getParameterMap());
      RequestType requestType = RequestType.getRequestType(callPath, queryName, resourcePrefix);
      if (LogConfig.INFO) log.info(String.format("%s: %s", requestType, callPath));
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
      return false;
    } catch (IllegalArgumentException e) {
      throw new WebServiceSystemException(e);
    } catch (NullPointerException e) {
      throw new WebServiceSystemException(e);
    }
  }
}
