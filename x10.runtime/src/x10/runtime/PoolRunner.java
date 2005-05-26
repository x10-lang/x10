
/**
* Thread in the thread pool that can be used to run multiple
* activities over time.
*
*Moved out of LocalPlace_c by vj.
*Should be replaced by a standard thread pool.
* @author Christian Grothoff
*/
package x10.runtime;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final  class PoolRunner extends Thread // LoadMonitored 
	implements ActivityRunner { // if you do NOT want load monitoring, make 'extend Thread'
   /**
    * For building a linked list of these.
    */
   PoolRunner next;
   private boolean active = true;
   private Runnable job;
   private Activity act;
   private Method ac;
   private Object vmto;
   LocalPlace_c place; // not final, may be set by DefaultRuntime_c.setCurrentPlace(place p)
   
   PoolRunner(LocalPlace_c p) {
       place = p;
       p.addThread( this );
       
       try {
           Field vmt = java.lang.Thread.class.getDeclaredField("vmdata");
           vmt.setAccessible(true);
           this.vmto = vmt.get(this); // o is 'VM_Thread'
           this.ac = vmto.getClass().getDeclaredMethod("accumulateCycles", new Class[0]);
           setName("PoolRunner" + hashCode());
       } catch (SecurityException se) {
           // System.out.println("GSPT: " + se);
       } catch (IllegalAccessException iae) {
           // System.out.println("GSPT: " + iae);
       } catch (NoClassDefFoundError ncfe) {
           // System.out.println("GSPT: " + ncfe);
       } catch (NoSuchMethodException ncme) {
           // System.out.println("GSPT: " + ncfe);
       } catch (NoSuchFieldException ncfe) {
           // System.out.println("GSPT: " + ncfe);
       }
   }
   public Activity getActivity() {
   	return act;
   }
   public void setActivity( Activity a) {
   	this.act = a;
   }

   /**
    * On JikesRVM, get the total number of CPU cycles spend in this
    * thread.  We use reflection mostly because we want this to
    * still link (!) and compile under other VMs.  
    * @return 0 on error
    */
   /*package*/ long getThreadRunTime() {
       if (ac == null)
           return 0;
       try {
           // Field vmt = java.lang.Thread.class.getDeclaredField("vmdata");
           // vmt.setAccessible(true);
           // Object o = vmt.get(this); // o is 'VM_Thread'
           // Field trt = o.getClass().getDeclaredField("totalCycles");
           // trt.setAccessible(true);
           // return trt.getLong(o);                
           return ((Long)ac.invoke(vmto, new Object[0])).longValue();                
       } catch (SecurityException se) {
           // System.out.println("GSPT: " + se);
       } catch (IllegalAccessException iae) {
           // System.out.println("GSPT: " + iae);
       } catch (NoClassDefFoundError ncfe) {
           // System.out.println("GSPT: " + ncfe);
       } catch (InvocationTargetException ite) {
           // System.out.println("GSPT: " + ncfe);
       }/* catch (NoSuchFieldException nsfe) {
           // System.out.println("GSPT: " + nsfe);
           // not JikesRVM
       }*/
       return 0;   
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
       this.notifyAll();
   }
   /**
    * Change the 'running' status of a thread.
    * @param delta +1 for thread starts to run (unblocked), -1 for thread is blocked
    */
   public void changeRunningStatus(int delta) {
       place.changeRunningStatus(delta);
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
                   changeRunningStatus(1);
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
               synchronized (place) {
                   if (place.isShutdown()) {
                   	if (Report.should_report("activity", 5)) {
                   		Report.report(5, logString() + " shuts down.");
                   	}
                   	
                       return;
                   }
                   place.repool(this);
               }
           }
       }
       if (Report.should_report("activity", 5)) {
   		Report.report(5, logString() + " shuts down.");
   	}
   }
   
   public String thisThreadString() {
   	return Thread.currentThread() +  "@" + place + ":" + System.currentTimeMillis();
   }
   public static String logString() {
   	return ((PoolRunner) Thread.currentThread()).thisThreadString();
   }
   
   public String toString() {
   	return "<PoolRunner " + hashCode() + ">";
   			
   }
} // end of LocalPlace_c.PoolRunner
