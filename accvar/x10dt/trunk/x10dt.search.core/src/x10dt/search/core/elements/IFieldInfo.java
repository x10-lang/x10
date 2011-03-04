/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.elements;


/**
 * Provides field information for a given field coming from the search index database.
 * 
 * @author egeay
 */
public interface IFieldInfo extends IMemberInfo {
  
  /**
   * Returns the field type information.
   * 
   * @return A non-null type information.
   */
  public ITypeInfo getFieldTypeInfo();

}
