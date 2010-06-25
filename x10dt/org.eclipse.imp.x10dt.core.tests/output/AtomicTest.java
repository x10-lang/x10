
public class AtomicTest
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<AtomicTest>_RTT = new x10.rtt.RuntimeType<AtomicTest>(
/* base class */AtomicTest.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 20
long
      val;
    
//#line 21
final static long
      N =
      ((long)(((int)(1000))));
    
//#line 22
long
      startCount;
    
//#line 23
long
      endCount;
    
    
//#line 25
public boolean
                  run(
                  ){
        
//#line 26
boolean b;
        
//#line 27
x10.
          lang.
          Runtime.runAsync(x10.lang.Place.place(x10.core.Ref.home(this)),
                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                               
//#line 28
try {{
                                   
//#line 28
x10.
                                     lang.
                                     Runtime.lock();
                                   {
                                       
//#line 29
AtomicTest.this.startCount = val;
                                       
//#line 30
for (
//#line 30
int i =
                                                          0;
                                                        ((((long)(((long)(((int)(i))))))) < (((long)(AtomicTest.N))));
                                                        
//#line 30
i += 1) {
                                           
//#line 30
AtomicTest.this.val += 1L;
                                       }
                                       
//#line 31
AtomicTest.this.endCount = val;
                                   }
                               }}finally {{
                                     
//#line 28
x10.
                                       lang.
                                       Runtime.release();
                                 }}
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
        
//#line 34
for (
//#line 34
long i =
                           ((long)(((int)(0))));
                         ((((long)(i))) < (((long)(((((long)(AtomicTest.N))) * (((long)(((long)(((int)(100))))))))))));
                         
//#line 34
i += 1L) {
            
//#line 35
try {{
                
//#line 35
x10.
                  lang.
                  Runtime.lock();
                {
                    
//#line 35
this.val = i;
                    
//#line 35
b = (((long) endCount) !=
                                     ((long) ((long)(((int)(0))))));
                }
            }}finally {{
                  
//#line 35
x10.
                    lang.
                    Runtime.release();
              }}
            
//#line 36
if (b) {
                
//#line 36
break;
            }
            }
        
//#line 39
try {{
            
//#line 39
x10.
              lang.
              Runtime.lock();
            {
                
//#line 39
b = (((long) ((((long)(startCount))) + (((long)(AtomicTest.N))))) ==
                                 ((long) endCount));
            }
        }}finally {{
              
//#line 39
x10.
                lang.
                Runtime.release();
          }}
        
//#line 40
return b;
        }
        
        
//#line 43
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
        							AtomicTest.main(args);
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
        public static void main(final x10.core.Rail<java.lang.String> id$33538)  {
            
//#line 44
new AtomicTest().execute();
        }/* } */
        
        public AtomicTest() {
            super();
            
//#line 20
this.val = ((long)(((int)(0))));
            
//#line 22
this.startCount = ((long)(((int)(0))));
            
//#line 23
this.endCount = AtomicTest.N;
        }
        
        }
        