
public class MontyPi2
extends x10.core.Ref
{
public static class /* Join: { */RTT/* } */ extends x10.types.RuntimeType<MontyPi2> {
public static final /* Join: { */RTT/* } */ it = new /* Join: { */RTT/* } */();
    
    
    public RTT() {super(MontyPi2.class);
                      }
    public boolean instanceof$(java.lang.Object o) {
    if (! (o instanceof MontyPi2)) return false;
        return true;
    }
    public java.util.List<x10.types.Type<?>> getTypeParameters() {
    return null;
    }
}

    
    
//#line 5
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
    			x10.runtime.Runtime.start(
    				// body of main activity
    				new x10.core.fun.VoidFun_0_0() { 
    					public void apply() {
    						// preload classes
    						if (Boolean.getBoolean("x10.PRELOAD_CLASSES")) {
    							x10.runtime.impl.java.PreLoader.preLoad(this.getClass().getEnclosingClass(), Boolean.getBoolean("x10.PRELOAD_STRINGS"));
    						}
    
    						// catch and rethrow checked exceptions
    						// (closures cannot throw checked exceptions)
    						try {
    							// call the original app-main method
    							MontyPi2.main(args);
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
    			if (t instanceof x10.lang.MultipleExceptions) {
    				x10.core.ValRail<Throwable> exceptions = ((x10.lang.MultipleExceptions) t).exceptions;
    				for(int i = 0; i < exceptions.length; i++) {
    					exceptions.get(i).printStackTrace();
    				}
    			}
    		}
    	}
    }
    
    // the original app-main method
    public static void main(final x10.core.Rail<java.lang.String> s)  {
        
//#line 7
final int N =
          ((/* template:equalsequals { */x10.types.Equality.equalsequals(/* template:place-check { */((x10.core.Rail<java.lang.String>) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), s))/* } */.length,0)/* } */))
          ? 500
          : java.lang.Integer.parseInt(/* template:place-check { */((x10.core.Rail<java.lang.String>) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), s))/* } */.apply(0));
        
//#line 8
final x10.
          lang.
          Array<java.lang.Double> result =
          x10.
          lang.
          Array.<java.lang.Double>make(x10.types.Types.DOUBLE,
                                       x10.
                                         lang.
                                         Dist.makeUnique(),
                                       new x10.lang.Box<x10.core.fun.Fun_0_1<x10.
                                         lang.
                                         Point,java.lang.Double>>(new x10.core.fun.Fun_0_1.RTT(x10.lang.Point.RTT.it,x10.types.Types.DOUBLE), new x10.core.fun.Fun_0_1</* Join: { */x10.
                                         lang.
                                         Point, java.lang.Double/* } */>() {public final java.lang.Double apply(/* Join: { */final x10.
                                         lang.
                                         Point id146/* } */) { {
                                           
//#line 8
return 0.0;
                                       }}
                                       public x10.types.Type<?> rtt_x10$lang$Fun_0_1_Z1() { return x10.lang.Point.RTT.it; }
                                       public x10.types.Type<?> rtt_x10$lang$Fun_0_1_U() { return x10.types.Types.DOUBLE; }
                                       }));
        
//#line 10
/* template:forloop { */for (x10.core.Iterator p__ = (result.
                                                                            dist).iterator(); p__.hasNext(); ) {
        	final  x10.
          lang.
          Point p = (x10.
          lang.
          Point) p__.next();
        	/* Join: { *//* Join: { *//* } */
{
            
//#line 10
x10.
              runtime.
              Runtime.runAt((result.
                               dist).apply(p),
                            new x10.core.fun.VoidFun_0_0() {public final void apply(/* Join: { *//* } */) { {
                                
//#line 11
final x10.
                                  util.
                                  Random r =
                                  new x10.
                                  util.
                                  Random();
                                
//#line 12
double a =
                                  0.0;
                                
//#line 13
/* template:forloop { */for (x10.core.Iterator j__ = (x10.
                                  lang.
                                  Region.makeRectangular(1,
                                                         N)).iterator(); j__.hasNext(); ) {
                                	final  x10.
                                  lang.
                                  Point j = (x10.
                                  lang.
                                  Point) j__.next();
                                	/* Join: { *//* Join: { *//* } */
{
                                    
//#line 14
final double x =
                                      (/* template:place-check { */((x10.
                                      util.
                                      Random) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), r))/* } */).nextDouble();
                                    
//#line 14
final double y =
                                      (/* template:place-check { */((x10.
                                      util.
                                      Random) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), r))/* } */).nextDouble();
                                    
//#line 15
if (x *
                                                    x +
                                                    y *
                                                    y <=
                                                    1.0) {
                                        
//#line 15
a += ((double) (int) (java.lang.Integer)
                                                           (1));
                                    }
                                }/* } */
                                }/* } */
                                
//#line 17
(result).set(a,
                                                         p);
                            }}
                            });
        }/* } */
        }/* } */
        
//#line 20
final double pi =
          ((double) (int) (java.lang.Integer)
            (4)) *
        (result).reduce(new x10.core.fun.Fun_0_2</* Join: { */java.lang.Double, java.lang.Double, java.lang.Double/* } */>() {public final java.lang.Double apply(/* Join: { */final java.lang.Double x, final java.lang.Double y/* } */) { {
                            
//#line 20
return x +
                            y;
                        }}
                        public x10.types.Type<?> rtt_x10$lang$Fun_0_2_Z1() { return x10.types.Types.DOUBLE; }
                        public x10.types.Type<?> rtt_x10$lang$Fun_0_2_Z2() { return x10.types.Types.DOUBLE; }
                        public x10.types.Type<?> rtt_x10$lang$Fun_0_2_U() { return x10.types.Types.DOUBLE; }
                        },
                        ((double) (int) (java.lang.Integer)
                          (0))) /
        ((double) (int) (java.lang.Integer)
          ((N *
            x10.
              lang.
              Place.MAX_PLACES)));
        
//#line 21
(x10.
          io.
          Console.OUT).println("The value of pi is " +
                               pi);
    }/* } */
    
    
//#line 4
public MontyPi2() {
        
//#line 4
super();
    }
}
