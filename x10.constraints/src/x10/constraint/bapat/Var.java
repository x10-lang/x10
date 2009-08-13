package x10.constraint.bapat;

import java.util.LinkedList;
import java.util.List;

import stp.Expr;
import stp.VC;

public abstract class Var {
	
	protected List<Constraint> constraints;
	
	public Var() {
		constraints = new LinkedList<Constraint>();
	}
	
	public void addConstraint(Constraint c) {
		constraints.add(c);
	}
	
	public List<Constraint> getConstraints() {
		return constraints;
	}
	
	abstract public Expr toSTPExpr(VC vc);
	
	abstract public Expr getAssertions(VC vc);
	
	abstract public String toLongString();

}
