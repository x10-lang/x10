package x10.constraint;

import x10.constraint.smt.XSmtConstraintSystem;

/**
 * Static factory class that manages the constraint system currently in use.
 * The default constraint system is the native constraint system. There are 
 * two possible constraint systems that can be used so far: SMT and the 
 * native constraint system. 
 * 
 * @author lshadare
 *
 */
public class XConstraintManager {
	static XConstraintSystem<? extends XType> constraint_factory = null;
	

	/**
	 * Returns the current constraint system by either creating a new one or
	 * returning the existing one.  
	 * @return
	 */
	public static <T extends XType> XConstraintSystem<T> getConstraintSystem() {
		// instantiate the constraint system if this has not been done before
		if (constraint_factory == null ) {
				constraint_factory = new XSmtConstraintSystem<T>();
		}
		
		@SuppressWarnings("unchecked")
		XConstraintSystem<T> res = (XConstraintSystem<T>)constraint_factory;
		return res; 
	}
}
