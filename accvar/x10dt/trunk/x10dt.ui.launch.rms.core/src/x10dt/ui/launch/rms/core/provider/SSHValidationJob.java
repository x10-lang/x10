/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.rms.core.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.remote.core.IRemoteProcess;
import org.eclipse.ptp.remote.core.IRemoteProcessBuilder;

import x10dt.ui.launch.core.Constants;
import x10dt.ui.launch.rms.core.Messages;
import x10dt.ui.launch.rms.core.RMSCoreActivator;

/**
 * Checks that 'ssh "machine" hostname' succeeds.
 * 
 * @author egeay
 */
public final class SSHValidationJob extends Job {

  /**
   * Creates the validation job with the X10 runtime system for a particular host name. 
   * 
   * @param x10RuntimeSystem The X10 Runtime System to use in order to create the process.
   * @param hostName The host name to check.
   * @param mainMonitor The monitor to use to report progress or cancel the operation.
   */
  public SSHValidationJob(final AbstractX10RuntimeSystem x10RuntimeSystem, final String hostName, 
                          final IProgressMonitor mainMonitor) {
    super(Messages.SXRS_SSHValidationTaskName);
    this.fX10RuntimeSystem = x10RuntimeSystem;
    this.fMainMonitor = mainMonitor;
    this.fHostName = hostName;
  }

  // --- Abstract methods implementation
  
  protected IStatus run(final IProgressMonitor monitor) {
    this.fMainMonitor.beginTask(null, 1);
    this.fMainMonitor.subTask(Messages.SXRS_ValidatesSSHTaskName);
    // Tries checking from the first machine in the list.
    final List<String> command = new ArrayList<String>();
    command.add("ssh"); //$NON-NLS-1$
    command.add(this.fHostName);
    command.add("hostname"); //$NON-NLS-1$

    final IRemoteProcessBuilder processBuilder = this.fX10RuntimeSystem.createProcessBuilder(command, null /* workingDirectory */);
    IRemoteProcess initProcess = null;
    try {
      initProcess = processBuilder.start();
    } catch (IOException except) {
      return new Status(IStatus.ERROR, RMSCoreActivator.PLUGIN_ID, NLS.bind(Messages.SXRS_ProcessStartError,
                                                                            getCommandString(command)), except);
    }

    final IRemoteProcess process = initProcess;
    final ExecutorService executorService = Executors.newSingleThreadExecutor();

    final Future<String> stderrExecService = executorService.submit(new Callable<String>() {

      public String call() {
        final StringBuilder errBuilder = new StringBuilder();
        final BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        try {
          String line = null;
          int i = 0;
          while ((line = stderr.readLine()) != null) {
            if (i == 1) {
              errBuilder.append('\n');
            } else {
              i = 1;
            }
            errBuilder.append(line);
          }
        } catch (IOException except) {
          // Simply forgets.
        }
        return errBuilder.toString();
      }

    });

    final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    scheduledExecutor.schedule(new Runnable() {

      public void run() {
        if (! process.isCompleted()) {
          process.destroy();
        }
        scheduledExecutor.shutdown();
      }

    }, 3, TimeUnit.SECONDS);
    try {
      while (!process.isCompleted() && !this.fMainMonitor.isCanceled()) {
        try {
          synchronized (this) {
            wait(200);
          }
        } catch (InterruptedException except) {
          // Simply forgets.
        }
      }

      if (process.exitValue() != 0) {
        String error = Constants.EMPTY_STR;
        try {
          error = stderrExecService.get();
        } catch (Exception except) {
          // Simply forgets.
        }
        if (error.length() == 0) {
          return new Status(IStatus.ERROR, RMSCoreActivator.PLUGIN_ID, NLS.bind(Messages.SXRS_ValidationError,
                                                                                getCommandString(command)));
        } else {
          return new Status(IStatus.ERROR, RMSCoreActivator.PLUGIN_ID, NLS.bind(Messages.SXRS_SSHValidationErrorWithErrOutput,
                                                                                getCommandString(command), error));
        }
      }
    } finally {
      stderrExecService.cancel(true);
      process.destroy();
      executorService.shutdown();
      this.fMainMonitor.worked(1);
    }
    this.fMainMonitor.done();
    return Status.OK_STATUS;
  }
  
  // --- Private code
  
  private String getCommandString(final List<String> command) {
    final StringBuilder sb = new StringBuilder();
    int i = 0;
    for (final String cmd : command) {
      if (i == 1) {
        sb.append(' ');
      } else {
        i = 1;
      }
      sb.append(cmd);
    }
    return sb.toString();
  }
  
  // --- Fields
  
  private final IProgressMonitor fMainMonitor;
  
  private final String fHostName;
  
  private final AbstractX10RuntimeSystem fX10RuntimeSystem;
  
}