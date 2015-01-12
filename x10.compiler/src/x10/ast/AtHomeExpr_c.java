/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.ast;

import java.util.Collections;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ast.Formal;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.InternalCompilerError;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.PruningVisitor;
import polyglot.visit.ReachChecker;
import x10.constraint.XTerm;
import x10.constraint.XFailure;
import x10.types.ClosureDef;
import x10.types.constraints.XConstrainedTerm;
import x10.types.constraints.CConstraint;
import polyglot.types.Context;

import polyglot.types.TypeSystem;
import x10.types.checker.PlaceChecker;
import x10.errors.Errors;


/**
 * An <code>AtHomeExpr</code> is a representation of the X10 athome construct:
 * <code>athome (vars) { expression }<code>
 */
public class AtHomeExpr_c extends AtExpr_c implements AtHomeExpr {

    protected List<Expr> vars;

    public AtHomeExpr_c(NodeFactory nf, Position p, List<Expr> vars, Block body) {
        this(nf, p, vars, null, body);
    }

    public AtHomeExpr_c(NodeFactory nf, Position p, List<Expr> vars, List<Node> captures, Block body) {
        super(nf, p, null, captures, body);
        this.vars = vars;
    }

    public List<Expr> home() {
        return vars;
    }

    public AtHomeExpr_c home(List<Expr> vars) {
        if (vars == this.vars) return this;
        AtHomeExpr_c n = (AtHomeExpr_c) copy();
        n.vars = vars;
        return n;
    }

    public AtHomeExpr_c captures(List<Node> captures) {
        return (AtHomeExpr_c) super.captures(captures);
    }

    /**
     * Visit the children of the expression.
     * TODO
     */
    public Node visitChildren(NodeVisitor v) {
        AtHomeExpr_c n = (AtHomeExpr_c) super.visitChildren(v);
        List<Expr> vars = visitList(this.vars, v);
        if (n.vars != vars) {
            if (n == this) n = (AtHomeExpr_c) copy();
            n.vars = vars;
        }
        return n;
    }

    // TODO
    @Override
    public Node typeCheckOverride(Node parent, ContextVisitor tc) {
        if (this.place == null) {
            Expr place = computePlace(this, tc);
            assert (place != null);
            return this.place(place).typeCheckOverride(parent, tc);
        }
        return super.typeCheckOverride(parent, tc);
    }

    // TODO
    @Override
    public Node typeCheck(ContextVisitor tc) {
        if (this.place == null) {
            Expr place = computePlace(this, tc);
            assert (place != null);
            return this.place(place).typeCheck(tc);
        }
        return super.typeCheck(tc);
    }

    // TODO
    public static Expr computePlace(Node n, ContextVisitor tc) {
        return (Expr) tc.nodeFactory().Here(n.position().markCompilerGenerated()).typeCheck(tc);
    }

    public String toString() {
        return  "(#" + hashCode() + // todo: using hashCode leads to non-determinism in the output of the compiler
                ") athome[" + returnType + "](" + vars + ") " + body;
    }

    /** Write the expression to an output file. */ // TODO
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        w.write("athome[");
        printBlock(returnType, w, tr);
        w.write("](");
        printSubExpr(place, false, w, tr);
        w.write(") ");
        printBlock(body, w, tr);
    }
}
