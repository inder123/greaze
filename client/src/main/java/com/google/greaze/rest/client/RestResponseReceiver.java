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
package com.google.greaze.rest.client;

import java.net.HttpURLConnection;
import java.util.logging.Level;

import com.google.greaze.definition.rest.Id;
import com.google.greaze.definition.rest.RestResource;
import com.google.greaze.definition.rest.RestResponse;
import com.google.greaze.definition.rest.RestResponseSpec;
import com.google.gson.Gson;

/**
 * Receives a response coming on an {@link HttpURLConnection}.
 * 
 * @author Inderjeet Singh
 */
public class RestResponseReceiver<R extends RestResource<R>>
    extends RestResponseBaseReceiver<Id<R>, R> {

  public RestResponseReceiver(Gson gson, RestResponseSpec spec) {
    this(gson, spec, null);
  }
  public RestResponseReceiver(Gson gson, RestResponseSpec spec, Level logLevel) {
    super(gson, spec, logLevel);
  }

  @Override
  public RestResponse<R> receive(HttpURLConnection conn) {
    return (RestResponse<R>) super.receive(conn);
  }
}
