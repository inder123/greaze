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
package com.google.greaze.end2end.fixtures;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.greaze.definition.fixtures.NetworkSwitcherPiped;
import com.google.greaze.server.GreazeDispatcherServlet;
import com.google.inject.servlet.GuiceFilter;

/**
 * {@link NetworkSwitcherPiped} for web requests. It uses Guice to service requests.
 *
 * @author Inderjeet Singh
 */
public class NetworkSwitcherWebService extends NetworkSwitcherPiped {
  private static final GuiceFilter guice = new GuiceFilter();
  private final FilterChain filterChain;

  protected NetworkSwitcherWebService(final GreazeDispatcherServlet dispatcher) {
    this.filterChain = new FilterChain() {
      @Override
      public void doFilter(ServletRequest request, ServletResponse response) throws IOException,
          ServletException {
        dispatcher.service(request, response);
      }
    };
  }

  protected void serviceRequest(HttpServletRequest req, HttpServletResponse res) throws IOException {
    try {
      guice.doFilter(req, res, filterChain);
      res.flushBuffer();
    } catch (ServletException e) {
      throw new IOException(e);
    }
  }
}
