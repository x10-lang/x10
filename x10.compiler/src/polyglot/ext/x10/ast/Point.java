/*
 * Created on Dec 9, 2004
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import java.util.List;


/** An immutable representation of a point in a region.
 * @author vj
 *
 */
public interface Point extends Expr {
	
	/** Returns the rank of the point, i.e. its dimensionality.
	 * 
	 * @return The rank of the point.
	 */
	int rank();
	
	/** Return a new Point whose value is given by this list of expressions.
	 * 
	 * @param l List of expressions for the new point.
	 * @return
	 */
	Point value( List l );

}
