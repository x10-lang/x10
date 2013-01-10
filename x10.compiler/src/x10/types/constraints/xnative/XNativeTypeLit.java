package x10.types.constraints.xnative;

import x10.constraint.XLit;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.xnative.XNativeLit;
import x10.types.constraints.XTypeLit;
import x10.types.matcher.Subst;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;

/**
 * A subclass of XLit that represents a Type literal.
 * @author vijay
 *
 */

public class XNativeTypeLit extends XNativeLit<Type,Type> implements XTypeLit {
	private static final long serialVersionUID = -1222245257474719757L;

	public XNativeTypeLit(Type kind, Type val) {
		super(kind,val);
	}

}
