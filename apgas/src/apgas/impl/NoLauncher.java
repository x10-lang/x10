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

import java.util.List;

/**
 * The {@link NoLauncher} class does not spawn any place.
 */
final class NoLauncher implements Launcher {
  @Override
  public Process launch(List<String> command, String host, boolean verbose) {
    if (verbose) {
      System.err.println("[APGAS] Ignoring attempt to spawn new place: "
          + String.join(" ", command));
    }
    return null;
  }

  @Override
  public void launch(int n, List<String> command, List<String> hosts,
      boolean verbose) throws Exception {
    if (verbose) {
      System.err.println("[APGAS] Ignoring attempt to spawn " + n
          + " new place(s): " + String.join(" ", command));
    }
  }

  @Override
  public void shutdown() {
  }

  @Override
  public boolean healthy() {
    return true;
  }

}
