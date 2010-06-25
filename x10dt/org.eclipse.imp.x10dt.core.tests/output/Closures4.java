
public class Closures4
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<Closures4>_RTT = new x10.rtt.RuntimeType<Closures4>(
/* base class */Closures4.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 20
static class C
                extends x10.core.Ref
                  implements x10.core.fun.Fun_0_2<java.lang.Integer,java.lang.Integer,java.lang.Integer>
                {public static final x10.rtt.RuntimeType<Closures4.
      C>_RTT = new x10.rtt.RuntimeType<Closures4.
      C>(
    /* base class */Closures4.
      C.class
    , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.core.fun.Fun_0_2._RTT, x10.rtt.Types.INT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    // bridge for method abstract public (a1:Z1, a2:Z2)=> U.apply(a1:Z1a2:Z2): U
    public java.lang.Integer
      apply$G(java.lang.Integer a1,
    java.lang.Integer a2){return apply((int) a1,
    (int) a2);}
    
        
        
//#line 21
public int
                      apply(
                      final int i,
                      final int j){
            
//#line 22
return ((((int)(i))) + (((int)(j))));
        }
        
        public C() {
            super();
        }
    
    }
    
    
    
//#line 26
public boolean
                  run(
                  ){
        
//#line 27
final Closures4.
          C x =
          ((Closures4.
          C)(new Closures4.
          C()));
        
//#line 28
final int j =
          x.apply((int)(3),
                  (int)(4));
        
//#line 29
return ((int) j) ==
        ((int) 7);
    }
    
    
//#line 32
/* template:Main { */
    public static class Main extends x10.runtime.impl.java.Runtime {
    	public static void main(java.lang.String[] args) {
    		// start native runtime
    		new Main().start(args);
    	}
    
    	// called by native runtime inside main x10 thread
    	public void main(final x10.core.Rail<java.lang.String> args) {
    		try {
    
    			// start xrx
    			x10.lang.Runtime.start(
    				// static init activity
    				new x10.core.fun.VoidFun_0_0() {
    					public void apply() {
    						// preload classes
    						if (Boolean.getBoolean("x10.PRELOAD_CLASSES")) {
    							x10.runtime.impl.java.PreLoader.preLoad(this.getClass().getEnclosingClass(), Boolean.getBoolean("x10.PRELOAD_STRINGS"));
    						}
    					}
    				},
    				// body of main activity
    				new x10.core.fun.VoidFun_0_0() {
    					public void apply() {
    						// catch and rethrow checked exceptions
    						// (closures cannot throw checked exceptions)
    						try {
    							// call the original app-main method
    							Closures4.main(args);
    						} catch (java.lang.RuntimeException e) {
    							throw e;
    						} catch (java.lang.Error e) {
    							throw e;
    						} catch (java.lang.Throwable t) {
    			 		   		throw new x10.lang.MultipleExceptions(t);
    			 		   	}
    					}
    				});
    
    		} catch (java.lang.Throwable t) {
    			t.printStackTrace();
    		}
    	}
    }
    
    // the original app-main method
    public static void main(x10.core.Rail<java.lang.String> args)  {
        
//#line 33
new Closures4().execute();
    }/* } */
    
    public Closures4() {
        super();
    }

}
