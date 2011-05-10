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
package com.google.greaze.example.service.definition;

import com.google.greaze.definition.CallPathParser;
import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.rest.RestCallSpec;
import com.google.greaze.definition.rest.RestCallSpecMap;
import com.google.greaze.definition.webservice.WebServiceCallSpec;
import com.google.greaze.example.definition.model.Cart;
import com.google.greaze.example.definition.model.Order;
import com.google.greaze.example.webservice.definition.TypedKeys;

/**
 * An example of a web-service definition
 *
 * @author inder
 */
public class SampleJsonService {

  /**
   * Server where the JSON service is deployed. localhost:8888 is the address when the service
   * is deployed to a local App engine instance. 
   */
  public static final String SERVER_BASE_URL = "http://localhost:8888/greazeexampleservice";
  
  /**
   * This value should match with the prefix used in {@link ServicePaths}
   */
  public static final String RESOURCE_PREFIX = "/rest";

  public static final double CURRENT_VERSION = 1.0;

  public static final WebServiceCallSpec PLACE_ORDER = new WebServiceCallSpec.Builder(
    new CallPathParser("", false, "/placeOrder").parse("/placeOrder"))
      .setMapBody()
      .setVersion(CURRENT_VERSION)
      .supportsHttpMethod(HttpMethod.POST)
      .addRequestParam(TypedKeys.Request.AUTH_TOKEN)
      .addRequestBodyParam(TypedKeys.RequestBody.CART)
      .addResponseBodyParam(TypedKeys.ResponseBody.ORDER)
      .build();

  public static final RestCallSpec CART_SPEC =
    new RestCallSpec.Builder(ServicePaths.CART.getCallPath(), Cart.class)
      .setVersion(CURRENT_VERSION)
      .build();

  public static final RestCallSpec ORDER_SPEC =
    new RestCallSpec.Builder(ServicePaths.ORDER.getCallPath(), Order.class)
      .setVersion(CURRENT_VERSION)
      .build(); 

  public static final RestCallSpecMap REST_CALL_SPEC_MAP =
    new RestCallSpecMap.Builder()
      .set(ServicePaths.CART.getCallPath(), CART_SPEC)
      .set(ServicePaths.ORDER.getCallPath(), ORDER_SPEC)
      .build();
}
