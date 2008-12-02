/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Inlined Instanceof checking code must ensures both type and constraint equality is checked !
 * Issue: Types are not compatible but constraint are verified.
 * @author vcave
 **/
public class NotInstanceof1_Inline extends x10Test {
	 
	public def run():boolean = {
		val diffType = this.getDifferentType();
		
		return !(diffType instanceof X10DepTypeClassOne{p==1});
	}

	private def getSameType():Object = {
		return new X10DepTypeClassOne(1);
	}
	
	private def getDifferentType():Object = {
		return new OtherClass(1);
	}
	
	public static def main(args: Rail[String]) = {
		new NotInstanceof1_Inline().execute();
	}
		 
	 public class OtherClass (p:int) {
		public  def this(p:int) = {
		    this.p=p;
		}
	 }
}
