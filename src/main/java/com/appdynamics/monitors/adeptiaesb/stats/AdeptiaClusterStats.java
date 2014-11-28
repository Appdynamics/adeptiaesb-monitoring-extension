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
import com.appdynamics.monitors.adeptiaesb.beans.ClusterMonitor;
import com.appdynamics.monitors.adeptiaesb.beans.ClusterNode;
import com.appdynamics.monitors.adeptiaesb.http.AdeptiaStatsRequester;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import com.thoughtworks.xstream.XStream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdeptiaClusterStats extends AdeptiaStats {

    @Override
    public Map<String, String> getStats(SimpleHttpClient httpClient, String jSessionId, AdeptiaStatsRequester adeptiaStatsRequester) throws TaskExecutionException {

        String clusterStatsString = adeptiaStatsRequester.requestStats(httpClient, jSessionId, "cluster");

        XStream xStream = new XStream();
        xStream.ignoreUnknownElements();
        xStream.processAnnotations(ClusterMonitor.class);

        ClusterMonitor clusterMonitor = (ClusterMonitor) xStream.fromXML(clusterStatsString);

        Map<String, String> stats = populateStats(clusterMonitor);

        return stats;
    }

    private Map<String, String> populateStats(ClusterMonitor clusterMonitor) {

        Map<String, String> stats = new HashMap<String, String>();

        if (clusterMonitor == null || clusterMonitor.getClusterNodes() == null || clusterMonitor.getClusterNodes().size() <= 0) {
            return stats;
        }

        List<ClusterNode> clusterNodes = clusterMonitor.getClusterNodes();
        for (ClusterNode clusterNode : clusterNodes) {
            String nodeName = clusterNode.getName();
            String port = clusterNode.getPort();
            String nodePort = nodeName + ":" + port;

            String status = clusterNode.getStatus();
            String networkLink = clusterNode.getNetworkLink();

            stats.put("Cluster|" + nodePort + "|Status", "UP".equalsIgnoreCase(status) ? "1" : "0");
            stats.put("Cluster|" + nodePort + "|Network Link", "UP".equalsIgnoreCase(networkLink) ? "1" : "0");
        }
        return stats;
    }
}