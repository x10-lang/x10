/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.pdb;

import polyglot.ast.ClassDecl;
import polyglot.ast.Node;
import polyglot.visit.NodeVisitor;
import x10dt.search.core.facts.FactWriterFactory;
import x10dt.search.core.facts.IFactWriter;


final class AllMembersFactWriterVisitor extends NodeVisitor {
  
  AllMembersFactWriterVisitor(final String scopeTypeName) {
    this.fFactWriter = FactWriterFactory.createAllMembersFactWriter(scopeTypeName);
  }
  
  // --- Overridden methods
  
  public NodeVisitor enter(final Node node) {
    if (node instanceof ClassDecl && !node.position().isCompilerGenerated()) {
      this.fFactWriter.writeFacts((ClassDecl) node);
    }
    return this;
  }
  
  // --- Fields
  
  private final IFactWriter fFactWriter;
  
}
