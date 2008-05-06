package x10.constraint;

public class C_Not_c extends C_Formula_c implements C_Not {
	
	public C_Not_c(C_Term arg) {
		super(C_Terms.notName, arg);
	}
	
	public Promise internIntoConstraint(Constraint c, Promise last) throws Failure {
		assert false : "Should not intern " + this;
		return super.internIntoConstraint(c, last);
	}

}
