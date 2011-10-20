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

import java.io.IOException;
import java.lang.reflect.Array;

import x10.core.Any;
import x10.x10rt.DeserializationDispatcher;
import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

public class RuntimeType<T> implements Type<T>, X10JavaSerializable {

    private static final long serialVersionUID = 1L;
    private static final short _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RuntimeType.class);

    public enum Variance {INVARIANT, COVARIANT, CONTRAVARIANT}
    private static final Variance[][] invariants = {
        null,
        new Variance[] {Variance.INVARIANT},
        new Variance[] {Variance.INVARIANT,Variance.INVARIANT},
        new Variance[] {Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT},
        new Variance[] {Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT},
        new Variance[] {Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT},
        new Variance[] {Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT},
        new Variance[] {Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT},
        new Variance[] {Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT},
        new Variance[] {Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT},
        new Variance[] {Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT,Variance.INVARIANT},
    };
    public static Variance[] INVARIANTS(int length) {
        assert length >= 1;
        if (length <= 10) {
            return invariants[length];
        }
        Variance[] variances = new Variance[length];
        java.util.Arrays.fill(variances, Variance.INVARIANT);
        return variances;
    }
    
    public Class<?> impl;
    private Variance[] variances;
    public Type<?>[] parents;

    // Just for allocation
    public RuntimeType() {
    }
    
    public RuntimeType(Class<?> impl) {
        this(impl, null, null);
    }

    public RuntimeType(Class<?> impl, Variance[] variances) {
        this(impl, variances, null);
    }

    public RuntimeType(Class<?> impl, Type<?>[] parents) {
        this(impl, null, parents);
    }
    
    public RuntimeType(Class<?> impl, Variance[] variances, Type<?>[] parents) {
        this.impl = impl;
        this.variances = variances;
        this.parents = parents;
    }

    public Class<?> getImpl() {
        return impl;
    }
    
    // not used
