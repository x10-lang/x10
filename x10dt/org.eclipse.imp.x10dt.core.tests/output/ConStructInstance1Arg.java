
public class ConStructInstance1Arg
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ConStructInstance1Arg>_RTT = new x10.rtt.RuntimeType<ConStructInstance1Arg>(
/* base class */ConStructInstance1Arg.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 19
static class A
                extends x10.core.Struct
                {public static final x10.rtt.RuntimeType<ConStructInstance1Arg.
      A>_RTT = new x10.rtt.RuntimeType<ConStructInstance1Arg.
      A>(
    /* base class */ConStructInstance1Arg.
      A.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class), x10.rtt.Types.runtimeType(x10.core.Struct.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 19
final public int
          i;
        
        
        
//#line 21
final void
                      m(
                      final ConStructInstance1Arg.
                        A id$36448){
            
        }
        
        
//#line 23
final void
                      n(
                      final int i){
            
//#line 24
final ConStructInstance1Arg.
              A a =
              new ConStructInstance1Arg.
              A(i);
            
//#line 26
this.m(new x10.core.fun.Fun_0_1<ConStructInstance1Arg.
                                 A, ConStructInstance1Arg.
                                 A>() {public final ConStructInstance1Arg.
                                 A apply$G(final ConStructInstance1Arg.
                                 A __desugarer__var__377__) { return apply(__desugarer__var__377__);}
                               public final ConStructInstance1Arg.
                                 A apply(final ConStructInstance1Arg.
                                 A __desugarer__var__377__) { {
                                   
//#line 26
if (!(((int) __desugarer__var__377__.
                                                              i) ==
                                                     ((int) 2))) {
                                       
//#line 26
throw new java.lang.ClassCastException("ConStructInstance1Arg.A{self.i==2}");
                                   }
                                   
//#line 26
return __desugarer__var__377__;
                               }}
                               public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return ConStructInstance1Arg.A._RTT;if (i ==1) return ConStructInstance1Arg.A._RTT;return null;
                               }
                               }.apply(((ConStructInstance1Arg.
                                         A)
                                         a)));
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
        if (!(o instanceof ConStructInstance1Arg.A)) return false;
        if (!x10.rtt.Equality.equalsequals(this.i, ((ConStructInstance1Arg.A) o).i)) return false;
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
new ConStructInstance1Arg.
              A(1).n((int)(3));
            
//#line 33
return false;
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.ClassCastException) {
        final java.lang.ClassCastException id$36450 = (java.lang.ClassCastException) __$generated_wrappedex$__.getCause();
        {
            
//#line 35
return true;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (final java.lang.ClassCastException id$36450) {
            
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
    							ConStructInstance1Arg.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$36451)  {
        
//#line 40
new ConStructInstance1Arg().execute();
    }/* } */
    
    public ConStructInstance1Arg() {
        super();
    }

}
