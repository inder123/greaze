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
package com.google.greaze.server.filters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.greaze.definition.WebServiceSystemException;
import com.google.inject.Injector;

/**
 * A filter (similar to Servlet Filters) that is invoked as an interceptor for each
 * request.
 *
 * @author Inderjeet Singh
 */
public interface GreazeFilter {

  /**
   * This method is called to initialize the filter with injector and resourcePrefix.
   * A filter should save these into fields for reuse later.
   */
  public void init(Injector injector, String resourcePrefix);

  /**
   * This method is called to service a request through this filter
   * Returns false if the response is complete and the filters chain should be terminated here.
   * True if the filters should continue to be invoked.
   */
  public boolean service(HttpServletRequest req, HttpServletResponse res)
      throws WebServiceSystemException;
}
