package x10.constraint;



public class ConstraintManager {
	static XConstraintSystem constraint_factory = null;
	static String env_variable = "CONSTRAINT_SYSTEM";
	
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
