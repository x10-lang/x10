/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */

package apgas.impl;

import java.lang.ProcessBuilder.Redirect;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
  public void launch(int n, List<String> command, List<String> hosts,
      boolean verbose) throws Exception {
    final ProcessBuilder pb = new ProcessBuilder(command);
    pb.redirectOutput(Redirect.INHERIT);
    pb.redirectError(Redirect.INHERIT);
    boolean warningEmitted = false;
    final Iterator<String> it = hosts == null ? null : hosts.iterator();
    String host;
    host = it == null ? InetAddress.getLoopbackAddress().getHostAddress()
        : it.next();

    for (int i = 0; i < n; i++) {
      if (it != null) {
        if (it.hasNext()) {
          host = it.next();
        } else if (!warningEmitted) {
          System.err.println(
              "[APGAS] Warning: hostfile too short; repeating last host: "
                  + host);
          warningEmitted = true;
        }
      }
      Process process;
      boolean local = false;
      try {
        local = InetAddress.getByName(host).isLoopbackAddress();
      } catch (final UnknownHostException e) {
      }
      if (local) {
        process = pb.start();
        if (verbose) {
          System.err.println(
              "[APGAS] Spawning new place: " + String.join(" ", command));
        }
      } else {
        command.add(0, "ssh");
        command.add(1, "-t");
        command.add(2, "-t");
        command.add(3, host);
        if (verbose) {
          System.err.println(
              "[APGAS] Spawning new place: " + String.join(" ", command));
        }
        process = pb.start();
        command.remove(0);
        command.remove(0);
        command.remove(0);
        command.remove(0);
      }
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
  public Process launch(List<String> command, String host, boolean verbose)
      throws Exception {
    final ProcessBuilder pb = new ProcessBuilder(command);
    pb.redirectOutput(Redirect.INHERIT);
    pb.redirectError(Redirect.INHERIT);

    Process process;
    boolean local = false;
    try {
      local = InetAddress.getByName(host).isLoopbackAddress();
    } catch (final UnknownHostException e) {
    }
    if (!local) {
      command.add(0, "ssh");
      command.add(1, "-t");
      command.add(2, "-t");
      command.add(3, host);
    }
    process = pb.start();
    if (verbose) {
      System.err
          .println("[APGAS] Spawning new place: " + String.join(" ", command));
    }
    if (!local) {
      command.remove(0);
      command.remove(0);
      command.remove(0);
      command.remove(0);
    }
    synchronized (this) {
      if (dying <= 1) {
        processes.add(process);
        return process;
      }
    }
    process.destroyForcibly();
    throw new IllegalStateException("Shutdown in progress");
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
}
