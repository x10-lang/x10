package x10.types.constraints;

import polyglot.types.Type;
import x10.constraint.XConstraintManager;
import x10.types.constraints.smt.CSmtConstraintSystem;



public class ConstraintManager {
	static CConstraintSystem constraint_factory = null;
	
	public static CConstraintSystem getConstraintSystem() {
		// instantiate the constraint system if this has not been done before
		if (constraint_factory == null ) {
				constraint_factory = new CSmtConstraintSystem(); 	
		}
		
		return constraint_factory; 
	}
	
}
