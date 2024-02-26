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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Marker;
import org.slf4j.helpers.LegacyAbstractLogger;

/**
 * This class is responsible for adapting the Bukkit logger instance against the SLF4J {@link
 * org.slf4j.Logger} interface.
 */
final class BukkitLoggerAdapter extends LegacyAbstractLogger {

  private final transient Logger logger;

  BukkitLoggerAdapter(@NotNull Logger logger, @NotNull String name) {
    super.name = name;
    this.logger = logger;
  }

  @Override
  public boolean isTraceEnabled() {
    return logger.isLoggable(Level.FINEST);
  }

  @Override
  public boolean isDebugEnabled() {
    return logger.isLoggable(Level.FINE);
  }

  @Override
  public boolean isInfoEnabled() {
    return logger.isLoggable(Level.INFO);
  }

  @Override
  public boolean isWarnEnabled() {
    return logger.isLoggable(Level.WARNING);
  }

  @Override
  public boolean isErrorEnabled() {
    return logger.isLoggable(Level.SEVERE);
  }

  @Override
  protected String getFullyQualifiedCallerName() {
    return getClass().getName();
  }

  @Override
  protected void handleNormalizedLoggingCall(
      @NotNull org.slf4j.event.Level level,
      @Nullable Marker marker,
      @Nullable String msg,
      @Nullable Object[] args,
      @Nullable Throwable throwable) {
    var slf4jLogRecord =
        new Slf4jLogRecord(level, getFullyQualifiedCallerName(), marker, msg, args, throwable);
    var julLogRecord = JulLogRecordFactory.create(slf4jLogRecord, name);
    logger.log(julLogRecord);
  }
}
