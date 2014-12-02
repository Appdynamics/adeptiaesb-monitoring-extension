# AppDynamics Adeptia ESB Monitoring Extension

This extension works only with the standalone machine agent.

##Use Case

Adeptia ESB Suite is an enterprise-class software product that is designed to integrate ANY APPLICATION with ANY DATA at ANY LOCATION. It combines process centric and services (SOA) based approach with application and partner integration. ESB Suite easily and quickly automates data flows and connects applications using industry-specific standards.
This extension monitors Adeptia ESB instance and collects useful statistics and reports to AppDynamics Controller.

##Installation

1. Run 'mvn clean install' from the adeptiaesb-monitoring-extension directory and find the AdeptiaESBMonitor.zip in the 'target' directory.
2. Unzip AdeptiaESBMonitor.zip and copy the 'AdeptiaESBMonitor' directory to `<MACHINE_AGENT_HOME>/monitors/`
3. Configure the extension by referring to the below section.
5. Restart the Machine Agent.

In the AppDynamics Metric Browser, look for: Application Infrastructure Performance  | \<Tier\> | Custom Metrics | Adeptia in case of default metric path

## Configuration

Note : Please make sure not to use tab (\t) while editing yaml files. You can validate the yaml file using a [yaml validator](http://yamllint.com/)

1. Configure the Adeptia ESB Extension by editing the config.yml file in `<MACHINE_AGENT_HOME>/monitors/AdeptiaESBMonitor/`.
2. Specify the Adeptia ESB instance host, port, username and password in the config.yml.
   For eg.
   ```
        # Adeptia ESB instance particulars
        host: "localhost"
        port: 8080
        usessl: false
        username: "admin"
        password: "indigo1"
        #Start optional parameters
        #   proxyHost:""
        #   proxyPort:
        #   proxyUsername:""
        #   proxyPassword:""
        #End optional parameters

        #prefix used to show up metrics in AppDynamics
        metricPrefix:  "Custom Metrics|Adeptia|"

   ```

3. Configure the path to the config.yml file by editing the <task-arguments> in the monitor.xml file in the `<MACHINE_AGENT_HOME>/monitors/AdeptiaESBMonitor/` directory. Below is the sample

     ```
     <task-arguments>
         <!-- config file-->
         <argument name="config-file" is-required="true" default-value="monitors/AdeptiaESBMonitor/config.yml" />
          ....
     </task-arguments>
    ```



##Metrics

###Kernel and Webrunner metrics

####Database Matrix
* {Host}|Database Matrix|{log/repository}|Active Connection Count
* {Host}|Database Matrix|{log/repository}|Idle Connection Count
* {Host}|Database Matrix|{log/repository}|Status

####JVM GC Matrix
* {Host}|JVM GC Matrix|{GC Name}|Garbage Collections
* {Host}|JVM GC Matrix|{GC Name}|Total Time Spent(In millis)

####JVM Memory Matrix
* {Host}|JVM Memory Matrix|Heap/Non Heap Memory|Committed Memory
* {Host}|JVM Memory Matrix|Heap/Non Heap Memory|Init Memory
* {Host}|JVM Memory Matrix|Heap/Non Heap Memory|Max Memory
* {Host}|JVM Memory Matrix|Heap/Non Heap Memory|Used Memory

####Jvm Thread Matrix
* {Host}|Jvm Thread Matrix|Live Thread Count
* {Host}|Jvm Thread Matrix|Peak Thread Count
* {Host}|Jvm Thread Matrix|Thread Deadlock Count

####Processflow
* {Host}|Processflow|Queued
* {Host}|Processflow|Running
* {Host}|Processflow|Waiting

###Cluster metrics
* {Host}|{Post}|Network Link
* {Host}|{Post}|Status

## Custom Dashboard
![](https://raw.githubusercontent.com/Appdynamics/adeptiaesb-monitoring-extension/master/AdeptiaESB.png)

##Contributing

Always feel free to fork and contribute any changes directly here on GitHub.

##Community

Find out more in the [AppSphere]() community.

##Support

For any questions or feature request, please contact [AppDynamics Center of Excellence](mailto:help@appdynamics.com).

