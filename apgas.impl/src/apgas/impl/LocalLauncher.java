/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package apgas.impl;

import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;

import apgas.util.Launcher;

/**
 * The {@link Launcher} class implements a launcher on the localhost using a
 * {#link ProcessBuilder}.
 */
final class LocalLauncher implements Launcher {
  /**
   * The processes we spawned.
   */
  private final List<Process> processes = new ArrayList<Process>();

  /**
   * Status of the shutdown sequence (0 live, 1 shutting down the Global
   * Runtime, 2 shutting down the JVM).
   */
  private int dying;

  /**
   * Constructs a new {#link LocalLauncher} instance.
   */
  LocalLauncher() {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> terminate()));
  }

  @Override
  public void launch(int n, List<String> command) throws Exception {
    for (int i = 0; i < n; i++) {
      final ProcessBuilder pb = new ProcessBuilder(command);
      pb.redirectOutput(Redirect.INHERIT);
      pb.redirectError(Redirect.INHERIT);
      Process process = pb.start();
      synchronized (this) {
        if (dying <= 1) {
          processes.add(process);
          process = null;
        }
      }
      if (process != null) {
        process.destroyForcibly();
        throw new IllegalStateException("Shutdown in progress");
      }
    }
  }

  @Override
  public boolean healthy() {
    synchronized (this) {
      if (dying > 0) {
        return false;
      }
    }
    for (final Process process : processes) {
      if (!process.isAlive()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public synchronized void shutdown() {
    if (dying == 0) {
      dying = 1;
    }
  }

  /**
   * Kills all spawned processes.
   */
  private void terminate() {
    synchronized (this) {
      dying = 2;
    }
    for (final Process process : processes) {
      process.destroyForcibly();
    }
  }
}
