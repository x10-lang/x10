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

import java.lang.reflect.Array;

import x10.core.Any;

public class RuntimeType<T> implements Type<T> {

    public enum Variance {INVARIANT, COVARIANT, CONTRAVARIANT}
    
    Type<?>[] parents;
    Class<?> base;
    Variance[] variances;
    
    public RuntimeType(Class<?> c) {
        this.base = c;
    }
    
    public RuntimeType(Class<?> c, Variance[] variances) {
        this.base = c;
        this.variances = variances;
    }

    public RuntimeType(Class<?> c, Type<?>[] parents) {
        this.base = c;
        this.parents = parents;
    }
    
    public RuntimeType(Class<?> c, Variance[] variances, Type<?>[] parents) {
        this.base = c;
        this.variances = variances;
        this.parents = parents;
    }

    public Class<?> getJavaClass() {
        return base;
    }
    
    public Variance[] getVariances() {
        return variances;
    }
    
    public Type<?>[] getParents() {
        return parents;
    }
    
    public String toString() {
        return typeName();
    }
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof RuntimeType<?>) {
            RuntimeType<?> rt = (RuntimeType<?>) o;
            if (base.equals(rt.base)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isSubtype(Type<?> o) {
        if (this == o) return true;
        if (o == Types.ANY) return true;
        if (o == Types.OBJECT) return !Types.isStructType(this);
        if (o instanceof RuntimeType<?>) {
            RuntimeType<?> rt = (RuntimeType<?>) o;
            if (rt.base.isAssignableFrom(base)) {
                return true;
            }
        }
        if (o instanceof ParameterizedType) {
            ParameterizedType<?> pt = (ParameterizedType<?>) o;
            if (pt.getRuntimeType().isSuperType(pt.getParams(), this, null)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasZero() {
        return true;
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
        Class<?> target = o.getClass();
        if (target == base || checkAnonymous(target)) {
            Any any = (Any) o;
            for (int i = 0, s = params.length; i < s; i ++) {
                switch (variances[i]) {
                case INVARIANT:
                    if (!params[i].equals(any.$getParam(i))) {return false;}
                    break;
                case COVARIANT:
                    if (!any.$getParam(i).isSubtype(params[i])) {return false;}
                    break;
                case CONTRAVARIANT:
                    if (!params[i].isSubtype(any.$getParam(i))) {return false;}
                    break;
                }
            }
            return true;
        }
        else if (base.isInstance(o)) { // i.e. type of o != This
            return checkParents(o, params);
        }
        else if (o instanceof String || o instanceof Number) {
            // @NativeRep'ed type
            return checkParents(o, params);
        }
        else {
            return false;
        }
    }

    private boolean checkAnonymous(Class<?> target) {
        if (!target.isAnonymousClass()) {
            return false;
        }
        if (target.getSuperclass() != java.lang.Object.class && target.getSuperclass() == base) {
            return true;
        }
        if (target.getInterfaces().length == 1 && target.getInterfaces()[0] == base) {
            return true;
        }
        return false;
    }

    private final boolean checkParents(Object o, Type<?>... params) {
        if (o instanceof Any) {
            Any any = (Any) o;
            RuntimeType<?> rtt = any.$getRTT(); // o._RTT
            if (rtt == null) {
                return true;
            }
            return instantiateCheck(params, rtt, any);
        }
        /*
        else if (o instanceof String) {
            // @NativeRep'ed String type (the one with parents info)
            RuntimeType<?> rtt = (RuntimeType<?>) Types.getNativeRepRTT(o);
            return instantiateCheck(params, rtt, o);
        }
        else if (o instanceof Number) {
            // @NativeRep'ed numeric type
            return false;
        }
        */
        else if (null != Types.getNativeRepRTT(o)) {
            // @NativeRep'ed types to raw Java classes (e.g. String, Integer, etc.)
            RuntimeType<?> rtt = Types.getNativeRepRTT(o);
            return instantiateCheck(params, rtt, o);
        }
        return false;
    }

    // e.g. C[T1,T2]:Super[Int, T1] -> C[Int,Double]:Super[Int,Int] 
    private final boolean instantiateCheck(Type<?>[] params, RuntimeType<?> rtt, Object o) {
        if (rtt.parents != null) {
            for (Type<?> t : rtt.parents) {
                if (base.isAssignableFrom(t.getJavaClass())) {
                    if (t instanceof ParameterizedType<?>) {
                        ParameterizedType<?> pt = (ParameterizedType<?>) t;
                        Type<?>[] paramsT = pt.getParams();
                        Type<?>[] newParamsT = new Type<?>[paramsT.length];
                        for (int i = 0; i < paramsT.length; i ++ ) {
                            if (paramsT[i] != null && paramsT[i] instanceof UnresolvedType) {
                                int index = ((UnresolvedType) paramsT[i]).getIndex();
                                assert(index == -1);
                                newParamsT[i] = rtt;
                            }
                            else {
                                newParamsT[i] = paramsT[i];
                            }
                        }
                        if (isSuperType(params, pt.getRuntimeType(), newParamsT)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // e.g. C[T1,T2]:Super[Int, T1] -> C[Int,Double]:Super[Int,Int] 
    private final boolean instantiateCheck(Type<?>[] params, RuntimeType<?> rtt, Any any) {
        if (rtt.parents != null) {
            for (Type<?> t : rtt.parents) {
                if (base.isAssignableFrom(t.getJavaClass())) {
                    if (t instanceof ParameterizedType<?>) {
                        ParameterizedType<?> pt = (ParameterizedType<?>) t;
                        Type<?>[] paramsT = pt.getParams();
                        Type<?>[] newParamsT = new Type<?>[paramsT.length];
                        for (int i = 0; i < paramsT.length; i ++ ) {
                            if (paramsT[i] != null && paramsT[i] instanceof UnresolvedType) {
                                int index = ((UnresolvedType) paramsT[i]).getIndex();
                                newParamsT[i]= index == -1 ? rtt : any.$getParam(index);
                            }
                            else {
                                newParamsT[i] = paramsT[i];
                            }
                        }
                        if (isSuperType(params, pt.getRuntimeType(), newParamsT)) {
                            return true;
                        }
                    }
                    if (t instanceof RuntimeType && equals(t)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    // e.g. C[T1,T2]:Super[Int, T1] -> C[Int,Double]:Super[Int,Int] 
    private final boolean instantiateCheck(Type<?>[] params, RuntimeType<?> rtt, Type<?>[] paramsRTT) {
        if (rtt.parents != null) {
            for (Type<?> t : rtt.parents) {
                if (base.isAssignableFrom(t.getJavaClass())) {
                    if (t instanceof ParameterizedType<?>) {
                        ParameterizedType<?> pt = (ParameterizedType<?>) t;
                        Type<?>[] paramsT = pt.getParams();
                        Type<?>[] newParamsT = new Type<?>[paramsT.length];
                        for (int i = 0; i < paramsT.length; i ++ ) {
                            if (paramsT[i] != null && paramsT[i] instanceof UnresolvedType) {
                                int index = ((UnresolvedType) paramsT[i]).getIndex();
                                newParamsT[i] = index == -1 ? rtt : paramsRTT[index];
                            }
                            else {
                                newParamsT[i] = paramsT[i];
                            }
                        }
                        if (isSuperType(params, pt.getRuntimeType(), newParamsT)) {
                            return true;
                        }
                    }
                    if (t instanceof RuntimeType && equals(t)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    // check "type and paramsType" <: "this and params"
    final boolean isSuperType(Type<?>[] params, RuntimeType<?> rtt, Type<?>[] paramsType) {
        if (base == rtt.getJavaClass()) {
            if (params != null) {
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
    
    public Object makeArray(int length) {
        return Array.newInstance(base, length);
    }

    public Object makeArray(Object... elems) {
        return elems;
    }
    
    public T getArray(Object array, int i) {
        return ((T[])array)[i];
    }

    public T setArray(Object array, int i, T v) {
        ((T[])array)[i] = v;
        return v;
    }
    
    public int arrayLength(Object array) {
        return ((T[])array).length;
    }

    public String typeName() {
        String name = base.toString();
        if (name.startsWith("class ")) {
            name = name.substring("class ".length());
        } else if (name.startsWith("interface ")) {
            name = name.substring("interface ".length());
        }
        return name;
    }

    protected final String typeNameForFun(Object o) {
        String str = "(";
        int i;
        for (i = 0; i < variances.length - 1; i++) {
            if (i != 0) str += ",";
            str += ((Any) o).$getParam(i).typeName();
        }
        str += ")=>";
        str += ((Any) o).$getParam(i).typeName();
        return str;
    }
    protected final String typeNameForVoidFun(Object o) {
        String str = "(";
        if (variances != null && variances.length > 0) {
            for (int i = 0; i < variances.length; i++) {
                if (i != 0) str += ",";
                str += ((Any) o).$getParam(i).typeName();
            }
        }
        str += ")=>void";
        return str;
    }
    protected final String typeNameForOthers(Object o) {
        String str = typeName();
        if (variances != null && variances.length > 0) {
            if (o instanceof Any) {
                str += "[";
                for (int i = 0; i < variances.length; i ++) {
                    if (i != 0) str += ",";
                    str += ((Any) o).$getParam(i).typeName();
                }
                str += "]";
            }
        }
        return str;
    }
    // should be overridden by RTT of all function types
    public String typeName(Object o) {
        return typeNameForOthers(o);
    }
    
    // for shortcut
    public boolean instanceof$(Object o, Type<?> param0) {
        if (o == null) {return false;}
        Class<?> target = o.getClass();
        if (target == base || checkAnonymous(target)) {
            Any any = (Any) o;
            if (variances[0].equals(Variance.INVARIANT)) {
                if (!param0.equals(any.$getParam(0))) {return false;}
            }
            else if(variances[0].equals(Variance.COVARIANT)) {
                if (!any.$getParam(0).isSubtype(param0)) {return false;}
            }
            else if(variances[0].equals(Variance.CONTRAVARIANT)) {
                if (!param0.isSubtype(any.$getParam(0))) {return false;}
            }
            return true;
        }
        else if (base.isInstance(o)) {
            return checkParents(o, param0);
        }
        else if (o instanceof String || o instanceof Number) {
            // @NativeRep'ed type
            return checkParents(o, param0);
        }
        else {
            return false;
        }
    }


    // for shortcut
    public final boolean instanceof$(Object o, Type<?> param0, Type<?> param1) {
        if (o == null) {return false;}
        Class<?> target = o.getClass();
        if (target == base || checkAnonymous(target)) {
            Any any = (Any) o;
            if (variances[0].equals(Variance.INVARIANT)) {
                if (!param0.equals(any.$getParam(0))) {return false;}
            }
            else if(variances[0].equals(Variance.COVARIANT)) {
                if (!any.$getParam(0).isSubtype(param0)) {return false;}
            }
            else if(variances[0].equals(Variance.CONTRAVARIANT)) {
                if (!param0.isSubtype(any.$getParam(0))) {return false;}
            }
            if (variances[1].equals(Variance.INVARIANT)) {
                if (!param1.equals(any.$getParam(1))) {return false;}
            }
            else if(variances[1].equals(Variance.COVARIANT)) {
                if (!any.$getParam(1).isSubtype(param1)) {return false;}
            }
            else if(variances[1].equals(Variance.CONTRAVARIANT)) {
                if (!param1.isSubtype(any.$getParam(1))) {return false;}
            }
            return true;
        }
        else if (base.isInstance(o)) {
            return checkParents(o, param0, param1);
        }
        else if (o instanceof String || o instanceof Number) {
            // @NativeRep'ed type
            return checkParents(o, param0, param1);
        }
        else {
            return false;
        }
        
    }


    // for shortcut 
    public final boolean instanceof$(Object o, Type<?> param0, Type<?> param1, Type<?> param2) {
        if (o == null) {return false;}
        Class<?> target = o.getClass();
        if (target == base || checkAnonymous(target)) {
            Any any = (Any) o;
            if (variances[0].equals(Variance.INVARIANT)) {
                if (!param0.equals(any.$getParam(0))) {return false;}
            }
            else if(variances[0].equals(Variance.COVARIANT)) {
                if (!any.$getParam(0).isSubtype(param0)) {return false;}
            }
            else if(variances[0].equals(Variance.CONTRAVARIANT)) {
                if (!param0.isSubtype(any.$getParam(0))) {return false;}
            }
            
            if (variances[1].equals(Variance.INVARIANT)) {
                if (!param1.equals(any.$getParam(1))) {return false;}
            }
            else if(variances[1].equals(Variance.COVARIANT)) {
                if (!any.$getParam(1).isSubtype(param1)) {return false;}
            }
            else if(variances[1].equals(Variance.CONTRAVARIANT)) {
                if (!param1.isSubtype(any.$getParam(1))) {return false;}
            }
            
            if (variances[2].equals(Variance.INVARIANT)) {
                if (!param2.equals(any.$getParam(2))) {return false;}
            }
            else if(variances[2].equals(Variance.COVARIANT)) {
                if (!any.$getParam(2).isSubtype(param2)) {return false;}
            }
            else if(variances[2].equals(Variance.CONTRAVARIANT)) {
                if (!param2.isSubtype(any.$getParam(2))) {return false;}
            }
            return true;
        }
        else if (base.isInstance(o)) {
            return checkParents(o, param0, param1, param2);
        }
        else if (o instanceof String || o instanceof Number) {
            // @NativeRep'ed type
            return checkParents(o, param0, param1, param2);
        }
        else {
            return false;
        }
    }
}
