FROM ubuntu:22.04

# Set environment variables to avoid interactive prompts
ENV DEBIAN_FRONTEND=noninteractive

# Install system dependencies
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    git \
    unzip \
    vim \
    nano \
    htop \
    lsof \
    net-tools \
    telnet \
    mysql-client \
    redis-tools \
    --no-install-recommends \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Install OpenJDK 17 (LTS version recommended for Spring Boot)
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    --no-install-recommends \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Set JAVA_HOME
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# Install Maven
ARG MAVEN_VERSION=3.9.5
RUN wget https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz && \
    tar -xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /opt && \
    ln -s /opt/apache-maven-${MAVEN_VERSION} /opt/maven && \
    rm apache-maven-${MAVEN_VERSION}-bin.tar.gz

# Set Maven environment
ENV M2_HOME=/opt/maven
ENV MAVEN_HOME=/opt/maven
ENV PATH=$MAVEN_HOME/bin:$PATH

# Install Docker (for containerization if needed)
RUN curl -fsSL https://get.docker.com -o get-docker.sh && \
    sh get-docker.sh && \
    rm get-docker.sh

# Install Node.js (useful for frontend tooling if needed)
RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs

# Create a non-root user for development
RUN useradd -m -d /home/ubuntu -s /bin/bash ubuntu && \
    echo "ubuntu ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers

# Set up bash completion and aliases
RUN echo ". /etc/bash_completion" >> /etc/bash.bashrc && \
    echo "alias ll='ls -alF'" >> /home/ubuntu/.bashrc && \
    echo "alias la='ls -A'" >> /home/ubuntu/.bashrc && \
    echo "alias l='ls -CF'" >> /home/ubuntu/.bashrc

USER ubuntu
WORKDIR /home/ubuntu

# Configure git to avoid issues with safe.directory
RUN git config --global --add safe.directory '*' && \
    git config --global init.defaultBranch main

# Create Maven local repository directory
RUN mkdir -p /home/ubuntu/.m2

# Reset DEBIAN_FRONTEND
ENV DEBIAN_FRONTEND=

# Verify installations
RUN java -version && \
    mvn -version && \
    node --version && \
    npm --version
