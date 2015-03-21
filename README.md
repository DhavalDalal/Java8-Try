# Java8 Try<T> 
To wrap checked exceptions and to make failures explicit.  This is modeled after 
Scala's Try[T]


## Build Info
We are using Gradle 2.0 for my builds.  Please do not checkin Eclipse or Intellij or any IDE specific files.  
For Idea or Eclipse they can be generated using
* `gradlew eclipse`
* `gradlew idea`

Note:
Eclipse IDE might show the following error for groovy files:
"Error compiling Groovy project. Either the Groovy-JDT patch is not installed or JavaBuilder is not being used."
This is because groovy plugin for eclipse needs JavaBuilder but doesn't understand ScalaBuilder.

## Project Versioning
We will be following [JBoss Versioning Convention](https://community.jboss.org/wiki/JBossProjectVersioning?_sscc=t)
* `major.minor.micro.Alpha[n]`
* `major.minor.micro.Beta[n]`
* `major.minor.micro.CR[n]`
Please refer to `AppConfig.groovy` - a single place of change for all the project configuration changes

## License
**This software is licensed under the terms of the [FreeBSD License](http://en.wikipedia.org/wiki/BSD_licenses)**

## Using Try<T>

Please refer to the following presentation.
