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
    
    public Class<?> impl;
    public Variance[] variances;
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
    
    public Variance[] getVariances() {
        return variances;
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

    public boolean isSubtype(Type<?> o) {
        if (this == o) return true;
        if (o == Types.ANY) return true;
        if (o == Types.OBJECT) return !Types.isStructType(this);
        if (o instanceof RuntimeType<?>) {
            RuntimeType<?> rt = (RuntimeType<?>) o;
            if (rt.impl.isAssignableFrom(impl)) {
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

    public boolean instanceOf(Object o) {
        if (o == null) {return false;}
        if (o.getClass() == impl) {
            return true;
        }
        return impl.isInstance(o);
    }

    private static final boolean isSubtype(Variance varianceForParam, Type<?> typeForFormalParam, Type<?> typeForActualParam) {
        switch (varianceForParam) {
        case INVARIANT:
        	return typeForActualParam.equals(typeForFormalParam);
        case COVARIANT:
        	return typeForFormalParam.isSubtype(typeForActualParam);
        case CONTRAVARIANT:
        	return typeForActualParam.isSubtype(typeForFormalParam);
        }
//        assert false; // should never happen
        return true;
    }
    // o instanceof This and params
    public final boolean instanceOf(Object o, Type<?>... params) {
        if (o == null) {return false;}
        Class<?> target = o.getClass();
        if (target == impl || checkAnonymous(target)) {
            Variance varianceForParam;
            Type<?> typeForFormalParam;
            Type<?> typeForActualParam;
            for (int i = 0, s = params.length; i < s; i++) {
                varianceForParam = variances[i];
                typeForFormalParam = Types.getParam(o, i);
                typeForActualParam = params[i];
                if (!isSubtype(varianceForParam, typeForFormalParam, typeForActualParam)) {return false;}
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
        if (impl == rtt.getImpl()) {
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
            if (o instanceof Any || Types.supportTypeParameterOfJavaType) {
                str += "[";
                for (int i = 0; i < variances.length; i ++) {
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
    public boolean instanceOf(Object o, Type<?> param0) {
        if (o == null) {return false;}
        Class<?> target = o.getClass();
        if (target == impl || checkAnonymous(target)) {
            Variance varianceForParam;
            Type<?> typeForFormalParam;
            Type<?> typeForActualParam;
            varianceForParam = variances[0];
            typeForFormalParam = Types.getParam(o, 0);
            typeForActualParam = param0;
            if (!isSubtype(varianceForParam, typeForFormalParam, typeForActualParam)) {return false;}
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


    // for shortcut
    public final boolean instanceOf(Object o, Type<?> param0, Type<?> param1) {
        if (o == null) {return false;}
        Class<?> target = o.getClass();
        if (target == impl || checkAnonymous(target)) {
            Variance varianceForParam;
            Type<?> typeForFormalParam;
            Type<?> typeForActualParam;
            varianceForParam = variances[0];
            typeForFormalParam = Types.getParam(o, 0);
            typeForActualParam = param0;
            if (!isSubtype(varianceForParam, typeForFormalParam, typeForActualParam)) {return false;}
            varianceForParam = variances[1];
            typeForFormalParam = Types.getParam(o, 1);
            typeForActualParam = param1;
            if (!isSubtype(varianceForParam, typeForFormalParam, typeForActualParam)) {return false;}
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


    // for shortcut 
    public final boolean instanceOf(Object o, Type<?> param0, Type<?> param1, Type<?> param2) {
        if (o == null) {return false;}
        Class<?> target = o.getClass();
        if (target == impl || checkAnonymous(target)) {
            Variance varianceForParam;
            Type<?> typeForFormalParam;
            Type<?> typeForActualParam;
            varianceForParam = variances[0];
            typeForFormalParam = Types.getParam(o, 0);
            typeForActualParam = param0;
            if (!isSubtype(varianceForParam, typeForFormalParam, typeForActualParam)) {return false;}
            varianceForParam = variances[1];
            typeForFormalParam = Types.getParam(o, 1);
            typeForActualParam = param1;
            if (!isSubtype(varianceForParam, typeForFormalParam, typeForActualParam)) {return false;}
            varianceForParam = variances[2];
            typeForFormalParam = Types.getParam(o, 2);
            typeForActualParam = param2;
            if (!isSubtype(varianceForParam, typeForFormalParam, typeForActualParam)) {return false;}
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
