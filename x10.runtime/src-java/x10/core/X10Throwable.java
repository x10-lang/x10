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

package x10.core;

import x10.array.Array;
import x10.array.Point;
import x10.lang.Iterator;
import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.Types;
import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

import java.io.IOException;
import java.lang.String;

// XTENLANG-2686: Now x10/lang/Throwable.x10 is mapped to x10/core/X10Throwable.java which extends x10/core/Throwable.java.
//                This makes it possible to have non-X10-catchable exceptions (e.g. UnknownJavaException) under x10.core.Throwable.
public class X10Throwable extends x10.core.Throwable implements RefI {

    private static final long serialVersionUID = 1L;
    static {
        x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, X10Throwable.class, "x10.lang.Throwable");
    }
    private static short _serialization_id;

    // constructor just for allocation
    public X10Throwable(java.lang.System[] $dummy) {
        super();
    }

    public X10Throwable $init() {return this;}
    
    public X10Throwable() {
        super();
    }

    // TODO
    // public X10Throwable $init(java.lang.String message) {return this;}
    
    public X10Throwable(java.lang.String message) {
        super(message);
    }

    // TODO
    // public X10Throwable $init(java.lang.Throwable cause) {return this;}
    
    public X10Throwable(java.lang.Throwable cause) {
        super(cause);
    }

    // TODO
    // public X10Throwable $init(java.lang.String message, java.lang.Throwable cause) {return this;}
    
    public X10Throwable(java.lang.String message, java.lang.Throwable cause) {
        super(message, cause);
    }

    public static final RuntimeType<X10Throwable> $RTT = new NamedType<X10Throwable>(
        "x10.lang.Throwable",
        X10Throwable.class,
        new Type[] { Types.OBJECT }
    );
    public RuntimeType<?> $getRTT() {
        return $RTT;
    }
    public Type<?> $getParam(int i) {
        return null;
    }

    public void $_serialize(X10JavaSerializer serializer) throws IOException {
        serializer.write(getMessage());
        // TODO ideally we should serialize cause but its java.lang.Throwable which makes it tough
//        serializer.write(getCause());
        serializer.write($getStackTrace());
    }

    public short $_get_serialization_id() {
        return _serialization_id;
    }

    public static void $_set_serialization_id(short id) {
         _serialization_id = id;
    }

    public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException {
        X10Throwable t = new X10Throwable((java.lang.System[])null);
        deserializer.record_reference(t);
        return $_deserialize_body(t, deserializer);
	}

    public static X10JavaSerializable $_deserialize_body(X10Throwable t, X10JavaDeserializer deserializer) throws IOException {
        String message = deserializer.readString();
        t.message = message;
        x10.array.Array<java.lang.String> stackTrace = (Array<String>) deserializer.readRef();
        StackTraceElement[] elements;
        if (stackTrace != null) {
            elements = new StackTraceElement[stackTrace.size];
            Iterator<Point> iterator = stackTrace.iterator();
            int i = 0;
            while (iterator.hasNext$O()) {
                String s = stackTrace.$apply$G(iterator.next$G());
                int index = s.indexOf("(");
                String classAndMethod = s.substring(0, index);
                int index2 = classAndMethod.lastIndexOf(".");
                String className = classAndMethod.substring(0, index2);
                String methodName = classAndMethod.substring(index2 + 1);
                String fileAndLine = s.substring(index + 1, s.length() - 1);
                String[] strings = fileAndLine.split(":");
                String fileName = strings[0];
                int lineNumber = 0;
                if (strings.length > 1) {
                    lineNumber = Integer.parseInt(strings[1]);
                }
                elements[i] = new StackTraceElement(className, methodName, strings[0], lineNumber);
                i++;
            }
        } else {
            elements = new StackTraceElement[0];
        }
        t.setStackTrace(elements);
        return t;
    }

}
