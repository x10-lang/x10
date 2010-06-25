
public class ConStructInstance2Arg_2
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ConStructInstance2Arg_2>_RTT = new x10.rtt.RuntimeType<ConStructInstance2Arg_2>(
/* base class */ConStructInstance2Arg_2.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 20
static class A
                extends x10.core.Struct
                {public static final x10.rtt.RuntimeType<ConStructInstance2Arg_2.
      A>_RTT = new x10.rtt.RuntimeType<ConStructInstance2Arg_2.
      A>(
    /* base class */ConStructInstance2Arg_2.
      A.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class), x10.rtt.Types.runtimeType(x10.core.Struct.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 20
final public int
          i;
        
        
        
//#line 22
final void
                      m(
                      final ConStructInstance2Arg_2.
                        A q,
                      final int i){
            
        }
        
        
//#line 24
final void
                      n(
                      final int i){
            
//#line 25
final ConStructInstance2Arg_2.
              A a =
              new ConStructInstance2Arg_2.
              A(i);
            
//#line 27
this.m(new x10.core.fun.Fun_0_1<ConStructInstance2Arg_2.
                                 A, ConStructInstance2Arg_2.
                                 A>() {public final ConStructInstance2Arg_2.
                                 A apply$G(final ConStructInstance2Arg_2.
                                 A __desugarer__var__380__) { return apply(__desugarer__var__380__);}
                               public final ConStructInstance2Arg_2.
                                 A apply(final ConStructInstance2Arg_2.
                                 A __desugarer__var__380__) { {
                                   
//#line 27
if (!(((int) __desugarer__var__380__.
                                                              i) ==
                                                     ((int) 2))) {
                                       
//#line 27
throw new java.lang.ClassCastException("ConStructInstance2Arg_2.A{self.i==2}");
                                   }
                                   
//#line 27
return __desugarer__var__380__;
                               }}
                               public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return ConStructInstance2Arg_2.A._RTT;if (i ==1) return ConStructInstance2Arg_2.A._RTT;return null;
                               }
                               }.apply(((ConStructInstance2Arg_2.
                                         A)
                                         a)),
                               (int)(new x10.core.fun.Fun_0_1<java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final java.lang.Integer __desugarer__var__381__) { return apply((int)__desugarer__var__381__);}
                               public final int apply(final int __desugarer__var__381__) { {
                                   
//#line 27
if (!(((int) __desugarer__var__381__) ==
                                                     ((int) a.
                                                              i))) {
                                       
//#line 27
throw new java.lang.ClassCastException("x10.lang.Int{self==a.i}");
                                   }
                                   
//#line 27
return __desugarer__var__381__;
                               }}
                               public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;if (i ==1) return x10.rtt.Types.INT;return null;
                               }
                               }.apply(((int) (int) (java.lang.Integer)
                                         ((((int)(i))) + (((int)(1))))))));
        }
        
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
    
    final public boolean structEquals(final java.lang.Object o) {
        if (!(o instanceof ConStructInstance2Arg_2.A)) return false;
        if (!x10.rtt.Equality.equalsequals(this.i, ((ConStructInstance2Arg_2.A) o).i)) return false;
        return true;
        }
    
    }
    
    
    
//#line 30
public boolean
                  run(
                  ){
        
//#line 31
try {{
            
//#line 32
new ConStructInstance2Arg_2.
              A(1).n((int)(2));
            
//#line 33
return false;
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.ClassCastException) {
        final java.lang.ClassCastException id$36660 = (java.lang.ClassCastException) __$generated_wrappedex$__.getCause();
        {
            
//#line 35
return true;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (final java.lang.ClassCastException id$36660) {
            
//#line 35
return true;
        }
    }
    
    
//#line 39
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
    							ConStructInstance2Arg_2.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$36661)  {
        
//#line 40
new ConStructInstance2Arg_2().execute();
    }/* } */
    
    public ConStructInstance2Arg_2() {
        super();
    }

}
