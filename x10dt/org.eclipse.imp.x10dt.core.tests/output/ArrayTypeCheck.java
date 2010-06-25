
public class ArrayTypeCheck
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ArrayTypeCheck>_RTT = new x10.rtt.RuntimeType<ArrayTypeCheck>(
/* base class */ArrayTypeCheck.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 22
public boolean
                  run(
                  ){
        
//#line 24
x10.
          array.
          DistArray<java.lang.Integer> a1 =
          x10.
          array.
          DistArray.<java.lang.Integer>make(x10.rtt.Types.INT,
                                            x10.
                                              array.
                                              Dist.makeConstant(x10.
                                                                  array.
                                                                  Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                                             array.
                                                                                             Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                                                                             array.
                                                                                             Region[] { x10.
                                                                                             array.
                                                                                             Region.makeRectangular((int)(0),
                                                                                                                    (int)(2)),x10.
                                                                                             array.
                                                                                             Region.makeRectangular((int)(0),
                                                                                                                    (int)(3)) })/* } */),
                                                                x10.
                                                                  lang.
                                                                  Runtime.here()),
                                            new x10.core.fun.Fun_0_1<x10.
                                              array.
                                              Point, java.lang.Integer>() {public final java.lang.Integer apply$G(x10.
                                              array.
                                              Point p) { return apply(p);}
                                            public final int apply(x10.
                                              array.
                                              Point p) { {
                                                
//#line 24
final int i =
                                                  p.apply((int)(0));
                                                
//#line 24
return i;
                                            }}
                                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                            }
                                            });
        
//#line 26
x10.
          io.
          Console.OUT.println("1");
        
//#line 28
final x10.
          array.
          Dist E =
          x10.
          array.
          Dist.makeConstant(x10.
                              array.
                              Region.makeRectangular((int)(-1),
                                                     (int)(-2)),
                            x10.
                              lang.
                              Runtime.here());
        
//#line 30
try {{
            
//#line 31
x10.
              io.
              Console.OUT.println((("a1.dist ") + (a1.
                                                     dist)));
            
//#line 32
x10.
              io.
              Console.OUT.println((("E ") + (E)));
            
//#line 33
x10.
              io.
              Console.OUT.println((("== ") + ((/* template:equalsequals { */x10.rtt.Equality.equalsequals(((x10.
                                                 array.
                                                 Dist)(a1.
                                                         dist)),E)/* } */))));
            
//#line 35
x10.
              array.
              DistArray<java.lang.Integer> a2 =
              new x10.core.fun.Fun_0_1<x10.
              array.
              DistArray<java.lang.Integer>, x10.
              array.
              DistArray<java.lang.Integer>>() {public final x10.
              array.
              DistArray<java.lang.Integer> apply$G(final x10.
              array.
              DistArray<java.lang.Integer> __desugarer__var__217__) { return apply(__desugarer__var__217__);}
            public final x10.
              array.
              DistArray<java.lang.Integer> apply(final x10.
              array.
              DistArray<java.lang.Integer> __desugarer__var__217__) { {
                
//#line 35
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__217__,null))/* } */ &&
                                  !(/* template:equalsequals { */x10.rtt.Equality.equalsequals(((x10.
                                      array.
                                      Dist)(__desugarer__var__217__.
                                              dist)),E)/* } */)) {
                    
//#line 35
throw new java.lang.ClassCastException("x10.array.DistArray[x10.lang.Int]{self.dist==E}");
                }
                
//#line 35
return __desugarer__var__217__;
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.INT);if (i ==1) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.INT);return null;
            }
            }.apply(((x10.
                      array.
                      DistArray)
                      a1));
            
//#line 36
x10.
              io.
              Console.OUT.println("did not get exception");
            
//#line 37
return false;
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.ClassCastException) {
        java.lang.ClassCastException z = (java.lang.ClassCastException) __$generated_wrappedex$__.getCause();
        {
            
//#line 39
x10.
              io.
              Console.OUT.println("2");
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.ClassCastException z) {
            
//#line 39
x10.
              io.
              Console.OUT.println("2");
        }
        
//#line 42
try {{
            
//#line 43
final x10.
              array.
              Dist D =
              x10.
              array.
              Dist.makeUnique();
            
//#line 44
x10.
              array.
              DistArray<java.lang.Integer> a3 =
              new x10.core.fun.Fun_0_1<x10.
              array.
              DistArray<java.lang.Integer>, x10.
              array.
              DistArray<java.lang.Integer>>() {public final x10.
              array.
              DistArray<java.lang.Integer> apply$G(final x10.
              array.
              DistArray<java.lang.Integer> __desugarer__var__218__) { return apply(__desugarer__var__218__);}
            public final x10.
              array.
              DistArray<java.lang.Integer> apply(final x10.
              array.
              DistArray<java.lang.Integer> __desugarer__var__218__) { {
                
//#line 44
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__218__,null))/* } */ &&
                                  !(/* template:equalsequals { */x10.rtt.Equality.equalsequals(((x10.
                                      array.
                                      Dist)(__desugarer__var__218__.
                                              dist)),D)/* } */)) {
                    
//#line 44
throw new java.lang.ClassCastException("x10.array.DistArray[x10.lang.Int]{self.dist==D}");
                }
                
//#line 44
return __desugarer__var__218__;
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.INT);if (i ==1) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.INT);return null;
            }
            }.apply(((x10.
                      array.
                      DistArray)
                      a1));
            
//#line 45
return false;
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.ClassCastException) {
        java.lang.ClassCastException z = (java.lang.ClassCastException) __$generated_wrappedex$__.getCause();
        {
            
//#line 47
x10.
              io.
              Console.OUT.println("3");
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.ClassCastException z) {
            
//#line 47
x10.
              io.
              Console.OUT.println("3");
        }
        
//#line 50
int i =
          1;
        
//#line 51
int j =
          2;
        
//#line 52
int k =
          0;
        
//#line 53
final x10.
          array.
          Point p =
          ((x10.
          array.
          Point)(x10.
          array.
          Point.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { i,j,k })/* } */)));
        
//#line 54
final x10.
          array.
          Point q =
          ((x10.
          array.
          Point)(x10.
          array.
          Point.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { i,j })/* } */)));
        
//#line 55
final x10.
          array.
          Point r =
          ((x10.
          array.
          Point)(x10.
          array.
          Point.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { i })/* } */)));
        
//#line 57
if (/* template:equalsequals { */x10.rtt.Equality.equalsequals(p,q)/* } */) {
            
//#line 57
return false;
        }
        
//#line 59
return true;
    }
    
    
//#line 62
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
    							ArrayTypeCheck.main(args);
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
        
//#line 63
new ArrayTypeCheck().execute();
    }/* } */
    
    public ArrayTypeCheck() {
        super();
    }

}
