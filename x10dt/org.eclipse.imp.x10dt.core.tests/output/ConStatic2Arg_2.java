
public class ConStatic2Arg_2
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ConStatic2Arg_2>_RTT = new x10.rtt.RuntimeType<ConStatic2Arg_2>(
/* base class */ConStatic2Arg_2.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 19
static class A
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<ConStatic2Arg_2.
      A>_RTT = new x10.rtt.RuntimeType<ConStatic2Arg_2.
      A>(
    /* base class */ConStatic2Arg_2.
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
    
    
    
//#line 21
static void
                  m(
                  final ConStatic2Arg_2.
                    A q,
                  final int i){
        
    }
    
    
//#line 23
static void
                  n(
                  final int i){
        
//#line 24
final ConStatic2Arg_2.
          A a =
          ((ConStatic2Arg_2.
          A)(new ConStatic2Arg_2.
          A(i)));
        
//#line 26
ConStatic2Arg_2.m(new x10.core.fun.Fun_0_1<ConStatic2Arg_2.
                                        A, ConStatic2Arg_2.
                                        A>() {public final ConStatic2Arg_2.
                                        A apply$G(final ConStatic2Arg_2.
                                        A __desugarer__var__375__) { return apply(__desugarer__var__375__);}
                                      public final ConStatic2Arg_2.
                                        A apply(final ConStatic2Arg_2.
                                        A __desugarer__var__375__) { {
                                          
//#line 26
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__375__,null))/* } */ &&
                                                            !(((int) __desugarer__var__375__.
                                                                       i) ==
                                                              ((int) 2))) {
                                              
//#line 26
throw new java.lang.ClassCastException("ConStatic2Arg_2.A{self.i==2}");
                                          }
                                          
//#line 26
return __desugarer__var__375__;
                                      }}
                                      public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return ConStatic2Arg_2.A._RTT;if (i ==1) return ConStatic2Arg_2.A._RTT;return null;
                                      }
                                      }.apply(((ConStatic2Arg_2.
                                                A)
                                                a)),
                                      (int)(new x10.core.fun.Fun_0_1<java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final java.lang.Integer __desugarer__var__376__) { return apply((int)__desugarer__var__376__);}
                                      public final int apply(final int __desugarer__var__376__) { {
                                          
//#line 26
if (!(((int) __desugarer__var__376__) ==
                                                            ((int) a.
                                                                     i))) {
                                              
//#line 26
throw new java.lang.ClassCastException("x10.lang.Int{self==a.i}");
                                          }
                                          
//#line 26
return __desugarer__var__376__;
                                      }}
                                      public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;if (i ==1) return x10.rtt.Types.INT;return null;
                                      }
                                      }.apply(((int) (int) (java.lang.Integer)
                                                ((((int)(i))) + (((int)(1))))))));
    }
    
    
//#line 29
public boolean
                  run(
                  ){
        
//#line 30
try {{
            
//#line 31
ConStatic2Arg_2.n((int)(2));
            
//#line 32
return false;
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.ClassCastException) {
        final java.lang.ClassCastException id$36343 = (java.lang.ClassCastException) __$generated_wrappedex$__.getCause();
        {
            
//#line 34
return true;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (final java.lang.ClassCastException id$36343) {
            
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
    							ConStatic2Arg_2.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$36344)  {
        
//#line 39
new ConStatic2Arg_2().execute();
    }/* } */
    
    public ConStatic2Arg_2() {
        super();
    }

}
