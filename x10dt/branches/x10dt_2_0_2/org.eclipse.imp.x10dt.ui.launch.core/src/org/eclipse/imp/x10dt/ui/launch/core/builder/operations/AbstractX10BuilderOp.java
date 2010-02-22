/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.builder.operations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.imp.utils.ConsoleUtil;
import org.eclipse.imp.x10dt.ui.launch.core.LaunchCore;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.IX10PlatformConfiguration;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IInputListener;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IResourceUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.UIUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.X10BuilderUtils;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.elementcontrols.IResourceManagerControl;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteFileManager;
import org.eclipse.ptp.remote.core.IRemoteProcess;
import org.eclipse.ptp.remote.core.IRemoteProcessBuilder;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;


abstract class AbstractX10BuilderOp implements IX10BuilderOp {
  
  protected AbstractX10BuilderOp(final IResourceManager resourceManager, final IProject project, final String workspaceDir) {
    this.fResourceManager = resourceManager;
    this.fProject = project;
    this.fWorkspaceDir = workspaceDir;
    final IResourceManagerControl rmControl = (IResourceManagerControl) resourceManager;
    final IResourceManagerConfiguration rmc = rmControl.getConfiguration();
    this.fRemoteServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(rmc.getRemoteServicesId());
    this.fRemoteConnection = this.fRemoteServices.getConnectionManager().getConnection(rmc.getConnectionName());
  }
  
  // --- Interface methods implementation
  
  public final void archive(final IX10PlatformConfiguration platform, final IProgressMonitor monitor) throws CoreException {
    monitor.subTask(Messages.CPPB_ArchivingTaskName);
    final List<String> archiveCmd = new ArrayList<String>();
    archiveCmd.add(platform.getArchiver());
    archiveCmd.addAll(X10BuilderUtils.getAllTokens(platform.getArchivingOpts()));
    final StringBuilder libName = new StringBuilder();
    libName.append(this.fWorkspaceDir).append("/lib").append(this.fProject.getName()).append(".a"); //$NON-NLS-1$//$NON-NLS-2$
    archiveCmd.add(libName.toString());
    for (final String objectFile : this.fObjectFiles) {
      archiveCmd.add(objectFile);
    }

    try {
      final IRemoteProcessBuilder archiveProcessBuilder = getProcessBuilder(archiveCmd);
      final IRemoteProcess process = archiveProcessBuilder.start();
      
      final MessageConsole messageConsole = ConsoleUtil.findConsole(Messages.CPPB_ConsoleName);
      final MessageConsoleStream mcStream = messageConsole.newMessageStream();
      UIUtils.printStream(process.getInputStream(), process.getErrorStream(), new IInputListener() {
        
        public void after() {
        }
        
        public void before() {
        }

        public void read(final String line) {
        }
        
        public void readError(final String line) {
          mcStream.println(line);
        }
        
      });

      process.waitFor();
    
      final int returnCode = process.exitValue();
      process.destroy();
    
      if (returnCode != 0) {
        IResourceUtils.addMarkerTo(getProject(), NLS.bind(Messages.CPPB_LibCreationError, archiveCmd), IMarker.SEVERITY_ERROR,
                                   getProject().getFullPath().toString(), IMarker.PRIORITY_HIGH);
      }
    } catch (IOException except) {
      IResourceUtils.addMarkerTo(getProject(), NLS.bind(Messages.CPPB_RemoteOpError, getResourceManagerName()), 
                                 IMarker.SEVERITY_ERROR, getProject().getLocation().toString(), IMarker.PRIORITY_HIGH);
      LaunchCore.log(IStatus.ERROR, NLS.bind(Messages.CPPB_RemoteOpError, getResourceManagerName()), except);
    } catch (InterruptedException except) {
      IResourceUtils.addMarkerTo(getProject(), NLS.bind(Messages.CPPB_CancelOpMsg, getResourceManagerName()), 
                                 IMarker.SEVERITY_WARNING, getProject().getLocation().toString(), IMarker.PRIORITY_LOW);
    } finally {
      monitor.done();
    }
  }
  
