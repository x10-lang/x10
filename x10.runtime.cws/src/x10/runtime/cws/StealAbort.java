package x10.runtime.cws;

/**
 * An instance of StealAbort is thrown by the victim when it discovers that the current
 * frame has been stolen. This causes the Java call stack to be unwound. The exception is
 * caught in the scheduler. Since the exception is not meant to carry any meaningful information,
 * a single exception is precreated and used to respond to all steals.
 * 
 * The design of this library is based on the Cilk runtime, developed by the Cilk
 * group at MIT.
 * 
 * @author vj 05/25/2007
 *
 */
public class StealAbort extends Exception {

	public static final StealAbort abort = new StealAbort();
	public StealAbort() {
		super();
	}

	public StealAbort(String message) {
		super(message);
	}

	public StealAbort(String message, Throwable cause) {
		super(message, cause);
	}

	public StealAbort(Throwable cause) {
		super(cause);
	}

}
