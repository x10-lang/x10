/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.ast;


import java.util.Collections;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.frontend.AbstractGoal_c;
import polyglot.frontend.Goal;
import polyglot.frontend.SchedulerException;
import polyglot.types.LazyRef;
import polyglot.types.Type;
import polyglot.util.CollectionUtil;
import polyglot.util.ErrorInfo;
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
		        v.job().compiler().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR, "Could not infer type; possible recursive or ambiguous initialization.", n.position());
			r.update(v.typeSystem().unknownType(n.position()));
			return false;
		}

		try {
			Node m = parent.visitChild(n, v);
			v.job().nodeMemo().put(n, m);
			v.job().nodeMemo().put(m, m);
			if (m instanceof Expr) {
			    Type t = ((Expr) m).type();
			    if (t instanceof X10ClassType) {
			        X10ClassType ct = (X10ClassType) t;
			        if (ct.isAnonymous()) {
			            if (ct.interfaces().size() > 0)
			                t = ct.interfaces().get(0);
			            else
			                t = ct.superClass();
			        }
			    }
			    r.update(t);
			}
			return r.known();
		}
		catch (SchedulerException e) {
			return false;
		}
	}

	@Override
	public String toString() {
	    return name() + "[" + n + "] (" + stateString() + ")";
	}
}
