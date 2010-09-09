import harness.x10Test;

/**
 * Purpose: Checks conversion to a wider clause that does not meet constraints fails compile 
 * X10DepTypeClassTwo(:p==1) <-- X10DepTypeClassTwo(:p==0&&q==1)
 * @author vcave
 **/
public class ClassDepTypeCastWideningClause2_MustFailCompile extends x10Test {

	public boolean run() {
		
		try {						
			X10DepTypeClassTwo(:p==1) test = 
				 new X10DepTypeClassTwo(:p==0&&q==1)(0,1);						

		}catch(Throwable e) {
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
		new ClassDepTypeCastWideningClause2_MustFailCompile().execute();
	}

}
 