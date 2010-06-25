
public class ClosureEnclosingScope1s
extends ClosureTest
{public static final x10.rtt.RuntimeType<ClosureEnclosingScope1s>_RTT = new x10.rtt.RuntimeType<ClosureEnclosingScope1s>(
/* base class */ClosureEnclosingScope1s.class
, /* parents */ new x10.rtt.Type[] {ClosureTest._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 25
final int
      a =
      1;
    
    
//#line 27
public boolean
                  run(
                  ){
        
//#line 29
int b =
          1;
        
//#line 42
this.check("new C().foo()",
                               (int)(new ClosureEnclosingScope1s.
                                 C$47(this,
                                      b).foo()),
                               (int)(4));
        
//#line 45
return result;
    }
    
    
//#line 48
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
    							ClosureEnclosingScope1s.main(args);
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
        
//#line 49
new ClosureEnclosingScope1s().execute();
    }/* } */
    
    public ClosureEnclosingScope1s() {
        super();
    }
    
    
//#line 31
private static class C$47
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<ClosureEnclosingScope1s.
      C$47>_RTT = new x10.rtt.RuntimeType<ClosureEnclosingScope1s.
      C$47>(
    /* base class */ClosureEnclosingScope1s.
      C$47.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 23
final private ClosureEnclosingScope1s
          out$;
        
//#line 29
private int
          b;
        
//#line 32
final int
          c;
        
        
//#line 33
int
                      foo(
                      ){
            
//#line 34
final x10.core.fun.Fun_0_0<java.lang.Integer> fun =
              ((x10.core.fun.Fun_0_0)(new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
            public final int apply() { {
                
//#line 35
int d =
                  1;
                
//#line 36
return ((new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                     public final int apply() { {
                                         
//#line 36
return ((((int)(((((int)(((((int)(ClosureEnclosingScope1s.
                                                                                         C$47.this.
                                                                                         out$.
                                                                                         a))) + (((int)(ClosureEnclosingScope1s.
                                                                                                          C$47.this.
                                                                                                          b))))))) + (((int)(c))))))) + (((int)(d))));
                                     }}
                                     public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                     }
                                     })).apply();
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
            }
            }));
            
//#line 38
return fun.apply$G();
        }
        
        public C$47(final ClosureEnclosingScope1s out$,
                    final int b) {
            super();
            
//#line 23
this.out$ = out$;
            
//#line 32
this.c = 1;
            
//#line 29
this.b = b;
        }
    
    }
    

}
