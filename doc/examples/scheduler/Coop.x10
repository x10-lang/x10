/*
  Limitations:
  - Each async must be launched in the scope of a finish.
  - The termination of a finish cannot depend on the termination of a
    finish which is not nested into it. For example, the following
    program is incorrect:

    val coop = new Coop();
    val c1 = coop.makeCondition(false);
    val c2 = coop.makeCondition(false);
    val c3 = coop.makeCondition(false);
    coop.finish {
        coop.async {
            coop.when(c1) {}
            coop.atomic { c2 = true; }
            coop.finish {
                coop.when(c3) {}
            }
        }
        finish {
            coop.atomic { c1 = true; }
            coop.when (c2) {}
        };
        coop.atomic { c3 = true; } // This statement will never be executed.
    }
 - Same limitation with when.
*/


import x10.util.*;

class Coop {
    static class Deadlock extends Exception{}

    static class FinishState {
	var activeAsyncs: Long = 0;

	public def this() {}

	public def startAsync() {
	    activeAsyncs++;
	}

	public def stopAsync() {
	    activeAsyncs--;
	}

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
	public def addAll(all: Rail[Activity]) {
	    state.addAll(all);
	}
	public def removeAll() {
	    val all = state.toRail();
	    while (state.size() > 0) {
		state.removeLast();
	    }
	    return all;
	}
    }

    private val rootFinish: FinishState;
    private var currentFinish: FinishState;
    private val todo: ActivitySet;

    public def this() {
	rootFinish = new FinishState();
	currentFinish = new FinishState();
	todo = new ActivitySet();
    }

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
	}
    }

    public operator async (body: ()=>void) {
    	currentFinish.startAsync();
    	todo.add(new Activity(currentFinish,
			      ()=> { body(); currentFinish.stopAsync(); }));
    }

    public static class Condition {
	var state: Boolean;
	val schedTodo: ActivitySet;
	val waiting = new ActivitySet();
	public def this(todo: ActivitySet, b: Boolean) {
	    state = b;
	    schedTodo = todo;
	}
	public operator this() = (b:Boolean) {
	    state = b;
	    if (b) {
		schedTodo.addAll(waiting.removeAll());
	    }
	}
	public def status() {
	    return state;
	}
	public def wait(a:Activity) {
	    waiting.add(a);
	}
    }

    public def makeCondition(b:Boolean) {
	return new Condition(todo, b);
    }


    public operator when(b: Condition, body:()=>void) {
	if (b.status()) {
	    body();
	} else {
	    val finished = new Cell[Boolean](false);
	    b.wait(new Activity(currentFinish,
				() => { body(); finished() = true; }));
	    while (!finished()) {
		sched();
	    }
	}
    }

    public operator atomic(body:()=>void) {
	body();
    }

    /* Test */

    // static def testDeadlockFinish() {
    // 	val coop = new Coop();
    // 	val c1 = coop.makeCondition(false);
    // 	val c2 = coop.makeCondition(false);
    // 	val c3 = coop.makeCondition(false);
    // 	coop.finish {
    // 	    Console.OUT.println("Start");
    // 	    coop.async {
    // 		coop.when(c1) {}
    // 		Console.OUT.println("2");
    // 		coop.atomic { c2() = true; }
    // 		coop.finish {
    // 		    coop.when(c3) {}
    // 		}
    // 	    }
    // 	    finish {
    // 		Console.OUT.println("1");
    // 		coop.atomic { c1() = true; }
    // 		coop.when (c2) {}
    // 		Console.OUT.println("3");
    // 	    };
    // 	    coop.atomic { c3() = true; } // This statement will never be executed.
    // 	}
    // }

    static def testDeadlockFinish() {
    	val coop = new Coop();
    	val c1 = coop.makeCondition(false);
    	val c2 = coop.makeCondition(false);
    	val c3 = coop.makeCondition(false);
    	coop.finish {
    	    Console.OUT.println("Start");
    	    coop.async {
    		Console.OUT.println("2");
    		coop.finish {
    		    coop.when(c3) {}
    		}
    	    }
    	    finish {
    		Console.OUT.println("1");
    	    };
	    Console.OUT.println("3");
    	    coop.atomic { c3() = true; } // This statement will never be executed.
    	}
    }

    static def test() {
	val coop = new Coop();
	coop.finish {
	    for (i in 1 .. 10) coop.async {
	       Console.OUT.println(i);
	    }
	    val c = coop.makeCondition(false);
	    coop.when(c){}
	}
    }

    public static def main(Rail[String]) {
	testDeadlockFinish();
	// test();
	Console.OUT.println("End!");
    }

}
