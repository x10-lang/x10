package x10.constraint;

import java.util.List;
/**
 * Representation of an XExpr consisting of a an operator and one or more children 
 * that must be non-null. An XExpr can be used to represented a formula or a field/method 
 * access. A field/method access is represented by an XExpr having a XLabeledOp representing the
 * field/method as the operator and one child for the container: a.f corresponds to (XLabeledOp(f) a). 
 * A method call is represented by the application of the method to the arguments: a.foo(x,y) is 
 * represented as (APPLY (XLabeledOp(foo) a) x y ).
 * 
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
	XOp<T> op();
	
	/**
	 * Returns a non-empty list consisting of the children of this XExpr
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
	/**
	 * Returns the ith child of the expression
	 * @param i
	 * @return
	 */
	XTerm<T> get(int i);
}
