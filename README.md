# bukkit-slf4j

[![](https://jitpack.io/v/Djaytan/bukkit-slf4j.svg)](https://jitpack.io/#Djaytan/bukkit-slf4j)

SLF4J bridge to Bukkit's logger for plugin.

This project is highly inspired from
the [slf4j-jdk14](https://github.com/qos-ch/slf4j/tree/master/slf4j-jdk14) one.

Even if PaperMC provide
a [`getSLF4JLogger()` method](https://jd.papermc.io/paper/1.18/org/bukkit/plugin/Plugin.html#getSLF4JLogger()),
you need to inject the retrieved instance in any class that need to log something. When it's time to
deal with libraries like HikariCP, things become even harder when wanted to have clean console
output when plugin is running. Finally, the previously mentioned method is not available with
Spigot.

This solution goes beyond by overcoming all these limitations.

## How to use

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

Then you can simply declare loggers as follows:

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

You can find a concrete Bukkit plugin using this
extension [here](https://github.com/Djaytan/mc-jobs-reborn-patch-place-break).
