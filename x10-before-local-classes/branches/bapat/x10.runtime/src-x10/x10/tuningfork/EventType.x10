/*
 *
 * (C) Copyright IBM Corporation 2009
 *
 *  This file is part of X10 runtime. It is 
 *  governed by the licence under which 
 *  X10 is released.
 *
 */
package x10.tuningfork;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * An EventType describes the types and attributes of the data values 
 * associated with a particular event index.
 */
@NativeRep("java", "com.ibm.tuningfork.tracegen.types.EventType", null, null)
public value EventType {

    public native def this(name:String, description:String);
    public native def this(name:String, description:String, attribute:EventAttribute);

    // Corresponds to the constructor <String, String, EventAttribute[]>
    @Native("java", "new com.ibm.tuningfork.tracegen.types.EventType(#1, #2, (com.ibm.tuningfork.tracegen.types.EventAttribute[])((#3).value))")
    public static native def make(name:String, description:String,
			          attributes:Rail[EventAttribute]):EventType;

}
