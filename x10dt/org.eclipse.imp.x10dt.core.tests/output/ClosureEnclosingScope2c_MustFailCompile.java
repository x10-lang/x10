
public class ClosureEnclosingScope2c_MustFailCompile
extends ClosureTest
{public static final x10.rtt.RuntimeType<ClosureEnclosingScope2c_MustFailCompile>_RTT = new x10.rtt.RuntimeType<ClosureEnclosingScope2c_MustFailCompile>(
/* base class */ClosureEnclosingScope2c_MustFailCompile.class
, /* parents */ new x10.rtt.Type[] {ClosureTest._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 25
final int
      a =
      1;
    
    
//#line 27
public boolean
                  run(
                  ){
        
//#line 29
final int b =
          1;
        
//#line 36
this.check("new C().foo()",
                               (int)(new ClosureEnclosingScope2c_MustFailCompile.
                                 C$48(this,
                                      b).foo()),
                               (int)(3));
        
//#line 39
return result;
    }
    
    
//#line 42
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
    							ClosureEnclosingScope2c_MustFailCompile.main(args);
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
        
//#line 43
new ClosureEnclosingScope2c_MustFailCompile().execute();
    }/* } */
    
    public ClosureEnclosingScope2c_MustFailCompile() {
        super();
    }
    
    
//#line 31
private static class C$48
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<ClosureEnclosingScope2c_MustFailCompile.
      C$48>_RTT = new x10.rtt.RuntimeType<ClosureEnclosingScope2c_MustFailCompile.
      C$48>(
    /* base class */ClosureEnclosingScope2c_MustFailCompile.
      C$48.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 23
final private ClosureEnclosingScope2c_MustFailCompile
          out$;
        
//#line 29
final private int
          b;
        
//#line 32
int
          c;
        
        
//#line 33
int
                      foo(
                      ){
            
//#line 33
return ((new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                 public final int apply() { {
                                     
//#line 33
return ((((int)(((((int)(new x10.core.fun.Fun_0_1<ClosureEnclosingScope2c_MustFailCompile, ClosureEnclosingScope2c_MustFailCompile>() {public final ClosureEnclosingScope2c_MustFailCompile apply$G(final ClosureEnclosingScope2c_MustFailCompile __desugarer__var__526__) { return apply(__desugarer__var__526__);}
                                                                          public final ClosureEnclosingScope2c_MustFailCompile apply(final ClosureEnclosingScope2c_MustFailCompile __desugarer__var__526__) { {
                                                                              
//#line 33
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__526__,null))/* } */ &&
                                                                                                !(/* template:equalsequals { */x10.rtt.Equality.equalsequals(x10.lang.Place.place(x10.core.Ref.home(__desugarer__var__526__)),x10.
                                                                                                    lang.
                                                                                                    Runtime.here())/* } */)) {
                                                                                  
//#line 33
throw new java.lang.ClassCastException("ClosureEnclosingScope2c_MustFailCompile{self.home==here}");
                                                                              }
                                                                              
//#line 33
return __desugarer__var__526__;
                                                                          }}
                                                                          public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return ClosureEnclosingScope2c_MustFailCompile._RTT;if (i ==1) return ClosureEnclosingScope2c_MustFailCompile._RTT;return null;
                                                                          }
                                                                          }.apply(((ClosureEnclosingScope2c_MustFailCompile)
                                                                                    ClosureEnclosingScope2c_MustFailCompile.
                                                                                      C$48.this.
                                                                                      out$)).
                                                                            a))) + (((int)(ClosureEnclosingScope2c_MustFailCompile.
                                                                                             C$48.this.
                                                                                             b))))))) + (((int)(c))));
                                 }}
                                 public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                 }
                                 })).apply();
        }
        
        public C$48(final ClosureEnclosingScope2c_MustFailCompile out$,
                    final int b) {
            super();
            
//#line 23
this.out$ = out$;
            
//#line 29
this.b = b;
            
//#line 32
this.c = 1;
        }
    
    }
    

}
