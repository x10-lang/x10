
public class Array1b
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<Array1b>_RTT = new x10.rtt.RuntimeType<Array1b>(
/* base class */Array1b.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 22
public boolean
                  run(
                  ){
        
//#line 24
final x10.
          array.
          Region e =
          ((x10.
          array.
          Region)(x10.
          array.
          Region.makeRectangular((int)(1),
                                 (int)(10))));
        
//#line 25
final x10.
          array.
          Region r =
          ((x10.
          array.
          Region)(x10.
          array.
          Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                     array.
                                     Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                     array.
                                     Region[] { e,e })/* } */)));
        
//#line 27
harness.
          x10Test.chk((boolean)(((Object)r).equals((x10.
                                                      array.
                                                      Region.makeRectangular((int)(1),
                                                                             (int)(10))).$times((x10.
                                                                                                   array.
                                                                                                   Region.makeRectangular((int)(1),
                                                                                                                          (int)(10)))))));
        
//#line 30
final x10.
          array.
          Dist d =
          ((x10.
          array.
          Dist)(x10.
          array.
          Dist.makeConstant(x10.
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
                            x10.
                              lang.
                              Runtime.here())));
        
//#line 32
harness.
          x10Test.chk((boolean)(d.equals(x10.
                                           array.
                                           Dist.makeConstant(x10.
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
                                                             x10.
                                                               lang.
                                                               Runtime.here()))));
        
//#line 33
harness.
          x10Test.chk((boolean)(d.equals(x10.
                                           array.
                                           Dist.makeConstant(x10.
                                                               array.
                                                               Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                                          array.
                                                                                          Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                                                                          array.
                                                                                          Region[] { e,e })/* } */),
                                                             x10.
                                                               lang.
                                                               Runtime.here()))));
        
//#line 34
harness.
          x10Test.chk((boolean)(d.equals(x10.
                                           array.
                                           Dist.makeConstant(r,
                                                             x10.
                                                               lang.
                                                               Runtime.here()))));
        
//#line 36
final x10.
          array.
          DistArray<java.lang.Integer> ia =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Integer>make(x10.rtt.Types.INT,
                                            d,
                                            new x10.core.fun.Fun_0_1<x10.
                                              array.
                                              Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                              array.
                                              Point id$7019) { return apply(id$7019);}
                                            public final int apply(final x10.
                                              array.
                                              Point id$7019) { {
                                                
//#line 36
return 0;
                                            }}
                                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                            }
                                            })));
        {
            
//#line 38
final x10.
              array.
              Region p7097 =
              ((x10.
              array.
              Region)(e));
            
//#line 38
final x10.core.Rail<java.lang.Integer> p7098 =
              ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Integer>makeVarRail(x10.rtt.Types.INT, ((int)(1)))));
            
//#line 38
final int i7099min7100 =
              p7097.min((int)(0));
            
//#line 38
final int i7099max7101 =
              p7097.max((int)(0));
            
//#line 38
for (
//#line 38
int i7099 =
                               i7099min7100;
                             ((((int)(i7099))) <= (((int)(i7099max7101))));
                             
//#line 38
i7099 += 1) {
                
//#line 38
final int i =
                  i7099;
                
//#line 38
((int[])p7098.value)[0] = i7099;
                
//#line 38
final x10.
                  array.
                  Point p =
                  x10.
                  array.
                  Point.make(p7098);
                {
                    {
                        
//#line 38
final x10.
                          array.
                          Region q7092 =
                          ((x10.
                          array.
                          Region)(e));
                        
//#line 38
final x10.core.Rail<java.lang.Integer> q7093 =
                          ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Integer>makeVarRail(x10.rtt.Types.INT, ((int)(1)))));
                        
//#line 38
final int j7094min7095 =
                          q7092.min((int)(0));
                        
//#line 38
final int j7094max7096 =
                          q7092.max((int)(0));
                        
//#line 38
for (
//#line 38
int j7094 =
                                           j7094min7095;
                                         ((((int)(j7094))) <= (((int)(j7094max7096))));
                                         
//#line 38
j7094 += 1) {
                            
//#line 38
final int j =
                              j7094;
                            
//#line 38
((int[])q7093.value)[0] = j7094;
                            
//#line 38
final x10.
                              array.
                              Point q =
                              x10.
                              array.
                              Point.make(q7093);
                            {
                                
//#line 39
harness.
                                  x10Test.chk((boolean)(((int) ia.apply$G((int)(i),
                                                                          (int)(j))) ==
                                              ((int) 0)));
                                
//#line 40
ia.set$G((int)(((((int)(i))) + (((int)(j))))),
                                                     (int)(i),
                                                     (int)(j));
                            }
                        }
                    }
                }
            }
        }
        {
            
//#line 43
final x10.
              array.
              Region p7102 =
              ((x10.
              array.
              Region)(ia.region()));
            
//#line 43
final x10.core.Rail<java.lang.Integer> p7103 =
              ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Integer>makeVarRail(x10.rtt.Types.INT, ((int)(2)))));
            
