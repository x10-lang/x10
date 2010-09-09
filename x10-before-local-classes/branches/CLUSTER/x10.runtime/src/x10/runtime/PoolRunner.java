
/**
* Thread in the thread pool that can be used to run multiple
* activities over time.
*
*Moved out of LocalPlace_c by vj.
*Should be replaced by a standard thread pool.
*
* @author Christian Grothoff
* @author vj
*/
package x10.runtime;

import x10.lang.place;

public final  class PoolRunner extends Thread 
	implements ActivityRunner {
   /**
    * For building a linked list of these.
    */
   PoolRunner next;
   private boolean active = true;
   private Runnable job;
   private Activity act;

   
   /* the pace where this runner currently executed - can be different from 
    *  homePlace, e.g inside an array initiizer, see also 
    *  DefaultRuntime_c.setCurrentPlace(place p) */
   public LocalPlace_c place; 
   public LocalPlace_c getPlace() { return place; }
   public void setPlace(place p) { place = (LocalPlace_c) p; }
   
   /* the pace to which this worker thread belongs */
   public final LocalPlace_c homePlace;
   
   PoolRunner(LocalPlace_c p) {
       homePlace = p;
	   place = p;
       //p.addThread( this );
   }
   public Activity getActivity() {
   	return act;
   }
   public void setActivity( Activity a) {
   	this.act = a;
   }

   
   synchronized void shutdown() {
       active = false;
       this.notifyAll();
   }
   /**
    * Assign a new job to this runner and notify it of the change so it can start running again.
    * @param r
    */
   synchronized void run(Runnable r) {
       assert job == null;
       job = r;
       changeRunningStatus(1);
       this.notifyAll();
   }
   /**
    * Change the 'running' status of a thread.
    * @param delta +1 for thread starts to run (unblocked), -1 for thread is blocked
    */
   public void changeRunningStatus(int delta) {
	   homePlace.changeRunningStatus(delta);
   }
   
   /**
    * Run jobs until shutdown is called.
    */
   public synchronized void run() {
       while (active) {
           if (Report.should_report("activity", 5)) {
               Report.report(5, logString() + " waiting for a job.");
           }
           while (active && job == null) {
               try {
                   wait();
               } catch (InterruptedException ie) {
                   throw new Error(ie);
               }
               if (Report.should_report("activity", 5)) {
                   Report.report(5, logString() +" awakes.");
               }
           }
           if (job != null) {
               Runnable j = job;
               job = null;
               try {
                   //changeRunningStatus(1); //Move to run(Runnalbe) to better reflect the state of 'place'
                   if (Report.should_report("activity", 5)) {
                       Report.report(5, logString() + " starts running " + j +".");
                   }
                   j.run();
                   if (Report.should_report("activity", 5)) {
                       Report.report(5, logString() + " finished running " + j +".");
                   }
                   
               } finally {
                   changeRunningStatus(-1);
                   // act.finalizeTermination();
               }
               // notify the LocalPlace_c that we're again available
               // for more work!
               synchronized (homePlace) {
                   if (homePlace.isShutdown()) {
                       if (Report.should_report("activity", 5)) {
                           Report.report(5, logString() + " shuts down.");
                       }                       
                       return;
                   }
                   if (homePlace.shouldRunnerTerminate())
                       active = false;
                   else
                	   homePlace.repool(this);
               }
           }
       }
       if (Report.should_report("activity", 5)) {
           Report.report(5, logString() + " shuts down.");
       }
   }
   
   public String thisThreadString() {
   	return Thread.currentThread() +  "@" + homePlace + ":" + System.currentTimeMillis();
   }
   public static String logString() {
   	return ((PoolRunner) Thread.currentThread()).thisThreadString();
   }
   
   public String toString() {
   	return "<PoolRunner " + hashCode() + ">";
   			
   }
} // end of LocalPlace_c.PoolRunner
