/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Dec 20, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.CompoundStmt;
import polyglot.ast.Stmt;

/**
 * @author vj Dec 20, 2004
 * 
 */
public interface Finish extends CompoundStmt {
	Stmt body();

}
