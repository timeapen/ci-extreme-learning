FROM jenkins:2.32.3

ARG gid_docker=204

USER root

# set the timezone
ENV JAVA_OPTS="-Duser.timezone=America/Toronto"\
    TERM=xterm

# setup additional package repositories
RUN curl -SL --silent https://deb.nodesource.com/setup_6.x | bash -

# setup additional package repositories
RUN apt-get update -qq && \
    apt-get autoremove -q -y && \
    apt-get install -q -y nodejs python-pip && \
    rm -rf /var/lib/apt/lists/*

# install nodeglobal node packages
RUN npm install --global phantomjs-prebuilt \
    gulp \
    bower

# update pip
RUN pip install -U pip

# install Docker
ENV DOCKER_BUCKET=get.docker.com \
    DOCKER_VERSION=1.12.1 \
    DOCKER_SHA256=05ceec7fd937e1416e5dce12b0b6e1c655907d349d52574319a1e875077ccb79

RUN set -x && \
    curl -SL --silent "https://${DOCKER_BUCKET}/builds/Linux/x86_64/docker-${DOCKER_VERSION}.tgz" -o docker.tgz && \
    echo "${DOCKER_SHA256} *docker.tgz" | sha256sum -c - && \
    tar -xzf docker.tgz && \
    mv docker/* /usr/local/bin/ && \
    rmdir docker && \
    rm docker.tgz && \
    docker -v

RUN groupadd --gid ${gid_docker} docker && \
    usermod --groups docker jenkins

RUN pip install \
    docker-compose \
    robotframework

# install Jenkins Plugins
RUN /usr/local/bin/install-plugins.sh \
    ansicolor:0.4.3 \
    antisamy-markup-formatter:1.5 \
    branch-api:2.0.7 \
    build-timeout \
    cloudbees-folder \
    credentials-binding \
    docker-workflow \
    github-branch-source \
    github:1.26.1 \
    gradle \
    http_request:1.8.13 \
    job-dsl \
    ldap \
    m2release \
    mailer \
    pipeline-model-api:1.0.1 \
    pipeline-model-declarative-agent:1.0.1 \
    pipeline-model-definition:1.0.1 \
    pipeline-stage-tags-metadata:1.0.1 \
    pipeline-utility-steps \
    robot:1.6.4 \
    script-security:1.27 \
    timestamper \
    workflow-aggregator:2.5

# install maven 3.3.9
ENV mvn_tarball=apache-maven-3.3.9-bin.tar.gz
RUN wget -nc --no-verbose -P /opt http://apache.mirror.gtcomm.net/maven/maven-3/3.3.9/binaries/${mvn_tarball} && \
    tar -xzf /opt/${mvn_tarball} -C /opt && \
    rm /opt/${mvn_tarball} && \
    ln -sf /opt/apache-maven-3.3.9 /opt/maven && \
    ln -sf /opt/maven/bin/mvn /usr/local/bin


#install gradle
RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys D7CC6F019D06AF36
RUN echo "deb http://ppa.launchpad.net/cwchien/gradle/ubuntu precise main" >> /etc/apt/sources.list
RUN apt-get update && \
    apt-get install -y gradle

USER jenkins

# copy Jenkins groovy set up scripts
COPY jenkins-scripts/executors.groovy \
     jenkins-scripts/register-tool-maven3.groovy \
     jenkins-scripts/register-tool-gradle.groovy \
     jenkins-scripts/init-admin-password.groovy \
     /usr/share/jenkins/ref/init.groovy.d/

# copy script approval configuration file
COPY cfg/jenkins/scriptApproval.xml /usr/share/jenkins/ref/
