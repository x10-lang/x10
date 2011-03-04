/**
 * 
 */
package x10.effects.tests;

import junit.framework.TestCase;
import x10.constraint.XLocal;
import x10.constraint.XTerms;
import x10.effects.constraints.ArrayElementLocs;
import x10.effects.constraints.ArrayLocs;
import x10.effects.constraints.Effect;
import x10.effects.constraints.Effect_c;
import x10.effects.constraints.Effects;
import x10.effects.constraints.FieldLocs;
import x10.effects.constraints.LocalLocs;

/**
 * @author vj
 */
public class BaseTests extends TestCase {
    LocalLocs l1 = Effects.makeLocalLocs(new XLocal("l1"));
    LocalLocs l2 = Effects.makeLocalLocs(new XLocal("l2"));
    LocalLocs l3 = Effects.makeLocalLocs(new XLocal("l3"));
    LocalLocs l4 = Effects.makeLocalLocs(new XLocal("l4"));

	public BaseTests() {
		super("BaseTests");
	}
	
	/**
	 * Does {read(l1), write(l2), atomic(l3)} commute with {read(l4)}?
	 * @throws Throwable
	 */
	public void test1() throws Throwable {
		Effect e1 = new Effect_c(Effects.FUN);
		e1.addRead(l1);
		e1.addWrite(l2);
		e1.addAtomicInc(l3);
		
		Effect e2 = new Effect_c(Effects.FUN);
		e2.addRead(l4);
		
		boolean result = e1.commutesWith(e2);
		assertTrue(result);
		
	}
	
	/**
	 * Does {read(l1)} commute with {read(l1)}?
	 * @throws Throwable
	 */
	public void test2() throws Throwable {
		Effect e1 = new Effect_c(Effects.FUN);	
		e1.addRead(l1);

		Effect e2 = new Effect_c(Effects.FUN);
		e2.addRead(l1);
		
		boolean result = e1.commutesWith(e2);
		assertTrue(result);
		
	}
	
	/**
	 * Does {read(l1)} commute with {write(l1)}?
	 * @throws Throwable
	 */
	public void test3() throws Throwable {
		Effect e1 = new Effect_c(Effects.FUN);	
		e1.addRead(l1);

		Effect e2 = new Effect_c(Effects.FUN);
		e2.addWrite(l1);
		
		boolean result = e1.commutesWith(e2);
		assertFalse(result);
		
	}

	/**
	 * Does {read(l1)} commute with {atomic(l1)}?
	 * @throws Throwable
	 */
	public void test4() throws Throwable {
		Effect e1 = new Effect_c(Effects.FUN);	
		e1.addRead(l1);

		Effect e2 = new Effect_c(Effects.FUN);
		e2.addAtomicInc(l1);
		
		boolean result = e1.commutesWith(e2);
		assertFalse(result);
		
	}
	
	/**
	 * Does {write(l1)} commute with {read(l1)}?
	 * @throws Throwable
	 */
	public void test5() throws Throwable {
		Effect e1 = new Effect_c(Effects.FUN);	
		e1.addWrite(l1);

		Effect e2 = new Effect_c(Effects.FUN);
		e2.addRead(l1);
		
		boolean result = e1.commutesWith(e2);
		assertFalse(result);
		
	}
	
	/**
	 * Does {write(l1)} commute with {write(l1)}?
	 * @throws Throwable
	 */
	public void test6() throws Throwable {
		Effect e1 = new Effect_c(Effects.FUN);	
		e1.addWrite(l1);

		Effect e2 = new Effect_c(Effects.FUN);
		e2.addWrite(l1);
		
		boolean result = e1.commutesWith(e2);
		assertFalse(result);
		
	}
	
	/**
	 * Does {write(l1)} commute with {atomicInc(l1)}?
	 * @throws Throwable
	 */
	public void test7() throws Throwable {
		Effect e1 = new Effect_c(Effects.FUN);	
		e1.addWrite(l1);

		Effect e2 = new Effect_c(Effects.FUN);
		e2.addAtomicInc(l1);
		
		boolean result = e1.commutesWith(e2);
		assertFalse(result);
		
	}

	/**
	 * Does {atomicInc(l1)} commute with {read(l1)}?
	 * @throws Throwable
	 */
	public void test8() throws Throwable {
		Effect e1 = new Effect_c(Effects.FUN);	
		e1.addAtomicInc(l1);

		Effect e2 = new Effect_c(Effects.FUN);
		e2.addRead(l1);
		
		boolean result = e1.commutesWith(e2);
		assertFalse(result);
		
	}
	
	/**
	 * Does {atomicInc(l1)} commute with {write(l1)}?
	 * @throws Throwable
	 */
	public void test9() throws Throwable {
		Effect e1 = new Effect_c(Effects.FUN);	
		e1.addAtomicInc(l1);

		Effect e2 = new Effect_c(Effects.FUN);
		e2.addWrite(l1);
		
		boolean result = e1.commutesWith(e2);
		assertFalse(result);
		
	}
	
