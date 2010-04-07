/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.builder.target_op;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IProcessOuputListener;
import org.eclipse.imp.x10dt.ui.launch.core.utils.UIUtils;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteFileManager;
import org.eclipse.ptp.remote.core.IRemoteProcess;
import org.eclipse.ptp.remote.core.IRemoteProcessBuilder;
import org.eclipse.ptp.remote.core.IRemoteServices;


abstract class AbstractTargetOpHelper implements ITargetOpHelper {
  
  AbstractTargetOpHelper(final IRemoteServices remoteServices, final IRemoteConnection remoteConnection) {
    this.fRemoteServices = remoteServices;
    this.fRemoteConnection = remoteConnection;
    this.fFileManager = remoteServices.getFileManager(remoteConnection);
  }
  
  // --- Interface methods implementation
  
  public final String getEnvVarValue(final String envVarName) {
    return this.fRemoteConnection.getEnv(envVarName);
  }

  public final IFileStore getStore(final String resourcePath) {
    return this.fFileManager.getResource(resourcePath);
  }
  
  public final int run(final List<String> command, final String directory, final Map<String, String> envVariables,
                       final IProcessOuputListener outputListener) throws IOException, InterruptedException {
    IRemoteProcessBuilder processBuilder = this.fRemoteServices.getProcessBuilder(this.fRemoteConnection, command);
    if (directory != null) {
      processBuilder = processBuilder.directory(getStore(directory));
    }
    if (envVariables != null) {
      processBuilder.environment().putAll(envVariables);
    }
    final IRemoteProcess process = processBuilder.start();
    
    UIUtils.printStream(process.getInputStream(), process.getErrorStream(), outputListener);
    
    process.waitFor();
    
    final int returnCode = process.exitValue();
    process.destroy();
    
    return returnCode;
  }
  
  public final int run(final List<String> command, final String directory,
                       final IProcessOuputListener outputListener) throws IOException, InterruptedException {
    return run(command, directory, null, outputListener);
  }
  
  public final int run(final List<String> command, final Map<String, String> envVariables,
                       final IProcessOuputListener outputListener) throws IOException, InterruptedException {
    return run(command, null, envVariables, outputListener);
  }
  
  public final int run(final List<String> command,
                       final IProcessOuputListener outputListener) throws IOException, InterruptedException {
    return run(command, null, null, outputListener);
  }

  public final String toPath(final URI uri) {
    return this.fFileManager.toPath(uri);
  }
  
  // --- Fields
  
  private final IRemoteServices fRemoteServices;
  
  private final IRemoteConnection fRemoteConnection;
  
  private final IRemoteFileManager fFileManager;

}
