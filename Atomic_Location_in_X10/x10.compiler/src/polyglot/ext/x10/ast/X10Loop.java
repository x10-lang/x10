/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.CompoundStmt;
import polyglot.ast.Stmt;
import polyglot.ast.Expr;
import polyglot.ast.Formal;

/**
 * @author vj Dec 9, 2004
 * 
 */
public interface X10Loop extends CompoundStmt {
	Stmt body();
	Formal formal();
	Expr domain();

}