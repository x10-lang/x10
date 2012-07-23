package x10.constraint.smt;

/**
 * A representation of a failure, thrown to signal there was a problem
 * with calling the external SMT solver. 
 * @author lshadare
 *
 */
public class XSmtFailure extends Exception {
	private static final long serialVersionUID = 1432509073931195796L;
    Object etc;
	public XSmtFailure() {super();}
	public XSmtFailure(Throwable cause) {super(cause);}
	public XSmtFailure(Object etc) {this.etc = etc;}
	public XSmtFailure(String m) {super(m);}
	public XSmtFailure(String m, Throwable cause) {super(m, cause);}
	public XSmtFailure(String m, Object etc) {super(m);this.etc = etc;}
}
