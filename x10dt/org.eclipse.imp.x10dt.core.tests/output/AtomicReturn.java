
public class AtomicReturn
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<AtomicReturn>_RTT = new x10.rtt.RuntimeType<AtomicReturn>(
/* base class */AtomicReturn.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 18
int
      a;
    
//#line 19
final static int
      N =
      100;
    
    
//#line 21
int
                  update1(
                  ){
        
//#line 22
try {{
            
//#line 22
x10.
              lang.
              Runtime.lock();
            {
                
//#line 23
this.a += 1;
                
//#line 24
return a;
            }
        }}finally {{
              
//#line 22
x10.
                lang.
                Runtime.release();
          }}
        }
    
    
//#line 28
int
                  update3(
                  ){
        
//#line 29
try {{
            
//#line 29
x10.
              lang.
              Runtime.lock();
            {
                
//#line 30
return new x10.core.fun.Fun_0_1<java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(java.lang.Integer t) { return apply((int)t);}
                public final int apply(int t) { {
                    
//#line 30
return ((((int)(t))) - (((int)(1))));
                }}
                public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;if (i ==1) return x10.rtt.Types.INT;return null;
                }
                }.apply(this.a += 1);
            }
        }}finally {{
              
//#line 29
x10.
                lang.
                Runtime.release();
          }}
        }
    
    
//#line 34
public boolean
                  run(
                  ){
        
//#line 35
this.update1();
        
//#line 36
this.update3();
        
//#line 37
x10.
          io.
          Console.OUT.println((int)(a));
        
//#line 38
return ((int) a) ==
        ((int) 2);
    }
    
    
//#line 41
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
    							AtomicReturn.main(args);
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
        
//#line 42
new AtomicReturn().execute();
    }/* } */
    
    public AtomicReturn() {
        super();
        
//#line 18
this.a = 0;
    }
    
    }
    