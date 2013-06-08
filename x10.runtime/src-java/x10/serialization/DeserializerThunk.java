/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

package x10.serialization;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import sun.misc.Unsafe;
import x10.io.SerialData;
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

    protected static final String CONSTRUCTOR_METHOD_NAME_FOR_REFLECTION = "$initForReflection";
    protected static Unsafe unsafe = DeserializerThunk.getUnsafe();

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
    static DeserializerThunk getDeserializerThunk(Class<? extends Object> clazz) throws SecurityException, NoSuchFieldException, NoSuchMethodException {
        DeserializerThunk ans = DeserializerThunk.thunks.get(clazz);
        if (ans == null) {
            ans = DeserializerThunk.getDeserializerThunkHelper(clazz);
            DeserializerThunk.thunks.put(clazz, ans);
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
    <T> T deserializeObject(Class<?> clazz, X10JavaDeserializer jds) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        T obj = null;

        // If the class is java.lang.Class we cannot create an instance in the following manner so we just skip it
        if (!"java.lang.Class".equals(clazz.getName())) {
            try {
                assert !Modifier.isAbstract(clazz.getModifiers());                    
                obj = (T)DeserializerThunk.unsafe.allocateInstance(clazz);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
        int i = jds.record_reference(obj);
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
    <T> T deserializeObject(Class<?> clazz, T obj, int i, X10JavaDeserializer jds) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (superThunk != null) {
            obj = superThunk.deserializeObject(clazz.getSuperclass(), obj, i, jds);
        }
        return deserializeBody(clazz, obj, i, jds);
    }

    protected abstract <T> T deserializeBody(Class<?> clazz, T obj, int i, X10JavaDeserializer jds) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException;

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

    private static DeserializerThunk getDeserializerThunkHelper(Class<?> clazz) throws SecurityException, NoSuchFieldException, NoSuchMethodException {

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
        boolean isHadoopSerializable = Runtime.implementsHadoopWritable(clazz);
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

        if (isHadoopSerializable) {
            return new HadoopDeserializerThunk(clazz);
        }
        
        if (isX10JavaSerializable) {
            return new X10JavaSerializableDeserializerThunk(clazz);
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
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                System.err.println("DeserializerThunk: class "+clazz+" does not have a $_deserialize_body method");
                throw new RuntimeException(e);
            }
            deserializeBodyMethod.setAccessible(true);
        }

        @Override
        protected <T> T deserializeBody(Class<?> clazz, T obj, int i, X10JavaDeserializer jds) throws IOException,
                IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {

            deserializeBodyMethod.invoke(null, obj, jds);
            return obj;
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

        protected <T> T deserializeBody(Class<?> clazz, T obj, int i, X10JavaDeserializer jds) throws IOException, IllegalAccessException {             
            for (Field field : fields) {
                Class<?> type = field.getType();
                if (type.isPrimitive()) {
                    jds.readPrimitiveUsingReflection(field, obj);
                } else if (type.isArray()) {
                    field.set(obj, jds.readArrayUsingReflection(type.getComponentType()));
                } else if ("java.lang.String".equals(type.getName())) {
                    field.set(obj, jds.readStringUsingReflection());
                } else {
                    Object value = jds.readRefUsingReflection();
                    field.set(obj, value);
                }
            }
            return obj;
        }
    }

    private static class HadoopDeserializerThunk extends DeserializerThunk {
        private static final Class<?>[] EMPTY_ARRAY = new Class[]{};
        
        protected final Constructor<?> constructor;
        protected final Method readMethod;

        HadoopDeserializerThunk(Class<? extends Object> clazz) throws SecurityException, NoSuchMethodException {
            super(null);
            constructor = clazz.getDeclaredConstructor(EMPTY_ARRAY);
            constructor.setAccessible(true);

            readMethod = clazz.getMethod("readFields", java.io.DataInput.class);
            readMethod.setAccessible(true);
        }

        @SuppressWarnings("unchecked")
        @Override
        <T> T deserializeObject(Class<?> clazz, X10JavaDeserializer jds) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Calling hadoop deserializer with object of type " + clazz);
            }

            // Hadoop assumes that the default constructor will be used to create the instance.
            // The default constructor will execute field initialization expressions.  
            // So we have to mimic that behavior here.
            T obj = (T)constructor.newInstance();
            int i = jds.record_reference(obj);

            return deserializeObject(clazz, obj, i, jds);
        }

        @Override
        protected <T> T deserializeBody(Class<?> clazz, T obj, int i, X10JavaDeserializer jds) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
            readMethod.invoke(obj, jds.in);
            return obj;
        }

    }

    private static class CustomDeserializerThunk extends DeserializerThunk {
        protected final Field[] fields;
        protected final Method makeMethod;

        CustomDeserializerThunk(Class<? extends Object> clazz) throws SecurityException, NoSuchFieldException, NoSuchMethodException {
            super(null);

            // Even though this class implements a custom serialization protocol,
            // the runtime needs to invisibly serialize those instance fields that
            // are used to provide RTT information for generic types (not visible at the user-level).
            TypeVariable<? extends Class<? extends Object>>[] typeParameters = clazz.getTypeParameters();
            if (typeParameters.length > 0) {
                // Must sort the fields to get JVM-independent ordering.
                Set<Field> flds = new TreeSet<Field>(new FieldComparator());
                for (TypeVariable<? extends Class<? extends Object>> typeParameter: typeParameters) {
                    Field field = clazz.getDeclaredField(typeParameter.getName());
                    field.setAccessible(true);
                    flds.add(field);
                }
                fields = flds.toArray(new Field[flds.size()]); 
            } else {
                fields = new Field[0];
            }

            // We can't use the same method name in all classes cause it creates an endless loop cause when super.init is called it calls back to this method
            makeMethod = clazz.getMethod(clazz.getName().replace(".", "$") + "$" + DeserializerThunk.CONSTRUCTOR_METHOD_NAME_FOR_REFLECTION, SerialData.class);
            makeMethod.setAccessible(true);
        }

        @Override
        protected <T> T deserializeBody(Class<?> clazz, T obj, int i, X10JavaDeserializer jds) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
            for (Field field : fields) {
                Object value = jds.readRefUsingReflection();
                field.set(obj, value);
            }

            SerialData serialData = (SerialData) jds.readRefUsingReflection();
            makeMethod.invoke(obj, serialData);
            return obj;
        }
    }

    private static class JavaLangStringDeserializerThunk extends DeserializerThunk {

        JavaLangStringDeserializerThunk(Class <? extends Object> clazz) {
            super(null);
        }
        
        @Override
        <T> T deserializeObject(Class<?> clazz, X10JavaDeserializer jds) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
            int i = jds.record_reference(null);
            return deserializeObject(clazz, null, i, jds);
        }

        @Override
        <T> T deserializeObject(Class<?> clazz, T obj, int i, X10JavaDeserializer jds) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
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
                        String message = (String)jds.readRef();
                        Field detailMessageField = java.lang.Throwable.class.getDeclaredField("detailMessage");
                        detailMessageField.setAccessible(true);
                        detailMessageField.set(obj, message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (X10JavaSerializer.THROWABLES_SERIALIZE_STACKTRACE) {
                    java.lang.StackTraceElement[] trace = (java.lang.StackTraceElement[]) jds.readArrayUsingReflection(java.lang.StackTraceElement.class);
                    java.lang.Throwable t = (java.lang.Throwable) obj;
                    t.setStackTrace(trace);
                }
                if (X10JavaSerializer.THROWABLES_SERIALIZE_CAUSE) {
                    try {
                        java.lang.Throwable cause = (java.lang.Throwable) jds.readRef();
                        Field causeField = java.lang.Throwable.class.getDeclaredField("cause");
                        causeField.setAccessible(true);
                        causeField.set(obj, cause);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return obj;
            } else if ("java.lang.Class".equals(clazz.getName())) {
                String className = jds.readString();
                try {
                    T t = (T) Class.forName(className);
                    jds.update_reference(i, t);
                    return t;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else if ("java.lang.Object".equals(clazz.getName())) {
                return obj;
            }
            throw new RuntimeException("Unhandled type in special case thunk: "+obj.getClass());
        }
    }
}