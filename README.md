# Java8 Try<T> 
To wrap checked exceptions and to make failures explicit.  This is a translation
of Scala's Try[T].  Basic api to do with container semantics like transformation, 
filtering, flatMap and failure recovery and builing a recovery chain.


## Build Info
Use Gradle 2.0 for builds.  Please do not checkin Eclipse or Intellij or any IDE specific files.  
For Idea or Eclipse they can be generated using
* `gradlew eclipse`
* `gradlew idea`


## Project Versioning
We will be following [JBoss Versioning Convention](https://community.jboss.org/wiki/JBossProjectVersioning?_sscc=t)
* `major.minor.micro.Alpha[n]`
* `major.minor.micro.Beta[n]`
* `major.minor.micro.CR[n]`
Please refer to `AppConfig.groovy` - a single place of change for all the project configuration changes

## License
**This software is licensed under the terms of the [FreeBSD License](http://en.wikipedia.org/wiki/BSD_licenses)**

## Using Try<T> Examples

* Refer to examples folder
* Please refer to the following [presentation] (http://dhavaldalal.github.io/Java8-Try/#why-java8-try)
