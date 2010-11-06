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

public class TypeCheckFragmentGoal<T> extends AbstractGoal_c implements SourceGoal{
    private static final long serialVersionUID = -843644476867221586L;

    protected Node parent;
    protected Node n;
    protected TypeChecker v;
    protected LazyRef<T> r;
    protected boolean mightFail;

    public TypeCheckFragmentGoal(Node parent, Node n, TypeChecker v, LazyRef<T> r, boolean mightFail) {
	this.parent = parent;
	this.n = n;
	this.v = v;
	this.r = r;
	this.mightFail = mightFail;
	this.scheduler = v.job().extensionInfo().scheduler();
    }
    
    public List<Goal> prereqs() {
	List<Goal> l = super.prereqs();
	List<Goal> l2 = Collections.singletonList(v.job().extensionInfo().scheduler().PreTypeCheck(v.job()));
	if (l.isEmpty())
	    return l2;
	else
	    return CollectionUtil.<Goal> append(l, l2);
    }


    protected LazyRef<T> r() {
        return r;
    }

    protected Node process(Node parent, Node n, TypeChecker v) {
        return parent.visitChild(n, v);
    }

    protected T defaultRecursiveValue() {
        return r().getCached();
    }

    public Job job() {
        return v.job();
    }


    public boolean runTask() {
        Goal g = v.job().extensionInfo().scheduler().PreTypeCheck(v.job());
        assert g.hasBeenReached();

        if (state() == Goal.Status.RUNNING_RECURSIVE) {
            r().update(defaultRecursiveValue()); // marks r known
            return mightFail;
        }

        try {
            Node m = process(parent, n, v);
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