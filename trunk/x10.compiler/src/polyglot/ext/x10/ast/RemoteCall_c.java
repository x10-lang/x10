/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Receiver;
import polyglot.ast.Call_c;
import polyglot.ast.Id;
import polyglot.util.Position;

/** An immutable data structure representing a remote method invocation 
 * o -> m(t1,...,tn).
 * @author vj Dec 9, 2004
 * 
 */
public class RemoteCall_c extends Call_c implements RemoteCall {

	/**
	 * @param pos
	 * @param target
	 * @param name
	 * @param arguments
	 */
	public RemoteCall_c(Position pos, Receiver target, Id name,
			List arguments) {
		super(pos, target, name, arguments);
	}

}
