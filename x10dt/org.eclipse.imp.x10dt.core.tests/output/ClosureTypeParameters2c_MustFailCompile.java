
public class ClosureTypeParameters2c_MustFailCompile
extends ClosureTest
{public static final x10.rtt.RuntimeType<ClosureTypeParameters2c_MustFailCompile>_RTT = new x10.rtt.RuntimeType<ClosureTypeParameters2c_MustFailCompile>(
/* base class */ClosureTypeParameters2c_MustFailCompile.class
, /* parents */ new x10.rtt.Type[] {ClosureTest._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 22
static class V
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<ClosureTypeParameters2c_MustFailCompile.
      V>_RTT = new x10.rtt.RuntimeType<ClosureTypeParameters2c_MustFailCompile.
      V>(
    /* base class */ClosureTypeParameters2c_MustFailCompile.
      V.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 20
final private ClosureTypeParameters2c_MustFailCompile
          out$;
        
//#line 22
final static java.lang.String
          name =
          "V";
        
        public V(final ClosureTypeParameters2c_MustFailCompile out$) {
            super();
            
//#line 20
this.out$ = out$;
        }
    
    }
    
    
//#line 23
static class W
                extends ClosureTypeParameters2c_MustFailCompile.
                  V
                {public static final x10.rtt.RuntimeType<ClosureTypeParameters2c_MustFailCompile.
      W>_RTT = new x10.rtt.RuntimeType<ClosureTypeParameters2c_MustFailCompile.
      W>(
    /* base class */ClosureTypeParameters2c_MustFailCompile.
      W.class
    , /* parents */ new x10.rtt.Type[] {ClosureTypeParameters2c_MustFailCompile.V._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 20
final private ClosureTypeParameters2c_MustFailCompile
          out$;
        
//#line 23
final static java.lang.String
          name =
          "W";
        
        public W(final ClosureTypeParameters2c_MustFailCompile out$) {
            super(out$);
            
//#line 20
this.out$ = out$;
        }
    
    }
    
    
//#line 24
static class X
                extends ClosureTypeParameters2c_MustFailCompile.
                  V
                {public static final x10.rtt.RuntimeType<ClosureTypeParameters2c_MustFailCompile.
      X>_RTT = new x10.rtt.RuntimeType<ClosureTypeParameters2c_MustFailCompile.
      X>(
    /* base class */ClosureTypeParameters2c_MustFailCompile.
      X.class
    , /* parents */ new x10.rtt.Type[] {ClosureTypeParameters2c_MustFailCompile.V._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 20
final private ClosureTypeParameters2c_MustFailCompile
          out$;
        
//#line 24
final static java.lang.String
          name =
          "X";
        
        public X(final ClosureTypeParameters2c_MustFailCompile out$) {
            super(out$);
            
//#line 20
this.out$ = out$;
        }
    
    }
    
    
//#line 25
static class Y
                extends ClosureTypeParameters2c_MustFailCompile.
                  X
                {public static final x10.rtt.RuntimeType<ClosureTypeParameters2c_MustFailCompile.
      Y>_RTT = new x10.rtt.RuntimeType<ClosureTypeParameters2c_MustFailCompile.
      Y>(
    /* base class */ClosureTypeParameters2c_MustFailCompile.
      Y.class
    , /* parents */ new x10.rtt.Type[] {ClosureTypeParameters2c_MustFailCompile.X._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 20
final private ClosureTypeParameters2c_MustFailCompile
          out$;
        
//#line 25
final static java.lang.String
          name =
          "Y";
        
        public Y(final ClosureTypeParameters2c_MustFailCompile out$) {
            super(out$);
            
//#line 20
this.out$ = out$;
        }
    
    }
    
    
//#line 26
static class Z
                extends ClosureTypeParameters2c_MustFailCompile.
                  X
                {public static final x10.rtt.RuntimeType<ClosureTypeParameters2c_MustFailCompile.
      Z>_RTT = new x10.rtt.RuntimeType<ClosureTypeParameters2c_MustFailCompile.
      Z>(
    /* base class */ClosureTypeParameters2c_MustFailCompile.
      Z.class
    , /* parents */ new x10.rtt.Type[] {ClosureTypeParameters2c_MustFailCompile.X._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 20
final private ClosureTypeParameters2c_MustFailCompile
          out$;
        
//#line 26
final static java.lang.String
          name =
          "Z";
        
        public Z(final ClosureTypeParameters2c_MustFailCompile out$) {
            super(out$);
            
//#line 20
this.out$ = out$;
        }
    
    }
    
    
    
//#line 28
public boolean
                  run(
                  ){
        
//#line 31
this.check("new C[Z]().f()",
                               new ClosureTypeParameters2c_MustFailCompile.
                                 C$76<ClosureTypeParameters2c_MustFailCompile.
                                 Z>(ClosureTypeParameters2c_MustFailCompile.Z._RTT,
                                    this).
                                 f.apply$G(),
                               "hi");
        
//#line 33
return result;
    }
    
    
//#line 36
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
    							ClosureTypeParameters2c_MustFailCompile.main(args);
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
        
//#line 37
new ClosureTypeParameters2c_MustFailCompile().execute();
    }/* } */
    
    public ClosureTypeParameters2c_MustFailCompile() {
        super();
    }
    
    
//#line 30
private static class C$76<T>
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<ClosureTypeParameters2c_MustFailCompile.
      C$76>_RTT = new x10.rtt.RuntimeType<ClosureTypeParameters2c_MustFailCompile.
      C$76>(
    /* base class */ClosureTypeParameters2c_MustFailCompile.
      C$76.class, 
    /* variances */ new x10.rtt.RuntimeType.Variance[] {x10.rtt.RuntimeType.Variance.INVARIANT}
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    public x10.rtt.Type getParam(int i) {if (i ==0)return T;return null;}
    
        private final x10.rtt.Type T;
        
        
//#line 20
final private ClosureTypeParameters2c_MustFailCompile
          out$;
        
//#line 30
final x10.core.fun.Fun_0_0<java.lang.String>
          f;
        
        public C$76(final x10.rtt.Type T,
                    final ClosureTypeParameters2c_MustFailCompile out$) {
                                                                                 super();
                                                                             this.T = T;
                                                                              {
                                                                                 
//#line 20
this.out$ = out$;
                                                                                 
//#line 30
this.f = ((x10.core.fun.Fun_0_0)(new x10.core.fun.Fun_0_0<java.lang.String>() {public final java.lang.String apply$G() { return apply();}
                                                                                 public final java.lang.String apply() { {
                                                                                     
//#line 30
return "hi";
                                                                                 }}
                                                                                 public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.runtimeType(java.lang.String.class);return null;
                                                                                 }
                                                                                 }));
                                                                             }}
    
    }
    

}
