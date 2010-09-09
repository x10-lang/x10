import harness.x10Test;

public class StaticCastWithHeritage extends x10Test {

	public StaticCastWithHeritage() {
	}
	
	public boolean run(){
		final int a=1, b=2;

		// no constraints
		X10DepTypeClassOne one = new X10DepTypeSubClassOne(a,b);
		
		X10DepTypeClassOne(:p==1) two = (X10DepTypeClassOne(:p==1)) new X10DepTypeSubClassOne(:p==1)(1,b);
		
		X10DepTypeClassOne(:p==1) three = (X10DepTypeClassOne(:p==1)) new X10DepTypeSubClassOne(:p==1)(a,b);

		X10DepTypeClassOne(:p==b) four = (X10DepTypeClassOne(:p==b)) new X10DepTypeSubClassOne(:p==b)(b,b);
		
		X10DepTypeClassOne(:p==a) five = (X10DepTypeClassOne(:p==a)) new X10DepTypeSubClassOne(:p==a)(a,b);
		
		return true;
	}

	public static void main(String [] args ) {
		new StaticCastWithHeritage().execute();
	}
}

