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
package com.google.greaze.example.service.definition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.CallPathParser;

/**
 * An enum describing all paths for this service
 *
 * @author Inderjeet Singh
 */
public enum ServicePaths {
  CART("/cart"),
  ORDER("/order");
  
  private final CallPath path;

  private ServicePaths(String callPath) {
    CallPathParser callPathParser =
      new CallPathParser(SampleJsonService.RESOURCE_PREFIX, false, callPath);
    this.path = callPathParser.parse(SampleJsonService.RESOURCE_PREFIX + callPath);
  }

  public CallPath getCallPath() {
    return path;
  }

  public static Collection<CallPath> allServicePaths() {
    Collection<CallPath> servicePaths = new ArrayList<CallPath>();
    for (ServicePaths service : ServicePaths.values()) {
      servicePaths.add(service.getCallPath());
    }
    return Collections.unmodifiableCollection(servicePaths);
  }

  public static CallPath getCallPath(CallPath invokedPath) {
    for (ServicePaths path : values()) {
      CallPath callPath = path.path;
      String callPathInfo = callPath.get();
      // A rest path can end with a resource-id too.
      // For example, /rest/cart/1234 should match with /rest/cart
      if (callPathInfo != null && invokedPath.matches(callPath)) {
        return callPath;
      }
    }
    return null;
  }
}