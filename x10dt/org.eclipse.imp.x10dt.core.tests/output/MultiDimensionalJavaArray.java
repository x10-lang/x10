
public class MultiDimensionalJavaArray
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<MultiDimensionalJavaArray>_RTT = new x10.rtt.RuntimeType<MultiDimensionalJavaArray>(
/* base class */MultiDimensionalJavaArray.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 23
final static double
      PI =
      x10.
      lang.
      Math.PI;
    
    
//#line 25
public boolean
                  run(
                  ){
        
//#line 26
final x10.
          array.
          Region MIN =
          ((x10.
          array.
          Region)(x10.
          array.
          Region.makeRectangular((int)(0),
                                 (int)(99))));
        
//#line 27
final x10.
          array.
          Region MAJ =
          ((x10.
          array.
          Region)(x10.
          array.
          Region.makeRectangular((int)(0),
                                 (int)(9))));
        
//#line 28
final x10.
          array.
          Array<x10.
          array.
          Array<java.lang.Double>> a =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<x10.
          array.
          Array<java.lang.Double>>(new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.DOUBLE),
                                   MIN,
                                   new x10.core.fun.Fun_0_1<x10.
                                     array.
                                     Point, x10.
                                     array.
                                     Array<java.lang.Double>>() {public final x10.
                                     array.
                                     Array<java.lang.Double> apply$G(final x10.
                                     array.
                                     Point id$25036) { return apply(id$25036);}
                                   public final x10.
                                     array.
                                     Array<java.lang.Double> apply(final x10.
                                     array.
                                     Point id$25036) { {
                                       
//#line 28
return new x10.
                                         array.
                                         Array<java.lang.Double>(x10.rtt.Types.DOUBLE,
                                                                 MAJ);
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.DOUBLE);return null;
                                   }
                                   })));
        {
            
//#line 30
final x10.
              array.
              Region p25113 =
              ((x10.
              array.
              Region)((x10.
                         array.
                         Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                                    array.
                                                    Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                                    array.
                                                    Region[] { MIN,MAJ })/* } */))));
            
//#line 30
final int j25114min25115 =
              p25113.min((int)(1));
            
//#line 30
final int j25114max25116 =
              p25113.max((int)(1));
            
//#line 30
final int i25117min25118 =
              p25113.min((int)(0));
            
//#line 30
final int i25117max25119 =
              p25113.max((int)(0));
            
//#line 30
for (
//#line 30
int i25117 =
                               i25117min25118;
                             ((((int)(i25117))) <= (((int)(i25117max25119))));
                             
//#line 30
i25117 += 1) {
                
//#line 30
final int i =
                  i25117;
                
//#line 30
for (
//#line 30
int j25114 =
                                   j25114min25115;
                                 ((((int)(j25114))) <= (((int)(j25114max25116))));
                                 
//#line 30
j25114 += 1) {
                    
//#line 30
final int j =
                      j25114;
                    {
                        
//#line 31
a.apply$G((int)(i)).set$G((double)((((((double)(((double)(int)(((int)(((((int)(i))) * (((int)(j))))))))))) / (((double)(MultiDimensionalJavaArray.PI)))))),
                                                              (int)(j));
                    }
                }
            }
        }
        
//#line 33
final x10.
          array.
          Array<java.lang.Double> d =
          ((x10.
          array.
          Array)(a.apply$G((int)(((((int)(MIN.max((int)(0))))) / (((int)(2))))))));
        {
            
//#line 34
final x10.
              array.
              Region p25120 =
              ((x10.
              array.
              Region)(MAJ));
            
//#line 34
final int j25121min25122 =
              p25120.min((int)(0));
            
//#line 34
final int j25121max25123 =
              p25120.max((int)(0));
            
//#line 34
for (
//#line 34
int j25121 =
                               j25121min25122;
                             ((((int)(j25121))) <= (((int)(j25121max25123))));
                             
//#line 34
j25121 += 1) {
                
//#line 34
final int j =
                  j25121;
                {
                    
//#line 35
harness.
                      x10Test.chk((boolean)(((double) d.apply$G((int)(j))) ==
                                  ((double) ((((((double)(((double)(int)(((int)(((((int)(((((int)(MIN.max((int)(0))))) / (((int)(2))))))) * (((int)(j))))))))))) / (((double)(MultiDimensionalJavaArray.PI)))))))));
                }
            }
        }
        
//#line 37
return true;
    }
    
    
//#line 40
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
    							MultiDimensionalJavaArray.main(args);
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
        
//#line 41
new MultiDimensionalJavaArray().execute();
    }/* } */
    
    public MultiDimensionalJavaArray() {
        super();
    }

}
