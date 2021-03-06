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

import java.util.List;

import com.google.greaze.definition.rest.Id;
import com.google.greaze.definition.rest.RestResourceImpl;

/**
 * A cart that can be posted to the server
 * 
 * @author inder
 */
@SuppressWarnings("serial")
public class Cart extends RestResourceImpl<Cart> {
  private final List<LineItem> lineItems;
  private final String buyerName;
  private final String creditCard;

  // Needed to make it work on App Engine
  @SuppressWarnings("unused")
  private Cart() {
    this(null, null, null);
  }

  public Cart(List<LineItem> lineItems, String buyerName, String creditCard) {
    this(null, lineItems, buyerName, creditCard);
  }

  public Cart(Id<Cart> id, List<LineItem> lineItems, String buyerName, String creditCard) {
    super(id);
    this.lineItems = lineItems;
    this.buyerName = buyerName;
    this.creditCard = creditCard;
  }

  public List<LineItem> getLineItems() {
    return lineItems;
  }

  public String getBuyerName() {
    return buyerName;
  }

  public String getCreditCard() {
    return creditCard;
  }

  @Override
  public String toString() {
    return String.format("{buyerName: %s, creditCard: %s, lineItems:[%s]}",
        buyerName, creditCard, lineItems);
  }
}
