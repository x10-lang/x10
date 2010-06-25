
public class DepTypeConsistency1_MustFailCompile
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<DepTypeConsistency1_MustFailCompile>_RTT = new x10.rtt.RuntimeType<DepTypeConsistency1_MustFailCompile>(
/* base class */DepTypeConsistency1_MustFailCompile.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 23
static class Tester
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<DepTypeConsistency1_MustFailCompile.
      Tester>_RTT = new x10.rtt.RuntimeType<DepTypeConsistency1_MustFailCompile.
      Tester>(
    /* base class */DepTypeConsistency1_MustFailCompile.
      Tester.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 23
final public int
          i;
        
        
//#line 20
final private DepTypeConsistency1_MustFailCompile
          out$;
        
        
//#line 24
public Tester(final DepTypeConsistency1_MustFailCompile out$,
                                  final int arg) {
            
//#line 24
super();
            
//#line 20
this.out$ = out$;
            
//#line 24
this.i = arg;
        }
        
        final public int
          i(
          ){
            return this.
                     i;
        }
    
    }
    
    
    
//#line 27
public boolean
                  run(
                  ){
        
//#line 27
return true;
    }
    
    
//#line 29
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
    							DepTypeConsistency1_MustFailCompile.main(args);
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
        
//#line 30
new DepTypeConsistency1_MustFailCompile().execute();
    }/* } */
    
    public DepTypeConsistency1_MustFailCompile() {
        super();
    }

}
