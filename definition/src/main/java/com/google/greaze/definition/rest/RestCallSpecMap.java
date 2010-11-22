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
package com.google.greaze.definition.rest;

import java.util.HashMap;
import java.util.Map;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.internal.utils.GreazePreconditions;

/**
 * A {@link Map} of {@link CallPath} to {@link RestCallSpec}
 *
 * @author inder
 */
public final class RestCallSpecMap {

  public static final class Builder {
    private final Map<CallPath, RestCallSpec> resources =
      new HashMap<CallPath, RestCallSpec>();
    
    public Builder set(CallPath baseCallPath, RestCallSpec spec) {
      GreazePreconditions.checkArgument(resources.get(baseCallPath) == null);
      resources.put(baseCallPath, spec);
      return this;
    }

    public RestCallSpecMap build() {
      return new RestCallSpecMap(resources);
    }
  }

  private final Map<CallPath, RestCallSpec> resources;

  public RestCallSpecMap(Map<CallPath, RestCallSpec> resources) {
    this.resources = resources;
  }

  public RestCallSpec get(CallPath callPath) {
    return resources.get(callPath);
  }
}
