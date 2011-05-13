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
package com.google.greaze.end2end.fixtures;

import com.google.common.collect.ImmutableList;
import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.fixtures.NetworkSwitcherPiped;
import com.google.greaze.server.inject.GreazeServerModule;
import com.google.gson.Gson;

/**
 * Connects a RequestReceiver to HttpURLConnection
 * 
 * @author Inderjeet Singh
 */
public class NetworkSwitcherWebService extends NetworkSwitcherPiped {

  protected final GreazeServerModule gsm;
  protected final Gson serverGson;

  /**
   * @param serverGson Gson instance used for server-side JSON serialization/deserialization
   * @param callPath The path where the web-service is made available
   */
  public NetworkSwitcherWebService(Gson serverGson, CallPath callPath) {
    this.serverGson = serverGson;
    this.gsm = new GreazeServerModule("/fake", ImmutableList.of(callPath), callPath.getBasePath());
  }
}