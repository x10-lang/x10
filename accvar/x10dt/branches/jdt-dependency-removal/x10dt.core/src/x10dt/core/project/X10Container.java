/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.core.project;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.imp.model.IPathContainer;
import org.eclipse.imp.model.IPathEntry;


final class X10Container implements IPathContainer {
  
  X10Container(final IPath path, List<IPathEntry> classPathEntries) {
    this.fPath = path;
    this.fClassPathEntries = classPathEntries;
  }

  // --- Interface methods implementation
  
  public List<IPathEntry> getPathEntries() {
    return this.fClassPathEntries;
  }

  public String getDescription() {
    return "X10 Runtime Container"; //$NON-NLS-1$
  }

  public PathContainerType getType() {
    return PathContainerType.APPLICATION;
  }

  public IPath getPath() {
    return this.fPath;
  }
  
  // --- Fields
  
  private final IPath fPath;
  
  private final List<IPathEntry> fClassPathEntries;

}
