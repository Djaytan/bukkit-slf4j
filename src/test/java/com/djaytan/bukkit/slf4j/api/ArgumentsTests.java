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

import static com.djaytan.bukkit.slf4j.api.TestHelper.executeLogging;
import static com.djaytan.bukkit.slf4j.api.TestHelper.testLoggingExecutionOutputExpectedMessage;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

final class ArgumentsTests {

  private static final String SAMPLE_LOG_TEMPLATE_MESSAGE = "test template: {}, {}";
  private static final Object SAMPLE_LOG_ARGUMENT_1 = "log-argument-1";
  private static final Object SAMPLE_LOG_ARGUMENT_2 = "log-argument-2";

  @Test
  void consumer_can_log_with_arguments() {
    testLoggingExecutionOutputExpectedMessage(
        slf4jLogger ->
            slf4jLogger.info(
                SAMPLE_LOG_TEMPLATE_MESSAGE, SAMPLE_LOG_ARGUMENT_1, SAMPLE_LOG_ARGUMENT_2),
        "test template: log-argument-1, log-argument-2");
  }

  @Test
  void consumer_can_log_with_only_one_argument() {
    testLoggingExecutionOutputExpectedMessage(
        slf4jLogger -> slf4jLogger.info("{}", SAMPLE_LOG_ARGUMENT_1), "log-argument-1");
  }

  @Test
  void consumer_can_log_with_non_string_argument() {
    testLoggingExecutionOutputExpectedMessage(slf4jLogger -> slf4jLogger.info("{}", 14), "14");
  }

  @Test
  void consumer_can_log_complex_object_as_argument() {
    var complexObject =
        List.of(LocalDateTime.of(2024, 2, 29, 23, 54, 38), LocalDateTime.MAX, LocalDateTime.MIN);

    testLoggingExecutionOutputExpectedMessage(
        slf4jLogger -> slf4jLogger.info("List of local date-time objects: {}", complexObject),
        "List of local date-time objects: [2024-02-29T23:54:38, "
            + "+999999999-12-31T23:59:59.999999999, -999999999-01-01T00:00]");
  }

  /** Test relying on ellipse method such as {@link Logger#info(String, Object...)}. */
  @Test
  void consumer_can_log_lots_of_arguments() {
    testLoggingExecutionOutputExpectedMessage(
        slf4jLogger ->
            slf4jLogger.info(
                "{}, {}, {}, {}, {}, {}, {}, {}, {}, {}",
                SAMPLE_LOG_ARGUMENT_1,
                SAMPLE_LOG_ARGUMENT_2,
                3,
                4,
                5,
                6,
                7,
                8,
                9,
                10),
        "log-argument-1, log-argument-2, 3, 4, 5, 6, 7, 8, 9, 10");
  }

  @Test
  @SuppressWarnings({"java:S3457", "LoggingPlaceholderCountMatchesArgumentCount"})
  void exceeding_arguments_shall_be_ignored() {
    testLoggingExecutionOutputExpectedMessage(slf4jLogger -> slf4jLogger.info("{}", 14, 15), "14");
  }

  @Test
  @SuppressWarnings({"java:S3457", "LoggingPlaceholderCountMatchesArgumentCount"})
  void templates_must_remain_for_missing_argument() {
    testLoggingExecutionOutputExpectedMessage(
        slf4jLogger -> slf4jLogger.info("{}, {}", 14), "14, {}");
  }

  @Test
  void last_argument_is_treated_specially_if_it_is_a_throwable() {
    // Arrange
    var throwable = new IllegalStateException("test invalid state");

    // Act
    String logOutput = executeLogging(slf4jLogger -> slf4jLogger.info("{}", 14, throwable));

    // Assert
    assertThat(logOutput)
        .contains(
            "14"
                + System.lineSeparator()
                + "java.lang.IllegalStateException: test invalid state"
                + System.lineSeparator()
                + "\tat");
  }

  @Test
  @SuppressWarnings({"java:S3457", "LoggingPlaceholderCountMatchesArgumentCount"})
  void last_argument_is_treated_specially_if_it_is_a_throwable_even_with_missing_argument() {
    // Arrange
    var throwable = new IllegalStateException("test invalid state");

    // Act
    String logOutput = executeLogging(slf4jLogger -> slf4jLogger.info("{}, {}", 14, throwable));

    // Assert
    assertThat(logOutput)
        .contains(
            "14, {}"
                + System.lineSeparator()
                + "java.lang.IllegalStateException: test invalid state"
                + System.lineSeparator()
                + "\tat");
  }
}
