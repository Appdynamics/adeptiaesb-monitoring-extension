/**
 * Copyright 2014 AppDynamics, Inc.
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
package com.appdynamics.monitors.adeptiaesb.beans;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;


@XStreamAlias("jvm-gc-matrix")
public class JvmGCMatrix {
    @XStreamImplicit(itemFieldName = "garbage-collection")
    private List<GarbageCollection> garbageCollections;

    public List<GarbageCollection> getGarbageCollections() {
        return garbageCollections;
    }

    public void setGarbageCollections(List<GarbageCollection> garbageCollections) {
        this.garbageCollections = garbageCollections;
    }
}