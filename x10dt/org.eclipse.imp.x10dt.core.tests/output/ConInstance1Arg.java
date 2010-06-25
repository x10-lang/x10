
public class ConInstance1Arg
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ConInstance1Arg>_RTT = new x10.rtt.RuntimeType<ConInstance1Arg>(
/* base class */ConInstance1Arg.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 19
static class A
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<ConInstance1Arg.
      A>_RTT = new x10.rtt.RuntimeType<ConInstance1Arg.
      A>(
    /* base class */ConInstance1Arg.
      A.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 19
final public int
          i;
        
        
//#line 18
final private ConInstance1Arg
          out$;
        
        final public int
          i(
          ){
            return this.
                     i;
        }
        
        public A(final ConInstance1Arg out$,
                 final int i) {
            super();
            
//#line 18
this.out$ = out$;
            this.i = i;
        }
    
    }
    
    
    
//#line 21
void
                  m(
                  final ConInstance1Arg.
                    A id$35717){
        
    }
    
    
//#line 23
void
                  n(
                  final int i){
        
//#line 24
final ConInstance1Arg.
          A a =
          ((ConInstance1Arg.
          A)(new ConInstance1Arg.
          A(this,
            i)));
        
//#line 26
this.m(new x10.core.fun.Fun_0_1<ConInstance1Arg.
                             A, ConInstance1Arg.
                             A>() {public final ConInstance1Arg.
                             A apply$G(final ConInstance1Arg.
                             A __desugarer__var__368__) { return apply(__desugarer__var__368__);}
                           public final ConInstance1Arg.
                             A apply(final ConInstance1Arg.
                             A __desugarer__var__368__) { {
                               
//#line 26
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__368__,null))/* } */ &&
                                                 !(((int) __desugarer__var__368__.
                                                            i) ==
                                                   ((int) 2))) {
                                   
//#line 26
throw new java.lang.ClassCastException("ConInstance1Arg.A{self.i==2}");
                               }
                               
//#line 26
return __desugarer__var__368__;
                           }}
                           public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return ConInstance1Arg.A._RTT;if (i ==1) return ConInstance1Arg.A._RTT;return null;
                           }
                           }.apply(((ConInstance1Arg.
                                     A)
                                     a)));
    }
    
    
//#line 29
public boolean
                  run(
                  ){
        
//#line 30
try {{
            
//#line 31
this.n((int)(3));
            
//#line 32
return false;
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.ClassCastException) {
        final java.lang.ClassCastException id$35719 = (java.lang.ClassCastException) __$generated_wrappedex$__.getCause();
        {
            
//#line 34
return true;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (final java.lang.ClassCastException id$35719) {
            
//#line 34
return true;
        }
    }
    
    
//#line 38
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
    							ConInstance1Arg.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$35720)  {
        
//#line 39
new ConInstance1Arg().execute();
    }/* } */
    
    public ConInstance1Arg() {
        super();
    }

}
