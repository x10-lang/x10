/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.
/**
 * Language clarification needed.
 * Methods whose parameter types are only distinguished by whether they are
 * nullable end up with the same Java signature after erasing the nullable
 * attribute.
 *
 * Ideally we should produce a meaningful compile-time error here, along the
 * lines of what Java does for generics.
 * Alternatively, we can implement a C++-style name mangling scheme to
 * help distinguish those cases.
 *
 * @author grothoff 01/2006
 */
 import harness.x10Test;
class NullableOverriding extends x10Test {

	public boolean run() {
		NullableOverriding a = new NullableOverriding();
		nullable<NullableOverriding> b = new NullableOverriding();
		return 3 == m(a) + m(b);
	}

	public static int m(nullable<NullableOverriding> o) {
		return 1;
	}

	public static int m(NullableOverriding o) {
		return 2;
	}

	public static void main(String[] args) {
		new NullableOverriding().execute();
	}
}

