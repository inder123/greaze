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

import com.google.greaze.definition.rest.ResourceId;
import com.google.greaze.definition.rest.RestRequestBase;
import com.google.greaze.definition.rest.RestResourceBase;
import com.google.greaze.webservice.client.RequestSender;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.util.logging.Level;

/**
 * Class to send a REST requests on a {@link HttpURLConnection}.
 * 
 * @author inder
 */
public final class RestRequestSender extends RequestSender {

  public RestRequestSender(Gson gson) {
    this(gson, null);
  }

  public RestRequestSender(Gson gson, Level logLevel) {
    super(gson, logLevel);
  }

  public <I extends ResourceId, R extends RestResourceBase<I, R>> void send(
      HttpURLConnection conn, RestRequestBase<I, R> request) {
    super.send(conn, request);
  }
}
