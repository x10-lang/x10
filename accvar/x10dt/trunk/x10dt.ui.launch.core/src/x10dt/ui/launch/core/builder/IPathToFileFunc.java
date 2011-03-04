/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package x10dt.ui.launch.core.builder;

import java.io.File;

import org.eclipse.core.runtime.IPath;

import x10dt.ui.launch.core.utils.IFunctor;

/**
 * Functor converting a {@link IPath} into a {@link File}.
 * 
 * @author egeay
 */
public final class IPathToFileFunc implements IFunctor<IPath, File> {
   
  // --- Interface methods implementation
  
  public File apply(final IPath path) {
    return path.toFile();
  }

}
