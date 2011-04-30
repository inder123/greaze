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

import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.webservice.ResponseBody;
import com.google.greaze.definition.webservice.ResponseSpec;
import com.google.greaze.definition.webservice.WebServiceRequest;
import com.google.greaze.definition.webservice.WebServiceResponse;
import com.google.greaze.example.definition.model.Cart;
import com.google.greaze.example.definition.model.Order;
import com.google.greaze.example.webservice.definition.TypedKeys;
import com.google.greaze.server.WebServiceDispatcher;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Dispatcher that extends the logic for the application
 *
 * @author Inderjeet Singh
 */
public final class WebServiceDispatcherExample extends WebServiceDispatcher {

  @Inject
  public WebServiceDispatcherExample(Injector injector) {
    super(injector);
  }

  @Override
  protected WebServiceResponse buildResponse(ResponseSpec responseSpec,
      WebServiceRequest webServiceRequest) {
    Cart cart = webServiceRequest.getBody().get(TypedKeys.RequestBody.CART);
    String authToken = webServiceRequest.getHeader(TypedKeys.Request.AUTH_TOKEN);

    Order order = placeOrder(cart, authToken);

    // Empty headers per the spec
    HeaderMap responseHeaders = new HeaderMap.Builder(responseSpec.getHeadersSpec()).build();
    ResponseBody responseBody = new ResponseBody.Builder(responseSpec.getBodySpec())
        .put(TypedKeys.ResponseBody.ORDER, order)
        .build();
    return new WebServiceResponse(responseHeaders, responseBody);
  }
  
  private Order placeOrder(Cart cart, String authToken) {
    // Create an order, in this case a dummy one.
    return new Order(cart, "Order123");
  }
}
