Scala IDE dashboard
===================

This is a web application for displaying information about the projects under the Scala IDE umbrella.
It is a [Play](http://www.playframework.com/) application.

Importing the project in Eclipse
--------------------------------

This is a Play/sbt project. To generate the files needed by Eclipse, launch sbt and use

```
eclipse with-source=true
```

More information about how to setup a Scala IDE environment for Play development is available in a [tutorial](http://scala-ide.org/docs/tutorials/play/index.html).

Running the application
-----------------------

The application requires an OAuth token to connect to GitHub. The main usage is to go over the unauthenticated rate limit of the API (60 requests per hour max).

To create an OAuth token, go to your [settings/applications page](https://github.com/settings/applications), and create a `Personal Access Token`. Then provides this token to the application as a Java properties parameter:

```bash
sbt -Ddashboard.oauthtoken=<oauth_token>
```
