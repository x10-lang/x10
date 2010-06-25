
public class MethodSelector4
extends ClosureTest
{public static final x10.rtt.RuntimeType<MethodSelector4>_RTT = new x10.rtt.RuntimeType<MethodSelector4>(
/* base class */MethodSelector4.class
, /* parents */ new x10.rtt.Type[] {ClosureTest._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 34
int
                  foo(
                  ){
        
//#line 34
return 10;
    }
    
    
//#line 35
int
                  foo(
                  final int i){
        
//#line 35
return ((((int)(i))) * (((int)(10))));
    }
    
    
//#line 37
static class C4<T>
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<MethodSelector4.
      C4>_RTT = new x10.rtt.RuntimeType<MethodSelector4.
      C4>(
    /* base class */MethodSelector4.
      C4.class, 
    /* variances */ new x10.rtt.RuntimeType.Variance[] {x10.rtt.RuntimeType.Variance.INVARIANT}
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    public x10.rtt.Type getParam(int i) {if (i ==0)return T;return null;}
    
        private final x10.rtt.Type T;
        
        
        
//#line 38
int
                      foo(
                      ){
            
//#line 38
return 1;
        }
        
        
//#line 39
T
                      foo$G(
                      final T i){
            
//#line 39
return i;
        }
        
        public C4(final x10.rtt.Type T) {
                                                 super();
                                             this.T = T;
                                              {
                                                 
                                             }}
    
    }
    
    
    
//#line 42
public boolean
                  run(
                  ){
        
//#line 44
final MethodSelector4.
          C4<java.lang.Integer> c =
          ((MethodSelector4.
          C4)(new MethodSelector4.
          C4<java.lang.Integer>(x10.rtt.Types.INT)));
        
//#line 45
final x10.core.fun.Fun_0_0<java.lang.Integer> f1 =
          ((x10.core.fun.Fun_0_0)(new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
        public final int apply() { {
            
//#line 45
return c.foo();
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
        }
        }));
        
//#line 46
final x10.core.fun.Fun_0_1<java.lang.Integer,java.lang.Integer> f2 =
          ((x10.core.fun.Fun_0_1)(new x10.core.fun.Fun_0_1<java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final java.lang.Integer id$62171) { return apply((int)id$62171);}
        public final int apply(final int id$62171) { {
            
//#line 46
return c.foo$G((int)(id$62171));
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;if (i ==1) return x10.rtt.Types.INT;return null;
        }
        }));
        
//#line 47
this.check("f1()",
                               (int)(java.lang.Integer)(f1.apply$G()),
                               (int)(1));
        
//#line 48
this.check("f2(2)",
                               (int)(java.lang.Integer)(f2.apply$G(2)),
                               (int)(2));
        
//#line 50
return result;
    }
    
    
//#line 53
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
    							MethodSelector4.main(args);
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
        
//#line 54
new MethodSelector4().execute();
    }/* } */
    
    public MethodSelector4() {
        super();
    }

}
