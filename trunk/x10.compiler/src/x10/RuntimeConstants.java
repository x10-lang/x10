// Licensed Materials - Property of IBM
// (C) Copyright IBM Corporation 2004-2008. All Rights Reserved. 
// Note to U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP  Schedule Contract with IBM Corp. 
//                                                                             
// --------------------------------------------------------------------------- 

/*
 * Created on Oct 2, 2004
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
