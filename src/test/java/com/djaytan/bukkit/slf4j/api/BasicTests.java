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

import static com.djaytan.bukkit.slf4j.api.TestHelper.SAMPLE_LOGGER_NAME;
import static com.djaytan.bukkit.slf4j.api.TestHelper.executeLogging;
import static com.djaytan.bukkit.slf4j.api.TestHelper.flushHandlers;
import static com.djaytan.bukkit.slf4j.api.TestHelper.getJulStreamLogger;
import static com.djaytan.bukkit.slf4j.api.TestHelper.setJulLoggerLevel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

final class BasicTests {

  private static final String SAMPLE_LOG_MESSAGE = "test log message";

  @AfterEach
  void setUp() {
    BukkitLoggerFactory.resetBukkitLogger();
  }

  @ParameterizedTest
  @MethodSource
  void consumer_can_log_at_different_levels_once_having_provided_a_bukkit_logger(
      @NotNull Level slf4jLevel, @NotNull java.util.logging.Level expectedJulLevel) {
    // Arrange
    Consumer<Logger> julLoggerPreparer =
        julLogger -> setJulLoggerLevel(julLogger, java.util.logging.Level.FINEST);

    // Act
    String logOutput =
        executeLogging(
            julLoggerPreparer,
            slf4jLogger -> slf4jLogger.makeLoggingEventBuilder(slf4jLevel).log(SAMPLE_LOG_MESSAGE));

    // Assert
    assertThat(logOutput)
        .contains(getClass().getName())
        .contains("consumer_can_log_at_different_levels_once_having_provided_a_bukkit_logger")
        .contains(expectedJulLevel.getName())
        .contains(SAMPLE_LOG_MESSAGE);
  }

  private static @NotNull Stream<Arguments>
      consumer_can_log_at_different_levels_once_having_provided_a_bukkit_logger() {
    return Stream.of(
        arguments(Level.TRACE, java.util.logging.Level.FINEST),
        arguments(Level.DEBUG, java.util.logging.Level.FINE),
        arguments(Level.INFO, java.util.logging.Level.INFO),
        arguments(Level.WARN, java.util.logging.Level.WARNING),
        arguments(Level.ERROR, java.util.logging.Level.SEVERE));
  }

  @Test
  void consumer_can_log_in_a_simpler_and_more_direct_way() {
    // Act
    String logOutput = executeLogging(slf4jLogger -> slf4jLogger.warn(SAMPLE_LOG_MESSAGE));

    // Assert
    assertThat(logOutput)
        .contains(getClass().getName())
        .contains("consumer_can_log_in_a_simpler_and_more_direct_way")
        .contains(java.util.logging.Level.WARNING.getName())
        .contains(SAMPLE_LOG_MESSAGE);
  }

  @Test
  void logged_message_end_with_line_separator() {
    // Act
    String logOutput = executeLogging(slf4jLogger -> slf4jLogger.info(SAMPLE_LOG_MESSAGE));

    // Assert
    assertThat(logOutput).endsWith(System.lineSeparator());
  }

  @Test
  void consumer_can_log_a_throwable() {
    // Act
    var throwable = new IllegalArgumentException("test illegal argument");
    String logOutput =
        executeLogging(slf4jLogger -> slf4jLogger.info(SAMPLE_LOG_MESSAGE, throwable));

    // Assert
    assertThat(logOutput)
        .contains(
            "java.lang.IllegalArgumentException: test illegal argument"
                + System.lineSeparator()
                + "\tat");
  }

  @Test
  void throwable_stack_trace_end_with_line_separator() {
    // Act
    var throwable = new IllegalArgumentException("test illegal argument");
    String logOutput =
        executeLogging(slf4jLogger -> slf4jLogger.info(SAMPLE_LOG_MESSAGE, throwable));

    // Assert
    assertThat(logOutput).endsWith(System.lineSeparator());
  }

  @Test
  void default_logging_level_is_preserved() {
    // Act
    String logOutput = executeLogging(slf4jLogger -> slf4jLogger.debug(SAMPLE_LOG_MESSAGE));

    // Assert
    assertThat(logOutput).isEmpty();
  }

  @Test
  void change_to_the_logging_level_is_reflected() {
    // Arrange
    Consumer<Logger> julLoggerPreparer =
        julLogger -> setJulLoggerLevel(julLogger, java.util.logging.Level.FINE);

    // Act
    String debugLogOutput =
        executeLogging(julLoggerPreparer, slf4jLogger -> slf4jLogger.debug("test 1"));
    BukkitLoggerFactory.resetBukkitLogger();
    String traceLogOutput =
        executeLogging(julLoggerPreparer, slf4jLogger -> slf4jLogger.trace("test 2"));

    // Assert
    assertThat(debugLogOutput).endsWith("test 1" + System.lineSeparator());
    assertThat(traceLogOutput).isEmpty();
  }

  @Test
  void
      send_a_warning_message_when_an_attempt_to_override_an_already_defined_bukkit_logger_is_detected() {
    // Arrange
    var baos = new ByteArrayOutputStream();
    var julLogger = getJulStreamLogger(SAMPLE_LOGGER_NAME, baos);

    BukkitLoggerFactory.provideBukkitLogger(julLogger);

    var anotherJulLogger = Logger.getLogger("another logger name");

    // Act
    BukkitLoggerFactory.provideBukkitLogger(anotherJulLogger);

    // Assert
    flushHandlers(julLogger);
    String loggedFinalMessage = baos.toString(StandardCharsets.UTF_8);

    assertThat(loggedFinalMessage)
        .contains(BukkitLoggerFactory.class.getName())
        .contains("provideBukkitLogger")
        .contains(java.util.logging.Level.WARNING.getName())
        .contains(
            "Bukkit logger redefinition attempt detected! Any such action is considered as "
                + "unexpected and thus is simply ignored to avoid harms");
  }

  @Test
  void keep_the_already_defined_bukkit_logger_when_an_attempt_to_override_it_is_detected() {
    // Arrange
    var baos = new ByteArrayOutputStream();
    var julLogger = getJulStreamLogger(SAMPLE_LOGGER_NAME, baos);

    BukkitLoggerFactory.provideBukkitLogger(julLogger);

    var anotherJulLogger = Logger.getLogger("another logger name");

    // Act
    BukkitLoggerFactory.provideBukkitLogger(anotherJulLogger);
    var slf4jLogger = LoggerFactory.getLogger(getClass());
    slf4jLogger.info(SAMPLE_LOG_MESSAGE);

    // Assert
    flushHandlers(julLogger);
    String loggedFinalMessage = baos.toString(StandardCharsets.UTF_8);

    assertThat(loggedFinalMessage).contains(SAMPLE_LOG_MESSAGE);
  }

  @Test
  void bukkit_logger_name_must_always_be_used() {
    // Arrange
    String loggerName = "another logger name";
    var julLogger = Logger.getLogger(SAMPLE_LOGGER_NAME);

    BukkitLoggerFactory.provideBukkitLogger(julLogger);

    // Act
    var slf4jLogger = LoggerFactory.getLogger(loggerName);

    // Assert
    assertThat(slf4jLogger.getName()).isEqualTo(julLogger.getName());
  }

  @Test
  void throw_exception_when_getting_logger_without_providing_bukkit_one_first() {
    // Act
    ThrowingCallable call = () -> LoggerFactory.getLogger(SAMPLE_LOGGER_NAME);

    // Assert
    assertThatThrownBy(call)
        .isExactlyInstanceOf(IllegalStateException.class)
        .hasMessage("The Bukkit logger must be defined first");
  }
}
