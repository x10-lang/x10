/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.serialization;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import sun.misc.Unsafe;
import x10.io.Deserializer;
import x10.io.SerializationException;
import x10.rtt.NamedStructType;
import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.runtime.impl.java.Runtime;

/**
 * An instance of a DeserializerThunk knows how to deserialize a specific class.
 * The various subclasses of this abstract class encode different cases of
 * the serialization protocol and are matched 1:1 with subclasses of SerializerThunk.<p>
 *  
 * The motivation for this software architecture is to improve the runtime efficiency 
 * of serialization/deserialization by doing as much of the protocol and meta-data 
 * operations once (at Thunk creation time) and amortizing that cost across all the
 * uses of the Thunk object.
 * 
 * @see SerializerThunk
 */
abstract class DeserializerThunk {

    protected static Unsafe unsafe = getUnsafe();

    protected static ConcurrentHashMap<Class<?>, DeserializerThunk> thunks = new ConcurrentHashMap<Class<?>, DeserializerThunk>(50);

    protected final DeserializerThunk superThunk;

    protected DeserializerThunk(DeserializerThunk st) {
        superThunk = st;
    }

    /**
     * Factory method to find or create the DeserializerThunk instance for clazz.
     * 
     * @param clazz The class to be deserialized.
     * @return The DeserializerThunk instance for the argument class.
     */
    static DeserializerThunk getDeserializerThunk(Class<? extends Object> clazz) {
        DeserializerThunk ans = thunks.get(clazz);
        if (ans == null) {
            ans = getDeserializerThunkHelper(clazz);
            thunks.put(clazz, ans);
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Creating deserialization thunk "+ans.getClass()+" for "+clazz);
            }
        }
        return ans;
    }

    /**
     * Create an instance of clazz and initialize it by reading
     * values for its instance fields from jds. 
     * 
     * @param clazz The class to create
     * @param jds The deserializer from which to obtain the new object's state.
     * @return the deserialized object.
     */
    @SuppressWarnings("unchecked")
    <T> T deserializeObject(Class<?> clazz, X10JavaDeserializer jds) throws IOException {
        T obj = null;

        // If the class is java.lang.Class we cannot create an instance in the following manner so we just skip it
        if (!"java.lang.Class".equals(clazz.getName())) {
            try {
                assert !Modifier.isAbstract(clazz.getModifiers());                    
                obj = (T)unsafe.allocateInstance(clazz);
            } catch (InstantiationException e) {
                throw new SerializationException(e);
            }
        }
        
        int i = x10.core.StructI.class.isAssignableFrom(clazz) ? -666 : jds.record_reference(obj);
        return deserializeObject(clazz, obj, i, jds);
    }

    /**
     * Initialize obj, an instance of clazz, by reading its instance state from jds.
     * 
     * @param clazz The class that is being deserialized
     * @param obj The object instance of the class to initialize
     * @param i The id of this object (in case deserialization needs to replace it)
     * @param jds The deserializer from which to obtain the new object's state.
     * @return The deserialized object. 
     */
    <T> T deserializeObject(Class<?> clazz, T obj, int i, X10JavaDeserializer jds) throws IOException {
        if (superThunk != null) {
            obj = superThunk.deserializeObject(clazz.getSuperclass(), obj, i, jds);
        }
        return deserializeBody(clazz, obj, i, jds);
    }

    protected abstract <T> T deserializeBody(Class<?> clazz, T obj, int i, X10JavaDeserializer jds) throws IOException;

    protected static Unsafe getUnsafe() {
        Unsafe unsafe = null;
        try {
            Class<Unsafe> uc = Unsafe.class;
            Field[] fields = uc.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getName().equals("theUnsafe")) {
                    fields[i].setAccessible(true);
                    unsafe = (Unsafe) fields[i].get(uc);
                    break;
                }
            }
        } catch (Exception ignore) {
        }
        return unsafe;
    }

    private static DeserializerThunk getDeserializerThunkHelper(Class<?> clazz) {

        // We need to handle these classes in a special way cause there implementation of serialization/deserialization is
        // not straight forward. Hence we just call into the custom serialization of these classes.
        if ("java.lang.String".equals(clazz.getName())) {
            return new JavaLangStringDeserializerThunk(null);
        } else if ("x10.rtt.NamedType".equals(clazz.getName())) {
            return new SpecialCaseDeserializerThunk(null);
        } else if ("x10.rtt.NamedStructType".equals(clazz.getName())) {
            return new SpecialCaseDeserializerThunk(null);  
        } else if ("x10.rtt.RuntimeType".equals(clazz.getName())) {
            return new SpecialCaseDeserializerThunk(null);      
        } else if (x10.core.GlobalRef.class.getName().equals(clazz.getName())) {
            return new SpecialCaseDeserializerThunk(null);
        } else if ("java.lang.Throwable".equals(clazz.getName())) {
            return new SpecialCaseDeserializerThunk(null);
        } else if ("java.lang.Class".equals(clazz.getName())) {
            return new SpecialCaseDeserializerThunk(null);
        } else if ("java.lang.Object".equals(clazz.getName())) {
            return new SpecialCaseDeserializerThunk(null);
        }

        Class<?>[] interfaces = clazz.getInterfaces();
        boolean isCustomSerializable = false;
        boolean isX10JavaSerializable = SerializationUtils.useX10SerializationProtocol(clazz);
        for (Class<?> aInterface : interfaces) {
            if ("x10.io.CustomSerialization".equals(aInterface.getName())) {
                isCustomSerializable = true;
                break;
            }
        }

        if (isCustomSerializable) {
            return new CustomDeserializerThunk(clazz);
        }

        if (isX10JavaSerializable) {
            return new X10JavaSerializableDeserializerThunk(clazz);
        }

        if (Runtime.USE_JAVA_SERIALIZATION && java.io.Serializable.class.isAssignableFrom(clazz)) {
            // Use default Java deserialization for this class
            return new DefaultJavaDeSerializationThunk(clazz);
        }
        
        Class<?> superclass = clazz.getSuperclass();
        DeserializerThunk superThunk = null;
        if (!("java.lang.Object".equals(superclass.getName()) || "x10.core.Ref".equals(superclass.getName()) || "x10.core.Struct".equals(superclass.getName()))) {
            superThunk = getDeserializerThunk(superclass);
        }

        return new FieldBasedDeserializerThunk(clazz, superThunk);
    }

    /**
     * A thunk for a vanilla X10 class (supports compiler-generated serialization code).
     */
    private static class X10JavaSerializableDeserializerThunk extends DeserializerThunk {
        protected final Method deserializeBodyMethod;

        X10JavaSerializableDeserializerThunk(Class<? extends Object> clazz) {
            super(null);  // The compiler-generated serialization code will invoke the superclass deserializer directly
            
            try {
                deserializeBodyMethod = clazz.getDeclaredMethod("$_deserialize_body", clazz, X10JavaDeserializer.class);
            } catch (SecurityException e) {
                System.err.println("DeserializerThunk: class "+clazz+" does not have a $_deserialize_body method");
                throw new SerializationException(e);
            } catch (NoSuchMethodException e) {
                System.err.println("DeserializerThunk: class "+clazz+" does not have a $_deserialize_body method");
                throw new SerializationException(e);
            }
            deserializeBodyMethod.setAccessible(true);
        }

        @Override
        protected <T> T deserializeBody(Class<?> clazz, T obj, int i, X10JavaDeserializer jds) throws IOException {
            try {
                deserializeBodyMethod.invoke(null, obj, jds);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause; // don't wrap
                } else {
                    throw new SerializationException(cause != null ? cause : e);
                }
            } catch (RuntimeException e) {
                throw e; // don't wrap
            } catch (Throwable e) {
                throw new SerializationException(e);
            }
            return obj;
        }
    }
    
    /**
     * A thunk for a vanilla X10 class (supports compiler-generated serialization code).
     */
    private static class DefaultJavaDeSerializationThunk extends DeserializerThunk {
        
        DefaultJavaDeSerializationThunk(Class<? extends Object> clazz) {
            super(null);  // The compiler-generated serialization code will invoke the superclass deserializer directly
        }
        
        
        /**
         * Create an instance of clazz and initialize it by reading
         * values for its instance fields from jds. 
         * 
         * @param clazz The class to create
         * @param jds The deserializer from which to obtain the new object's state.
         * @return the deserialized object.
         */
        @SuppressWarnings("unchecked")
        @Override
        <T> T deserializeObject(Class<?> clazz, X10JavaDeserializer jds) throws IOException {
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("...deserializing object using Java deserialization");
            }

            int id = jds.record_reference(null);   // Fudge id assignment in case the object has reachability to something we have already deserialized
            T obj = (T)jds.readUsingObjectInputStream();
            jds.update_reference(id, obj);

            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("...completed deserializing object using Java deserialization");
            }

            return obj;
        }
        
        @Override
        protected <T> T deserializeBody(Class<?> clazz, T obj, int i, X10JavaDeserializer jds) {
            throw new SerializationException("Unreachable code; should not call this method!");
        }
    }
    
    private static class FieldBasedDeserializerThunk extends DeserializerThunk {
        protected final Field[] fields;

        FieldBasedDeserializerThunk(Class<? extends Object> clazz, DeserializerThunk st) {
            super(st);

            // XTENLANG-2982,2983 transient fields may be initialized with readObject method. 
            Method readObjectMethod = null;
            try {
                readObjectMethod = clazz.getDeclaredMethod("readObject", java.io.ObjectInputStream.class);
            } catch (Exception e) {}

            // Sort the fields to get JVM-independent ordering.
            Set<Field> flds = new TreeSet<Field>(new FieldComparator());
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || (Modifier.isTransient(modifiers) && readObjectMethod == null)) {
                    continue;
                }
                field.setAccessible(true);
                flds.add(field);
            }
            fields = flds.toArray(new Field[flds.size()]);
        }

        protected <T> T deserializeBody(Class<?> clazz, T obj, int i, X10JavaDeserializer jds) throws IOException {
            for (Field field : fields) {
                try {
                    Class<?> type = field.getType();
                    if (type.isPrimitive()) {
                        Class<?> type1 = field.getType();
                        if ("int".equals(type1.getName())) {
                            field.setInt(obj, jds.readInt());
                        } else if ("double".equals(type1.getName())) {
                            field.setDouble(obj, jds.readDouble());
                        } else if ("float".equals(type1.getName())) {
                            field.setFloat(obj, jds.readFloat());
                        } else if ("boolean".equals(type1.getName())) {
                            field.setBoolean(obj, jds.readBoolean());
                        } else if ("byte".equals(type1.getName())) {
                            field.setByte(obj, jds.readByte());
                        } else if ("short".equals(type1.getName())) {
                            field.setShort(obj, jds.readShort());
                        } else if ("long".equals(type1.getName())) {
                            field.setLong(obj, jds.readLong());
                        } else if ("char".equals(type1.getName())) {
                            field.setChar(obj, jds.readChar());
                        }
                    } else if ("java.lang.String".equals(type.getName())) {
                        field.set(obj, jds.readString());
                    } else {
                        Object value = jds.readObject();
                        field.set(obj, value);
                    }
                } catch (RuntimeException e) {
                    throw e; // don't wrap
                } catch (Throwable e) {
                    throw new SerializationException(e);
                }
            }
            return obj;
        }
    }

    private static class CustomDeserializerThunk extends DeserializerThunk {
        protected final Field[] fields;
        protected final Method deserializationConstructor;

        CustomDeserializerThunk(Class<? extends Object> clazz) {
            super(null);

            // Even though this class implements a custom serialization protocol,
            // the runtime needs to invisibly serialize those instance fields that
            // are used to provide RTT information for generic types (not visible at the user-level).
            TypeVariable<? extends Class<? extends Object>>[] typeParameters = clazz.getTypeParameters();
            if (typeParameters.length > 0) {
                // Must sort the fields to get JVM-independent ordering.
                Set<Field> flds = new TreeSet<Field>(new FieldComparator());
                for (TypeVariable<? extends Class<? extends Object>> typeParameter: typeParameters) {
                    try {
                        Field field = clazz.getDeclaredField(typeParameter.getName());
                        field.setAccessible(true);
                        flds.add(field);
                    } catch (NoSuchFieldException e) {
                        throw new SerializationException(e);
                    }
                }
                fields = flds.toArray(new Field[flds.size()]); 
            } else {
                fields = new Field[0];
            }

            // We can't use the same method name in all classes cause it creates an endless loop cause when super.init is called it calls back to this method
            try {
                deserializationConstructor = clazz.getMethod(clazz.getName().replace(".", "$") + "$_deserialize_body", Deserializer.class);
                deserializationConstructor.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new SerializationException(e);
            }
        }

        @Override
        protected <T> T deserializeBody(Class<?> clazz, T obj, int i, X10JavaDeserializer jds) throws IOException {
            for (Field field : fields) {
                Object value = jds.readObject();
                try {
                    field.set(obj, value);
                } catch (IllegalArgumentException e) {
                    throw new SerializationException(e);
                } catch (IllegalAccessException e) {
                    throw new SerializationException(e);
                }
            }

            try {
                deserializationConstructor.invoke(obj, new Deserializer(jds));
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause; // don't wrap
                } else {
                    throw new SerializationException(cause != null ? cause : e);
                }
            } catch (RuntimeException e) {
                throw e; // don't wrap
            } catch (Throwable e) {
                throw new SerializationException(e);
            }
            short marker = jds.readSerializationId();
            if (marker != SerializationConstants.CUSTOM_SERIALIZATION_END) {
                X10JavaDeserializer.raiseSerializationProtocolError();
            }
            return obj;
        }
    }

    private static class JavaLangStringDeserializerThunk extends DeserializerThunk {

        JavaLangStringDeserializerThunk(Class <? extends Object> clazz) {
            super(null);
        }
        
        @Override
        <T> T deserializeObject(Class<?> clazz, X10JavaDeserializer jds) throws IOException {
            int i = jds.record_reference(null);
            return deserializeObject(clazz, null, i, jds);
        }

        @Override
        <T> T deserializeObject(Class<?> clazz, T obj, int i, X10JavaDeserializer jds) throws IOException {
            return deserializeBody(clazz, obj, i, jds);
        }

        @SuppressWarnings("unchecked")
        protected <T> T deserializeBody(Class<?> clazz, T obj, int i, X10JavaDeserializer jds) throws IOException {
            String realVal = jds.readStringValue();
            jds.update_reference(i, realVal);
            return (T) realVal;
        }
    }
    
    private static class SpecialCaseDeserializerThunk extends DeserializerThunk {
    	// XTENLANG-3258: enable writable stack trace before calling setStackTrace
        private static final StackTraceElement[] UNASSIGNED_STACK = new StackTraceElement[0];

        SpecialCaseDeserializerThunk(Class <? extends Object> clazz) {
            super(null);
        }

        SpecialCaseDeserializerThunk(Class <? extends Object> clazz, DeserializerThunk st) {
            super(st);
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        protected <T> T deserializeBody(Class<?> clazz, T obj, int i, X10JavaDeserializer jds) throws IOException {
            if ("x10.rtt.NamedType".equals(clazz.getName())) {
                NamedType.$_deserialize_body((NamedType) obj, jds);
                return obj;
            } else if ("x10.rtt.NamedStructType".equals(clazz.getName())) {
                NamedStructType.$_deserialize_body((NamedStructType) obj, jds);
                return obj;
            } else if ("x10.rtt.RuntimeType".equals(clazz.getName())) {
                X10JavaSerializable x10JavaSerializable = RuntimeType.$_deserialize_body((RuntimeType) obj, jds);
                if (obj != x10JavaSerializable) {
                    jds.update_reference(i, x10JavaSerializable);
                    obj = (T) x10JavaSerializable;
                }
                return obj;
            } else if (x10.core.GlobalRef.class.getName().equals(clazz.getName())) {
                return (T) x10.core.GlobalRef.$_deserialize_body((x10.core.GlobalRef) obj, jds);
            } else if ("java.lang.Throwable".equals(clazz.getName())) {
                if (X10JavaSerializer.THROWABLES_SERIALIZE_MESSAGE) {
                    try {
                        String message = (String)jds.readObject();
                        Field detailMessageField = java.lang.Throwable.class.getDeclaredField("detailMessage");
                        detailMessageField.setAccessible(true);
                        detailMessageField.set(obj, message);
                    } catch (RuntimeException e) {
                        throw e; // don't wrap
                    } catch (Throwable e) {
                        throw new SerializationException(e);
                    }
                }
                if (X10JavaSerializer.THROWABLES_SERIALIZE_STACKTRACE) {
                    java.lang.StackTraceElement[] trace = (java.lang.StackTraceElement[]) jds.readObject();
                	// XTENLANG-3258: enable writable stack trace before calling setStackTrace
                    boolean nonNonIBMJavaVM = false;
                    try {
                    	// For IBM Java VM: set enableWritableStackTrace before calling setStackTrace
                    	Field enableWritableStackTraceField = Throwable.class.getDeclaredField("enableWritableStackTrace");
                    	enableWritableStackTraceField.setAccessible(true);
                    	enableWritableStackTraceField.setBoolean(obj, true);
                    } catch (Exception e) {
                    	nonNonIBMJavaVM = true;
                    }
                    if (nonNonIBMJavaVM) {
                        try {
                            // For Oracle Java VM: set stackTrace before calling setStackTrace
                            Field stackTraceField = Throwable.class.getDeclaredField("stackTrace");
                            stackTraceField.setAccessible(true);
                            stackTraceField.set(obj, UNASSIGNED_STACK);
                        } catch (Exception e) { }
                    }
                    ((Throwable) obj).setStackTrace(trace);
                }
                if (X10JavaSerializer.THROWABLES_SERIALIZE_CAUSE) {
                    try {
                        java.lang.Throwable cause = (java.lang.Throwable) jds.readObject();
                        Field causeField = java.lang.Throwable.class.getDeclaredField("cause");
                        causeField.setAccessible(true);
                        causeField.set(obj, cause);
                    } catch (RuntimeException e) {
                        throw e; // don't wrap
                    } catch (Throwable e) {
                        throw new SerializationException(e);
                    }
                }
                return obj;
            } else if ("java.lang.Class".equals(clazz.getName())) {
                try {
                    T t = (T) jds.dict.loadClass(jds);
                    jds.update_reference(i, t);
                    return t;
                } catch (ClassNotFoundException e) {
                    throw new SerializationException(e);
                }
            } else if ("java.lang.Object".equals(clazz.getName())) {
                return obj;
            }
            String msg = "Unhandled type in special case thunk: "+obj.getClass();
            System.err.println(msg);
            throw new SerializationException(msg);
        }
    }
}