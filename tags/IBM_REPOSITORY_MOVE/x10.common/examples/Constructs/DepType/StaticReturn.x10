import harness.x10Test;

/**
 * The test checks that final fields can be accessed in a depclause.
 *
 * @author vj
 */
public class StaticReturn extends x10Test {
		 
	public boolean run() {		
		dist(:rank==2) s =   starY();
		return true;
	}
	dist(:rank==2) starY() {	
		dist(:rank==2) d = [0:-1,0:-1]->here;	
		return d;
	}
	public static void main(String[] args) {
		new StaticReturn().execute();
	}
}

