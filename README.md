# Welcome to the Liberty app accelerator
An application builder to allow you to construct starter applications for building your projects with Liberty.

Liberty app accelerator is still in its early stages so we will be adding new technologies and capabilities to the page over time. For now, feel free to <a href="http://liberty-starter.wasdev.developer.ibm.com/start/">try it out</a> and see how quickly you can have a fully fledged application. If you have any questions, comments or suggestions please raise an <a href="https://github.com/WASdev/tool.accelerate.core/issues">issue</a> on our GitHub repository.

For an introduction see our <a href="https://developer.ibm.com/wasdev/blog/2015/11/27/introducing-the-liberty-starter-a-tool-to-get-you-writing-microservices-quickly/">blog post</a>. To learn how the Liberty Starter can be used to create the <a href="https://developer.ibm.com/wasdev/docs/an-evolved-plants-by-websphere-image-service-making-the-break/">image service</a> for the <a href="https://developer.ibm.com/wasdev/docs/starting-evolution-microservices-using-plants-websphere-sample/">Plants by WebSphere evolution</a> from monolith to microservice, see our new <a href="https://developer.ibm.com/wasdev/docs/creating-image-service-plants-websphere-using-liberty-starter"/>article</a>.

The current technologies we have are:

## Rest
This provides you with the jaxrs-2.0 feature and the jsonp-1.0 feature.

Inside the application project produced there is a application.rest package containing the <code>LibertyRestEndpoint</code> class. This adds a REST endpoint which you can access at /rest. Inside the wlpcfg project there is the <code>it.rest.LibertyRestEndpointTest</code> that will test the REST endpoint to ensure it is working.

For the complete feature documentation, see the <a href="http://www.ibm.com/support/knowledgecenter/SSAW57_8.5.5/com.ibm.websphere.wlp.nd.multiplatform.doc/ae/rwlp_feat.html%23rwlp_feat__jaxrs-2.0">jaxrs-2.0</a> feature description in IBM Knowledge Center.

## Servlet
This provides you with the servlet-3.1 feature.

Inside the application project there is a application.servlet package containing the <code>LibertyServlet</code> class. This adds a servlet with an endpoint which you can access at /servlet. Inside the wlpcfg project there is the <code>it.servlet.LibertyServletTest</code> that will test the servlet's endpoint to ensure it is working.

For the complete feature documentation, see the <a href="http://www.ibm.com/support/knowledgecenter/SSAW57_8.5.5/com.ibm.websphere.wlp.nd.multiplatform.doc/ae/rwlp_feat.html%23rwlp_feat__servlet-3.1">servlet-3.1</a> feature description in IBM Knowledge Center.

## Spring Boot with Spring MVC
This project demonstrate a "hello world" web site with Spring technology and run on IBM WebSphere Liberty Server 
Learn more about IBM WebSphere Liberty, visit <a href="https://developer.ibm.com/wasdev/">https://developer.ibm.com/wasdev/</a>
	
Inside the application project containing two classes:
* application.springboot.Application</code>: The entry point for the SpringBoot application.
* application.springboot.web.LibertyHelloController</code>: A Spring MVC endpoint which you can access at /springbootweb.

Inside the wlpcfg project there is the <code>it.springboot.web.HelloControllerTest</code> that will test the Spring MVC endpoint to ensure it is working.
For the complete feature documentation, see the <a href="http://spring.io/guides/gs/serving-web-content">Serving Web Content with Spring MVC</a>

## Spring Boot with Jersey
This project demonstrate creating a "hello world" RESTful web service with Spring technology and run on IBM WebSphere Liberty Server 
Learn more about IBM WebSphere Liberty, visit <a href="https://developer.ibm.com/wasdev/">https://developer.ibm.com/wasdev/</a> 


Inside the application project containing 3 classes:
* application.springboot.Application</code>: The entry point for the SpringBoot application.
* application.springboot.jersey.Endpoint</code>: A Jersey endpoint which you can access at /springbootjersey.
* application.springboot.jersey.JerseyConfig</code>: Jersey configuration class.

Inside the wlpcfg project there is the <code>it.springboot.jersey.SampleJerseyApplicationTests</code> that will test the Jersey endpoint to ensure it is working.

For the complete feature documentation, see the <a href="http://spring.io/guides/gs/rest-service/">Building a RESTful Web Service</a>

