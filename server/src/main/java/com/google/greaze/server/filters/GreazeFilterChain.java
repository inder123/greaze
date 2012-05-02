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

import java.util.ArrayList;
import java.util.List;

/**
 * A chain of filters to be applied to all requests by a Greaze dispatcher
 *
 * @author Inderjeet Singh
 */
public final class GreazeFilterChain {
  private final List<GreazeFilter> filters;

  public GreazeFilterChain() {
    this(new ArrayList<GreazeFilter>());
  }

  private GreazeFilterChain(List<GreazeFilter> filters) {
    this.filters = filters;
  }
  public void install(GreazeFilter filter) {
    filters.add(filter);
  }

  public GreazeFilterChain copyOf() {
    List<GreazeFilter> copy = new ArrayList<GreazeFilter>();
    copy.addAll(filters);
    return new GreazeFilterChain(copy);
  }

  public Iterable<GreazeFilter> getFilters() {
    return filters;
  }
}
