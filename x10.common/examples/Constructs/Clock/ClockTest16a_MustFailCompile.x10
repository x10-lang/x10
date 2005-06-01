/**
 * @author kemal 5/2005
 *
 *The x10 compiler must conservatively check if an activity can 
 *potentially
 *pass a clock it already dropped,
 *to a subactivity.
 *If so, it must report a compile time error.
 *
 *Language clarification needed on disambiguation
 *algorithm to use.
 *Compiler analysis may not be possible in many cases, such
 *as async's invoked in library methods and indirectly called methods.
 *
 */

/**
 * A class to invoke a 'function pointer' that may do an async
 */
class Y {
    static void test(final foo f) {
	{
          f.apply(); // it is hard to determine f does an async clocked(c) S,
                     //where the current activity is not registered on c
          next;
	}
    }
}


public class ClockTest16a_MustFailCompile {

	public boolean run() {
           final X x=new X();
           finish async {
		final clock c0=clock.factory.clock(); 
		final clock c1=clock.factory.clock();
		final clock[] ca= new clock[] {c0, c1};
                (ca[0]).drop();

                // Question:
                // Can an activity ever pass a dropped clock to a
                // subactivity of itself, in statement async(cx) S?

                // Compiler answer: NO, actual runtime answer: NO
                // no compiler error
                {
                    final clock cx=ca[1];
		    async clocked(cx) {// no clock use error 
			next;
                    }
                }

                // Compiler: MAYBE, actual: NO
                // must have a compiler error
                {
                    final clock cx=ca[x.one()];
		    async clocked(cx) { //no clock use error
                       next;
                    }
                }

                foo f0=new foo() {
                   public void apply() {
                      final clock cx=ca[x.zero()];
		      async clocked(cx) { //clock use error
                         next;
                      }
                   }
                };

                foo f1=new foo() {
                   public void apply() {
                      final clock cx=ca[x.one()];
		      async clocked(cx) { //no clock use error
                         next;
                      }
                   }
                };

	        final foo[] fooArray=new foo[] {f0,f1};

                // Compiler: MAYBE, actual: NO
                // must have a compiler error
                Y.test(fooArray[x.one()]);
                 
                System.out.println("point #1");
                // Compiler: MAYBE, actual: YES
                // must have a compiler error
                Y.test(fooArray[x.zero()]);
                       
                System.out.println("point #2");
                // Compiler: MAYBE, actual: YES
                // must have a compiler error
	        {
                    final clock cx=ca[x.zero()];
		    async clocked(cx) { // clock use error 
                       next;
                    }
                }

                System.out.println("point #3");
                // Compiler: YES, actual:YES
                // must have a compiler error
	        {
                    final clock cx=ca[0];
		    async clocked(cx) { // clock use error 
                       next;
                    }
                }

           }

	   return true;

	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new ClockTest16a_MustFailCompile()).run();
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
 *
 * foo f1=new foo(){public void apply() S1}; //assign body S1 to f1
 *
 * // values of free final variables of S1 are also captured in f1.
 *
 * f1.apply(); // invoke S1 indirectly using its captured
 *
 * // free variables 
 *
 */

interface foo {
    public void apply();
}


/** 
 * Dummy class to make static memory disambiguation difficult
 * for a typical compiler
 */
class X {
    public int[] z={1,0};
    int zero() { return z[z[z[1]]];} 
    int one() { return z[z[z[0]]];} 
    void modify() {z[0]+=1;}
}
