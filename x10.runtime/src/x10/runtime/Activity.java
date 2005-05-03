package x10.runtime;

import java.util.ArrayList;
import java.util.Stack;

/**
 * @author Christian Grothoff, Christoph von Praun
 */
public abstract class Activity implements Runnable {

    /**
     * The place on which this activity runs. 
     * At what place is the given thread running (needed to support
     * running multiple Places within the same VM). Note that we're
     * using a weak hash map here since the threadpool code may decide
     * to reduce the number of threads by just letting one exit, and
     * we would in that case not want to hold on to the memory.
     * This is field is used by the default runtime to manage activities. 
     */
    protected Place place_;
    
    /**
     * Exception collector for this activity. This is field is used by the default
     * runtime to manage activities.  The stack is a stack of Throwables.
     */
    protected Stack/* <Throwable> */ finish_;
    
    
    /**
    * What listeners are registered for termination/spawning events
    * for the given activity?  Vector<ActivitySpawnListener>
    */
    protected ArrayList/* <ActivitySpawnListener> */ asl_;
    
    /**
     * This is an Activity that has a result in the form of a Future.
     */
    public static abstract class Expr extends Activity {
        
        public Future_c future; 
        /**
         * Wait for the completion of this activity and return the
         * return value.
         */
        public abstract x10.lang.Object getResult();
        
    } // end of Activity.Expr

} // end of Activity