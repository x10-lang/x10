/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import polyglot.ast.Formal;
import polyglot.util.Position;
import polyglot.util.CodeWriter;

import polyglot.ext.jl.ast.Field_c;


import polyglot.visit.PrettyPrinter;


/** An immutable representation of the X10 statement: ateach (i : D) S
 * @author vj Dec 9, 2004
 * 
 */
public class AtEach_c extends X10Loop_c implements AtEach, Clocked {

        protected List clocks;
	
	/**
	 * @param pos
	 */
	public AtEach_c(Position pos) {
		super(pos);
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
	
         /** Expression */
         public List clocks() {
             return this.clocks;
         }
    
         /** clock */
        public Clocked expr(List clocks) {
            AtEach_c n = (AtEach_c) copy();
            n.clocks = clocks;
            return n;
        }
    
	public Expr getDomain( Expr d ) {
		return new Field_c(position(), d, "distribution");
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
