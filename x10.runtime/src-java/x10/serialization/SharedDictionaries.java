/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

package x10.serialization;

import java.lang.reflect.Method;

import x10.runtime.impl.java.Runtime;

/**
 * Commonly serialized types whose serialization ids are
 * known at all places and therefore do not have to be assigned
 * on a per-message basis.
 * 
 * We start with an initial set of built-in types, that are extensible 
 * by the user on the command line by setting the property x10.ADDITIONAL_SHARED_TYPES.  
 * 
 * TODO: Future work:
 * The design is intended to support future dynamic optimization where the 
 * serialization subsystem can use profiling to identify commonly 
 * serialized types and use a multi-phase protocol to promote such
 * types into the shared dictionary.
 */
class SharedDictionaries {
    
    private static boolean initialized = false;
    private static SerializationDictionary serializationDict;
    private static DeserializationDictionary deserializationDict;    
   
    private static synchronized void doInitialization() {
        if (initialized) return;
        
        serializationDict = new SerializationDictionary(SerializationConstants.FIRST_SHARED_ID);
        deserializationDict = new DeserializationDictionary();
        
        String[] builtInTypes = new String[] {
                /* Core class library: x10.lang */
                "x10.lang.Cell",
                "x10.lang.Clock",
                "x10.lang.Complex",
                /* "x10.lang.GlobalRef",  */           // TODO: @NativeRep to x10.core.GlobalRef
                "x10.lang.PlaceLocalHandle",
                "x10.lang.Place",

                /* XRX: core implementation classes */
                "x10.lang.FinishState",
                "x10.lang.FinishState$Finish",
                "x10.lang.Runtime$RemoteControl",
                
                /* Core class library: x10.array */
                "x10.array.Array",
                "x10.array.DistArray",
                "x10.array.DistArray$LocalState",
                "x10.array.Point",
                "x10.array.RectRegion1D",
                "x10.array.RemoteArray",
               
                /* Core class library: x10.io */
                "x10.io.SerialData",
                
                /* Core class library: x10.util */
                /* "x10.util.IndexedMemoryChunk", */       // TODO: @NativeRep to x10.core.IndexedMemoryChunk
                "x10.util.Pair",
                /* "x10.util.RemoteIndexedMemoryChunk", */ // TODO: @NativeRep to x10.core.RemoteIndexedMemoryChunk
                
                /* Managed X10 implementation classes */
                // TODO: For mixed mode, we need to map these to the
                //       backend neutral concepts (eg, x10.lang.GlobalRef not x10.core.GlobalRef)
                "x10.core.Boolean",
                "x10.core.Byte",
                "x10.core.Char",
                "x10.core.Double",
                "x10.core.Float",
                "x10.core.GlobalRef",
                "x10.core.IndexedMemoryChunk",
                "x10.core.Int",
                "x10.core.Long",
                "x10.core.PlaceLocalHandle",
                "x10.core.RemoteIndexedMemoryChunk",
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
            processType(type);
        }
        
        String userTypeProp = System.getProperty("x10.ADDITIONAL_SHARED_TYPES");
        if (userTypeProp != null) {
            String[] userTypes = userTypeProp.split(":");
            for (String type : userTypes) {
                processType(type);
            }
        }
        
        initialized = true;
    }

    private static void processType(String type) {
        try {
            Class <?> clazz = Class.forName(type);
            short id = serializationDict.getSerializationId(clazz, null, true);
            assert id < SerializationConstants.FIRST_DYNAMIC_ID : "Not enough shared seraialization ids reserved!";
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
    
    static short getSerializationId(Class<?> clazz, Object obj) {
        if (!initialized) doInitialization();
        return serializationDict.getSerializationId(clazz, obj, false);
    }
    
    static Class<?> getClassForID(short sid) {
        if (!initialized) doInitialization();
        assert SerializationConstants.FIRST_SHARED_ID <= sid && sid < SerializationConstants.FIRST_DYNAMIC_ID;
        return deserializationDict.getClassForID(sid);
    }
    
    static Method getMethod(short sid) {
        if (!initialized) doInitialization();
        assert SerializationConstants.FIRST_SHARED_ID <= sid && sid < SerializationConstants.FIRST_DYNAMIC_ID;
        return deserializationDict.getMethod(sid);
    }
}
