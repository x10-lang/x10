
public class ClockTest10a
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockTest10a>_RTT = new x10.rtt.RuntimeType<ClockTest10a>(
/* base class */ClockTest10a.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 35
final x10.core.Rail<java.lang.Integer>
      varA;
    
//#line 36
final x10.core.Rail<java.lang.Integer>
      varB;
    
//#line 37
final x10.core.Rail<java.lang.Integer>
      varC;
    
//#line 38
final x10.core.Rail<java.lang.Integer>
      varD;
    
//#line 39
final x10.core.Rail<java.lang.Integer>
      varE;
    
//#line 40
final public static int
      N =
      10;
    
//#line 41
final public static int
      pipeDepth =
      2;
    
    
//#line 43
static int
                  ph(
                  int x){
        
//#line 43
return ((((int)(x))) % (((int)(2))));
    }
    
    
//#line 45
public boolean
                  run(
                  ){
        
//#line 46
try {{
            
//#line 46
x10.
              lang.
              Runtime.startFinish();
            {
                
//#line 46
x10.
                  lang.
                  Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                       
//#line 47
final x10.
                                         lang.
                                         Clock a =
                                         x10.
                                         lang.
                                         Clock.make();
                                       
//#line 48
final x10.
                                         lang.
                                         Clock b =
                                         x10.
                                         lang.
                                         Clock.make();
                                       
//#line 49
final x10.
                                         lang.
                                         Clock c =
                                         x10.
                                         lang.
                                         Clock.make();
                                       
//#line 50
x10.
                                         lang.
                                         Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                            lang.
                                                            Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                            lang.
                                                            Clock[] { a })/* } */,
                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 50
ClockTest10a.this.taskA(a);
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                          });
                                       
//#line 51
x10.
                                         lang.
                                         Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                            lang.
                                                            Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                            lang.
                                                            Clock[] { a,b })/* } */,
                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 51
ClockTest10a.this.taskB(a,
                                                                                                  b);
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                          });
                                       
//#line 52
x10.
                                         lang.
                                         Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                            lang.
                                                            Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                            lang.
                                                            Clock[] { a,c })/* } */,
                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 52
ClockTest10a.this.taskC(a,
                                                                                                  c);
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                          });
                                       
//#line 53
x10.
                                         lang.
                                         Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                            lang.
                                                            Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                            lang.
                                                            Clock[] { b,c })/* } */,
                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 53
ClockTest10a.this.taskD(b,
                                                                                                  c);
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                          });
                                       
//#line 54
x10.
                                         lang.
                                         Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                            lang.
                                                            Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                            lang.
                                                            Clock[] { c })/* } */,
                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 54
ClockTest10a.this.taskE(c);
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                          });
                                   }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                   });
            }
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
        java.lang.Throwable __desugarer__var__454__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 46
