/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.utils;

import x10dt.search.core.elements.ITypeInfo;
import x10dt.ui.launch.core.utils.IFunctor;

/**
 * Functor to extract the name of an {@link ITypeInfo} instance.
 * 
 * @author egeay
 */
public final class GetNameFunc implements IFunctor<ITypeInfo, String> {

  // --- Interface methods implementation
  
  public String apply(final ITypeInfo element) {
    return element.getName();
  }

}
