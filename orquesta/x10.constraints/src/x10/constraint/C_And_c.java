package x10.constraint;

import java.util.Collections;
import java.util.HashMap;

public class C_And_c extends C_Formula_c implements C_And {
	
	public C_And_c(C_Term left, C_Term right) {
		super(C_Terms.andName, left, right);
	}
	
	public Promise internIntoConstraint(Constraint c, Promise last) throws Failure {
		assert false : "Should not intern " + this;
		return super.internIntoConstraint(c, last);
	}

}
