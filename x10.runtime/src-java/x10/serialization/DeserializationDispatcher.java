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

public class DeserializationDispatcher implements SerializationConstants {
    
    // TODO: Remove as soon as as all calls are removed from generated and handwritten code.
    public static short addDispatcher(Class<?> clazz) {
        return NO_PREASSIGNED_ID;
     }
    
    // TODO: Remove as soon as as all calls are removed from generated and handwritten code.
    public static short addDispatcher(Class<?> clazz, String name) {
        return NO_PREASSIGNED_ID;
     }

    public static void registerHandlers() {
        x10.x10rt.MessageHandlers.registerHandlers();
    }

}
