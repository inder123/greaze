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
 * Unit test for {@link MetaData}
 *
 * @author Inderjeet Singh
 */
public class MetaDataTest extends TestCase {

  private Gson gson;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    this.gson = new GsonBuilder()
      .registerTypeHierarchyAdapter(MetaDataBase.class, new MetaDataBase.GsonTypeAdapter())
      .create();
  }

  public void testSerialize() {
    MetaData<MyResource> metaData = new MetaData<MyResource>();
    metaData.putBoolean("booleanValue", true);
    String json = gson.toJson(metaData);
    assertTrue(json.contains("booleanValue"));
    assertTrue(json.contains("true"));
  }

  public void testDeserialize() {
    String json = "{booleanValue:true}";
    Type metaDataType = new TypeToken<MetaData<MyResource>>(){}.getType();
    MetaData<MyResource> metaData = gson.fromJson(json, metaDataType);
    assertTrue(metaData.getBoolean("booleanValue"));
  }

  @SuppressWarnings("rawtypes")
  public void testSerializeRaw() {
    MetaData metaData = new MetaData();
    metaData.putString("stringValue", "foo bar");
    String json = gson.toJson(metaData);
    assertTrue(json.contains("stringValue"));
    assertTrue(json.contains("foo bar"));
  }

  @SuppressWarnings("rawtypes")
  public void testDeserializeRaw() {
    String json = "{stringValue:'bar bar'}";
    MetaData metaData = gson.fromJson(json, MetaData.class);
    assertEquals("bar bar", metaData.getString("stringValue"));
  }

  private static class MyResource extends RestResourceImpl<MyResource> {
  }
}
