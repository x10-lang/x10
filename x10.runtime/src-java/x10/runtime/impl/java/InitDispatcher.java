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

package x10.runtime.impl.java;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import x10.rtt.RuntimeType;
import x10.rtt.Type;

import x10.core.ThrowableUtilities;
import x10.x10rt.DeserializationDispatcher;
import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

public abstract class InitDispatcher {

    public static final int UNINITIALIZED = 0;
    public static final int INITIALIZING = 1;
    public static final int INITIALIZED = 2;

    private static List<Method> initializeMethods = new ArrayList<Method>();
    private static List<Method> deserializeMethods = new ArrayList<Method>();
    private static short fieldId = 0;

    // N.B. must be sync with StaticInitializer.java and InitDispatcher.java
    private static final String initializerPrefix = "getInitialized$";
    private static final String deserializerPrefix = "getDeserialized$";

    static final short static_broadcast__serialization_id = init();

    static short init() {
        short id = DeserializationDispatcher.addDispatcher(DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$Deserialize.class);
        DeserializationDispatcher.setStaticInitializer(id);
        return id;
    }

    /**
     * Executed only in place 0
     */
    static class $Closure$Initialize implements x10.core.fun.VoidFun_0_0 {
        private static final long serialVersionUID = 1L;
    	private final Method initializer;
        public void $apply() {
            // execute X10-level static initialization
            try {
                initializer.invoke(null, (Object[])null);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                throw new java.lang.Error(e);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new java.lang.Error(e);
            }
        }
        $Closure$Initialize(Method initializer) {
        	this.initializer = initializer;
        }
        public RuntimeType<?> $getRTT() { return $RTT; }
        public Type<?> $getParam(int i) { return null; }

        public void $_serialize(X10JavaSerializer serializer) throws IOException {
            throw new UnsupportedOperationException("Cannot serialize " + getClass());
        }

        public short $_get_serialization_id() {
            throw new UnsupportedOperationException("Cannot serialize " + getClass());
        }
    }
    public static void runInitializer() {
        if (Runtime.TRACE_SER) {
            System.out.println("There are " + initializeMethods.size() + " initializers to run");
        }
        for (final Method initializer : initializeMethods) {
            // System.out.println("runInitializer executes " + initializer.getName());
        	// create an initialization closure
        	x10.core.fun.VoidFun_0_0 body = new $Closure$Initialize(initializer);
            // execute the closure asynchronously to resolve any dependencies
            x10.lang.Runtime.runAsync(body);
        }
        // static initialization all finished
        fieldId = -1;
        if (Runtime.TRACE_SER) {
            System.out.println("====================== STATIC INITIALIZATION DONE ======================");
        }
    }

