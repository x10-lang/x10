/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import java.util.List;

/** An immutable representation of the X10 construct [range1,...,rangen] 
 * representing an n-ary region. (We may choose to represent this instead as
 * new region(range1, ..., rangen);)
 * @author vj Dec 9, 2004
 * 
 */
public interface Region extends Expr {
	int rank();
	Expr index(int i);
	List ranges();
	
	// Do we really need updaters?

}
