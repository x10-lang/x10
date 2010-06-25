
public class Array1Exploded
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<Array1Exploded>_RTT = new x10.rtt.RuntimeType<Array1Exploded>(
/* base class */Array1Exploded.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 20
public int
                  select(
                  final x10.
                    array.
                    Point p,
                  final x10.
                    array.
                    Point id7752){
        
//#line 20
final int i =
          p.apply((int)(0));
        
//#line 20
final int j =
          p.apply((int)(1));
        
//#line 20
final int k =
          id7752.apply((int)(0));
        
//#line 20
final int l =
          id7752.apply((int)(1));
        
//#line 20
return ((((int)(i))) + (((int)(k))));
    }
    
    
//#line 22
public boolean
                  run(
                  ){
        
//#line 24
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
        
//#line 25
final x10.
          array.
          Array<java.lang.Integer> ia =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Integer>(x10.rtt.Types.INT,
                                   r)));
        {
            
//#line 27
final x10.
              array.
              Region p7828 =
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
final x10.core.Rail<java.lang.Integer> p7829 =
              ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Integer>makeVarRail(x10.rtt.Types.INT, ((int)(2)))));
            
//#line 27
final int j7830min7831 =
              p7828.min((int)(1));
            
//#line 27
final int j7830max7832 =
              p7828.max((int)(1));
            
//#line 27
final int i7833min7834 =
              p7828.min((int)(0));
            
//#line 27
final int i7833max7835 =
              p7828.max((int)(0));
            
//#line 27
for (
//#line 27
int i7833 =
                               i7833min7834;
                             ((((int)(i7833))) <= (((int)(i7833max7835))));
                             
//#line 27
i7833 += 1) {
                
//#line 27
final int i =
                  i7833;
                
//#line 27
((int[])p7829.value)[0] = i7833;
                
//#line 27
for (
//#line 27
int j7830 =
                                   j7830min7831;
                                 ((((int)(j7830))) <= (((int)(j7830max7832))));
                                 
//#line 27
j7830 += 1) {
                    
//#line 27
final int j =
                      j7830;
                    
//#line 27
((int[])p7829.value)[1] = j7830;
                    
//#line 27
final x10.
                      array.
                      Point p =
                      ((x10.
                      array.
                      Point)(x10.
                      array.
                      Point.make(p7829)));
                    {
                        
//#line 28
harness.
                          x10Test.chk((boolean)(((int) ia.apply$G(p)) ==
                                      ((int) 0)));
                        
//#line 29
ia.set$G((int)(((((int)(i))) + (((int)(j))))),
                                             p);
                    }
                }
            }
        }
        {
            
//#line 32
final x10.
              array.
              Region p7836 =
              ((x10.
              array.
              Region)(r));
            
//#line 32
final x10.core.Rail<java.lang.Integer> p7837 =
              ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Integer>makeVarRail(x10.rtt.Types.INT, ((int)(2)))));
            
//#line 32
final int j7838min7839 =
              p7836.min((int)(1));
            
//#line 32
final int j7838max7840 =
              p7836.max((int)(1));
            
//#line 32
final int i7841min7842 =
              p7836.min((int)(0));
            
//#line 32
final int i7841max7843 =
              p7836.max((int)(0));
            
//#line 32
for (
//#line 32
int i7841 =
                               i7841min7842;
                             ((((int)(i7841))) <= (((int)(i7841max7843))));
                             
//#line 32
i7841 += 1) {
                
//#line 32
final int i =
                  i7841;
                
//#line 32
((int[])p7837.value)[0] = i7841;
                
//#line 32
for (
//#line 32
int j7838 =
                                   j7838min7839;
                                 ((((int)(j7838))) <= (((int)(j7838max7840))));
                                 
//#line 32
j7838 += 1) {
                    
//#line 32
final int j =
                      j7838;
                    
//#line 32
((int[])p7837.value)[1] = j7838;
                    
//#line 32
final x10.
                      array.
                      Point p =
                      ((x10.
                      array.
                      Point)(x10.
                      array.
                      Point.make(p7837)));
                    {
                        
//#line 33
final x10.
                          array.
                          Point q1 =
                          ((x10.
                          array.
                          Point)(x10.
                          array.
                          Point.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { i,j })/* } */)));
                        
//#line 33
final int m =
                          q1.apply((int)(0));
                        
//#line 33
final int n =
                          q1.apply((int)(1));
                        
//#line 34
harness.
                          x10Test.chk((boolean)(((int) i) ==
                                      ((int) m)));
                        
//#line 35
harness.
                          x10Test.chk((boolean)(((int) j) ==
                                      ((int) n)));
                        
//#line 36
harness.
                          x10Test.chk((boolean)(((int) ia.apply$G((int)(i),
                                                                  (int)(j))) ==
                                      ((int) ((((int)(i))) + (((int)(j)))))));
                        
//#line 37
harness.
                          x10Test.chk((boolean)(((int) ia.apply$G((int)(i),
                                                                  (int)(j))) ==
                                      ((int) ia.apply$G(p))));
                        
//#line 38
harness.
                          x10Test.chk((boolean)(((int) ia.apply$G(q1)) ==
                                      ((int) ia.apply$G(p))));
                    }
                }
            }
        }
        
//#line 41
harness.
          x10Test.chk((boolean)(((int) 4) ==
                      ((int) this.select(x10.
                                           array.
                                           Point.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { 1,2 })/* } */),
                                         x10.
                                           array.
                                           Point.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { 3,4 })/* } */)))));
        
//#line 43
return true;
    }
    
    
//#line 46
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
    							Array1Exploded.main(args);
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
        
//#line 47
new Array1Exploded().execute();
    }/* } */
    
    public Array1Exploded() {
        super();
    }

}
