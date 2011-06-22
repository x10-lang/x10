/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

package x10.core;

import x10.rtt.NamedType;
import x10.rtt.ParameterizedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.Types;

public class Vec<T> extends x10.core.Struct {

    public int size;
    public x10.array.Array<T> backing;

    public Vec<T> clone() {
        return new Vec<T>(T, this);
    }

    private Type<T> T;
    public static final RuntimeType<Vec<?>> $RTT = new NamedType<Vec<?>>(
        "x10.util.Vec",
        Vec.class,
        new RuntimeType.Variance[] { RuntimeType.Variance.INVARIANT },
        new Type[] { Types.STRUCT }
    );

    @Override
    public RuntimeType<Vec<?>> $getRTT() {
        return $RTT;
    }

    @Override
    public Type<?> $getParam(int i) {
        if (i == 0) return T;
        return null;
    }

    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER) {
            java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling");
        }
        oos.defaultWriteObject();
    }
    
    public Vec(java.lang.System[] $dummy) {
        super($dummy);
    }

    public Vec $init(final Type<T> T, final int s) {
        this.T = T;
        this.size = s;
        this.backing = x10.array.Array.<T> $make(T, size);
        return this;
    }

    public Vec(final Type<T> T, final int s) {
        this.T = T;
        this.size = s;
        this.backing = x10.array.Array.<T> $make(T, size);
    }

    public Vec $init(final Type<T> T, Vec<T> other) {
        this.T = T;
        this.size = other.size;
        this.backing = x10.array.Array.<T> $make(T, other.size);
        for (int i = 0; i < this.size; ++i) {
            this.backing.$set_1_$$x10$array$Array_T$G(i, other.backing.$apply$G(i));
        }
        return this;
    }

    public Vec(final Type<T> T, Vec<T> other) {
        this.T = T;
        this.size = other.size;
        this.backing = x10.array.Array.<T> $make(T, other.size);
        for (int i = 0; i < this.size; ++i) {
            this.backing.$set_1_$$x10$array$Array_T$G(i, other.backing.$apply$G(i));
        }
    }

    // zero value constructor
    public Vec(final Type<T> T, final java.lang.System $dummy) {
        this.T = T;
        this.backing = null;
        this.size = 0;
    }

    final public static <U> Vec<U> make(final Type U, final int s) {
        return new Vec<U>(U, s);
    }

    final public T get(final int i) {
        return backing.$apply$G(i);
    }

    final public T set(final int i, final T v) {
        return backing.$set_1_$$x10$array$Array_T$G(i, v);
    }

    final public int size() {
        return this.size;
    }

    final native public java.lang.String typeName();

    final public java.lang.String toString() {
        return "struct x10.util.Vec: size=" + size + " backing=" + this.backing;
    }

    final public int hashCode() {
        int result = 1;
        result = 8191 * result + ((java.lang.Object) this.size).hashCode();
        result = 8191 * result + this.backing.hashCode();
        return result;
    }

    final public boolean equals(java.lang.Object other) {
        if (!Vec.$RTT.instanceOf(other, T)) {
            return false;
        }
        return this.equals_0_$_x10$util$Vec_T_$((Vec) Types.asStruct(new ParameterizedType(Vec.$RTT, T), other));
    }

    final public boolean equals_0_$_x10$util$Vec_T_$(Vec other) {
        if (this.size != other.size) return false;
        for (int i = 0; i < this.size; ++i) {
            if (this.backing.$apply$G(i) != other.backing.$apply$G(i)) return false;
        }
        return true;
    }

    final public boolean _struct_equals$O(java.lang.Object other) {
        if (!Vec.$RTT.instanceOf(other, T)) return false;
        return this._struct_equals_0_$_x10$util$Vec_T_$((Vec) Types.asStruct(new ParameterizedType(Vec.$RTT, T), other));
    }

    final public boolean _struct_equals_0_$_x10$util$Vec_T_$(Vec other) {
        if (this.size != other.size) return false;
        for (int i = 0; i < this.size; ++i) {
            if (this.backing.$apply$G(i) != other.backing.$apply$G(i)) return false;
        }
        return true;
    }

    final public Vec<T> x10$util$Vec$$x10$util$Vec$this() {
        return this;
    }
}
