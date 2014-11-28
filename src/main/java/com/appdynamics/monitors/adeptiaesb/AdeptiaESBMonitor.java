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
package com.appdynamics.monitors.adeptiaesb;


import com.appdynamics.TaskInputArgs;
import com.appdynamics.extensions.PathResolver;
import com.appdynamics.extensions.http.SimpleHttpClient;
import com.appdynamics.extensions.yml.YmlReader;
import com.appdynamics.monitors.adeptiaesb.config.Configuration;
import com.appdynamics.monitors.adeptiaesb.http.AdeptiaStatsRequester;
import com.appdynamics.monitors.adeptiaesb.stats.AdeptiaClusterStats;
import com.appdynamics.monitors.adeptiaesb.stats.AdeptiaKernelStats;
import com.appdynamics.monitors.adeptiaesb.stats.AdeptiaWebRunnerStats;
import com.google.common.base.Strings;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AdeptiaESBMonitor extends AManagedMonitor {

    private static final Logger logger = Logger.getLogger(AdeptiaESBMonitor.class);

    private static final String CONFIG_ARG = "config-file";


    public AdeptiaESBMonitor() {
        String version = getClass().getPackage().getImplementationTitle();
        String msg = String.format("Using Monitor Version [%s]", version);
        logger.info(msg);
        System.out.println(msg);
    }

    public TaskOutput execute(Map<String, String> taskArguments, TaskExecutionContext executionContext) throws TaskExecutionException {
        if (taskArguments != null) {
            logger.info("Starting the Adeptia Monitoring Task");
            String configFilename = getConfigFilename(taskArguments.get(CONFIG_ARG));
            try {
                Configuration config = YmlReader.readFromFile(configFilename, Configuration.class);
                Map<String, String> clientArguments = buildHttpClientArguments(config);

                SimpleHttpClient httpClient = SimpleHttpClient.builder(clientArguments).build();

                AdeptiaStatsRequester adeptiaStatsRequester = new AdeptiaStatsRequester();
                String jSessionId = adeptiaStatsRequester.initRequest(httpClient, config.getUsername(), config.getPassword());

                SimpleHttpClient statsHttpClient = SimpleHttpClient.builder(clientArguments).build();
                AdeptiaClusterStats adeptiaClusterStats = new AdeptiaClusterStats();
                Map<String, String> clusterStats = adeptiaClusterStats.getStats(statsHttpClient, jSessionId, adeptiaStatsRequester);
                printMetrics(config, clusterStats);

                AdeptiaKernelStats adeptiaKernelStats = new AdeptiaKernelStats();
                Map<String, String> kernelStats = adeptiaKernelStats.getStats(statsHttpClient, jSessionId, adeptiaStatsRequester);
                printMetrics(config, kernelStats);

                AdeptiaWebRunnerStats adeptiaWebRunnerStats = new AdeptiaWebRunnerStats();
                Map<String, String> webRunnerStats = adeptiaWebRunnerStats.getStats(statsHttpClient, jSessionId, adeptiaStatsRequester);
                printMetrics(config, webRunnerStats);


                logger.info("Adeptia monitoring task completed successfully.");
                return new TaskOutput("Adeptia monitoring task completed successfully.");
            } catch (Exception e) {
                logger.error("Metrics collection failed", e);
            }
        }
        throw new TaskExecutionException("Adeptia monitoring task completed with failures.");
    }

    private void printMetrics(Configuration config, Map<String, String> resourceStats) {
        String metricPrefix = config.getMetricPrefix();
        for (Map.Entry<String, String> statsEntry : resourceStats.entrySet()) {
            String value = statsEntry.getValue();
            String key = metricPrefix + statsEntry.getKey();
            try {
                double metricValue = Double.parseDouble(value.trim());
                print(key, metricValue);
            } catch (NumberFormatException e) {
                logger.error("Value of metric [" + key + "] can not be converted to number, Ignoring the it.");
            }
        }
    }

    private void print(String key, double metricValue) {
        MetricWriter metricWriter = super.getMetricWriter(key, MetricWriter.METRIC_AGGREGATION_TYPE_AVERAGE, MetricWriter.METRIC_TIME_ROLLUP_TYPE_AVERAGE, MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_COLLECTIVE
        );
        metricWriter.printMetric(String.valueOf(Math.round(metricValue)));
    }

    private String getConfigFilename(String filename) {
        if (filename == null) {
            return "";
        }
        // for absolute paths
        if (new File(filename).exists()) {
            return filename;
        }
        // for relative paths
        File jarPath = PathResolver.resolveDirectory(AManagedMonitor.class);
        String configFileName = "";
        if (!Strings.isNullOrEmpty(filename)) {
            configFileName = jarPath + File.separator + filename;
        }
        return configFileName;
    }

    private Map<String, String> buildHttpClientArguments(Configuration config) {
        Map<String, String> clientArgs = new HashMap<String, String>();
        clientArgs.put(TaskInputArgs.USE_SSL, Boolean.toString(config.getUsessl()));
        clientArgs.put(TaskInputArgs.HOST, config.getHost());
        clientArgs.put(TaskInputArgs.PORT, String.valueOf(config.getPort()));
        clientArgs.put(TaskInputArgs.USER, config.getUsername());
        clientArgs.put(TaskInputArgs.PASSWORD, config.getPassword());

        //set optional proxy params
        clientArgs.put(TaskInputArgs.PROXY_HOST, config.getProxyHost());
        clientArgs.put(TaskInputArgs.PROXY_PORT, String.valueOf(config.getProxyPort()));
        clientArgs.put(TaskInputArgs.PROXY_USER, config.getProxyUsername());
        clientArgs.put(TaskInputArgs.PROXY_PASSWORD, config.getProxyPassword());

        return clientArgs;
    }

    public static void main(String[] args) throws TaskExecutionException {
        Map<String, String> clientArgs = new HashMap<String, String>();
        clientArgs.put(CONFIG_ARG, "/home/satish/AppDynamics/Code/extensions/adeptiaesb-monitoring-extension/src/main/resources/config/config.yml");

        AdeptiaESBMonitor adeptiaESBMonitor = new AdeptiaESBMonitor();
        adeptiaESBMonitor.execute(clientArgs, null);
    }
}