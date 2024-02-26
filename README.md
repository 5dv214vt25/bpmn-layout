# BPMN Layout

BPMN Layout is a library and a CLI tool to generate BPMN DI layouts for BPMN processes.

## Getting Started

Compile the project with the following command:

```shell
mvn clean compile assembly:single
```

Run the CLI tool with the following command:

```shell
java -jar target/bpmn-layout-jar-with-dependencies.jar input.bpmn output.bpmn
```

## Depend on using Maven Central

```xml
<dependency>
  <groupId>io.github.iharsuvorau</groupId>
  <artifactId>bpmn-layout</artifactId>
  <version>1.0.6</version>
</dependency>
```

## Depend on using GitHub Packages

Current project depends on the GitHub Packages repository for package distribution. Please, set up your access to GH
Packages following [these guidelines](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#installing-a-package).
You need to have an additional server section in `settings.xml`:

```xml
<server>
    <id>github</id>
    <username>gh username</username>
    <password>gh classic token</password>
</server>
```