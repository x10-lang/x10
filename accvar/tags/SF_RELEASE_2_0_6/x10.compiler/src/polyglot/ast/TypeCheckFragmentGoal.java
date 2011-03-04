/**
 * 
 */
package polyglot.ast;

import java.util.Collections;
import java.util.List;

import polyglot.frontend.*;
import polyglot.types.LazyRef;
import polyglot.util.CollectionUtil;
import polyglot.visit.TypeChecker;

public class TypeCheckFragmentGoal extends AbstractGoal_c {
    protected Node parent;
    protected Node n;
    protected TypeChecker v;
    protected LazyRef r;
    protected boolean mightFail;

    public TypeCheckFragmentGoal(Node parent, Node n, TypeChecker v, LazyRef r, boolean mightFail) {
	this.parent = parent;
	this.n = n;
	this.v = v;
	this.r = r;
	this.mightFail = mightFail;
    }
    
    public List<Goal> prereqs() {
	List<Goal> l = super.prereqs();
	List<Goal> l2 = Collections.singletonList(v.job().extensionInfo().scheduler().PreTypeCheck(v.job()));
	if (l.isEmpty())
	    return l2;
	else
	    return CollectionUtil.<Goal> append(l, l2);
    }

    public boolean runTask() {
	Goal g = v.job().extensionInfo().scheduler().PreTypeCheck(v.job());
	assert g.hasBeenReached();

	if (state() == Goal.Status.RUNNING_RECURSIVE) {
	    r.update(r.getCached()); // marks r known
	    // if (! mightFail)
	    // v.job().compiler().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR,
	    // "Recursive resolution for " + n + ".", n.position());
	    return mightFail;
	}

	try {
	    Node m = parent.visitChild(n, v);
	    v.job().nodeMemo().put(n, m);
	    v.job().nodeMemo().put(m, m);
	    return mightFail || r.known();
	}
	catch (SchedulerException e) {
	    return false;
	}
    }
    
    public String toString() {
	return v.job() + ":" + v.job().extensionInfo() + ":" + name() + " (" + stateString() + ") " + parent + "->" + n;
    }
}