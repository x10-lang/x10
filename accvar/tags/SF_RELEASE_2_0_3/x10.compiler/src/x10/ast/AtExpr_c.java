/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.ast;

import java.util.Collections;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.PruningVisitor;
import polyglot.visit.ReachChecker;
import x10.constraint.XConstraint_c;
import x10.constraint.XTerm;
import x10.types.ClosureDef;
import x10.types.X10Context;
import x10.types.X10NamedType;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.checker.PlaceChecker;
import x10.visit.ExprFlattener;
import x10.visit.ExprFlattener.Flattener;


/** A <code>AtExp </code> is a representation of the X10 at construct:
 * <code>at (place) { expression }<code>
 * stmts are used to represent the fully exploded version of the expression
 * as might be needed in order to inline array expressions.
 * @author ??
 * @author vj 08/30/09 - Refactored out PlacedClosure.
 */
public class AtExpr_c extends PlacedClosure_c
    implements AtExpr {
    
    public AtExpr_c(Position p, Expr place, TypeNode returnType, Block body) {
	    super(p, place, returnType, body);
    }

    public Node typeCheck( ContextVisitor tc ) throws SemanticException {
    	AtExpr_c n = (AtExpr_c) super.typeCheck(tc);
    	Type t = n.returnType().type();
    	Context childC = enterChildScope(body, tc.context());
    	t = PlaceChecker.ReplaceHereByPlaceTerm(t, (X10Context) childC);
    	return n.type(t);
    }
    
    public String toString() {
    	return  "(#" + hashCode() + ") at[" + returnType + "](" + place + ") " + body;
    }
   
    /** Write the expression to an output file. */

    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
    	w.write("at[");
    	printBlock(returnType, w, tr);
    	w.write("](");
    	printSubExpr(place, false, w, tr);
    	w.write(") ");
    	printBlock(body, w, tr);
    }

  
        
}
