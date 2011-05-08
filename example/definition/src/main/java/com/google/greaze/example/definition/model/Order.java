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
package com.google.greaze.example.definition.model;

import com.google.greaze.definition.rest.RestResourceImpl;

/**
 * An order rest resource
 *
 * @author Inderjeet Singh
 */
public class Order extends RestResourceImpl<Order> {
  public final Cart postedCart;
  public final String orderNumber;

  // TODO(inder): remove these when Gson's UnsafeAllocator is made to work on App Engine
  private Order() {
    this(null, null);
  }

  public Order(Cart postedCart, String orderNumber) {
    this.postedCart = postedCart;
    this.orderNumber = orderNumber;
  }

  public Cart getPostedCart() {
    return postedCart;
  }

  public String getOrderNumber() {
    return orderNumber;
  }

  @Override
  public String toString() {
    return String.format("{orderNumber: %s, postedCart: %s}", orderNumber, postedCart);
  }
}
