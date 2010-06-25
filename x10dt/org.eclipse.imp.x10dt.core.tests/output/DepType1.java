
public class DepType1
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<DepType1>_RTT = new x10.rtt.RuntimeType<DepType1>(
/* base class */DepType1.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 19
final public int
      i;
    
//#line 19
final public int
      j;
    
    
//#line 20
final int
      v;
    
//#line 21
final boolean
      b;
    
//#line 24
static class Test
                extends DepType1
                {public static final x10.rtt.RuntimeType<DepType1.
      Test>_RTT = new x10.rtt.RuntimeType<DepType1.
      Test>(
    /* base class */DepType1.
      Test.class
    , /* parents */ new x10.rtt.Type[] {DepType1._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 24
final public int
          k;
        
        
//#line 19
final private DepType1
          out$;
        
        
//#line 25
Test(final DepType1 out$,
                         final int k) {
            
//#line 26
super(k,
                              k);
            
//#line 19
this.out$ = out$;
            
//#line 27
this.k = k;
        }
        
        final public int
          k(
          ){
            return this.
                     k;
        }
    
    }
    
    
//#line 30
DepType1.
      Test
      t;
    
    
//#line 33
public DepType1(final int i,
                                final int j) {
        
//#line 33
super();
        
//#line 30
this.t = null;
        
//#line 34
this.i = i;
        
//#line 34
this.j = j;
        
//#line 35
this.v = 0;
        
//#line 36
this.b = true;
    }
    
    
//#line 40
public boolean
                  run(
                  ){
        
//#line 41
final DepType1 d =
          ((DepType1)(new DepType1(3,
                                   6)));
        
//#line 42
return true;
    }
    
    
//#line 45
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
    							DepType1.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> a)  {
        
//#line 46
new DepType1(3,
                                 9).execute();
    }/* } */
    
    final public int
      i(
      ){
        return this.
                 i;
    }
    
    final public int
      j(
      ){
        return this.
                 j;
    }

}
