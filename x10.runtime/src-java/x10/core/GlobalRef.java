/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */
package x10.core;

public class GlobalRef<T> extends x10.core.Struct {
    public static final x10.rtt.RuntimeType<GlobalRef> _RTT = 
        new x10.rtt.RuntimeType<GlobalRef>(GlobalRef.class,
                new x10.rtt.RuntimeType.Variance[] { x10.rtt.RuntimeType.Variance.INVARIANT },
                 new x10.rtt.Type[] {
                     x10.rtt.Types
                     .runtimeType(java.lang.Object.class),
                     x10.rtt.Types
                     .runtimeType(java.lang.Object.class) });

    public x10.rtt.RuntimeType<?> getRTT() {
        return _RTT;
    }

    public x10.rtt.Type<?> getParam(int i) {
        if (i == 0) return T;
        return null;
    }

    private final x10.rtt.Type T;

    final public x10.lang.Place home;

    final public T value;

    public GlobalRef(final x10.rtt.Type T, final T t) {
        this.T = T;
        this.home = x10.lang.Runtime.here();
        this.value = ((T) (t));
    }

    final public T apply$G() {
        return value;
    }

    final public x10.lang.Place home() {
        return this.home;
    }

    final native public java.lang.String typeName();

    final public java.lang.String toString() {
        return (((((((("struct x10.core.GlobalRef:") + (" home="))) + (this.home))) + (" it="))) + (this.value));
    }

    final public int hashCode() {
        return (31 * this.home.hashCode()) + System.identityHashCode(this.value); // this.value.hashCode();
    }

    final public boolean equals(java.lang.Object other) {
        if ((!(((boolean) (x10.core.GlobalRef._RTT.instanceof$(other, T)))))) {
            return false;
        }

        return this.equals(((x10.core.GlobalRef) ((new java.lang.Object() {
            final x10.core.GlobalRef cast(final x10.core.GlobalRef self) {
                if (self == null) return null;
                x10.rtt.Type rtt = new x10.rtt.ParameterizedType(x10.core.GlobalRef._RTT, T);
                if (rtt != null && !rtt.instanceof$(self)) throw new java.lang.ClassCastException();
                return self;
            }
        }.cast((x10.core.GlobalRef) other)))));
    }

    final public boolean equals(x10.core.GlobalRef<T> other) {
        return x10.rtt.Equality.equalsequals(this.home, other.home)
                && x10.rtt.Equality.equalsequals(this.value, ((T) (other.value)));
    }

    final public boolean _struct_equals(java.lang.Object other) {
        if ((!(((boolean) (x10.core.GlobalRef._RTT.instanceof$(other, T)))))) {
            return false;
        }

        return this._struct_equals(((x10.core.GlobalRef) ((new java.lang.Object() {
            final x10.core.GlobalRef cast(final x10.core.GlobalRef self) {
                if (self == null) return null;
                x10.rtt.Type rtt = new x10.rtt.ParameterizedType(x10.core.GlobalRef._RTT, T);
                if (rtt != null && !rtt.instanceof$(self)) throw new java.lang.ClassCastException();
                return self;
            }
        }.cast((x10.core.GlobalRef) other)))));
    }

    final public boolean _struct_equals(x10.core.GlobalRef<T> other) {
        return x10.rtt.Equality.equalsequals(this.home, other.home)
                && x10.rtt.Equality.equalsequals(this.value, ((T) (other.value)));
    }

}
