/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import polyglot.ast.Variable;
import polyglot.util.Position;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import polyglot.ast.Formal;
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


/** An immutable representation of an X10 for loop: for (i : D) S
 * @author vj Dec 9, 2004
 * 
 */

public class ForLoop_c extends X10Loop_c {

	/**
	 * @param pos
	 */
	public ForLoop_c(Position pos) {
		super(pos);
		
	}

	/**
	 * @param pos
	 * @param formal
	 * @param domain
	 * @param body
	 */
	public ForLoop_c(Position pos, Formal formal, Expr domain, Stmt body) {
		super(pos, formal, domain, body);
		
	}

}
