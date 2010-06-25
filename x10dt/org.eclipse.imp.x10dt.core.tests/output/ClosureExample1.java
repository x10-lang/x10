
public class ClosureExample1
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClosureExample1>_RTT = new x10.rtt.RuntimeType<ClosureExample1>(
/* base class */ClosureExample1.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 29
<T> x10.
                  util.
                  Box<T>
                  find(
                  final x10.rtt.Type T,
                  final x10.core.fun.Fun_0_1<T,java.lang.Boolean> f,
                  final x10.
                    util.
                    List<T> xs){
        
//#line 30
for (
//#line 30
final x10.
                           util.
                           ListIterator<T> x53834 =
                           xs.iterator();
                         x53834.hasNext();
                         ) {
            
//#line 30
final T x =
              ((T)(x53834.next$G()));
            
//#line 32
if (f.apply$G(x)) {
                
//#line 32
return x10.
                  util.
                  Box.<T>$implicit_convert(T,
                                           x);
            }
        }
        
//#line 33
return null;
    }
    
    
//#line 36
final x10.
      util.
      List<java.lang.Integer>
      xs;
    
//#line 39
final x10.
      util.
      Box<java.lang.Integer>
      x;
    
    
//#line 41
public boolean
                  run(
                  ){
        
//#line 43
xs.add((int)(1));
        
//#line 44
xs.add((int)(2));
        
//#line 45
xs.add((int)(3));
        
//#line 46
return true;
    }
    
    
//#line 49
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
    							ClosureExample1.main(args);
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
        
//#line 50
new ClosureExample1().execute();
    }/* } */
    
    public ClosureExample1() {
        super();
        
//#line 36
this.xs = ((x10.
          util.
          List)(((x10.
                  util.
                  List)
                  new x10.
                  util.
                  ArrayList<java.lang.Integer>(x10.rtt.Types.INT))));
        
//#line 39
this.x = this.<java.lang.Integer>find(x10.rtt.Types.INT,
                                                          new x10.core.fun.Fun_0_1<java.lang.Integer, java.lang.Boolean>() {public final java.lang.Boolean apply$G(final java.lang.Integer x) { return apply((int)x);}
                                                          public final boolean apply(final int x) { {
                                                              
//#line 39
return (((((int)(x))) > (((int)(0)))));
                                                          }}
                                                          public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;if (i ==1) return x10.rtt.Types.BOOLEAN;return null;
                                                          }
                                                          },
                                                          xs);
    }

}
