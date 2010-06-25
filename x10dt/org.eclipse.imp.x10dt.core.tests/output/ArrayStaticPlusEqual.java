
public class ArrayStaticPlusEqual
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ArrayStaticPlusEqual>_RTT = new x10.rtt.RuntimeType<ArrayStaticPlusEqual>(
/* base class */ArrayStaticPlusEqual.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 16
final static x10.core.Rail<java.lang.Integer>
      v =
      (new java.lang.Object() {final x10.core.Rail<java.lang.Integer> apply(int length) {int[] array = new int[length];for (int x$ = 0; x$ < length; x$++) {final int x = x$;array[x] = 0;}return new x10.core.Rail<java.lang.Integer>(x10.rtt.Types.INT, 2, array);}}.apply(2));
    
    
//#line 18
public boolean
                  run(
                  ){
        
//#line 19
x10.
          lang.
          Runtime.runAt(x10.lang.Place.place(x10.core.Ref.home(ArrayStaticPlusEqual.v)),
                        new x10.core.fun.VoidFun_0_0() {public final void apply() { {
                            {
                                
//#line 20
final int i17567min17568 =
                                  0;
                                
//#line 20
final int i17567max17569 =
                                  1;
                                
//#line 20
for (
//#line 20
int i17567 =
                                                   i17567min17568;
                                                 ((((int)(i17567))) <= (((int)(i17567max17569))));
                                                 
//#line 20
i17567 += 1) {
                                    
//#line 20
final int i =
                                      i17567;
                                    {
                                        
//#line 20
new x10.core.fun.Fun_0_3<x10.core.Rail<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.core.Rail<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer z) { return apply(x,(int)y0,(int)z);}
                                        public final int apply(final x10.core.Rail<java.lang.Integer> x, final int y0, final int z) { {
                                            
//#line 20
return ((int[])x.value)[y0] = ((((int)(((int[])x.value)[y0]))) + (((int)(z))));
                                        }}
                                        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.core.Rail._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;return null;
                                        }
                                        }.apply(ArrayStaticPlusEqual.v,
                                                i,
                                                5);
                                    }
                                }
                            }
                            {
                                
//#line 21
final int i17570min17571 =
                                  0;
                                
//#line 21
final int i17570max17572 =
                                  1;
                                
//#line 21
for (
//#line 21
int i17570 =
                                                   i17570min17571;
                                                 ((((int)(i17570))) <= (((int)(i17570max17572))));
                                                 
//#line 21
i17570 += 1) {
                                    
//#line 21
final int i =
                                      i17570;
                                    {
                                        
//#line 21
harness.
                                          x10Test.chk((boolean)(((int) ((int[])ArrayStaticPlusEqual.v.value)[i]) ==
                                                      ((int) 5)));
                                    }
                                }
                            }
                        }}
                        });
        
//#line 23
return true;
    }
    
    
//#line 26
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
    							ArrayStaticPlusEqual.main(args);
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
        
//#line 27
new ArrayStaticPlusEqual().execute();
    }/* } */
    
    public ArrayStaticPlusEqual() {
        super();
    }

}
