
public class ClosureEnclosingScope4
extends ClosureTest
{public static final x10.rtt.RuntimeType<ClosureEnclosingScope4>_RTT = new x10.rtt.RuntimeType<ClosureEnclosingScope4>(
/* base class */ClosureEnclosingScope4.class
, /* parents */ new x10.rtt.Type[] {ClosureTest._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 24
final int
      a =
      1;
    
    
//#line 26
public boolean
                  run(
                  ){
        
//#line 36
this.check("new C().new D().sum",
                               (int)(new ClosureEnclosingScope4.
                                       C$50.
                                       D(new ClosureEnclosingScope4.
                                           C$50(this)).
                                       sum),
                               (int)(11));
        
//#line 38
return result;
    }
    
    
//#line 41
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
    							ClosureEnclosingScope4.main(args);
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
        
//#line 42
new ClosureEnclosingScope4().execute();
    }/* } */
    
    public ClosureEnclosingScope4() {
        super();
    }
    
    
//#line 28
private static class C$50
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<ClosureEnclosingScope4.
      C$50>_RTT = new x10.rtt.RuntimeType<ClosureEnclosingScope4.
      C$50>(
    /* base class */ClosureEnclosingScope4.
      C$50.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 22
final private ClosureEnclosingScope4
          out$;
        
//#line 29
final int
          a;
        
//#line 30
static class D
                    extends x10.core.Ref
                    {public static final x10.rtt.RuntimeType<ClosureEnclosingScope4.
          C$50.
          D>_RTT = new x10.rtt.RuntimeType<ClosureEnclosingScope4.
          C$50.
          D>(
        /* base class */ClosureEnclosingScope4.
          C$50.
          D.class
        , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
        );
        public x10.rtt.RuntimeType getRTT() {return _RTT;}
        
        
        
            
//#line 28
final private ClosureEnclosingScope4.
              C$50
              out$;
            
//#line 31
final int
              a;
            
//#line 32
final int
              sum;
            
            public D(final ClosureEnclosingScope4.
                       C$50 out$) {
                super();
                
//#line 28
this.out$ = out$;
                
//#line 31
this.a = 4;
                
//#line 32
this.sum = ((new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                         public final int apply() { {
                                             
//#line 32
return (((((int)(((((int)(((((int)(ClosureEnclosingScope4.
                                                                                              C$50.
                                                                                              D.this.
                                                                                              out$.
                                                                                              out$.
                                                                                              a))) + (((int)(ClosureEnclosingScope4.
                                                                                                               C$50.
                                                                                                               D.this.
                                                                                                               out$.
                                                                                                               a))))))) + (((int)(ClosureEnclosingScope4.
                                                                                                                                    C$50.
                                                                                                                                    D.this.
                                                                                                                                    a))))))) + (((int)(a)))));
                                         }}
                                         public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                         }
                                         })).apply();
            }
        
        }
        
        
        public C$50(final ClosureEnclosingScope4 out$) {
            super();
            
//#line 22
this.out$ = out$;
            
//#line 29
this.a = 2;
        }
    
    }
    

}
