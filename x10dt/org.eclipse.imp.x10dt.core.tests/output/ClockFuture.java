
public class ClockFuture
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockFuture>_RTT = new x10.rtt.RuntimeType<ClockFuture>(
/* base class */ClockFuture.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 22
private boolean
      clock_has_advanced;
    
    
//#line 24
public int
                  m(
                  ){
        
//#line 25
int ret =
          0;
        
//#line 26
try {{
            
//#line 26
x10.
              lang.
              Runtime.lock();
            
//#line 26
while (true) {
                
//#line 26
if (clock_has_advanced) {
                    {
                        
//#line 27
ret = 42;
                    }
                    
//#line 26
break;
                }
                
//#line 26
x10.
                  lang.
                  Runtime.await();
            }
        }}finally {{
              
//#line 26
x10.
                lang.
                Runtime.release();
          }}
        
//#line 29
return ret;
        }
    
    
//#line 32
public boolean
                  run(
                  ){
        
//#line 33
final x10.
          lang.
          Clock c =
          x10.
          lang.
          Clock.make();
        
//#line 34
x10.
          lang.
          Future<java.lang.Integer> f =
          x10.
          lang.
          Runtime.<java.lang.Integer>evalFuture(x10.rtt.Types.INT,
                                                x10.
                                                  lang.
                                                  Runtime.here(),
                                                new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                public final int apply() { {
                                                    
//#line 34
return ClockFuture.this.m();
                                                }}
                                                public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                }
                                                });
        
//#line 35
x10.
          io.
          Console.OUT.print("1 ... ");
        
//#line 37
x10.
          lang.
          Runtime.next();
        
//#line 38
x10.
          io.
          Console.OUT.print("2 ... ");
        
//#line 39
try {{
            
//#line 39
x10.
              lang.
              Runtime.lock();
            {
                
//#line 39
this.clock_has_advanced = true;
            }
        }}finally {{
              
//#line 39
x10.
                lang.
                Runtime.release();
          }}
        
//#line 40
x10.
          io.
          Console.OUT.print("3 ...");
        
//#line 41
int result =
          ((java.lang.Integer)(f.force$G()));
        
//#line 42
harness.
          x10Test.chk((boolean)(((int) result) ==
                      ((int) 42)));
        
//#line 43
x10.
          io.
          Console.OUT.println("4");
        
//#line 44
return true;
        }
    
    
//#line 47
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
    							ClockFuture.main(args);
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
        
//#line 48
new ClockFuture().execute();
    }/* } */
    
    public ClockFuture() {
        super();
        
//#line 22
this.clock_has_advanced = false;
    }
    
    }
    