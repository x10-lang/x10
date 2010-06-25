
public class Array1
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<Array1>_RTT = new x10.rtt.RuntimeType<Array1>(
/* base class */Array1.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 23
public boolean
                  run(
                  ){
        
//#line 25
final x10.
          array.
          Region e =
          ((x10.
          array.
          Region)(x10.
          array.
          Region.makeRectangular((int)(1),
                                 (int)(10))));
        
//#line 26
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
final x10.
          array.
          Array<java.lang.Integer> ia =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Integer>(x10.rtt.Types.INT,
                                   r,
                                   new x10.core.fun.Fun_0_1<x10.
                                     array.
                                     Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                     array.
                                     Point id$6778) { return apply(id$6778);}
                                   public final int apply(final x10.
                                     array.
                                     Point id$6778) { {
                                       
//#line 27
return 0;
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
        {
            
//#line 29
final x10.
              array.
              Region p6857 =
              ((x10.
              array.
              Region)(e));
            
//#line 29
final x10.core.Rail<java.lang.Integer> p6858 =
              ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Integer>makeVarRail(x10.rtt.Types.INT, ((int)(1)))));
            
//#line 29
final int i6859min6860 =
              p6857.min((int)(0));
            
//#line 29
final int i6859max6861 =
              p6857.max((int)(0));
            
//#line 29
for (
//#line 29
int i6859 =
                               i6859min6860;
                             ((((int)(i6859))) <= (((int)(i6859max6861))));
                             
//#line 29
i6859 += 1) {
                
//#line 29
final int i =
                  i6859;
                
//#line 29
((int[])p6858.value)[0] = i6859;
                
//#line 29
final x10.
                  array.
                  Point p =
                  ((x10.
                  array.
                  Point)(x10.
                  array.
                  Point.make(p6858)));
                {
                    {
                        
//#line 30
final x10.
                          array.
                          Region q6852 =
                          ((x10.
                          array.
                          Region)(e));
                        
//#line 30
final x10.core.Rail<java.lang.Integer> q6853 =
                          ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Integer>makeVarRail(x10.rtt.Types.INT, ((int)(1)))));
                        
//#line 30
final int j6854min6855 =
                          q6852.min((int)(0));
                        
//#line 30
final int j6854max6856 =
                          q6852.max((int)(0));
                        
//#line 30
for (
//#line 30
int j6854 =
                                           j6854min6855;
                                         ((((int)(j6854))) <= (((int)(j6854max6856))));
                                         
//#line 30
j6854 += 1) {
                            
//#line 30
final int j =
                              j6854;
                            
//#line 30
((int[])q6853.value)[0] = j6854;
                            
//#line 30
final x10.
                              array.
                              Point q =
                              ((x10.
                              array.
                              Point)(x10.
                              array.
                              Point.make(q6853)));
                            {
                                
//#line 31
harness.
                                  x10Test.chk((boolean)(((int) ia.apply$G((int)(i),
                                                                          (int)(j))) ==
                                              ((int) 0)));
                                
//#line 32
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
            
//#line 36
final x10.
              array.
              Region p6862 =
              ((x10.
              array.
              Region)(r));
            
//#line 36
final x10.core.Rail<java.lang.Integer> p6863 =
              ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Integer>makeVarRail(x10.rtt.Types.INT, ((int)(2)))));
            
//#line 36
final int j6864min6865 =
              p6862.min((int)(1));
            
//#line 36
final int j6864max6866 =
              p6862.max((int)(1));
            
//#line 36
final int i6867min6868 =
              p6862.min((int)(0));
            
//#line 36
final int i6867max6869 =
              p6862.max((int)(0));
            
//#line 36
for (
//#line 36
int i6867 =
                               i6867min6868;
                             ((((int)(i6867))) <= (((int)(i6867max6869))));
                             
//#line 36
i6867 += 1) {
                
//#line 36
final int i =
                  i6867;
                
//#line 36
((int[])p6863.value)[0] = i6867;
                
//#line 36
for (
//#line 36
int j6864 =
                                   j6864min6865;
                                 ((((int)(j6864))) <= (((int)(j6864max6866))));
                                 
//#line 36
j6864 += 1) {
                    
//#line 36
final int j =
                      j6864;
                    
//#line 36
((int[])p6863.value)[1] = j6864;
                    
//#line 36
final x10.
                      array.
                      Point p =
                      ((x10.
                      array.
                      Point)(x10.
                      array.
                      Point.make(p6863)));
                    {
                        
//#line 37
final x10.
                          array.
                          Point q1 =
                          ((x10.
                          array.
                          Point)(x10.
                          array.
                          Point.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { i,j })/* } */)));
                        
//#line 38
harness.
                          x10Test.chk((boolean)(((int) i) ==
                                      ((int) q1.apply((int)(0)))));
                        
//#line 39
harness.
                          x10Test.chk((boolean)(((int) j) ==
                                      ((int) q1.apply((int)(1)))));
                        
//#line 40
harness.
                          x10Test.chk((boolean)(((int) ia.apply$G((int)(i),
                                                                  (int)(j))) ==
                                      ((int) ((((int)(i))) + (((int)(j)))))));
                        
//#line 41
harness.
                          x10Test.chk((boolean)(((int) ia.apply$G((int)(i),
                                                                  (int)(j))) ==
                                      ((int) ia.apply$G(p))));
                        
//#line 42
harness.
                          x10Test.chk((boolean)(((int) ia.apply$G(q1)) ==
                                      ((int) ia.apply$G(p))));
                        
//#line 43
ia.set$G((int)(((((int)(ia.apply$G(p)))) - (((int)(1))))),
                                             p);
                        
//#line 44
harness.
                          x10Test.chk((boolean)(((int) ia.apply$G(p)) ==
                                      ((int) ((((int)(((((int)(i))) + (((int)(j))))))) - (((int)(1)))))));
                        
//#line 45
harness.
                          x10Test.chk((boolean)(((int) ia.apply$G(q1)) ==
                                      ((int) ia.apply$G(p))));
                    }
                }
            }
        }
        
//#line 48
return true;
    }
    
    
//#line 51
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
    							Array1.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$6779)  {
        
//#line 52
new Array1().execute();
    }/* } */
    
    public Array1() {
        super();
    }

}
