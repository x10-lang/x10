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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import x10.io.CustomSerialization;
import x10.io.SerialData;
import x10.runtime.impl.java.Runtime;

/**
 * An instance of a SerializerThunk knows how to serialize a specific class.
 * The various subclasses of this abstract class encode different cases of
 * the serialization protocol and are matched 1:1 with subclasses of DeserializerThunk.<p>
 *  
 * The motivation for this software architecture is to improve the runtime efficiency 
 * of serialization/deserialization by doing as much of the protocol and meta-data 
 * operations once (at Thunk creation time) and amortizing that cost across all the
 * uses of the Thunk object.
 * 
 * @see DeserializerThunk
 */
abstract class SerializerThunk {

    private static ConcurrentHashMap<Class<?>, SerializerThunk> thunks = new ConcurrentHashMap<Class<?>, SerializerThunk>(50);

    protected final SerializerThunk superThunk;

    protected SerializerThunk(SerializerThunk st) {
        superThunk = st;
    }

    /**
     * Factory method to find or create the SerializerThunk instance for clazz.
     * @param clazz The class for which to find a SerializerThunk instance
     * @return a SerializerThunk instance for clazz
     */
    static SerializerThunk getSerializerThunk(Class<? extends Object> clazz) throws SecurityException, NoSuchFieldException, NoSuchMethodException {
        SerializerThunk ans = thunks.get(clazz);
        if (ans == null) {
            ans = SerializerThunk.getSerializerThunkHelper(clazz);
            thunks.put(clazz, ans);
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Creating serialization thunk "+ans.getClass()+" for "+clazz);
            }
        }
        return ans;
    }

    /**
     * Serialize obj, an instance of clazz, into xjs.
     * 
     * @param obj The object to serialize
     * @param clazz The class of obj
     * @param xjs The serializer into which the object should be serialized.
     * 
     */
    <T> void serializeObject(T obj, Class<? extends Object > clazz, X10JavaSerializer xjs) throws IllegalAccessException, IOException, IllegalArgumentException, InvocationTargetException, SecurityException, NoSuchFieldException {
        if (superThunk != null) {
            superThunk.serializeObject(obj, clazz.getSuperclass(), xjs);
        }
        serializeBody(obj, clazz, xjs);
    }

    abstract <T> void serializeBody(T obj, Class<? extends Object> clazz, X10JavaSerializer xjs) throws IllegalAccessException, IOException, IllegalArgumentException, InvocationTargetException, SecurityException, NoSuchFieldException;

    private static SerializerThunk getSerializerThunkHelper(Class<? extends Object> clazz) throws SecurityException, NoSuchFieldException, NoSuchMethodException {

        // We need to handle these classes in a special way because their 
        // implementation of serialization/deserialization is not straight forward.
        if ("java.lang.String".equals(clazz.getName())) {
            return new SerializerThunk.SpecialCaseSerializerThunk(clazz);
        } else if ("x10.rtt.NamedType".equals(clazz.getName())) {
            SerializerThunk superThunk = getSerializerThunk(clazz.getSuperclass());
            return new SerializerThunk.SpecialCaseSerializerThunk(clazz, superThunk);
        } else if ("x10.rtt.NamedStructType".equals(clazz.getName())) {
            SerializerThunk superThunk = getSerializerThunk(clazz.getSuperclass());
            return new SerializerThunk.SpecialCaseSerializerThunk(clazz, superThunk);
        } else if ("x10.rtt.RuntimeType".equals(clazz.getName())) {
            return new SerializerThunk.SpecialCaseSerializerThunk(clazz);
        } else if ("x10.core.IndexedMemoryChunk".equals(clazz.getName())) {
            return new SerializerThunk.SpecialCaseSerializerThunk(clazz);
        } else if ("x10.core.IndexedMemoryChunk$$Closure$0".equals(clazz.getName())) {
            return new SerializerThunk.SpecialCaseSerializerThunk(clazz);
        } else if ("x10.core.IndexedMemoryChunk$$Closure$1".equals(clazz.getName())) {
            return new SerializerThunk.SpecialCaseSerializerThunk(clazz);
        } else if (x10.core.GlobalRef.class.getName().equals(clazz.getName())) {
            return new SerializerThunk.SpecialCaseSerializerThunk(clazz);
        } else if ("java.lang.Throwable".equals(clazz.getName())) {
            return new SerializerThunk.SpecialCaseSerializerThunk(clazz);
        } else if ("java.lang.Class".equals(clazz.getName())) {
            return new SerializerThunk.SpecialCaseSerializerThunk(clazz);
        } else if ("java.lang.Object".equals(clazz.getName())) {
            return new SerializerThunk.SpecialCaseSerializerThunk(clazz);
        }

        Class<?>[] interfaces = clazz.getInterfaces();
        boolean isCustomSerializable = false;
        boolean isHadoopSerializable = false;
        for (Class<?> aInterface : interfaces) {
            if ("x10.io.CustomSerialization".equals(aInterface.getName())) {
                isCustomSerializable = true;
                break;
            }
        }

        if (Runtime.implementsHadoopWritable(clazz)) {
            isHadoopSerializable = true;
        }

        if (isCustomSerializable && isHadoopSerializable) {
            throw new RuntimeException("serializer: " + clazz + " implements both x10.io.CustomSerialization and org.apache.hadoop.io.Writable.");
        }

        if (isCustomSerializable) {
            return new SerializerThunk.CustomSerializerThunk(clazz);
        }

        if (isHadoopSerializable) {
            return new SerializerThunk.HadoopSerializerThunk(clazz);
        }

        Class<?> superclass = clazz.getSuperclass();
        SerializerThunk superThunk = null;
        if (!("java.lang.Object".equals(superclass.getName()) || "x10.core.Ref".equals(superclass.getName()) || "x10.core.Struct".equals(superclass.getName()))) {
            superThunk = getSerializerThunk(clazz.getSuperclass());
        }

        return new SerializerThunk.FieldBasedSerializerThunk(clazz, superThunk);
    }


    private static class FieldBasedSerializerThunk extends SerializerThunk {
        protected final Field[] fields;

        FieldBasedSerializerThunk(Class<? extends Object> clazz, SerializerThunk st) {
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

        <T> void serializeBody(T obj, Class<? extends Object> clazz, X10JavaSerializer xjs) throws IllegalAccessException, IOException {
            for (Field field : fields) {
                Class<?> type = field.getType();
                if (type.isPrimitive()) {
                    xjs.writePrimitiveUsingReflection(field, obj);
                } else if (type.isArray()) {
                    xjs.writeArrayUsingReflection(field.get(obj));
                } else if ("java.lang.String".equals(type.getName())) {
                    xjs.writeStringUsingReflection(field, obj);
                } else {
                    xjs.writeObjectUsingReflection(field.get(obj));
                }
            }
        }
    }

    private static class HadoopSerializerThunk extends SerializerThunk {
        protected final Method writeMethod;

        HadoopSerializerThunk(Class<? extends Object> clazz) throws SecurityException, NoSuchMethodException {
            super(null);
            writeMethod = clazz.getMethod("write", java.io.DataOutput.class);
            writeMethod.setAccessible(true);
        }

        <T> void serializeBody(T obj, Class<? extends Object> clazz, X10JavaSerializer xjs) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("\tInvoking "+writeMethod);
            }
            writeMethod.invoke(obj, xjs.out);
        }
    }

    private static class CustomSerializerThunk extends SerializerThunk {
        protected final Field[] fields;

        CustomSerializerThunk(Class<? extends Object> clazz) throws SecurityException, NoSuchFieldException {
            super(null);

            // Sort the fields to get JVM-independent ordering.
            // Need to serialize the fields related to RTT's since they
            // are specific to the Java backend.
            Set<Field> flds = new TreeSet<Field>(new FieldComparator());
            TypeVariable<? extends Class<? extends Object>>[] typeParameters = clazz.getTypeParameters();
            for (TypeVariable<? extends Class<? extends Object>> typeParameter: typeParameters) {
                Field field = clazz.getDeclaredField(typeParameter.getName());
                field.setAccessible(true);
                flds.add(field);
            }
            fields = flds.toArray(new Field[flds.size()]);                      
        }

        <T> void serializeBody(T obj, Class<? extends Object> clazz, X10JavaSerializer xjs) throws IllegalArgumentException, IOException, IllegalAccessException {
            for (Field field: fields) {
                xjs.writeObjectUsingReflection(field.get(obj));
            }
            CustomSerialization cs = (CustomSerialization)obj;
            SerialData serialData = cs.serialize();
            xjs.writeObjectUsingReflection(serialData);
        }
    }

    private static class SpecialCaseSerializerThunk extends SerializerThunk {

        SpecialCaseSerializerThunk(Class <? extends Object> clazz) {
            super(null);
        }

        SpecialCaseSerializerThunk(Class <? extends Object> clazz, SerializerThunk st) {
            super(st);
        }

        @SuppressWarnings("rawtypes")
        <T> void serializeBody(T obj, Class<? extends Object> clazz, X10JavaSerializer xjs) throws IOException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
            if ("java.lang.String".equals(clazz.getName())) {
                xjs.writeStringValue((String) obj);
            } else if ("x10.rtt.NamedType".equals(clazz.getName())) {
                Field typeNameField = clazz.getDeclaredField("typeName");
                String typeName = (String) typeNameField.get(obj);
                xjs.writeClassID(typeName);
            } else if ("x10.rtt.NamedStructType".equals(clazz.getName())) {
                Field typeNameField = clazz.getDeclaredField("typeName");
                String typeName = (String) typeNameField.get(obj);
                xjs.writeClassID(typeName);
            } else if ("x10.rtt.RuntimeType".equals(clazz.getName())) {
                Field javaClassField = clazz.getDeclaredField("javaClass");
                Class<?> javaClass = (Class<?>) javaClassField.get(obj);
                xjs.writeClassID(javaClass.getName());
            } else if ("x10.core.IndexedMemoryChunk".equals(clazz.getName())) {
                ((x10.core.IndexedMemoryChunk) obj).$_serialize(xjs);
            } else if ("x10.core.IndexedMemoryChunk$$Closure$0".equals(clazz.getName())) {
                ((x10.core.IndexedMemoryChunk.$Closure$0) obj).$_serialize(xjs);
            } else if ("x10.core.IndexedMemoryChunk$$Closure$1".equals(clazz.getName())) {
                ((x10.core.IndexedMemoryChunk.$Closure$1) obj).$_serialize(xjs);
            } else if (x10.core.GlobalRef.class.getName().equals(clazz.getName())) {
                ((x10.core.GlobalRef) obj).$_serialize(xjs);
            } else if ("java.lang.Throwable".equals(clazz.getName())) {
                java.lang.Throwable t = (java.lang.Throwable) obj;
                if (X10JavaSerializer.THROWABLES_SERIALIZE_MESSAGE) {
                    xjs.write(t.getMessage());
                }
                if (X10JavaSerializer.THROWABLES_SERIALIZE_STACKTRACE) {
                    xjs.writeArrayUsingReflection(t.getStackTrace());
                }
                if (X10JavaSerializer.THROWABLES_SERIALIZE_CAUSE) {
                    xjs.write(t.getCause());
                }
            } else if ("java.lang.Class".equals(clazz.getName())) {
                xjs.write(((Class)obj).getName());
            }
        }
    }
}