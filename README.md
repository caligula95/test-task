# Test task application for TUI project

This project exposes REST API which can return results from Github API user repositories and branches.

# How to build and run

Project can be compiled with JDK 11 and above `javac`.

To compile just do `mvn clean package`.

Spring Boot Version : 2.7.7 Java Version : 11

## Prerequisites

* JAVA 11 should be installed
* Maven should be installed

**Note** : Here, we are using one of the free GitHub API listed in [Github](https://developer.github.com/v3)

To run the application locally execute the below command :

```
java -jar target/tui-tech-task*.jar
```

The server will start at <http://localhost:8080>.

## Exploring the Rest APIs

The application contains the following REST API

```
1. GET /users/{username}/repositories - Get the repositories along with branches for a given user
```

```
curl -X 'GET' \
  'http://localhost:8080/users/testuser/repositories' \
  -H 'accept: application/json'
```

Deployed project can be found by this url: [Project url](https://ifsa2wc7ca.execute-api.eu-central-1.amazonaws.com)

*aws-cloudformation-template.yaml* was used to deploy this project to AWS.
*aws-cloudformation-api-gateway-template.yaml* was used to create AWS Gateway API for this project.