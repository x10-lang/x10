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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import x10.core.ThrowableUtilities;
import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

public abstract class InitDispatcher {

    public static final int UNINITIALIZED = 0;
    public static final int INITIALIZING = 1;
    public static final int INITIALIZED = 2;

    private static List<Method> initializeMethods = new ArrayList<Method>();
    private static List<Method> deserializeMethods = new ArrayList<Method>();
    private static int fieldId = 0;

    private static final String initializerPrefix = "getInitialized$";
    private static final String deserializerPrefix = "getDeserialized$";

    /**
     * Executed only in place 0
     */
    static class $Closure$Initialize implements x10.core.fun.VoidFun_0_0 {
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
        public x10.rtt.RuntimeType<?> $getRTT() {
            return $RTT;
        }
        public x10.rtt.Type<?> $getParam(int i) {
            return null;
        }

        public void $_serialize(X10JavaSerializer serializer) throws IOException {
            throw new UnsupportedOperationException("Cannot serialize " + getClass());
        }

        public int $_get_serialization_id() {
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

    public static int addInitializer(String className, String fieldName) {
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

            Method deserializer = clazz.getMethod(deserializerPrefix+fieldName, byte[].class);
            deserializeMethods.add(deserializer);

            fieldId++;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new java.lang.Error(e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new java.lang.Error(e);
        }
        return fieldId-1;
    }

    public static class $Closure$Deserialize implements x10.core.fun.VoidFun_0_0 {
    	public int fieldId;
    	public byte[] buf;
        private static final int _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(InitDispatcher.$Closure$Deserialize.class);

        public void $apply() {
            // execute deserializer for fieldValue
            try {
                deserializeMethods.get(fieldId).invoke(null, buf);
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
        $Closure$Deserialize(int fieldId, byte[] buf) {
            this.fieldId = fieldId;
            this.buf = buf;
        }
        public x10.rtt.RuntimeType<?> $getRTT() {
            return $RTT;
        }
        public x10.rtt.Type<?> $getParam(int i) {
            return null;
        }

        public void $_serialize(X10JavaSerializer serializer) throws IOException {
            serializer.write(fieldId);
            serializer.write(buf);
        }

        public int $_get_serialization_id() {
            return _serialization_id;
        }

        public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException {
            $Closure$Deserialize closure$Deserialize = new $Closure$Deserialize();
            deserializer.record_reference(closure$Deserialize);
            return $_deserialize_body(closure$Deserialize, deserializer);
        }

        public static X10JavaSerializable $_deserialize_body($Closure$Deserialize closure$Deserialize, X10JavaDeserializer deserializer) throws IOException {
            int id = deserializer.readInt();
            byte[] bytes = deserializer.readByteArray();
            closure$Deserialize.fieldId = id;
            closure$Deserialize.buf = bytes;
            return (X10JavaSerializable) closure$Deserialize;
        }
    }
    public static <T> void broadcastStaticField(T fieldValue, final int fieldId) {
    	// no need for broadcast while running on a single place
    	if (Runtime.MAX_PLACES <= 1) {
    		return;
    	}
    	
        // if (X10RT.VERBOSE) System.out.println("@MultiVM: broadcastStaticField(id="+fieldId+"):"+fieldValue);

        // serialize to bytearray

        final byte[] buf = serializeField(fieldValue);
        
        // create a deserialization closure
        x10.core.fun.VoidFun_0_0 body = new $Closure$Deserialize(fieldId, buf);
        
        // Invoke the closure at all places except here
        Runtime.runAtAll(false, body);
    }
    // for Emitter.mangleSignedNumeric
    public static void broadcastStaticField$s1(final Object fieldValue, final int fieldId) {
        // no need for broadcast while running on a single place
        if (Runtime.MAX_PLACES <= 1) {
                return;
        }
        
        // if (X10RT.VERBOSE) System.out.println("@MultiVM: broadcastStaticField(id="+fieldId+"):"+fieldValue);

        // serialize to bytearray
        final byte[] buf = serializeField(fieldValue);
        
        // create a deserialization closure
        x10.core.fun.VoidFun_0_0 body = new $Closure$Deserialize(fieldId, buf);
        
        // Invoke the closure at all places except here
        Runtime.runAtAll(false, body);
    }

    public static void broadcastStaticField(int fieldValue, final int fieldId) {
    	// no need for broadcast while running on a single place
    	if (Runtime.MAX_PLACES <= 1) {
    		return;
    	}

        final byte[] buf;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream oos = new DataOutputStream(baos);
            X10JavaSerializer serializer = new X10JavaSerializer(oos);
            serializer.write(fieldValue);
            oos.close();
            buf = baos.toByteArray();
        } catch (IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Exception(e);
            xe.printStackTrace();
            throw xe;
        }
        // create a deserialization closure
        x10.core.fun.VoidFun_0_0 body = new $Closure$Deserialize(fieldId, buf);
        // Invoke the closure at all places except here
        Runtime.runAtAll(false, body);
    }

    public static void broadcastStaticField(double fieldValue, final int fieldId) {
    	// no need for broadcast while running on a single place
    	if (Runtime.MAX_PLACES <= 1) {
    		return;
    	}

        final byte[] buf;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream oos = new DataOutputStream(baos);
            X10JavaSerializer serializer = new X10JavaSerializer(oos);
            serializer.write(fieldValue);
            oos.close();
            buf = baos.toByteArray();
        } catch (IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Exception(e);
            xe.printStackTrace();
            throw xe;
        }
        // create a deserialization closure
        x10.core.fun.VoidFun_0_0 body = new $Closure$Deserialize(fieldId, buf);
        // Invoke the closure at all places except here
        Runtime.runAtAll(false, body);
    }

    public static void broadcastStaticField(float fieldValue, final int fieldId) {
    	// no need for broadcast while running on a single place
    	if (Runtime.MAX_PLACES <= 1) {
    		return;
    	}

        final byte[] buf;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream oos = new DataOutputStream(baos);
            X10JavaSerializer serializer = new X10JavaSerializer(oos);
            serializer.write(fieldValue);
            oos.close();
            buf = baos.toByteArray();
        } catch (IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Exception(e);
            xe.printStackTrace();
            throw xe;
        }
        // create a deserialization closure
        x10.core.fun.VoidFun_0_0 body = new $Closure$Deserialize(fieldId, buf);
        // Invoke the closure at all places except here
        Runtime.runAtAll(false, body);
    }

