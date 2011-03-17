package x10.core;

import x10.array.Array;


public class Vec<T> extends x10.core.Struct {

    public int size;
    public x10.array.Array<T> backing;
        
    public Vec<T> clone() {
        return new Vec<T>(T, this);
    }

    private x10.rtt.Type T;
    public static final x10.rtt.RuntimeType<Vec> _RTT = new x10.rtt.NamedType<Vec> (
        "x10.util.Vec", Vec.class, 
        new x10.rtt.RuntimeType.Variance[] { x10.rtt.RuntimeType.Variance.INVARIANT },
        new x10.rtt.Type[] { x10.rtt.Types.ANY, x10.rtt.Types.ANY }
    );
    public x10.rtt.RuntimeType<?> getRTT() { return _RTT; }
    
    public x10.rtt.Type<?> getParam(int i) {if (i==0) return T; return null; }

    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER) {
            java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling");
        }
        oos.defaultWriteObject();
    }

    // zero value constructor
    public Vec(final x10.rtt.Type T, final java.lang.System[] dummy$) {
        this.T = T;
        this.backing = null;
        this.size = 0;
    }
    
    public Vec(final x10.rtt.Type T, final int s) {
        this.T = T;
        this.size = s;
        this.backing = ((x10.array.Array)(new x10.array.Array<T>(T, size)));
    }

    public Vec(final x10.rtt.Type T, Vec<T> other) {
        this.T = T;
        this.size = other.size;
        this.backing = ((x10.array.Array)(new x10.array.Array<T>(T, other.size)));
        for (int i=0 ; i<this.size ; ++i) {
            this.backing.$set_1_$$x10$array$Array_T$G(i, other.backing.$apply$G(i));
        }
    }

    final public static <U>x10.core.Vec<U> make(final x10.rtt.Type U, final int s) {
        return new x10.core.Vec<U>(U, s);
    }

    final public T get(final int i) {
        return ((x10.array.Array<T>)backing).$apply$G((int)(i));
    }
        
    final public T set(final int i, final T v) {
        return ((x10.array.Array<T>)backing).$set_1_$$x10$array$Array_T$G((int)(i), ((T)(v)));
    }
        
    final public int size() { return this.size; }
        
    final native public java.lang.String typeName();
        
    final public java.lang.String toString() {
        return "struct x10.util.Vec: size="+size+" backing="+this.backing;
    }
        
    final public int hashCode() {
        int result = 1;
        result = ((((8191) * (((int)(result))))) + (((int)(((java.lang.Object)(this.size)).hashCode()))));
        result = ((((8191) * (((int)(result))))) + (((int)(((java.lang.Object)(this.backing)).hashCode()))));
        return result;
    }
        
    final public boolean equals(java.lang.Object other) {
        if (!(x10.core.Vec._RTT.instanceof$(other, T))) {
            return false;
        }
        return this.equals_0_$_x10$util$Vec_T_$((x10.core.Vec)x10.rtt.Types.asStruct(new x10.rtt.ParameterizedType(x10.core.Vec._RTT, T), other));
    }

    final public boolean equals_0_$_x10$util$Vec_T_$(x10.core.Vec other) {
        if (((int) this.size) != ((int) ((x10.core.Vec<T>)other).size)) return false;
        for (int i=0 ; i<this.size ; ++i) {
            if (this.backing.$apply$G(i) != other.backing.$apply$G(i)) return false;
        }
        return true;
    }
        
    final public boolean _struct_equals$O(java.lang.Object other) {
        if (!(x10.core.Vec._RTT.instanceof$(other, T))) return false;
        return this._struct_equals_0_$_x10$util$Vec_T_$((x10.core.Vec)(((x10.core.Vec)x10.rtt.Types.asStruct(new x10.rtt.ParameterizedType(x10.core.Vec._RTT, T),other))));
    }
        
    final public boolean _struct_equals_0_$_x10$util$Vec_T_$(x10.core.Vec other) {
        if (((int) this.size) != ((int) ((x10.core.Vec<T>)other).  size)) return false;
        for (int i=0 ; i<this.size ; ++i) {
            if (this.backing.$apply$G(i) != other.backing.$apply$G(i)) return false;
        }
        return true;
    }
        
    final public x10.core.Vec<T> x10$util$Vec$$x10$util$Vec$this() {
        return this;
    }
}
