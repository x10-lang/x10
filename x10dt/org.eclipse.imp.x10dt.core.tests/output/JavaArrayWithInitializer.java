
public class JavaArrayWithInitializer
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<JavaArrayWithInitializer>_RTT = new x10.rtt.RuntimeType<JavaArrayWithInitializer>(
/* base class */JavaArrayWithInitializer.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 22
final static int
      N =
      25;
    
    
//#line 24
public boolean
                  run(
                  ){
        
//#line 26
final x10.
          array.
          Array<java.lang.Integer> foo1 =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Integer>(x10.rtt.Types.INT,
                                   x10.
                                     array.
                                     Region.makeRectangular((int)(0),
                                                            (int)(((((int)(JavaArrayWithInitializer.N))) - (((int)(1)))))),
                                   new x10.core.fun.Fun_0_1<x10.
                                     array.
                                     Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                     array.
                                     Point id24812) { return apply(id24812);}
                                   public final int apply(final x10.
                                     array.
                                     Point id24812) { {
                                       
//#line 26
final int i =
                                         id24812.apply((int)(0));
                                       
//#line 26
return i;
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
        
//#line 28
x10.
          io.
          Console.OUT.println("1");
        {
            
//#line 30
final int i24897min24898 =
              0;
            
//#line 30
final int i24897max24899 =
              ((((int)(JavaArrayWithInitializer.N))) - (((int)(1))));
            
//#line 30
for (
//#line 30
int i24897 =
                               i24897min24898;
                             ((((int)(i24897))) <= (((int)(i24897max24899))));
                             
//#line 30
i24897 += 1) {
                
//#line 30
final int i =
                  i24897;
                {
                    
//#line 30
harness.
                      x10Test.chk((boolean)(((int) foo1.apply$G((int)(i))) ==
                                  ((int) i)));
                }
            }
        }
        
//#line 31
final x10.
          array.
          Array<java.lang.Integer> foo2 =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Integer>(x10.rtt.Types.INT,
                                   x10.
                                     array.
                                     Region.makeRectangular((int)(0),
                                                            (int)(((((int)(JavaArrayWithInitializer.N))) - (((int)(1)))))),
                                   new x10.core.fun.Fun_0_1<x10.
                                     array.
                                     Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                     array.
                                     Point id24814) { return apply(id24814);}
                                   public final int apply(final x10.
                                     array.
                                     Point id24814) { {
                                       
//#line 31
final int i =
                                         id24814.apply((int)(0));
                                       
//#line 31
return i;
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
        
//#line 33
x10.
          io.
          Console.OUT.println("2");
        {
            
//#line 35
final int i24900min24901 =
              0;
            
//#line 35
final int i24900max24902 =
              ((((int)(JavaArrayWithInitializer.N))) - (((int)(1))));
            
//#line 35
for (
//#line 35
int i24900 =
                               i24900min24901;
                             ((((int)(i24900))) <= (((int)(i24900max24902))));
                             
//#line 35
i24900 += 1) {
                
//#line 35
final int i =
                  i24900;
                {
                    
//#line 35
harness.
                      x10Test.chk((boolean)(((int) foo2.apply$G((int)(i))) ==
                                  ((int) i)));
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
    							JavaArrayWithInitializer.main(args);
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
new JavaArrayWithInitializer().execute();
    }/* } */
    
    public JavaArrayWithInitializer() {
        super();
    }

}
