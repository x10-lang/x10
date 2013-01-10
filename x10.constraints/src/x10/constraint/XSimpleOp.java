package x10.constraint;

import x10.constraint.smt.XPrinter;

/**
 * XSimpleOp is a representation of simple operators i.e. operators that are not
 * parameterized (such as =, && etc.). It is essentially a wrapper around a subset 
 * of the Kind enum values. 
 *  
 * @author lshadare
 *
 */
public class XSimpleOp<T extends XType> extends XOp<T> {

	T type;
	
	/**
	 * Constructs a simple operator with the given kind. Note that the
	 * kind must not be a kind that is parameterized. 
	 * @param kind
	 */
	XSimpleOp(XOp.Kind kind, T type) {
		super(kind);
		this.type = type;
		assert kind!= XOp.Kind.APPLY_LABEL; 
	}

	@Override
	public T type() { return type; }
	
	@Override
	public String toString() {
		return kind.name();
	}

	@Override
	public String prettyPrint() {
		return kind.prettyPrint();
	}

	@Override
	public String print(XPrinter<T> p) {
		return kind.print(p);
	}
	
}
