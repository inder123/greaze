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
package com.google.greaze.example.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.greaze.definition.CallPath;
import com.google.greaze.definition.rest.ResourceIdFactory;
import com.google.greaze.definition.rest.MetaDataBase;
import com.google.greaze.definition.rest.RestCallSpecMap;
import com.google.greaze.definition.rest.RestCallSpec;
import com.google.greaze.definition.rest.RestRequestBase;
import com.google.greaze.definition.rest.RestResponseBase;
import com.google.greaze.definition.rest.Id;
import com.google.greaze.example.definition.model.Cart;
import com.google.greaze.example.definition.model.Order;
import com.google.greaze.example.service.definition.ServicePaths;
import com.google.greaze.rest.server.RepositoryBase;
import com.google.greaze.rest.server.RepositoryInMemoryBase;
import com.google.greaze.rest.server.ResponseBuilderMap;
import com.google.greaze.rest.server.RestRequestBaseReceiver;
import com.google.greaze.rest.server.RestResponseBaseBuilder;
import com.google.greaze.rest.server.RestResponseSender;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A dispatcher for all the REST requests
 *
 * @author Inderjeet Singh
 */
public final class ResourceDepotDispatcher {
  private static final double CURRENT_VERSION = 1D;
  private final RestCallSpecMap resourceMap;
  private final ResponseBuilderMap responseBuilders;
  private final RestCallSpec cartSpec;
  private final RestCallSpec orderSpec;
  private final Gson gson;

  public ResourceDepotDispatcher() {
    this.cartSpec = new RestCallSpec.Builder(ServicePaths.CART.getCallPath(), Cart.class)
      .setVersion(CURRENT_VERSION)
      .build();
    this.orderSpec = new RestCallSpec.Builder(ServicePaths.CART.getCallPath(), Cart.class)
      .setVersion(CURRENT_VERSION)
      .build(); 
    this.resourceMap = new RestCallSpecMap.Builder()
      .set(ServicePaths.CART.getCallPath(), cartSpec)
      .set(ServicePaths.ORDER.getCallPath(), orderSpec)
      .build();
    gson = new GsonBuilder()
      .setVersion(CURRENT_VERSION)
      .registerTypeAdapter(Id.class, new Id.GsonTypeAdapter())
      .registerTypeAdapter(MetaDataBase.class, new MetaDataBase.GsonTypeAdapter())
      .create();
    RepositoryBase<Id<Cart>, Cart> carts =
      new RepositoryInMemoryBase<Id<Cart>, Cart>(Id.class, Cart.class);
    RepositoryBase<Id<Order>, Order> orders =
      new RepositoryInMemoryBase<Id<Order>, Order>(Id.class, Order.class);
    responseBuilders = new ResponseBuilderMap.Builder()
        .set(cartSpec.getResourceType(), new RestResponseBaseBuilder<Id<Cart>, Cart>(carts))
        .set(orderSpec.getResourceType(), new RestResponseBaseBuilder<Id<Order>, Order>(orders))
        .build();
  }

  public ResourceIdFactory<Id<?>> getIDFactory(RestCallSpec callSpec) {
    return new ResourceIdFactory<Id<?>>(Id.class, callSpec.getResourceType());
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public RestRequestBase getRestRequest(Gson gson, RestCallSpec callSpec, CallPath callPath,
      HttpServletRequest request, ResourceIdFactory<Id<?>> idFactory) {
    RestRequestBaseReceiver requestReceiver = new RestRequestBaseReceiver(gson, callSpec.getRequestSpec());
    return requestReceiver.receive(request, idFactory.createId(callPath.getResourceId()));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public void service(HttpServletRequest req, HttpServletResponse res, CallPath callPath) {
    RestCallSpec callSpec = resourceMap.get(callPath).createCopy(callPath);
    ResourceIdFactory<Id<?>> idFactory = getIDFactory(callSpec);
    RestRequestBase<?, ?> restRequest = getRestRequest(gson, callSpec, callPath, req, idFactory);
    RestResponseBase.Builder response = new RestResponseBase.Builder(callSpec.getResponseSpec());
    RestResponseBaseBuilder responseBuilder = responseBuilders.get(callSpec.getResourceType());
    responseBuilder.buildResponse(restRequest, response);
    RestResponseBase restResponse = response.build();
    RestResponseSender responseSender = new RestResponseSender(gson);
    responseSender.send(res, restResponse);
  }
}
