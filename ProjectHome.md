Greaze is an open-source framework for creating JSON-based REST and procedural Web services in Java. Here are some of the features supported by Greaze:
  * Define a JSON REST resource by creating an equivalent Java class. For example, define Order class to create an Order resource with JSON body.
  * Define a resource query by creating a Java class for the query parameters. The response is a list of JSON resources matching with the query. You will need to define a class on the server-side that will do the actual query.
  * Define an arbitrary Web-service that requires certain headers in the request, and receives certain headers and body in the response. You can also combine arbitrary number and type of objects in the request or response body.
  * Greaze provides easy-to-use Genericized Java classes for accessing the client service.
  * Greaze includes support for deployment on Google App Engine.


An early access version is now available through Maven Central. Note that until the 1.0 version, the API is subject to drastic changes.

Android game [SpellMeRight](https://market.android.com/details?id=com.applimobile.spellmeright) uses Greaze framework to create a multiplayer game play. The Android app uses a Greaze client that connects to a Greaze server deployed on Google App Engine.

[Here is the sites page](https://sites.google.com/site/greazeproject/) for this project.

[Continuous build for Greaze sub-projects](http://continuousbuild.dyndns.org:8080/)

Help appreciated for the following tasks:

  1. Improve pom.xml for server-side to better support Google App Engine
  1. Add support for HTTP pipelining between client and server
  1. Improve Maven pom.xml to have a common archetype
  1. Support HTTP pipelining in Greaze client
  1. Support Hanging GETs in Greaze server and client