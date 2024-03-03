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
package com.djaytan.bukkit.slf4j;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

final class TestHelper {

  static final String SAMPLE_LOGGER_NAME = "sample.logger.DummyClass";

  static void testLoggingExecutionOutputExpectedMessage(
      @NotNull Consumer<org.slf4j.Logger> slf4jLoggerCaller, @NotNull String expectedLogMessage) {
    String logOutput = executeLogging(slf4jLoggerCaller);
    assertThat(logOutput).endsWith(expectedLogMessage + System.lineSeparator());
  }

  /**
   * Executes logging through SLF4J while relying on the {@link BukkitLoggerServiceProvider} for
   * testing purposes.
   *
   * @param slf4jLoggerCaller The callback in charge of interacting with the generated SLF4J logger
   *     for testing purposes.
   * @return The log output after having manipulated the generated SLF4J logger.
   */
  static @NotNull String executeLogging(@NotNull Consumer<org.slf4j.Logger> slf4jLoggerCaller) {
    return executeLogging(x -> {}, slf4jLoggerCaller);
  }

  /**
   * Executes logging through SLF4J while relying on the {@link BukkitLoggerServiceProvider} for
   * testing purposes.
   *
   * @param prepareJulLogger The callback in charge of finalizing the JUL logger preparation.
   * @param slf4jLoggerCaller The callback in charge of interacting with the generated SLF4J logger
   *     for testing purposes.
   * @return The log output after having manipulated the generated SLF4J logger.
   */
  static @NotNull String executeLogging(
      @NotNull Consumer<Logger> prepareJulLogger,
      @NotNull Consumer<org.slf4j.Logger> slf4jLoggerCaller) {
    var baos = new ByteArrayOutputStream();
    var julLogger = getJulStreamLogger(SAMPLE_LOGGER_NAME, baos);
    prepareJulLogger.accept(julLogger);

    BukkitLoggerFactory.provideBukkitLogger(julLogger);

    var slf4jLogger = LoggerFactory.getLogger(SAMPLE_LOGGER_NAME);
    slf4jLoggerCaller.accept(slf4jLogger);

    flushHandlers(julLogger);
    return baos.toString(StandardCharsets.UTF_8);
  }

  static @NotNull Logger getJulStreamLogger(
      @NotNull String loggerName, @NotNull ByteArrayOutputStream byteArrayOutputStream) {
    var logger = Logger.getLogger(loggerName);
    var logHandler = new StreamHandler(byteArrayOutputStream, new SimpleFormatter());
    logger.addHandler(logHandler);
    return logger;
  }

  static void setJulLoggerLevel(@NotNull Logger logger, @NotNull java.util.logging.Level level) {
    logger.setLevel(level);

    for (Handler handler : logger.getHandlers()) {
      handler.setLevel(level);
    }

    Logger parent = logger.getParent();
    while (parent != null) {
      for (Handler handler : parent.getHandlers()) {
        handler.setLevel(level);
      }
      parent = parent.getParent();
    }
  }

  static void flushHandlers(@NotNull Logger logger) {
    for (Handler handler : logger.getHandlers()) {
      handler.flush();
    }
  }
}
