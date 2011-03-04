/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteFileManager;
import org.eclipse.ptp.remote.core.IRemoteProcess;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.remote.ui.IRemoteUIConstants;
import org.eclipse.ptp.remote.ui.IRemoteUIFileManager;
import org.eclipse.ptp.remote.ui.IRemoteUIServices;
import org.eclipse.ptp.remote.ui.PTPRemoteUIPlugin;
import org.eclipse.swt.widgets.Shell;

import x10dt.ui.launch.core.LaunchCore;
import x10dt.ui.launch.core.Messages;
import x10dt.ui.launch.core.utils.IProcessOuputListener;
import x10dt.ui.launch.core.utils.PTPConstants;

/**
 * Utility methods using PTP.
 * 
 * @author egeay
 */
public final class PTPUtils {
  
  /**
   * Reads TMP, TEMP and TMPDIR via {@link IRemoteConnection} in order to find the temp directory for the given resource
   * manager. If none of these variables are defined, it tries to check if "/tmp" directory exists.
   * 
   * @param connection The remote connection to use.
   * @param rmFileManager The file manager associated with it.
   * @return The temp directory if it is defined, otherwise <b>null</b>.
   */
  public static String getTempDirectory(final IRemoteConnection connection, final IRemoteFileManager rmFileManager) {
    final String tmpVar = connection.getEnv(TMP_ENV_VAR);
    if (tmpVar == null) {
      final String tempVar = connection.getEnv(TEMP_ENV_VAR);
      if (tempVar == null) {
        final String tmpDirVar = connection.getEnv(TMPDIR_ENV_VAR);
        if (tmpDirVar == null) {
          if (rmFileManager.getResource(TMP_DIR).fetchInfo().exists()) {
            return TMP_DIR.replace('\\', '/');
          } else {
            return null;
          }
        } else {
          return tmpDirVar.replace('\\', '/');
        }
      } else {
        return tempVar.replace('\\', '/');
      }
    } else {
      return tmpVar.replace('\\', '/');
    }
  }

  /**
   * Launches remote tools PTP browser either to select a remote file or directory with the help of parameters provided.
   * 
   * @param shell The shell to use for creating the dialog.
   * @param rmConnection The remote connection to use for accessing the files.
   * @param dialogTitle The dialog title.
   * @param initialPath The initial path to give to the dialog.
   * @param browseDirectory True will create a dialog for selection of a directory, false for a selection of a file.
   * @return The path to the file or directory selected or <b>null</b>.
   */
  public static String remoteBrowse(final Shell shell, final IRemoteConnection rmConnection, final String dialogTitle,
                                    final String initialPath, final boolean browseDirectory) {
    final IRemoteServices rmServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(PTPConstants.REMOTE_CONN_SERVICE_ID);
    final IRemoteUIServices rmUIServices = PTPRemoteUIPlugin.getDefault().getRemoteUIServices(rmServices);
    
    final IRemoteUIFileManager fileManager = rmUIServices.getUIFileManager();
    if (fileManager != null) {
      fileManager.setConnection(rmConnection);
      fileManager.showConnections(false);
      final String path;
      if (browseDirectory) {
        path = fileManager.browseDirectory(shell, dialogTitle, initialPath, IRemoteUIConstants.NONE); 
      } else {
        path = fileManager.browseFile(shell, dialogTitle, initialPath, IRemoteUIConstants.NONE);
      }
      if (path != null) {
        return path.replace('\\', '/');
      }
    }
    return null;
  }
  
  /**
   * Runs the process provided capturing the standard and error output and redirecting them to the output listener transmitted.
   * 
   * @param process The process to run.
   * @param listener The process output listener.
   * @throws InterruptedException If the current thread is {@linkplain Thread#interrupt() interrupted} by another
   * thread while it is waiting, then the wait is ended and an {@link InterruptedException} is thrown.
   * @return The exit value for the process. 
   */
  public static int run(final IRemoteProcess process, final IProcessOuputListener listener) throws InterruptedException {
    final BufferedReader outReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    final Thread outThread = new Thread(new Runnable() {
      
      public void run() {
        try {
          String line;
          while ((line = outReader.readLine()) != null) {
            listener.read(line);
          }
        } catch (IOException except) {
          LaunchCore.log(IStatus.ERROR, Messages.CPPB_OutputStreamReadingError, except);
        }
      }
      
    });
  
    final BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    final Thread errThread = new Thread(new Runnable() {
      
      public void run() {
        try {
          String line;
          while ((line = errReader.readLine()) != null) {
            listener.readError(line);
          }
        } catch (IOException except) {
          LaunchCore.log(IStatus.ERROR, Messages.CPPB_ErrorStreamReadingError, except);
        }
      }
      
    });
    
    try {
      outThread.start();
      errThread.start();
    
      process.waitFor();
    
      outThread.join();
      errThread.join();
    
      return process.exitValue();
    } finally {
      process.destroy();
    }
  }
  
  // --- Fields
  
  private static final String TMP_ENV_VAR = "TMP"; //$NON-NLS-1$
  
  private static final String TEMP_ENV_VAR = "TEMP"; //$NON-NLS-1$
  
  private static final String TMPDIR_ENV_VAR = "TMPDIR"; //$NON-NLS-1$
  
  private static final String TMP_DIR = "/tmp"; //$NON-NLS-1$

}
