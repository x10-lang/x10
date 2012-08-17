package x10.constraint;

import x10.constraint.smt.XSmtConstraintSystem;
import x10.constraint.smt.XSmtType;

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
	static XConstraintSystem<? extends XType> constraint_factory = null;
	static String env_variable = "CONSTRAINT_SYSTEM";
	

	/**
	 * Returns the current constraint system by either creating a new one or
	 * returning the existing one.  
	 * @return
	 */
	
	public static <T extends XType> XConstraintSystem<T> getConstraintSystem() {
		// instantiate the constraint system if this has not been done before
		if (constraint_factory == null ) {
			//String mode = System.getenv(env_variable);
			//if(mode== null || !mode.equals("SMT")) {
			//	constraint_factory = new XNativeConstraintSystem<T>();
			//} else {
				constraint_factory = new XSmtConstraintSystem<T>();
			//}
		}
		
		@SuppressWarnings("unchecked")
		XConstraintSystem<T> res = (XConstraintSystem<T>)constraint_factory;
		return res; 
	}

	public static String header;
	public static void setHeader(String string) {
		header = string; 
		
	}
}
