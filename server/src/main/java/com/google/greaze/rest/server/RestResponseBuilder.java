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
package com.google.greaze.rest.server;

import com.google.greaze.definition.rest.RestResource;
import com.google.greaze.definition.rest.Id;

/**
 * A class that builds response for a REST resource.
 *
 * @author Inderjeet Singh
 *
 * @param <R> The type of the rest resource
 */
public class RestResponseBuilder<R extends RestResource<R>>
    extends RestResponseBaseBuilder<Id<R>, R>{

  public RestResponseBuilder(Repository<R> resources) {
    super(resources);
  }
}
