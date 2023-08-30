veraPDF-rest
=========================

*Dropwizard based veraPDF REST Services*

Introduction
------------
This represents a development prototype, there's little in the way of exception handling and unit testing.
The services are capable of serving up XML or JSON dependent upon the content type requested.

### Technologies
The project's a Maven managed Java application, the application is based on
[DropWizard](https://www.dropwizard.io/en/stable/index.html), this brings together a set of reliable libraries, the
following are most used and may prove informative if you are reading the code:

 * [Jetty](https://www.eclipse.org/jetty/) as a lean HTTP server,
 * [Jersey](https://jersey.java.net/) for REST services and associated, and
 * [Jackson](https://github.com/FasterXML/jackson/) for JSON and XML serialisation.

A good place to get going is the [Dropwizard getting started guide](https://www.dropwizard.io/en/stable/getting-started.html).
The [Dropwizard core documentation](https://www.dropwizard.io/en/stable/manual/core.html) covers the features used in the code base.

Running DockerHub image
--------------------

To run the veraPDF rest image from DockerHub:
```
docker run -d -p 8080:8080 -p 8081:8081 verapdf/rest:latest
```

Port 8080 serves both the veraPDF web interface and the veraPDF Rest API. Port 8081 serves the DropWizard diagnostics. 

Building and running locally
--------------------

### Docker
This uses a Docker multi-stage build so the final container image which
may be deployed does not require more than the base OpenJDK JRE without
the entire build tool-chain.

Tested lightly:

```
docker build -t verapdf-rest:latest . && docker run -d -p 8080:8080 -p 8081:8081 verapdf-rest:latest
```

If you encounter an error during docker run about "Can't set cookie dm_task_set_cookie failed", try:

```
sudo dmsetup udevcomplete_all
```

The built verapdf-rest image is notable smaller than just the base Maven image even before you consider the
downloaded dependencies so the multi-stage build is definitely worthwhile:

```
cadams@ganymede:~ $ docker images
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
verapdf-rest        latest              c3c2a52a7bc0        5 minutes ago       297MB
maven               latest              88714384d642        11 days ago         749MB
```

Using the Alpine-based OpenJRE images provides a further hefty size reduction and we don't seem to be using
anything which would be easier on Ubuntu:

```
verapdf-rest        latest              c69af6445b35        31 seconds ago      103MB
```

There's an "official" docker image that can be grabbed by `docker pull verapdf/rest:latest`.

### Project structure
Currently it's delivered as a single Maven module, veraPDF-rest.

### Want to try?
First clone this project, go to the project directory and then build the Maven project:

	git clone https://github.com/veraPDF/veraPDF-rest.git
	cd veraPDF-rest
	git checkout integration
	mvn clean package

To start up the server:

	java -jar target/verapdf-rest-0.2.0-SNAPSHOT.jar server

Go to [localhost:8080/api/info](http://localhost:8080/api/info) to see if the server is running, you should
see something like:

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

You can also list the available validation profiles at
[localhost:8080/api/profiles](http://localhost:8080/api/profiles):

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

    curl localhost:8080/api/info

### Validation Profile services
Validation Profiles contain the PDF/A and PDF/UA validation tests and their description.  A list of profile details is available
at [localhost:8080/api/profiles/](http://localhost:8080/api/profiles/). To test with curl:

    curl localhost:8080/api/profiles

Each profile is identified by a letter code made up the PDF/A or PDF/UA version and level. These are listed at
[localhost:8080/api/profiles/ids/](http://localhost:8080/api/profiles/ids/):

    curl localhost:8080/api/profiles/ids

An individual profile can be obtained by ID at `http://localhost:8080/api/profiles/*id*`, e.g.
[localhost:8080/api/profiles/1b/](http://localhost:8080/api/profiles/1b/):

    curl localhost:8080/api/profiles/1b

The curl call defaults to a JSON representation, to obtain the XML profile:

    curl localhost:8080/api/profiles/1b -H  "Accept:application/xml"

### Validation services
Validation is also available as a POST service at `http://localhost:8080/api/validate/*id*`. There's currently
no client application or page, but curl can be used:

    curl -F "file=@veraPDF-corpus/PDF_A-1b/6.1 File structure/6.1.12 Implementation limits/veraPDF test suite 6-1-12-t01-fail-a.pdf" localhost:8080/api/validate/1b

or to obtain the result in XML:

    curl -F "file=@veraPDF-corpus/PDF_A-1b/6.1 File structure/6.1.12 Implementation limits/veraPDF test suite 6-1-12-t01-fail-a.pdf" localhost:8080/api/validate/1b -H "Accept:application/xml"

### Configuration files
Configuration parameters are located in `/opt/verapdf-rest/config` folder of the container file system. The details on the parameters are available at https://docs.verapdf.org/cli/config/
