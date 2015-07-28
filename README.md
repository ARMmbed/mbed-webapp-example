mbed web application - example
==============================

This is a simple web application that connects to mbed Device Server. 
It shows devices, its resources and read/update resource values. 

Development
-----------

### Requirements
- Java 8
- Maven 3.x

Build:

    mvn clean install
    
Run:
    
    mvn jetty:run


Build with static code analyses check:

    mvn clean install -P static-code-check
    
