
public class ArrayOpAssign2
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ArrayOpAssign2>_RTT = new x10.rtt.RuntimeType<ArrayOpAssign2>(
/* base class */ArrayOpAssign2.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 21
int
      i;
    
//#line 22
int
      j;
    
    
//#line 24
public boolean
                  run(
                  ){
        
//#line 26
final x10.
          array.
          Region R =
          ((x10.
          array.
          Region)(x10.
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
                                                            (int)(10)) })/* } */)));
        
//#line 27
x10.
          array.
          Array<java.lang.Integer> ia =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Integer>(x10.rtt.Types.INT,
                                   R,
                                   new x10.core.fun.Fun_0_1<x10.
                                     array.
                                     Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                     array.
                                     Point id$15973) { return apply(id$15973);}
                                   public final int apply(final x10.
                                     array.
                                     Point id$15973) { {
                                       
//#line 27
return 0;
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
        
//#line 29
ia.set$G((int)(1),
                             (int)(i),
                             (int)(j));
        
//#line 31
harness.
          x10Test.chk((boolean)(((int) ia.apply$G((int)(i),
                                                  (int)(j))) ==
                      ((int) 1)));
        
//#line 32
harness.
          x10Test.chk((boolean)(((int) ((new x10.core.fun.Fun_0_1<java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(java.lang.Integer t) { return apply((int)t);}
                                         public final int apply(int t) { {
                                             
//#line 32
return ((((int)(t))) - (((int)(1))));
                                         }}
                                         public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;if (i ==1) return x10.rtt.Types.INT;return null;
                                         }
                                         }.apply(new x10.core.fun.Fun_0_4<x10.
                                                   array.
                                                   Array<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                                   array.
                                                   Array<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer y1,final java.lang.Integer z) { return apply(x,(int)y0,(int)y1,(int)z);}
                                                 public final int apply(final x10.
                                                   array.
                                                   Array<java.lang.Integer> x, final int y0, final int y1, final int z) { {
                                                     
//#line 32
return x.set$G((int)(((((int)(x.apply$G((int)(y0),
                                                                                                         (int)(y1))))) + (((int)(z))))),
                                                                                (int)(y0),
                                                                                (int)(y1));
                                                 }}
                                                 public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;if (i ==4) return x10.rtt.Types.INT;return null;
                                                 }
                                                 }.apply(ia,
                                                         i,
                                                         j,
                                                         1))))) ==
                      ((int) 1)));
        
//#line 33
harness.
          x10Test.chk((boolean)(((int) ia.apply$G((int)(i),
                                                  (int)(j))) ==
                      ((int) 2)));
        
//#line 34
harness.
          x10Test.chk((boolean)(((int) ((new x10.core.fun.Fun_0_1<java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(java.lang.Integer t) { return apply((int)t);}
                                         public final int apply(int t) { {
                                             
//#line 34
return ((((int)(t))) + (((int)(1))));
                                         }}
                                         public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;if (i ==1) return x10.rtt.Types.INT;return null;
                                         }
                                         }.apply(new x10.core.fun.Fun_0_4<x10.
                                                   array.
                                                   Array<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                                   array.
                                                   Array<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer y1,final java.lang.Integer z) { return apply(x,(int)y0,(int)y1,(int)z);}
                                                 public final int apply(final x10.
                                                   array.
                                                   Array<java.lang.Integer> x, final int y0, final int y1, final int z) { {
                                                     
//#line 34
return x.set$G((int)(((((int)(x.apply$G((int)(y0),
                                                                                                         (int)(y1))))) - (((int)(z))))),
                                                                                (int)(y0),
                                                                                (int)(y1));
                                                 }}
                                                 public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;if (i ==4) return x10.rtt.Types.INT;return null;
                                                 }
                                                 }.apply(ia,
                                                         i,
                                                         j,
                                                         1))))) ==
                      ((int) 2)));
        
//#line 35
harness.
          x10Test.chk((boolean)(((int) ia.apply$G((int)(i),
                                                  (int)(j))) ==
                      ((int) 1)));
        
//#line 36
harness.
          x10Test.chk((boolean)(((int) ((new x10.core.fun.Fun_0_4<x10.
                                           array.
                                           Array<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                           array.
                                           Array<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer y1,final java.lang.Integer z) { return apply(x,(int)y0,(int)y1,(int)z);}
                                         public final int apply(final x10.
                                           array.
                                           Array<java.lang.Integer> x, final int y0, final int y1, final int z) { {
                                             
//#line 36
return x.set$G((int)(((((int)(x.apply$G((int)(y0),
                                                                                                 (int)(y1))))) + (((int)(z))))),
                                                                        (int)(y0),
                                                                        (int)(y1));
                                         }}
                                         public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;if (i ==4) return x10.rtt.Types.INT;return null;
                                         }
                                         }.apply(ia,
                                                 i,
                                                 j,
                                                 1)))) ==
                      ((int) 2)));
        
//#line 37
harness.
          x10Test.chk((boolean)(((int) ia.apply$G((int)(i),
                                                  (int)(j))) ==
                      ((int) 2)));
        
//#line 38
harness.
          x10Test.chk((boolean)(((int) ((new x10.core.fun.Fun_0_4<x10.
                                           array.
                                           Array<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                           array.
                                           Array<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer y1,final java.lang.Integer z) { return apply(x,(int)y0,(int)y1,(int)z);}
                                         public final int apply(final x10.
                                           array.
                                           Array<java.lang.Integer> x, final int y0, final int y1, final int z) { {
                                             
//#line 38
return x.set$G((int)(((((int)(x.apply$G((int)(y0),
                                                                                                 (int)(y1))))) - (((int)(z))))),
                                                                        (int)(y0),
                                                                        (int)(y1));
                                         }}
                                         public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;if (i ==4) return x10.rtt.Types.INT;return null;
                                         }
                                         }.apply(ia,
                                                 i,
                                                 j,
                                                 1)))) ==
                      ((int) 1)));
        
//#line 39
harness.
          x10Test.chk((boolean)(((int) ia.apply$G((int)(i),
                                                  (int)(j))) ==
                      ((int) 1)));
        
//#line 41
return true;
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
    							ArrayOpAssign2.main(args);
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
        
//#line 45
new ArrayOpAssign2().execute();
    }/* } */
    
    public ArrayOpAssign2() {
        super();
        
//#line 21
this.i = 1;
        
//#line 22
this.j = 1;
    }

}
