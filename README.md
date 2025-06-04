# BPMN Layout

BPMN Layout is a library and a CLI tool to generate BPMN DI layouts for BPMN processes.
## Note
This repo is a fork of [bpmn-layout](https://github.com/AutomatedProcessImprovement/bpmn-layout) and its sub repo [layeredgraphlayout](https://github.com/jfschaefer/layeredgraphlayout). The coordinates are flipped, such that the bpmn diagram gets a horizontal, left-to-right layout instead of a vertical, top-to-bottom layout. The width are also modified such that the arrows is aligned better. 


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
