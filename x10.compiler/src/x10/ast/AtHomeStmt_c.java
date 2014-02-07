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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Stmt_c;
import polyglot.ast.Term;
import polyglot.types.ClassType;
import polyglot.types.CodeDef;
import polyglot.types.CodeInstance;
import polyglot.types.Context;
import polyglot.types.Def;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import x10.util.CollectionFactory;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.FlowGraph;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.PruningVisitor;
import polyglot.visit.TypeBuilder;
import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.errors.Errors;
import x10.types.AtDef;
import x10.types.ClosureDef;
import x10.types.ParameterType;
import x10.types.ThisDef;
import x10.types.X10ClassDef;
import x10.types.X10MemberDef;
import x10.types.X10MethodDef;
import x10.types.X10ProcedureDef;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.XConstrainedTerm;

/**
 * An <code>AtHomeStmt</code> is a representation of the X10 athome construct:
 * <code>athome (vars) { statement }<code>
 */
public class AtHomeStmt_c extends AtStmt_c implements AtHomeStmt {

    protected List<Expr> vars;

    public AtHomeStmt_c(Position pos, List<Expr> vars, Stmt body) {
        this(pos, vars, null, body);
    }
    public AtHomeStmt_c(Position pos, List<Expr> vars, List<Node> captures, Stmt body) {
        super(pos, null, captures, body);
        this.vars = vars;
    }

    public List<Expr> home() {
        return vars;
    }

    public AtHomeStmt_c home(List<Expr> vars) {
        if (vars == this.vars) return this;
        AtHomeStmt_c n = (AtHomeStmt_c) copy();
        n.vars = vars;
        return n;
    }

    public AtHomeStmt_c body(Stmt body) {
        return (AtHomeStmt_c) super.body(body);
    }

    public AtHomeStmt_c captures(List<Node> captures) {
        return (AtHomeStmt_c) super.captures(captures);
    }

    public AtHomeStmt_c atDef(AtDef ci) {
        return (AtHomeStmt_c) super.atDef(ci);
    }

    // TODO
    @Override
    public Node typeCheckOverride(Node parent, ContextVisitor tc) {
        if (this.place == null) {
            Expr place = AtHomeExpr_c.computePlace(this, tc);
            assert (place != null);
            return this.place(place).typeCheckOverride(parent, tc);
        }
        return super.typeCheckOverride(parent, tc);
    }

    // TODO
    @Override
    public Node typeCheck(ContextVisitor tc) {
        if (this.place == null) {
            Expr place = AtHomeExpr_c.computePlace(this, tc);
            assert (place != null);
            return this.place(place).typeCheck(tc);
        }
        return super.typeCheck(tc);
    }

    /** Visit the children of the statement. */ // TODO
    public Node visitChildren(NodeVisitor v) {
        AtHomeStmt_c n = (AtHomeStmt_c) super.visitChildren(v);
        List<Expr> vars = visitList(this.vars, v);
        if (n.vars != vars) {
            if (n == this) n = (AtHomeStmt_c) copy();
            n.vars = vars;
        }
        return n;
    }

    public String toString() { // TODO
        return "athome (" + vars + ") " + body;
    }

    /** Write the statement to an output file. */ // TODO
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        w.write("athome (");
        printBlock(place, w, tr);
        w.write(") ");
        printSubStmt(body, w, tr);
    }
}

