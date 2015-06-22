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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.Serializable;

/**
 * This class is used in conjunction with the writeReplace
 * methods defined on subclasses of Ref and Struct to
 * allow X10 objects to be serialized by Java middleware
 * frameworks that use java.io.Serializable while still mainly
 * using the X10 serialization implementation to write/read
 * object graphs to/from byte[].
 */
@SuppressWarnings("serial")
public class SerializationProxy implements Serializable {
    final byte[] data;
    
    public SerializationProxy(Object that) {
        X10JavaSerializer ser = new X10JavaSerializer();
        ser.writeAny(that);
        data = ser.getDataBytes();
    }
    
    private Object readResolve() throws java.io.ObjectStreamException {
        X10JavaDeserializer deser = new X10JavaDeserializer(new DataInputStream(new ByteArrayInputStream(data)));
        return deser.readAny();
    }
}
