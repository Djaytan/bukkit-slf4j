# bukkit-slf4j

![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Fcom%2Fdjaytan%2Fbukkit%2Fbukkit-slf4j%2Fmaven-metadata.xml)
[![CI](https://github.com/Djaytan/bukkit-slf4j/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Djaytan/bukkit-slf4j/actions/workflows/ci.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Djaytan_bukkit-slf4j&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Djaytan_bukkit-slf4j)  
[![semantic-release: conventional-commits](https://img.shields.io/badge/semantic--release-conventional--commits-e10079?logo=semantic-release)](https://github.com/semantic-release/semantic-release)
[![OpenSSF Best Practices](https://www.bestpractices.dev/projects/8432/badge)](https://www.bestpractices.dev/projects/8432)
[![OpenSSF Scorecard](https://api.securityscorecards.dev/projects/github.com/Djaytan/bukkit-slf4j/badge)](https://securityscorecards.dev/viewer/?uri=github.com/Djaytan/bukkit-slf4j)

SLF4J bridge to Bukkit's logger for plugin.

## Why Using It

Even if PaperMC provides
a [`getSLF4JLogger()` method](https://jd.papermc.io/paper/1.18/org/bukkit/plugin/Plugin.html#getSLF4JLogger()),
you need to inject the retrieved instance in any class that need to log something. When it's time to
deal with libraries like HikariCP, things become even harder when wanted to have clean console
output when plugin is running. Finally, the previously mentioned method is not available with
Spigot.

This solution goes beyond by overcoming all these limitations with a simple approach highly inspired
from the [slf4j-jdk14](https://github.com/qos-ch/slf4j/tree/master/slf4j-jdk14) one.

## Setup

The library is available in
the [Maven Central Repository](https://central.sonatype.com/artifact/com.djaytan.bukkit/bukkit-slf4j/overview).

### Maven

```xml

<dependency>
  <groupId>com.djaytan.bukkit</groupId>
  <artifactId>bukkit-slf4j</artifactId>
  <version>2.0.0</version>
</dependency>
```

### Gradle

    implementation group: 'com.djaytan.bukkit', name: 'bukkit-slf4j', version: '2.0.0'

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

Then you can simply declare a new logger as follows:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyClass {

  // The Bukkit logger will be injected
  private static final Logger log = LoggerFactory.getLogger(LogExample.class);

  public void dummyMethod() {
    log.atInfo().log("Bukkit x SLF4J");
  }
}
```

Or if using Lombok's [`@Slf4j` annotation](https://projectlombok.org/features/log):

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyClass {

  public void dummyMethod() {
    log.atInfo().log("Bukkit x SLF4J");
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
