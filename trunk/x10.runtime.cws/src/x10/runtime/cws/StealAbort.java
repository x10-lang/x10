package x10.runtime.cws;

public class StealAbort extends Exception {

	public static final StealAbort abort = new StealAbort();
	public StealAbort() {
		super();
		// TODO Auto-generated constructor stub
	}

	public StealAbort(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public StealAbort(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public StealAbort(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
