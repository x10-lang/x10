
public class ClosureEnclosingScope5
extends ClosureTest
{public static final x10.rtt.RuntimeType<ClosureEnclosingScope5>_RTT = new x10.rtt.RuntimeType<ClosureEnclosingScope5>(
/* base class */ClosureEnclosingScope5.class
, /* parents */ new x10.rtt.Type[] {ClosureTest._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 24
final int
      a =
      1;
    
//#line 26
static class C
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<ClosureEnclosingScope5.
      C>_RTT = new x10.rtt.RuntimeType<ClosureEnclosingScope5.
      C>(
    /* base class */ClosureEnclosingScope5.
      C.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 26
final public int
          a;
        
        
//#line 22
final private ClosureEnclosingScope5
          out$;
        
        
//#line 27
C(final ClosureEnclosingScope5 out$,
                      final int x) {
            
//#line 27
super();
            
//#line 22
this.out$ = out$;
            
//#line 28
this.a = x;
        }
        
        
//#line 30
static class D
                    extends x10.core.Ref
                    {public static final x10.rtt.RuntimeType<ClosureEnclosingScope5.
          C.
          D>_RTT = new x10.rtt.RuntimeType<ClosureEnclosingScope5.
          C.
          D>(
        /* base class */ClosureEnclosingScope5.
          C.
          D.class
        , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
        );
        public x10.rtt.RuntimeType getRTT() {return _RTT;}
        
        
        
            
//#line 30
final public int
              a;
            
            
//#line 26
final private ClosureEnclosingScope5.
              C
              out$;
            
            
//#line 31
D(final ClosureEnclosingScope5.
                            C out$,
                          final int x) {
                
//#line 31
super();
                
//#line 26
this.out$ = out$;
                
//#line 32
this.a = x;
            }
            
            
//#line 34
int
                          sum(
                          ){
                
//#line 35
final int a1 =
                  this.
                    out$.
                    out$.
                    a;
                
//#line 36
final int a2 =
                  this.
                    out$.
                    a;
                
//#line 37
final int a3 =
                  ClosureEnclosingScope5.
                    C.
                    D.this.
                    a;
                
//#line 38
return ((((int)(((((int)(((((int)(a1))) + (((int)(a2))))))) + (((int)(a3))))))) + (((int)(a))));
            }
            
            final public int
              a(
              ){
                return this.
                         a;
            }
        
        }
        
        
        final public int
          a(
          ){
            return this.
                     a;
        }
    
    }
    
    
    
//#line 43
public boolean
                  run(
                  ){
        
//#line 45
this.check("new C(2).new D(4).sum",
                               (int)(new ClosureEnclosingScope5.
                                 C.
                                 D(new ClosureEnclosingScope5.
                                     C(this,
                                       2),
                                   4).sum()),
                               (int)(11));
        
//#line 47
return result;
    }
    
    
//#line 50
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
    							ClosureEnclosingScope5.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$53287)  {
        
//#line 51
new ClosureEnclosingScope5().execute();
    }/* } */
    
    public ClosureEnclosingScope5() {
        super();
    }

}
