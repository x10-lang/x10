/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.facts;

/**
 * Factory methods to create implementations of {@link IFactWriter} interface.
 * 
 * @author egeay
 */
public final class FactWriterFactory {
  
  /**
   * Creates an implementation of {@link IFactWriter} that collects all types, methods and fields of a given class declaration.
   * 
   * @param scopeTypeName The scope type name to use for the fact writer.
   * @param shouldComputeReferences Indicates if the references should also be collected or not.
   * @return A non-null implementation of {@link IFactWriter}.
   */
  public static IFactWriter createAllMembersFactWriter(final String scopeTypeName, final boolean shouldComputeReferences) {
    return new AllMembersFactWriter(scopeTypeName, shouldComputeReferences);
  }
  
  /**
   * Creates an implementation of {@link IFactWriter} that collects all types, methods and fields of a given class 
   * declaration with its references, as well as its superclass and interfaces.
   * 
   * @param scopeTypeName The scope type name to use for the fact writer.
   * @return A non-null implementation of {@link IFactWriter}.
   */
  public static IFactWriter createTypeHierarchyFactWriter(final String scopeTypeName) {
    return new TypeHierarchyFactWriter(scopeTypeName);
  }
  
  // --- Private code
  
  private FactWriterFactory() {}

}
