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
package com.google.greaze.example.webservice.client;

import java.util.ArrayList;
import java.util.List;

import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.UrlParams;
import com.google.greaze.definition.webservice.RequestBody;
import com.google.greaze.definition.webservice.WebServiceCallSpec;
import com.google.greaze.definition.webservice.WebServiceRequest;
import com.google.greaze.definition.webservice.WebServiceResponse;
import com.google.greaze.example.definition.model.Cart;
import com.google.greaze.example.definition.model.LineItem;
import com.google.greaze.example.definition.model.Order;
import com.google.greaze.example.service.definition.SampleJsonService;
import com.google.greaze.example.webservice.definition.TypedKeys;
import com.google.greaze.webservice.client.ServerConfig;
import com.google.greaze.webservice.client.WebServiceClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ExampleClient {

  /**
   * Server where the JSON service is deployed. localhost:8888 is the address when the service
   * is deployed to a local App engine instance. 
   */
  private static final String SERVER_BASE_URL = "http://localhost:8888/greazeexampleservice";

  private final WebServiceClient wsClient;
  public ExampleClient() {
    wsClient = new WebServiceClient(new ServerConfig(SERVER_BASE_URL)); 
  }

  public Order placeOrder(Cart cart, String authToken) {
    WebServiceCallSpec spec = SampleJsonService.PLACE_ORDER;
    Gson gson = spec.addTypeAdapters(new GsonBuilder()).create();
    
	HeaderMap requestHeaders = new HeaderMap.Builder(spec.getRequestSpec().getHeadersSpec())
	    .put(TypedKeys.Request.AUTH_TOKEN, authToken)
	    .build();
	UrlParams urlParams = new UrlParams.Builder(spec.getRequestSpec().getUrlParamsSpec())
	    .build();
	RequestBody requestBody = new RequestBody.Builder(spec.getRequestSpec().getBodySpec())
	    .put(TypedKeys.RequestBody.CART, cart)
	    .build();
	WebServiceRequest request = new WebServiceRequest(
	    HttpMethod.POST, requestHeaders, urlParams, requestBody, false);
	WebServiceResponse response = wsClient.getResponse(spec, request, gson);
	return response.getBody().get(TypedKeys.ResponseBody.ORDER);
  }

  public static void main(String[] args) {
    ExampleClient client = new ExampleClient();
    List<LineItem> lineItems = new ArrayList<LineItem>();
    lineItems.add(new LineItem("item1", 2, 1000000L, "USD"));
	Cart cart = new Cart(lineItems, "first last", "4111-1111-1111-1111");
	String authToken = "authToken";
	Order order = client.placeOrder(cart, authToken );
	System.out.print(order);
  }
}
