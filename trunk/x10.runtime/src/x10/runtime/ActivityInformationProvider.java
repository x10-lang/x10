/*
 * Created on Oct 12, 2004
 */
package x10.runtime;

import x10.lang.Activity;

/**
 * Interface for the service that provides information about
 * activities, notably which activity object is responsible for
 * the current execution.
 * 
 * @author Christian Grothoff
 */
public interface ActivityInformationProvider {

    /**
     * Get the Activity object that is associated with the
     * execution of this method.
     * @return
     */
    public Activity getCurrentActivity();
    
} // end of ActivityInformationProvider
