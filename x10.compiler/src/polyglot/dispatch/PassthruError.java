package polyglot.dispatch;

public class PassthruError extends RuntimeException {

    public PassthruError(Exception e) {
	super(e);
    }
}
