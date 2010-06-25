
public class ConConstructor1Arg
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ConConstructor1Arg>_RTT = new x10.rtt.RuntimeType<ConConstructor1Arg>(
/* base class */ConConstructor1Arg.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 19
static class A
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<ConConstructor1Arg.
      A>_RTT = new x10.rtt.RuntimeType<ConConstructor1Arg.
      A>(
    /* base class */ConConstructor1Arg.
      A.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 19
final public int
          i;
        
        
        final public int
          i(
          ){
            return this.
                     i;
        }
        
        public A(final int i) {
            super();
            this.i = i;
        }
    
    }
    
    
    
//#line 20
ConConstructor1Arg() {
        
//#line 20
super();
    }
    
    
//#line 21
ConConstructor1Arg(final ConConstructor1Arg.
                                     A id$35466) {
        
//#line 21
super();
    }
    
    
//#line 22
ConConstructor1Arg(final int i) {
        
//#line 24
this(new x10.core.fun.Fun_0_1<ConConstructor1Arg.
                           A, ConConstructor1Arg.
                           A>() {public final ConConstructor1Arg.
                           A apply$G(final ConConstructor1Arg.
                           A __desugarer__var__367__) { return apply(__desugarer__var__367__);}
                         public final ConConstructor1Arg.
                           A apply(final ConConstructor1Arg.
                           A __desugarer__var__367__) { {
                             
//#line 24
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__367__,null))/* } */ &&
                                               !(((int) __desugarer__var__367__.
                                                          i) ==
                                                 ((int) 2))) {
                                 
//#line 24
throw new java.lang.ClassCastException("ConConstructor1Arg.A{self.i==2}");
                             }
                             
//#line 24
return __desugarer__var__367__;
                         }}
                         public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return ConConstructor1Arg.A._RTT;if (i ==1) return ConConstructor1Arg.A._RTT;return null;
                         }
                         }.apply(((ConConstructor1Arg.
                                   A)
                                   new ConConstructor1Arg.
                                   A(i))));
    }
    
    
//#line 27
public boolean
                  run(
                  ){
        
//#line 28
try {{
            
//#line 29
final ConConstructor1Arg x =
              ((ConConstructor1Arg)(new ConConstructor1Arg(3)));
            
//#line 30
return false;
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.ClassCastException) {
        final java.lang.ClassCastException id$35468 = (java.lang.ClassCastException) __$generated_wrappedex$__.getCause();
        {
            
//#line 32
return true;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (final java.lang.ClassCastException id$35468) {
            
//#line 32
return true;
        }
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
    							ConConstructor1Arg.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$35469)  {
        
//#line 37
new ConConstructor1Arg().execute();
    }/* } */

}
