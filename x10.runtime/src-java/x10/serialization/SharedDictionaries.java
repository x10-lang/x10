/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.serialization;

import x10.runtime.impl.java.Runtime;
import x10.serialization.DeserializationDictionary.MasterDeserializationDictionary;
import x10.serialization.SerializationDictionary.MasterSerializationDictionary;

/**
 * Commonly serialized types whose serialization ids are
 * known at all places and therefore do not have to be assigned
 * on a per-message basis.
 * 
 * We start with an initial set of built-in types, that are extensible 
 * by the user on the command line by setting the property x10.ADDITIONAL_SHARED_TYPES.  
 * 
 * As the program executes, new shared ids are assigned dynamically to frequently
 * serialized types.
 */
class SharedDictionaries implements SerializationConstants {
    
    private static boolean initialized = false;
    private static final MasterSerializationDictionary serializationDict = new MasterSerializationDictionary();
    private static final MasterDeserializationDictionary deserializationDict = new MasterDeserializationDictionary();
    
    private static short nextSharedId = FIRST_SHARED_ID;
   
    private static synchronized void doInitialization() {
        if (initialized) return;
        
        // To simplify lookup in X10JavaSerializer, injecting mappings 
        // for all special serialization IDs here.
        serializationDict.addEntry(java.lang.String.class, STRING_ID);
        serializationDict.addEntry(java.lang.Float.class, FLOAT_ID);
        serializationDict.addEntry(java.lang.Double.class, DOUBLE_ID);
        serializationDict.addEntry(java.lang.Integer.class, INTEGER_ID);
        serializationDict.addEntry(java.lang.Boolean.class, BOOLEAN_ID);
        serializationDict.addEntry(java.lang.Byte.class, BYTE_ID);
        serializationDict.addEntry(java.lang.Short.class, SHORT_ID);
        serializationDict.addEntry(java.lang.Long.class, LONG_ID);
        serializationDict.addEntry(java.lang.Character.class, CHARACTER_ID);
        serializationDict.addEntry(x10.rtt.AnyType.class, RTT_ANY_ID);
        serializationDict.addEntry(x10.rtt.BooleanType.class, RTT_BOOLEAN_ID);
        serializationDict.addEntry(x10.rtt.ByteType.class, RTT_BYTE_ID);
        serializationDict.addEntry(x10.rtt.CharType.class, RTT_CHAR_ID);
        serializationDict.addEntry(x10.rtt.DoubleType.class, RTT_DOUBLE_ID);
        serializationDict.addEntry(x10.rtt.FloatType.class, RTT_FLOAT_ID);
        serializationDict.addEntry(x10.rtt.IntType.class, RTT_INT_ID);
        serializationDict.addEntry(x10.rtt.LongType.class, RTT_LONG_ID);
        serializationDict.addEntry(x10.rtt.ShortType.class, RTT_SHORT_ID);
        serializationDict.addEntry(x10.rtt.UByteType.class, RTT_UBYTE_ID);
        serializationDict.addEntry(x10.rtt.UIntType.class, RTT_UINT_ID);
        serializationDict.addEntry(x10.rtt.ULongType.class, RTT_ULONG_ID);
        serializationDict.addEntry(x10.rtt.UShortType.class, RTT_USHORT_ID);
        
        String[] builtInTypes = new String[] {
                /* Core class library: x10.lang */
                "x10.lang.Cell",
                "x10.lang.Clock",
                "x10.lang.Complex",
                /* "x10.lang.GlobalRef",  */           // TODO: @NativeRep to x10.core.GlobalRef
                "x10.lang.PlaceLocalHandle",
                "x10.lang.Place",
                "x10.lang.Point",
                /* "x10.lang.Rail", */                 // TODO: @NativeRep to x10.core.Rail

                /* XRX: core implementation classes */
                "x10.lang.FinishState",
                "x10.lang.FinishState$Finish",
                "x10.lang.Runtime$RemoteControl",
                
                /* Core class library: x10.regionarray */
                "x10.regionarray.Array",
                "x10.regionarray.DistArray",
                "x10.regionarray.DistArray$LocalState",
                "x10.regionarray.RectRegion1D",
                "x10.regionarray.RemoteArray",
               
                /* Core class library: x10.io */
                "x10.io.SerialData",
                
                /* Core class library: x10.util */
                "x10.util.Pair",
                
                /* Managed X10 implementation classes */
                // TODO: For mixed mode, we need to map these to the
                //       backend neutral concepts (eg, x10.lang.GlobalRef not x10.core.GlobalRef)
                "x10.core.Boolean",
                "x10.core.Byte",
                "x10.core.Char",
                "x10.core.Double",
                "x10.core.Float",
                "x10.core.GlobalRef",
                "x10.core.Int",
                "x10.core.Long",
                "x10.core.PlaceLocalHandle",
                "x10.core.Rail",
                "x10.core.Short",
                "x10.core.UByte",
                "x10.core.UInt",
                "x10.core.ULong",
                "x10.core.UShort",
                "x10.rtt.NamedStructType",
                "x10.rtt.NamedType",
                "x10.rtt.ParameterizedType",
                "x10.rtt.RuntimeType",
                
                /* Types that correspond to closures in XRX annotated with @RemoteInvocation */
                /* TODO: This list should be machine generated; this is way too fragile! */
                "x10.lang.FinishState$RemoteFinishSPMD$$Closure_notifyActivityTermination_1",
                "x10.lang.FinishState$RemoteFinishSPMD$$Closure_notifyActivityTermination_2",

                "x10.lang.FinishState$RemoteFinishAsync$$Closure_notifyActivityTermination_1",
                "x10.lang.FinishState$RemoteFinishAsync$$Closure_notifyActivityTermination_2",
                
                "x10.lang.FinishState$RemoteFinish$$Closure_notifyActivityTermination_1",
                "x10.lang.FinishState$RemoteFinish$$Closure_notifyActivityTermination_2",
                "x10.lang.FinishState$RemoteFinish$$Closure_notifyActivityTermination_3",
                "x10.lang.FinishState$RemoteFinish$$Closure_notifyActivityTermination_4",
                "x10.lang.FinishState$RemoteFinish$$Closure_notifyActivityTermination_5",
                "x10.lang.FinishState$RemoteFinish$$Closure_notifyActivityTermination_6",
      
                "x10.lang.FinishState$DenseRemoteFinish$$Closure_notifyActivityTermination_1",
                "x10.lang.FinishState$DenseRemoteFinish$$Closure_notifyActivityTermination_2",
                "x10.lang.FinishState$DenseRemoteFinish$$Closure_notifyActivityTermination_3",
                "x10.lang.FinishState$DenseRemoteFinish$$Closure_notifyActivityTermination_4",
                "x10.lang.FinishState$DenseRemoteFinish$$Closure_notifyActivityTermination_5",
                "x10.lang.FinishState$DenseRemoteFinish$$Closure_notifyActivityTermination_6",
                "x10.lang.FinishState$DenseRemoteFinish$$Closure_notifyActivityTermination_7",
                
                "x10.lang.FinishState$RemoteCollectingFinish$$Closure_notifyActivityTermination_1",
                "x10.lang.FinishState$RemoteCollectingFinish$$Closure_notifyActivityTermination_2",
                "x10.lang.FinishState$RemoteCollectingFinish$$Closure_notifyActivityTermination_3",
                "x10.lang.FinishState$RemoteCollectingFinish$$Closure_notifyActivityTermination_4",
                "x10.lang.FinishState$RemoteCollectingFinish$$Closure_notifyActivityTermination_5",
                "x10.lang.FinishState$RemoteCollectingFinish$$Closure_notifyActivityTermination_6",

                "x10.lang.Runtime$$Closure_start_1",
                "x10.lang.Runtime$$Closure_start_2",
                "x10.lang.Runtime$$Closure_start_3",
                "x10.lang.Runtime$$Closure_runAsync",
                "x10.lang.Runtime$$Closure_runUncountedAsync",
                "x10.lang.Runtime$$Closure_runAt_1",
                "x10.lang.Runtime$$Closure_runAt_2",
                "x10.lang.Runtime$$Closure_runAtSimple_1",
                "x10.lang.Runtime$$Closure_runAtSimple_2",
                "x10.lang.Runtime$$Closure_runAtSimple_3",
                "x10.lang.Runtime$$Closure_evalAt_1",
                "x10.lang.Runtime$$Closure_evalAt_2"
        };
        
        for (String type : builtInTypes) {
            assignIdToType(type, nextSharedId++);
        }
        
        String userTypeProp = System.getProperty("x10.ADDITIONAL_SHARED_TYPES");
        if (userTypeProp != null) {
            String[] userTypes = userTypeProp.split(":");
            for (String type : userTypes) {
                assignIdToType(type, nextSharedId++);
            }
        }
        
        initialized = true;
    }

    static SerializationDictionary getSerializationDictionary() {
        if (!initialized) doInitialization();
        return serializationDict;
    }
    
    static DeserializationDictionary getDeserializationDictionary() {
        if (!initialized) doInitialization();
        return deserializationDict;
    }

    private static void assignIdToType(String type, short id) {
        try {
            // Note: all shared types should be found in classpath and thus Class.forName should work. 
            Class <?> clazz = Class.forName(type);
            assert id < SerializationConstants.FIRST_DYNAMIC_ID : "Not enough shared serialization ids reserved!";
            serializationDict.addEntry(clazz, id);
            deserializationDict.addEntry(id, type);
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Assigned shared serialization id "+id+" to "+type);
            }
        } catch (ClassNotFoundException e) {
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Unable to load common type "+type+". It will not have a preassigned id");
            }
        }
    }
}
