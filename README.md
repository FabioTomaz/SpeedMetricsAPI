# Speed Metrics Service

Demo API to calculate in realtime the speed of factory lines.

## Requirements

For building and running the application you need:

- [JDK 1.11](https://www.oracle.com/pt/java/technologies/javase-jdk11-downloads.html)
- [Maven 3](https://maven.apache.org)
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)


## To build

```shell
mvn clean install
```

## To Run

You can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```

Or you can use docker-compose:

```shell
docker-compose up
```