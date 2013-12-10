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

Deployment on Heroku
-------------------

The GitHub OAuth is references in the Heroku config file as `GH_OAUTH_TOKEN`. The variable is set using:

```bash
heroku config:set GH_OAUTH_TOKEN=<oauth_token>
```

Support for websockets on Heroku is still experimental. It needs to be enabled with:

```bash
heroku labs:enable websockets
```

Otherwise, it is a normal Play application on Heroku.

Deployment for a different team
-------------------------------

The main configuration file is `conf/projects.conf`. It contains the list of projects to display.

There are 3 categories supported by default: `product`, `documentation` and `support`. More can be used, and a css configuration created for them.

The other UI elements (logo, page title...) need to be changed in the templates.

