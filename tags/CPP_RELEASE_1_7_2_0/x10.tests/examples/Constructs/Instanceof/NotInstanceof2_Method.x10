/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Side Effect Instanceof checking code must ensures both type and c
 * onstraint equality is checked !
 * Issue: Types are not compatible but constraint are verified.
 * @author vcave
 **/
public class NotInstanceof2_Method extends x10Test {
	 
	public def run():boolean = 
	 !(this.getDifferentType() instanceof X10DepTypeClassOne{p==1});
	
	private def getSameType():Object = new X10DepTypeClassOne(1);
	
	private def getDifferentType():Object =  new OtherClass(1);
	
	public static def main(Rail[String]) = {
		new NotInstanceof2_Method().execute();
	}
		 
	 public class OtherClass (p:int) {
		public def this(p:int)= {
		    this.p=p;
		}
	 }
}
