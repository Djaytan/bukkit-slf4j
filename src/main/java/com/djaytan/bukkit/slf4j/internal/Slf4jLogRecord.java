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

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.MessageFormatter;

/**
 * Instances of this class represent records related to a logging call through the SLF4J API (i.e.
 * {@link org.slf4j.Logger}).
 *
 * @param slf4jLevel The SLF4J level of the message to log.
 * @param adapterOrSubstituteCallerFqcn The adapter or substitute SLF4J logger fully qualified class
 *     name having performed the logging call.
 * @param marker The named objects used to enrich log statements.
 * @param message The message to be logged.
 * @param arguments The arguments to be used for the message formatting.
 * @param throwable The throwable to be logged.
 */
record Slf4jLogRecord(
    @NotNull Level slf4jLevel,
    @NotNull String adapterOrSubstituteCallerFqcn,
    @Nullable Marker marker,
    @Nullable String message,
    @Nullable Object[] arguments,
    @Nullable Throwable throwable) {

  @NotNull
  java.util.logging.Level julLevel() {
    return switch (slf4jLevel) {
      case TRACE -> java.util.logging.Level.FINEST;
      case DEBUG -> java.util.logging.Level.FINE;
      case INFO -> java.util.logging.Level.INFO;
      case WARN -> java.util.logging.Level.WARNING;
      case ERROR -> java.util.logging.Level.SEVERE;
    };
  }

  @NotNull
  String formattedMessage() {
    return MessageFormatter.basicArrayFormat(message, arguments);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Slf4jLogRecord that = (Slf4jLogRecord) o;
    return slf4jLevel == that.slf4jLevel
        && Objects.equals(adapterOrSubstituteCallerFqcn, that.adapterOrSubstituteCallerFqcn)
        && Objects.equals(marker, that.marker)
        && Objects.equals(message, that.message)
        && Arrays.equals(arguments, that.arguments)
        && Objects.equals(throwable, that.throwable);
  }

  @Override
  public int hashCode() {
    int result =
        Objects.hash(slf4jLevel, adapterOrSubstituteCallerFqcn, marker, message, throwable);
    result = 31 * result + Arrays.hashCode(arguments);
    return result;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Slf4jLogRecord.class.getSimpleName() + "[", "]")
        .add("slf4jLevel=" + slf4jLevel)
        .add("adapterOrSubstituteCallerFqcn='" + adapterOrSubstituteCallerFqcn + "'")
        .add("marker=" + marker)
        .add("message='" + message + "'")
        .add("arguments=" + Arrays.toString(arguments))
        .add("throwable=" + throwable)
        .toString();
  }
}
