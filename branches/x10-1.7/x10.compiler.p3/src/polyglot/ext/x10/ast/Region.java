/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;

/** An immutable representation of the X10 construct [range1,...,rangen] 
 * representing an n-ary region. (We may choose to represent this instead as
 * new region(range1, ..., rangen);)
 * @author vj Dec 9, 2004
 * 
 */
public interface Region extends Expr {
	int rank();
	Expr index(int i);
	List<Expr> ranges();
	
	// Do we really need updaters?

}
