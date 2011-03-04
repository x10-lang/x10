/**
 * 
 */
package polyglot.ext.x10.types.constr;

import polyglot.types.SemanticException;
import polyglot.util.Position;

/**
 * @author vj
 *
 */
public class Failure extends SemanticException {

	/**
	 * 
	 */
	public Failure() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public Failure(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param position
	 */
	public Failure(Position position) {
		super(position);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param m
	 */
	public Failure(String m) {
		super(m);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param m
	 * @param cause
	 */
	public Failure(String m, Throwable cause) {
		super(m, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param m
	 * @param position
	 */
	public Failure(String m, Position position) {
		super(m, position);
		// TODO Auto-generated constructor stub
	}

}
