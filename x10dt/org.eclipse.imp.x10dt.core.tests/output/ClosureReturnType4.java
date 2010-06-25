
public class ClosureReturnType4
extends ClosureTest
{public static final x10.rtt.RuntimeType<ClosureReturnType4>_RTT = new x10.rtt.RuntimeType<ClosureReturnType4>(
/* base class */ClosureReturnType4.class
, /* parents */ new x10.rtt.Type[] {ClosureTest._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 25
static class A
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<ClosureReturnType4.
      A>_RTT = new x10.rtt.RuntimeType<ClosureReturnType4.
      A>(
    /* base class */ClosureReturnType4.
      A.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 23
final private ClosureReturnType4
          out$;
        
        
//#line 26
java.lang.String
                      name(
                      ){
            
//#line 26
return "A";
        }
        
        public A(final ClosureReturnType4 out$) {
            super();
            
//#line 23
this.out$ = out$;
        }
    
    }
    
    
//#line 29
static class B
                extends ClosureReturnType4.
                  A
                {public static final x10.rtt.RuntimeType<ClosureReturnType4.
      B>_RTT = new x10.rtt.RuntimeType<ClosureReturnType4.
      B>(
    /* base class */ClosureReturnType4.
      B.class
    , /* parents */ new x10.rtt.Type[] {ClosureReturnType4.A._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 23
final private ClosureReturnType4
          out$;
        
        
//#line 30
java.lang.String
                      name(
                      ){
            
//#line 30
return "B";
        }
        
        public B(final ClosureReturnType4 out$) {
            super(out$);
            
//#line 23
this.out$ = out$;
        }
    
    }
    
    
//#line 33
static class C
                extends ClosureReturnType4.
                  A
                {public static final x10.rtt.RuntimeType<ClosureReturnType4.
      C>_RTT = new x10.rtt.RuntimeType<ClosureReturnType4.
      C>(
    /* base class */ClosureReturnType4.
      C.class
    , /* parents */ new x10.rtt.Type[] {ClosureReturnType4.A._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 23
final private ClosureReturnType4
          out$;
        
        
//#line 34
java.lang.String
                      name(
                      ){
            
//#line 34
return "C";
        }
        
        public C(final ClosureReturnType4 out$) {
            super(out$);
            
//#line 23
this.out$ = out$;
        }
    
    }
    
    
    
//#line 37
public boolean
                  run(
                  ){
        
//#line 39
final x10.core.fun.Fun_0_1<java.lang.Integer,ClosureReturnType4.
          A> f =
          ((x10.core.fun.Fun_0_1)(new x10.core.fun.Fun_0_1<java.lang.Integer, ClosureReturnType4.
          A>() {public final ClosureReturnType4.
          A apply$G(final java.lang.Integer x) { return apply((int)x);}
        public final ClosureReturnType4.
          A apply(final int x) { {
            
//#line 39
if (((int) x) ==
                            ((int) 0)) {
                
//#line 39
return new ClosureReturnType4.
                  B(ClosureReturnType4.this);
            }
            
//#line 39
return new ClosureReturnType4.
              C(ClosureReturnType4.this);
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;if (i ==1) return ClosureReturnType4.A._RTT;return null;
        }
        }));
        
//#line 40
final ClosureReturnType4.
          A a0 =
          ((ClosureReturnType4.
          A)(f.apply$G(0)));
        
//#line 41
final ClosureReturnType4.
          A a1 =
          ((ClosureReturnType4.
          A)(f.apply$G(1)));
        
//#line 42
this.check("a0.name()",
                               a0.name(),
                               "B");
        
//#line 43
this.check("a1.name()",
                               a1.name(),
                               "C");
        
//#line 45
return result;
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
    							ClosureReturnType4.main(args);
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
new ClosureReturnType4().execute();
    }/* } */
    
    public ClosureReturnType4() {
        super();
    }

}
