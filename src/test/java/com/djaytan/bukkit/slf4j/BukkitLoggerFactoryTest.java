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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mock.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
final class BukkitLoggerFactoryTest {

  private static final String LOGGER_NAME = "test-logger-name";

  @Mock(strictness = Strictness.LENIENT)
  private java.util.logging.Logger sampleLogger;

  private final BukkitLoggerFactory bukkitLoggerFactory = new BukkitLoggerFactory();

  @BeforeEach
  void beforeEach() {
    when(sampleLogger.isLoggable(Level.INFO)).thenReturn(true);
  }

  @AfterEach
  void tearDown() throws Exception {
    Field staticLoggerField = BukkitLoggerFactory.class.getDeclaredField("staticLogger");
    staticLoggerField.setAccessible(true);
    staticLoggerField.set(null, null);
  }

  @Test
  void givenUndefinedJdk14Logger_whenRetrievingLogger_shouldThrowException() {
    // When
    ThrowingCallable call = () -> bukkitLoggerFactory.getLogger(LOGGER_NAME);

    // Then
    assertThatThrownBy(call)
        .isExactlyInstanceOf(IllegalStateException.class)
        .hasMessage("The Bukkit logger must be defined first");
  }

  @Test
  void givenDefinedJdk14Logger_whenRetrievingLogger_shouldReturnTheDefinedOne(
      @Captor ArgumentCaptor<LogRecord> interceptedLogRecord) {
    // Given
    BukkitLoggerFactory.provideBukkitLogger(sampleLogger);

    // When
    Logger retrievedLogger = bukkitLoggerFactory.getLogger(LOGGER_NAME);
    retrievedLogger.info("test");

    // Then
    verify(sampleLogger).log(interceptedLogRecord.capture());
    var logRecord = interceptedLogRecord.getValue();
    assertThat(logRecord.getLevel()).isEqualTo(Level.INFO);
    assertThat(logRecord.getMessage()).isEqualTo("test");
  }

  @Test
  void givenDefinedJdk14Logger_whenRedefiningIt_shouldWarnAndIgnore() {
    // Given
    BukkitLoggerFactory.provideBukkitLogger(sampleLogger);

    // When
    var anotherLogger = mock(java.util.logging.Logger.class);
    BukkitLoggerFactory.provideBukkitLogger(anotherLogger);

    // Then
    verify(sampleLogger)
        .warning(
            "Bukkit logger redefinition attempt detected! Any such action is considered as "
                + "unexpected and thus is simply ignored to avoid harms");
  }

  @Test
  void givenRedefinitionAttempt_whenRetrievingLogger_shouldReturnTheInitialOne() {
    // Given
    var anotherLogger = mock(java.util.logging.Logger.class);
    BukkitLoggerFactory.provideBukkitLogger(sampleLogger);
    BukkitLoggerFactory.provideBukkitLogger(anotherLogger);

    // When
    Logger retrievedLogger = bukkitLoggerFactory.getLogger(LOGGER_NAME);
    retrievedLogger.info("test");

    // Then
    verify(sampleLogger).log(any(LogRecord.class));
    verifyNoInteractions(anotherLogger);
  }

  /*
   * We don't care about the name, hence the fact we don't perform any check on it.
   */
  @Test
  void
      givenDefinedJdk14Logger_whenRetrievingLoggerWithoutProvidingName_shouldReturnTheDefinedLogger() {
    // Given
    BukkitLoggerFactory.provideBukkitLogger(sampleLogger);

    // When
    Logger retrievedLogger = bukkitLoggerFactory.getLogger(null);
    retrievedLogger.info("test");

    // Then
    verify(sampleLogger).log(any(LogRecord.class));
  }
}
