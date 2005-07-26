/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Formal;
import polyglot.ext.jl.ast.Field_c;
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

/** An immutable representation of the X10 statement: foreach (i : D) S 
 * @author vj Dec 9, 2004
 * @author Christian Grothoff
 */
public class ForEach_c extends X10Loop_c implements ForEach, Clocked {
	
    protected List clocks;
        
	/**
	 * @param pos
	 */
	public ForEach_c(Position pos) {
		super(pos);
	}
	
	/**
	 * @param pos
	 * @param formal
	 * @param domain
	 * @param body
	 */
	public ForEach_c(Position pos, Formal formal, Expr domain, List clocks, Stmt body) {
		super(pos, formal, domain, body);
		this.clocks = clocks;
	}
	
    /** Expression */
    public List clocks() {
        return this.clocks;
    }
    
    /** clock */
    public Clocked expr(List clocks) {
        ForEach_c n = (ForEach_c) copy();
        n.clocks = clocks;
        return n;
    }
    
	
	public String toString() {
		return "foreach (" + formal + ":" + domain + ")" + body;
	}
	
	public void prettyPrint( CodeWriter w, PrettyPrinter tr ) {
		w.write("foreach(");
		printBlock(formal, w, tr);
		w.write(" : ");
		printBlock(domain, w, tr);
		w.write(") ");
		printSubStmt(body, w, tr);
	}
	public Node visitChildren( NodeVisitor v) {
		Formal formal = (Formal) visitChild(this.formal, v);
		Expr domain = (Expr) visitChild(this.domain, v);
		Stmt body = (Stmt) visitChild(this.body, v);
		return reconstruct( formal, domain, body);
	}
}
