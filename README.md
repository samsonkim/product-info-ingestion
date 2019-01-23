[![Build Status](https://travis-ci.com/samsonkim/product-info-ingestion.svg?branch=master)](https://travis-ci.com/samsonkim/product-info-ingestion)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

# Product Information Ingestion Library

Library to integrate with a grocery store's product information system.  The specification can be found [here](https://github.com/prestoqinc/code-exercise-services/blob/master/ProductInformationIntegrationSpec.md). 

A sample store application is provided to demonstrate the features of the library.
The application accepts a store's product catalog as input and generates a file consisting of a List of ProductRecords in json format.  See Building and running the application section for additional details.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. 

### Prerequisites

For development, you need to install:
 * JDK - https://openjdk.java.net/
 * maven - https://maven.apache.org/
 * lombok plugin (available on Eclipse, Intellij, Visual Studio Code) - https://projectlombok.org/

### Installing

#### Using OS X - Homebrew

OpenJDK

```
brew install adoptopenjdk/openjdk/adoptopenjdk-openjdk8
```

Maven

```
brew install maven 
```

## Running the tests

To run the unit tests from the command line:

```
mvn clean test
```

## Building and running the application

To build and package the application from the command line:

```
mvn clean package
```

This produces an executable jar (productinfoingestion-1.0-SNAPSHOT.jar) found in the target directory.

To execute the application with the sample input file:

```
java -jar target/productinfoingestion-1.0-SNAPSHOT.jar src/main/resources/input-sample.txt
```

This will produce a file(sample.json) which is a JSON list of ProductRecords.


## Built With

* [Travis CI](https://travis-ci.com/) - Hosted Continuous Integration Service 
* [OpenJDK](https://openjdk.java.net/) - Java Development Kit 
* [Maven](https://maven.apache.org/) - Dependency Management
* [Jackson](https://github.com/FasterXML/jackson) - JSON Library 
* [Lombok](https://projectlombok.org/) - POJO Helper Utilities 
* [Vavr](http://www.vavr.io/) - Functional Java Library 
* [Commons-lang](https://commons.apache.org/proper/commons-lang/) - Apache Helper Library 
* [Junit](https://junit.org/junit4/) - Unit testing framework
* [Mockito](https://site.mockito.org/) - Mocking framework for unit tests 

## Authors

* **Samson Kim** - *Initial work* 

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
