/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.launch.builder;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.imp.x10dt.ui.cpp.launch.LaunchCore;
import org.eclipse.imp.x10dt.ui.cpp.launch.LaunchMessages;
import org.eclipse.imp.x10dt.ui.cpp.launch.utils.IResourceUtils;
import org.eclipse.osgi.util.NLS;

import polyglot.util.AbstractErrorQueue;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.Position;


final class X10ErrorQueue extends AbstractErrorQueue implements ErrorQueue {

  X10ErrorQueue(final int errorsLimit, final IProject project, final String compilerName) {
    super(errorsLimit, compilerName);
    this.fProject = project;
    this.fRoot = ResourcesPlugin.getWorkspace().getRoot();
  }
  
  // --- Abstract methods implementation
  
  protected void displayError(final ErrorInfo error) {
    if (error.getPosition() == null) {
      LaunchCore.log(IStatus.WARNING, NLS.bind(LaunchMessages.EQ_PosErrorMsg, error));
    } else {
      final int severity = (error.getErrorKind() == ErrorInfo.WARNING) ? IMarker.SEVERITY_WARNING : IMarker.SEVERITY_ERROR;
      final Position position = error.getPosition();
      try {
        final IFile[] files = this.fRoot.findFilesForLocationURI(new URI(position.file()));
        if (files.length == 0) {
          // We could not find the associated resource. So we add a marker to the project.
          IResourceUtils.addMarkerTo(this.fProject, error.getMessage(), severity, position.file(), IMarker.PRIORITY_NORMAL, 
                                     position.line(), position.offset(), position.endOffset());
        } else {
          for (final IFile file : files) {
            IResourceUtils.addMarkerTo(file, error.getMessage(), severity, position.file(), IMarker.PRIORITY_NORMAL, 
                                       position.line(), position.offset(), position.endOffset());
          }
        }
      } catch (URISyntaxException except) {
        LaunchCore.log(IStatus.ERROR, NLS.bind(LaunchMessages.EQ_URIErrorMsg, position.file()), except);
      } catch (CoreException except) {
        LaunchCore.log(except.getStatus());
      }
    }
  }
  
  // --- Fields
  
  private final IProject fProject;
  
  private final IWorkspaceRoot fRoot;

}
