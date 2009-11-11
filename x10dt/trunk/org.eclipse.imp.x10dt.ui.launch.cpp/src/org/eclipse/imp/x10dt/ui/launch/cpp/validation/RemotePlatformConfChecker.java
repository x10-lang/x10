/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.validation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.imp.utils.Pair;
import org.eclipse.imp.x10dt.ui.launch.core.builder.ELanguage;
import org.eclipse.imp.x10dt.ui.launch.core.wizards.validation.AbstractPlatformConfChecker;
import org.eclipse.imp.x10dt.ui.launch.core.wizards.validation.IPlatformConfChecker;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.ptp.remote.core.IRemoteFileManager;
import org.eclipse.ptp.remote.core.IRemoteProcess;
import org.eclipse.ptp.remote.core.IRemoteProcessBuilder;

/**
 * An implementation of {@link IPlatformConfChecker} for remote validation.
 * 
 * @author egeay
 */
public final class RemotePlatformConfChecker extends AbstractPlatformConfChecker implements IPlatformConfChecker {

  // --- Abstract methods implementation
  
  protected boolean isLocalChecker() {
    return false;
  }

  protected Pair<String, String> compileX10File(final ELanguage language, final String testFilePath, 
                                                final InputStream sourceInputStream, final String workspaceDir, 
                                                final String x10DistLoc, final String pgasDistLoc,
                                                final String[] x10LibsLocs, final IRemoteFileManager fileManager,
                                                final SubMonitor monitor) throws Exception {
    monitor.beginTask(LaunchMessages.LPCC_GeneratingFilesMsg, 10);
    
    final List<String> command = new ArrayList<String>();
    command.add(x10DistLoc + "/bin/x10c++"); //$NON-NLS-1$
    command.add("-commandlineonly"); //$NON-NLS-1$
    command.add(testFilePath);
    
    final IRemoteProcessBuilder processBuilder = getProcessBuilder(command).directory(fileManager.getResource(workspaceDir));
    processBuilder.environment().put("X10LIB", pgasDistLoc); //$NON-NLS-1$
    final IRemoteProcess process = processBuilder.start();
    try {
      process.waitFor();
      final int exitValue = process.exitValue();
      if (exitValue == 0) {
        final String generatedFile = testFilePath.replace(".x10", ".cc"); //$NON-NLS-1$ //$NON-NLS-2$
        if (fileManager.getResource(generatedFile).fetchInfo().exists()) {
          return new Pair<String, String>(null, generatedFile);
        } else {
          return new Pair<String, String>(LaunchMessages.LPCC_NoGeneratedFilesError, null);
        }
      } else {
        final BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        final StringBuilder errMsgBuilder = new StringBuilder();
        String line;
        while ((line = errReader.readLine()) != null) {
          errMsgBuilder.append(line).append('\n');
        }
        errReader.close();
        return new Pair<String, String>(errMsgBuilder.toString(), null);
      }
    } finally { 
      process.destroy();
      monitor.done();
    }
  }


}
