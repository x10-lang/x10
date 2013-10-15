/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2013.
 */

package x10.serialization;

class SerializationUtils {
    static boolean useX10SerializationProtocol(Class<?> clazz) {
        if (!x10.serialization.X10JavaSerializable.class.isAssignableFrom(clazz)) return false;
        
        // Now look for the serialization/deserialization methods to be sure it
        // really implements the protocol.
        try {
            clazz.getDeclaredMethod("$_deserialize_body", clazz, X10JavaDeserializer.class);
            clazz.getDeclaredMethod("$_deserializer", X10JavaDeserializer.class);
            clazz.getMethod("$_serialize", X10JavaSerializer.class);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

}
