/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.builder;

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
import org.eclipse.imp.x10dt.ui.launch.core.builder.target_op.ITargetOpHelper;
import org.eclipse.imp.x10dt.ui.launch.core.builder.target_op.IX10BuilderFileOp;
import org.eclipse.imp.x10dt.ui.launch.core.builder.target_op.TargetOpHelperFactory;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.ETargetOS;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IProcessOuputListener;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IResourceUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.X10BuilderUtils;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IConnectionConf;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;


abstract class AbstractX10BuilderOp implements IX10BuilderFileOp {
  
  protected AbstractX10BuilderOp(final IX10PlatformConf platformConf, final IProject project, 
                                 final String workspaceDir) {
    this.fConfName = platformConf.getName();
    this.fProject = project;
    this.fWorkspaceDir = workspaceDir;
    this.fPlatformConf = platformConf;
    final IConnectionConf connConf = platformConf.getConnectionConf();
    final boolean isCygwin = this.fPlatformConf.getCppCompilationConf().getTargetOS() == ETargetOS.WINDOWS;
    this.fTargetOpHelper = TargetOpHelperFactory.create(connConf.isLocal(), isCygwin, connConf.getConnectionName());
  }
  
  // --- Interface methods implementation
  
  public final void archive(final IProgressMonitor monitor) throws CoreException {
    monitor.subTask(Messages.CPPB_ArchivingTaskName);
    final List<String> archiveCmd = new ArrayList<String>();
    archiveCmd.add(this.fPlatformConf.getCppCompilationConf().getArchiver());
    archiveCmd.addAll(X10BuilderUtils.getAllTokens(this.fPlatformConf.getCppCompilationConf().getArchivingOpts(true)));
    final StringBuilder libName = new StringBuilder();
    libName.append("lib").append(this.fProject.getName()).append(".a"); //$NON-NLS-1$//$NON-NLS-2$
    archiveCmd.add(libName.toString());
    for (final String objectFile : this.fObjectFiles) {
      archiveCmd.add(objectFile);
    }

    try {
      final MessageConsole messageConsole = ConsoleUtil.findConsole(Messages.CPPB_ConsoleName);
      final MessageConsoleStream mcStream = messageConsole.newMessageStream();
      final int returnCode = this.fTargetOpHelper.run(archiveCmd, this.fWorkspaceDir, new IProcessOuputListener() {
        
        public void read(final String line) {
        }
        
        public void readError(final String line) {
          mcStream.println(line);
        }
        
      });
    
      if (returnCode != 0) {
        IResourceUtils.addBuildMarkerTo(getProject(), NLS.bind(Messages.CPPB_LibCreationError, archiveCmd), IMarker.SEVERITY_ERROR,
                                        getProject().getFullPath().toString(), IMarker.PRIORITY_HIGH);
      }
    } catch (IOException except) {
      IResourceUtils.addBuildMarkerTo(getProject(), NLS.bind(Messages.CPPB_TargetOpError, this.fConfName), 
                                      IMarker.SEVERITY_ERROR, getProject().getLocation().toString(), IMarker.PRIORITY_HIGH);
      LaunchCore.log(IStatus.ERROR, NLS.bind(Messages.CPPB_TargetOpError, this.fConfName), except);
    } catch (InterruptedException except) {
      IResourceUtils.addBuildMarkerTo(getProject(), NLS.bind(Messages.CPPB_CancelOpMsg, this.fConfName), 
                                      IMarker.SEVERITY_WARNING, getProject().getLocation().toString(), IMarker.PRIORITY_LOW);
    } finally {
      monitor.done();
    }
  }
  
