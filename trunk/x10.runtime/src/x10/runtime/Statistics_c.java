/*
 * Created on Oct 23, 2004
 */
package x10.runtime;

import java.util.HashMap;

import x10.lang.Activity;

/**
 * Class that gathers various run-time statistics.
 * 
 * @author Christian Grothoff
 */
public class Statistics_c {

    /**
     * The 'global' Statistics_c object.
     */
    public static Statistics_c _ = new Statistics_c();
    
    /**
     * Activate gathering statistics.  Called by the Runtime during the
     * initialization process.
     */
    static void boot() { /* triggers static initializer! */ }
    
    /**
     * Map of collector.class objects to the values returned by the
     * corresponding Collector.activate() methods.
     */
    private final HashMap collection_ = new HashMap(); // <Class,Collector>
    
    /**
     * Create the statistics gatherer.  Will automatically register
     * all active statistics collectors that are defined as inner
     * classes of this class.
     */
    public Statistics_c() {
        Class[] inners = this.getClass().getDeclaredClasses();
        for (int i=0;i<inners.length;i++) {
            Class c = inners[i];
            try {
                Collector coll = (Collector) c.newInstance();                
                if (coll.isActive())
                    install(coll);
            } catch (ClassCastException cce) {
                /* ok, not a collector */
            } catch (IllegalAccessException iae) {
                /* oh, well, can happen, ignore! */
            } catch (InstantiationException ie) {
                /* can happen, ignore! */
            }
        }
    }
    
    /**
     * Install the given statistics collector.
     * 
     * @param c
     */
    public void install(Collector c) {
        collection_.put(c.getClass(), c.activate());
    }

    public String toString() {
        return collection_.toString(); // for now, may nicer later...
    }

    /**
     * Get the collector that corresponds to the given class.
     * @param c the class of the collector
     * @return null if no such collector is installed, otherwise
     *  the object returned by the collector's activate method
     */
    public Object getCollector(Class c) {
        return collection_.get(c);
    }
    
    /**
     * Base-class for all statistics plugins.
     * 
     * @author Christian Grothoff
     */
    public static abstract class Collector {
        protected Collector() {            
        }        
        /**
         * Is this plugin active?  Checks with the configuration if this
         * plugin was disabled by the user.
         * @return true if the plugin is enabled
         */
        public boolean isActive() {
            if (Configuration.STATISTICS_DISABLE.equals("all"))
                return false;
            if (Configuration.STATISTICS_DISABLE.equals("none"))
                return true;
            return -1 == Configuration.STATISTICS_DISABLE.lastIndexOf(this.getClass().getName().replaceAll("$", "."));
        }
        /**
         * Return an object that describes the results collected by
         * this collector.  A typical implementation just returns 'this'.
         * @return an Object of which the toString() method prints useful data about 
         *  the system
         */
        public abstract Object activate();
        
    } // end of Statistics_c.Collector

    /**
     * Class that keeps track of how many activities are currently running in the system
     * and how many have been spawned overall.
     * 
     * @author Christian Grothoff
     */
    public static class ActivityCounter {

        int totalActivityCount = 1;        
        int totalActivitiesRunning = 1;
        
        public Object activate() {
            final DefaultRuntime_c dr = (DefaultRuntime_c)x10.lang.Runtime._;
            dr.registerActivitySpawnListener(dr.getCurrentActivity(),
                                             new ActivitySpawnListener() {
                public void notifyActivitySpawn(Activity a,
                                                Activity i) {
                    totalActivitiesRunning++;
                    totalActivityCount++;
                    dr.registerActivitySpawnListener(a, this);
                }
                public void notifyActivityTerminated(Activity a) {
                    totalActivitiesRunning--;
                }                
            });
            return this;
        }
        public String toString() {
            return "Activities:\n\tTOTAL: " + totalActivityCount + "\n\tRUNNING: " + totalActivitiesRunning + "\n"; 
        }
    
    } // end of Statistics_c.ActivityCounter
    

    /**
     * Class that keeps track of how many activities are created in a 
     * remote place.  This is a measure of how much remote communication
     * there is.
     * 
     * @author Christian Grothoff
     */
    public static class InterPlaceCommunicationCounter {

        int localActivitySpawns = 1;        
        int remoteActivitySpawns = 0;
        
        public Object activate() {
            final DefaultRuntime_c dr = (DefaultRuntime_c)x10.lang.Runtime._;
            dr.registerActivitySpawnListener(dr.getCurrentActivity(),
                                             new ActivitySpawnListener() {
                public void notifyActivitySpawn(Activity a,
                                                Activity i) {
                    if (dr.getPlaceOfActivity(a) == dr.getPlaceOfActivity(i))
                        localActivitySpawns++;
                    else
                        remoteActivitySpawns++;
                    dr.registerActivitySpawnListener(a, this);
                }
                public void notifyActivityTerminated(Activity a) {
                    // we don't care.
                }                
            });
            return this;
        }
        public String toString() {
            return "Activities:\n\tLocal->Local: " + localActivitySpawns + "\n\tRUNNING: " + remoteActivitySpawns + "\n"; 
        }
    
    } // end of Statistics_c.ActivityCounter
    

    
} // end of Statistics_c
