package x10.constraint;

import x10.constraint.xnative.XNativeConstraintSystem;
import x10.constraint.xsmt.XSmtConstraintSystem;

/**
 * Static factory class that manages the constraint system currently in use.
 * The default constraint system is the native constraint system. The SMT
 * constraint system is enabled by defining the environment "CONSTRAINT_SYSTEM"
 * variable to be equal to "SMT"
 * 
 * @author lshadare
 *
 */
public class XConstraintManager {
	static XConstraintSystem constraint_factory = null;
	static String env_variable = "CONSTRAINT_SYSTEM";
	
	public static final String asExprEqualsName = "==";
	public static final String asExprDisEqualsName = "!=";
	public static final String asExprAndName = "&&";
	public static final String asExprNotName = "!";

	/**
	 * Returns the current constraint system by either creating a new one or
	 * returning the existing one.  
	 * @return
	 */
	public static XConstraintSystem getConstraintSystem() {
		// instantiate the constraint system if this has not been done before
		if (constraint_factory == null ) {
			String mode = System.getenv(env_variable);
			if(mode== null || !mode.equals("SMT")) {
				constraint_factory = new XNativeConstraintSystem();
			} else {
				constraint_factory = new XSmtConstraintSystem(); 	
			}
		}
		
		return constraint_factory; 
	}

}
