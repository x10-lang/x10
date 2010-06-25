
public class ClosureTypeParameters1a
extends ClosureTest
{public static final x10.rtt.RuntimeType<ClosureTypeParameters1a>_RTT = new x10.rtt.RuntimeType<ClosureTypeParameters1a>(
/* base class */ClosureTypeParameters1a.class
, /* parents */ new x10.rtt.Type[] {ClosureTest._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 31
public boolean
                  run(
                  ){
        
//#line 34
this.check("new X[String]().f()",
                               (int)(java.lang.Integer)(new ClosureTypeParameters1a.
                                                          X$52<java.lang.String>(x10.rtt.Types.runtimeType(java.lang.String.class),
                                                                                 this).
                                                          f.apply$G()),
                               (int)(0));
        
//#line 36
return result;
    }
    
    
//#line 39
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
    							ClosureTypeParameters1a.main(args);
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
        
//#line 40
new ClosureTypeParameters1a().execute();
    }/* } */
    
    public ClosureTypeParameters1a() {
        super();
    }
    
    
//#line 33
private static class X$52<T>
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<ClosureTypeParameters1a.
      X$52>_RTT = new x10.rtt.RuntimeType<ClosureTypeParameters1a.
      X$52>(
    /* base class */ClosureTypeParameters1a.
      X$52.class, 
    /* variances */ new x10.rtt.RuntimeType.Variance[] {x10.rtt.RuntimeType.Variance.INVARIANT}
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    public x10.rtt.Type getParam(int i) {if (i ==0)return T;return null;}
    
        private final x10.rtt.Type T;
        
        
//#line 29
final private ClosureTypeParameters1a
          out$;
        
//#line 33
final x10.core.fun.Fun_0_0<java.lang.Integer>
          f;
        
        public X$52(final x10.rtt.Type T,
                    final ClosureTypeParameters1a out$) {
                                                                 super();
                                                             this.T = T;
                                                              {
                                                                 
//#line 29
this.out$ = out$;
                                                                 
//#line 33
this.f = ((x10.core.fun.Fun_0_0)(new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                                 public final int apply() { {
                                                                     
//#line 33
return 0;
                                                                 }}
                                                                 public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                                 }
                                                                 }));
                                                             }}
    
    }
    

}
