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
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("node")
public class Node {
    @XStreamAsAttribute()
    private String name;
    @XStreamAlias("start-time")
    private String startTime;
    @XStreamAlias("jvm-memory-matrix")
    private JvmMemoryMatrix jvmMemoryMatrix;
    @XStreamAlias("jvm-gc-matrix")
    private JvmGCMatrix jvmGCMatrix;
    @XStreamAlias("jvm-thread-matrix")
    private JvmThreadMatrix jvmThreadMatrix;
    @XStreamImplicit(itemFieldName = "database-matrix")
    private List<DatabaseMatrix> databaseMatrixes;
    @XStreamAlias("processflow")
    private ProcessFlow processFlow;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public JvmMemoryMatrix getJvmMemoryMatrix() {
        return jvmMemoryMatrix;
    }

    public void setJvmMemoryMatrix(JvmMemoryMatrix jvmMemoryMatrix) {
        this.jvmMemoryMatrix = jvmMemoryMatrix;
    }

    public JvmGCMatrix getJvmGCMatrix() {
        return jvmGCMatrix;
    }

    public void setJvmGCMatrix(JvmGCMatrix jvmGCMatrix) {
        this.jvmGCMatrix = jvmGCMatrix;
    }

    public JvmThreadMatrix getJvmThreadMatrix() {
        return jvmThreadMatrix;
    }

    public void setJvmThreadMatrix(JvmThreadMatrix jvmThreadMatrix) {
        this.jvmThreadMatrix = jvmThreadMatrix;
    }

    public List<DatabaseMatrix> getDatabaseMatrixes() {
        return databaseMatrixes;
    }

    public void setDatabaseMatrixes(List<DatabaseMatrix> databaseMatrixes) {
        this.databaseMatrixes = databaseMatrixes;
    }

    public ProcessFlow getProcessFlow() {
        return processFlow;
    }

    public void setProcessFlow(ProcessFlow processFlow) {
        this.processFlow = processFlow;
    }
}
