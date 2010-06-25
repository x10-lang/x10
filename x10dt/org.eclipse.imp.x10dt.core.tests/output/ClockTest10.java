
public class ClockTest10
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockTest10>_RTT = new x10.rtt.RuntimeType<ClockTest10>(
/* base class */ClockTest10.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 33
final x10.core.Rail<java.lang.Integer>
      varA;
    
//#line 34
final x10.core.Rail<java.lang.Integer>
      varB;
    
//#line 35
final x10.core.Rail<java.lang.Integer>
      varC;
    
//#line 36
final x10.core.Rail<java.lang.Integer>
      varD;
    
//#line 37
final x10.core.Rail<java.lang.Integer>
      varE;
    
//#line 38
final public static int
      N =
      10;
    
//#line 39
final public static int
      pipeDepth =
      2;
    
    
//#line 41
static int
                  ph(
                  int x){
        
//#line 41
return ((((int)(x))) % (((int)(2))));
    }
    
    
//#line 43
public boolean
                  run(
                  ){
        
//#line 44
try {{
            
//#line 44
x10.
              lang.
              Runtime.startFinish();
            {
                
//#line 44
x10.
                  lang.
                  Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                       
//#line 45
final x10.
                                         lang.
                                         Clock a =
                                         x10.
                                         lang.
                                         Clock.make();
                                       
//#line 46
final x10.
                                         lang.
                                         Clock b =
                                         x10.
                                         lang.
                                         Clock.make();
                                       
//#line 47
final x10.
                                         lang.
                                         Clock c =
                                         x10.
                                         lang.
                                         Clock.make();
                                       
//#line 48
x10.
                                         lang.
                                         Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                            lang.
                                                            Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                            lang.
                                                            Clock[] { a })/* } */,
                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 48
ClockTest10.this.taskA(a);
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                          });
                                       
//#line 49
x10.
                                         lang.
                                         Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                            lang.
                                                            Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                            lang.
                                                            Clock[] { a,b })/* } */,
                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 49
ClockTest10.this.taskB(a,
                                                                                                 b);
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                          });
                                       
//#line 50
x10.
                                         lang.
                                         Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                            lang.
                                                            Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                            lang.
                                                            Clock[] { a,c })/* } */,
                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 50
ClockTest10.this.taskC(a,
                                                                                                 c);
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                          });
                                       
//#line 51
x10.
                                         lang.
                                         Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                            lang.
                                                            Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                            lang.
                                                            Clock[] { b,c })/* } */,
                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 51
ClockTest10.this.taskD(b,
                                                                                                 c);
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                          });
                                       
//#line 52
x10.
                                         lang.
                                         Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                            lang.
                                                            Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                            lang.
                                                            Clock[] { c })/* } */,
                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 52
ClockTest10.this.taskE(c);
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                          });
                                   }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                   });
            }
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
        java.lang.Throwable __desugarer__var__448__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 44
