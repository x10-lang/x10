
public class IntArrayMaxAbs
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<IntArrayMaxAbs>_RTT = new x10.rtt.RuntimeType<IntArrayMaxAbs>(
/* base class */IntArrayMaxAbs.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 20
public boolean
                  run(
                  ){
        
//#line 21
final x10.
          array.
          Array<java.lang.Integer> ia =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Integer>(x10.rtt.Types.INT,
                                   x10.
                                     array.
                                     Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                array.
                                                                Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                                                array.
                                                                Region[] { x10.
                                                                array.
                                                                Region.makeRectangular((int)(1),
                                                                                       (int)(10)),x10.
                                                                array.
                                                                Region.makeRectangular((int)(1),
                                                                                       (int)(10)) })/* } */),
                                   new x10.core.fun.Fun_0_1<x10.
                                     array.
                                     Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                     array.
                                     Point p) { return apply(p);}
                                   public final int apply(final x10.
                                     array.
                                     Point p) { {
                                       
//#line 21
return (-(((int)(p.apply((int)(0))))));
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
        
//#line 23
final int absMax =
          ia.reduce$G(new x10.core.fun.Fun_0_2<java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final java.lang.Integer a,final java.lang.Integer b) { return apply((int)a,(int)b);}
                      public final int apply(final int a, final int b) { {
                          
//#line 24
final int ma =
                            x10.
                            lang.
                            Math.abs((int)(a));
                          
//#line 24
final int mb =
                            x10.
                            lang.
                            Math.abs((int)(b));
                          
//#line 25
return ((((int)(ma))) <= (((int)(mb))))
                            ? mb
                            : ma;
                      }}
                      public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;return null;
                      }
                      },
                      (int)(0));
        
//#line 28
harness.
          x10Test.println((("ABSmax=") + (absMax)));
        
//#line 29
return ((int) absMax) ==
        ((int) 10);
    }
    
    
//#line 32
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
    							IntArrayMaxAbs.main(args);
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
        
//#line 33
new IntArrayMaxAbs().execute();
    }/* } */
    
    public IntArrayMaxAbs() {
        super();
    }

}