//    public Variance[] getVariances() {
//        return variances;
//    }
    
    private final Variance getVariance(int i) {
        return variances[i];
    }
    
    private final int numParams() {
        return variances != null ? variances.length : 0;
    }
    
    public Type<?>[] getParents() {
        return parents;
    }
    
    @Override
    public String toString() {
        return typeName();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof RuntimeType<?>) {
            RuntimeType<?> rt = (RuntimeType<?>) o;
            if (!impl.equals(rt.impl)) {
                return false;
            }
            // N.B. for given impl, we assume variances and parents are unique.
            // Therefore we don't need to compare them.
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return impl.hashCode();
    }

    public boolean isAssignableTo(Type<?> superType) {
        if (this == superType) return true;
        if (superType == Types.ANY) return true;
        if (superType == Types.OBJECT) return !Types.isStructType(this);
        if (superType instanceof RuntimeType<?>) {
            RuntimeType<?> rt = (RuntimeType<?>) superType;
            if (rt.impl.isAssignableFrom(impl)) {
                return true;
            }
        }
        if (superType instanceof ParameterizedType) {
            ParameterizedType<?> pt = (ParameterizedType<?>) superType;
            if (pt.getRuntimeType().isAssignableFrom(pt.getParams(), this, null)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasZero() {
        return true;
    }

    public boolean isInstance(Object o) {
        if (o == null) {return false;}
        if (o.getClass() == impl) {
            return true;
        }
        return impl.isInstance(o);
    }
    @Deprecated
    public final boolean instanceOf(Object o) {
        return isInstance(o);
    }

    private static final boolean subtypeTestForParam(Variance variance, Type<?> paramForType, Type<?> paramForInstance) {
        switch (variance) {
        case INVARIANT:
            return paramForType.equals(paramForInstance);
        case COVARIANT:
            return paramForInstance.isAssignableTo(paramForType);
        case CONTRAVARIANT:
            return paramForType.isAssignableTo(paramForInstance);
        }
//        assert false; // should never happen
        return true;
    }
    // o instanceof This and params
    public final boolean isInstance(Object o, Type<?>... params) {
        if (o == null) {return false;}
        Class<?> target = o.getClass();
        if (target == impl || checkAnonymous(target)) {
            for (int i = 0, s = params.length; i < s; i++) {
                Variance variance;
                Type<?> paramForInstance;
                Type<?> paramForType;
                variance = getVariance(i);
                paramForInstance = Types.getParam(o, i);
                paramForType = params[i];
                if (!subtypeTestForParam(variance, paramForType, paramForInstance)) {return false;}
            }
            return true;
        }
        else if (impl.isInstance(o)) { // i.e. type of o != This
            return checkParents(o, params);
        }
        // not needed for Java primitives. not sure for String
        /*
        else if (o instanceof String || o instanceof Number) {
            // @NativeRep'ed type
            return checkParents(o, params);
        }
        */
        else {
            return false;
        }
    }
    @Deprecated
    public final boolean instanceOf(Object o, Type<?>... params) {
        return isInstance(o, params);
    }

    private boolean checkAnonymous(Class<?> target) {
        if (!target.isAnonymousClass()) {
            return false;
        }
        if (target.getSuperclass() != java.lang.Object.class && target.getSuperclass() == impl) {
            return true;
        }
        if (target.getInterfaces().length == 1 && target.getInterfaces()[0] == impl) {
            return true;
        }
        return false;
    }

    private final boolean checkParents(Object o, Type<?>... params) {
        if (o instanceof Any) {
            Any any = (Any) o;
            RuntimeType<?> rtt = any.$getRTT(); // o.$RTT
            if (rtt == null) {
                return true;
            }
            return instantiateCheck(params, rtt, any);
        }
        else if (Types.supportTypeParameterOfJavaType) {
            RuntimeType<?> rtt = Types.getRTT(o);
            return instantiateCheck(params, rtt, o);
        }
        return false;
    }

    // e.g. C[T1,T2]:Super[Int, T1] -> C[Int,Double]:Super[Int,Int] 
    private final boolean instantiateCheck(Type<?>[] params, RuntimeType<?> rtt, Object o) {
        if (rtt.parents != null) {
            for (Type<?> t : rtt.parents) {
                if (impl.isAssignableFrom(t.getImpl())) {
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
                        if (isAssignableFrom(params, pt.getRuntimeType(), newParamsT)) {
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
                if (impl.isAssignableFrom(t.getImpl())) {
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
                        if (isAssignableFrom(params, pt.getRuntimeType(), newParamsT)) {
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
                if (impl.isAssignableFrom(t.getImpl())) {
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
                        if (isAssignableFrom(params, pt.getRuntimeType(), newParamsT)) {
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
    
    // check "rtt and paramsType" <: "this and params"
    final boolean isAssignableFrom(Type<?>[] params, RuntimeType<?> rtt, Type<?>[] paramsType) {
        if (impl == rtt.getImpl()) {
            if (params != null) {
                for (int i = 0, s = params.length; i < s; i ++) {
                    Variance variance;
                    Type<?> paramForInstance;
                    Type<?> paramForType;
                    variance = getVariance(i);
                    paramForInstance = paramsType[i];
                    paramForType = params[i];
                    if (!subtypeTestForParam(variance, paramForType, paramForInstance)) {return false;}
                }
            }
            return true;
        }
        else if (impl.isAssignableFrom(rtt.getImpl())) {
            return instantiateCheck(params, rtt, paramsType);
        }
        else {
            return false;
        }
    }
    
    public Object makeArray(int length) {
        return Array.newInstance(impl, length);
    }

    public Object makeArray(int dim0, int dim1) {
    	return Array.newInstance(impl, new int[] { dim0, dim1 });
    }
    
    public Object makeArray(int dim0, int dim1, int dim2) {
    	return Array.newInstance(impl, new int[] { dim0, dim1, dim2 });
    }
    
    public Object makeArray(int dim0, int dim1, int dim2, int dim3) {
    	return Array.newInstance(impl, new int[] { dim0, dim1, dim2, dim3 });
    }
    
    public Object makeArray(Object... elems) {
        return elems;
    }
    
    public T getArray(Object array, int i) {
        return ((T[])array)[i];
    }

//    public T setArray(Object array, int i, T v) {
//        ((T[])array)[i] = v;
//        return v;
//    }
    public void setArray(Object array, int i, T v) {
        ((T[])array)[i] = v;
    }
    
    public int arrayLength(Object array) {
        return ((T[])array).length;
    }

    public String typeName() {
        return impl.getName();
    }

    protected final String typeNameForFun(Object o) {
        int numParams = numParams();
        String str = "(";
        int i;
        for (i = 0; i < numParams - 1; i++) {
            if (i != 0) str += ",";
            str += ((Any) o).$getParam(i).typeName();
        }
        str += ")=>";
        str += ((Any) o).$getParam(i).typeName();
        return str;
    }
    protected final String typeNameForVoidFun(Object o) {
        int numParams = numParams();
        String str = "(";
        if (numParams > 0) {
            for (int i = 0; i < numParams; i++) {
                if (i != 0) str += ",";
                str += ((Any) o).$getParam(i).typeName();
            }
        }
        str += ")=>void";
        return str;
    }
    protected final String typeNameForOthers(Object o) {
        int numParams = numParams();
        String str = typeName();
        if (numParams > 0) {
            if (o instanceof Any || Types.supportTypeParameterOfJavaType) {
                str += "[";
                for (int i = 0; i < numParams; i ++) {
                    if (i != 0) str += ",";
                    str += Types.getParam(o, i).typeName();
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
    public boolean isInstance(Object o, Type<?> param0) {
        if (o == null) {return false;}
        Class<?> target = o.getClass();
        if (target == impl || checkAnonymous(target)) {
            Variance variance;
            Type<?> paramForInstance;
            Type<?> paramForType;
            variance = getVariance(0);
            paramForInstance = Types.getParam(o, 0);
            paramForType = param0;
            if (!subtypeTestForParam(variance, paramForType, paramForInstance)) {return false;}
            return true;
        }
        else if (impl.isInstance(o)) {
            return checkParents(o, param0);
        }
        // not needed for Java primitives. not sure for String
        /*
        else if (o instanceof String || o instanceof Number) {
            // @NativeRep'ed type
            return checkParents(o, param0);
        }
        */
        else {
            return false;
        }
    }
    @Deprecated
    public final boolean instanceOf(Object o, Type<?> param0) {
        return isInstance(o, param0);
    }


    // for shortcut
    public final boolean isInstance(Object o, Type<?> param0, Type<?> param1) {
        if (o == null) {return false;}
        Class<?> target = o.getClass();
        if (target == impl || checkAnonymous(target)) {
            Variance variance;
            Type<?> paramForInstance;
            Type<?> paramForType;
            variance = getVariance(0);
            paramForInstance = Types.getParam(o, 0);
            paramForType = param0;
            if (!subtypeTestForParam(variance, paramForType, paramForInstance)) {return false;}
            variance = getVariance(1);
            paramForInstance = Types.getParam(o, 1);
            paramForType = param1;
            if (!subtypeTestForParam(variance, paramForType, paramForInstance)) {return false;}
            return true;
        }
        else if (impl.isInstance(o)) {
            return checkParents(o, param0, param1);
        }
        // not needed for Java primitives. not sure for String
        /*
        else if (o instanceof String || o instanceof Number) {
            // @NativeRep'ed type
            return checkParents(o, param0, param1);
        }
        */
        else {
            return false;
        }
        
    }
    @Deprecated
    public final boolean instanceOf(Object o, Type<?> param0, Type<?> param1) {
        return isInstance(o, param0, param1);
    }


    // for shortcut
    public final boolean isInstance(Object o, Type<?> param0, Type<?> param1, Type<?> param2) {
        if (o == null) {return false;}
        Class<?> target = o.getClass();
        if (target == impl || checkAnonymous(target)) {
            Variance variance;
            Type<?> paramForInstance;
            Type<?> paramForType;
            variance = getVariance(0);
            paramForInstance = Types.getParam(o, 0);
            paramForType = param0;
            if (!subtypeTestForParam(variance, paramForType, paramForInstance)) {return false;}
            variance = getVariance(1);
            paramForInstance = Types.getParam(o, 1);
            paramForType = param1;
            if (!subtypeTestForParam(variance, paramForType, paramForInstance)) {return false;}
            variance = getVariance(2);
            paramForInstance = Types.getParam(o, 2);
            paramForType = param2;
            if (!subtypeTestForParam(variance, paramForType, paramForInstance)) {return false;}
            return true;
        }
        else if (impl.isInstance(o)) {
            return checkParents(o, param0, param1, param2);
        }
        // not needed for Java primitives. not sure for String
        /*
        else if (o instanceof String || o instanceof Number) {
            // @NativeRep'ed type
            return checkParents(o, param0, param1, param2);
        }
        */
        else {
            return false;
        }
    }
    @Deprecated
    public final boolean instanceOf(Object o, Type<?> param0, Type<?> param1, Type<?> param2) {
        return isInstance(o, param0, param1, param2);
    }

	public void $_serialize(X10JavaSerializer serializer) throws IOException {
        String name = impl.getName();
        serializer.writeClassID(name);
	}

	public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException {
        RuntimeType rt = new RuntimeType();
        int i = deserializer.record_reference(rt);
        X10JavaSerializable x10JavaSerializable = $_deserialize_body(rt, deserializer);
        if (rt != x10JavaSerializable) {
            deserializer.update_reference(i, x10JavaSerializable);
        }
        return x10JavaSerializable;
	}

	public short $_get_serialization_id() {
		return _serialization_id;
	}

    public static X10JavaSerializable $_deserialize_body(RuntimeType rt, X10JavaDeserializer deserializer) throws IOException {
        short classId = deserializer.readShort();
        String className = DeserializationDispatcher.getClassNameForID(classId, deserializer);
        if (className == null) {
            return null;
        } else if ("x10.core.Boolean".equals(className)) {
            return Types.BOOLEAN;
        } else if ("x10.core.Byte".equals(className)) {
            return Types.BYTE;
        } else if ("x10.core.Char".equals(className)) {
            return Types.CHAR;
        } else if ("x10.core.Double".equals(className)) {
            return Types.DOUBLE;
        } else if ("x10.core.Float".equals(className)) {
            return Types.FLOAT;
        } else if ("x10.core.Int".equals(className)) {
            return Types.INT;
        } else if ("x10.core.Long".equals(className)) {
            return Types.LONG;
        } else if ("x10.core.Object".equals(className)) {
            return Types.OBJECT;
        } else if ("x10.core.Short".equals(className)) {
            return Types.SHORT;
        } else if ("x10.core.String".equals(className)) {
            return Types.STRING;
        } else if ("x10.core.UByte".equals(className)) {
            return Types.UBYTE;
        } else if ("x10.core.UInt".equals(className)) {
            return Types.UINT;
        } else if ("x10.core.ULong".equals(className)) {
            return Types.ULONG;
        } else if ("x10.core.UShort".equals(className)) {
            return Types.USHORT;
        }
        try {
            Class<?> aClass = Class.forName(className);
            rt.impl = aClass;
        } catch (ClassNotFoundException e) {
            // This should not happen though
            throw new RuntimeException(e);
        }
        return rt;
    }
}