    public static void broadcastStaticField(long fieldValue, final int fieldId) {
    	// no need for broadcast while running on a single place
    	if (Runtime.MAX_PLACES <= 1) {
    		return;
    	}

        final byte[] buf;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream oos = new DataOutputStream(baos);
            X10JavaSerializer serializer = new X10JavaSerializer(oos);
            serializer.write(fieldValue);
            oos.close();
            buf = baos.toByteArray();
        } catch (IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Exception(e);
            xe.printStackTrace();
            throw xe;
        }
        // create a deserialization closure
        x10.core.fun.VoidFun_0_0 body = new $Closure$Deserialize(fieldId, buf);
        // Invoke the closure at all places except here
        Runtime.runAtAll(false, body);
    }

    public static void broadcastStaticField(short fieldValue, final int fieldId) {
    	// no need for broadcast while running on a single place
    	if (Runtime.MAX_PLACES <= 1) {
    		return;
    	}

        final byte[] buf;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream oos = new DataOutputStream(baos);
            X10JavaSerializer serializer = new X10JavaSerializer(oos);
            serializer.write(fieldValue);
            oos.close();
            buf = baos.toByteArray();
        } catch (IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Exception(e);
            xe.printStackTrace();
            throw xe;
        }
        // create a deserialization closure
        x10.core.fun.VoidFun_0_0 body = new $Closure$Deserialize(fieldId, buf);
        // Invoke the closure at all places except here
        Runtime.runAtAll(false, body);
    }

    public static void broadcastStaticField(byte fieldValue, final int fieldId) {
    	// no need for broadcast while running on a single place
    	if (Runtime.MAX_PLACES <= 1) {
    		return;
    	}

        final byte[] buf;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream oos = new DataOutputStream(baos);
            X10JavaSerializer serializer = new X10JavaSerializer(oos);
            serializer.write(fieldValue);
            oos.close();
            buf = baos.toByteArray();
        } catch (IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Exception(e);
            xe.printStackTrace();
            throw xe;
        }
        // create a deserialization closure
        x10.core.fun.VoidFun_0_0 body = new $Closure$Deserialize(fieldId, buf);
        // Invoke the closure at all places except here
        Runtime.runAtAll(false, body);
    }

    public static void broadcastStaticField(char fieldValue, final int fieldId) {
    	// no need for broadcast while running on a single place
    	if (Runtime.MAX_PLACES <= 1) {
    		return;
    	}

        final byte[] buf;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream oos = new DataOutputStream(baos);
            X10JavaSerializer serializer = new X10JavaSerializer(oos);
            serializer.write(fieldValue);
            oos.close();
            buf = baos.toByteArray();
        } catch (IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Exception(e);
            xe.printStackTrace();
            throw xe;
        }
        // create a deserialization closure
        x10.core.fun.VoidFun_0_0 body = new $Closure$Deserialize(fieldId, buf);
        // Invoke the closure at all places except here
        Runtime.runAtAll(false, body);
    }

    public static void broadcastStaticField(boolean fieldValue, final int fieldId) {
    	// no need for broadcast while running on a single place
    	if (Runtime.MAX_PLACES <= 1) {
    		return;
    	}

        final byte[] buf;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream oos = new DataOutputStream(baos);
            X10JavaSerializer serializer = new X10JavaSerializer(oos);
            serializer.write(fieldValue);
            oos.close();
            buf = baos.toByteArray();
        } catch (IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Exception(e);
            xe.printStackTrace();
            throw xe;
        }
        // create a deserialization closure
        x10.core.fun.VoidFun_0_0 body = new $Closure$Deserialize(fieldId, buf);
        // Invoke the closure at all places except here
        Runtime.runAtAll(false, body);
    }

    public static void broadcastStaticField(String fieldValue, final int fieldId) {
    	// no need for broadcast while running on a single place
    	if (Runtime.MAX_PLACES <= 1) {
    		return;
    	}

        final byte[] buf;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream oos = new DataOutputStream(baos);
            X10JavaSerializer serializer = new X10JavaSerializer(oos);
            serializer.write(fieldValue);
            oos.close();
            buf = baos.toByteArray();
        } catch (IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Exception(e);
            xe.printStackTrace();
            throw xe;
        }
        // create a deserialization closure
        x10.core.fun.VoidFun_0_0 body = new $Closure$Deserialize(fieldId, buf);
        // Invoke the closure at all places except here
        Runtime.runAtAll(false, body);
    }

    private static byte[] serializeField(Object object) {
        try {
            return Runtime.serialize(object);
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Exception(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    public static Object deserializeField(byte[] buf) {
        try {
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(buf);
            if (X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) {
                java.io.DataInputStream in = new java.io.DataInputStream(bais);
                X10JavaDeserializer deserializer = new X10JavaDeserializer(in);
                Object o = deserializer.readRef();
                in.close();
                return o;
            }
            java.io.ObjectInputStream in = new java.io.ObjectInputStream(bais);
            Object val = in.readObject();
            in.close();
            return val;
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Exception(e);
            xe.printStackTrace();
            throw xe;
        } catch (ClassNotFoundException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Exception(e);
            xe.printStackTrace();
            throw xe;            
        }
    }

    public static int deserializeInt(byte[] buf) {
        try {
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(buf);
            DataInputStream in = new DataInputStream(bais);
            X10JavaDeserializer deserializer = new X10JavaDeserializer(in);
            int i = deserializer.readInt();
            in.close();
            return i;
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Exception(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    public static double deserializeDouble(byte[] buf) {
        try {
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(buf);
            DataInputStream in = new DataInputStream(bais);
            X10JavaDeserializer deserializer = new X10JavaDeserializer(in);
            double v = deserializer.readDouble();
            in.close();
            return v;
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Exception(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    public static float deserializeFloat(byte[] buf) {
        try {
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(buf);
            DataInputStream in = new DataInputStream(bais);
            X10JavaDeserializer deserializer = new X10JavaDeserializer(in);
            float v = deserializer.readFloat();
            in.close();
            return v;
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Exception(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    public static long deserializeLong(byte[] buf) {
        try {
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(buf);
            DataInputStream in = new DataInputStream(bais);
            X10JavaDeserializer deserializer = new X10JavaDeserializer(in);
            long v = deserializer.readLong();
            in.close();
            return v;
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Exception(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    public static short deserializeShort(byte[] buf) {
        try {
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(buf);
            DataInputStream in = new DataInputStream(bais);
            X10JavaDeserializer deserializer = new X10JavaDeserializer(in);
            short v = deserializer.readShort();
            in.close();
            return v;
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Exception(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    public static byte deserializeByte(byte[] buf) {
        try {
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(buf);
            DataInputStream in = new DataInputStream(bais);
            X10JavaDeserializer deserializer = new X10JavaDeserializer(in);
            byte v = deserializer.readByte();
            in.close();
            return v;
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Exception(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    public static char deserializeChar(byte[] buf) {
        try {
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(buf);
            DataInputStream in = new DataInputStream(bais);
            X10JavaDeserializer deserializer = new X10JavaDeserializer(in);
            char v = deserializer.readChar();
            in.close();
            return v;
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Exception(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    public static boolean deserializeBoolean(byte[] buf) {
        try {
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(buf);
            DataInputStream in = new DataInputStream(bais);
            X10JavaDeserializer deserializer = new X10JavaDeserializer(in);
            boolean v = deserializer.readBoolean();
            in.close();
            return v;
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Exception(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    public static String deserializeString(byte[] buf) {
        try {
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(buf);
            DataInputStream in = new DataInputStream(bais);
            X10JavaDeserializer deserializer = new X10JavaDeserializer(in);
            String v = deserializer.readString();
            in.close();
            return v;
        } catch (java.io.IOException e) {
            x10.core.Throwable xe = ThrowableUtilities.getCorrespondingX10Exception(e);
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
}
