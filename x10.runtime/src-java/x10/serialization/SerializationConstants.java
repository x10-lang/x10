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

/**
 * Constants used in the X10 serialization/deserialization protocol
 */
public interface SerializationConstants {
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
    
    public static final short RTT_ANY_ID = 10;
    public static final short RTT_BOOLEAN_ID = 11;
    public static final short RTT_BYTE_ID = 12;
    public static final short RTT_CHAR_ID = 13;
    public static final short RTT_DOUBLE_ID = 14;
    public static final short RTT_FLOAT_ID = 15;
    public static final short RTT_INT_ID = 16;
    public static final short RTT_LONG_ID = 17;
    public static final short RTT_SHORT_ID = 18;
    public static final short RTT_STRING_ID = 19;
    public static final short RTT_UBYTE_ID = 20;
    public static final short RTT_UINT_ID = 21;
    public static final short RTT_ULONG_ID = 22;
    public static final short RTT_USHORT_ID = 23;
    
    public static final short MAX_HARDCODED_ID = 23;
    
    public static final short NO_PREASSIGNED_ID = MAX_HARDCODED_ID + 1;
    public static final short FIRST_SHARED_ID = NO_PREASSIGNED_ID + 1;
    
    public static final short FIRST_DYNAMIC_ID = 1000;

    public static final short REPEATED_OBJECT_ID = Short.MAX_VALUE;
    public static final short JAVA_OBJECT_STREAM_ID = REPEATED_OBJECT_ID - 1;
    public static final short JAVA_ARRAY_ID = REPEATED_OBJECT_ID - 2;
    public static final short CUSTOM_SERIALIZATION_END = REPEATED_OBJECT_ID - 3;
    public static final short DYNAMIC_ID_ID = REPEATED_OBJECT_ID - 4;
    public static final short RESET_OBJECT_GRAPH_BOUNDARY_ID = REPEATED_OBJECT_ID -5;
}
