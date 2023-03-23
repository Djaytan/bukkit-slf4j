# bukkit-slf4j

[![](https://jitpack.io/v/Djaytan/bukkit-slf4j.svg)](https://jitpack.io/#Djaytan/bukkit-slf4j)
![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/djaytan/bukkit-slf4j/maven.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Djaytan_bukkit-slf4j&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Djaytan_bukkit-slf4j)

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

## How To Use

The dependency can be obtained through JitPack by following steps
described [here](https://jitpack.io/#Djaytan/bukkit-slf4j/) (Maven, Gradle and so on are supported).

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

## Authors

* [Djaytan](https://github.com/Djaytan) - *Initial work*

See also the list of
[contributors](https://github.com/Djaytan/bukkit-slf4j/graphs/contributors)
who participated in this project.

## Licence

This project is under the [MIT](https://opensource.org/licenses/MIT) license.
