/**
 * @author kemal 11/2005
 *
 * Language clarification needed.
 *
 * It may not be practical to enforce the rule
 * "No async or when or finish or future or force or next inside an atomic"
 * at compile time.
 *
 * The following tentative rule is enforced by this test case, for now.
 *
 * If an async or when or finish or future or force or next is executed
 * inside an atomic, it is detected at run time, and should
 * throw an exception (of type TBD).
 *
 */
/**
 * A class to invoke a 'function pointer' inside of async
 */
class Y {
    static void test(final foo f, final lockStruct l) {
        // Compiler analysis may not be possible here
          f.apply(l); // it is hard to determine if f executes an async or when
    }
}

/**
 * class containing misc. synchronization constructs
 */

class lockStruct {
    boolean lock0;
    boolean lock1;
    future<int> futureInt;
    clock c;
    lockStruct() {
	futureInt= future{1};
	lock0=false;
	lock1=false;
	c= clock.factory.clock();
    }
}

public class AtomicContainingWhen_MustFailRun {

final foo[] fooArray=new foo[] {new f0(),new f1(),new f2(), new f3()};

	public boolean run() {
           final X x=new X();

	   final lockStruct l=new lockStruct();

           atomic Y.test(fooArray[x.one()],l);
           atomic Y.test(fooArray[x.zero()],l);
           atomic Y.test(fooArray[x.two()],l);
           atomic Y.test(fooArray[3],l);

	   return true;

	}

        // just to confuse a compiler
	public void modifyFoo(X x) { fooArray[x.two()+1]=new f2();}
	
    /**
     * main method
     */
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new AtomicContainingWhen_MustFailRun()).run();
        } catch (Throwable e) {
                e.printStackTrace();
                b.val=false;
        }
        System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
        x10.lang.Runtime.setExitCode(b.val?0:1);
    }
    static class boxedBoolean {
        boolean val=false;
    }


}

/**
 * An interface to use like a simple 'function pointer'
 */
interface foo {
    public void apply(lockStruct l);
}

class f0 implements foo {
	// it is hard to determine if this is invoked inside
        // an atomic, at compile time.
	public void apply(final lockStruct l) {
	    System.out.println("in f0:#1");
	    when(!l.lock0) l.lock0=true;
	    System.out.println("in f0:#2");
	}
}
class f1 implements foo {
	// it is hard to determine if this is invoked inside
        // an atomic, at compile time.
	public void apply(final lockStruct l) {
	    System.out.println("in f1:#1");
	    when(!l.lock1) l.lock1=true;
	    System.out.println("in f1:#2");
	}
}

class f2 implements foo {
	// it is hard to determine if this is invoked inside
        // an atomic, at compile time.
	public void apply(final lockStruct l) {
	    System.out.println("in f2:#1");
            l.c.resume();
	    System.out.println("in f2:#2");
	    finish async {}
	    System.out.println("in f2:#3");
	    int x=future{l.futureInt.force()}.force();
	    System.out.println("in f2:#4");
	    next;
	    System.out.println("in f2:#5");
	}
}

class f3 implements foo {
	public void apply(final lockStruct l) {
         	System.out.println("in f3");
	}
}

/** 
 * Dummy class to make static memory disambiguation difficult
 * for a typical compiler
 */
class X {
    public int[] z={1,0};
    int zero() { return z[z[z[1]]];} 
    int one() { return z[z[z[0]]];} 
    int two() { return (zero()-1)*(zero()-1)+one(); }
    void modify() {z[0]+=1;}
}
