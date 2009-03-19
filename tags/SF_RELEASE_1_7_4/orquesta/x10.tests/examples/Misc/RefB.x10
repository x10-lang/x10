/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;
	
public class RefB(f2: RefA) extends x10Test {
	public RefB(f2: RefA) { 
		this.f2=f2;
	}
	
	public boolean run() { return true;}
}
