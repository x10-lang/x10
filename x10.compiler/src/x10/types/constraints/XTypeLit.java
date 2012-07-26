package x10.types.constraints;

import x10.constraint.XLit;
import polyglot.types.Type;

public interface XTypeLit extends XLit<Type, Type> {
	public Type type();
}
