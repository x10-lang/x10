/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Jan 23, 2005
 */
package x10.ast;

import java.util.List;

import polyglot.ast.Formal;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;

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
