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

/**
 * @author vj Dec 9, 2004
 * 
 */
public class ReductionCall_c extends Call_c implements ReductionCall {

	Kind kind;
	
	/**
	 * @param pos
	 * @param target
	 * @param name
	 * @param arguments
	 */
	public ReductionCall_c(Position pos, Receiver target, String name,
			List arguments, Kind kind) {
		super(pos, target, name, arguments);
		this.kind = kind;
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.ReductionCall#kind()
	 */
	public Kind kind() {
		return kind;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.ReductionCall#isScan()
	 */
	public boolean isScan() {
		return kind==ReductionCall.SCAN;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.ReductionCall#isReduce()
	 */
	public boolean isReduce() {
		return kind==ReductionCall.REDUCE;
	}

}
