/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.x10dt.ui.launch.core.LaunchCore;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IResourceUtils;
import org.eclipse.osgi.util.NLS;

import polyglot.util.AbstractErrorQueue;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.Position;


final class X10ErrorQueue extends AbstractErrorQueue implements ErrorQueue {

  X10ErrorQueue(final int errorsLimit, final String compilerName) {
    super(errorsLimit, compilerName);
  }
  
  // --- Abstract methods implementation
  
  protected void displayError(final ErrorInfo error) {
    if (error.getPosition() == null) {
      LaunchCore.log(IStatus.WARNING, NLS.bind(Messages.EQ_PosErrorMsg, error));
    } else {
      final int severity = (error.getErrorKind() == ErrorInfo.WARNING) ? IMarker.SEVERITY_WARNING : IMarker.SEVERITY_ERROR;
      final Position position = error.getPosition();
      final IPath rootPath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
      final IPath filePath = new Path(position.file());
      final String workspaceRelatedPath = filePath.removeFirstSegments(rootPath.segmentCount()).makeAbsolute().toString();
      final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(workspaceRelatedPath));
      IResourceUtils.addBuildMarkerTo(file, error.getMessage(), severity, position.file(), IMarker.PRIORITY_NORMAL, 
                                      position.line(), position.offset(), position.endOffset());
    }
  }
  
}
