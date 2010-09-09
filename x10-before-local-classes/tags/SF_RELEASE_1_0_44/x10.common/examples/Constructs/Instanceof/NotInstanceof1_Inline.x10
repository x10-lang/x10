/*
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
	 
	public boolean run() {
		java.lang.Object diffType = this.getDifferentType()
		
		return !(diffType instanceof X10DepTypeClassOne(:p==1));
	}

	private java.lang.Object getSameType( {
		return new X10DepTypeClassOne(1);
	}
	
	private java.lang.Object getDifferentType( {
		return new OtherClass(1);
	}
	
	public static void main(String[] args) {
		new NotInstanceof1_Inline().execute();
	}
		 
	 public class OtherClass (int p) {
		public OtherClass(int p) {
		    this.p=p;
		}
	 }
}
 