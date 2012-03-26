/*
 * Copyright (C) 2012 Google Inc.
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
package com.google.greaze.example.server.cart;

import java.util.List;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.greaze.definition.rest.Id;
import com.google.greaze.example.definition.model.Cart;
import com.google.greaze.example.definition.model.LineItem;
import com.google.greaze.rest.server.RepositoryInMemory;

/**
 * An example of a custom repository for a resource.
 *
 * @author Inderjeet Singh
 */
public final class RepositoryServerCarts extends RepositoryInMemory<Cart> {

  public RepositoryServerCarts() {
    // Add some canned data for testing the service
    put(createCart("1", "candy", 1));
    put(createCart("2", "balloon", 2));
    put(createCart("3", "rattle", 3));
  }

  private static Cart createCart(String cartId, String itemName, int priceInUsd) {
    List<LineItem> lineItems =
        Lists.newArrayList(new LineItem(itemName, 1, priceInUsd*1000000, "USD"));
    return new Cart(Id.<Cart>get(cartId), lineItems, "inder", "4111-1111-1111-1111");
  }
}
