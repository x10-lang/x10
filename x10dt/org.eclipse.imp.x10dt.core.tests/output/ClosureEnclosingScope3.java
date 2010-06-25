
public class ClosureEnclosingScope3
extends ClosureTest
{public static final x10.rtt.RuntimeType<ClosureEnclosingScope3>_RTT = new x10.rtt.RuntimeType<ClosureEnclosingScope3>(
/* base class */ClosureEnclosingScope3.class
, /* parents */ new x10.rtt.Type[] {ClosureTest._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 24
final int
      a =
      1;
    
//#line 26
static class C
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<ClosureEnclosingScope3.
      C>_RTT = new x10.rtt.RuntimeType<ClosureEnclosingScope3.
      C>(
    /* base class */ClosureEnclosingScope3.
      C.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 22
final private ClosureEnclosingScope3
          out$;
        
//#line 27
final int
          a;
        
//#line 28
static class D
                    extends x10.core.Ref
                    {public static final x10.rtt.RuntimeType<ClosureEnclosingScope3.
          C.
          D>_RTT = new x10.rtt.RuntimeType<ClosureEnclosingScope3.
          C.
          D>(
        /* base class */ClosureEnclosingScope3.
          C.
          D.class
        , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
        );
        public x10.rtt.RuntimeType getRTT() {return _RTT;}
        
        
        
            
//#line 26
final private ClosureEnclosingScope3.
              C
              out$;
            
//#line 29
final int
              a;
            
//#line 30
final int
              sum;
            
            public D(final ClosureEnclosingScope3.
                       C out$) {
                super();
                
//#line 26
this.out$ = out$;
                
//#line 29
this.a = 4;
                
//#line 30
this.sum = ((new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                         public final int apply() { {
                                             
//#line 30
return (((((int)(((((int)(((((int)(ClosureEnclosingScope3.
                                                                                              C.
                                                                                              D.this.
                                                                                              out$.
                                                                                              out$.
                                                                                              a))) + (((int)(ClosureEnclosingScope3.
                                                                                                               C.
                                                                                                               D.this.
                                                                                                               out$.
                                                                                                               a))))))) + (((int)(ClosureEnclosingScope3.
                                                                                                                                    C.
                                                                                                                                    D.this.
                                                                                                                                    a))))))) + (((int)(a)))));
                                         }}
                                         public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                         }
                                         })).apply();
            }
        
        }
        
        
        public C(final ClosureEnclosingScope3 out$) {
            super();
            
//#line 22
this.out$ = out$;
            
//#line 27
this.a = 2;
        }
    
    }
    
    
    
//#line 37
public boolean
                  run(
                  ){
        
//#line 39
this.check("new C().new D().sum",
                               (int)(new ClosureEnclosingScope3.
                                       C.
                                       D(new ClosureEnclosingScope3.
                                           C(this)).
                                       sum),
                               (int)(11));
        
//#line 41
return result;
    }
    
    
//#line 44
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
    							ClosureEnclosingScope3.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$52771)  {
        
//#line 45
new ClosureEnclosingScope3().execute();
    }/* } */
    
    public ClosureEnclosingScope3() {
        super();
    }

}
