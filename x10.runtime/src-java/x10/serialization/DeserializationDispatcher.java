/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

package x10.serialization;

import x10.runtime.impl.java.Runtime;

import java.io.IOException;
import java.lang.RuntimeException;
import java.lang.Short;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeserializationDispatcher implements SerializationConstants {

    // Should start issuing serializationID's from 1 cause the serializationID 0 is used to indicate a null value.
    // We first increment nextSerializationID before issuing the serializationID hence initialize to NULL_ID
    private static short nextSerializationID = NULL_ID;

    private static List<DeserializationInfo> idToDeserializationInfo = new ArrayList<DeserializationInfo>();
    private static List<Method> idToDeserializermethod = new ArrayList<Method>();
    private static Map<String, Short> classNameToId = new HashMap<String, Short>();

    // Keep track of the asyncs that need to be registered with the X10 RT implementation
    private static List<DeserializationInfo> asyncs = new ArrayList<DeserializationInfo>();
    private static Map<Integer, Short> messageIdToSID = new HashMap<Integer, Short>();

    public static enum ClosureKind {
        CLOSURE_KIND_NOT_ASYNC,    // is not a closure, or is a closure that is not created in place of an async by the desugarer
        CLOSURE_KIND_SIMPLE_ASYNC, // is an async with just finish state
        CLOSURE_KIND_GENERAL_ASYNC // is an async represented with general XRX closure
    };

    public static short addDispatcher(ClosureKind closureKind, Class clazz) {
        if (nextSerializationID == NULL_ID) {
            classNameToId.put(null, nextSerializationID);
            idToDeserializationInfo.add(nextSerializationID, null);
            idToDeserializermethod.add(nextSerializationID, null);
            nextSerializationID++;
            try {
                add(ClosureKind.CLOSURE_KIND_NOT_ASYNC, Class.forName("java.lang.String"), false);
                add(ClosureKind.CLOSURE_KIND_NOT_ASYNC, Class.forName("java.lang.Float"), false);
                add(ClosureKind.CLOSURE_KIND_NOT_ASYNC, Class.forName("java.lang.Double"), false);
                add(ClosureKind.CLOSURE_KIND_NOT_ASYNC, Class.forName("java.lang.Integer"), false);
                add(ClosureKind.CLOSURE_KIND_NOT_ASYNC, Class.forName("java.lang.Boolean"), false);
                add(ClosureKind.CLOSURE_KIND_NOT_ASYNC, Class.forName("java.lang.Byte"), false);
                add(ClosureKind.CLOSURE_KIND_NOT_ASYNC, Class.forName("java.lang.Short"), false);
                add(ClosureKind.CLOSURE_KIND_NOT_ASYNC, Class.forName("java.lang.Long"), false);
                add(ClosureKind.CLOSURE_KIND_NOT_ASYNC, Class.forName("java.lang.Character"), false);
            } catch (ClassNotFoundException e) {
                // This will never happen
            }
        }
        add(closureKind, clazz, true);
        return (short) (nextSerializationID-1);
    }

    public static short addDispatcher(ClosureKind closureKind, Class clazz, String alternate) {
        short serializationID = addDispatcher(closureKind, clazz);
        classNameToId.put(alternate, serializationID);
        return serializationID;
    }

    private static void add(ClosureKind closureKind, Class clazz, boolean addDeserializerMethod) {
        classNameToId.put(clazz.getName(), nextSerializationID);
        DeserializationInfo deserializationInfo = new DeserializationInfo(closureKind, clazz, nextSerializationID);
        idToDeserializationInfo.add(nextSerializationID, deserializationInfo);
        if (deserializationInfo.closureKind != ClosureKind.CLOSURE_KIND_NOT_ASYNC) {
            // This class needs a message ID
            asyncs.add(deserializationInfo);
        }
        if (addDeserializerMethod && !(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()))) {
            idToDeserializermethod.add(nextSerializationID, getDeserializerMethod(clazz));
        } else {
            idToDeserializermethod.add(nextSerializationID, null);
        }
        nextSerializationID++;
    }

    public static Object getInstanceForId(short serializationID, X10JavaDeserializer deserializer) throws IOException {

        if (serializationID == NULL_ID) {
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Deserializing a null reference");
            }
            return null;
        }
        if (serializationID == REF_VALUE) {
            return deserializer.getObjectAtPosition(deserializer.readInt());
        }
        if (serializationID <= MAX_ID_FOR_PRIMITIVE) {
            return deserializePrimitive(serializationID, deserializer);
        }

        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing non-null value with id " + serializationID);
        }
        try {
            Method method = idToDeserializermethod.get(serializationID);
            return method.invoke(null, deserializer);
        } catch (InvocationTargetException e) {
            // This should never happen
            throw new RuntimeException("Error in deserializing non-null value with id " + serializationID, e);
        } catch (SecurityException e) {
            // This should never happen
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            // This should never happen
            throw new RuntimeException(e);
        }
    }

    public static Method getDeserializerMethod(Class<?> clazz) {
        try {
            Method method = clazz.getMethod("$_deserializer", X10JavaDeserializer.class);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            // This should never happen
            throw new RuntimeException(e);
        }
    }

    public static Object deserializePrimitive(short serializationID, X10JavaDeserializer deserializer) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing non-null value with id " + serializationID);
        }
        Object obj = null;
        switch (serializationID) {
            case STRING_ID:
                obj = deserializer.readStringValue();
                break;
            case FLOAT_ID:
                obj = deserializer.readFloat();
                break;
            case DOUBLE_ID:
                obj = deserializer.readDouble();
                break;
            case INTEGER_ID:
                obj = deserializer.readInt();
                break;
            case BOOLEAN_ID:
                obj = deserializer.readBoolean();
                break;
            case BYTE_ID:
                obj = deserializer.readByte();
                break;
            case SHORT_ID:
                obj = deserializer.readShort();
                break;
            case CHARACTER_ID:
                obj = deserializer.readChar();
                break;
            case LONG_ID:
                obj = deserializer.readLong();
                break;
        }
        deserializer.record_reference(obj);
        return obj;
    }

    public static String getClassNameForID(short serializationID, X10JavaDeserializer deserializer) {
         if (serializationID == JAVA_CLASS_ID) {
            try {
                return deserializer.readStringValue();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return getClassForID(serializationID, deserializer).getName();
    }

    public static Class getClassForID(short serializationID, X10JavaDeserializer deserializer) {
        if (serializationID == JAVA_CLASS_ID) {
            try {
                String className = deserializer.readStringValue();
                return Class.forName(className);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return idToDeserializationInfo.get(serializationID).clazz;
    }

    public static short getSerializationIDForClassName(String str) {
        Short serializationID = classNameToId.get(str);

        // When there are inner classes the type name is of the form A.B.C where
        // C is an inner class of B. But classNameToId uses class.getName() to store
        // the class names thus the following code is a workaround for this issue. We
        // need to have the class name cause we do class.forName using this name
        // but the typenames are stored at A.B.C so this disconnect is inevitable
        int i;
        String s = str;
        while (serializationID == null && ((i = s.lastIndexOf(".")) > 0)) {
            s = s.substring(0, i) + "$" + s.substring(i + 1);
            serializationID = classNameToId.get(s);
        }
        if (serializationID == null) {
            return -1;
        }
        return serializationID;
    }

    public static void registerHandlers() {
        x10.x10rt.MessageHandlers.registerHandlers();
    }

    public static int getMessageID(short serializationID) {
        return idToDeserializationInfo.get(serializationID).msgType;
    }

    public static short getSerializationID(int msg_id) {
        return messageIdToSID.get(msg_id);
    }

    public static ClosureKind getClosureKind(short serializationID) {
        return idToDeserializationInfo.get(serializationID).closureKind;
    }

    private static class DeserializationInfo {
        public ClosureKind closureKind;
        public Class<?> clazz;
        public int msgType;
        public short serializationID;

        private DeserializationInfo(ClosureKind closureKind, Class<?> clazz, short serializationID) {
            this.closureKind = closureKind;
            this.clazz = clazz;
            this.serializationID = serializationID;
        }
    }
}
