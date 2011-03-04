/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.elements;

import org.eclipse.imp.model.ICompilationUnit;

/**
 * Provides information for a given member coming from the search index database.
 * 
 * @author egeay
 */
public interface IMemberInfo extends IX10Element {
  
  /**
   * Returns the enclosing compilation unit entity for this type.
   * 
   * @return A <b>possibly null</b> object instance if we not could access the enclosing entity.
   */
  public ICompilationUnit getCompilationUnit();
  
  /**
   * Returns the type in which this member is declared, or <b>null</b> if this member is not declared in a type 
   * (for example, a top-level type).
   *
   * @return The type in which this member is declared, or <b>null</b> if there is none.
   */
  public ITypeInfo getDeclaringType();
  
  /**
   * Returns the member name represented.
   * 
   * @return A non-null string.
   */
  public String getName();
  
  /**
   * Returns a code identifying uniquely the list of flags relevant for the member.
   * 
   * <p>Look at {@link X10} to test the existence of some particular flags.
   * 
   * @return A natural number.
   */
  public int getX10FlagsCode();

}
