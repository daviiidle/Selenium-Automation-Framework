# Multi-stage build for Java Selenium framework
FROM maven:3.9.4-openjdk-11-slim AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean compile -B

# Runtime stage
FROM openjdk:11-jre-slim

# Install Chrome and dependencies
RUN apt-get update && apt-get install -y \
    wget \
    gnupg \
    unzip \
    curl \
    && wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Install Firefox
RUN apt-get update && apt-get install -y firefox-esr

# Create app directory
WORKDIR /app

# Copy application from build stage
COPY --from=build /app/target ./target
COPY --from=build /app/src ./src
COPY --from=build /app/pom.xml .

# Create directories for reports and screenshots
RUN mkdir -p target/reports target/screenshots target/logs

# Set environment variables
ENV HEADLESS=true
ENV BROWSER=chrome
ENV ENVIRONMENT=dev

# Run tests
CMD ["mvn", "clean", "test"]