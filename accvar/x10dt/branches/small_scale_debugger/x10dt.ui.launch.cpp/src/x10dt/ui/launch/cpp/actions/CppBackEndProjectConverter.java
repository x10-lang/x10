/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.actions;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.window.IShellProvider;

import x10dt.ui.launch.core.LaunchCore;
import x10dt.ui.launch.core.actions.IBackEndX10ProjectConverter;
import x10dt.ui.launch.core.dialogs.DialogsFactory;
import x10dt.ui.launch.cpp.LaunchMessages;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;
import x10dt.ui.launch.cpp.platform_conf.X10PlatformConfFactory;

/**
 * Implementation of {@link IBackEndX10ProjectConverter} when converting an X10 project to use the C++ back-end.
 * 
 * @author egeay
 */
public final class CppBackEndProjectConverter implements IBackEndX10ProjectConverter {

  // --- Interface methods implementation
  
  public String getProjectNatureId() {
    return LaunchCore.X10_CPP_PRJ_NATURE_ID;
  }

  public void preProjectSetup(final IShellProvider shellProvider, final IProject project) {
    final IFile platformConfFile = X10PlatformConfFactory.getFile(project);
    if (! EFS.getLocalFileSystem().getStore(platformConfFile.getLocationURI()).fetchInfo().exists()) {
      final IX10PlatformConf platformConf = X10PlatformConfFactory.load(platformConfFile);
      final IX10PlatformConfWorkCopy platformConfWorkCopy = platformConf.createWorkingCopy();
      platformConfWorkCopy.initializeToDefaultValues(project);
      platformConfWorkCopy.applyChanges();

      try {
        X10PlatformConfFactory.save(platformConfFile, platformConfWorkCopy);
      } catch (CoreException except) {
        DialogsFactory.createErrorBuilder().setDetailedMessage(except.getStatus())
                      .createAndOpen(shellProvider, LaunchMessages.CBEPC_PlatformConfSavingErrTitle, 
                                     LaunchMessages.CBEPC_PlatformConfSavingErrMsg);
      }
    }
  }

}