x10.
              lang.
              Runtime.pushException(__desugarer__var__448__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__448__) {
            
//#line 44
x10.
              lang.
              Runtime.pushException(__desugarer__var__448__);
        }finally {{
             
//#line 44
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 54
return true;
        }
    
    
//#line 57
void
                  taskA(
                  final x10.
                    lang.
                    Clock a){
        {
            
//#line 58
final int k44807min44808 =
              1;
            
//#line 58
final int k44807max44809 =
              ClockTest10.N;
            
//#line 58
for (
//#line 58
int k44807 =
                               k44807min44808;
                             ((((int)(k44807))) <= (((int)(k44807max44809))));
                             
//#line 58
k44807 += 1) {
                
//#line 58
final int k =
                  k44807;
                {
                    
//#line 59
((int[])varA.value)[ClockTest10.ph((int)(k))] = k;
                    
//#line 60
x10.
                      io.
                      Console.OUT.println(((((((" ") + (k))) + (" A producing "))) + (((int[])varA.value)[ClockTest10.ph((int)(k))])));
                    
//#line 61
x10.
                      lang.
                      Runtime.next();
                }
            }
        }
    }
    
    
//#line 64
void
                  taskB(
                  final x10.
                    lang.
                    Clock a,
                  final x10.
                    lang.
                    Clock b){
        {
            
//#line 65
final int k44810min44811 =
              1;
            
//#line 65
final int k44810max44812 =
              ClockTest10.N;
            
//#line 65
for (
//#line 65
int k44810 =
                               k44810min44811;
                             ((((int)(k44810))) <= (((int)(k44810max44812))));
                             
//#line 65
k44810 += 1) {
                
//#line 65
final int k =
                  k44810;
                {
                    
//#line 66
final ClockTest10.
                      boxedInt tmp =
                      ((ClockTest10.
                      boxedInt)(new ClockTest10.
                      boxedInt()));
                    
//#line 67
try {{
                        
//#line 67
x10.
                          lang.
                          Runtime.startFinish();
                        {
                            
//#line 67
tmp.val = ((((int)(((int[])varA.value)[ClockTest10.ph((int)(((((int)(k))) - (((int)(1))))))]))) + (((int)(((int[])varA.value)[ClockTest10.ph((int)(((((int)(k))) - (((int)(1))))))]))));
                        }
                    }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                    if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                    java.lang.Throwable __desugarer__var__449__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                    {
                        
//#line 67
x10.
                          lang.
                          Runtime.pushException(__desugarer__var__449__);
                    }
                    }
                    throw __$generated_wrappedex$__;
                    }catch (java.lang.Throwable __desugarer__var__449__) {
                        
//#line 67
x10.
                          lang.
                          Runtime.pushException(__desugarer__var__449__);
                    }finally {{
                         
//#line 67
x10.
                           lang.
                           Runtime.stopFinish();
                     }}
                    
//#line 68
x10.
                      io.
                      Console.OUT.println(((((((" ") + (k))) + (" B consuming oldA producing "))) + (tmp.
                                                                                                       val)));
                    
//#line 69
a.resume();
                    
//#line 70
((int[])varB.value)[ClockTest10.ph((int)(k))] = tmp.
                                                                                  val;
                    
//#line 71
x10.
                      io.
                      Console.OUT.println(((" ") + ("B before next")));
                    
//#line 72
x10.
                      lang.
                      Runtime.next();
                    }
                }
            }
        }
    
    
//#line 75
void
                  taskC(
                  final x10.
                    lang.
                    Clock a,
                  final x10.
                    lang.
                    Clock c){
        {
            
//#line 76
final int k44813min44814 =
              1;
            
//#line 76
final int k44813max44815 =
              ClockTest10.N;
            
//#line 76
for (
//#line 76
int k44813 =
                               k44813min44814;
                             ((((int)(k44813))) <= (((int)(k44813max44815))));
                             
//#line 76
k44813 += 1) {
                
//#line 76
final int k =
                  k44813;
                {
                    
//#line 77
final ClockTest10.
                      boxedInt tmp =
                      ((ClockTest10.
                      boxedInt)(new ClockTest10.
                      boxedInt()));
                    
//#line 78
try {{
                        
//#line 78
x10.
                          lang.
                          Runtime.startFinish();
                        {
                            
//#line 78
tmp.val = ((((int)(((int[])varA.value)[ClockTest10.ph((int)(((((int)(k))) - (((int)(1))))))]))) * (((int)(((int[])varA.value)[ClockTest10.ph((int)(((((int)(k))) - (((int)(1))))))]))));
                        }
                    }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                    if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                    java.lang.Throwable __desugarer__var__450__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                    {
                        
//#line 78
x10.
                          lang.
                          Runtime.pushException(__desugarer__var__450__);
                    }
                    }
                    throw __$generated_wrappedex$__;
                    }catch (java.lang.Throwable __desugarer__var__450__) {
                        
//#line 78
x10.
                          lang.
                          Runtime.pushException(__desugarer__var__450__);
                    }finally {{
                         
//#line 78
x10.
                           lang.
                           Runtime.stopFinish();
                     }}
                    
//#line 79
x10.
                      io.
                      Console.OUT.println(((((((" ") + (k))) + (" C consuming oldA "))) + (tmp.
                                                                                             val)));
                    
//#line 80
a.resume();
                    
//#line 81
((int[])varC.value)[ClockTest10.ph((int)(k))] = tmp.
                                                                                  val;
                    
//#line 82
x10.
                      io.
                      Console.OUT.println(((" ") + ("C before next")));
                    
//#line 83
x10.
                      lang.
                      Runtime.next();
                    }
                }
            }
        }
    
    
