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
package com.appdynamics.monitors.adeptiaesb.http;


import com.appdynamics.extensions.http.Response;
import com.appdynamics.extensions.http.SimpleHttpClient;
import com.appdynamics.extensions.http.WebTarget;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.apache.log4j.Logger;


public class AdeptiaStatsRequester {

    private static final Logger logger = Logger.getLogger(AdeptiaStatsRequester.class);

    private static String STATS_URL = "/adeptia/control/monitorMatrix";

    public String initRequest(SimpleHttpClient simpleHttpClient, String user, String password) {

        WebTarget controlRequest = simpleHttpClient.target().path("/adeptia/control/");
        Response controlResponse = controlRequest.get();
        String controlCookieHeader = controlResponse.getHeader("SET-COOKIE");
        String controlJSessionID = getSessionIdFromCookie(controlCookieHeader);

        WebTarget loginRequest = simpleHttpClient.target().path("/adeptia/control/login.jsp");
        loginRequest.header("Content-Type", "application/x-www-form-urlencoded");
        loginRequest.header("Cookie", "JSESSIONID=" + controlJSessionID + ";");
        Response loginResponse = loginRequest.post("user=" + user + "&password=" + password);
        String loginCookieHeader = loginResponse.getHeader("SET-COOKIE");
        return getSessionIdFromCookie(loginCookieHeader);
    }

    private String getSessionIdFromCookie(String header) {
        String[] split = header.split(";");
        for (String s : split) {
            if (s.contains("JSESSIONID")) {
                return s;
            }
        }
        return null;
    }

    public String requestStats(SimpleHttpClient simpleHttpClient, String jSessionId, String monitorFlag) throws TaskExecutionException {
        WebTarget target = simpleHttpClient.target().path(STATS_URL);

        target.header("Cookie", jSessionId);
        target.query("monitorFlag", monitorFlag).query("matrixFlag", "all");
        Response response = target.get();

        String contentHeader = response.getHeader("Content-Type");
        if (response.getStatus() != 200 || "text/html".contains(contentHeader)) {
            logger.error("Unable to get the " + monitorFlag + " stats");
            throw new TaskExecutionException("Unable to get the " + monitorFlag + " stats");
        }

        return response.string();
    }
}