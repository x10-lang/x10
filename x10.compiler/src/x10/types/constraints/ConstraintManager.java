package x10.types.constraints;

import x10.types.constraints.smt.CSmtConstraintSystem;
import x10.types.constraints.xnative.CNativeConstraintSystem;

/**
 * Static factory class that manages the constraint system currently in use.
 * The default constraint system is the native constraint system. There are 
 * two possible constraint systems that can be used so far: SMT and the 
 * native constraint system. 
 * 
 * @author lshadare, dcunnin
 *
 */
public class ConstraintManager {
	static CConstraintSystem constraint_factory = null;
	
	static final boolean USE_SMT_SOLVER = System.getProperty("X10_USE_SMT_SOLVER") != null;

	/**
	 * Returns the current constraint system by either creating a new one or
	 * returning the existing one.  
	 * @return
	 */
	public static CConstraintSystem getConstraintSystem() {
		// instantiate the constraint system if this has not been done before
		if (constraint_factory == null ) {
			constraint_factory = USE_SMT_SOLVER ? new CSmtConstraintSystem() : new CNativeConstraintSystem();
		}

		return constraint_factory;
	}
}
