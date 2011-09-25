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

package x10.x10rt;

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

public class DeserializationDispatcher {

    public static final short NULL_ID = 0;
    public static final short STRING_ID = 1;
    public static final short FLOAT_ID = 2;
    public static final short DOUBLE_ID = 3;
    public static final short INTEGER_ID = 4;
    public static final short BOOLEAN_ID = 5;
    public static final short BYTE_ID = 6;
    public static final short SHORT_ID = 7;
    public static final short LONG_ID = 8;
    public static final short CHARACTER_ID = 9;
    public static final String NULL_VALUE = "__NULL__";

    public static final short refValue = Short.MAX_VALUE;
    public static final short javaClassID = refValue - 1;

    // Should start issuing id's from 1 cause the id 0 is used to indicate a null value.
    // We first increment i before issuing the id hence initialize to NULL_ID
    private static short i = NULL_ID;

    private static List<DeserializationInfo> idToDeserializationInfo = new ArrayList<DeserializationInfo>();
    private static List<Method> idToDeserializermethod = new ArrayList<Method>();
    private static Map<String, Short> classNameToId = new HashMap<String, Short> ();

    // Keep track of the asyncs that need to be registered with the X10 RT implementation
    private static List<DeserializationInfo> asyncs = new ArrayList<DeserializationInfo>();
    private static Map<Integer, Short> messageIdToSID = new HashMap<Integer, Short> ();

    public static enum ClosureKind {
        CLOSURE_KIND_NOT_ASYNC,    // is not a closure, or is a closure that is not created in place of an async by the desugarer
        CLOSURE_KIND_SIMPLE_ASYNC, // is an async with just finish state
        CLOSURE_KIND_GENERAL_ASYNC // is an async represented with generial XRX closure
    };

    public static short addDispatcher(ClosureKind closureKind, Class clazz) {
        if (i == NULL_ID) {
            classNameToId.put(null, i);
            idToDeserializationInfo.add(i, null);
            idToDeserializermethod.add(i, null);
            i++;
            try {
                add(ClosureKind.CLOSURE_KIND_NOT_ASYNC, Class.forName("java.lang.String"), false);
                add(ClosureKind.CLOSURE_KIND_NOT_ASYNC, Class.forName("java.lang.Float"), false);
                add(ClosureKind.CLOSURE_KIND_NOT_ASYNC, Class.forName("java.lang.Double"), false);
                add(ClosureKind.CLOSURE_KIND_NOT_ASYNC, Class.forName("java.lang.Integer"), false);
                add(ClosureKind.CLOSURE_KIND_NOT_ASYNC, Class.forName("java.lang.Boolean"), false);
                add(ClosureKind.CLOSURE_KIND_NOT_ASYNC, Class.forName("java.lang.Byte"), false);
                add(ClosureKind.CLOSURE_KIND_NOT_ASYNC, Class.forName("java.lang.Short"), false);
                add(ClosureKind.CLOSURE_KIND_NOT_ASYNC, Class.forName("java.lang.Character"), false);
            } catch (ClassNotFoundException e) {
                // This will never happen
            }
        }
        add(closureKind, clazz, true);
        return (short) (i-1);
    }

    public static short addDispatcher(ClosureKind closureKind, Class clazz, String alternate) {
        short i = addDispatcher(closureKind, clazz);
        classNameToId.put(alternate, i);
        return i;
    }

