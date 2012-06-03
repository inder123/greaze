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
package com.google.greaze.end2end.fixtures;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.rest.RestCallSpecMap;
import com.google.greaze.definition.rest.query.ResourceQuery;
import com.google.greaze.rest.server.ResponseBuilderMap;
import com.google.greaze.server.dispatcher.ResourceQueryDispatcher;
import com.google.greaze.server.filters.GreazeFilterChain;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;

/**
 * A Builder for a Guice {@link Module} for Greaze REST resources and queries.
 * 
 * @author Inderjeet Singh
 */
@SuppressWarnings("rawtypes")
public class RestModuleBuilder {

  private GreazeFilterChain filters;
  private GsonBuilder serverGsonBuilder;
  private ResourceQuery queryHandler;
  private ResponseBuilderMap responseBuilderMap;
  private RestCallSpecMap restCallSpecMap;

  public RestModuleBuilder setResponseBuilderMap(ResponseBuilderMap responseBuilderMap) {
    this.responseBuilderMap = responseBuilderMap;
    return this;
  }

  public RestModuleBuilder setRestCallSpecMap(RestCallSpecMap restCallSpecMap) {
    this.restCallSpecMap = restCallSpecMap;
    return this;
  }

  public RestModuleBuilder setResourceQueryHandler(ResourceQuery queryHandler) {
    this.queryHandler = queryHandler;
    return this;
  }

  public RestModuleBuilder setGreazeFilterChain(GreazeFilterChain filters) {
    this.filters = filters;
    return this;
  }

  public RestModuleBuilder setServerGsonBuilder(GsonBuilder serverGsonBuilder) {
    this.serverGsonBuilder = serverGsonBuilder;
    return this;
  }

  @SuppressWarnings("unused")
  public Module build() {
    if (responseBuilderMap == null) responseBuilderMap = new ResponseBuilderMap.Builder().build();
    if (restCallSpecMap == null) restCallSpecMap = new RestCallSpecMap.Builder().build();
    if (filters == null) filters = new GreazeFilterChain();
    if (serverGsonBuilder == null) serverGsonBuilder = new GsonBuilder();

    return new AbstractModule() {
      @Override
      protected void configure() {
      }

      @Singleton
      @Provides
      public ResponseBuilderMap getResponseBuilderMap() {
        return responseBuilderMap;
      }

      @Singleton
      @Provides
      public RestCallSpecMap getRestCallSpecMap() {
        return restCallSpecMap;
      }

      @Singleton
      @Provides
      public GreazeFilterChain getFilters() {
        return filters;
      }

      @RequestScoped
      @Provides
      public GsonBuilder getGsonBuilder() {
        return serverGsonBuilder;
      }

      @RequestScoped
      @Provides
      public Gson getGson(GsonBuilder gsonBuilder) {
        return gsonBuilder.create();
      }

      @RequestScoped
      @Provides
      public ResourceQueryDispatcher getResourceQueryDispatcher(Provider<GsonBuilder> gsonProvider) {
        return new ResourceQueryDispatcher(gsonProvider) {
          public void service(HttpServletRequest req, HttpServletResponse res, String queryName,
              CallPath callPath) {
            super.service(req, res, queryName, callPath, queryHandler);
          }
        };
      }
    };
  }
}
