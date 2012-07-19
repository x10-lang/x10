package x10.constraint.redesign;
/**
 * A representation of an XExpr operator parameterized by the label. It is used
 * to represent field/method projection (a.f, a.foo()) when applied to a receiver 
 * as part of an XExpr. The specific field/method information is stored in def. 
 *  
 * @author lshadare
 *
 * @param <D>
 */
public class XLabeledOp<T extends XType, D extends XDef<T>> extends XOp<T> {
	D def; 
	
	XLabeledOp(D def) {
		this.def = def; 
		this.kind = XOp.Kind.APPLY;
	}
	
	public D getLabel() {
		return def; 
	}

	@Override
	public T type() {
		return def.resultType();
	}
}
