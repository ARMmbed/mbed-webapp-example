mbed web application - example
==============================

This is a simple web application that connects to mbed Device Server (mbed DS). 

### Features

- Configure connection to mbed DS
- List all devices
- List device resources
- Invoke proxy requests (GET, PUT, POST, DELETE) 
 

Development
-----------
[![Build Status](https://magnum.travis-ci.com/ARMmbed/mbed-webapp-example.svg?token=dwQ5RVGhwvjYBMfR1k6t&branch=master)](https://magnum.travis-ci.com/ARMmbed/mbed-webapp-example)

### Requirements
- Java 8
- Maven 3.x
- mbed DS - where the example application connects to

### Build:

    mvn clean package

With static code analyses check (findbugs, pmd, jacoco):

    mvn clean package -P static-code-check

Build executable war (with embedded tomcat):

    mvn clean package tomcat7:exec-war-only

### Run
- Jetty (embedded):
    
        mvn jetty:run

- Tomcat (embedded):

        mvn tomcat7:run

- Executable war (with embedded Tomcat):

        cd target
        java -Dcom.arm.mbed.restclient.servlet.server-port=8082 -jar example-app-1.0-SNAPSHOT-war-exec.jar -httpPort=8082

Open from browser: http://localhost:8082

Configure with mbed Connector
==============================

1. Open in browser: http://connector-test.dev.mbed.com/.
2. Sign up for the first time or login with your credentials. 
3. Click the **Access keys** link.
4. Create new access key.
5. Copy the mbed DS address from this page.
6. Open the example-app in browser: http://localhost:8082.
7. Select **Configuration** tab at the top of the page.
8. Select **Token Authentication**.
9. Enter the access key and the copied mbed DS address.
10. Select Pull or Push notification channel. Pull is recommended. Push Notifications requires publicly available URL for the example app (example value: http://REMOTE_HOST:8082/mds-notif)
11. Save.