//#line 43
final int j7104min7105 =
              p7102.min((int)(1));
            
//#line 43
final int j7104max7106 =
              p7102.max((int)(1));
            
//#line 43
final int i7107min7108 =
              p7102.min((int)(0));
            
//#line 43
final int i7107max7109 =
              p7102.max((int)(0));
            
//#line 43
for (
//#line 43
int i7107 =
                               i7107min7108;
                             ((((int)(i7107))) <= (((int)(i7107max7109))));
                             
//#line 43
i7107 += 1) {
                
//#line 43
final int i =
                  i7107;
                
//#line 43
((int[])p7103.value)[0] = i7107;
                
//#line 43
for (
//#line 43
int j7104 =
                                   j7104min7105;
                                 ((((int)(j7104))) <= (((int)(j7104max7106))));
                                 
//#line 43
j7104 += 1) {
                    
//#line 43
final int j =
                      j7104;
                    
//#line 43
((int[])p7103.value)[1] = j7104;
                    
//#line 43
final x10.
                      array.
                      Point p =
                      ((x10.
                      array.
                      Point)(x10.
                      array.
                      Point.make(p7103)));
                    {
                        
//#line 44
final x10.
                          array.
                          Point q1 =
                          ((x10.
                          array.
                          Point)(x10.
                          array.
                          Point.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { i,j })/* } */)));
                        
//#line 44
final int m =
                          q1.apply((int)(0));
                        
//#line 44
final int n =
                          q1.apply((int)(1));
                        
//#line 45
harness.
                          x10Test.chk((boolean)(((int) i) ==
                                      ((int) m)));
                        
//#line 46
harness.
                          x10Test.chk((boolean)(((int) j) ==
                                      ((int) n)));
                        
//#line 47
harness.
                          x10Test.chk((boolean)(((int) ia.apply$G((int)(i),
                                                                  (int)(j))) ==
                                      ((int) ((((int)(i))) + (((int)(j)))))));
                        
//#line 48
harness.
                          x10Test.chk((boolean)(((int) ia.apply$G((int)(i),
                                                                  (int)(j))) ==
                                      ((int) ia.apply$G(p))));
                        
//#line 49
harness.
                          x10Test.chk((boolean)(((int) ia.apply$G(q1)) ==
                                      ((int) ia.apply$G(p))));
                        
//#line 50
ia.set$G((int)(((((int)(ia.apply$G(p)))) - (((int)(1))))),
                                             p);
                        
//#line 51
harness.
                          x10Test.chk((boolean)(((int) ia.apply$G(p)) ==
                                      ((int) ((((int)(((((int)(i))) + (((int)(j))))))) - (((int)(1)))))));
                        
//#line 52
harness.
                          x10Test.chk((boolean)(((int) ia.apply$G(q1)) ==
                                      ((int) ia.apply$G(p))));
                    }
                }
            }
        }
        
//#line 55
return true;
    }
    
    
//#line 58
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
    							Array1b.main(args);
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
        
//#line 59
new Array1b().execute();
    }/* } */
    
    public Array1b() {
        super();
    }

}
