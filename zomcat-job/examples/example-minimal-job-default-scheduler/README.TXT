Example Job-Framework Project
------------------------------

This is a very simple example for https://devwiki.zalando.de/Jobs_Framework
It shows a backend job service doing a simple "echo".

* build everything by calling "mvn install"
* go in "backend" and start it whit "mvn tomcat:run-war"
Every 15 seconds a "Hello world" message should appear.
NOTE: System.out is forbidden in web projects, here it's used only for demonstration purposes.


Building everything by yourself from an empty project:
* Add zomcat, zomcat-job, zomcat-monitor, zomcat-appconfig and appconfig to the dependencies in your pom file
* Add the job framework to your application context (backendContext.xml)
    <!-- This is the Jobs Framework inclusion -->
    <import resource="classpath:META-INF/zomcat/scheduler.xml" />
* Define your SProc provider and include your applicationConfig in your application context
* The resource 'scheduler.conf' will contain the job scheduling configuration


