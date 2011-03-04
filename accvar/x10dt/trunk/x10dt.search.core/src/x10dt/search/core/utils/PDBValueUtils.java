/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.utils;

import static x10dt.search.core.pdb.X10FactTypeNames.X10_FieldName;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_MethodName;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_TypeName;

import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.eclipse.imp.pdb.facts.ITuple;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.impl.fast.ValueFactory;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;

import x10dt.search.core.elements.IFieldInfo;
import x10dt.search.core.elements.IMethodInfo;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.elements.IX10Element;
import x10dt.search.core.pdb.SearchDBTypes;

/**
 * Utility methods to convert {@link IX10Element} data structures into IMP PDB fact values.
 * 
 * @author egeay
 */
public final class PDBValueUtils {
  
  /**
   * Converts an {@link ITypeInfo} into its equivalent tuple.
   * 
   * @param typeInfo The type info to convert.
   * @return A non-null tuple implementation.
   */
  public static ITuple convert(final ITypeInfo typeInfo) {
    final IValueFactory valueFactory = ValueFactory.getInstance();
    final Type typeNameFactory = SearchDBTypes.getInstance().getType(X10_TypeName);
    final IValue typeName = typeNameFactory.make(valueFactory, typeInfo.getName());
    final IValue modifier = TypeFactory.getInstance().integerType().make(valueFactory, typeInfo.getX10FlagsCode());
    if (typeInfo.getDeclaringType() == null) {
      if (typeInfo.getLocation() == null) {
        return valueFactory.tuple(typeName);
      } else {
        return valueFactory.tuple(typeName, getLocation(valueFactory, typeInfo.getLocation()), modifier);
      }
    } else {
      return valueFactory.tuple(typeName, getLocation(valueFactory, typeInfo.getLocation()), modifier,
                                typeNameFactory.make(valueFactory, typeInfo.getDeclaringType().getName()));
    }
  }
  
  /**
   * Converts a {@link IMethodInfo} into its equivalent tuple.
   * 
   * @param methodInfo The method info to convert.
   * @return A non-null tuple implementation.
   */
  public static ITuple convert(final IMethodInfo methodInfo) {
    final IValueFactory valueFactory = ValueFactory.getInstance();
    final IValue methodName = SearchDBTypes.getInstance().getType(X10_MethodName).make(valueFactory, methodInfo.getName());
    final ISourceLocation location = getLocation(valueFactory, methodInfo.getLocation());
    final IValue modifier = TypeFactory.getInstance().integerType().make(valueFactory, methodInfo.getX10FlagsCode());
    final ITuple returnType = convert(methodInfo.getReturnType());
    final ITuple[] parameterTypes = new ITuple[methodInfo.getParameters().length];
    int i = -1;
    for (final ITypeInfo parameterTypeInfo : methodInfo.getParameters()) {
      parameterTypes[++i] = convert(parameterTypeInfo);
    }
    return valueFactory.tuple(location, methodName, returnType, valueFactory.list(parameterTypes), modifier);
  }
  
  /**
   * Converts a {@link IFieldInfo} into its equivalent tuple.
   * 
   * @param fieldInfo The field info to convert.
   * @return A non-null tuple implementation.
   */
  public static ITuple convert(final IFieldInfo fieldInfo) {
    final IValueFactory valueFactory = ValueFactory.getInstance();
    final IValue fieldName = SearchDBTypes.getInstance().getType(X10_FieldName).make(valueFactory, fieldInfo.getName());
    final ISourceLocation location = getLocation(valueFactory, fieldInfo.getLocation());
    final IValue modifier = TypeFactory.getInstance().integerType().make(valueFactory, fieldInfo.getX10FlagsCode());
    final ITuple fieldType = convert(fieldInfo.getFieldTypeInfo());
    return valueFactory.tuple(location, fieldName, fieldType, modifier);
  }
  
  // --- Private code
  
  private PDBValueUtils() {}
  
  private static final ISourceLocation getLocation(final IValueFactory valueFactory, final ISourceLocation sourceLoc) {
    return valueFactory.sourceLocation(sourceLoc.getURI(), sourceLoc.getOffset(), sourceLoc.getLength(), 
                                       sourceLoc.getBeginLine(), sourceLoc.getEndLine(), 
                                       sourceLoc.getBeginColumn(), sourceLoc.getEndColumn());
  }

}
