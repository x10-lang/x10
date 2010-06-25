
public class ConstructorInvocation2
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ConstructorInvocation2>_RTT = new x10.rtt.RuntimeType<ConstructorInvocation2>(
/* base class */ConstructorInvocation2.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 22
static class Test
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<ConstructorInvocation2.
      Test>_RTT = new x10.rtt.RuntimeType<ConstructorInvocation2.
      Test>(
    /* base class */ConstructorInvocation2.
      Test.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 22
final public int
          i;
        
//#line 22
final public int
          j;
        
        
//#line 20
final private ConstructorInvocation2
          out$;
        
        
//#line 23
Test(final ConstructorInvocation2 out$,
                         final int i,
                         final int j) {
            
//#line 23
super();
            
//#line 20
this.out$ = out$;
            
//#line 24
this.i = i;
            
//#line 24
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
    
    
//#line 28
static class Test2
                extends ConstructorInvocation2.
                  Test
                {public static final x10.rtt.RuntimeType<ConstructorInvocation2.
      Test2>_RTT = new x10.rtt.RuntimeType<ConstructorInvocation2.
      Test2>(
    /* base class */ConstructorInvocation2.
      Test2.class
    , /* parents */ new x10.rtt.Type[] {ConstructorInvocation2.Test._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 28
final public int
          k;
        
        
//#line 20
final private ConstructorInvocation2
          out$;
        
        
//#line 29
Test2(final ConstructorInvocation2 out$,
                          final int k) {
            
//#line 30
super(out$,
                              k,
                              k);
            
//#line 20
this.out$ = out$;
            
//#line 31
this.k = k;
        }
        
        final public int
          k(
          ){
            return this.
                     k;
        }
    
    }
    
    
    
//#line 34
public boolean
                  run(
                  ){
        
//#line 34
return true;
    }
    
    
//#line 36
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
    							ConstructorInvocation2.main(args);
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
        
//#line 37
new ConstructorInvocation2().execute();
    }/* } */
    
    public ConstructorInvocation2() {
        super();
    }

}
