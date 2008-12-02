/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks nullable checking is done.
 * Issue: method getNullable return null which makes the cast fails.
 * @author vcave
 **/
public class CastNullNullableToNonNullable extends x10Test {

		public boolean run() {
			try { 
			X10DepTypeClassTwo nonNull2 = 
				(X10DepTypeClassTwo) this.getNullable();
			} catch(ClassCastException e) {
				return true;
			}
			return false;
		}
		
		private nullable<X10DepTypeClassTwo> getNullable() {
			return null;
		}

	public static void main(String[] args) {
		new CastNullNullableToNonNullable().execute();
	}

}
 