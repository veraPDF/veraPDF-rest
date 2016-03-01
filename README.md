veraPDF-rest
=========================

*Dropwizard based veraPDF REST Services*

Introduction
------------
This represents a development prototype, there's little in the way of exception handling and unit testing.
The services are capable of serving up XML or JSON dependent upon the content type requested.

### Technologies
The project's a Maven managed Java application, the application is based on [DropWizard](http://dropwizard.io/index.html), this brings together a set of reliable libraries, the following are most used and may prove informative if your reading the code:

 * [Jetty](http://www.eclipse.org/jetty/) as a lean HTTP server,
 * [Jersey](http://jersey.java.net/) for REST services and associated, and
 * [Jackson](http://jersey.java.net/) for JSON and XML serialisation.

A good place to get going is the [Dropwizard getting started guide](http://dropwizard.io/getting-started.html).
The [Dropwizard core documentation](http://dropwizard.io/manual/core.html) covers the features used in the code base.

Building and running
--------------------
### Project structure
Currently it's delivered as a single Maven module, veraPDF-rest.

### Want to try?
First clone this project, got to the project directory and then build the Maven project:

	git clone git@github.com:veraPDF/veraPDF-rest.git
	cd veraPDF-rest
	mvn clean package

To start up the server:

	java -jar target/verapdf-rest-0.0.1-SNAPSHOT.jar server server

Go to [localhost:8080/api](http://localhost:8080/api) to see if the server is running, you should see something like:

	<Environment>
		<os>
			<name>Linux</name>
			<version>4.2.0-30-generic</version>
			<architecture>amd64</architecture>
		</os>
		<java>
			<vendor>Oracle Corporation</vendor>
			<version>1.7.0_95</version>
			<architecture>x64</architecture>
			<home>/usr/lib/jvm/java-7-openjdk-amd64/jre</home>
		</java>
		<server>
			<ipAddress>127.0.1.1</ipAddress>
			<hostName>dm-wrkstn</hostName>
			<machAddress></machAddress>
		</server>
	</Environment>

You can also list the available validation profiles at [localhost:8080/api/profiles](http://localhost:8080/api/profiles):

    <Set>
      <item>
        <dateCreated>1456384991133</dateCreated>
        <creator>veraPDF Consortium</creator>
        <name>PDF/A-1A validation profile</name>
        <description>Validation rules against ISO 19005-1:2005, Cor.1:2007 and Cor.2:2011</description>
      </item>
      <item>
        <dateCreated>1456480484892</dateCreated>
        <creator>veraPDF Consortium</creator>
        <name>PDF/A-2B validation profile</name>
        <description>Validation rules against ISO 19005-2:2011</description>
      </item>
      <item>
        <dateCreated>1456480579375</dateCreated>
        <creator>veraPDF Consortium</creator>
        <name>PDF/A-3B validation profile</name>
        <description>Validation rules against ISO 19005-3:2012</description>
      </item>
      <item>
      <dateCreated>1456385033982</dateCreated>
      <creator>veraPDF Consortium</creator>
      <name>PDF/A-1B validation profile</name>
      <description>Validation rules against ISO 19005-1:2005, Cor.1:2007 and Cor.2:2011</description>
      </item>
    </Set>

Services and curl tests
-----------------------
There are a few services that you can test a few with [curl](https://curl.haxx.se/).

### API Environment service
Shows some simple information about the server environment on [localhost:8080/api](http://localhost:8080/api)

    curl localhost:8080/api

### Validation Profile services
Validation Profiles contain the PDF/A validation tests and their description.  A list of profile details is available at [localhost:8080/api/profiles/](http://localhost:8080/api/profiles/). To test with curl:

    curl localhost:8080/api/profiles

Each profile is identified by a 2 letter code made up the PDF/A version amd level. These are listed at [localhost:8080/api/profles/ids/](http://localhost:8080/api/profiles/ids/):

    curl localhost:8080/api/profiles/ids

An individual profile can be obtained by ID at http://localhost:8080/api/profiles/*id*, e.g. [localhost:8080/api/profiles/1b/](http://localhost:8080/api/profiles/1b/):

    curl localhost:8080/api/profiles/1b

The curl call defaults to a JSON representation, to obtain the XML profile:

    curl localhost:8080/api/profiles/ids -H  "Accept:application/xml"

### PDF/A Validation services
PDF/A validation is also available as a POST service at http://localhost:8080/api/validate/*id*. There's currently no client application or page, but curl can be used:

    curl -F "file=@veraPDF-corpus/PDF_A-1b/6.1 File structure/6.1.12 Implementation limits/veraPDF test suite 6-1-12-t01-fail-a.pdf" localhost:8080/api/validate/1b

or to obtain the result in XML:

    curl -F "file=@veraPDF-corpus/PDF_A-1b/6.1 File structure/6.1.12 Implementation limits/veraPDF test suite 6-1-12-t01-fail-a.pdf" localhost:8080/api/validate/1b -H  "Accept:application/xml"