    public static short addInitializer(String className, String fieldName) {
        if (fieldId < 0) {
            System.err.println("Adding initializer too late! : " + className + "." + fieldName);
            System.exit(-1);
        }

        // System.out.println("addInitializer(id="+fieldId+"): "+className+"."+fieldName);
        try {
            Class<?> clazz = Class.forName(className);
            // register initializer and deserializer methods
            Method initializer = clazz.getMethod(initializerPrefix+fieldName, (Class<?>[])null);
            initializeMethods.add(initializer);

            Method deserializer = clazz.getMethod(deserializerPrefix+fieldName, X10JavaDeserializer.class);
            deserializeMethods.add(deserializer);

            fieldId++;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new java.lang.Error(e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new java.lang.Error(e);
        }
        return (short) (fieldId-1);
    }

    public static class $Closure$Deserialize implements x10.core.fun.VoidFun_0_0 {
        private static final long serialVersionUID = 1L;
    	public short fieldId;
    	public X10JavaDeserializer x10JavaDeserializer;

        public void $apply() {
            // execute deserializer for fieldValue
            try {
                deserializeMethods.get(fieldId).invoke(null, x10JavaDeserializer);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                throw new java.lang.Error(e);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new java.lang.Error(e);
            }
        }

        // Just for allocation
        $Closure$Deserialize() {
        }

        public RuntimeType<?> $getRTT() { return $RTT; }
        public Type<?> $getParam(int i) { return null; }

        // This is not meant to be serialized.
        public void $_serialize(X10JavaSerializer serializer) throws IOException {
            throw new UnsupportedOperationException("Cannot serialize " + getClass());
        }

        public short $_get_serialization_id() {
            return static_broadcast__serialization_id;
        }

        public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException {
            $Closure$Deserialize closure$Deserialize = new $Closure$Deserialize();

            // We explicitly do not record the reference here cause this class is not serialized as is.
            // It is serialized in a special way where only the contents of this class get serialized
            return $_deserialize_body(closure$Deserialize, deserializer);
        }

        public static X10JavaSerializable $_deserialize_body($Closure$Deserialize closure$Deserialize, X10JavaDeserializer deserializer) throws IOException {
            short id = deserializer.readShort();
            closure$Deserialize.fieldId = id;

            // We do not serialize the actual field value here. It gets deserialized later on by invoking the
            // corresponding deserialize method by the $apply method above.
            closure$Deserialize.x10JavaDeserializer = deserializer;
            return (X10JavaSerializable) closure$Deserialize;
        }
    }
    public static <T> void broadcastStaticField(T fieldValue, final short fieldId) {
    	// no need for broadcast while running on a single place
    	if (Runtime.MAX_PLACES <= 1) {
    		return;
    	}

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream oos = new DataOutputStream(baos);
        X10JavaSerializer serializer = new X10JavaSerializer(oos);
        try {
            serializer.write(fieldId);
            if (fieldValue instanceof X10JavaSerializable) {
                serializer.write((X10JavaSerializable) fieldValue);
            } else {
                serializer.write(fieldValue);
            }
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new java.lang.Error(e);
        }
        byte [] ba = baos.toByteArray();

        // [GlobalGC] Add speculative increment of remoteCounts of serialized GlobalRefs since the serialized data is used more than once
        x10.core.GlobalRef.adjustRemoteCountsInMap(serializer.getGrefMap(), Runtime.MAX_PLACES-1 -1);
        
        // Invoke the closure at all places except here
        Runtime.runAtAll(false, ba, static_broadcast__serialization_id);
    }

    public static void broadcastStaticField(int fieldValue, final short fieldId) {
    	// no need for broadcast while running on a single place
    	if (Runtime.MAX_PLACES <= 1) {
    		return;
    	}

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream oos = new DataOutputStream(baos);
        X10JavaSerializer serializer = new X10JavaSerializer(oos);
        try {
            serializer.write(fieldId);
            serializer.write(fieldValue);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new java.lang.Error(e);
        }
        byte [] ba = baos.toByteArray();

        // Invoke the closure at all places except here
        Runtime.runAtAll(false, ba, static_broadcast__serialization_id);
    }

    public static void broadcastStaticField(double fieldValue, final short fieldId) {
    	// no need for broadcast while running on a single place
    	if (Runtime.MAX_PLACES <= 1) {
    		return;
    	}

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream oos = new DataOutputStream(baos);
        X10JavaSerializer serializer = new X10JavaSerializer(oos);
        try {
            serializer.write(fieldId);
            serializer.write(fieldValue);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new java.lang.Error(e);
        }
        byte [] ba = baos.toByteArray();

        // Invoke the closure at all places except here
        Runtime.runAtAll(false, ba, static_broadcast__serialization_id);
    }

    public static void broadcastStaticField(float fieldValue, final short fieldId) {
    	// no need for broadcast while running on a single place
    	if (Runtime.MAX_PLACES <= 1) {
    		return;
    	}

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream oos = new DataOutputStream(baos);
        X10JavaSerializer serializer = new X10JavaSerializer(oos);
        try {
            serializer.write(fieldId);
            serializer.write(fieldValue);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new java.lang.Error(e);
        }
        byte [] ba = baos.toByteArray();

        // Invoke the closure at all places except here
        Runtime.runAtAll(false, ba, static_broadcast__serialization_id);
    }

    public static void broadcastStaticField(long fieldValue, final short fieldId) {
    	// no need for broadcast while running on a single place
    	if (Runtime.MAX_PLACES <= 1) {
    		return;
    	}

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream oos = new DataOutputStream(baos);
        X10JavaSerializer serializer = new X10JavaSerializer(oos);
        try {
            serializer.write(fieldId);
            serializer.write(fieldValue);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new java.lang.Error(e);
        }
        byte [] ba = baos.toByteArray();

        // Invoke the closure at all places except here
        Runtime.runAtAll(false, ba, static_broadcast__serialization_id);
    }

    public static void broadcastStaticField(short fieldValue, final short fieldId) {
    	// no need for broadcast while running on a single place
    	if (Runtime.MAX_PLACES <= 1) {
    		return;
    	}

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream oos = new DataOutputStream(baos);
        X10JavaSerializer serializer = new X10JavaSerializer(oos);
        try {
            serializer.write(fieldId);
            serializer.write(fieldValue);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new java.lang.Error(e);
        }
        byte [] ba = baos.toByteArray();

        // Invoke the closure at all places except here
        Runtime.runAtAll(false, ba, static_broadcast__serialization_id);
    }

    public static void broadcastStaticField(byte fieldValue, final short fieldId) {
    	// no need for broadcast while running on a single place
    	if (Runtime.MAX_PLACES <= 1) {
    		return;
    	}

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream oos = new DataOutputStream(baos);
        X10JavaSerializer serializer = new X10JavaSerializer(oos);
        try {
            serializer.write(fieldId);
            serializer.write(fieldValue);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new java.lang.Error(e);
        }
        byte [] ba = baos.toByteArray();

        // Invoke the closure at all places except here
        Runtime.runAtAll(false, ba, static_broadcast__serialization_id);
    }

    public static void broadcastStaticField(char fieldValue, final short fieldId) {
    	// no need for broadcast while running on a single place
    	if (Runtime.MAX_PLACES <= 1) {
    		return;
    	}

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream oos = new DataOutputStream(baos);
        X10JavaSerializer serializer = new X10JavaSerializer(oos);
        try {
            serializer.write(fieldId);
            serializer.write(fieldValue);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new java.lang.Error(e);
        }
        byte [] ba = baos.toByteArray();

        // Invoke the closure at all places except here
        Runtime.runAtAll(false, ba, static_broadcast__serialization_id);
    }

    public static void broadcastStaticField(boolean fieldValue, final short fieldId) {
    	// no need for broadcast while running on a single place
    	if (Runtime.MAX_PLACES <= 1) {
    		return;
    	}

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream oos = new DataOutputStream(baos);
        X10JavaSerializer serializer = new X10JavaSerializer(oos);
        try {
            serializer.write(fieldId);
            serializer.write(fieldValue);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new java.lang.Error(e);
        }
        byte [] ba = baos.toByteArray();

        // Invoke the closure at all places except here
        Runtime.runAtAll(false, ba, static_broadcast__serialization_id);
    }

    public static void broadcastStaticField(String fieldValue, final short fieldId) {
    	// no need for broadcast while running on a single place
    	if (Runtime.MAX_PLACES <= 1) {
    		return;
    	}

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream oos = new DataOutputStream(baos);
        X10JavaSerializer serializer = new X10JavaSerializer(oos);
        try {
            serializer.write(fieldId);
            serializer.write(fieldValue);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new java.lang.Error(e);
        }
        byte [] ba = baos.toByteArray();

        // Invoke the closure at all places except here
        Runtime.runAtAll(false, ba, static_broadcast__serialization_id);
    }

    public static void broadcastStaticFieldUsingReflection(Object fieldValue, final short fieldId) {
    	// no need for broadcast while running on a single place
    	if (Runtime.MAX_PLACES <= 1) {
    		return;
    	}

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream oos = new DataOutputStream(baos);
        X10JavaSerializer serializer = new X10JavaSerializer(oos);
        try {
            serializer.write(fieldId);
            serializer.writeObjectUsingReflection(fieldValue);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new java.lang.Error(e);
        }
        byte [] ba = baos.toByteArray();

        // Invoke the closure at all places except here
        Runtime.runAtAll(false, ba, static_broadcast__serialization_id);
    }

    public static Object deserializeField(byte[] buf) {
        try {
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(buf);
            java.io.DataInputStream in = new java.io.DataInputStream(bais);
            X10JavaDeserializer deserializer = new X10JavaDeserializer(in);
            Object o = deserializer.readRef();
            in.close();
            return o;
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Throwable(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    public static Object deserializeField(X10JavaDeserializer deserializer) {
        try {
            return deserializer.readRef();
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Throwable(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    public static int deserializeInt(X10JavaDeserializer deserializer) {
        try {
            int i = deserializer.readInt();
            return i;
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Throwable(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    public static double deserializeDouble(X10JavaDeserializer deserializer) {
        try {
            double v = deserializer.readDouble();
            return v;
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Throwable(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    public static float deserializeFloat(X10JavaDeserializer deserializer) {
        try {
            float v = deserializer.readFloat();
            return v;
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Throwable(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    public static long deserializeLong(X10JavaDeserializer deserializer) {
        try {
            long v = deserializer.readLong();
            return v;
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Throwable(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    public static short deserializeShort(X10JavaDeserializer deserializer) {
        try {
            short v = deserializer.readShort();
            return v;
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Throwable(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    public static byte deserializeByte(X10JavaDeserializer deserializer) {
        try {
            byte v = deserializer.readByte();
            return v;
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Throwable(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    public static char deserializeChar(X10JavaDeserializer deserializer) {
        try {
            char v = deserializer.readChar();
            return v;
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Throwable(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    public static boolean deserializeBoolean(X10JavaDeserializer deserializer) {
        try {
            boolean v = deserializer.readBoolean();
            return v;
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Throwable(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    public static String deserializeString(X10JavaDeserializer deserializer) {
        try {
            String v = deserializer.readString();
            return v;
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Throwable(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    public static Object deserializeFieldUsingReflection(X10JavaDeserializer deserializer) {
        try {
            Object v = deserializer.readRefUsingReflection();
            return v;
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Throwable(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    public static void lockInitialized() {
        x10.lang.Runtime.StaticInitBroadcastDispatcherLock();
    }

    public static void unlockInitialized() {
        x10.lang.Runtime.StaticInitBroadcastDispatcherUnlock();
    }

    public static void awaitInitialized() {
        x10.lang.Runtime.StaticInitBroadcastDispatcherAwait();
    }

    public static void notifyInitialized() {
        x10.lang.Runtime.StaticInitBroadcastDispatcherNotify();
    }

    public static void printStaticInitMessage(String message) {
        Runtime.printStaticInitMessage(message);
    }

    public static final boolean TRACE_STATIC_INIT = Runtime.TRACE_STATIC_INIT;
}
