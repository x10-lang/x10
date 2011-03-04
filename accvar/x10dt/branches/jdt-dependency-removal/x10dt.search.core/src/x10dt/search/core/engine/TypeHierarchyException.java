/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.engine;

/**
 * Encapsulates exception information for the X10 type hierarchy. See {@link ITypeHierarchy}.
 * 
 * @author egeay
 */
public final class TypeHierarchyException extends Exception {

  /**
   * Creates the type hierarchy exception with the message provided.
   * 
   * @param message The exception message.
   */
  public TypeHierarchyException(final String message) {
    super(message);
  }
  
  // --- Fields
  
  private static final long serialVersionUID = 6244277136788115474L;
  
}
