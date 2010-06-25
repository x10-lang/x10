
public class ClockTest13
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockTest13>_RTT = new x10.rtt.RuntimeType<ClockTest13>(
/* base class */ClockTest13.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 81
final public static int
      N =
      20;
    
//#line 82
final public static int
      M =
      ((((int)(ClockTest13.N))) / (((int)(2))));
    
//#line 83
final public static int
      chainLength =
      3;
    
//#line 84
int
      phaseA;
    
//#line 85
int
      phaseB;
    
//#line 86
int
      phaseC;
    
//#line 87
int
      phaseD;
    
    
//#line 89
public boolean
                  run(
                  ){
        
//#line 90
try {{
            
//#line 90
x10.
              lang.
              Runtime.startFinish();
            {
                
//#line 90
x10.
                  lang.
                  Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                       
//#line 91
final x10.
                                         lang.
                                         Clock a =
                                         x10.
                                         lang.
                                         Clock.make();
                                       
//#line 92
final x10.
                                         lang.
                                         Clock b =
                                         x10.
                                         lang.
                                         Clock.make();
                                       
//#line 93
final x10.
                                         lang.
                                         Clock c =
                                         x10.
                                         lang.
                                         Clock.make();
                                       
//#line 94
x10.
                                         lang.
                                         Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                            lang.
                                                            Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                            lang.
                                                            Clock[] { a })/* } */,
                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 94
ClockTest13.this.taskA(a);
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                          });
                                       
//#line 95
x10.
                                         lang.
                                         Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                            lang.
                                                            Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                            lang.
                                                            Clock[] { a,b })/* } */,
                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 95
ClockTest13.this.taskB(a,
                                                                                                 b);
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                          });
                                       
//#line 96
x10.
                                         lang.
                                         Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                            lang.
                                                            Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                            lang.
                                                            Clock[] { b,c })/* } */,
                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 96
ClockTest13.this.taskC(b,
                                                                                                 c);
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                          });
                                       
//#line 97
x10.
                                         lang.
                                         Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                            lang.
                                                            Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                            lang.
                                                            Clock[] { c })/* } */,
                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 97
ClockTest13.this.taskD(c);
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                          });
                                   }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                   });
            }
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
        java.lang.Throwable __desugarer__var__460__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 90
