package x10.constraint;

public class C_Equals_c extends C_Formula_c implements C_Equals {
	
	public C_Equals_c(C_Term left, C_Term right) {
		super(C_Terms.equalsName, left, right);
	}
	
	public Promise internIntoConstraint(Constraint c, Promise last) throws Failure {
		assert false : "Should not intern " + this;
		return super.internIntoConstraint(c, last);
	}

}
