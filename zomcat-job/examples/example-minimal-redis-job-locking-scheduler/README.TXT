Example Job-Framework Project
------------------------------

This is a very simple example for https://devwiki.zalando.de/Jobs_Framework
It shows a job using redis for job locking.

* build everything by calling "mvn clean install"
* go into "backend" and start it with "mvn clean tomcat:run-war"
Every 15 seconds a "Hello world" message should appear.
NOTE: System.out is forbidden in web projects, here it's used only for demonstration purposes.

Building everything by yourself from an empty project:
* Add zomcat-job, zomcat-monitor and zomcat-appconfig to the dependencies in your pom file
* Add the following entries into your application context (backendContext.xml)
    <!-- This is the Jobs Framework inclusion -->
    <import resource="classpath:META-INF/zomcat/scheduler.xml" />
    <!-- Redis configuration -->
    <import resource="classpath:META-INF/zomcat/redis-job-locking-manager.xml"/>
* The resource 'scheduler.conf' will contain the job scheduling configuration
 
Job monitor servlet: http://localhost:8080/example-minimal-redis-job-locking-scheduler-backend/jobs.monitor2
