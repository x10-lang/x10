/*
 *
 */
package polyglot.ext.x10.ast;

import java.util.List;
import java.util.ArrayList;

import polyglot.ast.Stmt;
import polyglot.ast.Expr;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.Stmt_c;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.CFGBuilder;

/**
 *
 */
public class When_c extends Stmt_c implements When {

    public List exprs; 

    public List statements;
    
    public When_c(Position p, List exprs, List statements) {
        super(p);
        this.exprs = TypedList.copyAndCheck(exprs, Expr.class, true);
	this.statements = TypedList.copyAndCheck(statements, Stmt.class, true);
    }

    /** Get the statements of the block. */
    public List statements() {
	return this.statements;
    }

    /** Set the statements of the block. */
    public When statements(List statements) {
	When_c n = (When_c) copy();
	n.statements = TypedList.copyAndCheck(statements, Stmt.class, true);
	return n;
    }

    /** Get the exprs of the block. */
    public List exprs() {
	return this.exprs;
    }

    /** Set the exprs of the block. */
    public When exprs(List exprs) {
	When_c n = (When_c) copy();
	n.exprs = TypedList.copyAndCheck(exprs, Expr.class, true);
	return n;
    }

    /** Append a statement to the statement list. */
    public When append(Stmt stmt) {
	List l = new ArrayList(statements.size()+1);
	l.addAll(statements);
	l.add(stmt);
	return statements(l);
    }

    /** Append a expr to the expr list. */
    public When append(Expr expr) {
	List l = new ArrayList(exprs.size()+1);
	l.addAll(exprs);
	l.add(expr);
	return exprs(l);
    }

    public Term entry() {
        // TODO:
        return this;
    }

    public List acceptCFG(CFGBuilder v, List succs) {
        v.visitCFGList(statements, this);
        return succs;
    }
}