  public final boolean compile(final IX10PlatformConfiguration platform, final IProgressMonitor monitor) throws CoreException {
    monitor.beginTask(null, this.fCompiledFiles.size());
    monitor.subTask(Messages.CPPB_RemoteCompilTaskName);
        
    try {
      for (final Map.Entry<File, String> entry : this.fCompiledFiles.entrySet()) {
        final String objectFile = entry.getValue().replace(CC_EXT, ".o"); //$NON-NLS-1$
        this.fObjectFiles.add(objectFile);

        final List<String> command = new ArrayList<String>();
        command.add(platform.getCompiler());
        command.addAll(X10BuilderUtils.getAllTokens(platform.getCompilerOpts()));
        command.add(INCLUDE_OPT + this.fWorkspaceDir);
        for (final String headerLoc : platform.getX10HeadersLocations()) {
          command.add(INCLUDE_OPT + headerLoc);
        }
        command.add("-c"); //$NON-NLS-1$
        command.add(entry.getValue());
        command.add("-o"); //$NON-NLS-1$
        command.add(objectFile);
        
        final IRemoteProcessBuilder processBuilder = getProcessBuilder(command);
        final IRemoteProcess process = processBuilder.start();
        
        final MessageConsole messageConsole = ConsoleUtil.findConsole(Messages.CPPB_ConsoleName);
        messageConsole.clearConsole();
        final MessageConsoleStream mcStream = messageConsole.newMessageStream();
        UIUtils.printStream(process.getInputStream(), process.getErrorStream(), new IInputListener() {
          
          public void after() {
          }
          
          public void before() {
            this.fCounter = 0;
          }

          public void read(final String line) {
          }
          
          public void readError(final String line) {
            if (this.fCounter == 0) {
              
              this.fCounter = 1;
            }
            mcStream.println(line);
          }
          
          // --- Fields
          
          int fCounter;
          
        });
        
        process.waitFor();
        
        final int returnCode = process.exitValue();
        process.destroy();
        
        monitor.worked(1);
        
        if (returnCode != 0) {
          IResourceUtils.addMarkerTo(getProject(), NLS.bind(Messages.CPPB_CompilErrorMsg, entry.getKey().getName()), 
                                     IMarker.SEVERITY_ERROR, entry.getKey().getAbsolutePath(), IMarker.PRIORITY_HIGH);
          return false;
        }
      }
    } catch (IOException except) {
      IResourceUtils.addMarkerTo(getProject(), NLS.bind(Messages.CPPB_RemoteOpError, getResourceManagerName()), 
                                 IMarker.SEVERITY_ERROR, getProject().getLocation().toString(), IMarker.PRIORITY_HIGH);
      LaunchCore.log(IStatus.ERROR, NLS.bind(Messages.CPPB_RemoteOpError, getResourceManagerName()), except);
      return false;
    } catch (InterruptedException except) {
      IResourceUtils.addMarkerTo(getProject(), NLS.bind(Messages.CPPB_CancelOpMsg, getResourceManagerName()), 
                                 IMarker.SEVERITY_WARNING, getProject().getLocation().toString(), IMarker.PRIORITY_LOW);
      return false;
    } finally {
      monitor.done();
    }
    return true;
  }

  // --- Code for descendants
  
  protected final void addCompiledFile(final File srcFile, final String destFilePath) {
    this.fCompiledFiles.put(srcFile, destFilePath);
  }

  protected final IProject getProject() {
    return this.fProject;
  }
  
  protected final IRemoteFileManager getRemoteFileManager() {
    return this.fRemoteServices.getFileManager(this.fRemoteConnection);
  }
  
  protected final String getResourceManagerName() {
    return this.fResourceManager.getName();
  }
  
  protected final IRemoteProcessBuilder getProcessBuilder(final List<String> command) {
    return this.fRemoteServices.getProcessBuilder(this.fRemoteConnection, command);
  }
  
  protected final String getWorkspaceDir() {
    return this.fWorkspaceDir;
  }
  
  // --- Fields
  
  private final IResourceManager fResourceManager;
  
  private final IProject fProject;
  
  private final String fWorkspaceDir;
  
  private final IRemoteServices fRemoteServices;
  
  private final IRemoteConnection fRemoteConnection;
  
  private final Map<File, String> fCompiledFiles = new HashMap<File, String>();
  
  private final List<String> fObjectFiles = new LinkedList<String>();
  
  
  static final String CC_EXT = ".cc"; //$NON-NLS-1$
  
  private static final String INCLUDE_OPT = "-I"; //$NON-NLS-1$

}
