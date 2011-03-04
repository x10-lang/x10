/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.facts;

import polyglot.ast.Call;
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Field;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.TypeNode;

/**
 * Responsible for populating a set of facts as a result of looking at information contained within a given Polyglot AST node. 
 * 
 * @author egeay
 */
public interface IFactWriter {
  
  /**
   * Collects or writes fact information into a fact writer during the beginning of a class declaration node visit. 
   * 
   * @param classDecl The class declaration to process.
   */
  public void classDeclVisitBegin(final ClassDecl classDecl);
  
  /**
   * Collects or writes fact information into a fact writer during the ending of a class declaration node visit.
   * 
   * @param classDecl
   */
  public void classDeclVisitEnd(final ClassDecl classDecl);
  
  /**
   * Collects or writes fact information into a fact writer during the beginning of a method declaration node visit. 
   * 
   * @param methodDecl The method declaration to process.
   */
  public void methodDeclVisitBegin(final MethodDecl methodDecl);
  
  /**
   * Collects or writes fact information into a fact writer during the ending of a method declaration node visit.
   * 
   * @param methodDecl The method declaration to process.
   */
  public void methodDeclVisitEnd(final MethodDecl methodDecl);
  
  /**
   * Collects information for a fact writer related to an X10 method call.
   * 
   * @param methodCall The method call visited.
   */
  public void visit(final Call methodCall);
  
  /**
   * Collects information for a fact writer related to a new class creation.
   * 
   * @param newCall The new constructor call.
   */
  public void visit(final New newCall);
    
  /**
   * Collects information for a fact writer related to a constructor call.
   * 
   * @param ctorCall The constructor call visited.
   */
  public void visit(final ConstructorCall ctorCall);
  
  /**
   * Collects information for a fact writer related to a field access.
   * 
   * @param fieldAccess The field access visited.
   */
  public void visit(final Field fieldAccess);
  
  /**
   * Collects information for a fact writer related to a syntactic representation of a type in the AST. 
   * 
   * @param typeNode The type node visited.
   */
  public void visit(final TypeNode typeNode);

}