//#line 86
void
                  taskD(
                  final x10.
                    lang.
                    Clock b,
                  final x10.
                    lang.
                    Clock c){
        {
            
//#line 87
final int k44816min44817 =
              1;
            
//#line 87
final int k44816max44818 =
              ClockTest10.N;
            
//#line 87
for (
//#line 87
int k44816 =
                               k44816min44817;
                             ((((int)(k44816))) <= (((int)(k44816max44818))));
                             
//#line 87
k44816 += 1) {
                
//#line 87
final int k =
                  k44816;
                {
                    
//#line 88
final ClockTest10.
                      boxedInt tmp =
                      ((ClockTest10.
                      boxedInt)(new ClockTest10.
                      boxedInt()));
                    
//#line 89
try {{
                        
//#line 89
x10.
                          lang.
                          Runtime.startFinish();
                        {
                            
//#line 89
tmp.val = ((((int)(((((int)(((int[])varB.value)[ClockTest10.ph((int)(((((int)(k))) - (((int)(1))))))]))) + (((int)(((int[])varC.value)[ClockTest10.ph((int)(((((int)(k))) - (((int)(1))))))]))))))) + (((int)(10))));
                        }
                    }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                    if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                    java.lang.Throwable __desugarer__var__451__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                    {
                        
//#line 89
x10.
                          lang.
                          Runtime.pushException(__desugarer__var__451__);
                    }
                    }
                    throw __$generated_wrappedex$__;
                    }catch (java.lang.Throwable __desugarer__var__451__) {
                        
//#line 89
x10.
                          lang.
                          Runtime.pushException(__desugarer__var__451__);
                    }finally {{
                         
//#line 89
x10.
                           lang.
                           Runtime.stopFinish();
                     }}
                    
//#line 90
x10.
                      io.
                      Console.OUT.println(((((((" ") + (k))) + (" D consuming oldB+oldC producing "))) + (tmp.
                                                                                                            val)));
                    
//#line 91
c.resume();
                    
//#line 92
b.resume();
                    
//#line 93
((int[])varD.value)[ClockTest10.ph((int)(k))] = tmp.
                                                                                  val;
                    
//#line 94
x10.
                      io.
                      Console.OUT.println(((((" ") + (k))) + (" D before next")));
                    
//#line 95
int n =
                      ((((int)(k))) - (((int)(ClockTest10.pipeDepth))));
                    
//#line 96
harness.
                      x10Test.chk((boolean)((!(((boolean)((((((int)(k))) > (((int)(ClockTest10.pipeDepth))))))))) ||
                                  ((int) ((int[])varD.value)[ClockTest10.ph((int)(k))]) ==
                                  ((int) ((((int)(((((int)(((((int)(n))) + (((int)(n))))))) + (((int)(((((int)(n))) * (((int)(n))))))))))) + (((int)(10)))))));
                    
//#line 97
x10.
                      lang.
                      Runtime.next();
                    }
                }
            }
        }
    
    
