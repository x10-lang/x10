
public class ArrayInitializer
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ArrayInitializer>_RTT = new x10.rtt.RuntimeType<ArrayInitializer>(
/* base class */ArrayInitializer.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 20
public boolean
                  run(
                  ){
        
//#line 22
final x10.
          array.
          Region e =
          ((x10.
          array.
          Region)(x10.
          array.
          Region.makeRectangular((int)(0),
                                 (int)(9))));
        
//#line 23
final x10.core.ValRail<x10.
          array.
          Region> r =
          ((x10.core.ValRail)(/* template:tuple { */x10.core.RailFactory.<x10.
          array.
          Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
          array.
          Region[] { e,e,e })/* } */));
        
//#line 25
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
                                     Region.$implicit_convert(r),
                                   new x10.core.fun.Fun_0_1<x10.
                                     array.
                                     Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                     array.
                                     Point id14310) { return apply(id14310);}
                                   public final int apply(final x10.
                                     array.
                                     Point id14310) { {
                                       
//#line 25
final int i =
                                         id14310.apply((int)(0));
                                       
//#line 25
final int j =
                                         id14310.apply((int)(1));
                                       
//#line 25
final int k =
                                         id14310.apply((int)(2));
                                       
//#line 25
return i;
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
        {
            
//#line 26
final x10.
              array.
              Region p14390 =
              ((x10.
              array.
              Region)(ia.
                        region));
            
//#line 26
final int k14391min14392 =
              p14390.min((int)(2));
            
//#line 26
final int k14391max14393 =
              p14390.max((int)(2));
            
//#line 26
final int j14394min14395 =
              p14390.min((int)(1));
            
//#line 26
final int j14394max14396 =
              p14390.max((int)(1));
            
//#line 26
final int i14397min14398 =
              p14390.min((int)(0));
            
//#line 26
final int i14397max14399 =
              p14390.max((int)(0));
            
//#line 26
for (
//#line 26
int i14397 =
                               i14397min14398;
                             ((((int)(i14397))) <= (((int)(i14397max14399))));
                             
//#line 26
i14397 += 1) {
                
//#line 26
final int i =
                  i14397;
                
//#line 26
for (
//#line 26
int j14394 =
                                   j14394min14395;
                                 ((((int)(j14394))) <= (((int)(j14394max14396))));
                                 
//#line 26
j14394 += 1) {
                    
//#line 26
final int j =
                      j14394;
                    
//#line 26
for (
//#line 26
int k14391 =
                                       k14391min14392;
                                     ((((int)(k14391))) <= (((int)(k14391max14393))));
                                     
//#line 26
k14391 += 1) {
                        
//#line 26
final int k =
                          k14391;
                        {
                            
//#line 26
harness.
                              x10Test.chk((boolean)(((int) ia.apply$G((int)(i),
                                                                      (int)(j),
                                                                      (int)(k))) ==
                                          ((int) i)));
                        }
                    }
                }
            }
        }
        
//#line 28
return true;
    }
    
    
//#line 31
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
    							ArrayInitializer.main(args);
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
        
//#line 32
new ArrayInitializer().execute();
    }/* } */
    
    public ArrayInitializer() {
        super();
    }

}
