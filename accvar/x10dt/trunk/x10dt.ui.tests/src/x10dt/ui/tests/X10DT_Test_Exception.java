package x10dt.ui.tests;

public class X10DT_Test_Exception extends Exception {
	private static final long serialVersionUID = 1L;
	private static final String msgPrefix = "x10dt test exception: ";
	public X10DT_Test_Exception(String message) {
		super(msgPrefix + message);
	}
	public X10DT_Test_Exception(String message, Throwable cause) {
		super(msgPrefix + message, cause);
	}
}