/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.builder;

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IFunctor;

/**
 * Functor converting a {@link IPath} into a {@link File}.
 * 
 * @author egeay
 */
public final class IPathToFileFunc implements IFunctor<IPath, File> {
   
  IPathToFileFunc() {
    this.fRootPath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
  }
  
  
  // --- Interface methods implementation
  
  public File apply(final IPath path) {
    final int count = path.matchingFirstSegments(this.fRootPath);
    if (this.fRootPath.segmentCount() == count) {
      return path.removeFirstSegments(count).makeAbsolute().toFile();
    } else {
      return path.toFile();
    }
  }
  
  // --- Fields
  
  private final IPath fRootPath;

}
