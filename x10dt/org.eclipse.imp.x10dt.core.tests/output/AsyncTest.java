
public class AsyncTest
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<AsyncTest>_RTT = new x10.rtt.RuntimeType<AsyncTest>(
/* base class */AsyncTest.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 22
boolean
      flag;
    
//#line 23
final static long
      N =
      ((long)(((int)(1000000000))));
    
    
//#line 25
public boolean
                  run(
                  ){
        
//#line 26
boolean b =
          false;
        
//#line 27
x10.
          lang.
          Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                               
//#line 27
try {{
                                   
//#line 27
x10.
                                     lang.
                                     Runtime.lock();
                                   {
                                       
//#line 27
AsyncTest.this.flag = true;
                                   }
                               }}finally {{
                                     
//#line 27
x10.
                                       lang.
                                       Runtime.release();
                                 }}
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
        
//#line 28
for (
//#line 28
long i =
                           ((long)(((int)(0))));
                         ((((long)(i))) < (((long)(((((long)(AsyncTest.N))) * (((long)(((long)(((int)(100))))))))))));
                         
//#line 28
i += 1L) {
            
//#line 29
try {{
                
//#line 29
x10.
                  lang.
                  Runtime.lock();
                {
                    
//#line 29
b = flag;
                }
            }}finally {{
                  
//#line 29
x10.
                    lang.
                    Runtime.release();
              }}
            
//#line 31
if (b) {
                
//#line 31
break;
            }
            }
        
//#line 33
return b;
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
        							AsyncTest.main(args);
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
            
//#line 37
new AsyncTest().execute();
        }/* } */
        
        public AsyncTest() {
            super();
            
//#line 22
this.flag = false;
        }
    
    }
    