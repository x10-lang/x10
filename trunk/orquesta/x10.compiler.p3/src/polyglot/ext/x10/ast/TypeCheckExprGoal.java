package polyglot.ext.x10.ast;


import java.util.Collections;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.frontend.AbstractGoal_c;
import polyglot.frontend.Goal;
import polyglot.frontend.SchedulerException;
import polyglot.types.LazyRef;
import polyglot.util.CollectionUtil;
import polyglot.visit.TypeChecker;

public class TypeCheckExprGoal extends AbstractGoal_c {
	Node parent;
	Expr n;
	TypeChecker v;
	LazyRef r;

	public TypeCheckExprGoal(Node parent, Expr n, TypeChecker v, LazyRef r) {
		this.parent = parent;
		this.n = n;
		this.v = v;
		this.r = r;
	}

	public List<Goal> prereqs() {
		List<Goal> l = super.prereqs();
		List<Goal> l2 = Collections.singletonList(v.job().extensionInfo().scheduler().PreTypeCheck(v.job()));
		if (l.isEmpty())
			return l2;
		else
			return CollectionUtil.<Goal>append(l, l2);
	}

	public boolean runTask() {
		Goal g = v.job().extensionInfo().scheduler().PreTypeCheck(v.job());
		assert g.hasBeenReached();
		
		if (state() == Goal.Status.RUNNING_RECURSIVE) {
			r.update(v.typeSystem().unknownType(n.position()));
			return false;
		}

		try {
			Node m = parent.visitChild(n, v);
			v.job().nodeMemo().put(n, m);
			if (m instanceof Expr) {
				r.update(((Expr) m).type());
			}
			return r.known();
		}
		catch (SchedulerException e) {
			return false;
		}
	}
}
