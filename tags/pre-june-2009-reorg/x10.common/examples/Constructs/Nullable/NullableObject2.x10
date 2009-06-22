/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Instanceof test for nullable types.
 *
 * (x instance of T) for  any T (nullable or non-nullable)
 * is true iff x is not null and the object x's runtime type is a subtype of T
 *
 * if the compile time type of x and T are
 * incomparable there should be a compiler error
 * Lines that should cause a compiler error are commented out.
 *
 * @author kemal
 * 1/2005
 */
public class NullableObject2 extends x10Test {
	public boolean run() {
		{
			x10.lang.Object x = new boxedInt(1);
			if (! (x instanceof x10.lang.Object)) throw new Error("1");
			if (! (x instanceof nullable<x10.lang.Object>)) throw new Error("2");
			if (! (x instanceof boxedInt)) throw new Error("3");
			if (! (x instanceof nullable<boxedInt>)) throw new Error("4");
			if (  (x instanceof boxedLong)) throw new Error("5");
			if (  (x instanceof nullable<boxedLong>)) throw new Error("6");
		}
		{
			x10.lang.Object x = new boxedLong(1);
			if (! (x instanceof x10.lang.Object)) throw new Error("7");
			if (! (x instanceof nullable<x10.lang.Object>)) throw new Error("8");
			if (  (x instanceof boxedInt)) throw new Error("9");
			if (  (x instanceof nullable<boxedInt>)) throw new Error("10");
			if (! (x instanceof boxedLong)) throw new Error("11");
			if (! (x instanceof nullable<boxedLong>)) throw new Error("12");
		}
		{
			x10.lang.Object x = new x10.lang.Object();
			if (! (x instanceof x10.lang.Object)) throw new Error("13");
			if (! (x instanceof nullable<x10.lang.Object>)) throw new Error("14");
			if (  (x instanceof boxedInt)) throw new Error("15");
			if (  (x instanceof nullable<boxedInt>)) throw new Error("16");
			if (  (x instanceof boxedLong)) throw new Error("17");
			if (  (x instanceof nullable<boxedLong>)) throw new Error("18");
		}
		{
			nullable<x10.lang.Object> x = null;
			if (  (x instanceof x10.lang.Object)) throw new Error("19");
			if (  (x instanceof nullable<x10.lang.Object>)) throw new Error("20");
			if (  (x instanceof boxedInt)) throw new Error("21");
			if (  (x instanceof nullable<boxedInt>)) throw new Error("22");
			if (  (x instanceof boxedLong)) throw new Error("23");
			if (  (x instanceof nullable<boxedLong>)) throw new Error("24");
		}
		{
		    nullable<x10.lang.Object> x = new boxedInt(1);
			if (! (x instanceof x10.lang.Object)) throw new Error("25");
			if (! (x instanceof nullable<x10.lang.Object>)) throw new Error("26");
			if (! (x instanceof boxedInt)) throw new Error("27");
			if (! (x instanceof nullable<boxedInt>)) throw new Error("28");
			if (  (x instanceof boxedLong)) throw new Error("29");
			if (  (x instanceof nullable<boxedLong>)) throw new Error("30");
		}
		{
		    nullable<x10.lang.Object> x= new boxedLong(1);
			if (! (x instanceof x10.lang.Object)) throw new Error("31");
			if (! (x instanceof nullable<x10.lang.Object>)) throw new Error("32");
			if (  (x instanceof boxedInt)) throw new Error("33");
			if (  (x instanceof nullable<boxedInt>)) throw new Error("34");
			if (! (x instanceof boxedLong)) throw new Error("35");
			if (! (x instanceof nullable<boxedLong>)) throw new Error("36");
		}
		{
		    nullable<x10.lang.Object> x= new x10.lang.Object();
			if (! (x instanceof x10.lang.Object)) throw new Error("37");
			if (! (x instanceof nullable<x10.lang.Object>)) throw new Error("38");
			if (  (x instanceof boxedInt)) throw new Error("39");
			if (  (x instanceof nullable<boxedInt>)) throw new Error("40");
			if (  (x instanceof boxedLong)) throw new Error("41");
			if (  (x instanceof nullable<boxedLong>)) throw new Error("42");
		}
		{
			boxedInt x = new boxedInt(1);
			if (! (x instanceof x10.lang.Object)) throw new Error();
			if (! (x instanceof nullable<x10.lang.Object>)) throw new Error("43");
			if (! (x instanceof boxedInt)) throw new Error("44");
			if (! (x instanceof nullable<boxedInt>)) throw new Error("45");
			//if (  (x instanceof boxedLong)) throw new Error("46");
			//if (  (x instanceof nullable boxedLong)) throw new Error("47");
		}
		{
		    nullable<x10.lang.Object> x= null;
			if (  (x instanceof x10.lang.Object)) throw new Error("48");
			if (  (x instanceof nullable<x10.lang.Object>)) throw new Error("49");
			if (  (x instanceof boxedInt)) throw new Error("50");
			if (  (x instanceof nullable<boxedInt>)) throw new Error("51");
			//if (  (x instanceof boxedLong)) throw new Error("52");
			//if (  (x instanceof nullable boxedLong)) throw new Error("53");
			if (  (((nullable<x10.lang.Object>)x) instanceof nullable<boxedLong>) ) throw new Error("54");
		}
		{
		    nullable<x10.lang.Object> x= new boxedInt(1);
			if (! (x instanceof x10.lang.Object)) throw new Error("55");
			if (! (x instanceof nullable<x10.lang.Object>)) throw new Error("56");
			if (! (x instanceof boxedInt)) throw new Error("57");
			if (! (x instanceof nullable<boxedInt>)) throw new Error("58");
			//if (  (x instanceof boxedLong)) throw new Error("59");
			//if (  (x instanceof nullable boxedLong)) throw new Error("60");
		}
		{
			boxedLong x = new boxedLong(1);
			if (! (x instanceof x10.lang.Object)) throw new Error("61");
			if (! (x instanceof nullable<x10.lang.Object>)) throw new Error("62");
			//if (  (x instanceof boxedInt)) throw new Error("63");
			//if (  (x instanceof nullable boxedInt)) throw new Error("64");
			if (! (x instanceof boxedLong)) throw new Error("65");
			if (! (x instanceof nullable<boxedLong>)) throw new Error("66");
		}
		{
			nullable<boxedLong> x = null;
			if (  (x instanceof x10.lang.Object)) throw new Error("67");
			if (  (x instanceof nullable<x10.lang.Object>)) throw new Error("68");
			//if (  (x instanceof boxedInt)) throw new Error("69");
			//if (  (x instanceof nullable boxedInt)) throw new Error("70");
			if (  (((nullable<x10.lang.Object>)x) instanceof nullable<boxedInt>)) throw new Error("71");
			if (  (x instanceof boxedLong)) throw new Error("72");
			if (  (x instanceof nullable<boxedLong>)) throw new Error("73");
		}
		{
			nullable<boxedLong> x = new boxedLong(1);
			if (! (x instanceof x10.lang.Object)) throw new Error("74");
			if (! (x instanceof nullable<x10.lang.Object>)) throw new Error("75");
			//if (  (x instanceof boxedInt)) throw new Error("76");
			//if (  (x instanceof nullable boxedInt)) throw new Error("77");
			if (! (x instanceof boxedLong)) throw new Error("78");
			if (! (x instanceof nullable<boxedLong>)) throw new Error("79");
		}
		return true;
	}

	public static void main(String[] args) {
		new NullableObject2().execute();
	}

	/**
	 * Helper class -- boxed int
	 */
	static class boxedInt extends x10.lang.Object {
		int val;
		boxedInt(int x) { val = x; }
	}

	/**
	 * Helper class -- boxed long
	 */
	static class boxedLong extends x10.lang.Object {
		long val;
		boxedLong(long x) { val = x; }
	}
}

