/*
 * Created on Oct 12, 2004
 */
package x10.runtime;

import x10.lang.Activity;
import x10.lang.Place;

/**
 * Interface for the service that provides information about
 * activities, notably which activity object is responsible for
 * the current execution.
 * 
 * @author Christian Grothoff
 */
interface ActivityInformationProvider {

    /**
     * Get the Activity object that is associated with the
     * execution of this method.
     * @return
     */
    public Activity getCurrentActivity();
    
    /**
     * Notify the asl via a callback whenever the given activity
     * starts another Activity (via async, future or now).
     */
    public void registerActivitySpawnListener(Activity i,
                                              ActivitySpawnListener asl);

    /**
     * At which place is the given activity running?
     * 
     * @param a
     * @return null if the activity is not running anywhere
     */
    public Place getPlaceOfActivity(Activity a);
    
} // end of ActivityInformationProvider
