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
 * 
 *  E.g. class A { class B { def m() { A.this } } is represented with var()==this and qualifier()==A
 * @author lshadare
 *
 */
public interface CQualifiedVar extends XField<Type, QualifierDef> {
	/**
	 * Return the variable itself. 
	 * @return
	 */
	//XVar<Type> var();
}
