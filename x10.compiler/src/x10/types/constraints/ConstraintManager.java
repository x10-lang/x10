package x10.types.constraints;

import x10.constraint.XConstraintSystem;
import x10.types.constraints.xnative.CNativeConstraintSystem;



public class ConstraintManager {
	static CConstraintSystem constraint_factory = null;
	static String env_variable = "CONSTRAINT_SYSTEM";
	
	public static final String asExprEqualsName = "==";
	public static final String asExprDisEqualsName = "!=";
	public static final String asExprAndName = "&&";
	public static final String asExprNotName = "!";

	
	public static CConstraintSystem getConstraintSystem() {
		// instantiate the constraint system if this has not been done before
		if (constraint_factory == null ) {
			//String mode = System.getenv(env_variable);
			//if(mode.equals("smt")) {
			//	constraint_factory = new XSmtConstraintSystem(); 	
			//} else {
				constraint_factory = new CNativeConstraintSystem();
			//}
		}
		
		return constraint_factory; 
	}
}
