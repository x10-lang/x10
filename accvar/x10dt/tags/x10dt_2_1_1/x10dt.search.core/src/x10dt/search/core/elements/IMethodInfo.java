/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.elements;


/**
 * Provides method information for a given method coming from the search index database.
 * 
 * @author egeay
 */
public interface IMethodInfo extends IMemberInfo {
    
  /**
   * Returns the method parameters type information.
   * 
   * @return A non-null, but possibly empty, array of type info.
   */
  public ITypeInfo[] getParameters();
  
  /**
   * Returns the method return type information. This may not have location information in case of X10 void type.
   * 
   * @return A non-null type information.
   */
  public ITypeInfo getReturnType();
  
  /**
   * Indicates if the current method is a constructor or not.
   * 
   * @return True if it is a constructor, false otherwise.
   */
  public boolean isConstructor();
  
}