x10.
              lang.
              Runtime.pushException(__desugarer__var__454__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__454__) {
            
//#line 46
x10.
              lang.
              Runtime.pushException(__desugarer__var__454__);
        }finally {{
             
//#line 46
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 56
return true;
        }
    
    
//#line 59
void
                  taskA(
                  final x10.
                    lang.
                    Clock a){
        {
            
//#line 60
final int k45169min45170 =
              1;
            
//#line 60
final int k45169max45171 =
              ClockTest10a.N;
            
//#line 60
for (
//#line 60
int k45169 =
                               k45169min45170;
                             ((((int)(k45169))) <= (((int)(k45169max45171))));
                             
//#line 60
k45169 += 1) {
                
//#line 60
final int k =
                  k45169;
                {
                    
//#line 61
((int[])varA.value)[ClockTest10a.ph((int)(k))] = k;
                    
//#line 62
x10.
                      io.
                      Console.OUT.println(((((k) + (" A producing "))) + (((int[])varA.value)[ClockTest10a.ph((int)(k))])));
                    
//#line 63
x10.
                      lang.
                      Runtime.next();
                }
            }
        }
    }
    
    
//#line 66
void
                  taskB(
                  final x10.
                    lang.
                    Clock a,
                  final x10.
                    lang.
                    Clock b){
        {
            
//#line 67
final int k45172min45173 =
              1;
            
//#line 67
final int k45172max45174 =
              ClockTest10a.N;
            
//#line 67
for (
//#line 67
int k45172 =
                               k45172min45173;
                             ((((int)(k45172))) <= (((int)(k45172max45174))));
                             
//#line 67
k45172 += 1) {
                
//#line 67
final int k =
                  k45172;
                {
                    
//#line 68
((int[])varB.value)[ClockTest10a.ph((int)(k))] = ((((int)(((int[])varA.value)[ClockTest10a.ph((int)(((((int)(k))) - (((int)(1))))))]))) + (((int)(((int[])varA.value)[ClockTest10a.ph((int)(((((int)(k))) - (((int)(1))))))]))));
                    
//#line 69
x10.
                      io.
                      Console.OUT.println(((((k) + (" B consuming oldA producing "))) + (((int[])varB.value)[ClockTest10a.ph((int)(k))])));
                    
//#line 70
x10.
                      lang.
                      Runtime.next();
                }
            }
        }
    }
    
    
//#line 73
void
                  taskC(
                  final x10.
                    lang.
                    Clock a,
                  final x10.
                    lang.
                    Clock c){
        {
            
//#line 74
final int k45175min45176 =
              1;
            
//#line 74
final int k45175max45177 =
              ClockTest10a.N;
            
//#line 74
for (
//#line 74
int k45175 =
                               k45175min45176;
                             ((((int)(k45175))) <= (((int)(k45175max45177))));
                             
//#line 74
k45175 += 1) {
                
//#line 74
final int k =
                  k45175;
                {
                    
//#line 75
((int[])varC.value)[ClockTest10a.ph((int)(k))] = ((((int)(((int[])varA.value)[ClockTest10a.ph((int)(((((int)(k))) - (((int)(1))))))]))) * (((int)(((int[])varA.value)[ClockTest10a.ph((int)(((((int)(k))) - (((int)(1))))))]))));
                    
//#line 76
x10.
                      io.
                      Console.OUT.println(((((k) + (" C consuming oldA producing "))) + (((int[])varC.value)[ClockTest10a.ph((int)(k))])));
                    
//#line 77
x10.
                      lang.
                      Runtime.next();
                }
            }
        }
    }
    
    
//#line 80
void
                  taskD(
                  final x10.
                    lang.
                    Clock b,
                  final x10.
                    lang.
                    Clock c){
        {
            
//#line 81
final int k45178min45179 =
              1;
            
//#line 81
final int k45178max45180 =
              ClockTest10a.N;
            
//#line 81
for (
//#line 81
int k45178 =
                               k45178min45179;
                             ((((int)(k45178))) <= (((int)(k45178max45180))));
                             
//#line 81
k45178 += 1) {
                
//#line 81
final int k =
                  k45178;
                {
                    
//#line 82
((int[])varD.value)[ClockTest10a.ph((int)(k))] = ((((int)(((((int)(((int[])varB.value)[ClockTest10a.ph((int)(((((int)(k))) - (((int)(1))))))]))) + (((int)(((int[])varC.value)[ClockTest10a.ph((int)(((((int)(k))) - (((int)(1))))))]))))))) + (((int)(10))));
                    
//#line 83
x10.
                      io.
                      Console.OUT.println(((((k) + (" D consuming oldC producing "))) + (((int[])varD.value)[ClockTest10a.ph((int)(k))])));
                    
//#line 84
int n =
                      ((((int)(k))) - (((int)(ClockTest10a.pipeDepth))));
                    
//#line 85
harness.
                      x10Test.chk((boolean)((!(((boolean)((((((int)(k))) > (((int)(ClockTest10a.pipeDepth))))))))) ||
                                  ((int) ((int[])varD.value)[ClockTest10a.ph((int)(k))]) ==
                                  ((int) ((((int)(((((int)(((((int)(n))) + (((int)(n))))))) + (((int)(((((int)(n))) * (((int)(n))))))))))) + (((int)(10)))))));
                    
//#line 86
x10.
                      lang.
                      Runtime.next();
                }
            }
        }
    }
    
    
//#line 89
void
                  taskE(
                  final x10.
                    lang.
                    Clock c){
        {
            
//#line 90
final int k45181min45182 =
              1;
            
//#line 90
final int k45181max45183 =
              ClockTest10a.N;
            
//#line 90
for (
//#line 90
int k45181 =
                               k45181min45182;
                             ((((int)(k45181))) <= (((int)(k45181max45183))));
                             
//#line 90
k45181 += 1) {
                
//#line 90
final int k =
                  k45181;
                {
                    
//#line 91
((int[])varE.value)[ClockTest10a.ph((int)(k))] = ((((int)(((int[])varC.value)[ClockTest10a.ph((int)(((((int)(k))) - (((int)(1))))))]))) * (((int)(7))));
                    
//#line 92
x10.
                      io.
                      Console.OUT.println(((((k) + (" E consuming oldC producing "))) + (((int[])varE.value)[ClockTest10a.ph((int)(k))])));
                    
//#line 93
int n =
                      ((((int)(k))) - (((int)(ClockTest10a.pipeDepth))));
                    
//#line 94
harness.
                      x10Test.chk((boolean)((!(((boolean)((((((int)(k))) > (((int)(ClockTest10a.pipeDepth))))))))) ||
                                  ((int) ((int[])varE.value)[ClockTest10a.ph((int)(k))]) ==
                                  ((int) ((((int)(((((int)(n))) * (((int)(n))))))) * (((int)(7)))))));
                    
//#line 95
x10.
                      lang.
                      Runtime.next();
                }
            }
        }
    }
    
    
//#line 99
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
    							ClockTest10a.main(args);
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
        
//#line 100
new ClockTest10a().execute();
    }/* } */
    
    
