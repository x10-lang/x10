
public class ConstructorInvocation
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ConstructorInvocation>_RTT = new x10.rtt.RuntimeType<ConstructorInvocation>(
/* base class */ConstructorInvocation.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 26
static class Test
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<ConstructorInvocation.
      Test>_RTT = new x10.rtt.RuntimeType<ConstructorInvocation.
      Test>(
    /* base class */ConstructorInvocation.
      Test.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 26
final public int
          i;
        
//#line 26
final public int
          j;
        
        
//#line 24
final private ConstructorInvocation
          out$;
        
        
//#line 27
Test(final ConstructorInvocation out$,
                         final int i,
                         final int j) {
            
//#line 27
super();
            
//#line 24
this.out$ = out$;
            
//#line 28
this.i = i;
            
//#line 28
this.j = j;
        }
        
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
    
    
//#line 32
static class Test2
                extends ConstructorInvocation.
                  Test
                {public static final x10.rtt.RuntimeType<ConstructorInvocation.
      Test2>_RTT = new x10.rtt.RuntimeType<ConstructorInvocation.
      Test2>(
    /* base class */ConstructorInvocation.
      Test2.class
    , /* parents */ new x10.rtt.Type[] {ConstructorInvocation.Test._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 32
final public int
          k;
        
        
//#line 24
final private ConstructorInvocation
          out$;
        
        
//#line 33
Test2(final ConstructorInvocation out$,
                          final int k) {
            
//#line 34
super(out$,
                              k,
                              k);
            
//#line 24
this.out$ = out$;
            
//#line 35
this.k = k;
        }
        
        final public int
          k(
          ){
            return this.
                     k;
        }
    
    }
    
    
    
//#line 38
public boolean
                  run(
                  ){
        
//#line 38
return true;
    }
    
    
//#line 40
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
    							ConstructorInvocation.main(args);
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
        
//#line 41
new ConstructorInvocation().execute();
    }/* } */
    
    public ConstructorInvocation() {
        super();
    }

}
