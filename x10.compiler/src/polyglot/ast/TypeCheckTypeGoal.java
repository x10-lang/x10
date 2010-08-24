package polyglot.ast;

import polyglot.types.LazyRef;
import polyglot.types.UnknownType;
import polyglot.util.ErrorInfo;
import polyglot.visit.TypeChecker;

public class TypeCheckTypeGoal extends TypeCheckFragmentGoal {
	public TypeCheckTypeGoal(Node parent, Node n, TypeChecker v, LazyRef r,
			boolean mightFail) {
		super(parent, n, v, r, mightFail);
	}

	@Override
	public boolean runTask() {
		boolean result = super.runTask();
		if (result) {
			if (r.getCached() instanceof UnknownType) {
//			    assert false;
			        v.errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR, "Could not compute type.", n.position());
			        return false;
			}
		}
		return result;
	}
}
