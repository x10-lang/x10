/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.facts;

import static x10dt.search.core.pdb.X10FactTypeNames.X10_AllFields;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_AllMethods;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_AllTypes;

import java.util.List;

import org.eclipse.imp.pdb.facts.ITuple;
import org.eclipse.imp.pdb.facts.IValue;

import polyglot.ast.ClassDecl;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.FieldDef;
import polyglot.types.MethodDef;


final class AllMembersFactWriter extends AbstractFactWriter implements IFactWriter {
  
  AllMembersFactWriter(final String scopeTypeName) {
    super(scopeTypeName);
  }

  // --- Overridden methods

  public void writeFacts(final ClassDecl classDecl) {
    final ClassDef classDef = classDecl.classDef();
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
    final List<ConstructorDef> constructorDefs = classDef.constructors();
    IValue[] methods = new IValue[methodDefs.size() + constructorDefs.size()];
    int i = -1;
    for (final MethodDef methodDef : methodDefs) {
      if (! methodDef.position().isCompilerGenerated()) {
        methods[++i] = createMethodValue(methodDef);
      }
    }
    for (final ConstructorDef constructorDef : constructorDefs) {
      if (! constructorDef.position().isCompilerGenerated()) {
        methods[++i] = createConstructorValue(constructorDef);
      }
    }
    if (i >= 0) {
      // If there were compiler-generated methods, the array is shorter than we first thought
      if (i != methods.length - 1) {
        IValue[] actualMethods = new IValue[i+1];
        System.arraycopy(methods, 0, actualMethods, 0, i+1);
        methods = actualMethods;
      }
      insertValue(X10_AllMethods, getValueFactory().tuple(typeNameValue, getValueFactory().list(methods)));
    }

    final List<FieldDef> fieldDefs = classDef.fields();
    IValue[] fields = new IValue[fieldDefs.size()];
    i = -1;
    for (final FieldDef fieldDef : fieldDefs) {
      if (fieldDef.position().isCompilerGenerated()) {
        continue;
      }
      fields[++i] = createFieldValue(fieldDef);
    }
    if (i >= 0) {
      // If there were compiler-generated fields, the array is shorter than we first thought
      if (i != fields.length - 1) {
        IValue[] actualFields = new IValue[i+1];
        System.arraycopy(fields, 0, actualFields, 0, i+1);
        fields = actualFields;
      }
      insertValue(X10_AllFields, getValueFactory().tuple(typeNameValue, getValueFactory().list(fields)));
    }
  }

}
