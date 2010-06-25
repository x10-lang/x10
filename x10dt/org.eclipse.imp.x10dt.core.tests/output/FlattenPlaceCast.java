
public class FlattenPlaceCast
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<FlattenPlaceCast>_RTT = new x10.rtt.RuntimeType<FlattenPlaceCast>(
/* base class */FlattenPlaceCast.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 22
final x10.
      array.
      DistArray<FlattenPlaceCast.
      Test>
      a;
    
//#line 23
final x10.
      array.
      DistArray<x10.
      lang.
      Place>
      d;
    
    
//#line 25
public FlattenPlaceCast() {
        
//#line 25
super();
        
//#line 26
this.a = ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<FlattenPlaceCast.
          Test>make(FlattenPlaceCast.Test._RTT,
                    x10.
                      array.
                      Dist.makeConstant((x10.
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
                                                                                             (int)(10)) })/* } */)),
                                        x10.
                                          lang.
                                          Runtime.here()),
                    new x10.core.fun.Fun_0_1<x10.
                      array.
                      Point, FlattenPlaceCast.
                      Test>() {public final FlattenPlaceCast.
                      Test apply$G(final x10.
                      array.
                      Point id$23373) { return apply(id$23373);}
                    public final FlattenPlaceCast.
                      Test apply(final x10.
                      array.
                      Point id$23373) { {
                        
//#line 26
return new FlattenPlaceCast.
                          Test();
                    }}
                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return FlattenPlaceCast.Test._RTT;return null;
                    }
                    })));
        
//#line 27
this.d = ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<x10.
          lang.
          Place>make(x10.lang.Place._RTT,
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
                       Point, x10.
                       lang.
                       Place>() {public final x10.
                       lang.
                       Place apply$G(final x10.
                       array.
                       Point id$23374) { return apply(id$23374);}
                     public final x10.
                       lang.
                       Place apply(final x10.
                       array.
                       Point id$23374) { {
                         
//#line 27
return x10.
                           lang.
                           Runtime.here();
                     }}
                     public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.lang.Place._RTT;return null;
                     }
                     })));
    }
    
    
//#line 30
static class Test
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<FlattenPlaceCast.
      Test>_RTT = new x10.rtt.RuntimeType<FlattenPlaceCast.
      Test>(
    /* base class */FlattenPlaceCast.
      Test.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
        public Test() {
            super();
        }
    
    }
    
    
    
//#line 32
public boolean
                  run(
                  ){
        
//#line 33
final x10.
          lang.
          Place d1next =
          d.apply$G((int)(1)).next();
        
//#line 43
final x10.
          lang.
          Place d1 =
          d.apply$G((int)(1));
        
//#line 44
final FlattenPlaceCast.
          Test x =
          ((FlattenPlaceCast.
          Test)(new x10.core.fun.Fun_0_1<FlattenPlaceCast.
          Test, FlattenPlaceCast.
          Test>() {public final FlattenPlaceCast.
          Test apply$G(final FlattenPlaceCast.
          Test __desugarer__var__248__) { return apply(__desugarer__var__248__);}
        public final FlattenPlaceCast.
          Test apply(final FlattenPlaceCast.
          Test __desugarer__var__248__) { {
            
//#line 44
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__248__,null))/* } */ &&
                              !(/* template:equalsequals { */x10.rtt.Equality.equalsequals(x10.lang.Place.place(x10.core.Ref.home(__desugarer__var__248__)),d1)/* } */)) {
                
//#line 44
throw new java.lang.ClassCastException("FlattenPlaceCast.Test{self.home==d1}");
            }
            
//#line 44
return __desugarer__var__248__;
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return FlattenPlaceCast.Test._RTT;if (i ==1) return FlattenPlaceCast.Test._RTT;return null;
        }
        }.apply(((FlattenPlaceCast.
                  Test)
                  a.apply$G((int)(1),
                            (int)(1))))));
        
//#line 45
final FlattenPlaceCast.
          Test y =
          ((FlattenPlaceCast.
          Test)(new x10.core.fun.Fun_0_1<FlattenPlaceCast.
          Test, FlattenPlaceCast.
          Test>() {public final FlattenPlaceCast.
          Test apply$G(final FlattenPlaceCast.
          Test __desugarer__var__249__) { return apply(__desugarer__var__249__);}
        public final FlattenPlaceCast.
          Test apply(final FlattenPlaceCast.
          Test __desugarer__var__249__) { {
            
//#line 45
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__249__,null))/* } */ &&
                              !(/* template:equalsequals { */x10.rtt.Equality.equalsequals(x10.lang.Place.place(x10.core.Ref.home(__desugarer__var__249__)),x10.
                                  lang.
                                  Runtime.here())/* } */)) {
                
//#line 45
throw new java.lang.ClassCastException("FlattenPlaceCast.Test{self.home==here}");
            }
            
//#line 45
return __desugarer__var__249__;
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return FlattenPlaceCast.Test._RTT;if (i ==1) return FlattenPlaceCast.Test._RTT;return null;
        }
        }.apply(((FlattenPlaceCast.
                  Test)
                  a.apply$G((int)(1),
                            (int)(1))))));
        
//#line 46
final FlattenPlaceCast.
          Test z =
          ((FlattenPlaceCast.
          Test)(new x10.core.fun.Fun_0_1<FlattenPlaceCast.
          Test, FlattenPlaceCast.
          Test>() {public final FlattenPlaceCast.
          Test apply$G(final FlattenPlaceCast.
          Test __desugarer__var__250__) { return apply(__desugarer__var__250__);}
        public final FlattenPlaceCast.
          Test apply(final FlattenPlaceCast.
          Test __desugarer__var__250__) { {
            
//#line 46
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__250__,null))/* } */ &&
                              !x10.core.Ref.at(__desugarer__var__250__, x10.
                              lang.
                              Runtime.here().id)) {
                
//#line 46
throw new java.lang.ClassCastException("FlattenPlaceCast.Test{self.home==here}");
            }
            
//#line 46
return __desugarer__var__250__;
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return FlattenPlaceCast.Test._RTT;if (i ==1) return FlattenPlaceCast.Test._RTT;return null;
        }
        }.apply(((FlattenPlaceCast.
                  Test)
                  a.apply$G((int)(1),
                            (int)(1))))));
        
//#line 47
return true;
    }
    
    
//#line 50
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
    							FlattenPlaceCast.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$23376)  {
        
//#line 51
new FlattenPlaceCast().execute();
    }/* } */

}
