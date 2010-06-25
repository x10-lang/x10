
public class ClockTest6
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockTest6>_RTT = new x10.rtt.RuntimeType<ClockTest6>(
/* base class */ClockTest6.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 65
final public static int
      N_INSTANCES =
      8;
    
//#line 66
final public static int
      N_NEXTS =
      4;
    
//#line 67
final public static int
      N_KINDS =
      4;
    
//#line 68
int
      globalCounter;
    
    
//#line 70
public boolean
                  run(
                  ){
        
//#line 71
try {{
            
//#line 71
x10.
              lang.
              Runtime.startFinish();
            {
                
//#line 71
x10.
                  lang.
                  Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                       
//#line 73
final x10.
                                         lang.
                                         Clock c =
                                         x10.
                                         lang.
                                         Clock.make();
                                       
//#line 74
final x10.
                                         lang.
                                         Clock d =
                                         x10.
                                         lang.
                                         Clock.make();
                                       
//#line 75
final x10.
                                         lang.
                                         Clock e =
                                         x10.
                                         lang.
                                         Clock.make();
                                       {
                                           
//#line 78
final int i48164min48165 =
                                             1;
                                           
//#line 78
final int i48164max48166 =
                                             ClockTest6.N_INSTANCES;
                                           
//#line 78
for (
//#line 78
int i48164 =
                                                              i48164min48165;
                                                            ((((int)(i48164))) <= (((int)(i48164max48166))));
                                                            
//#line 78
i48164 += 1) {
                                               
//#line 78
final int i =
                                                 i48164;
                                               {
                                                   
//#line 80
x10.
                                                     lang.
                                                     Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                        lang.
                                                                        Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                        lang.
                                                                        Clock[] { c })/* } */,
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          {
                                                                              
//#line 81
final int tick48152min48153 =
                                                                                0;
                                                                              
//#line 81
final int tick48152max48154 =
                                                                                (((((int)(ClockTest6.N_NEXTS))) - (((int)(1)))));
                                                                              
//#line 81
for (
//#line 81
int tick48152 =
                                                                                                 tick48152min48153;
                                                                                               ((((int)(tick48152))) <= (((int)(tick48152max48154))));
                                                                                               
//#line 81
tick48152 += 1) {
                                                                                  
//#line 81
final int tick =
                                                                                    tick48152;
                                                                                  {
                                                                                      
//#line 83
ClockTest6.this.doWork("1_",
                                                                                                                         (int)(i),
                                                                                                                         "(c)",
                                                                                                                         (int)(tick));
                                                                                      
//#line 84
x10.
                                                                                        lang.
                                                                                        Runtime.next();
                                                                                      
//#line 86
ClockTest6.this.verify("1_",
                                                                                                                         (int)(i),
                                                                                                                         (int)(tick));
                                                                                      
//#line 87
x10.
                                                                                        lang.
                                                                                        Runtime.next();
                                                                                  }
                                                                              }
                                                                          }
                                                                      }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                      });
                                                   
//#line 90
x10.
                                                     lang.
                                                     Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                        lang.
                                                                        Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                        lang.
                                                                        Clock[] { c,d })/* } */,
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          {
                                                                              
//#line 91
final int tick48155min48156 =
                                                                                0;
                                                                              
//#line 91
final int tick48155max48157 =
                                                                                (((((int)(ClockTest6.N_NEXTS))) - (((int)(1)))));
                                                                              
//#line 91
for (
//#line 91
int tick48155 =
                                                                                                 tick48155min48156;
                                                                                               ((((int)(tick48155))) <= (((int)(tick48155max48157))));
                                                                                               
//#line 91
tick48155 += 1) {
                                                                                  
//#line 91
final int tick =
                                                                                    tick48155;
                                                                                  {
                                                                                      
//#line 93
ClockTest6.this.doWork("2_",
                                                                                                                         (int)(i),
                                                                                                                         "(c, d)",
                                                                                                                         (int)(tick));
                                                                                      
//#line 94
x10.
                                                                                        lang.
                                                                                        Runtime.next();
                                                                                      
//#line 96
ClockTest6.this.verify("2_",
                                                                                                                         (int)(i),
                                                                                                                         (int)(tick));
                                                                                      
//#line 97
x10.
                                                                                        lang.
                                                                                        Runtime.next();
                                                                                  }
                                                                              }
                                                                          }
                                                                      }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                      });
                                                   