## Spring Boot with Security
This project demonstrate creating a simple web application with resources that are protected by Spring Security and run on IBM WebSphere Liberty Server 
Learn more about IBM WebSphere Liberty, visit <a href="https://developer.ibm.com/wasdev/">https://developer.ibm.com/wasdev/</a>
	
	
Inside the application project containing 3 classes:
* application.springboot.Application</code>: The entry point for the SpringBoot application.
* application.springboot.securityMvcConfig</code>: Map each uri to static html resources
* application.springboot.security.WebSecurityConfig</code>: Seucirty configuration for Springboot application

Inside the wlpcfg project there is the <code>it.springboot.security.HelloControllerTest</code> that will test the /springbootsecurity endpoint to ensure it is working.

For the complete feature documentation, see the <a href="http://spring.io/guides/gs/securing-web/">Securing a Web Application</a>

## Spring Boot with JDBC
This project demonstrate how to accessing relational data with Spring technology and run on IBM WebSphere Liberty Server 
Learn more about IBM WebSphere Liberty, visit <a href="https://developer.ibm.com/wasdev/">https://developer.ibm.com/wasdev/</a>

Inside the application project containing 3 classes:
* application.springboot.Application</code>: The entry point for the SpringBoot application.
* application.springboot.jdbc.Customer</code>: POJO class for DB table
* application.springboot.jdbc.JDBCController</code>: A Springboot JDBC endpoint which implements the JDBC DB access

Inside the wlpcfg project there is the <code>it.springboot.jdbc.HelloControllerTest</code> that will test the /springbootjdbc endpoint to ensure it is working.

For the complete feature documentation, see the <a href="http://spring.io/guides/gs/relational-data-access/">Accessing Relational Data using JDBC with Spring</a>

## Websockets
This provides you with the websocket-1.1 feature.

For the complete feature documentation, see the <a href="http://www.ibm.com/support/knowledgecenter/SSAW57_8.5.5/com.ibm.websphere.wlp.nd.multiplatform.doc/ae/rwlp_feat.html%23rwlp_feat__websocket-1.0">websocket-1.1</a> feature description in IBM Knowledge Center.

## Persistence
This provides you with jpa-2.1. For the complete feature documentation, see the <a href="http://www.ibm.com/support/knowledgecenter/SSAW57_8.5.5/com.ibm.websphere.wlp.nd.multiplatform.doc/ae/rwlp_feat.html%23rwlp_feat__jpa-2.1">jpa-2.1</a> feature description in IBM Knowledge Center.

## Watson SDK
The Watson SDK provides an API for accessing Watson Services. For the complete documentation, take a look at <a href="https://developer.ibm.com/watson/">the Watson developer pages</a> and <a href="https://github.com/watson-developer-cloud/java-sdk">the Watson SDK github project</a>.

# Building and adding to the app accelerator

## Building the Project
Once you have downloaded the project, to build it you just need to run the command <code>gradle clean build</code> in the top level project. Once the application has been built grab the StarterServer folder that is in liberty-starter-wlpcfg/servers and copy that into an existing Liberty install and run <code>server run StarterServer</code>. The application should be available at <code>localhost:9082</code>.

## Project Structure
The project is split up into several different pieces.

* <code>liberty-starter-application</code> contains the code to build the main application including the code for the UI
* <code>liberty-starter-common</code> contains common api code for the projects
* <code>liberty-filter-application</code> is a simple war that redirects people to the context <code>/start</code>
* <code>liberty-starter-wlpcfg</code> contains the Liberty usr directory where the logs and apps for the app accelerator will be put after a build
* <code>liberty-starter-test</code> contains a test microservice used during the testing phase

There are then a set of <code>starter-microservice-techId</code> projects that contain the code for the individual technology types.

## Deployment Options
There are two deployment options for the app accelerator: local and Bluemix

### Local Deployment

If you have chosen the local deployment option when you run <code>mvn install</code> the application will be compiled, tested and then the application will start at </code>localhost:9080/mylibertyApp</code>. To stop the application run <code>mvn clean -P stopServer</code>. This will stop the server and clean up the target directories.

### Bluemix Deployment

