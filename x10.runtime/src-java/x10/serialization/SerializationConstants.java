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

/**
 * Constants used in the X10 serialization/deserialization protocol
 */
interface SerializationConstants {
    public static final boolean PER_MESSAGE_IDS = true;
    
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
    public static final short MAX_ID_FOR_PRIMITIVE = 9;
     
    public static final short FIRST_DYNAMIC_ID = 100;

    public static final short REF_VALUE = Short.MAX_VALUE;
    public static final short JAVA_CLASS_ID = REF_VALUE - 1;
    public static final short JAVA_ARRAY_ID = REF_VALUE - 2;
}