//#line 100
x10.
                                                     lang.
                                                     Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                        lang.
                                                                        Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                        lang.
                                                                        Clock[] { c,e })/* } */,
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          {
                                                                              
//#line 101
final int tick48158min48159 =
                                                                                0;
                                                                              
//#line 101
final int tick48158max48160 =
                                                                                (((((int)(ClockTest6.N_NEXTS))) - (((int)(1)))));
                                                                              
//#line 101
for (
//#line 101
int tick48158 =
                                                                                                  tick48158min48159;
                                                                                                ((((int)(tick48158))) <= (((int)(tick48158max48160))));
                                                                                                
//#line 101
tick48158 += 1) {
                                                                                  
//#line 101
final int tick =
                                                                                    tick48158;
                                                                                  {
                                                                                      
//#line 103
ClockTest6.this.doWork("3_",
                                                                                                                          (int)(i),
                                                                                                                          "(c, e)",
                                                                                                                          (int)(tick));
                                                                                      
//#line 104
x10.
                                                                                        lang.
                                                                                        Runtime.next();
                                                                                      
//#line 106
ClockTest6.this.verify("3_",
                                                                                                                          (int)(i),
                                                                                                                          (int)(tick));
                                                                                      
//#line 107
x10.
                                                                                        lang.
                                                                                        Runtime.next();
                                                                                  }
                                                                              }
                                                                          }
                                                                      }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                      });
                                                   
//#line 110
x10.
                                                     lang.
                                                     Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                        lang.
                                                                        Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                        lang.
                                                                        Clock[] { c,d,e })/* } */,
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          {
                                                                              
//#line 111
final int tick48161min48162 =
                                                                                0;
                                                                              
//#line 111
final int tick48161max48163 =
                                                                                (((((int)(ClockTest6.N_NEXTS))) - (((int)(1)))));
                                                                              
//#line 111
for (
//#line 111
int tick48161 =
                                                                                                  tick48161min48162;
                                                                                                ((((int)(tick48161))) <= (((int)(tick48161max48163))));
                                                                                                
//#line 111
tick48161 += 1) {
                                                                                  
//#line 111
final int tick =
                                                                                    tick48161;
                                                                                  {
                                                                                      
//#line 113
ClockTest6.this.doWork("4_",
                                                                                                                          (int)(i),
                                                                                                                          "(c, d, e)",
                                                                                                                          (int)(tick));
                                                                                      
//#line 114
x10.
                                                                                        lang.
                                                                                        Runtime.next();
                                                                                      
//#line 116
ClockTest6.this.verify("4_",
                                                                                                                          (int)(i),
                                                                                                                          (int)(tick));
                                                                                      
//#line 117
x10.
                                                                                        lang.
                                                                                        Runtime.next();
                                                                                  }
                                                                              }
                                                                          }
                                                                      }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                      });
                                               }
                                           }
                                       }
                                   }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                   });
            }
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
        java.lang.Throwable __desugarer__var__508__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 71
x10.
              lang.
              Runtime.pushException(__desugarer__var__508__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__508__) {
            
//#line 71
x10.
              lang.
              Runtime.pushException(__desugarer__var__508__);
        }finally {{
             
//#line 71
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 125
return true;
        }
    
    
//#line 131
void
                   doWork(
                   java.lang.String kind,
                   int instance,
                   java.lang.String clocks,
                   int tick){
        
//#line 132
try {{
            
//#line 132
x10.
              lang.
              Runtime.lock();
            {
                
//#line 132
this.globalCounter += 1;
            }
        }}finally {{
              
//#line 132
x10.
                lang.
                Runtime.release();
          }}
        
//#line 133
x10.
          io.
          Console.OUT.println((((((((((((("Activity ") + (kind))) + (instance))) + (" in phase "))) + (tick))) + (" of clocks "))) + (clocks)));
        }
    
    
//#line 140
void
                   verify(
                   java.lang.String kind,
                   int instance,
                   int tick){
        
//#line 141
int tmp;
        
//#line 142
try {{
            
//#line 142
x10.
              lang.
              Runtime.lock();
            {
                
//#line 142
tmp = globalCounter;
            }
        }}finally {{
              
//#line 142
x10.
                lang.
                Runtime.release();
          }}
        
//#line 143
harness.
          x10Test.chk((boolean)(((int) ((((int)(((((int)((((((int)(tick))) + (((int)(1)))))))) * (((int)(ClockTest6.N_KINDS))))))) * (((int)(ClockTest6.N_INSTANCES))))) ==
                      ((int) tmp)));
        }
    
    
//#line 146
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
    							ClockTest6.main(args);
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
        
//#line 147
new ClockTest6().executeAsync();
    }/* } */
    
    public ClockTest6() {
        super();
        
//#line 68
this.globalCounter = 0;
    }
    
    }
    