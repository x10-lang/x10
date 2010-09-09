/*
* Created on Oct 13, 2004
*/
package x10.runtime;


/**
* Listener on events where a specified activity spawns other
* (asynchronous) activities.  For the registered activities,
* the client is also notified once they complete.
* 
* @author Christian Grothoff
*/
interface ActivitySpawnListener {

   /**
    * Function called to send a notification about the
    * creation of an activity.
    * 
    * @param a the new activity
    * @param i the parant activity spawning a (never null)
    */
   public void notifyActivitySpawn(Activity a,
                                   Activity i);
   
   /**
    * The activity that was registered for the creation of
    * sub-activities has terminated (and hence the registration
    * has been revoked, too).
    * 
    * @param a the activity that was registered for monitoring and
    *   which now is no more
    */
   public void notifyActivityTerminated(Activity a);
   
} // end of ActivitySpawnListener
