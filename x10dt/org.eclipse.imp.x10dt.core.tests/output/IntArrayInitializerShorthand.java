
public class IntArrayInitializerShorthand
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<IntArrayInitializerShorthand>_RTT = new x10.rtt.RuntimeType<IntArrayInitializerShorthand>(
/* base class */IntArrayInitializerShorthand.class
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
          Region r =
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
        
//#line 22
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
                                     Point id24057) { return apply(id24057);}
                                   public final int apply(final x10.
                                     array.
                                     Point id24057) { {
                                       
//#line 22
final int i =
                                         id24057.apply((int)(0));
                                       
//#line 22
final int j =
                                         id24057.apply((int)(1));
                                       
//#line 22
return ((((int)(i))) + (((int)(j))));
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
        {
            
//#line 24
final x10.
              array.
              Region p24130 =
              ((x10.
              array.
              Region)(r));
            
//#line 24
final x10.core.Rail<java.lang.Integer> p24131 =
              ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Integer>makeVarRail(x10.rtt.Types.INT, ((int)(2)))));
            
//#line 24
final int j24132min24133 =
              p24130.min((int)(1));
            
//#line 24
final int j24132max24134 =
              p24130.max((int)(1));
            
//#line 24
final int i24135min24136 =
              p24130.min((int)(0));
            
//#line 24
final int i24135max24137 =
              p24130.max((int)(0));
            
//#line 24
for (
//#line 24
int i24135 =
                               i24135min24136;
                             ((((int)(i24135))) <= (((int)(i24135max24137))));
                             
//#line 24
i24135 += 1) {
                
//#line 24
final int i =
                  i24135;
                
//#line 24
((int[])p24131.value)[0] = i24135;
                
//#line 24
for (
//#line 24
int j24132 =
                                   j24132min24133;
                                 ((((int)(j24132))) <= (((int)(j24132max24134))));
                                 
//#line 24
j24132 += 1) {
                    
//#line 24
final int j =
                      j24132;
                    
//#line 24
((int[])p24131.value)[1] = j24132;
                    
//#line 24
final x10.
                      array.
                      Point p =
                      ((x10.
                      array.
                      Point)(x10.
                      array.
                      Point.make(p24131)));
                    {
                        
//#line 25
harness.
                          x10Test.chk((boolean)(((int) ia.apply$G(p)) ==
                                      ((int) ((((int)(i))) + (((int)(j)))))));
                    }
                }
            }
        }
        
//#line 27
return true;
    }
    
    
//#line 30
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
    							IntArrayInitializerShorthand.main(args);
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
        
//#line 31
new IntArrayInitializerShorthand().execute();
    }/* } */
    
    public IntArrayInitializerShorthand() {
        super();
    }

}
