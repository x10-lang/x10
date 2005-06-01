//Generated automatically by 
//m4 NullableObject3.m4  NullableObject3.x10
//Do not edit


import x10.lang.*;
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
		
		
		{
			// x can be cast to (x10.lang.Object)
			boolean castable=true;
		 	try{
				x10.lang.Object __y=(x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable x10.lang.Object)
			boolean castable=true;
		 	try{
				nullable x10.lang.Object __y=(nullable x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (boxedInt)
			boolean castable=true;
		 	try{
				boxedInt __y=(boxedInt)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable boxedInt)
			boolean castable=true;
		 	try{
				nullable boxedInt __y=(nullable boxedInt)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can not be cast to (boxedLong)
			boolean castable=true;
		 	try{
				boxedLong __y=(boxedLong)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		
		
		{
			// x can not be cast to (nullable boxedLong)
			boolean castable=true;
		 	try{
				nullable boxedLong __y=(nullable boxedLong)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		}

		{
		x10.lang.Object x = new boxedLong(1);
		
		
		{
			// x can be cast to (x10.lang.Object)
			boolean castable=true;
		 	try{
				x10.lang.Object __y=(x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable x10.lang.Object)
			boolean castable=true;
		 	try{
				nullable x10.lang.Object __y=(nullable x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can not be cast to (boxedInt)
			boolean castable=true;
		 	try{
				boxedInt __y=(boxedInt)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		
		
		{
			// x can not be cast to (nullable boxedInt)
			boolean castable=true;
		 	try{
				nullable boxedInt __y=(nullable boxedInt)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		
		
		{
			// x can be cast to (boxedLong)
			boolean castable=true;
		 	try{
				boxedLong __y=(boxedLong)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable boxedLong)
			boolean castable=true;
		 	try{
				nullable boxedLong __y=(nullable boxedLong)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		}

		{
		x10.lang.Object x = new x10.lang.Object();
		
		
		{
			// x can be cast to (x10.lang.Object)
			boolean castable=true;
		 	try{
				x10.lang.Object __y=(x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable x10.lang.Object)
			boolean castable=true;
		 	try{
				nullable x10.lang.Object __y=(nullable x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can not be cast to (boxedInt)
			boolean castable=true;
		 	try{
				boxedInt __y=(boxedInt)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		
		
		{
			// x can not be cast to (nullable boxedInt)
			boolean castable=true;
		 	try{
				nullable boxedInt __y=(nullable boxedInt)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		
		
		{
			// x can not be cast to (boxedLong)
			boolean castable=true;
		 	try{
				boxedLong __y=(boxedLong)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		
		
		{
			// x can not be cast to (nullable boxedLong)
			boolean castable=true;
		 	try{
				nullable boxedLong __y=(nullable boxedLong)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		}

		{
		nullable x10.lang.Object x=null;
		
		
		{
			// x can not be cast to (x10.lang.Object)
			boolean castable=true;
		 	try{
				x10.lang.Object __y=(x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable x10.lang.Object)
			boolean castable=true;
		 	try{
				nullable x10.lang.Object __y=(nullable x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can not be cast to (boxedInt)
			boolean castable=true;
		 	try{
				boxedInt __y=(boxedInt)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable boxedInt)
			boolean castable=true;
		 	try{
				nullable boxedInt __y=(nullable boxedInt)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can not be cast to (boxedLong)
			boolean castable=true;
		 	try{
				boxedLong __y=(boxedLong)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable boxedLong)
			boolean castable=true;
		 	try{
				nullable boxedLong __y=(nullable boxedLong)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		}

		{
		nullable x10.lang.Object x=new boxedInt(1);
		
		
		{
			// x can be cast to (x10.lang.Object)
			boolean castable=true;
		 	try{
				x10.lang.Object __y=(x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable x10.lang.Object)
			boolean castable=true;
		 	try{
				nullable x10.lang.Object __y=(nullable x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (boxedInt)
			boolean castable=true;
		 	try{
				boxedInt __y=(boxedInt)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable boxedInt)
			boolean castable=true;
		 	try{
				nullable boxedInt __y=(nullable boxedInt)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can not be cast to (boxedLong)
			boolean castable=true;
		 	try{
				boxedLong __y=(boxedLong)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		
		
		{
			// x can not be cast to (nullable boxedLong)
			boolean castable=true;
		 	try{
				nullable boxedLong __y=(nullable boxedLong)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		}

		{
		nullable x10.lang.Object x=new boxedLong(1);
		
		
		{
			// x can be cast to (x10.lang.Object)
			boolean castable=true;
		 	try{
				x10.lang.Object __y=(x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable x10.lang.Object)
			boolean castable=true;
		 	try{
				nullable x10.lang.Object __y=(nullable x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can not be cast to (boxedInt)
			boolean castable=true;
		 	try{
				boxedInt __y=(boxedInt)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		
		
		{
			// x can not be cast to (nullable boxedInt)
			boolean castable=true;
		 	try{
				nullable boxedInt __y=(nullable boxedInt)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		
		
		{
			// x can be cast to (boxedLong)
			boolean castable=true;
		 	try{
				boxedLong __y=(boxedLong)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable boxedLong)
			boolean castable=true;
		 	try{
				nullable boxedLong __y=(nullable boxedLong)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		}

		{
		nullable x10.lang.Object x=new x10.lang.Object();
		
		
		{
			// x can be cast to (x10.lang.Object)
			boolean castable=true;
		 	try{
				x10.lang.Object __y=(x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable x10.lang.Object)
			boolean castable=true;
		 	try{
				nullable x10.lang.Object __y=(nullable x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can not be cast to (boxedInt)
			boolean castable=true;
		 	try{
				boxedInt __y=(boxedInt)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		
		
		{
			// x can not be cast to (nullable boxedInt)
			boolean castable=true;
		 	try{
				nullable boxedInt __y=(nullable boxedInt)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		
		
		{
			// x can not be cast to (boxedLong)
			boolean castable=true;
		 	try{
				boxedLong __y=(boxedLong)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		
		
		{
			// x can not be cast to (nullable boxedLong)
			boolean castable=true;
		 	try{
				nullable boxedLong __y=(nullable boxedLong)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		}


		{
		boxedInt x = new boxedInt(1);
		
		
		{
			// x can be cast to (x10.lang.Object)
			boolean castable=true;
		 	try{
				x10.lang.Object __y=(x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable x10.lang.Object)
			boolean castable=true;
		 	try{
				nullable x10.lang.Object __y=(nullable x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (boxedInt)
			boolean castable=true;
		 	try{
				boxedInt __y=(boxedInt)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable boxedInt)
			boolean castable=true;
		 	try{
				nullable boxedInt __y=(nullable boxedInt)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		}

		{
		nullable boxedInt x=null;
		
		
		{
			// x can not be cast to (x10.lang.Object)
			boolean castable=true;
		 	try{
				x10.lang.Object __y=(x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable x10.lang.Object)
			boolean castable=true;
		 	try{
				nullable x10.lang.Object __y=(nullable x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can not be cast to (boxedInt)
			boolean castable=true;
		 	try{
				boxedInt __y=(boxedInt)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable boxedInt)
			boolean castable=true;
		 	try{
				nullable boxedInt __y=(nullable boxedInt)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable boxedLong)
			boolean castable=true;
		 	try{
				nullable boxedLong __y=(nullable boxedLong)(nullable x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}

		}

		{
		nullable boxedInt x=new boxedInt(1);
		
		
		{
			// x can be cast to (x10.lang.Object)
			boolean castable=true;
		 	try{
				x10.lang.Object __y=(x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable x10.lang.Object)
			boolean castable=true;
		 	try{
				nullable x10.lang.Object __y=(nullable x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (boxedInt)
			boolean castable=true;
		 	try{
				boxedInt __y=(boxedInt)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable boxedInt)
			boolean castable=true;
		 	try{
				nullable boxedInt __y=(nullable boxedInt)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		}


		{
		boxedLong x = new boxedLong(1);
		
		
		{
			// x can be cast to (x10.lang.Object)
			boolean castable=true;
		 	try{
				x10.lang.Object __y=(x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable x10.lang.Object)
			boolean castable=true;
		 	try{
				nullable x10.lang.Object __y=(nullable x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (boxedLong)
			boolean castable=true;
		 	try{
				boxedLong __y=(boxedLong)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable boxedLong)
			boolean castable=true;
		 	try{
				nullable boxedLong __y=(nullable boxedLong)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		}

		{
		nullable boxedLong x=null;
		
		
		{
			// x can not be cast to (x10.lang.Object)
			boolean castable=true;
		 	try{
				x10.lang.Object __y=(x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable x10.lang.Object)
			boolean castable=true;
		 	try{
				nullable x10.lang.Object __y=(nullable x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable boxedInt)
			boolean castable=true;
		 	try{
				nullable boxedInt __y=(nullable boxedInt)(nullable x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can not be cast to (boxedLong)
			boolean castable=true;
		 	try{
				boxedLong __y=(boxedLong)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable boxedLong)
			boolean castable=true;
		 	try{
				nullable boxedLong __y=(nullable boxedLong)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		}

		{
		nullable boxedLong x=new boxedLong(1);
		
		
		{
			// x can be cast to (x10.lang.Object)
			boolean castable=true;
		 	try{
				x10.lang.Object __y=(x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable x10.lang.Object)
			boolean castable=true;
		 	try{
				nullable x10.lang.Object __y=(nullable x10.lang.Object)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (boxedLong)
			boolean castable=true;
		 	try{
				boxedLong __y=(boxedLong)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
		
		
		{
			// x can be cast to (nullable boxedLong)
			boolean castable=true;
		 	try{
				nullable boxedLong __y=(nullable boxedLong)x;
				X.use(__y);
			} catch(java.lang.Exception e) {
				castable=false;
			}
			if (!castable) throw new Error();
		}
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
