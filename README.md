App Engine Yet Another Notepad
Copyright (C) 2013-14 Max Tomin

## Simple notepad application for use with App Engine Java.

Requires [Apache Maven](http://maven.apache.org) 3.0 or greater, and JDK 7+ in order to run.

To build, run

    mvn package

Building will run the tests, but to explicitly run tests you can use the test target

    mvn test

To start the app, use the [App Engine Maven Plugin](http://code.google.com/p/appengine-maven-plugin/) that is already included in this demo.  Just run the command.

    mvn appengine:devserver

IntelliJ IDEA setup

   1. Make sure GWT, AppEngine and Maven plugins are installed and configured
   2. File - Import Project... - select .pom file
   3. "Frameworks detected: Google App Engine" facet should pop-up.
      If no, you can add it manually in Modules config: yanotepad/Web - right click - Add - Google App Engine
   4. Tick "Build on make" for yanotepad:war artifact
   5. On Run/Debug config, add Google AppEngine Dev Server
   6. Run it
