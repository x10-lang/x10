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

import java.util.List;

import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ast.Expr_c;
import polyglot.types.Resolver;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.PrettyPrinter;

import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.errors.Errors;
import polyglot.types.Context;
import polyglot.types.TypeSystem;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint;
import x10.types.constraints.ConstraintManager;
import x10.types.constraints.XConstrainedTerm;


/**
 *
 */
public class Here_c extends Expr_c implements Here {

    public Here_c(Position p) {
        super(p);
    }
    
    /**
     * Return the first (sub)term performed when evaluating this
     * term.
     */
    public Term firstChild() {
        return null;
    }

    /**
     * Visit this term in evaluation order.
     */
    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        return succs;
    }
    
    public String toString() {
    	return "here";
    }

    /*
    protected XConstrainedTerm placeTerm;
    public XConstrainedTerm placeTerm() { return placeTerm; }
    public Here_c placeTerm(XConstrainedTerm pt) {
        if (pt == placeTerm) return this;
        Here_c h = (Here_c) this.copy();
        h.placeTerm = pt;
        return h;
    }
    */

    /** Type check the expression. */
    public Node typeCheck(ContextVisitor tc) {
        TypeSystem ts = (TypeSystem) tc.typeSystem();
        Context xc = (Context) tc.context();

        CConstraint cc = ConstraintManager.getConstraintSystem().makeCConstraint(ts.Place(),ts);

        XConstrainedTerm h = xc.currentPlaceTerm();
        if (h == null) {
        	// [DC] in a class guard, field type, method guard, or in a method but not inside an 'at'
        	// 'here' refers to the home of the object.
        	cc.addSelfEquality(PlaceChecker.globalHere(ts));
        } else {
        	// inside an 'at', using a different 'here' variable (an EQV).
        	cc.addSelfEquality(h.term());
        	cc.addIn(h.constraint());
        }

        return this.type(Types.xclause(ts.Place(), cc));
    }

    /** Write the statement to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
    	w.write(" here ");
    }
}