x10.
              lang.
              Runtime.pushException(__desugarer__var__460__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__460__) {
            
//#line 90
x10.
              lang.
              Runtime.pushException(__desugarer__var__460__);
        }finally {{
             
//#line 90
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 99
return true;
        }
    
    
//#line 102
void
                   taskA(
                   final x10.
                     lang.
                     Clock a){
        {
            
//#line 103
final int k45880min45881 =
              1;
            
//#line 103
final int k45880max45882 =
              ClockTest13.N;
            
//#line 103
for (
//#line 103
int k45880 =
                                k45880min45881;
                              ((((int)(k45880))) <= (((int)(k45880max45882))));
                              
//#line 103
k45880 += 1) {
                
//#line 103
final int k =
                  k45880;
                {
                    
//#line 104
x10.
                      io.
                      Console.OUT.println(((k) + (" A new phase")));
                    
//#line 105
try {{
                        
//#line 105
x10.
                          lang.
                          Runtime.lock();
                        {
                            
//#line 105
this.phaseA += 1;
                        }
                    }}finally {{
                          
//#line 105
x10.
                            lang.
                            Runtime.release();
                      }}
                    
//#line 106
x10.
                      io.
                      Console.OUT.println(((k) + (" A resuming a")));
                    
//#line 107
a.resume();
                    
//#line 108
if (((((int)(k))) <= (((int)(((((int)(ClockTest13.M))) - (((int)(ClockTest13.chainLength))))))))) {
                        
//#line 109
x10.
                          io.
                          Console.OUT.println(((k) + (" Waiting for forward phase shift")));
                        
//#line 110
try {{
                            
//#line 110
x10.
                              lang.
                              Runtime.lock();
                            
//#line 110
while (true) {
                                
//#line 110
if (((int) phaseB) ==
                                                 ((int) ((((int)(phaseA))) + (((int)(1))))) &&
                                                 ((int) phaseC) ==
                                                 ((int) ((((int)(phaseB))) + (((int)(1))))) &&
                                                 ((int) phaseD) ==
                                                 ((int) ((((int)(phaseC))) + (((int)(1)))))) {
                                    {
                                        
//#line 114
x10.
                                          io.
                                          Console.OUT.println(((k) + (" Max forward phase shift reached")));
                                    }
                                    
//#line 110
break;
                                }
                                
//#line 110
x10.
                                  lang.
                                  Runtime.await();
                            }
                        }}finally {{
                              
//#line 110
x10.
                                lang.
                                Runtime.release();
                          }}
                        }
                    
//#line 117
x10.
                      lang.
                      Runtime.next();
                    }
                }
            }
        }
        
        
//#line 120
void
                       taskB(
                       final x10.
                         lang.
                         Clock a,
                       final x10.
                         lang.
                         Clock b){
            {
                
//#line 121
final int k45883min45884 =
                  1;
                
//#line 121
final int k45883max45885 =
                  ClockTest13.N;
                
//#line 121
for (
//#line 121
int k45883 =
                                    k45883min45884;
                                  ((((int)(k45883))) <= (((int)(k45883max45885))));
                                  
//#line 121
k45883 += 1) {
                    
//#line 121
final int k =
                      k45883;
                    {
                        
//#line 122
x10.
                          io.
                          Console.OUT.println(((k) + (" B new phase")));
                        
//#line 123
try {{
                            
//#line 123
x10.
                              lang.
                              Runtime.lock();
                            {
                                
//#line 123
this.phaseB += 1;
                            }
                        }}finally {{
                              
//#line 123
x10.
                                lang.
                                Runtime.release();
                          }}
                        
//#line 124
x10.
                          io.
                          Console.OUT.println(((k) + (" B resuming a")));
                        
//#line 125
a.resume();
                        
//#line 126
x10.
                          io.
                          Console.OUT.println(((k) + (" B resuming b")));
                        
//#line 127
b.resume();
                        
//#line 128
x10.
                          io.
                          Console.OUT.println(((k) + (" B before next")));
                        
//#line 129
x10.
                          lang.
                          Runtime.next();
                        }
                    }
                }
            }
        
        
//#line 132
void
                       taskC(
                       final x10.
                         lang.
                         Clock b,
                       final x10.
                         lang.
                         Clock c){
            {
                
//#line 133
final int k45886min45887 =
                  1;
                
//#line 133
final int k45886max45888 =
                  ClockTest13.N;
                
//#line 133
for (
//#line 133
int k45886 =
                                    k45886min45887;
                                  ((((int)(k45886))) <= (((int)(k45886max45888))));
                                  
//#line 133
k45886 += 1) {
                    
//#line 133
final int k =
                      k45886;
                    {
                        
//#line 134
x10.
                          io.
                          Console.OUT.println(((k) + (" C new phase")));
                        
//#line 135
try {{
                            
//#line 135
x10.
                              lang.
                              Runtime.lock();
                            {
                                
//#line 135
this.phaseC += 1;
                            }
                        }}finally {{
                              
//#line 135
x10.
                                lang.
                                Runtime.release();
                          }}
                        
//#line 136
x10.
                          io.
                          Console.OUT.println(((k) + (" C resuming b")));
                        
//#line 137
b.resume();
                        
//#line 138
x10.
                          io.
                          Console.OUT.println(((k) + (" C resuming c")));
                        
//#line 139
c.resume();
                        
//#line 140
x10.
                          io.
                          Console.OUT.println(((k) + (" C before next")));
                        
//#line 141
x10.
                          lang.
                          Runtime.next();
                        }
                    }
                }
            }
        
        
//#line 144
void
                       taskD(
                       final x10.
                         lang.
                         Clock c){
            {
                
//#line 145
final int k45889min45890 =
                  1;
                
//#line 145
final int k45889max45891 =
                  ClockTest13.N;
                
//#line 145
for (
//#line 145
int k45889 =
                                    k45889min45890;
                                  ((((int)(k45889))) <= (((int)(k45889max45891))));
                                  
//#line 145
k45889 += 1) {
                    
//#line 145
final int k =
                      k45889;
                    {
                        
//#line 146
x10.
                          io.
                          Console.OUT.println(((k) + (" D new phase")));
                        
//#line 147
try {{
                            
//#line 147
x10.
                              lang.
                              Runtime.lock();
                            {
                                
//#line 147
this.phaseD += 1;
                            }
                        }}finally {{
                              
//#line 147
x10.
                                lang.
                                Runtime.release();
                          }}
                        
//#line 148
x10.
                          io.
                          Console.OUT.println(((k) + (" D resuming c")));
                        
//#line 149
c.resume();
                        
//#line 150
if (((((int)(k))) >= (((int)(ClockTest13.M)))) &&
                                         ((((int)(k))) <= (((int)(((((int)(ClockTest13.N))) - (((int)(ClockTest13.chainLength))))))))) {
                            
//#line 151
x10.
                              io.
                              Console.OUT.println(((k) + (" Waiting for reverse phase shift")));
                            
//#line 152
try {{
                                
//#line 152
x10.
                                  lang.
                                  Runtime.lock();
                                
//#line 152
while (true) {
                                    
//#line 152
if (((int) phaseC) ==
                                                     ((int) ((((int)(phaseD))) + (((int)(1))))) &&
                                                     ((int) phaseB) ==
                                                     ((int) ((((int)(phaseC))) + (((int)(1))))) &&
                                                     ((int) phaseA) ==
                                                     ((int) ((((int)(phaseB))) + (((int)(1)))))) {
                                        {
                                            
//#line 156
x10.
                                              io.
                                              Console.OUT.println(((k) + (" Max reverse phase shift reached")));
                                        }
                                        
//#line 152
break;
                                    }
                                    
//#line 152
x10.
                                      lang.
                                      Runtime.await();
                                }
                            }}finally {{
                                  
//#line 152
x10.
                                    lang.
                                    Runtime.release();
                              }}
                            }
                        
//#line 159
x10.
                          io.
                          Console.OUT.println(((k) + (" D before next")));
                        
//#line 160
x10.
                          lang.
                          Runtime.next();
                        }
                    }
                }
            }
            
            
//#line 164
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
            							ClockTest13.main(args);
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
                
//#line 165
new ClockTest13().execute();
            }/* } */
            
            public ClockTest13() {
                super();
                
//#line 84
this.phaseA = 0;
                
//#line 85
this.phaseB = 0;
                
//#line 86
this.phaseC = 0;
                
//#line 87
this.phaseD = 0;
            }
        
        }
        