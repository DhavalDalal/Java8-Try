# Java8 Try<T> 
To wrap checked exceptions and to make failures explicit.  This is a translation
of Scala's Try[T].  Basic api to do with container semantics like transformation, 
filtering, flatMap and failure recovery are present as of date,  while other is
being evolved. 


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
* Please refer to the following presentation.
* <iframe src="//www.slideshare.net/slideshow/embed_code/key/vrqMJbJpW8Cz9o?startSlide=58" width="425" height="355" frameborder="0" marginwidth="0" marginheight="0" scrolling="no" style="border:1px solid #CCC; border-width:1px; margin-bottom:5px; max-width: 100%;" allowfullscreen> </iframe> <div style="margin-bottom:5px"> <strong> <a href="//www.slideshare.net/DhavalDalal/jumpingwithjava8" title="Jumping-with-java8" target="_blank">Jumping-with-java8</a> </strong> from <strong><a href="//www.slideshare.net/DhavalDalal" target="_blank">Dhaval Dalal</a></strong> </div>


