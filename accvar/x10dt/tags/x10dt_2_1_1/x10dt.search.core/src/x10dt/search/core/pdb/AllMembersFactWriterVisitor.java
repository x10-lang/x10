/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.pdb;

import static x10dt.search.core.pdb.X10FactTypeNames.X10_AllFields;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_AllMethods;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_AllTypes;

import java.util.List;

import org.eclipse.imp.pdb.facts.ITuple;
import org.eclipse.imp.pdb.facts.IValue;

import polyglot.ast.ClassDecl;
import polyglot.ast.Node;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.FieldDef;
import polyglot.types.MethodDef;
import polyglot.visit.NodeVisitor;


final class AllMembersFactWriterVisitor extends FactWriterVisitor {
  
  // --- Overridden methods
  
  public NodeVisitor enter(final Node node) {
    if (node instanceof ClassDecl) {
      final ClassDef classDef = ((ClassDecl) node).classDef();
      final ClassType classType = classDef.asType();
      final IValue typeNameValue = createTypeName(classType.fullName().toString());
      final ITuple tuple;
      if (classDef.outer() == null) {
        tuple = getValueFactory().tuple(typeNameValue, getSourceLocation(classType.position()),
                                        createModifiersCodeValue(classType.flags()));
      } else {
        final IValue outerTypeNameValue = createTypeName(classDef.outer().get().asType().fullName().toString());
        tuple = getValueFactory().tuple(typeNameValue, getSourceLocation(classType.position()),
                                        createModifiersCodeValue(classType.flags()), outerTypeNameValue);
      }
      insertValue(X10_AllTypes, tuple);
      
      final List<MethodDef> methodDefs = classDef.methods();
      final IValue[] methods = new IValue[methodDefs.size()];
      int i = -1;
      for (final MethodDef methodDef : methodDefs) {
        methods[++i] = createMethodValue(methodDef);
      }      
      insertValue(X10_AllMethods, getValueFactory().tuple(typeNameValue, getValueFactory().list(methods)));
      
      final List<FieldDef> fieldDefs = classDef.fields();
      final IValue[] fields = new IValue[fieldDefs.size()];
      i = -1;
      for (final FieldDef fieldDef : fieldDefs) {
        fields[++i] = createFieldValue(fieldDef);
      }
      insertValue(X10_AllFields, getValueFactory().tuple(typeNameValue, getValueFactory().list(fields)));
    }
    return this;
  }
  
}
