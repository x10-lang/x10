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

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import apgas.Configuration;

/**
 * The {@link SshLauncher} class implements a launcher using ssh.
 */
final class SshLauncher implements Launcher {
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
   * Constructs a new {@link SshLauncher} instance.
   */
  SshLauncher() {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> terminate()));
  }

  @Override
  public void launch(int n, List<String> command, boolean verbose)
      throws Exception {
    final ProcessBuilder pb = new ProcessBuilder(command);
    pb.redirectOutput(Redirect.INHERIT);
    pb.redirectError(Redirect.INHERIT);
    command.add(0, "ssh");
    command.add(1, "-t");
    command.add(2, "-t");
    final String hostfile = System.getProperty(Configuration.APGAS_HOSTFILE,
        "hostfile");
    Iterator<String> hosts = null;
    boolean warningEmitted = false;
    try {
      hosts = Files.readAllLines(FileSystems.getDefault().getPath(hostfile))
          .iterator();
    } catch (final IOException e) {
      System.err.println("[APGAS] Unable to read hostfile: " + hostfile);
      System.err.println("[APGAS] Defaulting to localhost");
      warningEmitted = true;
    }
    String host = "localhost";

    for (int i = 0; i < n; i++) {
      if (hosts != null && hosts.hasNext()) {
        host = hosts.next();
      } else if (!warningEmitted) {
        System.err
            .println("[APGAS] Warning: hostfile too short; repeating last host: "
                + host);
        warningEmitted = true;
      }
      command.add(3, host);
      if (verbose) {
        System.err.println("[APGAS] Spawning new place: "
            + String.join(" ", command));
      }
      Process process = pb.start();
      command.remove(3);
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
