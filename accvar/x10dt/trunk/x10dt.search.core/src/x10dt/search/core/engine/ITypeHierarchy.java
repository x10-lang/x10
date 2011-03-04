/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.engine;

import x10dt.search.core.elements.ITypeInfo;


/**
 * Provides a type hierarchy for a given type.
 * 
 * <p>Creates instance from {@link X10SearchEngine}.
 * 
 * @author egeay
 */
public interface ITypeHierarchy {
  
  /**
   * Returns true if the given type info is present in the type hierarchy built.
   * 
   * @param typeInfo The type info to compare it to.
   * @return True if the type info is present in the type hierarchy, false otherwise.
   */
  public boolean contains(final ITypeInfo typeInfo);
  
  /**
   * Returns all the classes present in the type hierarchy built.
   * 
   * @return A non-null, but possibly empty, array of type names.
   */
  public ITypeInfo[] getAllClasses();
  
  /**
   * Returns all the interfaces present in the type hierarchy built.
   * 
   * @return A non-null, but possibly empty, array of type info.
   */
  public ITypeInfo[] getAllInterfaces();
  
  /**
   * Returns all the sub classes present in the type hierarchy built for the given type name.
   * 
   * @param typeInfo The type information of interest.
   * @return A non-null, but possibly empty, array of type info.
   */
  public ITypeInfo[] getAllSubClasses(final ITypeInfo typeInfo);
  
  /**
   * Returns all the sub types present in the type hierarchy built for the given type name.
   * 
   * @param typeInfo The type information of interest.
   * @return A non-null, but possibly empty, array of type info.
   */
  public ITypeInfo[] getAllSubTypes(final ITypeInfo typeInfo);
  
  /**
   * Returns all the super classes present in the type hierarchy built for the given type name.
   * 
   * @param typeInfo The type information of interest.
   * @return A non-null, but possibly empty, array of type info.
   */
  public ITypeInfo[] getAllSuperClasses(final ITypeInfo typeInfo);
  
  /**
   * Returns all the super interfaces present in the type hierarchy built for the given type name.
   * 
   * @param typeInfo The type information of interest.
   * @return A non-null, but possibly empty, array of type info.
   */
  public ITypeInfo[] getAllSuperInterfaces(final ITypeInfo typeInfo);
  
  /**
   * Returns all the super types present in the type hierarchy built for the given type name.
   * 
   * @param typeInfo The type information of interest.
   * @return A non-null, but possibly empty, array of type info.
   */
  public ITypeInfo[] getAllSuperTypes(final ITypeInfo typeInfo);
  
  /**
   * Returns the direct interfaces in the type hierarchy built for the given type name.
   * 
   * @param typeInfo The type information of interest.
   * @return A non-null, but possibly empty, array of type info.
   */
  public ITypeInfo[] getInterfaces(final ITypeInfo typeInfo);
  
  /**
   * Returns the direct sub-types in the type hierarchy built for the given type name.
   * 
   *  @param typeInfo The type information of interest.
   * @return A non-null, but possibly empty, array of type info.
   */
  public ITypeInfo[] getSubTypes(final ITypeInfo typeInfo);
  
  /**
   * Returns the super class in the type hierarchy built for the given type name.
   * 
   * @param typeInfo The type information of interest.
   * @return A non-null value if such type name has a super class, otherwise <b>null</b>.
   */
  public ITypeInfo getSuperClass(final ITypeInfo typeInfo);
  
  /**
   * Returns the direct super-types in the type hierarchy built for the given type name.
   * 
   * @param typeInfo The type information of interest.
   * @return A non-null, but possibly empty, array of type info.
   */
  public ITypeInfo[] getSuperTypes(final ITypeInfo typeInfo);
  
  /**
   * Returns the type information for which the type hierarchy was built.
   * 
   * @return A non-null type information.
   */
  public ITypeInfo getType();

}
