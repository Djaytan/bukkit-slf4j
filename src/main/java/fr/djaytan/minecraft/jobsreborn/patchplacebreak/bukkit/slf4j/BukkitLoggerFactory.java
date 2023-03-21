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
package fr.djaytan.minecraft.jobsreborn.patchplacebreak.bukkit.slf4j;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * This class is highly inspired from slf4j-jdk14 module.
 *
 * <p>The purpose is to permit the creation of a SLF4J instance of the Bukkit logger to ease the
 * decoupling.
 */
public final class BukkitLoggerFactory implements ILoggerFactory {

  private static java.util.logging.Logger bukkitLogger;

  public static void provideBukkitLogger(java.util.logging.Logger bukkitLogger) {
    if (BukkitLoggerFactory.bukkitLogger != null) {
      BukkitLoggerFactory.bukkitLogger.warning("The Bukkit logger has already been provided.");
      return;
    }
    BukkitLoggerFactory.bukkitLogger = bukkitLogger;
  }

  @Override
  public Logger getLogger(String name) {
    return new BukkitLoggerAdapter(bukkitLogger);
  }
}
