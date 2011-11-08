package polyglot.types;



/** Reference to a type object. */
public class Ref_c<T extends TypeObject> extends TypeObject_c implements Ref<T> {
    private static final long serialVersionUID = -794358517607166940L;

    T v;
    
    public Ref_c(T v) {
        super(v.typeSystem(), v.position(), v.errorPosition());
        assert v != null;
        this.v = v;
    }
    
    /** Return true if the reference is not null. */
    public boolean known() {
        return v != null;
    }
    
    public T getCached() {
    	return v;
    }
    
    public T get() {
        return v;
    }
    
    public String toString() {
        if (v == null) return "null";
        return v.toString();
    }

	public void update(T v) {
		this.v = v;
	}
	
	public void when(final Handler<T> h) {
		h.handle(v);
	}
}

