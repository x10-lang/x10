/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;
	
public class RefA(nullable<RefB(:self.f2==this.f1)> f0, int f1) extends x10Test {
	public RefA(nullable<RefB(:self.f2==this.f1)> f0, int f1) { 
		// should give an error the type of an arg to a constructor
		// cannot reference this -- there is no this to refer to!!
		this.f0 = f0;
		this.f1 = f1;
	}
	public boolean run() {
		return true;
	
	}
	public static void main(String[] args) {
		new RefA(null, 1).execute();
	}
}
