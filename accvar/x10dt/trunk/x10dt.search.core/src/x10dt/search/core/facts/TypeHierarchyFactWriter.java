/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.facts;

import static x10dt.search.core.pdb.X10FactTypeNames.X10_TypeHierarchy;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.imp.pdb.facts.ITuple;
import org.eclipse.osgi.util.NLS;

import polyglot.ast.Call;
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Field;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.TypeNode;
import polyglot.types.ClassType;
import polyglot.types.Type;
import x10dt.search.core.Messages;
import x10dt.search.core.SearchCoreActivator;


final class TypeHierarchyFactWriter extends AbstractFactWriter implements IFactWriter {
  
  TypeHierarchyFactWriter(final String scopeTypeName) {
    super(scopeTypeName);
    this.fAllMembersFactWriter = new AllMembersFactWriter(scopeTypeName, true /* shouldComputeReferences */);
  }

  // --- Interface methods implementation
  
  public void classDeclVisitBegin(final ClassDecl classDecl) {
    final ClassType classType = classDecl.classDef().asType();
    if (! classType.position().isCompilerGenerated()) {
      this.fAllMembersFactWriter.classDeclVisitBegin(classDecl);

      final ITuple classTypeTuple = this.fAllMembersFactWriter.findOrCreateType(classType);

      final Type superType = classType.superClass();
      final ClassType superClass;
      if (superType == null) {
        superClass = null;
      } else {
        if (superType.isClass()) {
          superClass = superType.toClass();
        } else {
          SearchCoreActivator.log(IStatus.WARNING, NLS.bind(Messages.THFWV_UnknownSuperType, superType, classDecl.name()));
          superClass = null;
        }
      }
      if ((superClass != null) && ! superClass.position().isCompilerGenerated()) {
        insertValue(X10_TypeHierarchy, 
                    getValueFactory().tuple(classTypeTuple, this.fAllMembersFactWriter.findOrCreateType(superClass)));
      }

      for (final Type interfaceType : classType.interfaces()) {
        if ((interfaceType != null) && (interfaceType instanceof ClassType) &&
            ! interfaceType.position().isCompilerGenerated()) {
          insertValue(X10_TypeHierarchy, 
                      getValueFactory().tuple(classTypeTuple, this.fAllMembersFactWriter.findOrCreateType(interfaceType)));
        }
      }
    }
  }
  
  public void classDeclVisitEnd(final ClassDecl classDecl) {
    this.fAllMembersFactWriter.classDeclVisitEnd(classDecl);
  }
  
  public void methodDeclVisitBegin(final MethodDecl methodDecl) {
    this.fAllMembersFactWriter.methodDeclVisitBegin(methodDecl);
  }
  
  public void methodDeclVisitEnd(final MethodDecl methodDecl) {
    this.fAllMembersFactWriter.methodDeclVisitEnd(methodDecl);
  }
  
  public void visit(final Call methodCall) {
    this.fAllMembersFactWriter.visit(methodCall);
  }

  public void visit(final New newCall) {
    this.fAllMembersFactWriter.visit(newCall);
  }

  public void visit(final ConstructorCall ctorCall) {
    this.fAllMembersFactWriter.visit(ctorCall);
  }

  public void visit(final Field fieldAccess) {
    this.fAllMembersFactWriter.visit(fieldAccess);
  }
  
  public void visit(final TypeNode typeNode) {
    this.fAllMembersFactWriter.visit(typeNode);
  }
  
  // --- Fields
  
  private final AbstractFactWriter fAllMembersFactWriter;

}
