import harness.x10Test;

/**
 * Purpose: Checks the numeric expression is not evaluated several time while checking for constraint
 * Note: The cast should not be inlined to avoid several execution of ++j
 * Note: Contraint value is stored as an integer
 * @author vcave
 **/
public class Short_ConstraintDeclaredAsShort extends x10Test {

	public boolean run() {
		// BEGIN original test
		final short constraint = 0;

		// short j = -1;
		// short(:self == constraint) i = 
			// (short (:self == constraint)) (++j);
		// return ((j==constraint) && (i==constraint));
		// END original test
		
		// final int iconstraint = 0;	
		// not valid: as cast type are incompatibles int/*(:{self=iconstraint})*/ <-- int/*(:{self=0})*/
		// Whereas it could be valid at runtime 
		// int(:self==iconstraint) i = (int(:self==iconstraint)) 0;
		
		// valid: as int/*(:{self=iconstraint})*/ <-- may be valid at runtime
		// int(:self==iconstraint) i = (int(:self==iconstraint)) ((int) 0);
		
		// valid: because both constraint constant and assigned one are int.
		// int (:self == 0) i = 0;
		// int (:self == 0) i = (int) 0;

		// valid: because both constraint constant and assigned one are long.
		// long (:self == 0L) l = 0L;
		// long (:self == 0L) l = (long) 0;		
		
		// not valid: because constraint constant and assigned one have different type
		// long (:self == 0L) l = 0;
		// long (:self == 0) l = (long) 0;
	
		// not valid: because constraint constant and assigned one have different type
		// short (:self == 0) i = (short) 0;

		// not valid: cannot assign type short to a constrained one.
		// short (:self == constraint) i = (short) 0;

		// not valid: as constraint on self is a short and the other is an integer
		short (:self == constraint) i = (short(:self==0)) 0;
				
		// not valid: because cannot cast 0 which is of type int(:self==int) to short(:self==short)
		// short (:self == constraint) i = (short(:self==constraint)) 0;
		
		// not valid: as constraint on self is a short and the other is an integer
		// short (:self == constraint) i = (short(:self==0)) ((short) 0);

		// valid
		// short (:self == constraint) i = (short(:self==constraint)) ((short) 0);
		return true;
	}

	public static void main(String[] args) {
		new Short_ConstraintDeclaredAsShort().execute();
	}

}
 