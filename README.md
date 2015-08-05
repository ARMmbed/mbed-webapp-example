mbed web application - example
==============================

This is a simple web application that connects to mbed Device Server. 

### Features

- Configure connection to device server
- List all devices
- List device resources
- Invoke proxy requests (GET, PUT, POST, DELETE) 
 

Development
-----------
[![Build Status](https://magnum.travis-ci.com/ARMmbed/mbed-webapp-example.svg?token=dwQ5RVGhwvjYBMfR1k6t&branch=master)](https://magnum.travis-ci.com/ARMmbed/mbed-webapp-example)

### Requirements
- Java 8
- Maven 3.x

### Run
Jetty (embedded):
    
    mvn jetty:run

Tomcat (embedded):

    mvn tomcat7:run
    
Open from browser: http://localhost:8082/example-app    

### Build:

    mvn clean install

With static code analyses check (findbugs, pmd, cobertura):

    mvn clean install -P static-code-check
    
