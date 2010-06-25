
public class Atomic1
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<Atomic1>_RTT = new x10.rtt.RuntimeType<Atomic1>(
/* base class */Atomic1.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 21
int
      cnt;
    
//#line 22
int
      cnt_broken;
    
//#line 23
final public static int
      N =
      100;
    
    
//#line 24
int
                  threadRun(
                  ){
        
//#line 25
for (
//#line 25
int i =
                           0;
                         ((((int)(i))) < (((int)(Atomic1.N))));
                         
//#line 25
i += 1) {
            
//#line 26
int t;
            
//#line 27
try {{
                
//#line 27
x10.
                  lang.
                  Runtime.lock();
                {
                    
//#line 27
t = cnt_broken;
                }
            }}finally {{
                  
//#line 27
x10.
                    lang.
                    Runtime.release();
              }}
            
//#line 28
try {{
                
//#line 28
x10.
                  lang.
                  Runtime.lock();
                {
                    
//#line 28
this.cnt += 1;
                }
            }}finally {{
                  
//#line 28
x10.
                    lang.
                    Runtime.release();
              }}
            
//#line 29
try {{
                
//#line 29
x10.
                  lang.
                  Runtime.lock();
                {
                    
//#line 29
this.cnt_broken = ((((int)(t))) + (((int)(1))));
                }
            }}finally {{
                  
//#line 29
x10.
                    lang.
                    Runtime.release();
              }}
            }
            
//#line 31
return 0;
        }
        
        
//#line 34
public boolean
                      run(
                      ){
            
//#line 35
final x10.
              lang.
              Future<java.lang.Integer> a =
              x10.
              lang.
              Runtime.<java.lang.Integer>evalFuture(x10.rtt.Types.INT,
                                                    x10.lang.Place.place(x10.core.Ref.home(this)),
                                                    new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                    public final int apply() { {
                                                        
//#line 35
return Atomic1.this.threadRun();
                                                    }}
                                                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                    }
                                                    });
            
//#line 36
final x10.
              lang.
              Future<java.lang.Integer> b =
              x10.
              lang.
              Runtime.<java.lang.Integer>evalFuture(x10.rtt.Types.INT,
                                                    x10.lang.Place.place(x10.core.Ref.home(this)),
                                                    new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                    public final int apply() { {
                                                        
//#line 36
return Atomic1.this.threadRun();
                                                    }}
                                                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                    }
                                                    });
            
//#line 37
final x10.
              lang.
              Future<java.lang.Integer> c =
              x10.
              lang.
              Runtime.<java.lang.Integer>evalFuture(x10.rtt.Types.INT,
                                                    x10.lang.Place.place(x10.core.Ref.home(this)),
                                                    new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                    public final int apply() { {
                                                        
//#line 37
return Atomic1.this.threadRun();
                                                    }}
                                                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                    }
                                                    });
            
//#line 38
final x10.
              lang.
              Future<java.lang.Integer> d =
              x10.
              lang.
              Runtime.<java.lang.Integer>evalFuture(x10.rtt.Types.INT,
                                                    x10.lang.Place.place(x10.core.Ref.home(this)),
                                                    new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                    public final int apply() { {
                                                        
//#line 38
return Atomic1.this.threadRun();
                                                    }}
                                                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                    }
                                                    });
            
//#line 39
final x10.
              lang.
              Future<java.lang.Integer> e =
              x10.
              lang.
              Runtime.<java.lang.Integer>evalFuture(x10.rtt.Types.INT,
                                                    x10.lang.Place.place(x10.core.Ref.home(this)),
                                                    new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                    public final int apply() { {
                                                        
//#line 39
return Atomic1.this.threadRun();
                                                    }}
                                                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                    }
                                                    });
            
//#line 40
final x10.
              lang.
              Future<java.lang.Integer> f =
              x10.
              lang.
              Runtime.<java.lang.Integer>evalFuture(x10.rtt.Types.INT,
                                                    x10.lang.Place.place(x10.core.Ref.home(this)),
                                                    new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                    public final int apply() { {
                                                        
//#line 40
return Atomic1.this.threadRun();
                                                    }}
                                                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                    }
                                                    });
            
//#line 41
final x10.
              lang.
              Future<java.lang.Integer> g =
              x10.
              lang.
              Runtime.<java.lang.Integer>evalFuture(x10.rtt.Types.INT,
                                                    x10.lang.Place.place(x10.core.Ref.home(this)),
                                                    new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                    public final int apply() { {
                                                        
//#line 41
return Atomic1.this.threadRun();
                                                    }}
                                                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                    }
                                                    });
            
//#line 42
final x10.
              lang.
              Future<java.lang.Integer> h =
              x10.
              lang.
              Runtime.<java.lang.Integer>evalFuture(x10.rtt.Types.INT,
                                                    x10.lang.Place.place(x10.core.Ref.home(this)),
                                                    new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                    public final int apply() { {
                                                        
//#line 42
return Atomic1.this.threadRun();
                                                    }}
                                                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                    }
                                                    });
            
//#line 43
final int i =
              a.force$G();
            
//#line 44
final int j =
              b.force$G();
            
//#line 45
final int k =
              c.force$G();
            
//#line 46
final int l =
              d.force$G();
            
//#line 47
final int m =
              e.force$G();
            
//#line 48
final int n =
              f.force$G();
            
//#line 49
final int o =
              g.force$G();
            
//#line 50
final int p =
              h.force$G();
            
//#line 51
int t1;
            
//#line 52
int t2;
            
//#line 53
try {{
                
//#line 53
x10.
                  lang.
                  Runtime.lock();
                {
                    
//#line 53
t1 = cnt;
                }
            }}finally {{
                  
//#line 53
x10.
                    lang.
                    Runtime.release();
              }}
            
//#line 54
try {{
                
//#line 54
x10.
                  lang.
                  Runtime.lock();
                {
                    
//#line 54
t2 = cnt_broken;
                }
            }}finally {{
                  
//#line 54
x10.
                    lang.
                    Runtime.release();
              }}
            
//#line 55
x10.
              io.
              Console.OUT.println((((((("Atomic1: ") + (t1))) + (" =?= "))) + (t2)));
            
//#line 56
return ((int) t1) ==
            ((int) ((((int)(8))) * (((int)(Atomic1.N))))) &&
            ((((int)(t1))) >= (((int)(t2))));
            }
            
            
//#line 59
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
            							Atomic1.main(args);
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
                
//#line 60
new Atomic1().execute();
            }/* } */
            
            public Atomic1() {
                super();
                
//#line 21
this.cnt = 0;
                
//#line 22
this.cnt_broken = 0;
            }
        
        }
        