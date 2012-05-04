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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.greaze.definition.ErrorReason;
import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.server.filters.GreazeFilter;
import com.google.greaze.server.filters.GreazeFilterChain;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
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
  private static final Logger log = Logger.getLogger(GreazeDispatcherServlet.class.getSimpleName());
  private GreazeFilterChain filters;

  @Inject
  public GreazeDispatcherServlet(Injector injector, @Named("resource-prefix") String resourcePrefix,
      GreazeFilterChain filtersToBeInstalled) {
    this.filters = filtersToBeInstalled == null
        ? new GreazeFilterChain() : filtersToBeInstalled.copyOf();
    this.filters.install(new RequestServicingFilter());
    for (GreazeFilter filter : this.filters.getFilters()) {
      filter.init(injector, resourcePrefix);
    }
  }

  @Override
  public void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
    try {
      try {
        try {
          for (GreazeFilter filter : filters.getFilters()) {
            boolean continueFilterChain = filter.service(req, res);
            if (!continueFilterChain) {
              break;
            }
          }
        } catch (ProvisionException e) {
          Exception cause = (Exception) e.getCause();
          throw cause;
        }
      } catch (Exception e) {
        if (e instanceof WebServiceSystemException) {
          throw (WebServiceSystemException) e;
        } else {
          throw new WebServiceSystemException(ErrorReason.SERVER_UNAVAILABLE, e);
        }
      }
    } catch (WebServiceSystemException e) {
      ErrorReason reason = e.getReason();
      String reasonStr = reason.toString();
      log.log(Level.WARNING, reasonStr, e);
      res.setHeader(ErrorReason.HTTP_RESPONSE_HEADER_NAME, reasonStr);
      res.sendError(reason.getResponseCode(), e.getLocalizedMessage());
    }
  }
}
