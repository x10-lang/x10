package x10.rtt;

import java.util.List;

import x10.constraint.XConstraint;
import x10.core.fun.Fun_0_1;
import x10.core.fun.Fun_0_2;

public final class UnresolvedType implements Type {

    int index = -1;
    
    public UnresolvedType(int index) {
        this.index = index;
    }
    
    public final Fun_0_1 absOperator() {
        throw new UnsupportedOperationException();
    }

    public final Fun_0_2 addOperator() {
        throw new UnsupportedOperationException();
    }

    public final Fun_0_2 andOperator() {
        throw new UnsupportedOperationException();
    }

    public final int arrayLength(Object array) {
        throw new UnsupportedOperationException();
    }

    public final Fun_0_2 divOperator() {
        throw new UnsupportedOperationException();
    }

    public final Object getArray(Object array, int i) {
        throw new UnsupportedOperationException();
    }

    public final XConstraint getConstraint() {
        throw new UnsupportedOperationException();
    }

    public final Class getJavaClass() {
        throw new UnsupportedOperationException();
    }

    public final List getTypeParameters() {
        throw new UnsupportedOperationException();
    }

    public final boolean instanceof$(Object o) {
        throw new UnsupportedOperationException();
    }

    public final Fun_0_1 invOperator() {
        throw new UnsupportedOperationException();
    }

    public final boolean isSubtype(Type o) {
        throw new UnsupportedOperationException();
    }

    public final Object makeArray(int length) {
        throw new UnsupportedOperationException();
    }

    public final Fun_0_2 maxOperator() {
        throw new UnsupportedOperationException();
    }

    public final Object maxValue() {
        throw new UnsupportedOperationException();
    }

    public final Fun_0_2 minOperator() {
        throw new UnsupportedOperationException();
    }

    public final Object minValue() {
        throw new UnsupportedOperationException();
    }

    public final Fun_0_2 modOperator() {
        throw new UnsupportedOperationException();
    }

    public final Fun_0_2 mulOperator() {
        throw new UnsupportedOperationException();
    }

    public final Fun_0_1 negOperator() {
        throw new UnsupportedOperationException();
    }

    public final Fun_0_1 notOperator() {
        throw new UnsupportedOperationException();
    }

    public final Fun_0_2 orOperator() {
        throw new UnsupportedOperationException();
    }

    public final Fun_0_1 posOperator() {
        throw new UnsupportedOperationException();
    }

    public final Fun_0_1 scaleOperator(int k) {
        throw new UnsupportedOperationException();
    }

    public final Object setArray(Object array, int i, Object v) {
        throw new UnsupportedOperationException();
    }

    public final Fun_0_2 subOperator() {
        throw new UnsupportedOperationException();
    }

    public final Object unitValue() {
        throw new UnsupportedOperationException();
    }

    public final Fun_0_2 xorOperator() {
        throw new UnsupportedOperationException();
    }

    public final Object zeroValue() {
        throw new UnsupportedOperationException();
    }
    
    public final String typeName() {
        throw new UnsupportedOperationException();
    }
}
