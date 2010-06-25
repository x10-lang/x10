
public class AsyncTest1
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<AsyncTest1>_RTT = new x10.rtt.RuntimeType<AsyncTest1>(
/* base class */AsyncTest1.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 23
boolean
      flag;
    
    
//#line 24
public boolean
                  run(
                  ){
        
//#line 25
x10.
          lang.
          Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                               
//#line 25
x10.
                                 lang.
                                 Runtime.runAt(x10.lang.Place.place(x10.core.Ref.home(AsyncTest1.this)),
                                               new x10.core.fun.VoidFun_0_0() {public final void apply() { {
                                                   
//#line 25
try {{
                                                       
//#line 25
x10.
                                                         lang.
                                                         Runtime.lock();
                                                       {
                                                           
//#line 25
AsyncTest1.this.flag = true;
                                                       }
                                                   }}finally {{
                                                         
//#line 25
x10.
                                                           lang.
                                                           Runtime.release();
                                                     }}
                                                   }}
                                                   });
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
        
//#line 26
boolean b;
        
//#line 27
do  {
            
//#line 28
try {{
                
//#line 28
x10.
                  lang.
                  Runtime.lock();
                {
                    
//#line 28
b = flag;
                }
            }}finally {{
                  
//#line 28
x10.
                    lang.
                    Runtime.release();
              }}
            }while((!(((boolean)(b))))); 
        
//#line 30
return b;
        }
        
        
//#line 33
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
        							AsyncTest1.main(args);
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
            
//#line 34
new AsyncTest1().execute();
        }/* } */
        
        public AsyncTest1() {
            super();
            
//#line 23
this.flag = false;
        }
    
    }
    