//#line 103
static class boxedInt
                 extends x10.core.Ref
                 {public static final x10.rtt.RuntimeType<ClockTest10a.
      boxedInt>_RTT = new x10.rtt.RuntimeType<ClockTest10a.
      boxedInt>(
    /* base class */ClockTest10a.
      boxedInt.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 104
int
          val;
        
        public boxedInt() {
            super();
            
//#line 104
this.val = 0;
        }
    
    }
    
    
    public ClockTest10a() {
        super();
        
//#line 35
this.varA = ((x10.core.Rail)((new java.lang.Object() {final x10.core.Rail<java.lang.Integer> apply(int length) {int[] array = new int[length];for (int x$ = 0; x$ < length; x$++) {final int x = x$;array[x] = 0;}return new x10.core.Rail<java.lang.Integer>(x10.rtt.Types.INT, 2, array);}}.apply(2))));
        
//#line 36
this.varB = ((x10.core.Rail)((new java.lang.Object() {final x10.core.Rail<java.lang.Integer> apply(int length) {int[] array = new int[length];for (int x$ = 0; x$ < length; x$++) {final int x = x$;array[x] = 0;}return new x10.core.Rail<java.lang.Integer>(x10.rtt.Types.INT, 2, array);}}.apply(2))));
        
//#line 37
this.varC = ((x10.core.Rail)((new java.lang.Object() {final x10.core.Rail<java.lang.Integer> apply(int length) {int[] array = new int[length];for (int x$ = 0; x$ < length; x$++) {final int x = x$;array[x] = 0;}return new x10.core.Rail<java.lang.Integer>(x10.rtt.Types.INT, 2, array);}}.apply(2))));
        
//#line 38
this.varD = ((x10.core.Rail)((new java.lang.Object() {final x10.core.Rail<java.lang.Integer> apply(int length) {int[] array = new int[length];for (int x$ = 0; x$ < length; x$++) {final int x = x$;array[x] = 0;}return new x10.core.Rail<java.lang.Integer>(x10.rtt.Types.INT, 2, array);}}.apply(2))));
        
//#line 39
this.varE = ((x10.core.Rail)((new java.lang.Object() {final x10.core.Rail<java.lang.Integer> apply(int length) {int[] array = new int[length];for (int x$ = 0; x$ < length; x$++) {final int x = x$;array[x] = 0;}return new x10.core.Rail<java.lang.Integer>(x10.rtt.Types.INT, 2, array);}}.apply(2))));
    }
    
    }
    