	/**
	 * Does {atomicInc(l1)} commute with {atomicInc(l1)}?
	 * @throws Throwable
	 */
	public void test10() throws Throwable {
		Effect e1 = new Effect_c(Effects.FUN);	
		e1.addAtomicInc(l1);

		Effect e2 = new Effect_c(Effects.FUN);
		e2.addAtomicInc(l1);
		
		boolean result = e1.commutesWith(e2);
		assertFalse(result);
		
	}
	
	/**
	 * Is {read(l1)}.exists(l1).equals({})
	 * @throws Throwable
	 */
	public void test11() throws Throwable {
		Effect e1 = new Effect_c(Effects.FUN);	
		e1.addRead(l1);

		Effect e = e1.exists(l1);
		Effect e2 = new Effect_c(Effects.FUN);

		boolean result = e.equals(e2);
		assertTrue(result);
		
	}

	/**
	 * Is {read(l1,l2)}.exists(l1).equals({read(l2)})
	 * @throws Throwable
	 */
	public void test12() throws Throwable {
		Effect e1 = new Effect_c(Effects.FUN);	
		e1.addRead(l1);
		e1.addRead(l2);

		Effect e = e1.exists(l1);
		Effect e2 = new Effect_c(Effects.FUN);
		e2.addRead(l2);
		boolean result = e.equals(e2);
		assertTrue(result);
		
	}

	/**
	 * Is {read(L.f)}.exists(L,M).equals({read(M.f)})
	 * @throws Throwable
	 */
	public void test13() throws Throwable {
		XLocal L = new XLocal("L");
		FieldLocs Lf = Effects.makeFieldLocs(L, "f");
		XLocal M = new XLocal("M");
		FieldLocs Mf = Effects.makeFieldLocs(M, "f");
		Effect e1 = new Effect_c(Effects.FUN);	
		e1.addRead(Lf);

		
		Effect e = e1.exists(L, M);
		Effect e2 = new Effect_c(Effects.FUN);
		e2.addRead(Mf);

		boolean result = e.equals(e2);
		assertTrue(result);
		
	}
	
	/**
	 * Is {read(L(1))}.exists(L,M).equals({read(M(1))})?
	 * @throws Throwable
	 */
	public void test14() throws Throwable {
		XLocal L = new XLocal("L");
		ArrayElementLocs L1 = Effects.makeArrayElementLocs(L, XTerms.makeLit(1));
		XLocal M = new XLocal("M");
		ArrayElementLocs M1 = Effects.makeArrayElementLocs(M, XTerms.makeLit(1));
		Effect e1 = new Effect_c(Effects.FUN);	
		e1.addRead(L1);

		
		Effect e = e1.exists(L, M);
		Effect e2 = new Effect_c(Effects.FUN);
		e2.addRead(M1);

		boolean result = e.equals(e2);
		assertTrue(result);
		
	}

	/**
	 * Is {read(L(1))}.exists(L,M).equals({read(M(1))})?
	 * @throws Throwable
	 */
	public void test15() throws Throwable {
		XLocal L = new XLocal("L");
		XLocal One = new XLocal("1");
		XLocal Two = new XLocal("2");
		ArrayElementLocs L1 = Effects.makeArrayElementLocs(L, One);
		ArrayElementLocs L2 = Effects.makeArrayElementLocs(L, Two);
		Effect e1 = new Effect_c(Effects.FUN);	
		e1.addRead(L1);

		
		Effect e = e1.exists(One, Two);
		Effect e2 = new Effect_c(Effects.FUN);
		e2.addRead(L2);

		boolean result = e.equals(e2);
		assertTrue(result);
		
	}

	public void test16() throws Throwable {
		XLocal L = new XLocal("L");
		XLocal A = new XLocal("A");
		ArrayLocs L1 = Effects.makeArrayLocs(L);
		LocalLocs Al = Effects.makeLocalLocs(A);
		ArrayElementLocs LA = Effects.makeArrayElementLocs(L, A);
		Effect e1 = new Effect_c(Effects.FUN);	
		e1.addRead(L1);
		e1.addRead(Al);
        e1.addWrite(LA);
		
		Effect e = e1.forall(A);
		
		Effect e2 = new Effect_c(Effects.FUN);
		e2.addRead(L1);
		e2.addRead(Al);
		e2.addWrite(L1);
		 
		boolean result = e.equals(e2);
		assertTrue(result);
		
	}

	public void test17() throws Throwable {
		XLocal L = new XLocal("L");
		XLocal A = new XLocal("A");
		ArrayLocs L1 = Effects.makeArrayLocs(L);
		LocalLocs Al = Effects.makeLocalLocs(A);
		ArrayElementLocs LA = Effects.makeArrayElementLocs(L, A);
		Effect e1 = new Effect_c(Effects.FUN);	
		//e1.addRead(L1);
		e1.addRead(Al);
        e1.addWrite(LA);
		boolean result = e1.commutesWithForall(A);
		
		assertTrue(result);
		
	}
}
