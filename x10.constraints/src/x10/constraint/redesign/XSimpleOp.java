package x10.constraint.redesign;
/**
 * XSimpleOp is a representation of simple operators i.e. operators that are not
 * parameterized (such as =, && etc.). It is essentially a wrapper around a subset 
 * of the Kind enum values. 
 *  
 * @author lshadare
 *
 */
public class XSimpleOp<T extends XType> extends XOp<T> {
	/**
	 * Constructs a simple operator with the given kind. Note that the
	 * kind must not be a kind that is parameterized. 
	 * @param kind
	 */
	XSimpleOp(XOp.Kind kind) {
		assert kind!= XOp.Kind.APPLY; 
		this.kind = kind; 
	}

	@Override
	public T type() {
		return null; 
	}
}
