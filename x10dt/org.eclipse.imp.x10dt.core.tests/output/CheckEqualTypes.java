
public class CheckEqualTypes
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<CheckEqualTypes>_RTT = new x10.rtt.RuntimeType<CheckEqualTypes>(
/* base class */CheckEqualTypes.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 21
static class Test
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<CheckEqualTypes.
      Test>_RTT = new x10.rtt.RuntimeType<CheckEqualTypes.
      Test>(
    /* base class */CheckEqualTypes.
      Test.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 21
final public int
          i;
        
//#line 21
final public int
          j;
        
        
//#line 20
final private CheckEqualTypes
          out$;
        
        
//#line 22
public Test(final CheckEqualTypes out$,
                                final int i,
                                final int j) {
            
//#line 22
super();
            
//#line 20
this.out$ = out$;
            
//#line 23
this.i = i;
            
//#line 23
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
    
    
    
//#line 26
public boolean
                  m(
                  CheckEqualTypes.
                    Test t){
        
//#line 27
return true;
    }
    
    
//#line 29
public boolean
                  run(
                  ){
        
//#line 30
final int j =
          0;
        
//#line 31
CheckEqualTypes.
          Test t =
          ((CheckEqualTypes.
          Test)(new CheckEqualTypes.
          Test(this,
               0,
               0)));
        
//#line 33
return this.m(t);
    }
    
    
//#line 35
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
    							CheckEqualTypes.main(args);
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
        
//#line 36
new CheckEqualTypes().execute();
    }/* } */
    
    public CheckEqualTypes() {
        super();
    }

}
