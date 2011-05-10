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
package com.google.greaze.example.rest.client;

import com.google.greaze.example.service.definition.SampleJsonService;
import com.google.greaze.webservice.client.ServerConfig;

public final class ExampleServerConfig extends ServerConfig {

  /**
   * Server where the JSON service is deployed. localhost:8888 is the address when the service
   * is deployed to a local App engine instance. 
   */
  private static final String SERVER_BASE_URL = "http://localhost:8888/greazeexampleservice";

  public ExampleServerConfig() {
    super(SERVER_BASE_URL + SampleJsonService.RESOURCE_PREFIX);
  }
}
