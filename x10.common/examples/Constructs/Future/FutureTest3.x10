/**
 *@author kemal 4/2005
 *
 * Tests exceptions and side effects
 * within activities spawned by a future expression.
 *
 * Checks that future{e}.force() acts as a global finish
 * on e, and throws e's  exceptions
 *
 */

public class FutureTest3 {
	const int N=8;
	const int OUTOFRANGE=99;

	/**
 	 * Spawns subactivities that cause delayed side-effects.
	 */
	int m1(int[.] A, int K) {
		foreach(point [i]:A) {
			delay(3000); 
			atomic A[i]+=1;
		}
		int t;
 		atomic t=A[K]; //returns old value
		return t;
	}

	/**
 	 * Spawns subactivities that cause delayed side-effects
	 * and exceptions.
	 */
	int m2(int[.] A, int K) {
		foreach(point p[i]:A) {
			delay(3000); 
			atomic A[i]+=1;
			atomic A[OUTOFRANGE]= -1;
		}
		int t;
 		atomic t=A[K];//returns old value
		return t;
	}

	/**
	 * sleep for millis milliseconds.
	 */
	static void delay(int millis) {
		try {
			java.lang.Thread.sleep(millis);
		} catch(InterruptedException e) {
		}
	}

	/** 
	 * testing future with subactivities with
	 * side effects and exceptions.
	 */
	public boolean run() {

		int[.] A = new int[[0:N-1]->here];
		int K=3;
		boolean gotException;


		// side effect in expression

		// (need atomic here if there is sharing. x10 should support atomic{expression})
		int r1=future(here){(A[K]+=1)}.force();
		System.out.println("1");
		atomic chk(A[K]==1);
		chk(r1==1);

		// exception in expression
		int r2= -1;
		gotException=false;
		try {
			r2=future(here){(A[OUTOFRANGE]+=1)}.force();
		} catch(ArrayIndexOutOfBoundsException e) {
			gotException=true;
		}
		System.out.println("2");
		chk(r2== -1 && gotException);
		

		//subactivities of e must be finished
		//when future{e}.force() returns
		int r3= -1;
		gotException=false;
		try {
			r3=future(here){m1(A,K)}.force();
		} catch(Throwable e) {
			gotException=true;
		}
		System.out.println("3");
		chk(r3==1 && !gotException);
		// must read new values of A here
		for(point [i]:A) System.out.println("A["+i+"]="+A[i]);
		chk(A[K]==2);
		for(point [i]:A) atomic chk(imp(i!=K,A[i]==1));

		//future{e}.force() must throw
		//exceptions from subactivities of e
		int r4= -1;
		gotException=false;
		try {
			r4=future(here){m2(A,K)}.force();
		} catch(Throwable e) {
			gotException=true;
		}
		System.out.println("4");
		chk(r4==2 && gotException);
		// must read new values of A here
		for(point [i]:A) System.out.println("A["+i+"]="+A[i]);
		atomic chk(A[K]==3);
		for(point [i]:A) atomic chk(imp(i!=K,A[i]==2));

		//Only force() throws the exception,
		//a plain future call just spawns the expression
		future<int> fr5=future(here){m2(A,K)};
		System.out.println("5");
		// must read old values of A here
		atomic chk(A[K]==3);
		for(point [i]:A) atomic chk(imp(i!=K,A[i]==2));
		int r5= -1;
		gotException=false;
		try {
		 	r5=fr5.force();
		} catch(Throwable e) {
			gotException=true;
		}
		chk(r5==3 && gotException);
		// must read new values of A here
		for(point [i]:A) System.out.println("A["+i+"]="+A[i]);
		atomic chk(A[K]==4);
		for(point [i]:A) atomic chk(imp(i!=K,A[i]==3));

		return true;
	}

 	/**
	 * True iff x logically implies y
	 */
	static boolean imp(boolean x, boolean y) {
		return (!x||y);
	}

	/**
	 * Throws an error iff b is false
	 */
	static void chk(boolean b) {
		if (!b) throw new Error();
	}

	/**
	 * main method
	 */
	public static void main(String args[]) {
		boolean b=false;
		try {
			b= (new FutureTest3()).run();
		} catch (Throwable e) {
			e.printStackTrace();
			b=false;
		}
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
