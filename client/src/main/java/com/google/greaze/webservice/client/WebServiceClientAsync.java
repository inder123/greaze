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
import java.util.concurrent.LinkedBlockingQueue;

import com.google.greaze.definition.ErrorReason;
import com.google.greaze.definition.WebServiceSystemException;
import com.google.greaze.definition.webservice.WebServiceCallSpec;
import com.google.greaze.definition.webservice.WebServiceRequest;
import com.google.gson.Gson;

/**
 * A client for invoking a JSON-based Web-service in an asynchronous manner. The call is queued,
 * and control returns to the caller. A separate thread executes the call, and invokes the
 * client-supplied callback with results.
 *  
 * @author inder
 */
public class WebServiceClientAsync {

  private final BlockingQueue<QueueEntry> queue;
  private final boolean threadPerTask;
  private final TaskExecutor executor;

  public WebServiceClientAsync(ServerConfig serverConfig, Gson gson) {
    this(new WebServiceClient(serverConfig), gson);
  }

  public WebServiceClientAsync(WebServiceClient client, Gson gson) {
    queue = new LinkedBlockingQueue<QueueEntry>();
    this.threadPerTask = true;
    QueueConsumer consumer = new QueueConsumer(queue, client, gson);
    executor = getExecutor();
    executor.execute(consumer);
  }

  private TaskExecutor getExecutor() {
    return threadPerTask ? new ThreadPerTaskExecutor() : new SingleThreadExecutor();
  }

  public void callAsync(WebServiceCallSpec callSpec, WebServiceRequest request,
      ResponseCallback responseCallback) {
    try {
      queue.put(new QueueEntry(callSpec, request, responseCallback));
    } catch (InterruptedException e) {
      throw new WebServiceSystemException(ErrorReason.UNEXPECTED_RETRYABLE_ERROR, e);
    }
  }
  
  public void shutdownNow() {
    executor.shutdownNow();
  }
}
