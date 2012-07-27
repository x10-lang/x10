package x10.types.constraints;

import polyglot.types.Type;
import x10.constraint.XExpr;
import x10.constraint.XField;
import x10.constraint.XTerm;
import x10.constraint.XVar;
/**
 * A qualified variable is a variable used to refer to the outer scopes
 * in nested classes. The qualifier is the type of the outer class. Note 
 * that the type of the variable is not necessarily the type of the qualifier. 
 * @author lshadare
 *
 */
public interface CQualifiedVar extends XField<Type, Type> {
	/**
	 * Return the qualifier of the variable i.e. the type corresponding to
	 * the outer class.
	 * @return
	 */
	Type qualifier(); 
	/**
	 * Return the variable itself. 
	 * @return
	 */
	XVar<Type> var();
}
