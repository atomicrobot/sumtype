# Sum Type

[![Build Status](https://travis-ci.org/madebyatomicrobot/sumtype.svg?branch=master)](https://travis-ci.org/madebyatomicrobot/sumtype)

Sum Type is an annotation processor that makes it simple to emulate sum types in Java via visitors.

See https://en.wikipedia.org/wiki/Tagged_union for an in depth explanation of sum types, read on, or check out the samples.

Motivation
==========

Java lacks support for sum types which can be useful.  We can get close with a composite value object and a visitor, but
that implementation is cumbersome to write and maintain.

Usage
=====

Assuming you had three types (`Loading`, `Error`, `Results`) you wanted to capture in a sum type, create an interface
 with those types and annoate it with `@SumType`

```
@SumType
public interface Query {
    Loading loading();
    Results results();
    Error error();
}
```

This will generate several classes for you to use, most important being `QuerySumType` and `QuerySumTypeVisitor`.

To build a new `QuerySumType`, the value holder, use one of the generated static factory methods:

```
QuerySumType query = QuerySumType.ofLoading(new Loading());
```

To ensure your code handles all of the cases a sum type can be in, pass in an implementation of a generated visitor interface.

```
query.accept(new QuerySumTypeVisitor() {
    @Override
    public void visitLoading(Loading loading) {
        System.out.println(loading.toString());
    }

    @Override
    public void visitResults(Results results) {
        System.out.println(results.toString());
    }

    @Override
    public void visitError(Error error) {
        System.out.println(error.toString());
    }
});
```

## Philosophy

Sum types are exceptionally useful when dealing with event based architectures and we would like to minimize the friction
required to make use of them in a language where they are not a first class citizen.

Including in your project
=========================

```groovy
buildscript {
  repositories {
    mavenCentral()
  }

  dependencies {
    // Or latest versions
    classpath 'com.android.tools.build:gradle:1.1.2'
    classpath 'com.neenbedankt.gradle.plugins:android-apt:1.4'  // Or your preferred apt plugin if not using Android (see samples)
  }
}

apply plugin: 'com.android.application'
apply plugin: 'android-apt'  // Or your preferred apt plugin if not using Android (see samples)

dependencies {
  apt 'com.madebyatomicrobot:sumtype-compiler:{latest-version}'
  compile 'com.madebyatomicrobot:sumtype-annotations:{latest-version}'
}
```

| Artifact | Latest Version |
|------|---------|
| sumtype-compiler | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.madebyatomicrobot/sumtype-compiler/badge.png)](https://maven-badges.herokuapp.com/maven-central/com.madebyatomicrobot/sumtype-compiler/) |
| sumtype-annotations | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.madebyatomicrobot/sumtype-annotations/badge.png)](https://maven-badges.herokuapp.com/maven-central/com.madebyatomicrobot/sumtype-annotations/) |


Snapshots of the development version are available in [Sonatype’s `snapshots` repository][snap].


License
=======

    Copyright 2015 Atomic Robot LLC

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

About Atomic Robot
=======

[![Atomic Robot Logo](website/static/ar_logo.png)](http://www.madebyatomicrobot.com)  
[madebyatomicrobot.com](http://www.madebyatomicrobot.com)

We are iOS & Android experts that develop native mobile applications for startups and enterprise clients.
From wireframes to final design, we understand the unique challenges and opportunities in designing for mobile.
From concept to launch, we can guide you every step of the way.

Our clients partner with us to help them deliver exceptional mobile solutions to grow their business.
[Hire us][hire-us] to work on your next project.

[snap]: https://oss.sonatype.org/content/repositories/snapshots/
[hire-us]: http://www.madebyatomicrobot.com/questionnaire/
