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
package com.appdynamics.monitors.adeptiaesb.stats;

import com.appdynamics.extensions.http.SimpleHttpClient;
import com.appdynamics.monitors.adeptiaesb.beans.DatabaseMatrix;
import com.appdynamics.monitors.adeptiaesb.beans.GarbageCollection;
import com.appdynamics.monitors.adeptiaesb.beans.HeapMemory;
import com.appdynamics.monitors.adeptiaesb.beans.JvmGCMatrix;
import com.appdynamics.monitors.adeptiaesb.beans.JvmMemoryMatrix;
import com.appdynamics.monitors.adeptiaesb.beans.JvmThreadMatrix;
import com.appdynamics.monitors.adeptiaesb.beans.Monitor;
import com.appdynamics.monitors.adeptiaesb.beans.Node;
import com.appdynamics.monitors.adeptiaesb.beans.NonHeapMemory;
import com.appdynamics.monitors.adeptiaesb.beans.ProcessFlow;
import com.appdynamics.monitors.adeptiaesb.beans.Record;
import com.appdynamics.monitors.adeptiaesb.http.AdeptiaStatsRequester;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AdeptiaStats {

    private static final Logger logger = Logger.getLogger(AdeptiaStats.class);

    public abstract Map<String, String> getStats(SimpleHttpClient httpClient, String jSessionId, AdeptiaStatsRequester adeptiaStatsRequester) throws TaskExecutionException;

    protected Map<String, String> getStatsFromMonitor(Monitor monitor, String statPrefix) {

        Map<String, String> stats = new HashMap<String, String>();

        if (monitor == null || monitor.getNodes() == null || monitor.getNodes().size() <= 0) {
            return stats;
        }

        List<Node> nodes = monitor.getNodes();

        for (Node node : nodes) {
            String nodeName = node.getName();

            List<DatabaseMatrix> databaseMatrixes = node.getDatabaseMatrixes();
            populateDatabaseMatrix(databaseMatrixes, stats, nodeName, statPrefix);

            JvmGCMatrix jvmGCMatrix = node.getJvmGCMatrix();
            populateJvmGCMatrix(jvmGCMatrix, stats, nodeName, statPrefix);

            JvmMemoryMatrix jvmMemoryMatrix = node.getJvmMemoryMatrix();
            populateJvmMemoryMatrix(jvmMemoryMatrix, stats, nodeName, statPrefix);

            JvmThreadMatrix jvmThreadMatrix = node.getJvmThreadMatrix();
            populateJvmThreadMatrix(jvmThreadMatrix, stats, nodeName, statPrefix);

            ProcessFlow processFlow = node.getProcessFlow();
            populateProcessFlowMatrix(processFlow, stats, nodeName, statPrefix);
        }
        return stats;
    }

    private void populateProcessFlowMatrix(ProcessFlow processFlow, Map<String, String> stats, String nodeName, String statPrefix) {
        if (processFlow == null || processFlow.getRecords() == null || processFlow.getRecords().size() <= 0) {
            return;
        }

        List<Record> records = processFlow.getRecords();
        for (Record record : records) {
            stats.put(statPrefix+"|" + nodeName + "|Processflow|" + record.getStatus(), record.getCount());
        }
    }

    private void populateJvmThreadMatrix(JvmThreadMatrix jvmThreadMatrix, Map<String, String> stats, String nodeName, String statPrefix) {
        if (jvmThreadMatrix == null) {
            return;
        }

        stats.put(statPrefix+"|" + nodeName + "|Jvm Thread Matrix|Peak Thread Count", jvmThreadMatrix.getPeakThreadCount());
        stats.put(statPrefix+"|" + nodeName + "|Jvm Thread Matrix|Live Thread Count", jvmThreadMatrix.getLiveThreadCount());
        stats.put(statPrefix+"|" + nodeName + "|Jvm Thread Matrix|Thread Deadlock Count", jvmThreadMatrix.getThreadDeadlockCount());
    }

    private void populateJvmMemoryMatrix(JvmMemoryMatrix jvmMemoryMatrix, Map<String, String> stats, String nodeName, String statPrefix) {
        if (jvmMemoryMatrix == null) {
            return;
        }

        HeapMemory heapMemory = jvmMemoryMatrix.getHeapMemory();

        stats.put(statPrefix+"|" + nodeName + "|JVM Memory Matrix|Heap Memory|Max Memory", heapMemory.getMaxMemory());
        stats.put(statPrefix+"|" + nodeName + "|JVM Memory Matrix|Heap Memory|Used Memory", heapMemory.getUsedMemory());
        stats.put(statPrefix+"|" + nodeName + "|JVM Memory Matrix|Heap Memory|Committed Memory", heapMemory.getCommittedMemory());
        stats.put(statPrefix+"|" + nodeName + "|JVM Memory Matrix|Heap Memory|Init Memory", heapMemory.getInitMemory());

        NonHeapMemory nonHeapMemory = jvmMemoryMatrix.getNonHeapMemory();

        stats.put(statPrefix+"|" + nodeName + "|JVM Memory Matrix|Non Heap Memory|Max Memory", nonHeapMemory.getMaxMemory());
        stats.put(statPrefix+"|" + nodeName + "|JVM Memory Matrix|Non Heap Memory|Used Memory", nonHeapMemory.getUsedMemory());
        stats.put(statPrefix+"|" + nodeName + "|JVM Memory Matrix|Non Heap Memory|Committed Memory", nonHeapMemory.getCommittedMemory());
        stats.put(statPrefix+"|" + nodeName + "|JVM Memory Matrix|Non Heap Memory|Init Memory", nonHeapMemory.getInitMemory());
    }

    private void populateJvmGCMatrix(JvmGCMatrix jvmGCMatrix, Map<String, String> stats, String nodeName, String statPrefix) {
        if (jvmGCMatrix == null || jvmGCMatrix.getGarbageCollections() == null || jvmGCMatrix.getGarbageCollections().size() <= 0) {
            return;
        }

        List<GarbageCollection> garbageCollections = jvmGCMatrix.getGarbageCollections();
        for (GarbageCollection garbageCollection : garbageCollections) {
            String garbageCollectorName = garbageCollection.getGarbageCollectorName();
            stats.put(statPrefix+"|" + nodeName + "|JVM GC Matrix|" + garbageCollectorName + "|Garbage Collections", garbageCollection.getGarbageCollections());
            try {
                double timeSpentInMin = Double.parseDouble(garbageCollection.getTotalTimeSpent());
                double timeSpentInMillis = timeSpentInMin * 60000;
                stats.put(statPrefix+"|" + nodeName + "|JVM GC Matrix|" + garbageCollectorName + "|Total Time Spent(In millis)", String.valueOf(timeSpentInMillis));
            } catch (NumberFormatException e) {
                logger.error("Unable to parse the number [" + garbageCollection.getTotalTimeSpent() + "]", e);
            }
        }
    }

    private void populateDatabaseMatrix(List<DatabaseMatrix> databaseMatrixes, Map<String, String> stats, String nodeName, String statPrefix) {

        if (databaseMatrixes == null || databaseMatrixes.size() <= 0) {
            return;
        }

        for (DatabaseMatrix databaseMatrix : databaseMatrixes) {
            String status = databaseMatrix.getStatus();
            String type = databaseMatrix.getType();
            stats.put(statPrefix+"|" + nodeName + "|Database Matrix|" + type + "|Status", "UP".equalsIgnoreCase(status) ? "1" : "0");
            stats.put(statPrefix+"|" + nodeName + "|Database Matrix|" + type + "|Active Connection Count", databaseMatrix.getActiveConnectionCount());
            stats.put(statPrefix+"|" + nodeName + "|Database Matrix|" + type + "|Idle Connection Count", databaseMatrix.getIdleConnectionCount());
        }
    }
}