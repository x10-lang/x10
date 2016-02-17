import x10.util.*;

class Flatten {

    public static operator try (body: () => void,
                                handler: (MultipleExceptions) => void) {
        try { body(); }
        catch (me: MultipleExceptions) {
            val exns = new GrowableRail[CheckedThrowable]();
            flatten(me, exns);
            handler (new MultipleExceptions(exns));
        }
    }

    private static def flatten(me: MultipleExceptions, acc: GrowableRail[CheckedThrowable]) {
        for (e in me.exceptions) {
            if (e instanceof MultipleExceptions) {
                flatten(e as MultipleExceptions, acc);
            } else {
                acc.add(e);
            }
        }
    }

    public static def main(Rail[String]) {
	Flatten.try {
	    finish {
		async { throw new Exception("Exn 1"); }
		async finish {
		    async { throw new Exception("Exn 2"); }
		    async { throw new Exception("Exn 3"); }
		}
	    }
	} catch (me: MultipleExceptions) {
	    Console.OUT.println(me.exceptions);
	}
    }
}
