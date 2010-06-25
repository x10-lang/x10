
public class ClosureTypeParameters1i
extends ClosureTest
{public static final x10.rtt.RuntimeType<ClosureTypeParameters1i>_RTT = new x10.rtt.RuntimeType<ClosureTypeParameters1i>(
/* base class */ClosureTypeParameters1i.class
, /* parents */ new x10.rtt.Type[] {ClosureTest._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 32
public boolean
                  run(
                  ){
        
//#line 35
this.check("newX[int,int]().f(1,1)",
                               new ClosureTypeParameters1i.
                                 X$68<java.lang.Integer, java.lang.Integer>(x10.rtt.Types.INT,
                                                                            x10.rtt.Types.INT,
                                                                            this).
                                 f.apply$G(1,
                                           1),
                               "11");
        
//#line 37
return result;
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
    							ClosureTypeParameters1i.main(args);
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
new ClosureTypeParameters1i().execute();
    }/* } */
    
    public ClosureTypeParameters1i() {
        super();
    }
    
    
//#line 34
private static class X$68<T, U>
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<ClosureTypeParameters1i.
      X$68>_RTT = new x10.rtt.RuntimeType<ClosureTypeParameters1i.
      X$68>(
    /* base class */ClosureTypeParameters1i.
      X$68.class, 
    /* variances */ new x10.rtt.RuntimeType.Variance[] {x10.rtt.RuntimeType.Variance.INVARIANT, 
    x10.rtt.RuntimeType.Variance.INVARIANT}
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    public x10.rtt.Type getParam(int i) {if (i ==0)return T;if (i ==1)return U;return null;}
    
        private final x10.rtt.Type T;
        private final x10.rtt.Type U;
        
        
//#line 30
final private ClosureTypeParameters1i
          out$;
        
//#line 34
final x10.core.fun.Fun_0_2<T,U,java.lang.String>
          f;
        
        public X$68(final x10.rtt.Type T,
                    final x10.rtt.Type U,
                    final ClosureTypeParameters1i out$) {
                                                                 super();
                                                             this.T = T;
                                                             this.U = U;
                                                              {
                                                                 
//#line 30
this.out$ = out$;
                                                                 
//#line 34
this.f = ((x10.core.fun.Fun_0_2)(new x10.core.fun.Fun_0_2<T, U, java.lang.String>() {public final java.lang.String apply$G(final T x,final U y) { return apply(x,y);}
                                                                 public final java.lang.String apply(final T x, final U y) { {
                                                                     
//#line 34
return ((((Object)((java.lang.Object)(x))).toString()) + (((Object)((java.lang.Object)(y))).toString()));
                                                                 }}
                                                                 public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return T;if (i ==1) return U;if (i ==2) return x10.rtt.Types.runtimeType(java.lang.String.class);return null;
                                                                 }
                                                                 }));
                                                             }}
    
    }
    

}
