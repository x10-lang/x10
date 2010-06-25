
public class FlattenAsyncExpr
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<FlattenAsyncExpr>_RTT = new x10.rtt.RuntimeType<FlattenAsyncExpr>(
/* base class */FlattenAsyncExpr.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 25
x10.
      array.
      DistArray<java.lang.Integer>
      a;
    
    
//#line 27
public FlattenAsyncExpr() {
        
//#line 27
super();
        
//#line 25
this.a = null;
        
//#line 28
this.a = ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Integer>make(x10.rtt.Types.INT,
                                            x10.
                                              array.
                                              Dist.makeConstant(x10.
                                                                  array.
                                                                  Region.makeRectangular((int)(1),
                                                                                         (int)(10)),
                                                                x10.
                                                                  lang.
                                                                  Runtime.here()),
                                            new x10.core.fun.Fun_0_1<x10.
                                              array.
                                              Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                              array.
                                              Point id21255) { return apply(id21255);}
                                            public final int apply(final x10.
                                              array.
                                              Point id21255) { {
                                                
//#line 28
final int j =
                                                  id21255.apply((int)(0));
                                                
//#line 28
return j;
                                            }}
                                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                            }
                                            })));
    }
    
    
//#line 31
int
                  m(
                  int x){
        
//#line 32
return x;
    }
    
    
//#line 35
public boolean
                  run(
                  ){
        
//#line 36
x10.
          lang.
          Runtime.runAsync(a.
                             dist.apply((int)(1)),
                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                               
//#line 37
FlattenAsyncExpr.this.m((int)(50000));
                           }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                           });
        
//#line 40
return true;
    }
    
    
//#line 43
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
    							FlattenAsyncExpr.main(args);
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
        
//#line 44
new FlattenAsyncExpr().execute();
    }/* } */

}
