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
package com.google.greaze.definition.rest;

import java.lang.reflect.Type;

import junit.framework.TestCase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Unit test for {@link MetaDataValueBased}
 *
 * @author Inderjeet Singh
 */
public class MetaDataValueBasedTest extends TestCase {

  private Gson gson;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    this.gson = new GsonBuilder()
      .registerTypeHierarchyAdapter(MetaData.class, new MetaData.GsonTypeAdapter())
      .create();
  }

  public void testSerialize() {
    MetaDataValueBased<MyResource> metaData = MetaDataValueBased.create();
    metaData.putBoolean("booleanValue", true);
    String json = gson.toJson(metaData);
    System.out.println(json);
  }

  public void testDeserialize() {
    String json = "{booleanValue:true}";
    Type metaDataType = new TypeToken<MetaDataValueBased<MyResource>>(){}.getType();
    MetaDataValueBased<MyResource> metaData = gson.fromJson(json, metaDataType);
    assertTrue(metaData.getBoolean("booleanValue"));
  }

  private static class MyResource extends RestResourceImpl<MyResource> {
  }
}