To deploy an application to Bluemix you first need a <a href="https://console.eu-gb.bluemix.net/">Bluemix account</a>. Once you have created a Bluemix account you can build and deploy your application by running <code>mvn install -Dcf.org=[your email address] -Dcf.username=[your username] -Dcf.password=[your password]</code>. Where <code>cf.org</code> is the Bluemix organization you want to deploy to and <code>cf.username</code> and <code>cf.password</code> are your credentials for Bluemix. Once the build has been run see your command line output to find the endpoint for your application or look for it in the Bluemix dashboard.

You can optionally supply the following Bluemix configurations in the command line or in the top level pom.xml:
* <cf.context>eu-gb.mybluemix.net</cf.context>
* <cf.target>https://api.eu-gb.bluemix.net</cf.target>
* <cf.space>dev</cf.space>
* <cf.context.root>${cf.host}.${cf.context}/${warContext}</cf.context.root>

### Changing the deployment type

If you have chosen the deployment type on the app accelerator you can specify a different deployment type on the command line as follows:
* <code>mvn install -P localServer</code> for local deployment
* <code>mvn install -P bluemix</code> for Bluemix deployment

## Adding new technologies to the app accelerator
To see an example of everything you can include in a technology see the starter-microservice-test project. This is the example project we use to test the main piece of the app against.

### Create a technology from the template service

1. Copy the contents of the <code>starter-microservice-template</code> directory into a new directory. The convention is that the last part of the name is related to the technology. So, if you were creating a technology based on <code>SuperTech</code> then the directory would be <code>starter-microservice-supertech</code>. (SuperTech will be the name used for the rest of these instructions).

2. Update the id's and context root in the <code>build.gradle</code> file. The context root would change to <code>/supertech</code>, the id in the <code>installAllPoms</code> task would be <code>supertech</code> and the id in the <code>fvt</code> task would be <code>starter-microservice-supertech</code>.

2. Change the group ID values in the POM files under <code>starter-microservice-supertech/repository/0.0.1</code>, compile-pom.xml, provided-pom.xml and runtime-pom.xml. <groupId>net.wasdev.wlp.starters.template</groupId> becomes <groupId>net.wasdev.wlp.starters.supertech</groupId>.

3. Refactor the packages and classes under src to SuperTech i.e. <code>starter-microservice-supertech/src/main/java/com/ibm/liberty/starter/service/template</code> becomes <code>starter-microservice-supertech/src/main/java/com/ibm/liberty/starter/service/supertech</code>

4. Change the <code>GROUP_SUFFIX</code> constant in the <code>ProviderEndpoint</code> class to <code>supertech</code>.

6. Change the value of <code>context-root</code> in <code>src/main/webapp/WEB-INF/ibm-web-ext.xml</code> to <code>supertech</code>.

7. Edit <code>src/main/webapp/WEB-INF/classes/description.html</code> to tell everyone about how SuperTech works and it's benefits.

8. (If you don't want to provide sample code for your technology type skip to step 10). Put the application sample code into <code>src/main/webapp/sample/myProject-application</code>.

9. Put the Liberty configuration for the sample application into <code>src/main/webapp/sample/myProject-wlpcfg</code>.

10. Change the tests package to <code>src/test/java/com/ibm/liberty/starter/service/supertech/api/v1/it</code> and then the test classes to expect the correct responses for SuperTech.

### Configuring a new technology
1. In <code>liberty-starter-application/src/main/resources</code> update the services.json file to add your new technology, including an id, name, description and the endpoint you want to use. This will add your technology as an option on the main page.
 1. By convention the id should be <code>supertech</code> and the endpoint should be <code>/supertech</code>.
 1. The name and description are used in the UI to give the user information about the technology type.

2. In the settings.gradle file add <code>starter-microservice-supertech</code> to the <code>include</code> list. This will add your project into the build lifecycle.

2. In the build.gradle file in <code>liberty-starter-application</code> in the last set of <code>dependsOn</code> commands add your technology to the <code>war.dependsOn</code> list. You need to add <code>:nameOfYourProject:publishWar</code>. This will make sure your project is built before the <code>liberty-starter-application</code> project.

3. In <code>liberty-starter-wlpcfg/servers/StarterServer/server.xml</code> add your application to the list. You need to provide the name of the war file being created in location, the context-root that matches the endpoint specified in the <code>services.json</code> file in step 1 and the id you specified in step 1.

If you run <code>gradle clean build</code> your new project should now be built and the war should be put into the apps directory of your server.
