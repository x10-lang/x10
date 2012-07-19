package x10.constraint.redesign;

import java.util.List;
/**
 * Representation of an XExpr consisting of a an operator and one or more children 
 * that must be non-null. An XExpr can be used to represented a formula or field/method 
 * projection. In the case of projection the field/method information is encoded in the 
 * operator. Thus field projection will only have one child, the target: a.f would be 
 * encoded as (XLabeledOp(f) a).  
 * 
  * @author lshadare
 *
 * @param <T> type of XTerm
 */
public interface XExpr<T extends XType> extends XTerm<T> {
	
	/**
	 * Returns the operator of this XExpr
	 * @return
	 */
	XOp op();
	
	/**
	 * Returns a non-empty list consisting of the children of this expr
	 * @return
	 */
	List<? extends XTerm<T>> children();
	
	/**
	 * Returns true if the current expression is an atom (i.e. it is a 
	 * Boolean expression)
	 * 
	 * @return
	 */
	boolean isAtom(); 
	
	/**
	 * Returns true if the expression represents a compiler generated hidden 
	 * expr (such as self.here) 
	 * @return
	 */
	boolean isHidden(); 
}
