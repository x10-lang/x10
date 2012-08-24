package x10.types.constraints;

import x10.constraint.XLit;
import polyglot.types.Type;
/**
 * XTerm representation of a Type. 
 * @author lshadare
 *
 */
public interface XTypeLit extends XLit<Type, Type> {
	public Type type();
}
