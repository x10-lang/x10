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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeserializationDispatcher {

    public static final int NULL_ID = 0;
    public static final int STRING_ID = 1;
    public static final int FLOAT_ID = 2;
    public static final int DOUBLE_ID = 3;
    public static final int INTEGER_ID = 4;
    public static final int BOOLEAN_ID = 5;
    public static final int BYTE_ID = 6;
    public static final int SHORT_ID = 7;
    public static final int LONG_ID = 8;
    public static final int CHARACTER_ID = 9;
    public static final String NULL_VALUE = "__NULL__";

    private static final int INCREMENT_SIZE = 10;

    // Should start issuing id's from 1 cause the id 0 is used to indicate a null value.
    // We first increment i before issuing the id hence initialize to NULL_ID
    private static int i = NULL_ID;

    private static List<String> idToClassName = new ArrayList<String>();
    private static Map<String, Integer> classNameToId = new HashMap<String, Integer> ();

    public static int addDispatcher(String className) {
        if (i == NULL_ID) {
            add(NULL_VALUE);
            add("java.lang.String");
            add("java.lang.Float");
            add("java.lang.Double");
            add("java.lang.Integer");
            add("java.lang.Boolean");
            add("java.lang.Byte");
            add("java.lang.Short");
            add("java.lang.Character");
        }
        add(className);
        return i-1;
    }

    public static int addDispatcher(String className, String alternate) {
        int i = addDispatcher(className);
        classNameToId.put(alternate,  i);
        return i;
    }

    private static void add(String str) {
        classNameToId.put(str, i);
        idToClassName.add(i, str);
        i++;
    }

    public static Object getInstanceForId(int i, X10JavaDeserializer deserializer) throws IOException {

        if (i == X10JavaDeserializer.ref) {
            return deserializer.getObjectAtPosition(deserializer.readInt());
        } else if (i == NULL_ID) {
            if (Runtime.TRACE_SER) {
                System.out.println("Deserialized a null reference");
            }
            return null;
        } else if (i <=8) {
            if (Runtime.TRACE_SER) {
                System.out.println("Deserializing non-null value with id " + i);
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

        if (Runtime.TRACE_SER) {
            System.out.println("Deserializing non-null value with id " + i);
        }
        final String className = idToClassName.get(i);
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod("$_deserializer", X10JavaDeserializer.class);
            method.setAccessible(true);
            return method.invoke(null, deserializer);
        } catch (ClassNotFoundException e) {
            // This should never happen
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            // This should never happen
            throw new RuntimeException(e);
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

    public static String getClassNameForID(int id) {
        return idToClassName.get(id);
    }

    public static int getIDForClassName(String str) {
        Integer integer = classNameToId.get(str);

        // When there are inner classes the type name is of the form A.B.C where
        // C is an inner class of B. But classNameToId uses class.getName() to store
        // the class names thus the following code is a workaround for this issue. We
        // need to have the class name cause we do class.forName using this name
        // but the typenames are stored at A.B.C so this disconnect is inevitable
        int i;
        String s = str;
        while (integer == null && ((i = s.lastIndexOf(".")) > 0)) {
            s = s.substring(0, i) + "$" + s.substring(i + 1);
            integer = classNameToId.get(s);
        }
        return integer;
    }
}
