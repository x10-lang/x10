package x10.constraint;

import x10.constraint.xnative.XNativeConstraintSystem;

public class XConstraintManager {
	static XConstraintSystem constraint_factory = null;
	static String env_variable = "CONSTRAINT_SYSTEM";
	
	public static final String asExprEqualsName = "==";
	public static final String asExprDisEqualsName = "!=";
	public static final String asExprAndName = "&&";
	public static final String asExprNotName = "!";

	
	public static XConstraintSystem getConstraintSystem() {
		// instantiate the constraint system if this has not been done before
		if (constraint_factory == null ) {
			//String mode = System.getenv(env_variable);
			//if(mode.equals("smt")) {
			//	constraint_factory = new XSmtConstraintSystem(); 	
			//} else {
				constraint_factory = new XNativeConstraintSystem();
			//}
		}
		
		return constraint_factory; 
	}

}
