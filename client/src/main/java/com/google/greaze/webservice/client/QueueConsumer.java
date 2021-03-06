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
package com.google.greaze.webservice.client;

import java.util.concurrent.BlockingQueue;

import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.definition.webservice.WebServiceCall;
import com.google.greaze.definition.webservice.WebServiceResponse;
import com.google.gson.Gson;

/**
 * A consumer that executes in its own thread consuming queue entries and invoking web-service calls
 *
 * @author inder
 */
final class QueueConsumer implements Runnable {

  private final BlockingQueue<QueueEntry> queue;
  private final WebServiceClient client;
  private final Gson gson;

  QueueConsumer(BlockingQueue<QueueEntry> queue, WebServiceClient client, Gson gson) {
    this.queue = queue;
    this.client = client;
    this.gson = gson;
  }

  @Override
  public void run() {
    try {
      while(true) {
        consume(queue.take());
      }
    } catch (InterruptedException e) {
      // exit
    }
  }

  private void consume(QueueEntry entry) {
    try {
      WebServiceResponse response = client.getResponse(entry.callSpec, entry.request, gson);
      WebServiceCall call = new WebServiceCall(entry.callSpec, entry.request, response);
      if (entry.responseCallback != null) {
        entry.responseCallback.handleResponse(call);
      }
    } catch (WebServiceSystemException e) {
      if (entry.responseCallback != null) {
        entry.responseCallback.handleError(e, entry.request, entry.callSpec);
      }
    }
  }  
}
