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
package com.google.greaze.definition.rest.query;

import com.google.greaze.definition.rest.RestResource;
import com.google.greaze.definition.rest.ValueBasedId;

/**
 * A query for a list of resources
 *
 * @author Inderjeet Singh
 */
public interface ResourceQueryValueBased<
    R extends RestResource<ValueBasedId<R>, R>, Q extends ResourceQueryParams>
        extends ResourceQuery<ValueBasedId<R>, R, Q>{
}
