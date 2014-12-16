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

package x10;

/**
 * Constants used in X10 compiler that refer to the X10 Runtime.
 * 
 * @author Christian Grothoff
 */
public interface RuntimeConstants {
    
    /**
     * Name of the method of an x10 Activity that contains the code that is
     * to be executed for this activity.
     */
    public static final String ACTIVITY_RUN = "run"; 

    public static final String ACTIVITY_GET_RESULT = "getResult";

    public static final String PLACE_RUN_ACTIVATION_ASYNC = "runAsync";

    public static final String PLACE_RUN_ACTIVATION_FUTURE = "runFuture";
 
    
}
