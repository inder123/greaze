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
 */package com.google.greaze.example.rest.client;

import java.util.ArrayList;
import java.util.List;

import com.google.greaze.definition.rest.Id;
import com.google.greaze.definition.rest.MetaData;
import com.google.greaze.definition.rest.MetaDataBase;
import com.google.greaze.definition.rest.WebContext;
import com.google.greaze.example.definition.model.Cart;
import com.google.greaze.example.definition.model.LineItem;
import com.google.greaze.example.definition.model.Order;
import com.google.greaze.example.query.definition.QueryOrdersByItemName;
import com.google.greaze.example.service.definition.SampleJsonService;
import com.google.greaze.example.service.definition.ServicePaths;
import com.google.greaze.rest.client.ResourceDepotClient;
import com.google.greaze.rest.client.RestClientStub;
import com.google.greaze.rest.query.client.ResourceQueryClient;
import com.google.greaze.webservice.client.ServerConfig;
import com.google.greaze.webservice.client.WebServiceClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A sample client for the rest resource for {@link Order}
 *
 * @author Inderjeet Singh
 */
public class OrderClient {
  private final ResourceDepotClient<Cart> cartRestClient;
  private final ResourceDepotClient<Order> orderRestClient;
  private final ResourceQueryClient<Order, QueryOrdersByItemName> queryClient;
  private static final GsonBuilder gsonBuilder = new GsonBuilder()
    .setVersion(SampleJsonService.CURRENT_VERSION)
    .registerTypeAdapterFactory(new Id.GsonTypeAdapterFactory())
    .registerTypeAdapter(MetaData.class, new MetaDataBase.GsonTypeAdapter());
  private static final Gson gson = gsonBuilder.create();

  public OrderClient() {
    ServerConfig serverConfig = new ExampleServerConfig();

    RestClientStub restClientStub = new RestClientStub(serverConfig);
    cartRestClient = new ResourceDepotClient<Cart>(
        restClientStub, ServicePaths.CART.getCallPath(), Cart.class, gson);
    orderRestClient = new ResourceDepotClient<Order>(
        restClientStub, ServicePaths.ORDER.getCallPath(), Order.class, gson);
    queryClient = new ResourceQueryClient<Order, QueryOrdersByItemName>(
        new WebServiceClient(serverConfig), ServicePaths.ORDER.getCallPath(),
        QueryOrdersByItemName.class, gsonBuilder, Order.class); 
  }

  private Cart createCart(Cart cart) {
    return cartRestClient.post(cart, new WebContext());
  }

  public Order placeOrder(Cart cart) {
    Order order = new Order(cart, cart.getId().getValue());
    return orderRestClient.post(order, new WebContext());
  }

  private List<Order> query(String itemName) {
    return queryClient.query(new QueryOrdersByItemName(itemName), new WebContext());
  }

  public static void main(String[] args) {
    OrderClient client = new OrderClient();
    List<LineItem> lineItems = new ArrayList<LineItem>();
    String itemName = "cheese";
    lineItems.add(new LineItem(itemName, 2, 1000000L, "USD"));
    Cart cart = new Cart(lineItems, "Hungry Bird", "4111-1111-1111-1111");
    cart = client.createCart(cart);
    Order order = client.placeOrder(cart);
  
    System.out.println("Placed order: " + gson.toJson(order));
    List<Order> queriedOrders = client.query(itemName);
    System.out.printf("Queried orders by item name (%s): %s\n",
        itemName, gson.toJson(queriedOrders));
  }
}
