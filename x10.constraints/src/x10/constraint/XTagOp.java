package x10.constraint;

public class XTagOp<T extends XType, V> extends XOp<T>  {
	final V tag;
	final T type; 
	
	XTagOp(V tag, T type) {
		super(XOp.Kind.TAG);
		assert tag!= null && type != null; 
		this.tag = tag; 
		this.type = type; 
	}

	@Override
	public T type(XTypeSystem<? extends T> ts) {
		return type; 
	}

	@Override
	public T type() {
		return type; 
	}
	
	public V tag() {
		return tag; 
	}

	@Override
	public String toString() {
		return tag.toString();   
	}

}
