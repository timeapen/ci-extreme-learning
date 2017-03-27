# Purpose
A Debian-based Docker image containing a pre-configured and automated installation of the Jenkins continuous integration server with the following additional software packages and configurations

Both provides a configured Vagrant container, and auto-builds said Docker image when the Vagrant image is started up.

### Software
  - Maven 3.3.9
  - Git
  - NodeJs
  - Ruby
  - Python
  - Robot Test framework
  - JMeter 3


### System configurations
  - passkeyless ssh key for the jenkins user

### Jenkins configuration
  - Global Tool Configurations for configured for Maven
  - JAVA_OPTS: we currently set JAVA_OPTS="-Duser.timezone=America/Toronto"
    First option is self explanatory (setting the timezone).

### How it Works
* The built 'jenkins' docker container builds off the official Docker 'Jenkins' box
* The Dockerimage can be rebuilt within a Vagrant box
    * Upon Vagrant startup:
        * Sets up environment variables needed to create the Docker box
        * Builds the new 'jenkins' docker image

# Setup
Each Jenkins container requires some manual configuration before it is ready to go.

### Jenkins Setup
Once running, each Jenkins container requires some manual configuration before it is ready to go.

# Jenkins Pipeline Basics
The Jenkins Pipeline plugin allows development teams to define build with a Groovy script. The following resources are available to familiarize yourself with the pipeline plugin and syntax
  - [GitHub repository](https://github.com/jenkinsci/pipeline-plugin) and [tutorial](https://github.com/jenkinsci/pipeline-plugin/blob/master/TUTORIAL.md)
  - [workflow-cps-plugin GitHub repository](https://github.com/jenkinsci/workflow-cps-plugin) exposes global function and class definitions to all pipelines without code duplication

# Your First Build Pipeline
  1. [register global libraries](#register-global-libraries)
  2. artifact versioning
  3. project specific configurations

### Register Global Libraries
Jenkins has a [Global Pipeline Shared Libraries plugin](https://github.com/jenkinsci/workflow-cps-global-lib-plugin) that can be used to share a set of Global code that is common and can be shared across multiple pipeline jobs.

An older mode of making the global libraries available to Jenkins are to push the source code to Jenkins itself as described [here](https://github.com/jenkinsci/workflow-cps-global-lib-plugin#defining-the-internal-library), using Jenkins SSH.

### Artifact Versioning Strategy
Two core concepts of Continuous Delivery are
  - continuous delivery
  - immutable build artifacts

Continuous Delivery is the process of producing an artifact that is releasable to a production environment provided that all verification and testing stages pass successfully or demonstrate otherwise satisfactory characteristics (e.g. performance).

Creating immutable build artifacts refers to the process of using the same artifact in all deployment, testing, and verification steps including the deployment to production. By using the same deployment artifact without modifications, a software delivery team can ensure that no bugs or other defects have been introduced during the delivery process.

A natural result of these concepts is to ensure that each build artifact is independently identifiable with a unique version number. In order to achieve this unique version number, Maven poms should include the [release candidate plugin](http://smartcodeltd.co.uk/release-candidate-maven-plugin/).

The proposed format for artifact versions is `${sementic_version}-${timestamp}.${commit}`

The above versioning scheme has the following benefits:
1. product teams are in full control of the api/semantic version of their application, any changes here require an explicit change to the `pom.xml` file
2. inclusion of a (ISO 8601 compliant) timestamp allows all builds with the same semantic version to be sorted lexigraphically. It is always trivial to determine the latest artifact by sorting or visual inspection
3. the inclusion of a commit id ensures that each artifact can be easily traced back to the version of the source code used to build it. No tagging is  required in the source control system

Add the following snipped to the `<plugins />` section of your root pom.xml file:
```xml
<plugin>
  <groupId>com.smartcodeltd</groupId>
  <artifactId>release-candidate-maven-plugin</artifactId>
  <version>1.0-201605132325.9e7d5be</version>
  <executions>
    <execution>
      <id>default-cli</id>
      <goals>
        <goal>updateVersion</goal>
      </goals>
      <configuration>
        <!--
          releaseVersionFormat *must* include a hyphen before any fixed version information or
          semantic versioning scheme

          api_version version is set to all leading characters in the version number that are not
          a hyphen (-) character (regex: [^-]*). As a result, if the resulting version number does
          NOT contain a hyphen (-), the entire version number including dynamic portions such as
          timestamp or git changeset numbers will be detected as the api_version and will be
          preserved during the next run.

          e.g. given:
            - version = 0.0.0-SNAPSHOT
            - releaseVersionFormat = {{api_version}}_suffix

            after the first run, project.version == 0.0.0_suffix
            after the second run, project.version == 0.0.0_suffix_suffix
        -->
        <releaseVersionFormat>{{ api_version }}-{{ timestamp('YYYYMMddHHmm') }}.${revision}</releaseVersionFormat>
      </configuration>
    </execution>
    <execution>
      <goals>
        <goal>version</goal>
      </goals>
      <configuration>
        <outputTemplate>PROJECT_VERSION={{ version }}</outputTemplate>
      </configuration>
    </execution>
  </executions>
</plugin>
```
