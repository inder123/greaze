/*
 * Copyright (C) 2012 Greaze Authors.
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
package com.google.greaze.definition;

/**
 * Central configuration to decide which log levels get logged for Greaze.
 *
 * @author Inderjeet Singh
 */
public final class LogConfig {

  /**
   * Set this to false to disable INFO log prints.
   */
  public static final boolean INFO = true;

  /**
   * Set this to false to disable FINE log prints.
   */
  public static final boolean FINE = false;

  /**
   * Set this to false to disable FINER log prints.
   */
  public static final boolean FINER = false;

  /**
   * Set this to false to disable FINEST log prints.
   */
  public static final boolean FINEST = false;
}
