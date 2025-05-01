veraPDF Arlington
=========================

*Dropwizard based REST Service for veraPDF implementation of the PDF Arlington model*

Introduction
------------

The [PDF Arlington Model](https://github.com/pdf-association/arlington-pdf-model) covers the requirements of PDF object model as specified in ISO 32000-2:2020 (PDF 2.0, including [resolved errata](https://pdf-issues.pdfa.org/)) as well as _some_ (but not all!) aspects from earlier Adobe PDF references and various extensions (identified by the predicate `fn:Extension(...)`). 

veraPDF adds the support for this model by translating TSV files into its own profile syntax based on [veraPDF formal grammar](https://docs.verapdf.org/validation/rules/) for veraPDF rules. See the [veraPDF GitHub repository for Arlington](https://github.com/veraPDF/veraPDF-arlington-tools) for the implementation details.

The image provides services that receive PDF in the request, perform its check against a specified Arlington profile and serve up XML or JSON dependent upon the content type requested.

### Technologies
The project's a Maven managed Java application, the application is based on
[Dropwizard](https://www.dropwizard.io/en/stable/index.html), this brings together a set of reliable libraries, the
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
docker run -d -p 8080:8080 -p 8081:8081 verapdf/arlington:latest
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
docker build -t verapdf-arlington:latest . && docker run -d -p 8080:8080 -p 8081:8081 verapdf-arlington:latest
```

If you encounter an error during docker run about "Can't set cookie dm_task_set_cookie failed", try:

```
sudo dmsetup udevcomplete_all
```

The built verapdf-arlington image is notable smaller than just the base Maven image even before you consider the
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

There's an "official" docker image that can be grabbed by `docker pull verapdf/arlington:latest`.

### Swagger documentation
Swagger documentation is available at [localhost:8080/swagger](http://localhost:8080/swagger).

### Want to try?
First clone this project, go to the project directory, checkout to `arlington-master` branch for release version or `arlington` 
branch for dev version, and then build the Maven project:

	git clone https://github.com/veraPDF/veraPDF-rest.git
	cd veraPDF-rest
	git checkout arlington-master
	mvn clean package

To start up the server:

	java -jar target/verapdf-rest-arlington-1.28.1.jar server server.yml

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

You can also list the available Arlington profiles at
[localhost:8080/api/profiles](http://localhost:8080/api/profiles):

    <Set>
        <item>
        <name>Arlington PDF 1.4 profile</name>
        <description>Rules against PDF 1.4 Specification</description>
        <creator>veraPDF Consortium</creator>
        <dateCreated>1653331528872</dateCreated>
      </item>
      <item>
        <name>Arlington PDF 1.7 profile</name>
        <description>Rules against PDF 1.7 Specification</description>
        <creator>veraPDF Consortium</creator>
        <dateCreated>1653331528872</dateCreated>
      </item>
    </Set>

Services and curl tests
-----------------------
There are a few services that you can test with [curl](https://curl.haxx.se/).

### API Environment service
Shows some simple information about the server environment on [localhost:8080/api](http://localhost:8080/api)

    curl localhost:8080/api/info

### Arlington profile services
Arlington profile contain collections of checks for a specific PDF version and their descriptions.  A list of profile details is available
at [localhost:8080/api/profiles/](http://localhost:8080/api/profiles/). To test with curl:

    curl localhost:8080/api/profiles

Each profile is identified by a prefix `arlington` concatenated with the PDF version: `arlington1.1`, `arlington1.2` and so on. These are listed at
[localhost:8080/api/profiles/ids/](http://localhost:8080/api/profiles/ids/):

    curl localhost:8080/api/profiles/ids

An individual profile can be obtained by ID at `http://localhost:8080/api/profiles/*id*`, e.g.
[localhost:8080/api/profiles/1b/](http://localhost:8080/api/profiles/arlington2.0/):

    curl localhost:8080/api/profiles/arlington2.0

The curl call defaults to a JSON representation, to obtain the XML profile:

    curl localhost:8080/api/profiles/arlington2.0 -H  "Accept:application/xml"
	
A special profile ID `auto` is reserved for the automatic selection of the Arlington profile based on the version specified in the PDF document. This will default to `arlington1.4` for any PDF version prior or equal to 1.4, to `arlington1.7` for PDF versions from 1.5 to 1.7, and to `arlington2.0` for PDFs declaring version 2.0.

### Performing checks
Executing the check of a PDF against a specific Arlington profile is available as a POST request at `http://localhost:8080/api/validate/*id*`. To test with curl:

    curl -F "file=@samples/pdf.pdf" localhost:8080/api/validate/arlington1.7

or to obtain the result in XML:

    curl -F "file=@samples/pdf.pdf" localhost:8080/api/validate/arlington1.7 -H "Accept:application/xml"

One can check a PDF given by URL as a POST request `http://localhost:8080/api/validate/url/*id*`. To test with curl:

```
curl -F "url=http://www.pdf995.com/samples/pdf.pdf" localhost:8080/api/validate/url/arlington1.7
```

To check your local files you need to add folder with files to the docker container. To run the veraPDF Arlington image
with your local files run docker image with bind mount `-v /local/path/of/the/folder:/home/folder`. 
For example, to run the veraPDF Arlington image from DockerHub with your local files:

```
docker run -d -p 8080:8080 -p 8081:8081 -v /local/path/of/the/folder:/home/folder verapdf/arlington:latest
```
 
and use curl:

```
curl -F "url=file:///home/folder/pdf.pdf" localhost:8080/api/validate/url/arlington1.7
```

To limit PDF file size in the POST requests you need to send request with header (key `X-File-Size` and value in bytes). 
For example to use  request `http://localhost:8080/api/validate/url/*id*` with file which size is 300 KB run:

```
curl -H "X-File-Size: 307200" -F "url=http://www.pdf995.com/samples/pdf.pdf" localhost:8080/api/validate/url/auto 
```

### veraPDF configuration parameters
Configuration parameters are located in `/opt/verapdf-rest/config` folder of the container file system. The details on the veraPDF parameters are available at https://docs.verapdf.org/cli/config/. 

Additionally, this folder includes `server.yml` which contains [HTTP server configuration parameters](https://www.dropwizard.io/en/stable/manual/configuration.html) and additional parameters described below. 

#### Limiting PDF file size
To set the maximum file size of PDF, change `maxFileSize` in `server.yml` file or run docker image as:
```
docker run -d -p 8080:8080 -p 8081:8081 -e VERAPDF_MAX_FILE_SIZE=1 verapdf/arlington:latest
```
where VERAPDF_MAX_FILE_SIZE is 1MB. The default maximum PDF file size is 100MB.

#### Maximum heap size
To change maximum Java heap size in docker image run:
```
docker run -d -p 8080:8080 -p 8081:8081 -e JAVA_OPTS="-Xmx128M" verapdf/arlington:latest
```

#### Additional configuration parameters
See [Dropwizard Configuration Reference](https://www.dropwizard.io/en/stable/manual/configuration.html) for overview of available configuration parameters such as controlling the number of threads, queue size and others. 
