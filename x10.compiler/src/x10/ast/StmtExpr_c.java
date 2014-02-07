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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Node;
import polyglot.ast.Precedence;
import polyglot.ast.Stmt;
import polyglot.ast.AbstractBlock_c;
import polyglot.ast.Block_c;
import polyglot.ast.Term;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil; import x10.types.constants.ConstantValue;
import x10.util.CollectionFactory;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;

/**
 * A StmtExpr is a sequence of statements followed by an expression, the result.
 * 
 * The result may be null, if the type of the StmtExpr is Void.  This is used to
 * flatten a call to a void method.  Such StmtExpr's can only be used in Eval statements.
 *     - Bowen
 * 
 * @author igor
 *
 */
public class StmtExpr_c extends Expr_c implements StmtExpr {

    protected List<Stmt> statements;
    protected Expr result;

    public StmtExpr_c(Position pos, List<Stmt> statements, Expr result) {
        super(pos);
        assert(statements != null);
        //assert(result != null); // result can be null, if the type is Void  -- Bowen
        this.statements = TypedList.copyAndCheck(statements, Stmt.class, true);
        this.result = result;
    }

    /** Visit the children of the statement expression. */
    public Node visitChildren(NodeVisitor v) {
        List<Stmt> statements = visitList(this.statements, v);
        Expr result = (Expr) visitChild(this.result, v);
        return reconstruct(statements, result);
    }

    /** Type check the expression. */
    public Node typeCheck(ContextVisitor tc) {
        return type(result == null ? tc.typeSystem().Void() : result.type());
    }

    /** Get the result of the statement expression. */
    public Expr result() {
        return this.result;
    }

    /** Set the result of the statement expression. */
    public StmtExpr result(Expr result) {
        if (result == this.result) return this;
        StmtExpr_c n = (StmtExpr_c) copy();
        n.result = result;
        return n;
    }

    /** Get the statements of the statement expression. */
    public List<Stmt> statements() {
        return this.statements;
    }

    /** Set the statements of the statement expression. */
    public Block statements(List<Stmt> statements) {
        StmtExpr_c n = (StmtExpr_c) copy();
        n.statements = TypedList.copyAndCheck(statements, Stmt.class, true);
        return n;
    }

    /** Append a statement to the statement expression (just before the result). */
    public Block append(Stmt stmt) {
        List<Stmt> l = new ArrayList<Stmt>(statements.size()+1);
        l.addAll(statements);
        l.add(stmt);
        return statements(l);
    }

    /** Prepend a statement to the statement expression. */
    public Block prepend(Stmt stmt) {
        List<Stmt> l = new ArrayList<Stmt>(statements.size()+1);
        l.add(stmt);
        l.addAll(statements);
        return statements(l);
    }

    /** Append a list of statements to the statement expression (just before the result). */
    public StmtExpr append(List<Stmt> stmts) {
        List<Stmt> l = new ArrayList<Stmt>(statements.size()+stmts.size());
        l.addAll(statements);
        l.addAll(stmts);
        return (StmtExpr) statements(l);
    }

    /** Prepend a list of statements to the statement expression. */
    public StmtExpr prepend(List<Stmt> stmts) {
        List<Stmt> l = new ArrayList<Stmt>(statements.size()+stmts.size());
        l.addAll(stmts);
        l.addAll(statements);
        return (StmtExpr) statements(l);
    }
    public Context enterScope(Context c) {
        return c.pushBlock();
    }

    /** Reconstruct the statement expression. */
    protected StmtExpr_c reconstruct(List<Stmt> statements, Expr result) {
        if (! CollectionUtil.allEqual(statements, this.statements) || result != this.result) {
            StmtExpr_c n = (StmtExpr_c) copy();
            n.statements = TypedList.copyAndCheck(statements, Stmt.class, true);
            n.result = result;
            return n;
        }
        return this;
    }

    /** Write the expression to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        w.begin(0);
        w.write("({");
        w.unifiedBreak(4, 1, " ", 1);
        w.begin(0);
        for (Stmt n : statements) {
            printBlock(n, w, tr);
            w.newline();
        }
        if (result != null) {
            printSubExpr(result, w, tr);
        }
        w.end();
        w.unifiedBreak(0, 1, " ", 1);
        w.write("})");
        w.end();
    }

    public Term firstChild() {
        return listChild(statements, result);
    }

    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        if (result == null) {
            v.visitCFGList(statements, this, EXIT);
        } else {
            v.visitCFGList(statements, result, ENTRY);
            v.visitCFG(result, this, EXIT);
        }
        return succs;
    }

    public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append("({");

	    int count = 0;

	    for (Iterator<Stmt> i = statements.iterator(); i.hasNext(); ) {
	        if (count++ > 2) {
	            sb.append(" ...");
	            break;
	        }

	        Stmt n = i.next();
	        sb.append(" ");
	        sb.append(n.toString());
	    }

	    if (result != null) {
	        sb.append(" ");
	        sb.append(result.toString());
	    }

	    sb.append(" })");
	    return sb.toString();
	}

    public ConstantValue constantValue() {
        return null;
    }

    public boolean isConstant() {
        // StmtExprs are never constants, because we can't eliminate the Stmts because they may have side-effects!
        return false;
    }

    public Precedence precedence() {
        return Precedence.LITERAL;
    }
}
