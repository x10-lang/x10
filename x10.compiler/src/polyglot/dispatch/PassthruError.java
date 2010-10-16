package polyglot.dispatch;

public class PassthruError extends RuntimeException {
    private static final long serialVersionUID = 4936570922363220660L;

    public PassthruError(Exception e) {
	super(e);
    }
}
