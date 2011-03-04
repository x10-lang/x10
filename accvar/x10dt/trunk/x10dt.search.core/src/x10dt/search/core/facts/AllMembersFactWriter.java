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
import static x10dt.search.core.pdb.X10FactTypeNames.X10_FieldRefs;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_MethodRefs;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_MethodToTypeRefs;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_TypeToTypeRefs;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.imp.pdb.facts.ISet;
import org.eclipse.imp.pdb.facts.IValue;

import polyglot.ast.Call;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Field;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.TypeNode;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.MethodDef;
import x10.types.MethodInstance;
import x10.types.VoidType;


final class AllMembersFactWriter extends AbstractFactWriter implements IFactWriter {
  
  AllMembersFactWriter(final String scopeTypeName, final boolean shouldComputeReferences) {
    super(scopeTypeName);
    this.fShouldComputeReferences = shouldComputeReferences;
  }

  // --- Interface methods implementation
  
  public void classDeclVisitBegin(final ClassDecl classDecl) {
    this.fClassMemberStack.addFirst(classDecl);
    
    final ClassDef classDef = classDecl.classDef();
    final ClassType classType = classDef.asType();
    final IValue x10Type = findOrCreateType(classType);
    insertValue(X10_AllTypes, x10Type);

    final List<MethodDef> methodDefs = classDef.methods();
    final List<ConstructorDef> constructorDefs = classDef.constructors();
    IValue[] methods = new IValue[methodDefs.size() + constructorDefs.size()];
    int i = -1;
    for (final MethodDef methodDef : methodDefs) {
      if (! methodDef.position().isCompilerGenerated()) {
        methods[++i] = findOrCreateMethod(methodDef);
      }
    }
    for (final ConstructorDef constructorDef : constructorDefs) {
      if (! constructorDef.position().isCompilerGenerated()) {
        methods[++i] = findOrCreateConstructor(constructorDef);
      }
    }
    if (i >= 0) {
      // If there were compiler-generated methods, the array is shorter than we first thought
      if (i != methods.length - 1) {
        IValue[] actualMethods = new IValue[i+1];
        System.arraycopy(methods, 0, actualMethods, 0, i+1);
        methods = actualMethods;
      }
      insertValue(X10_AllMethods, getValueFactory().tuple(x10Type, getValueFactory().list(methods)));
    }

    final List<FieldDef> fieldDefs = classDef.fields();
    IValue[] fields = new IValue[fieldDefs.size()];
    i = -1;
    for (final FieldDef fieldDef : fieldDefs) {
      if (! fieldDef.position().isCompilerGenerated()) {
        fields[++i] = findOrCreateField(fieldDef);
      }
    }
    if (i >= 0) {
      // If there were compiler-generated fields, the array is shorter than we first thought
      if (i != fields.length - 1) {
        IValue[] actualFields = new IValue[i+1];
        System.arraycopy(fields, 0, actualFields, 0, i+1);
        fields = actualFields;
      }
      insertValue(X10_AllFields, getValueFactory().tuple(x10Type, getValueFactory().list(fields)));
    }
  }
  
  public void classDeclVisitEnd(final ClassDecl classDecl) {
    final ISet typeRefsSet = getValueFactory().set(this.fTypeToTypeRefs.toArray(new IValue[this.fTypeToTypeRefs.size()]));
    insertValue(X10_TypeToTypeRefs, getValueFactory().tuple(findOrCreateType(classDecl.classDef().asType()), typeRefsSet));
    
    this.fClassMemberStack.poll();
  }
  
  public void methodDeclVisitBegin(final MethodDecl methodDecl) {
    this.fClassMemberStack.addFirst(methodDecl);
  }
  
  public void methodDeclVisitEnd(final MethodDecl methodDecl) {
    final ISet methodRefsSet = getValueFactory().set(this.fMethodRefs.toArray(new IValue[this.fMethodRefs.size()]));
    insertValue(X10_MethodRefs, getValueFactory().tuple(findOrCreateMethod(methodDecl.methodDef()), methodRefsSet));
    
    final ISet fieldRefsSet = getValueFactory().set(this.fFieldRefs.toArray(new IValue[this.fFieldRefs.size()]));
    insertValue(X10_FieldRefs, getValueFactory().tuple(findOrCreateMethod(methodDecl.methodDef()), fieldRefsSet));
    
    final ISet typeRefsSet = getValueFactory().set(this.fMethodToTypeRefs.toArray(new IValue[this.fMethodToTypeRefs.size()]));
    insertValue(X10_MethodToTypeRefs, getValueFactory().tuple(findOrCreateMethod(methodDecl.methodDef()), typeRefsSet));
    
    this.fMethodRefs.clear();
    this.fFieldRefs.clear();
    this.fMethodToTypeRefs.clear();
    
    this.fClassMemberStack.poll();
  }

  public void visit(final Call methodCall) {
    final MethodInstance methodInstance = methodCall.methodInstance();
    if (this.fShouldComputeReferences && ! methodInstance.position().isCompilerGenerated()) {
      this.fMethodRefs.add(getValueFactory().tuple(findOrCreateMethod(methodInstance.def()),
                                                   createSourceLocation(methodCall.position())));
    }
  }

  public void visit(final New newCall) {
    final ConstructorInstance ctorInstance = newCall.constructorInstance();
    if (this.fShouldComputeReferences && ! ctorInstance.position().isCompilerGenerated()) {
      this.fMethodRefs.add(getValueFactory().tuple(findOrCreateConstructor(ctorInstance.def()),
                                                   createSourceLocation(newCall.position())));
    }
  }

  public void visit(final ConstructorCall ctorCall) {
    final ConstructorInstance ctorInstance = ctorCall.constructorInstance();
    if (this.fShouldComputeReferences && ! ctorInstance.position().isCompilerGenerated()) {
      this.fMethodRefs.add(getValueFactory().tuple(findOrCreateConstructor(ctorInstance.def()),
                                                   createSourceLocation(ctorCall.position())));
    }
  }

  public void visit(final Field fieldAccess) {
    final FieldInstance fieldInstance = fieldAccess.fieldInstance();
    if (this.fShouldComputeReferences && ! fieldInstance.position().isCompilerGenerated()) {
      this.fFieldRefs.add(getValueFactory().tuple(findOrCreateField(fieldInstance.def()),
                                                  createSourceLocation(fieldAccess.position())));
    }
  }
  
  public void visit(final TypeNode typeNode) {
    if (! (typeNode.typeRef().get() instanceof VoidType)) {
      if (this.fClassMemberStack.peek() instanceof MethodDecl) {
        this.fMethodToTypeRefs.add(getValueFactory().tuple(findOrCreateType(typeNode.typeRef().get()),
                                                           createSourceLocation(typeNode.position())));
      } else {
        this.fTypeToTypeRefs.add(getValueFactory().tuple(findOrCreateType(typeNode.typeRef().get()),
                                                         createSourceLocation(typeNode.position())));
      }
    }
  }
  
  // --- Fields
  
  private final boolean fShouldComputeReferences;
  
  private Set<IValue> fMethodRefs = new HashSet<IValue>();
  
  private Set<IValue> fFieldRefs = new HashSet<IValue>();
  
  private Set<IValue> fMethodToTypeRefs = new HashSet<IValue>();
  
  private Set<IValue> fTypeToTypeRefs = new HashSet<IValue>();
  
  private LinkedList<ClassMember> fClassMemberStack = new LinkedList<ClassMember>();

}
