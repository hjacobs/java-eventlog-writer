Example Job-Framework Project
------------------------------

This is a very simple example for https://devwiki.zalando.de/Jobs_Framework
It shows a backend job service doing a simple "echo".

* build everything by calling "mvn install -Pintegration-test"
* go into "backend" and start it whit "mvn tomcat:run-war"
Every 2 minutes a "Hello world" message should appear.
NOTE: System.out is forbidden in web projects, here it's used only for demonstration purposes.


Building everything by yourself from an empty project:
* Add zomcat, zomcat-job, zomcat-monitor, zomcat-appconfig and appconfig to the dependencies in your pom file
* Add the job framework to your application context (backendContext.xml)
    <import resource="classpath:META-INF/zomcat/jobs-single-quartz-scheduler-manager.xml"/>
    <import resource="classpath:META-INF/zomcat/jobs-database-configuration-provider.xml"/>
* Define your SProc provider and include your applicationConfig in your application context
* Include the Database Resources (check backendContext.xml):
    <build>
        <plugins>
            <plugin>
                <groupId>de.zalando</groupId>
                <artifactId>zalando-deploy-db-artifacts-plugin</artifactId>
                <executions>
                    <execution>
                        <id>deployDbCommons</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>deployDbArtifacts</goal>
                        </goals>
                        <configuration>
                            <artifactItems>
                                <artifactItem>
                                    <groupId>de.zalando</groupId>
                                    <artifactId>zalando-db-commons</artifactId>
                                    <outputDirectory>database/initialize</outputDirectory>
                                </artifactItem>
                                <artifactItem>
                                    <groupId>de.zalando</groupId>
                                    <artifactId>appdomain-manager</artifactId>
                                    <outputDirectory>database/initialize</outputDirectory>
                                </artifactItem>
                                <artifactItem>
                                    <groupId>de.zalando</groupId>
                                    <artifactId>zomcat-job</artifactId>
                                    <outputDirectory>database/initialize</outputDirectory>
                                </artifactItem>
                            </artifactItems>
                            <gitIgnore>../.gitignore</gitIgnore>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
 
* Define your jobManagerDatasourceProvide
* Configure your DB: (check database/initialize/07_data)
   create jobs groups under zz_commons.job_group_config
   create jobs under zz_commons.job_scheduling_config
* You can use SProcs to acces job configurations, job history... please check https://devwiki.zalando.de/Jobs_Framework


