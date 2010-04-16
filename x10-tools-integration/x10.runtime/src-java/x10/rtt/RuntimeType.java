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

package x10.rtt;

import java.util.List;

import x10.constraint.XConstraint;
import x10.core.Any;
import x10.core.fun.Fun_0_1;
import x10.core.fun.Fun_0_2;

public class RuntimeType<T> implements Type<T> {

    public enum Variance {INVARIANT, COVARIANT, CONTRAVARIANT}
    
    Type<?>[] parents;
    Class<?> base;
    Variance[] variances = null;
    
    public RuntimeType(Class<?> c) {
        this.base = c;
    }
    
    
    public RuntimeType(Class<?> c, Variance... variances) {
        this.base = c;
        this.variances = variances;
    }

    public RuntimeType(Class<?>c, Type<?>[] parents) {
        this.base = c;
        this.parents = parents;
    }
    
    public RuntimeType(Class<?> c, Variance[] variances, Type<?>[] parents) {
        this.base = c;
        this.variances = variances;
        this.parents = parents;
    }
    
    public String toString() {
    	return base.getName();
    }
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof RuntimeType) {
            RuntimeType<?> rt = (RuntimeType<?>) o;
            if (base.equals(rt.base)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isSubtype(Type<?> o) {
        if (this == o) return true;
        if (o instanceof RuntimeType) {
            RuntimeType<?> rt = (RuntimeType<?>) o;
            if (rt.base.isAssignableFrom(base)) {
                return true;
            }
        }
        return false;
    }
    
    public XConstraint getConstraint() {
        return null;
    }
    
    public List<Type<?>> getTypeParameters() {
        return null;
    }

    public Type<T> reinstantiate(List<Type<?>> parms) {
        return this;
    }

    public boolean instanceof$(Object o) {
        if (o == null) {return false;}
        if (o.getClass() == base) {
            return true;
        }
        return base.isInstance(o);
    }

    // o instanceof This and params
    public final boolean instanceof$(Object o, Type<?>... params) {
        if (o == null) {return false;}
        if (o.getClass() == base) {
            Any any = (x10.core.Any) o;
            for (int i = 0, s = params.length; i < s; i ++) {
                switch (variances[i]) {
                case INVARIANT:
                    if (!params[i].equals(any.getParam(i))) {return false;}
                    break;
                case COVARIANT:
                    if (!any.getParam(i).isSubtype(params[i])) {return false;}
                    break;
                case CONTRAVARIANT:
                    if (!params[i].isSubtype(any.getParam(i))) {return false;}
                    break;
                }
            }
            return true;
        }
        else if (base.isInstance(o)) { // i.e. type of o != This
            return checkParents(o, params);
        }
        else {
            return false;
        }
    }

    private final boolean checkParents(Object o, Type<?>... params) {
        if (o instanceof x10.core.Any) {
            Any any = (x10.core.Any) o;
            RuntimeType<?> rtt = any.getRTT(); // o._RTT
            if (rtt == null) {
                return true;
            }
            return instantiateCheck(params, rtt, any);
        }
        return false;
    }

    // e.g. C[T1,T2]:Super[Int, T1] -> C[Int,Double]:Super[Int,Int] 
    private final boolean instantiateCheck(Type<?>[] params, RuntimeType<?> rtt, Any any) {
        for (Type<?> t : rtt.parents) {
            if (base.isAssignableFrom(t.getJavaClass())) {
                if (t instanceof ParameterizedType<?>) {
                    ParameterizedType<?> pt = (ParameterizedType<?>) t;
                    Type<?>[] paramsT = pt.getParams();
                    Type<?>[] newParamsT = new Type<?>[paramsT.length];
                    for (int i = 0; i < paramsT.length; i ++ ) {
                        if (paramsT[i] != null && paramsT[i] instanceof UnresolvedType) {
                            newParamsT[i] = any.getParam(((UnresolvedType) paramsT[i]).index);
                        }
                        else {
                            newParamsT[i] = paramsT[i];
                        }
                    }
                    if (subtypeof(params, pt.getRuntimeType(), newParamsT)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    // e.g. C[T1,T2]:Super[Int, T1] -> C[Int,Double]:Super[Int,Int] 
    private final boolean instantiateCheck(Type<?>[] params, RuntimeType<?> rtt, Type<?>[] paramsRTT) {
        for (Type<?> t : rtt.parents) {
            if (base.isAssignableFrom(t.getJavaClass())) {
                if (t instanceof ParameterizedType<?>) {
                    ParameterizedType<?> pt = (ParameterizedType<?>) t;
                    Type<?>[] paramsT = pt.getParams();
                    Type<?>[] newParamsT = new Type<?>[paramsT.length];
                    for (int i = 0; i < paramsT.length; i ++ ) {
                        if (paramsT[i] != null && paramsT[i] instanceof UnresolvedType) {
                            newParamsT[i] = paramsRTT[((UnresolvedType) paramsT[i]).index];
                        }
                        else {
                            newParamsT[i] = paramsT[i];
                        }
                    }
                    if (subtypeof(params, pt.getRuntimeType(), newParamsT)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    // check "type and paramsType" <: "this and params"
    private final boolean subtypeof(Type<?>[] params, RuntimeType<?> rtt, Type<?>[] paramsType) {
        if (base == rtt.getJavaClass()) {
            for (int i = 0, s = params.length; i < s; i ++) {
                switch (variances[i]) {
                case INVARIANT:
                    if (!params[i].equals(paramsType[i])) {return false;}
                    break;
                case COVARIANT:
                    if (!paramsType[i].isSubtype(params[i])) {return false;}
                    break;
                case CONTRAVARIANT:
                    if (!params[i].isSubtype(paramsType[i])) {return false;}
                    break;
                }
            }
            return true;
        }
        else if (base.isAssignableFrom(rtt.getJavaClass())) {
            return instantiateCheck(params, rtt, paramsType);
        }
        else {
            return false;
        }
    }
    
    public T minValue() {
        throw new UnsupportedOperationException();
    }
    
    public T maxValue() {
        throw new UnsupportedOperationException();
    }

    public T zeroValue() {
        // null for ref types, otherwise complain
       /* if (!x10.runtime.impl.java.Configuration.NULL_DEFAULT_VALUE 
              && Value.class.isAssignableFrom(c)) {
            throw new UnsupportedOperationException();
        }*/
        return null;
    }
    
    public T unitValue() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_1<T, T> absOperator() {
        throw new UnsupportedOperationException();
    }
    
    public Fun_0_1<T, T> scaleOperator(int k) {
        throw new UnsupportedOperationException();
    }

    public Fun_0_2<T, T, T> addOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_2<T, T, T> andOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_2<T, T, T> divOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_2<T, T, T> maxOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_2<T, T, T> minOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_2<T, T, T> modOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_2<T, T, T> mulOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_1<T, T> negOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_1<T, T> notOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_2<T, T, T> orOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_2<T, T, T> subOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_2<T, T, T> xorOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_1<T, T> invOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_1<T, T> posOperator() {
        throw new UnsupportedOperationException();
    }

    public Class<?> getJavaClass() {
        return base;
    }
    
    public Object makeArray(int length) {
        return new Object[length];
    }

    public T getArray(Object array, int i) {
        return (T) ((Object[]) array)[i];
    }

    public T setArray(Object array, int i, T v) {
        ((Object[]) array)[i] = v;
        return v;
    }
    
    public int arrayLength(Object array) {
        	return ((Object[]) array).length;
    }

    public String typeName() {
        return base.toString().substring(6);
    }

    public String typeName(Object o) {
        String str = base.toString().substring(6);
        if (variances != null && variances.length > 0) {
            if (o instanceof Any) {
                str += "[";
                for (int i = 0; i < variances.length; i ++) {
                    if (i != 0) str += ",";
                    str += ((Any) o).getParam(i).typeName();
                }
                str += "]";
            }
        }
        return str;
    }
    
    // for shortcut
    public final boolean instanceof$(Object o, Type<?> param0) {
        if (o == null) {return false;}
        if (o.getClass() == base) {
            Any any = (x10.core.Any) o;
            if (variances[0].equals(Variance.INVARIANT)) {
                if (!param0.equals(any.getParam(0))) {return false;}
            }
            else if(variances[0].equals(Variance.COVARIANT)) {
                if (!any.getParam(0).isSubtype(param0)) {return false;}
            }
            else if(variances[0].equals(Variance.CONTRAVARIANT)) {
                if (!param0.isSubtype(any.getParam(0))) {return false;}
            }
            return true;
        }
        else if (base.isInstance(o)) {
            return checkParents(o, param0);
        }
        else {
            return false;
        }
    }


    // for shortcut
    public final boolean instanceof$(Object o, Type<?> param0, Type<?> param1) {
        if (o == null) {return false;}
        if (o.getClass() == base) {
            Any any = (x10.core.Any) o;
            if (variances[0].equals(Variance.INVARIANT)) {
                if (!param0.equals(any.getParam(0))) {return false;}
            }
            else if(variances[0].equals(Variance.COVARIANT)) {
                if (!any.getParam(0).isSubtype(param0)) {return false;}
            }
            else if(variances[0].equals(Variance.CONTRAVARIANT)) {
                if (!param0.isSubtype(any.getParam(0))) {return false;}
            }
            if (variances[1].equals(Variance.INVARIANT)) {
                if (!param1.equals(any.getParam(1))) {return false;}
            }
            else if(variances[1].equals(Variance.COVARIANT)) {
                if (!any.getParam(1).isSubtype(param1)) {return false;}
            }
            else if(variances[1].equals(Variance.CONTRAVARIANT)) {
                if (!param1.isSubtype(any.getParam(1))) {return false;}
            }
            return true;
        }
        else if (base.isInstance(o)) {
            return checkParents(o, param0, param1);
        }
        else {
            return false;
        }
        
    }


    // for shortcut 
    public final boolean instanceof$(Object o, Type<?> param0, Type<?> param1, Type<?> param2) {
        if (o == null) {return false;}
        if (o.getClass() == base) {
            Any any = (x10.core.Any) o;
            if (variances[0].equals(Variance.INVARIANT)) {
                if (!param0.equals(any.getParam(0))) {return false;}
            }
            else if(variances[0].equals(Variance.COVARIANT)) {
                if (!any.getParam(0).isSubtype(param0)) {return false;}
            }
            else if(variances[0].equals(Variance.CONTRAVARIANT)) {
                if (!param0.isSubtype(any.getParam(0))) {return false;}
            }
            
            if (variances[1].equals(Variance.INVARIANT)) {
                if (!param1.equals(any.getParam(1))) {return false;}
            }
            else if(variances[1].equals(Variance.COVARIANT)) {
                if (!any.getParam(1).isSubtype(param1)) {return false;}
            }
            else if(variances[1].equals(Variance.CONTRAVARIANT)) {
                if (!param1.isSubtype(any.getParam(1))) {return false;}
            }
            
            if (variances[2].equals(Variance.INVARIANT)) {
                if (!param2.equals(any.getParam(2))) {return false;}
            }
            else if(variances[2].equals(Variance.COVARIANT)) {
                if (!any.getParam(2).isSubtype(param2)) {return false;}
            }
            else if(variances[2].equals(Variance.CONTRAVARIANT)) {
                if (!param2.isSubtype(any.getParam(2))) {return false;}
            }
            return true;
        }
        else if (base.isInstance(o)) {
            return checkParents(o, param1, param2);
        }
        else {
            return false;
        }
    }
}
