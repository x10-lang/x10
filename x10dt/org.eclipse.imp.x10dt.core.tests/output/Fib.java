public class Fib
extends x10.core.Ref
{public static final x10.rtt.RuntimeType<Fib>_RTT = new x10.rtt.RuntimeType<Fib>(
/* base class */Fib.class
, /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 2
int
      n;
    
    
//#line 3
Fib(final int n) {
        
//#line 3
super();
        
//#line 2
this.n = 0;
        
//#line 4
this.n = n;
    }
    
    
//#line 6
void
                 fib(
                 ){
        
//#line 7
if (((((int)(n))) <= (((int)(2))))) {
            
//#line 8
this.n = 1;
            
//#line 9
return;
        }
        
//#line 11
final Fib f1 =
          ((Fib)(new Fib(((((int)(n))) - (((int)(1)))))));
        
//#line 12
final Fib f2 =
          ((Fib)(new Fib(((((int)(n))) - (((int)(2)))))));
        
//#line 13
try {{
            
//#line 13
x10.
              lang.
              Runtime.startFinish();
            {
                
//#line 14
x10.
                  lang.
                  Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                       
//#line 14
f1.fib();
                                   }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                   });
                
//#line 15
f2.fib();
            }
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
        java.lang.Throwable __desugarer__var__11__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 13
x10.
              lang.
              Runtime.pushException(__desugarer__var__11__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__11__) {
            
//#line 13
x10.
              lang.
              Runtime.pushException(__desugarer__var__11__);
        }finally {{
             
//#line 13
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 17
this.n = ((((int)(f1.
                                        n))) + (((int)(f2.
                                                         n))));
        }
    
    
//#line 20
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
    							Fib.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> args)  {
        
//#line 21
if (((((int)(args.
                                   length))) < (((int)(1))))) {
            
//#line 22
x10.
              io.
              Console.OUT.println("Usage: Fib <n>");
            
//#line 23
return;
        }
        
//#line 26
final int n =
          java.lang.Integer.parseInt(((java.lang.String)((Object[])args.value)[0]));
        
//#line 27
final Fib f =
          ((Fib)(new Fib(n)));
        
//#line 28
f.fib();
        
//#line 29
x10.
          io.
          Console.OUT.println((((((("fib(") + (n))) + (")= "))) + (f.
                                                                     n)));
    }/* } */
    
    }
    