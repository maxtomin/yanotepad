App Engine Yet Another Notepad
Copyright (C) 2013 Max Tomin

## Simple notepad application for use with App Engine Java.

Requires [Apache Maven](http://maven.apache.org) 3.0 or greater, and JDK 7+ in order to run.

To build, run

    mvn package

Building will run the tests, but to explicitly run tests you can use the test target

    mvn test

To start the app, use the [App Engine Maven Plugin](http://code.google.com/p/appengine-maven-plugin/) that is already included in this demo.  Just run the command.

    mvn appengine:devserver

IntelliJ IDEA setup

   1. Make sure GWT, AppEngine and Maven plugins are installed
   2. Import new project from Maven (use .pom file provided)
   3. Import new module from Maven (use .pom file provided)
   4. Add "Google AppEngine Dev Server" run configuration with default settings
   5. Run it
