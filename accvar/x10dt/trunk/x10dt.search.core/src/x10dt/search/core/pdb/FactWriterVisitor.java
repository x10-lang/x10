/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.pdb;

import polyglot.ast.ArrayTypeNode;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.visit.NodeVisitor;
import x10dt.search.core.facts.IFactWriter;


final class FactWriterVisitor extends NodeVisitor {
  
  FactWriterVisitor(final IFactWriter factWriter) {
    this.fFactWriter = factWriter;
  }
  
  // --- Overridden methods
  
  public NodeVisitor enter(final Node node) {
    if (! node.position().isCompilerGenerated()) {
      if (node instanceof ClassDecl) {
        this.fFactWriter.classDeclVisitBegin((ClassDecl) node);
      } else if (node instanceof MethodDecl) {
        this.fFactWriter.methodDeclVisitBegin((MethodDecl) node);
      } else if (node instanceof Call) {
        this.fFactWriter.visit((Call) node);
      } else if (node instanceof New) {
        this.fFactWriter.visit((New) node);
      } else if (node instanceof ConstructorCall) {
        this.fFactWriter.visit((ConstructorCall) node);
      } else if (node instanceof Field) {
        this.fFactWriter.visit((Field) node);
      } else if (node instanceof FieldAssign) {
        // Hack since FieldAssign does not visit a Field node !?
        this.fFactWriter.visit((Field) ((FieldAssign) node).left());
      } else if (node instanceof CanonicalTypeNode) {
        this.fFactWriter.visit((TypeNode) node);
      } else if (node instanceof ArrayTypeNode) {
        this.fFactWriter.visit(((ArrayTypeNode) node).base());
      }
    }
    return this;
  }
  
  public Node leave(final Node oldNode, final Node node, final NodeVisitor visitor) {
    if (! node.position().isCompilerGenerated()) {
      if (node instanceof ClassDecl) {
        this.fFactWriter.classDeclVisitEnd((ClassDecl) node);
      } else if (node instanceof MethodDecl) {
        this.fFactWriter.methodDeclVisitEnd((MethodDecl) node);
      }
    }
    return node;
  }
  
  // --- Fields
  
  private final IFactWriter fFactWriter;
  
}
