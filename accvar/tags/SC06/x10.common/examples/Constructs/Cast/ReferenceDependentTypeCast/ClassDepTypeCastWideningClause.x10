import harness.x10Test;

/**
 * Purpose: Checks casted assignment to variable works.
 * @author vcave
 **/
public class ClassDepTypeCastWideningClause extends x10Test {

	public boolean run() {
			X10DepTypeClassTwo(:p==0&&q==1) test1 = 
				((X10DepTypeClassTwo(:p==0&&q==1)) new X10DepTypeClassTwo(:p==0&&q==1)(0,1));
			
			X10DepTypeClassTwo(:p==0) test2 = 
				((X10DepTypeClassTwo(:p==0)) new X10DepTypeClassTwo(:p==0&&q==1)(0,1));
			
			X10DepTypeClassTwo(:q==1) test3 = 
				((X10DepTypeClassTwo(:q==1)) new X10DepTypeClassTwo(:p==0&&q==1)(0,1));
			
			X10DepTypeClassTwo test4 = 
				((X10DepTypeClassTwo) new X10DepTypeClassTwo(:p==0&&q==1)(0,1));
		return true;
	}

	public static void main(String[] args) {
		new ClassDepTypeCastWideningClause().execute();
	}

}
 