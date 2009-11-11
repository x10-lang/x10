/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.utils;

import org.eclipse.imp.utils.Pair;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.elementcontrols.IResourceManagerControl;
import org.eclipse.ptp.core.elements.IPUniverse;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteFileManager;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.remote.ui.IRemoteUIConstants;
import org.eclipse.ptp.remote.ui.IRemoteUIFileManager;
import org.eclipse.ptp.remote.ui.IRemoteUIServices;
import org.eclipse.ptp.remote.ui.PTPRemoteUIPlugin;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;

/**
 * Utility methods using PTP.
 * 
 * @author egeay
 */
public final class PTPUtils {
  
  /**
   * Browse either a file or a directory locally or remotely through the help of PTP Resource Manager.
   * 
   * @param shell The shell to use for opening the dialog.
   * @param rmConf The resource manager configuration to consider for accessing the remote file UI manager.
   * @param dialogTitle The dialog title.
   * @param initialPath The initial path for the dialog.
   * @param browseDirectory True if the final path has to be directory, false if it has to be a file.
   * @return The path selected by the end-user or <b>null</b> if none.
   */
  public static String browse(final Shell shell, final IResourceManagerConfiguration rmConf, final String dialogTitle,
                              final String initialPath, final boolean browseDirectory) {
    final IRemoteServices rmServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(rmConf.getRemoteServicesId());
    final IRemoteUIServices rmUIServices = PTPRemoteUIPlugin.getDefault().getRemoteUIServices(rmServices);
    if (rmServices != null && rmUIServices != null) {
      final IRemoteConnection rmConnection = rmServices.getConnectionManager().getConnection(rmConf.getConnectionName());
      if (rmConnection != null) {
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
      }
    }
    return null;
  }
  
  /**
   * Browse either a file or a directory locally or remotely through the help of PTP Resource Manager.
   * 
   * @param shell The shell to use for opening the dialog.
   * @param combo The SWT combo containing the resource manager id. If the combo has no selection, the method will return 
   * <b>null</b>.
   * @param dialogTitle The dialog title.
   * @param initialPath The initial path for the dialog.
   * @param browseDirectory True if the final path has to be directory, false if it has to be a file.
   * @return The path selected by the end-user or <b>null</b> if none.
   */
  public static String browse(final Shell shell, final Combo combo, final String dialogTitle, 
                              final String initialPath, final boolean browseDirectory) {
    if (combo.getSelectionIndex() == -1) {
      return null;
    }
    final IPUniverse universe = PTPCorePlugin.getDefault().getUniverse();
    final String rmId = (String) combo.getData(combo.getItem(combo.getSelectionIndex()));
    final IResourceManager resourceManager = universe.getResourceManager(rmId);
    final IResourceManagerControl rmControl = (IResourceManagerControl) resourceManager;
    return browse(shell, rmControl.getConfiguration(), dialogTitle, initialPath, browseDirectory);
  }
  
  /**
   * Browse a directory locally or remotely through the help of PTP Resource Manager.
   * 
   * <p>Simply calls {@link #browse(Shell, Combo, String, String, boolean)} with the right boolean flag.
   * 
   * @param shell The shell to use for opening the dialog.
   * @param combo The SWT combo containing the resource manager id. If the combo has no selection, the method will return 
   * <b>null</b>.
   * @param dialogTitle The dialog title.
   * @param initialPath The initial path for the dialog.
   * @return The path selected by the end-user or <b>null</b> if none.
   */
  public static String browseDirectory(final Shell shell, final Combo combo, final String dialogTitle, 
                                       final String initialPath) {
    return browse(shell, combo, dialogTitle, initialPath, true);
  }
  
  /**
   * Browse a directory locally or remotely through the help of PTP Resource Manager.
   * 
   * <p>Simply calls {@link #browse(Shell, IResourceManagerConfiguration, String, String, boolean)} with the right 
   * boolean flag.
   * 
   * @param shell The shell to use for opening the dialog.
   * @param rmConf The resource manager configuration to consider for accessing the remote file UI manager.
   * @param dialogTitle The dialog title.
   * @param initialPath The initial path for the dialog.
   * @return The path selected by the end-user or <b>null</b> if none.
   */
  public static String browseDirectory(final Shell shell, final IResourceManagerConfiguration rmConf, final String dialogTitle,
                                       final String initialPath) {
    return browse(shell, rmConf, dialogTitle, initialPath, true);
  }
  
