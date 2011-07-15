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

import com.google.greaze.definition.rest.Id;
import com.google.greaze.definition.rest.MetaData;
import com.google.greaze.definition.rest.MetaDataBase;
import com.google.greaze.definition.rest.RestCallSpec;
import com.google.greaze.definition.rest.RestCallSpecMap;
import com.google.greaze.definition.webservice.WebServiceCallSpec;
import com.google.greaze.example.definition.model.Cart;
import com.google.greaze.example.definition.model.Order;
import com.google.greaze.example.server.ResourceQueryDispatcherExample;
import com.google.greaze.example.server.WebServiceDispatcherExample;
import com.google.greaze.example.service.definition.SampleJsonService;
import com.google.greaze.rest.server.Repository;
import com.google.greaze.rest.server.RepositoryInMemory;
import com.google.greaze.rest.server.ResponseBuilderMap;
import com.google.greaze.rest.server.RestResponseBaseBuilder;
import com.google.greaze.rest.server.RestResponseBuilder;
import com.google.greaze.server.WebServiceDispatcher;
import com.google.greaze.server.dispatcher.ResourceQueryDispatcher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.google.inject.servlet.RequestScoped;

/**
 * Guice module for things specific to the application
 *
 * @author Inderjeet Singh
 */
public class GreazeExampleServerModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(String.class).annotatedWith(Names.named("rest-prefix")).toInstance("/rest");
  }

  @Singleton
  @Provides
  public Repository<Order> getRepositoryOrders() {
    return new RepositoryInMemory<Order>(Order.class);
  }

  @Singleton
  @Provides
  public Repository<Cart> getRepositoryCarts() {
    return new RepositoryInMemory<Cart>(Cart.class);
  }

  @Singleton
  @Provides
  public RestCallSpecMap getRestCallSpecMap() {
    return SampleJsonService.REST_CALL_SPEC_MAP;
  }

  @Singleton
  @Provides
  public ResponseBuilderMap getResponseBuilders(Repository<Cart> carts, Repository<Order> orders) {
  return new ResponseBuilderMap.Builder()
      .set(SampleJsonService.CART_SPEC.getResourceType(), new RestResponseBuilder<Cart>(carts))
      .set(SampleJsonService.ORDER_SPEC.getResourceType(), new RestResponseBuilder<Order>(orders))
      .build();
  }

  @Singleton
  @Provides
  public ResourceQueryDispatcher getQueryDispatcher(Repository<Order> orders) {
    return new ResourceQueryDispatcherExample(orders);
  }

  // This can be made Singleton since it doesn't really have anything specific to the request.
  // However, in many real use-cases, Gson instance needs to be created per request.
  @Singleton
  @Provides
  public GsonBuilder getGsonBuilder() {
    return new GsonBuilder()
      .setVersion(SampleJsonService.CURRENT_VERSION)
      .registerTypeAdapter(Id.class, new Id.GsonTypeAdapter())
      .registerTypeAdapter(MetaData.class, new MetaDataBase.GsonTypeAdapter());
  }

  @SuppressWarnings({"unchecked"})
  @RequestScoped
  @Provides
  public RestResponseBaseBuilder getRestResponseBaseBuilder(RestCallSpec callSpec,
      ResponseBuilderMap responseBuilders) {
    return responseBuilders.get(callSpec.getResourceType());
  }
  
  @Singleton
  @Provides
  public Gson getGson(GsonBuilder gsonBuilder) {
    return gsonBuilder.create();
  }

  @Singleton
  @Provides
  public WebServiceDispatcher getWebServiceDispatcher(Injector injector) {
    return new WebServiceDispatcherExample(injector);
  }

  @RequestScoped
  @Provides
  public WebServiceCallSpec getWebServiceCallSpec() {
    return SampleJsonService.PLACE_ORDER;
  }
}