    private static void add(ClosureKind closureKind, Class clazz, boolean addDeserializerMethod) {
        classNameToId.put(clazz.getName(), i);
        DeserializationInfo deserializationInfo = new DeserializationInfo(closureKind, clazz, i);
        idToDeserializationInfo.add(i, deserializationInfo);
        if (deserializationInfo.closureKind != ClosureKind.CLOSURE_KIND_NOT_ASYNC) {
            // This class needs a message ID
            asyncs.add(deserializationInfo);
        }
        if (addDeserializerMethod && !(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()))) {
            idToDeserializermethod.add(i, getDeserializerMethod(clazz));
        } else {
            idToDeserializermethod.add(i, null);
        }
        i++;
    }

    public static Object getInstanceForId(short i, X10JavaDeserializer deserializer) throws IOException {

        if (i == refValue) {
            return deserializer.getObjectAtPosition(deserializer.readInt());
        } else if (i == NULL_ID) {
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Deserialized a null reference");
            }
            return null;
        } else if (i <=8) {
            return deserializePrimitive(i, deserializer);
        }

        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing non-null value with id " + i);
        }
        try {
            Method method = idToDeserializermethod.get(i);
            return method.invoke(null, deserializer);
        } catch (InvocationTargetException e) {
            // This should never happen
            throw new RuntimeException(e);
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

    public static Object deserializePrimitive(short i, X10JavaDeserializer deserializer) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing non-null value with id " + i);
        }
        Object obj = null;
        switch(i) {
            case STRING_ID:
                 obj =  deserializer.readStringValue();
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
            case  CHARACTER_ID:
                 obj = deserializer.readChar();
                break;
            case LONG_ID:
                 obj = deserializer.readLong();
                break;
        }
        deserializer.record_reference(obj);
        return obj;
    }

    public static String getClassNameForID(short id, X10JavaDeserializer deserializer) {
         if (id == javaClassID) {
            try {
                return deserializer.readStringValue();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return getClassForID(id, deserializer).getName();
    }

    public static Class getClassForID(short id, X10JavaDeserializer deserializer) {
        if (id == javaClassID) {
            try {
                String className = deserializer.readStringValue();
                return Class.forName(className);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return idToDeserializationInfo.get(id).clazz;
    }

    public static short getIDForClassName(String str) {
        Short val = classNameToId.get(str);

        // When there are inner classes the type name is of the form A.B.C where
        // C is an inner class of B. But classNameToId uses class.getName() to store
        // the class names thus the following code is a workaround for this issue. We
        // need to have the class name cause we do class.forName using this name
        // but the typenames are stored at A.B.C so this disconnect is inevitable
        int i;
        String s = str;
        while (val == null && ((i = s.lastIndexOf(".")) > 0)) {
            s = s.substring(0, i) + "$" + s.substring(i + 1);
            val = classNameToId.get(s);
        }
        if (val == null) {
            return -1;
        }
        return val;
    }

    public static void registerHandlers() {
        x10.x10rt.MessageHandlers.registerHandlers(asyncs.size());
    }

    public static void registerHandlersCallback(int[] ids) {
        int j = 0;
       for (DeserializationInfo deserializationInfo : asyncs) {
           deserializationInfo.msgType = ids[j];
           messageIdToSID.put(deserializationInfo.msgType, deserializationInfo.sid);
           j++;
       }
       // We no longer need this data structure
        asyncs = null;
    }

    public static int getMessageID(short sid) {
        return idToDeserializationInfo.get(sid).msgType;
    }

    public static short getSerializationID(int msg_id) {
        return messageIdToSID.get(msg_id);
    }

    public static ClosureKind getClosureKind(short sid) {
        return idToDeserializationInfo.get(sid).closureKind;
    }

    public static void setStaticInitializer(short sid) {
        idToDeserializationInfo.get(sid).isStaticInitializer = true;
    }

    public static boolean isStaticInitializer(short sid) {
        return idToDeserializationInfo.get(sid).isStaticInitializer;
    }

    private static class DeserializationInfo {
        public ClosureKind closureKind;
        public Class clazz;
        public int msgType;
        public short sid;

        // We need to deserialize static initializers using custom serialization, this is a marker to know whether
        // this is a static serialization
        public boolean isStaticInitializer = false;

        private DeserializationInfo(ClosureKind closureKind, Class clazz, short sid) {
            this.closureKind = closureKind;
            this.clazz = clazz;
            this.sid = sid;
        }
    }
}