//#line 100
void
                   taskE(
                   final x10.
                     lang.
                     Clock c){
        {
            
//#line 101
final int k44819min44820 =
              1;
            
//#line 101
final int k44819max44821 =
              ClockTest10.N;
            
//#line 101
for (
//#line 101
int k44819 =
                                k44819min44820;
                              ((((int)(k44819))) <= (((int)(k44819max44821))));
                              
//#line 101
k44819 += 1) {
                
//#line 101
final int k =
                  k44819;
                {
                    
//#line 102
final ClockTest10.
                      boxedInt tmp =
                      ((ClockTest10.
                      boxedInt)(new ClockTest10.
                      boxedInt()));
                    
//#line 103
try {{
                        
//#line 103
x10.
                          lang.
                          Runtime.startFinish();
                        {
                            
//#line 103
tmp.val = ((((int)(((int[])varC.value)[ClockTest10.ph((int)(((((int)(k))) - (((int)(1))))))]))) * (((int)(7))));
                        }
                    }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                    if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                    java.lang.Throwable __desugarer__var__452__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                    {
                        
//#line 103
x10.
                          lang.
                          Runtime.pushException(__desugarer__var__452__);
                    }
                    }
                    throw __$generated_wrappedex$__;
                    }catch (java.lang.Throwable __desugarer__var__452__) {
                        
//#line 103
x10.
                          lang.
                          Runtime.pushException(__desugarer__var__452__);
                    }finally {{
                         
//#line 103
x10.
                           lang.
                           Runtime.stopFinish();
                     }}
                    
//#line 104
x10.
                      io.
                      Console.OUT.println(((((((" ") + (k))) + (" E consuming oldC producing "))) + (tmp.
                                                                                                       val)));
                    
//#line 105
c.resume();
                    
//#line 106
((int[])varE.value)[ClockTest10.ph((int)(k))] = tmp.
                                                                                   val;
                    
//#line 107
x10.
                      io.
                      Console.OUT.println(((((" ") + (k))) + (" E before next")));
                    
//#line 108
int n =
                      ((((int)(k))) - (((int)(ClockTest10.pipeDepth))));
                    
//#line 109
harness.
                      x10Test.chk((boolean)((!(((boolean)((((((int)(k))) > (((int)(ClockTest10.pipeDepth))))))))) ||
                                  ((int) ((int[])varE.value)[ClockTest10.ph((int)(k))]) ==
                                  ((int) ((((int)(((((int)(n))) * (((int)(n))))))) * (((int)(7)))))));
                    
//#line 110
x10.
                      lang.
                      Runtime.next();
                    }
                }
            }
        }
    
    
//#line 114
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
    							ClockTest10.main(args);
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
        
//#line 115
new ClockTest10().execute();
    }/* } */
    
    
//#line 118
static class boxedInt
                 extends x10.core.Ref
                 {public static final x10.rtt.RuntimeType<ClockTest10.
      boxedInt>_RTT = new x10.rtt.RuntimeType<ClockTest10.
      boxedInt>(
    /* base class */ClockTest10.
      boxedInt.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 119
int
          val;
        
        public boxedInt() {
            super();
            
//#line 119
this.val = 0;
        }
    
    }
    
    
    public ClockTest10() {
        super();
        
//#line 33
this.varA = ((x10.core.Rail)((new java.lang.Object() {final x10.core.Rail<java.lang.Integer> apply(int length) {int[] array = new int[length];for (int x$ = 0; x$ < length; x$++) {final int x = x$;array[x] = 0;}return new x10.core.Rail<java.lang.Integer>(x10.rtt.Types.INT, 2, array);}}.apply(2))));
        
//#line 34
this.varB = ((x10.core.Rail)((new java.lang.Object() {final x10.core.Rail<java.lang.Integer> apply(int length) {int[] array = new int[length];for (int x$ = 0; x$ < length; x$++) {final int x = x$;array[x] = 0;}return new x10.core.Rail<java.lang.Integer>(x10.rtt.Types.INT, 2, array);}}.apply(2))));
        
//#line 35
this.varC = ((x10.core.Rail)((new java.lang.Object() {final x10.core.Rail<java.lang.Integer> apply(int length) {int[] array = new int[length];for (int x$ = 0; x$ < length; x$++) {final int x = x$;array[x] = 0;}return new x10.core.Rail<java.lang.Integer>(x10.rtt.Types.INT, 2, array);}}.apply(2))));
        
//#line 36
this.varD = ((x10.core.Rail)((new java.lang.Object() {final x10.core.Rail<java.lang.Integer> apply(int length) {int[] array = new int[length];for (int x$ = 0; x$ < length; x$++) {final int x = x$;array[x] = 0;}return new x10.core.Rail<java.lang.Integer>(x10.rtt.Types.INT, 2, array);}}.apply(2))));
        
//#line 37
this.varE = ((x10.core.Rail)((new java.lang.Object() {final x10.core.Rail<java.lang.Integer> apply(int length) {int[] array = new int[length];for (int x$ = 0; x$ < length; x$++) {final int x = x$;array[x] = 0;}return new x10.core.Rail<java.lang.Integer>(x10.rtt.Types.INT, 2, array);}}.apply(2))));
    }
    
    }
    