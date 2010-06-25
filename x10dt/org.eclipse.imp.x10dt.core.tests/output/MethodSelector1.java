
public class MethodSelector1
extends ClosureTest
{public static final x10.rtt.RuntimeType<MethodSelector1>_RTT = new x10.rtt.RuntimeType<MethodSelector1>(
/* base class */MethodSelector1.class
, /* parents */ new x10.rtt.Type[] {ClosureTest._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 34
int
                  foo(
                  ){
        
//#line 34
return 1;
    }
    
    
//#line 35
int
                  foo(
                  final int i){
        
//#line 35
return i;
    }
    
    
//#line 41
public boolean
                  run(
                  ){
        
//#line 43
final x10.core.fun.Fun_0_0<java.lang.Integer> f1 =
          ((x10.core.fun.Fun_0_0)(new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
        public final int apply() { {
            
//#line 43
return MethodSelector1.this.foo();
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
        }
        }));
        
//#line 44
final x10.core.fun.Fun_0_1<java.lang.Integer,java.lang.Integer> f2 =
          ((x10.core.fun.Fun_0_1)(new x10.core.fun.Fun_0_1<java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final java.lang.Integer id$61382) { return apply((int)id$61382);}
        public final int apply(final int id$61382) { {
            
//#line 44
return MethodSelector1.this.foo((int)(id$61382));
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;if (i ==1) return x10.rtt.Types.INT;return null;
        }
        }));
        
//#line 45
final x10.core.fun.Fun_0_0<java.lang.Integer> f3 =
          ((x10.core.fun.Fun_0_0)(new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
        public final int apply() { {
            
//#line 45
return MethodSelector1.this.foo();
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
        }
        }));
        
//#line 46
final x10.core.fun.Fun_0_1<java.lang.Integer,java.lang.Integer> f4 =
          ((x10.core.fun.Fun_0_1)(new x10.core.fun.Fun_0_1<java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final java.lang.Integer id$61383) { return apply((int)id$61383);}
        public final int apply(final int id$61383) { {
            
//#line 46
return MethodSelector1.this.foo((int)(id$61383));
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;if (i ==1) return x10.rtt.Types.INT;return null;
        }
        }));
        
//#line 48
this.check("f1()",
                               (int)(java.lang.Integer)(f1.apply$G()),
                               (int)(1));
        
//#line 49
this.check("f2()",
                               (int)(java.lang.Integer)(f2.apply$G(2)),
                               (int)(2));
        
//#line 50
this.check("f3()",
                               (int)(java.lang.Integer)(f3.apply$G()),
                               (int)(1));
        
//#line 51
this.check("f4()",
                               (int)(java.lang.Integer)(f4.apply$G(2)),
                               (int)(2));
        
//#line 53
return result;
    }
    
    
//#line 56
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
    							MethodSelector1.main(args);
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
        
//#line 57
new MethodSelector1().execute();
    }/* } */
    
    public MethodSelector1() {
        super();
    }

}
