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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

import junit.framework.TestCase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Unit test for {@link Id}
 *
 * @author Inderjeet Singh
 */
public class IdTest extends TestCase {

  public void testRawTypeNotEqualToParameterizedOfConcreteType() {
    ParameterizedType type = (ParameterizedType) new TypeToken<Id<Foo>>(){}.getType(); 
    assertFalse(areEquivalentTypes(type, Id.class));
  }

  public void testRawTypeEqualToParameterizedOfWildcardType() {
    ParameterizedType fooType = (ParameterizedType) new TypeToken<Id<?>>(){}.getType(); 
    assertTrue(areEquivalentTypes(fooType, Id.class));
  }

  public void testStaticEquals() {
    Id<Foo> id1 = Id.get("3");
    Id<Foo> id2 = Id.get("3");
    Id<Foo> id3 = Id.get("4");
    assertTrue(Id.equals(id1, id2));
    assertFalse(Id.equals(null, id2));
    assertFalse(Id.equals(id1, null));
    assertTrue(Id.equals(null, null));
    assertFalse(Id.equals(id1, id3));
  }

  public void testJsonSerializationDeserialization() {
    Gson gson = new GsonBuilder().registerTypeAdapterFactory(new IdGsonTypeAdapterFactory()).create();
    Type type = new TypeToken<Id<Bar<Foo>>>() {}.getType();
    Id<Bar<Foo>> id = Id.get("abc");
    String json = gson.toJson(id, type);
    Id<Bar<Foo>> deserializedId = gson.fromJson(json, type);
    assertEquals(id, deserializedId);
  }

  public void testJavaSerializationDeserialization() throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(out);
    Id<Bar<Foo>> id = Id.get("abc");
    oos.writeObject(id);
    oos.close();
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(in);
    Object deserializedId = ois.readObject();
    assertEquals(id, deserializedId);
  }

  private static class Foo {
  }

  private static class Bar<T> {
  }

  /**
   * Visible for testing only
   */
  @SuppressWarnings("rawtypes")
  static boolean areEquivalentTypes(ParameterizedType type, Class clazz) {
    Class rawClass = (Class) type.getRawType();
    if (!clazz.equals(rawClass)) {
      return false;
    }
    for (Type typeVariable : type.getActualTypeArguments()) {
      if (typeVariable instanceof WildcardType) {
        continue;
      }
      // This is a real parameterized type, not just ?
      return false;
    }
    return true;
  }
}
