
public class ArrayInitializer1a
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ArrayInitializer1a>_RTT = new x10.rtt.RuntimeType<ArrayInitializer1a>(
/* base class */ArrayInitializer1a.class
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
                                     Region[] { e,e,e })/* } */)));
        
//#line 24
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
                                     Point id14541) { return apply(id14541);}
                                   public final int apply(final x10.
                                     array.
                                     Point id14541) { {
                                       
//#line 24
final int i =
                                         id14541.apply((int)(0));
                                       
//#line 24
final int j =
                                         id14541.apply((int)(1));
                                       
//#line 24
final int k =
                                         id14541.apply((int)(2));
                                       
//#line 24
return i;
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
        {
            
//#line 26
final x10.
              array.
              Region p14617 =
              ((x10.
              array.
              Region)(ia.
                        region));
            
//#line 26
final x10.core.Rail<java.lang.Integer> p14618 =
              ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Integer>makeVarRail(x10.rtt.Types.INT, ((int)(3)))));
            
//#line 26
final int k14619min14620 =
              p14617.min((int)(2));
            
//#line 26
final int k14619max14621 =
              p14617.max((int)(2));
            
//#line 26
final int j14622min14623 =
              p14617.min((int)(1));
            
//#line 26
final int j14622max14624 =
              p14617.max((int)(1));
            
//#line 26
final int i14625min14626 =
              p14617.min((int)(0));
            
//#line 26
final int i14625max14627 =
              p14617.max((int)(0));
            
//#line 26
for (
//#line 26
int i14625 =
                               i14625min14626;
                             ((((int)(i14625))) <= (((int)(i14625max14627))));
                             
//#line 26
i14625 += 1) {
                
//#line 26
final int i =
                  i14625;
                
//#line 26
((int[])p14618.value)[0] = i14625;
                
//#line 26
for (
//#line 26
int j14622 =
                                   j14622min14623;
                                 ((((int)(j14622))) <= (((int)(j14622max14624))));
                                 
//#line 26
j14622 += 1) {
                    
//#line 26
final int j =
                      j14622;
                    
//#line 26
((int[])p14618.value)[1] = j14622;
                    
//#line 26
for (
//#line 26
int k14619 =
                                       k14619min14620;
                                     ((((int)(k14619))) <= (((int)(k14619max14621))));
                                     
//#line 26
k14619 += 1) {
                        
//#line 26
final int k =
                          k14619;
                        
//#line 26
((int[])p14618.value)[2] = k14619;
                        
//#line 26
final x10.
                          array.
                          Point p =
                          ((x10.
                          array.
                          Point)(x10.
                          array.
                          Point.make(p14618)));
                        {
                            
//#line 26
harness.
                              x10Test.chk((boolean)(((int) ia.apply$G(p)) ==
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
    							ArrayInitializer1a.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$14542)  {
        
//#line 32
new ArrayInitializer1a().execute();
    }/* } */
    
    public ArrayInitializer1a() {
        super();
    }

}
