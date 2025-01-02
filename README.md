<h1 align="center">Bukkit SLF4J Bridge</h1>

<div align="center">

![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Fcom%2Fdjaytan%2Fbukkit%2Fbukkit-slf4j%2Fmaven-metadata.xml)
[![CI](https://github.com/Djaytan/bukkit-slf4j/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Djaytan/bukkit-slf4j/actions/workflows/ci.yml)
[![semantic-release: conventional-commits](https://img.shields.io/badge/semantic--release-conventional--commits-e10079?logo=semantic-release)](https://github.com/semantic-release/semantic-release)  
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Djaytan_bukkit-slf4j&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Djaytan_bukkit-slf4j)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Djaytan_bukkit-slf4j&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Djaytan_bukkit-slf4j)
[![OpenSSF Best Practices](https://www.bestpractices.dev/projects/8432/badge)](https://www.bestpractices.dev/projects/8432)
[![OpenSSF Scorecard](https://api.securityscorecards.dev/projects/github.com/Djaytan/bukkit-slf4j/badge)](https://securityscorecards.dev/viewer/?uri=github.com/Djaytan/bukkit-slf4j)  
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FDjaytan%2Fbukkit-slf4j.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2FDjaytan%2Fbukkit-slf4j?ref=badge_shield)

SLF4J bridge to Bukkit's logger for plugins.

</div>

## Why Using It?

For relying on [SLF4J](https://www.slf4j.org/) as a standard logging solution in a Bukkit
environment (including forks like Spigot, PaperMC, ...).

Bukkit [exposes a JUL logger](https://github.com/Bukkit/Bukkit/blob/f210234e59275330f83b994e199c76f6abd41ee7/src/main/java/org/bukkit/plugin/Plugin.java#L171-L178),
[built by itself](https://github.com/Bukkit/Bukkit/blob/f210234e59275330f83b994e199c76f6abd41ee7/src/main/java/org/bukkit/plugin/PluginLogger.java)
for each plugin individually.
Because of such setup, the [slf4j-jdk14](https://github.com/qos-ch/slf4j/tree/master/slf4j-jdk14)
bridge will work **without** taking into
account [the custom Bukkit's log format](https://github.com/Bukkit/Bukkit/blob/f210234e59275330f83b994e199c76f6abd41ee7/src/main/java/org/bukkit/plugin/PluginLogger.java#L22-L34).
The fact PaperMC exposes a [
`getSLF4JLogger()` method](https://jd.papermc.io/paper/1.18/org/bukkit/plugin/Plugin.html#getSLF4JLogger())
doesn't really change anything.

While you may consider injecting the plugin's logger instance yourself in each place where one is
required, this is not convenient nor applicable for any library relying purely on SLF4J like
[HikariCP](https://github.com/brettwooldridge/HikariCP/blob/a28b6ec81d9a22229553cce84b147c7bdd0c6490/src/main/java/com/zaxxer/hikari/HikariDataSource.java#L42).

This bridge handles for you the Bukkit's logger injection in each place where required, including
libraries completely agnostic to Bukkit and/or any logging implementation.
You just need to provide the Bukkit logger instance at plugin enabling time, and
[you are good to go](https://www.slf4j.org/manual.html)!

Its implementation is inspired by
the [slf4j-jdk14](https://github.com/qos-ch/slf4j/tree/master/slf4j-jdk14) one.

## Setup

The library is available in
the [Maven Central Repository](https://central.sonatype.com/artifact/com.djaytan.bukkit/bukkit-slf4j/overview).

### Maven

```xml

<dependency>
  <groupId>com.djaytan.bukkit</groupId>
  <artifactId>bukkit-slf4j</artifactId>
  <version>VERSION_HERE</version>
</dependency>
```

### Gradle

    implementation group: 'com.djaytan.bukkit', name: 'bukkit-slf4j', version: 'VERSION_HERE'

## How To Use

Once added, you simply need to pass the Bukkit logger when enabling plugin like as follows:

```java
public class YourPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    // It's important to call this method as soon as possible, especially before loading any class
    BukkitLoggerFactory.provideBukkitLogger(this.getLogger());

    // Then execute your plugin's specific logic
  }
}
```

Then you can declare a standard logger injection point as follows:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyClass {

  // The bridge will inject the Bukkit logger automatically
  private static final Logger log = LoggerFactory.getLogger(MyClass.class);

  public void myMethod() {
    log.info("Bukkit x SLF4J");
  }
}
```

You can find a concrete example of Bukkit plugin using this
extension [here](https://github.com/Djaytan/mc-jobs-reborn-patch-place-break).

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on ways to help us.

Take care to always follow our [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the
[tags on this repository](https://github.com/Djaytan/bukkit-slf4j/tags).

## Security Policy

In case you think having found a security vulnerability, please consult
our [Security Policy](docs/SECURITY.md).

## Licence

This project is under the [MIT](https://opensource.org/licenses/MIT) license.

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FDjaytan%2Fbukkit-slf4j.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2FDjaytan%2Fbukkit-slf4j?ref=badge_large)
