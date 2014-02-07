/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.ast;

import java.util.List;

import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import x10.types.X10LocalDef;

/**
 * @author vj Jan 23, 2005
 */
public interface X10Formal extends Formal, X10VarDecl {
   boolean isUnnamed();
   boolean hasExplodedVars();
   
   List<Formal> vars();
   X10Formal vars(List<Formal> vars);

   /** Get the local instances of the bound variables. */
   public LocalDef[] localInstances();

   public X10Formal flags(FlagsNode fn);
   public X10Formal type(TypeNode type);
   public X10Formal name(Id name);
   public X10Formal localDef(LocalDef li);
   public X10LocalDef localDef();

   /**
    * Return a list of statements containing the initializations for the exploded vars,
    * with s appended.
    * 
    * @param s -- the list of statements to be appended.
    * @return
    */
   List<Stmt> explode(ContextVisitor tc, List<Stmt> s, boolean prepend) throws SemanticException;
   List<Stmt> explode(ContextVisitor tc, Stmt s) throws SemanticException;
   List<Stmt> explode(ContextVisitor tc) throws SemanticException;
}
