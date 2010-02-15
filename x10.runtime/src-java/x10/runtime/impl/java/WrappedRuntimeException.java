package x10.runtime.impl.java;

public class WrappedRuntimeException extends RuntimeException {

	public WrappedRuntimeException(Exception ex) {
		super(ex);
	}

}
