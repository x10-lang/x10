/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.facts;

import polyglot.ast.ClassDecl;

/**
 * Responsible for populating a set of facts as a result of looking at information contained within a given Polyglot AST node. 
 * 
 * @author egeay
 */
public interface IFactWriter {
  
  /**
   * Writes the set of facts while process an AST class declaration.
   * 
   * @param classDecl The class declaration to process.
   */
  public void writeFacts(final ClassDecl classDecl);

}
