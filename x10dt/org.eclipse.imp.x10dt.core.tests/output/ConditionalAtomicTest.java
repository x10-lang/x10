
public class ConditionalAtomicTest
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ConditionalAtomicTest>_RTT = new x10.rtt.RuntimeType<ConditionalAtomicTest>(
/* base class */ConditionalAtomicTest.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 19
int
      value1;
    
//#line 20
int
      value2;
    
    
//#line 22
public boolean
                  run(
                  ){
        
//#line 23
final x10.
          lang.
          Clock c =
          x10.
          lang.
          Clock.make();
        
//#line 24
x10.
          lang.
          Runtime.runAsync(x10.lang.Place.place(x10.core.Ref.home(this)),
                           /* template:tuple { */x10.core.RailFactory.<x10.
                             lang.
                             Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                             lang.
                             Clock[] { c })/* } */,
                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                               
//#line 28
while (true) {
                                   
//#line 29
int temp;
                                   
//#line 30
try {{
                                       
//#line 30
x10.
                                         lang.
                                         Runtime.lock();
                                       {
                                           
//#line 30
temp = value1;
                                       }
                                   }}finally {{
                                         
//#line 30
x10.
                                           lang.
                                           Runtime.release();
                                     }}
                                   
//#line 31
if (((((int)(temp))) >= (((int)(42))))) {
                                       
//#line 31
break;
                                   }
                                   
//#line 32
try {{
                                       
//#line 32
x10.
                                         lang.
                                         Runtime.lock();
                                       
//#line 32
while (true) {
                                           
//#line 32
if (((int) value1) ==
                                                           ((int) value2)) {
                                               {
                                                   
//#line 32
ConditionalAtomicTest.this.value1 += 1;
                                                   
//#line 32
ConditionalAtomicTest.this.value2 -= 1;
                                               }
                                               
//#line 32
break;
                                           }
                                           
//#line 32
x10.
                                             lang.
                                             Runtime.await();
                                       }
                                   }}finally {{
                                         
//#line 32
x10.
                                           lang.
                                           Runtime.release();
                                     }}
                                   }
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
                           
//#line 35
x10.
                             lang.
                             Runtime.runAsync(x10.lang.Place.place(x10.core.Ref.home(this)),
                                              /* template:tuple { */x10.core.RailFactory.<x10.
                                                lang.
                                                Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                lang.
                                                Clock[] { c })/* } */,
                                              new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                  
//#line 39
while (true) {
                                                      
//#line 40
int temp;
                                                      
//#line 41
try {{
                                                          
//#line 41
x10.
                                                            lang.
                                                            Runtime.lock();
                                                          {
                                                              
//#line 41
temp = value2;
                                                          }
                                                      }}finally {{
                                                            
//#line 41
x10.
                                                              lang.
                                                              Runtime.release();
                                                        }}
                                                      
//#line 42
if (((((int)(temp))) >= (((int)(42))))) {
                                                          
//#line 42
break;
                                                      }
                                                      
//#line 43
try {{
                                                          
//#line 43
x10.
                                                            lang.
                                                            Runtime.lock();
                                                          
//#line 43
while (true) {
                                                              
//#line 43
if (((int) value1) ==
                                                                              ((int) ((((int)(value2))) + (((int)(2)))))) {
                                                                  {
                                                                      
//#line 44
ConditionalAtomicTest.this.value2 = value1;
                                                                  }
                                                                  
//#line 43
break;
                                                              }
                                                              
//#line 43
if (((int) value1) !=
                                                                              ((int) ((((int)(value2))) + (((int)(2))))) &&
                                                                              ((int) value1) !=
                                                                              ((int) value2)) {
                                                                  {
                                                                      
//#line 47
ConditionalAtomicTest.this.value1 = ConditionalAtomicTest.this.value2 = 43;
                                                                  }
                                                                  
//#line 43
break;
                                                              }
                                                              
//#line 43
x10.
                                                                lang.
                                                                Runtime.await();
                                                          }
                                                      }}finally {{
                                                            
//#line 43
x10.
                                                              lang.
                                                              Runtime.release();
                                                        }}
                                                      
//#line 47
;
                                                      }
                                                  }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                  });
                                              
//#line 50
x10.
                                                lang.
                                                Runtime.next();
                                              
//#line 52
int temp;
                                              
//#line 53
try {{
                                                  
//#line 53
x10.
                                                    lang.
                                                    Runtime.lock();
                                                  {
                                                      
//#line 53
temp = value1;
                                                  }
                                              }}finally {{
                                                    
//#line 53
x10.
                                                      lang.
                                                      Runtime.release();
                                                }}
                                              
//#line 54
return ((int) temp) ==
                                              ((int) 42);
                           }
                           
                           
//#line 57
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
                           							ConditionalAtomicTest.main(args);
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
                               
//#line 58
new ConditionalAtomicTest().executeAsync();
                           }/* } */
                           
                           public ConditionalAtomicTest() {
                               super();
                               
//#line 19
this.value1 = 0;
                               
//#line 20
this.value2 = 0;
                           }
        
        }
        