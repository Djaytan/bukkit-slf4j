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
package com.djaytan.bukkit.slf4j.internal;

import java.util.ResourceBundle;
import java.util.logging.LogRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.helpers.AbstractLogger;
import org.slf4j.helpers.LegacyAbstractLogger;
import org.slf4j.helpers.SubstituteLogger;
import org.slf4j.spi.DefaultLoggingEventBuilder;

/** This class is responsible for creating JUL {@link LogRecord}s based on SLF4J data. */
final class JulLogRecordFactory {

  /**
   * Fully qualified class names which must be considered as implementation specific and thus
   * ignored when inferring the caller location.
   */
  private static final String[] LOGGER_IMPL_CLASS_NAMES = {
    BukkitLoggerAdapter.class.getName(),
    LegacyAbstractLogger.class.getName(),
    AbstractLogger.class.getName(),
    SubstituteLogger.class.getName(),
    DefaultLoggingEventBuilder.class.getName(),
    JulLogRecordFactory.class.getName()
  };

  /**
   * Creates a JUL {@link LogRecord} based on the provided {@link Slf4jLogRecord} and a logger name.
   *
   * <p>In this implementation we try to fill the {@link LogRecord} object as much as possible.
   * Fortunately, some fields are already generated at construction time (e.g. {@link
   * LogRecord#getSequenceNumber()}, {@link LogRecord#getLongThreadID()} or {@link
   * LogRecord#getInstant()}, ...).
   *
   * <p>Note: the {@link LogRecord#setParameters(Object[])}, {@link
   * LogRecord#setResourceBundle(ResourceBundle)} and {@link
   * LogRecord#setResourceBundleName(String)} are never called for message formatting since it is
   * already handled by SLF4J (see {@link Slf4jLogRecord#formattedMessage()}). Furthermore, SLF4J
   * only supports a single formatting style (i.e. its own).
   *
   * @param slf4jLogRecord The SLF4J log record.
   * @param loggerName The logger name.
   * @return The newly created JUL {@link LogRecord}.
   */
  static @NotNull LogRecord create(
      @NotNull Slf4jLogRecord slf4jLogRecord, @NotNull String loggerName) {
    var julLogRecord = new LogRecord(slf4jLogRecord.julLevel(), slf4jLogRecord.formattedMessage());
    julLogRecord.setLoggerName(loggerName);
    julLogRecord.setThrown(slf4jLogRecord.throwable());

    var callerLocation = inferCallerLocation(slf4jLogRecord.adapterOrSubstituteCallerFqcn());

    if (callerLocation != null) {
      julLogRecord.setSourceClassName(callerLocation.sourceClassName());
      julLogRecord.setSourceMethodName(callerLocation.sourceMethodName());
    }

    return julLogRecord;
  }

  /**
   * Tries to infer the caller location and eventually return a {@link CallerLocation} object.
   *
   * <p>This implementation relies on the execution stack trace when attempting to retrieve the
   * class name and method name of the caller. One "hacky" way found to retrieve the execution stack
   * trace is by generating an exception stack trace through {@link Throwable#getStackTrace()}. Once
   * retrieved, we simply iterate over the stack trace elements until finding the caller frame.
   *
   * <p>A better approach for the implementation would be to rely on the {@link StackWalker} class
   * instead since that's the official and supported way for answering our specific need here. If we
   * want to improve the implementation, it seems to be the way to go. The JUL implementation has
   * gone into this direction, for example, since a recent version of JDK higher than 8 (see the
   * {@link LogRecord}{@code #inferCaller()} method for details). Furthermore, it will make the code
   * more testable (that's not fully the case with the current implementation because of <code>
   * new Throwable()</code> call).
   *
   * @param adapterOrSubstituteCallerFqcn The adapter or substitute SLF4J logger fully qualified
   *     class name having performed the logging call.
   * @return The inferred caller location if found, null otherwise
   */
  private static @Nullable CallerLocation inferCallerLocation(
      @NotNull String adapterOrSubstituteCallerFqcn) {
    // The first element is the top-most call on the execution stack i.e. always the following line:
    // com.djaytan.bukkit.slf4j/com.djaytan.bukkit.slf4j.internal.JulLogRecordFactory.inferCallerLocation(JulLogRecordFactory.java:<line_number>)
    StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();

    // First, search for a method in a logger implementation class.
    int firstLoggerImplClassIndex = -1;
    for (int i = 0; i < stackTraceElements.length; i++) {
      String className = stackTraceElements[i].getClassName();

      if (isLoggerImplClass(className, adapterOrSubstituteCallerFqcn)) {
        firstLoggerImplClassIndex = i;
        break;
      }
    }

    // Now search for the first frame called before the logger implementation classes.
    int inferedCallerClassNameIndex = -1;
    for (int i = firstLoggerImplClassIndex + 1; i < stackTraceElements.length; i++) {
      String className = stackTraceElements[i].getClassName();

      if (!isLoggerImplClass(className, adapterOrSubstituteCallerFqcn)) {
        inferedCallerClassNameIndex = i;
        break;
      }
    }

    // We haven't found a suitable frame, so let's just punt. This is acceptable as we are only
    // committed to making a "best effort" here.
    if (inferedCallerClassNameIndex == -1) {
      return null;
    }

    StackTraceElement stackTraceElement = stackTraceElements[inferedCallerClassNameIndex];
    return new CallerLocation(stackTraceElement.getClassName(), stackTraceElement.getMethodName());
  }

  private static boolean isLoggerImplClass(
      @NotNull String className, @NotNull String adapterOrSubstituteCallerFqcn) {
    if (className.equals(adapterOrSubstituteCallerFqcn)) {
      return true;
    }

    for (String loggerImplClassName : LOGGER_IMPL_CLASS_NAMES) {
      if (loggerImplClassName.equals(className)) {
        return true;
      }
    }
    return false;
  }

  private record CallerLocation(
      @NotNull String sourceClassName, @NotNull String sourceMethodName) {}
}
