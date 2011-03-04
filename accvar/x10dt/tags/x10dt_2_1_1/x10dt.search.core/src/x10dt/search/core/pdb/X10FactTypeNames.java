/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.pdb;

import org.eclipse.imp.pdb.facts.ISourceLocation;

/**
 * The list of X10 fact type names that are supported by X10 Search Engine.
 * 
 * <p>Certain of the resulting facts are also considered outputs of the IMP PDB database. 
 * See the extension point implementation of <b>org.eclipse.imp.pdb.analyzerFactory</b>.
 * 
 * @author egeay
 */
public final class X10FactTypeNames {
  
  /**
   * Fact type name identifying X10 type as a string with a fully qualified name.
   */
  public static final String X10_TypeName = "x10.typeName"; //$NON-NLS-1$
  
  /**
   * Fact type name identifying X10 method name as a string.
   */
  public static final String X10_MethodName = "x10.methodName"; //$NON-NLS-1$
  
  /**
   * Fact type name identifying X10 field name as a string.
   */
  public static final String X10_FieldName = "x10.fieldName"; //$NON-NLS-1$
  
  /**
   * Fact type name identifying X10 type as a tuple of ({@value #X10_TypeName}, {@link ISourceLocation}, int,
   * [{@value #X10_TypeName}]).
   */
  public static final String X10_Type = "x10.type"; //$NON-NLS-1$
  
  /**
   * Fact type name identifying X10 method as a tuple of 
   * ({@link #X10_MethodName}, {@link #X10_TypeName}, list({@link #X10_TypeName}), {@link ISourceLocation}, int).
   */
  public static final String X10_Method = "x10.method"; //$NON-NLS-1$
  
  /**
   * Fact type name identifying X10 field as a tuple of 
   * ({@link #X10_FieldName}, {@link #X10_TypeName}, {@link ISourceLocation}, int).
   */
  public static final String X10_Field = "x10.field"; //$NON-NLS-1$
  
  // --- Outputs
  
  /**
   * Fact type name identifying all X10 types as a set of X10 type name (see {@link #X10_Type}).
   */
  public static final String X10_AllTypes = "x10.all.types"; //$NON-NLS-1$
  
  /**
   * Fact type name identifying all X10 methods defined for an X10 type as a binary relation of a subset of
   * ({@link #X10_TypeName} X list({@link #X10_Method}).
   */
  public static final String X10_AllMethods = "x10.type.methods"; //$NON-NLS-1$
  
  /**
   * Fact type name identifying all X10 fields defined for an X10 type as a binary relation of a subset of
   * ({@link #X10_TypeName} X list({@link #X10_Field}).
   */
  public static final String X10_AllFields = "x10.type.fields"; //$NON-NLS-1$
  
  /**
   * Fact type name identifying an X10 type hierarchy as a binary relation of a subset of 
   * {@value #X10_TypeName} X {@value #X10_TypeName}.
   */
  public static final String X10_TypeHierarchy = "x10.typeHierarchy"; //$NON-NLS-1$
  
  // --- Scope
  
  /**
   * Fact type name identifying the application scope.
   */
  public static final String APPLICATION = "application"; //$NON-NLS-1$
  
  /**
   * Fact type name identifying the library scope.
   */
  public static final String LIBRARY = "library"; //$NON-NLS-1$
  
  /**
   * Fact type name identifying the runtime scope.
   */
  public static final String RUNTIME = "runtime"; //$NON-NLS-1$

}
