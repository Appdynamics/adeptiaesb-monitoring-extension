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
import com.appdynamics.monitors.adeptiaesb.beans.Monitor;
import com.appdynamics.monitors.adeptiaesb.http.AdeptiaStatsRequester;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import com.thoughtworks.xstream.XStream;

import java.util.Map;

public class AdeptiaWebRunnerStats extends AdeptiaStats {

    @Override
    public Map<String, String> getStats(SimpleHttpClient httpClient, String jSessionId, AdeptiaStatsRequester adeptiaStatsRequester) throws TaskExecutionException {

        String webRunnerStatsString = adeptiaStatsRequester.requestStats(httpClient, jSessionId, "webrunner");

        XStream xStream = new XStream();
        xStream.ignoreUnknownElements();
        xStream.processAnnotations(Monitor.class);

        Monitor webRunnerStats = (Monitor) xStream.fromXML(webRunnerStatsString);
        Map<String, String> stats = getStatsFromMonitor(webRunnerStats, "WebRunner");
        return stats;
    }
}