
public class AtomicOrdered
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<AtomicOrdered>_RTT = new x10.rtt.RuntimeType<AtomicOrdered>(
/* base class */AtomicOrdered.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 23
final public static int
      CACHESIZE =
      ((((int)(((((int)(32))) * (((int)(1024))))))) / (((int)(4))));
    
//#line 24
final public static int
      LINESIZE =
      ((((int)(128))) / (((int)(4))));
    
//#line 25
final public static int
      MAX_ASSOC =
      8;
    
//#line 27
final x10.
      array.
      Array<java.lang.Integer>
      A;
    
    
//#line 29
public boolean
                  run(
                  ){
        
//#line 30
final AtomicOrdered.
          pair r =
          ((AtomicOrdered.
          pair)(new AtomicOrdered.
          pair()));
        
//#line 31
try {{
            
//#line 31
x10.
              lang.
              Runtime.startFinish();
            {
                
//#line 32
x10.
                  lang.
                  Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                       
//#line 33
try {{
                                           
//#line 33
x10.
                                             lang.
                                             Runtime.startFinish();
                                           {
                                               
                                           }
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                                       if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                                       java.lang.Throwable __desugarer__var__356__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                                       {
                                           
//#line 33
x10.
                                             lang.
                                             Runtime.pushException(__desugarer__var__356__);
                                       }
                                       }
                                       throw __$generated_wrappedex$__;
                                       }catch (java.lang.Throwable __desugarer__var__356__) {
                                           
//#line 33
x10.
                                             lang.
                                             Runtime.pushException(__desugarer__var__356__);
                                       }finally {{
                                            
//#line 33
x10.
                                              lang.
                                              Runtime.stopFinish();
                                        }}
                                       
//#line 34
try {{
                                           
//#line 34
x10.
                                             lang.
                                             Runtime.lock();
                                           {
                                               
//#line 34
A.set$G((int)(1),
                                                                   (int)(0));
                                           }
                                       }}finally {{
                                             
//#line 34
x10.
                                               lang.
                                               Runtime.release();
                                         }}
                                       
//#line 35
int t;
                                       
//#line 36
try {{
                                           
//#line 36
x10.
                                             lang.
                                             Runtime.lock();
                                           {
                                               
//#line 36
t = A.apply$G((int)(AtomicOrdered.LINESIZE));
                                           }
                                       }}finally {{
                                             
//#line 36
x10.
                                               lang.
                                               Runtime.release();
                                         }}
                                       
//#line 37
r.v1 = t;
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                       });
                                       
//#line 39
x10.
                                         lang.
                                         Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 40
try {{
                                                                  
//#line 40
x10.
                                                                    lang.
                                                                    Runtime.startFinish();
                                                                  {
                                                                      
                                                                  }
                                                              }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                                                              if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                                                              java.lang.Throwable __desugarer__var__357__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                                                              {
                                                                  
//#line 40
x10.
                                                                    lang.
                                                                    Runtime.pushException(__desugarer__var__357__);
                                                              }
                                                              }
                                                              throw __$generated_wrappedex$__;
                                                              }catch (java.lang.Throwable __desugarer__var__357__) {
                                                                  
//#line 40
x10.
                                                                    lang.
                                                                    Runtime.pushException(__desugarer__var__357__);
                                                              }finally {{
                                                                   
//#line 40
x10.
                                                                     lang.
                                                                     Runtime.stopFinish();
                                                               }}
                                                              
//#line 41
try {{
                                                                  
//#line 41
x10.
                                                                    lang.
                                                                    Runtime.lock();
                                                                  {
                                                                      
//#line 41
A.set$G((int)(1),
                                                                                          (int)(AtomicOrdered.LINESIZE));
                                                                  }
                                                              }}finally {{
                                                                    
//#line 41
x10.
                                                                      lang.
                                                                      Runtime.release();
                                                                }}
                                                              
//#line 42
int t;
                                                              
//#line 43
try {{
                                                                  
//#line 43
x10.
                                                                    lang.
                                                                    Runtime.lock();
                                                                  {
                                                                      
//#line 43
t = A.apply$G((int)(0));
                                                                  }
                                                              }}finally {{
                                                                    
//#line 43
x10.
                                                                      lang.
                                                                      Runtime.release();
                                                                }}
                                                              
//#line 44
r.v2 = t;
                                                              }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                              });
                                       }
                }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                java.lang.Throwable __desugarer__var__358__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                {
                    
//#line 31
x10.
                      lang.
                      Runtime.pushException(__desugarer__var__358__);
                }
                }
                throw __$generated_wrappedex$__;
                }catch (java.lang.Throwable __desugarer__var__358__) {
                    
//#line 31
x10.
                      lang.
                      Runtime.pushException(__desugarer__var__358__);
                }finally {{
                     
//#line 31
x10.
                       lang.
                       Runtime.stopFinish();
                 }}
                
//#line 47
x10.
                  io.
                  Console.OUT.println((((((("v1 = ") + (r.
                                                          v1))) + (" v2 = "))) + (r.
                                                                                    v2)));
                
//#line 49
try {{
                    
//#line 49
x10.
                      lang.
                      Runtime.lock();
                    {
                        
//#line 49
harness.
                          x10Test.chk((boolean)((!(((boolean)((((int) r.
                                                                        v1) ==
                                                               ((int) 0) &&
                                                               ((int) r.
                                                                        v2) ==
                                                               ((int) 0))))))));
                    }
                }}finally {{
                      
//#line 49
x10.
                        lang.
                        Runtime.release();
                  }}
                
//#line 50
return true;
                }
                
                
//#line 53
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
                							AtomicOrdered.main(args);
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
                    
//#line 54
new AtomicOrdered().execute();
                }/* } */
                
                
//#line 57
static class pair
                            extends x10.core.Ref
                            {public static final x10.rtt.RuntimeType<AtomicOrdered.
                  pair>_RTT = new x10.rtt.RuntimeType<AtomicOrdered.
                  pair>(
                /* base class */AtomicOrdered.
                  pair.class
                , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
                );
                public x10.rtt.RuntimeType getRTT() {return _RTT;}
                
                
                
                    
//#line 58
int
                      v1;
                    
//#line 59
int
                      v2;
                    
                    public pair() {
                        super();
                        
//#line 58
this.v1 = 0;
                        
//#line 59
this.v2 = 0;
                    }
                
                }
                
                
                public AtomicOrdered() {
                    super();
                    
//#line 27
this.A = ((x10.
                      array.
                      Array)(new x10.
                      array.
                      Array<java.lang.Integer>(x10.rtt.Types.INT,
                                               x10.
                                                 array.
                                                 Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                            array.
                                                                            Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                                                            array.
                                                                            Region[] { x10.
                                                                            array.
                                                                            Region.makeRectangular((int)(0),
                                                                                                   (int)(((((int)(((((int)(AtomicOrdered.CACHESIZE))) * (((int)((((((int)(AtomicOrdered.MAX_ASSOC))) + (((int)(2)))))))))))) - (((int)(1)))))) })/* } */))));
                }
            
            }
            