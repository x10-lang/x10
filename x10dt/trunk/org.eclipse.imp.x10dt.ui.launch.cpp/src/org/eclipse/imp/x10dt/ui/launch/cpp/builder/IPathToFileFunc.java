/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.builder;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.imp.x10dt.ui.launch.cpp.utils.collections.IFunctor;


final class IPathToFileFunc implements IFunctor<IPath, File> {

  // --- Interface methods implementation
  
  public File apply(final IPath path) {
    return path.toFile();
  }

}