  public final boolean compile(final IProgressMonitor monitor) throws CoreException {
    monitor.beginTask(null, this.fCompiledFiles.size());
    monitor.subTask(Messages.CPPB_RemoteCompilTaskName);
        
    try {
      for (final Map.Entry<File, String> entry : this.fCompiledFiles.entrySet()) {
        final String objectFile = this.fTargetOpHelper.getTargetSystemPath(entry.getValue().replace(CC_EXT, ".o")); //$NON-NLS-1$
        this.fObjectFiles.add(objectFile);

        final List<String> command = new ArrayList<String>();
        command.add(this.fTargetOpHelper.getTargetSystemPath(this.fPlatformConf.getCppCompilationConf().getCompiler()));
        command.addAll(X10BuilderUtils.getAllTokens(this.fPlatformConf.getCppCompilationConf().getCompilingOpts(true)));
        command.add(INCLUDE_OPT + this.fTargetOpHelper.getTargetSystemPath(this.fWorkspaceDir));
        for (final String headerLoc : this.fPlatformConf.getCppCompilationConf().getX10HeadersLocations()) {
          command.add(INCLUDE_OPT + this.fTargetOpHelper.getTargetSystemPath(headerLoc));
        }
        command.add("-c"); //$NON-NLS-1$
        command.add(this.fTargetOpHelper.getTargetSystemPath(entry.getValue()));
        command.add("-o"); //$NON-NLS-1$
        command.add(objectFile);
          
        final MessageConsole messageConsole = ConsoleUtil.findConsole(Messages.CPPB_ConsoleName);
        messageConsole.clearConsole();
        final MessageConsoleStream mcStream = messageConsole.newMessageStream();
        final int returnCode = this.fTargetOpHelper.run(command, new IProcessOuputListener() {

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
                
        monitor.worked(1);
        
        if (returnCode != 0) {
          IResourceUtils.addBuildMarkerTo(getProject(), NLS.bind(Messages.CPPB_CompilErrorMsg, entry.getKey().getName()), 
                                          IMarker.SEVERITY_ERROR, entry.getKey().getAbsolutePath(), IMarker.PRIORITY_HIGH);
          return false;
        }
      }
    } catch (IOException except) {
      IResourceUtils.addBuildMarkerTo(getProject(), NLS.bind(Messages.CPPB_TargetOpError, this.fConfName), 
                                      IMarker.SEVERITY_ERROR, getProject().getLocation().toString(), IMarker.PRIORITY_HIGH);
      LaunchCore.log(IStatus.ERROR, NLS.bind(Messages.CPPB_TargetOpError, this.fConfName), except);
      return false;
    } catch (InterruptedException except) {
      IResourceUtils.addBuildMarkerTo(getProject(), NLS.bind(Messages.CPPB_CancelOpMsg, this.fConfName), 
                                      IMarker.SEVERITY_WARNING, getProject().getLocation().toString(), IMarker.PRIORITY_LOW);
      return false;
    } finally {
      monitor.done();
    }
    return true;
  }
  
  public final String getWorkspaceDir() {
    return this.fWorkspaceDir;
  }
  
  public final ITargetOpHelper getTargetOpHelper() {
    return this.fTargetOpHelper;
  }
  
  public final boolean hasAllPrerequisites() {
    return (this.fTargetOpHelper != null) && this.fPlatformConf.isComplete(true);
  }

  // --- Code for descendants
  
  protected final void addCompiledFile(final File srcFile, final String destFilePath) {
    this.fCompiledFiles.put(srcFile, destFilePath);
  }

  protected final IProject getProject() {
    return this.fProject;
  }
  
  // --- Fields
  
  private final String fConfName;
  
  private final IProject fProject;
  
  private final String fWorkspaceDir;
  
  private final IX10PlatformConf fPlatformConf;
  
  private final ITargetOpHelper fTargetOpHelper;
  
  private final Map<File, String> fCompiledFiles = new HashMap<File, String>();
  
  private final List<String> fObjectFiles = new LinkedList<String>();
  
  
  static final String CC_EXT = ".cc"; //$NON-NLS-1$
  
  private static final String INCLUDE_OPT = "-I"; //$NON-NLS-1$

}
