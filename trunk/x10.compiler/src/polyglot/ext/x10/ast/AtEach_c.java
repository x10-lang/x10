/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import polyglot.ast.Formal;
import polyglot.util.Position;
import polyglot.ast.Block;
import polyglot.ast.Stmt;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.Stmt_c;
import polyglot.util.Position;
import polyglot.util.CodeWriter;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.types.Type;

/** An immutable representation of the X10 statement: ateach (i : D) S
 * @author vj Dec 9, 2004
 * 
 */
public class AtEach_c extends X10Loop_c implements AtEach {
	
	/**
	 * @param pos
	 */
	public AtEach_c(Position pos) {
		super(pos);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param pos
	 * @param formal
	 * @param domain
	 * @param body
	 */
	public AtEach_c(Position pos, Formal formal, Expr domain, Stmt body) {
		super(pos, formal, domain, body);
	}
	
	public AtEach body(Stmt body) {
		AtEach_c n = (AtEach_c) copy();
		n.body = body;
		return n;
	}
	
	public AtEach formal(Formal formal) {
		AtEach_c n = (AtEach_c) copy();
		n.formal = formal;
		return n;
	}
	
	public AtEach domain(Expr domain) {
		AtEach_c n = (AtEach_c) copy();
		n.domain = domain;
		return n;
	}
	
	
	public String toString() {
		return "ateach (" + formal + ":" + domain + ")" + body;
	}
	
	public void prettyPrint( CodeWriter w, PrettyPrinter tr ) {
		w.write("ateach(");
		printBlock(formal, w, tr);
		w.write(" : ");
		printBlock(domain, w, tr);
		w.write(") ");
		printSubStmt(body, w, tr);
	}
	
	
}