  /**
   * Browse a file locally or remotely through the help of PTP Resource Manager.
   * 
   * <p>Simply calls {@link #browse(Shell, Combo, String, String, boolean)} with the right boolean flag.
   * 
   * @param shell The shell to use for opening the dialog.
   * @param combo The SWT combo containing the resource manager id. If the combo has no selection, the method will return 
   * <b>null</b>.
   * @param dialogTitle The dialog title.
   * @param initialPath The initial path for the dialog.
   * @return The path selected by the end-user or <b>null</b> if none.
   */
  public static String browseFile(final Shell shell, final Combo combo, final String dialogTitle, 
                                       final String initialPath) {
    return browse(shell, combo, dialogTitle, initialPath, false);
  }
  
  /**
   * Browse a file locally or remotely through the help of PTP Resource Manager.
   * 
   * <p>Simply calls {@link #browse(Shell, IResourceManagerConfiguration, String, String, boolean)} with the right 
   * boolean flag.
   * 
   * @param shell The shell to use for opening the dialog.
   * @param rmConf The resource manager configuration to consider for accessing the remote file UI manager.
   * @param dialogTitle The dialog title.
   * @param initialPath The initial path for the dialog.
   * @return The path selected by the end-user or <b>null</b> if none.
   */
  public static String browseFile(final Shell shell, final IResourceManagerConfiguration rmConf, final String dialogTitle, 
                                  final String initialPath) {
    return browse(shell, rmConf, dialogTitle, initialPath, false);
  }
  
  /**
   * Provides access to {@link IRemoteConnection} and {@link IRemoteFileManager} from a given resource manager.
   * 
   * @param rmManager The resource manager to consider.
   * @return The pair containing the connection and the remote file manager.
   */
  public static Pair<IRemoteConnection, IRemoteFileManager> getConnectionAndFileManager(final IResourceManager rmManager) {
    final IResourceManagerControl rmControl = (IResourceManagerControl) rmManager;
    final IResourceManagerConfiguration rmc = rmControl.getConfiguration();
    final IRemoteServices remoteServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(rmc.getRemoteServicesId());
    final IRemoteConnection rmConnection = remoteServices.getConnectionManager().getConnection(rmc.getConnectionName());
    return new Pair<IRemoteConnection, IRemoteFileManager>(rmConnection, remoteServices.getFileManager(rmConnection));
  }
  
  /**
   * Provides access to {@link IRemoteConnection} and {@link IRemoteFileManager} from a given resource manager id.
   * 
   * @param resourceManagerId The resource manager id identifying uniquely the resource manager.
   * @return The pair containing the connection and the remote file manager.
   */
  public static Pair<IRemoteConnection, IRemoteFileManager> getConnectionAndFileManager(final String resourceManagerId) {
    final IPUniverse universe = PTPCorePlugin.getDefault().getUniverse();
    return getConnectionAndFileManager(universe.getResourceManager(resourceManagerId));
  }
  
  /**
   * Provides access to {@link IRemoteConnection} and {@link IRemoteFileManager} from a SWT combo containing 
   * a resource manager id.
   * 
   * @param combo The combo containing the resource manager id identifying uniquely the resource manager.
   * @return The pair containing the connection and the remote file manager.
   */
  public static Pair<IRemoteConnection, IRemoteFileManager> getConnectionAndFileManager(final Combo combo) {
    return getConnectionAndFileManager((String) combo.getData(combo.getItem(combo.getSelectionIndex())));
  }
  
  /**
   * Reads TMP, TEMP and TMPDIR via {@link IRemoteConnection} in order to find the temp directory for the given resource
   * manager. If none of this variables are defined, it tries to check if "/tmp" directory exists.
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
  
  // --- Fields
  
  private static final String TMP_ENV_VAR = "TMP"; //$NON-NLS-1$
  
  private static final String TEMP_ENV_VAR = "TEMP"; //$NON-NLS-1$
  
  private static final String TMPDIR_ENV_VAR = "TMPDIR"; //$NON-NLS-1$
  
  private static final String TMP_DIR = "/tmp"; //$NON-NLS-1$

}
