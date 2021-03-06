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
package com.google.greaze.end2end.definition;

import com.google.greaze.definition.rest.Id;
import com.google.greaze.definition.rest.RestResourceImpl;

/**
 * Test fixture for an employee resource
 * 
 * @author Inderjeet Singh
 */
public class Employee extends RestResourceImpl<Employee> {
  private static final long serialVersionUID = 8675883750972755571L;
  private final String name;

  public Employee() {
    this(null, null);
  }

  public Employee(String name) {
    this(null, name);
  }

  public Employee(Id<Employee> id, String name) {
    super(id);
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return String.format("%d,%s", Id.getValue(id), name);
  }
}