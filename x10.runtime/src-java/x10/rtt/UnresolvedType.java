package x10.rtt;


public final class UnresolvedType implements Type {

	private static final long serialVersionUID = 1L;

	public static final UnresolvedType THIS = new UnresolvedType(-1);
	private static final UnresolvedType[] params = {
	    new UnresolvedType(0),
	    new UnresolvedType(1),
	    new UnresolvedType(2),
	    new UnresolvedType(3),
        new UnresolvedType(4),
        new UnresolvedType(5),
        new UnresolvedType(6),
        new UnresolvedType(7),
        new UnresolvedType(8),
        new UnresolvedType(9),
	};

    private final int index;
    
    @Deprecated
    public static UnresolvedType getParam(int index) {
    	return PARAM(index);
    }
    public static UnresolvedType PARAM(int index) {
        assert index >= 0;
        if (index < params.length) {
            return params[index];
        }
        return new UnresolvedType(index);
    }
    
    private UnresolvedType(int index) {
        this.index = index;
    }

    public String toString() {
        return "UnresolvedType(" + index + ")";
    }
    
    public final int getIndex() {
    	return index;
    }
    
    public final int arrayLength(Object array) {
        throw new UnsupportedOperationException();
    }

    public final Object getArray(Object array, int i) {
        throw new UnsupportedOperationException();
    }

    public final Class<?> getImpl() {
        throw new UnsupportedOperationException();
    }

    public final boolean instanceof$(Object o) {
        throw new UnsupportedOperationException();
    }

    public final boolean isSubtype(Type o) {
        throw new UnsupportedOperationException();
    }

    public final Object makeArray(int length) {
        throw new UnsupportedOperationException();
    }

    public Object makeArray(Object... elems) {
        throw new UnsupportedOperationException();
    }

//    public final Object setArray(Object array, int i, Object v) {
//        throw new UnsupportedOperationException();
//    }
    public final void setArray(Object array, int i, Object v) {
        throw new UnsupportedOperationException();
    }

    public final String typeName() {
        return toString();
    }

}
