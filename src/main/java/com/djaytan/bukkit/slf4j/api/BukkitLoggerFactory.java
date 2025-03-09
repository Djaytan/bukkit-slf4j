/*
 * The MIT License
 * Copyright © 2023 Loïc DUBOIS-TERMOZ (alias Djaytan)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.djaytan.bukkit.slf4j.api;

import com.djaytan.bukkit.slf4j.internal.BukkitLoggerAdapter;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * This class is responsible for creating SLF4J loggers based on a provided JUL one. This is a
 * specific scenario that we need to rely on when creating Bukkit plugins. That's due to the logging
 * design being based on a single instance shared across the whole plugin (mainly for appending the
 * plugin name while not displaying technical information such as the class full name).
 */
public final class BukkitLoggerFactory implements ILoggerFactory {

  private static java.util.logging.Logger staticLogger;

  @Internal
  public BukkitLoggerFactory() {
    // Bukkit logger must be provided by the library consumer
  }

  /**
   * Provides the Bukkit logger to be returned by any subsequent call to {@link
   * org.slf4j.LoggerFactory#getLogger(String)}.
   *
   * @param jdk14Logger The single JUL logger instance to be used each time the {@link
   *     org.slf4j.LoggerFactory#getLogger(String)} method is called.
   */
  public static void provideBukkitLogger(@NotNull java.util.logging.Logger jdk14Logger) {
    if (staticLogger != null) {
      staticLogger.warning(
          "Bukkit logger redefinition attempt detected! Any such action is considered as "
              + "unexpected and thus is simply ignored to avoid harms");
      return;
    }
    staticLogger = jdk14Logger;
  }

  /** Resets the Bukkit logger. */
  @TestOnly
  static void resetBukkitLogger() {
    staticLogger = null;
  }

  @Override
  @Contract("null -> fail")
  public Logger getLogger(@Nullable String name) {
    // Name provided as argument is discarded in favor of the static Bukkit logger one
    if (staticLogger == null) {
      throw new IllegalStateException("The Bukkit logger must be defined first");
    }
    return new BukkitLoggerAdapter(staticLogger, staticLogger.getName());
  }
}
