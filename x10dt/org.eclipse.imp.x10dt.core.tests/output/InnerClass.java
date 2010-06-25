
public class InnerClass
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<InnerClass>_RTT = new x10.rtt.RuntimeType<InnerClass>(
/* base class */InnerClass.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 24
public boolean
                  run(
                  ){
        
//#line 28
final boolean result =
          ((Object)new InnerClass.
          X$28(this)).equals(new InnerClass.
          Y$29(this));
        
//#line 30
return (!(((boolean)(result))));
    }
    
    
//#line 33
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
    							InnerClass.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$43948)  {
        
//#line 34
new InnerClass().execute();
    }/* } */
    
    public InnerClass() {
        super();
    }
    
    
//#line 26
private static class X$28
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<InnerClass.
      X$28>_RTT = new x10.rtt.RuntimeType<InnerClass.
      X$28>(
    /* base class */InnerClass.
      X$28.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 22
final private InnerClass
          out$;
        
        public X$28(final InnerClass out$) {
            super();
            
//#line 22
this.out$ = out$;
        }
    
    }
    
    
//#line 27
private static class Y$29
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<InnerClass.
      Y$29>_RTT = new x10.rtt.RuntimeType<InnerClass.
      Y$29>(
    /* base class */InnerClass.
      Y$29.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 22
final private InnerClass
          out$;
        
        public Y$29(final InnerClass out$) {
            super();
            
//#line 22
this.out$ = out$;
        }
    
    }
    

}
