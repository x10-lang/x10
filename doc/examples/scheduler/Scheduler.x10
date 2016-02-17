import x10.util.ArrayList;

/**
  Limitations:
  - Each @code{async} must be launched in the scope of a @code{finish}.

  - Activities must cooperate. For example, in the following program,
    the message "shy" may not be printed:

    val sched = new Scheduler.Sequential()
    sched.finish {
      sched.async { Console.OUT.println("shy"); }
      while (true) {}
    }

  - The termination of a finish cannot depend on the termination of a
    finish which is not nested into it.

 - Warning: each @code{async} is associated with a finish of the same
   scheduler. Here is an example:

   val sched1 = new Scheduler.Sequential()
   val sched2 = new Scheduler.Sequential()
   finish { // "finish 0"
       sched1.finish { // "finish 1"
           sched2.finish {
               sched1.async { } // associated to "finish 1"
               async { } // associated to "finish 0"
           }
       }
   }

*/
class Scheduler {

    /**
     * The @code{UnboundedAsync} exception is thrown when
     * an @code{async} is executed outside of the scope of
     * a @code{finish}.
     */
    public static class UnboundedAsync extends Exception {}

    /**
     * The @code{Deadlock} exception can be thrown if the scheduler
     * detects a deadlock.
     */
    public static class Deadlock extends Exception {}


    /**
     * Scheduler where all the activities are executed in the same
     * thread. Calls to the @code{async} method must be executed in
     * the same place as the enclosing @code{finish}. For instance,
     * the following code is incorrect:
     *   val sched = new Scheduler.LocalSequential();
     *   sched.finish {
     *       at(here.next()) { sched.async { Console.OUT.println("ERROR"); } }
     *   }
     */
    public static class LocalSequential {

        static class FinishState {
            var activeAsyncs: Long = 0;
            public def startAsync() { activeAsyncs++; }
            public def stopAsync() { activeAsyncs--; }
        }

        class Activity {
            public val ctrlFinish: FinishState;
            public val body: ()=>void;
            public def this(ctrlFinish: FinishState, body: ()=>void) {
                this.ctrlFinish = ctrlFinish;
                this.body = body;
            }
            public def run() {
                currentFinish = ctrlFinish;
                body();
            }
        }

        private static class ActivitySet {
            val state: ArrayList[Activity] = new ArrayList[Activity]();
            public def remove() {
                return state.removeLast();
            }
            public def add(a: Activity) {
                state.addBefore(0, a);
            }
        }

        private var currentFinish: FinishState = null;
        private val todo: ActivitySet = new ActivitySet();

        private def sched() {
            val activity: Activity;
            try {
                activity = todo.remove();
            } catch (Exception) { throw new Deadlock(); }
            activity.run();
        }

        public operator finish (body: ()=>void) {
            val parentFinish = currentFinish;
            val state = new FinishState();
            currentFinish = state;
            body();
            while (state.activeAsyncs > 0) {
                sched();
            };
            currentFinish = parentFinish;
        }

        public operator async (body: ()=>void) {
            if (currentFinish == null) {
                throw new UnboundedAsync ();
            }
            currentFinish.startAsync();
            todo.add(new Activity(currentFinish,
                                  ()=> { body(); currentFinish.stopAsync(); }));
        }

    }


    /**
     * Scheduler where all the activities are executed in the same
     * thread. Calls to the @code{async} method can be executed in
     * a different places than the enclosing @code{finish}. For instance,
     * the following code is correct:
     *   val sched = new Scheduler.Sequential();
     *   sched.finish {
     *       at(here.next()) { sched.async { Console.OUT.println("OK"); } }
     *   }
     */
    public static class Sequential {

        static class FinishState {
            val activeAsyncs: GlobalRef[Cell[Long]] = new GlobalRef[Cell[Long]](new Cell[Long](0));
            public def startAsync() { activeAsyncs.evalAtHome((cell:Cell[Long])=> { cell() = cell() + 1; true }); }
            public def stopAsync() { activeAsyncs.evalAtHome((cell:Cell[Long])=> { cell() = cell() - 1; true }); }
        }

        class Activity {
            public val ctrlFinish: FinishState;
            public val body: ()=>void;
            public def this(ctrlFinish: FinishState, body: ()=>void) {
                this.ctrlFinish = ctrlFinish;
                this.body = body;
            }
            public def run() {
                currentFinish() = ctrlFinish;
                body();
            }
        }

        private static class ActivitySet {
            val state = new GlobalRef(new ArrayList[Activity]());
            public def remove() {
                return state.evalAtHome((state:ArrayList[Activity])=>{ return state.removeLast(); });
            }
            public def add(a: Activity) {
                state.evalAtHome((state:ArrayList[Activity])=>{ state.addBefore(0, a); true });
            }
        }

        private val currentFinish: GlobalCell[FinishState] = GlobalCell.make(null as FinishState);
        private val todo: ActivitySet = new ActivitySet();

        private def sched() {
            val activity: Activity;
            try {
                activity = todo.remove();
            } catch (Exception) { throw new Deadlock(); }
            activity.run();
        }

        public operator finish (body: ()=>void) {
            val parentFinish = currentFinish();
            val state = new FinishState();
            currentFinish() = state;
            body();
            while (state.activeAsyncs()() > 0) {
                sched();
            };
            currentFinish() = parentFinish;
        }

        public operator async (body: ()=>void) {
	    val current = currentFinish();
            if (current == null) {
                throw new UnboundedAsync ();
            }
            current.startAsync();
            todo.add(new Activity(current,
                                  ()=> { body(); current.stopAsync(); }));
        }

    }


    /********************/
    public static def main(Rail[String]) {
	{
	    val sched = new Scheduler.LocalSequential();
	    sched.finish {
		at(here) { sched.async { Console.OUT.println("ERROR"); } }
	    }
	}
	// {
	//     val sched = new Scheduler.Sequential();
	//     sched.finish {
	// 	at(here) { sched.async { Console.OUT.println("OK"); } }
	//     }
	// }
    }

}
