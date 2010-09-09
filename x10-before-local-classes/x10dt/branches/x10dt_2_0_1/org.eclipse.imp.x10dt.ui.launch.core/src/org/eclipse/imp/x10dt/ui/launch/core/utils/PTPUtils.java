/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.utils;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.imp.utils.Pair;
import org.eclipse.imp.x10dt.ui.launch.core.Constants;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.imp.x10dt.ui.launch.core.dialogs.DialogsFactory;
import org.eclipse.ptp.core.IModelManager;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.elementcontrols.IResourceManagerControl;
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
    final String rmId = (String) combo.getData(combo.getItem(combo.getSelectionIndex()));
    final IModelManager modelManager = PTPCorePlugin.getDefault().getModelManager();
    final IResourceManager resourceManager = modelManager.getResourceManagerFromUniqueName(rmId);
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
    final IModelManager modelManager = PTPCorePlugin.getDefault().getModelManager();
    return getConnectionAndFileManager(modelManager.getResourceManagerFromUniqueName(resourceManagerId));
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
   * Determines the workspace directory for the given resource manager and project name provided. Typically it will be the
   * project name appended to the user home directory. If we fail to get the user home directory for some strange reasons,
   * we will add the project name to the TEMP directory. If we fail to read the TEMP directory also, the method will return
   * an empty string.
   * 
   * @param resourceManagerId The unique resource manager id.
   * @param projectName The project name to consider.
   * @return The workspace directory or an empty string if we could not identify one.
   */
  public static String getTargetWorkspaceDirectory(final String resourceManagerId, final String projectName) {
    final IModelManager modelManager = PTPCorePlugin.getDefault().getModelManager();
    final IResourceManager resourceManager = modelManager.getResourceManagerFromUniqueName(resourceManagerId);
    final IResourceManagerControl rmControl = (IResourceManagerControl) resourceManager;
    final IResourceManagerConfiguration rmc = rmControl.getConfiguration();
    final IRemoteServices rmServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(rmc.getRemoteServicesId());
    final boolean isLocal = PTPRemoteCorePlugin.getDefault().getDefaultServices().equals(rmServices);

    final StringBuilder wDirPathBuilder = new StringBuilder();
    if (isLocal) {
      wDirPathBuilder.append(System.getProperty("user.home").replace('\\', '/')); //$NON-NLS-1$
    } else {
      final IRemoteConnection connection = rmServices.getConnectionManager().getConnection(rmc.getConnectionName());
      final String userHome = PTPUtils.getUserHomeDirectoryFromEnvVariables(connection);
      if (userHome == null) {
        // Somehow we can't get access to user home through the classical environment variables !?
        final IRemoteFileManager rmFileManager = rmServices.getFileManager(connection);
        final String tmpDir = getTempDirectory(connection, rmFileManager);
        if (tmpDir == null) {
          return ""; //$NON-NLS-1$
        } else {
          wDirPathBuilder.append(tmpDir);
        }
      } else {
        wDirPathBuilder.append(userHome);
      }
    }
    wDirPathBuilder.append('/').append(projectName);
    return wDirPathBuilder.toString();
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
  
  /**
   * Returns the resource manager associated with the unique name provided.
   * 
   * <p>If the unique name provided does not match any, then:
   * <ul>
   * <li>If there is no resource manager the method will return <b>null</b>.</li>
   * <li>If there is some resource manager and the end-user starts and selects one, we will return this one of interest.</li>
   * </ul>
   * 
   * @param shell The shell to use for creating dialog boxes in case the id is staled.
   * @param rmUniqueName The resource manager unique name.
   * @return The resource manager if we could find it, otherwise <b>null</b>.
   */
  public static IResourceManager getResourceManager(final Shell shell, final String rmUniqueName) {
    final IModelManager modelManager = PTPCorePlugin.getDefault().getModelManager();
    final IResourceManager resourceManager = modelManager.getResourceManagerFromUniqueName(rmUniqueName);
    if (resourceManager == null) {
      final IResourceManager[] rms = PTPCorePlugin.getDefault().getUniverse().getResourceManagers();
      if (rms.length == 0) {
        return null;
      } else {
        return DialogsFactory.selectResourceManagerStartDialog(shell, Arrays.asList(rms), Messages.PU_RMNotFound);
      }
    } else {
      return resourceManager;
    }
  }
  
  /**
   * Returns the resource manager associated with the unique name stored in the project provided.
   * 
   * <p>If the unique name provided does not match any, then:
   * <ul>
   * <li>If there is no resource manager the method will return <b>null</b>.</li>
   * <li>If there is some resource manager and the end-user starts and selects one, we will return this one of interest.
   * Furthermore in such case we will update the staled internal unique name for the project.</li>
   * </ul>
   * 
   * @param shell The shell to use for creating dialog boxes in case the id is staled.
   * @param project The project of interest.
   * @return The resource manager if we could find it, otherwise <b>null</b>.
   * @throws CoreException Occurs if we could not read or store the internal unique name persisted in the project.
   */
  public static IResourceManager getResourceManager(final Shell shell, final IProject project) throws CoreException {
    final IModelManager modelManager = PTPCorePlugin.getDefault().getModelManager();
    final String rmUniqueName = project.getPersistentProperty(Constants.RES_MANAGER_ID);
    final IResourceManager resourceManager = modelManager.getResourceManagerFromUniqueName(rmUniqueName);
    if (resourceManager == null) {
      final IResourceManager[] rms = PTPCorePlugin.getDefault().getUniverse().getResourceManagers();
      if (rms.length == 0) {
        return null;
      } else {
        final IResourceManager newRM = DialogsFactory.selectResourceManagerStartDialog(shell, Arrays.asList(rms), 
                                                                                       Messages.PU_RMNotFound);
        if (newRM != null) {
          project.setPersistentProperty(Constants.RES_MANAGER_ID, newRM.getUniqueName());
        }
        return newRM;
      }
    } else {
      return resourceManager;
    }
  }
  
  /**
   * Returns if the given resource manager is local one or not.
   * 
   * @param resourceManager The resource manager to test.
   * @return True if the resource manager is a local one, false if it is a remote one.
   */
  public static boolean isLocal(final IResourceManager resourceManager) {
    final IResourceManagerControl rmControl = (IResourceManagerControl) resourceManager;
    final IResourceManagerConfiguration rmc = rmControl.getConfiguration();
    final IRemoteServices remoteServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(rmc.getRemoteServicesId());
    return PTPRemoteCorePlugin.getDefault().getDefaultServices().equals(remoteServices);
  }
  
  // --- Private code
  
  private static String getUserHomeDirectoryFromEnvVariables(final IRemoteConnection connection) {
    final String homeVar = connection.getEnv(USERPROFILE_VAR);
    if (homeVar == null) {
      final String userHomeVar = connection.getEnv(HOME_VAR);
      return (userHomeVar == null) ? null : userHomeVar;
    } else {
      return homeVar.replace('\\', '/');
    }
  }
  
  // --- Fields
  
  private static final String TMP_ENV_VAR = "TMP"; //$NON-NLS-1$
  
  private static final String TEMP_ENV_VAR = "TEMP"; //$NON-NLS-1$
  
  private static final String TMPDIR_ENV_VAR = "TMPDIR"; //$NON-NLS-1$
  
  private static final String TMP_DIR = "/tmp"; //$NON-NLS-1$
  
  private static final String USERPROFILE_VAR = "USERPROFILE"; //$NON-NLS-1$
  
  private static final String HOME_VAR = "HOME"; //$NON-NLS-1$

}
