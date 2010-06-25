
public class AtomicMethodTest
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<AtomicMethodTest>_RTT = new x10.rtt.RuntimeType<AtomicMethodTest>(
/* base class */AtomicMethodTest.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 19
long
      val;
    
//#line 20
final public static int
      N =
      1000;
    
//#line 21
long
      startCount;
    
//#line 22
long
      endCount;
    
    
//#line 23
void
                  body(
                  ){
        
//#line 23
try {{
            
//#line 23
x10.
              lang.
              Runtime.lock();
            {
                
//#line 24
this.startCount = this.
                                                val;
                
//#line 25
for (
//#line 25
int i =
                                   0;
                                 ((((int)(i))) < (((int)(AtomicMethodTest.N))));
                                 
//#line 25
i += 1) {
                    
//#line 25
this.val += 1L;
                }
                
//#line 26
this.endCount = this.
                                              val;
            }
        }}finally {{
              
//#line 23
x10.
                lang.
                Runtime.release();
          }}
        }
    
    
//#line 29
public boolean
                  run(
                  ){
        
//#line 30
x10.
          lang.
          Runtime.runAsync(x10.lang.Place.place(x10.core.Ref.home(this)),
                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                               
//#line 30
AtomicMethodTest.this.body();
                           }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                           });
        
//#line 31
for (
//#line 31
long i =
                           ((long)(((int)(0))));
                         ((((long)(i))) < (((long)(((long)(((int)(((((int)(AtomicMethodTest.N))) * (((int)(AtomicMethodTest.N))))))))))));
                         
//#line 31
i += 1L) {
            
//#line 32
boolean b;
            
//#line 33
try {{
                
//#line 33
x10.
                  lang.
                  Runtime.lock();
                {
                    
//#line 33
this.val = i;
                    
//#line 33
b = (((long) endCount) !=
                                     ((long) ((long)(((int)(0))))));
                }
            }}finally {{
                  
//#line 33
x10.
                    lang.
                    Runtime.release();
              }}
            
//#line 34
if (b) {
                
//#line 34
break;
            }
            }
        
//#line 37
boolean b;
        
//#line 38
try {{
            
//#line 38
x10.
              lang.
              Runtime.lock();
            {
                
//#line 38
b = (((long) ((((long)(startCount))) + (((long)(((long)(((int)(AtomicMethodTest.N))))))))) ==
                                 ((long) endCount));
            }
        }}finally {{
              
//#line 38
x10.
                lang.
                Runtime.release();
          }}
        
//#line 39
return b;
        }
        
        
//#line 42
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
        							AtomicMethodTest.main(args);
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
        public static void main(final x10.core.Rail<java.lang.String> id$32926)  {
            
//#line 43
new AtomicMethodTest().execute();
        }/* } */
        
        public AtomicMethodTest() {
            super();
            
//#line 19
this.val = ((long)(((int)(0))));
            
//#line 21
this.startCount = ((long)(((int)(0))));
            
//#line 22
this.endCount = ((long)(((int)(AtomicMethodTest.N))));
        }
    
    }
    