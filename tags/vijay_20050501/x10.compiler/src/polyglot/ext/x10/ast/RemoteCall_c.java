/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Receiver;
import polyglot.ext.jl.ast.Call_c;
import polyglot.util.Position;

/** An immutable data structure representing a remote method invocation 
 * o -> m(t1,...,tn).
 * @author vj Dec 9, 2004
 * 
 */
public class RemoteCall_c extends Call_c {

	/**
	 * @param pos
	 * @param target
	 * @param name
	 * @param arguments
	 */
	public RemoteCall_c(Position pos, Receiver target, String name,
			List arguments) {
		super(pos, target, name, arguments);
	}

}
