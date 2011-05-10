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
package com.google.greaze.example.server.inject;

import com.google.greaze.example.service.definition.SampleJsonService;
import com.google.greaze.example.service.definition.ServicePaths;
import com.google.greaze.server.inject.GreazeServerModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * This servlet context listener sets up the Guice injection. It is invoked because of its
 * setting through listener element in web.xml
 *
 * @author Inderjeet Singh
 */
public class ExampleServletContextListener extends GuiceServletContextListener {

  @Override
  protected Injector getInjector() {
    return Guice.createInjector(
        new GreazeExampleServerModule(),
        new GreazeServerModule("/greazeexampleservice", ServicePaths.allServicePaths(),
            SampleJsonService.RESOURCE_PREFIX));
  }
}