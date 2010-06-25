
public class BoxArrayAssign
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<BoxArrayAssign>_RTT = new x10.rtt.RuntimeType<BoxArrayAssign>(
/* base class */BoxArrayAssign.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 25
public boolean
                  run(
                  ){
        
//#line 26
final x10.
          array.
          Array<x10.
          util.
          Box<x10.
          lang.
          Complex>> table =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<x10.
          util.
          Box<x10.
          lang.
          Complex>>(new x10.rtt.ParameterizedType(x10.util.Box._RTT, x10.lang.Complex._RTT),
                    x10.
                      array.
                      Region.makeRectangular((int)(1),
                                             (int)(5)),
                    new x10.core.fun.Fun_0_1<x10.
                      array.
                      Point, x10.
                      util.
                      Box<x10.
                      lang.
                      Complex>>() {public final x10.
                      util.
                      Box<x10.
                      lang.
                      Complex> apply$G(final x10.
                      array.
                      Point id$18433) { return apply(id$18433);}
                    public final x10.
                      util.
                      Box<x10.
                      lang.
                      Complex> apply(final x10.
                      array.
                      Point id$18433) { {
                        
//#line 26
return (((x10.
                                              util.
                                              Box)
                                              (null)));
                    }}
                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return new x10.rtt.ParameterizedType(x10.util.Box._RTT, x10.lang.Complex._RTT);return null;
                    }
                    })));
        
//#line 27
/* template:forloop { */for (x10.core.Iterator p__ = (table).iterator(); p__.hasNext(); ) {
        	final  x10.
          array.
          Point p = (x10.
          array.
          Point) p__.next$G();
        	
{
            
//#line 27
x10.
              lang.
              Runtime.runAsync(x10.
                                 lang.
                                 Runtime.here(),
                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                   
//#line 27
table.set$G(null,
                                                           p);
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
        }
        }/* } */
        
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
    							BoxArrayAssign.main(args);
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
new BoxArrayAssign().execute();
    }/* } */
    
    public BoxArrayAssign() {
        super();
    }

}
