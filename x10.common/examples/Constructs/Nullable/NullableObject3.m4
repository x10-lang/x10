//Generated automatically by 
//m4 NullableObject3.m4 > NullableObject3.x10
//Do not edit
define(`isCastable',
`
		
		{
			// x can ifelse($3,`no',not )be cast to ($2)
			boolean castable=true;
		 	try{
				$2 __y=($2)$1;
				X.use(__y);
			} catch(ClassCastException e) {
				castable=false;
			}
			if (ifelse($3,`yes',`!',`')castable) throw new Error();
		}')

/**
 *
 * class cast test for nullable types
 *
 * In X10, nullable Object is a proper supertype of Object
 * (the latter does not include null, the former does).
 *
 * 
 * Tests miscellaneous classcast behavior with nullable types
 *
 * @author kemal
 * 1/2005
 * 
 */
public class NullableObject3 {
	public  boolean run() {

		{
		x10.lang.Object x = new boxedInt(1);
		isCastable(x,x10.lang.Object,yes)
		isCastable(x,nullable x10.lang.Object,yes)
		isCastable(x,boxedInt,yes)
		isCastable(x,nullable boxedInt,yes)
		isCastable(x,boxedLong,no)
		isCastable(x,nullable boxedLong,no)
		}

		{
		x10.lang.Object x = new boxedLong(1);
		isCastable(x,x10.lang.Object,yes)
		isCastable(x,nullable x10.lang.Object,yes)
		isCastable(x,boxedInt,no)
		isCastable(x,nullable boxedInt,no)
		isCastable(x,boxedLong,yes)
		isCastable(x,nullable boxedLong,yes)
		}

		{
		x10.lang.Object x = new x10.lang.Object();
		isCastable(x,x10.lang.Object,yes)
		isCastable(x,nullable x10.lang.Object,yes)
		isCastable(x,boxedInt,no)
		isCastable(x,nullable boxedInt,no)
		isCastable(x,boxedLong,no)
		isCastable(x,nullable boxedLong,no)
		}

		{
		nullable x10.lang.Object x=null;
		isCastable(x,x10.lang.Object,no)
		isCastable(x,nullable x10.lang.Object,yes)
		isCastable(x,boxedInt,no)
		isCastable(x,nullable boxedInt,yes)
		isCastable(x,boxedLong,no)
		isCastable(x,nullable boxedLong,yes)
		}

		{
		nullable x10.lang.Object x=new boxedInt(1);
		isCastable(x,x10.lang.Object,yes)
		isCastable(x,nullable x10.lang.Object,yes)
		isCastable(x,boxedInt,yes)
		isCastable(x,nullable boxedInt,yes)
		isCastable(x,boxedLong,no)
		isCastable(x,nullable boxedLong,no)
		}

		{
		nullable x10.lang.Object x=new boxedLong(1);
		isCastable(x,x10.lang.Object,yes)
		isCastable(x,nullable x10.lang.Object,yes)
		isCastable(x,boxedInt,no)
		isCastable(x,nullable boxedInt,no)
		isCastable(x,boxedLong,yes)
		isCastable(x,nullable boxedLong,yes)
		}

		{
		nullable x10.lang.Object x=new x10.lang.Object();
		isCastable(x,x10.lang.Object,yes)
		isCastable(x,nullable x10.lang.Object,yes)
		isCastable(x,boxedInt,no)
		isCastable(x,nullable boxedInt,no)
		isCastable(x,boxedLong,no)
		isCastable(x,nullable boxedLong,no)
		}


		{
		boxedInt x = new boxedInt(1);
		isCastable(x,x10.lang.Object,yes)
		isCastable(x,nullable x10.lang.Object,yes)
		isCastable(x,boxedInt,yes)
		isCastable(x,nullable boxedInt,yes)
		}

		{
		nullable boxedInt x=null;
		isCastable(x,x10.lang.Object,no)
		isCastable(x,nullable x10.lang.Object,yes)
		isCastable(x,boxedInt,no)
		isCastable(x,nullable boxedInt,yes)
		isCastable((nullable x10.lang.Object)x,nullable boxedLong,yes)

		}

		{
		nullable boxedInt x=new boxedInt(1);
		isCastable(x,x10.lang.Object,yes)
		isCastable(x,nullable x10.lang.Object,yes)
		isCastable(x,boxedInt,yes)
		isCastable(x,nullable boxedInt,yes)
		}


		{
		boxedLong x = new boxedLong(1);
		isCastable(x,x10.lang.Object,yes)
		isCastable(x,nullable x10.lang.Object,yes)
		isCastable(x,boxedLong,yes)
		isCastable(x,nullable boxedLong,yes)
		}

		{
		nullable boxedLong x=null;
		isCastable(x,x10.lang.Object,no)
		isCastable(x,nullable x10.lang.Object,yes)
		isCastable((nullable x10.lang.Object)x,nullable boxedInt,yes)
		isCastable(x,boxedLong,no)
		isCastable(x,nullable boxedLong,yes)
		}

		{
		nullable boxedLong x=new boxedLong(1);
		isCastable(x,x10.lang.Object,yes)
		isCastable(x,nullable x10.lang.Object,yes)
		isCastable(x,boxedLong,yes)
		isCastable(x,nullable boxedLong,yes)
		}

		return true;
		
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new NullableObject3()).run();
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
 * Helper class -- boxed int
 */

class boxedInt extends x10.lang.Object {
	int val;
	boxedInt(int x) {val=x;}
}

/**
 * Helper class -- boxed long
 */

class boxedLong extends x10.lang.Object {
	long val;
	boxedLong(long x) {val=x;}
}

class X {
		static void use( nullable java.lang.Object y) {}
}
