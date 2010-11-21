/*
 * Copyright (C) 2008 Google Inc.
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

import com.google.greaze.definition.rest.RestRequestSpec;
import com.google.greaze.definition.rest.RestResource;
import com.google.greaze.definition.rest.ValueBasedId;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;

/**
 * Receives and parses a request at the server side on a
 * {@link HttpServletRequest}.  
 * 
 * @author Inderjeet Singh
 */
public final class RestRequestReceiverValueBased<R extends RestResource<R>>
    extends RestRequestReceiver<ValueBasedId<R>, R> {

  public RestRequestReceiverValueBased(Gson gson, RestRequestSpec spec) {
    super(gson, spec);
  }
}
