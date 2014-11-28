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

@XStreamAlias("heap-memory")
public class HeapMemory {
    @XStreamAlias("max-memory")
    private String maxMemory;
    @XStreamAlias("used-memory")
    private String usedMemory;
    @XStreamAlias("committed-memory")
    private String committedMemory;
    @XStreamAlias("init-memory")
    private String initMemory;

    public String getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(String maxMemory) {
        this.maxMemory = maxMemory;
    }

    public String getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(String usedMemory) {
        this.usedMemory = usedMemory;
    }

    public String getCommittedMemory() {
        return committedMemory;
    }

    public void setCommittedMemory(String committedMemory) {
        this.committedMemory = committedMemory;
    }

    public String getInitMemory() {
        return initMemory;
    }

    public void setInitMemory(String initMemory) {
        this.initMemory = initMemory;
    }
}
