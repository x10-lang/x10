
public class NQueensDist
extends x10.core.Ref
{
    public static final x10.rtt.RuntimeType<NQueensDist> _RTT = new x10.rtt.RuntimeType<NQueensDist>(
    /* base class */NQueensDist.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    ) {public String typeName() {return "NQueensDist";}};
    public x10.rtt.RuntimeType<?> getRTT() {return _RTT;}
    
    
    
        
//#line 20
final public static x10.
          array.
          Array<java.lang.Integer>
          expectedSolutions =
          x10.core.RailFactory.<java.lang.Integer>makeArrayFromJavaArray(x10.rtt.Types.INT, new int[] {0, 1, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712, 365596, 2279184, 14772512});
        
//#line 23
public int
          N;
        
//#line 24
public int
          P;
        
//#line 25
public x10.
          array.
          DistArray<java.lang.Integer>
          results;
        
        
//#line 27
public NQueensDist(final int N,
                                       final int P) {
            super();
            {
                
            }
            
//#line 28
this.N = N;
            
//#line 29
this.P = P;
            
//#line 30
this.results = ((x10.
              array.
              DistArray)(x10.
              array.
              DistArray.<java.lang.Integer>make_1_$_x10$array$Point_$_$_x10$array$DistArray_T_$(x10.rtt.Types.INT,
                                                                                                ((x10.
                                                                                                  array.
                                                                                                  Dist)(x10.
                                                                                                  array.
                                                                                                  Dist.makeUnique())),
                                                                                                ((x10.core.fun.Fun_0_1)(new x10.core.fun.Fun_0_1<x10.
                                                                                                  array.
                                                                                                  Point, java.lang.Integer>() {public final java.lang.Integer apply(final x10.
                                                                                                  array.
                                                                                                  Point id$0, x10.rtt.Type t1) { return apply(id$0);}
                                                                                                public final int apply(final x10.
                                                                                                  array.
                                                                                                  Point id$0) { {
                                                                                                    
//#line 30
return 0;
                                                                                                }}
                                                                                                public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                                                                                }
                                                                                                })))));
        }
        
        
//#line 32
public void
                      start(
                      ){
            
//#line 33
new NQueensDist.
              Board(this).search();
        }
        
        
//#line 35
public int
                      run(
                      ){
            {
                
//#line 36
x10.
                  lang.
                  Runtime.ensureNotInAtomic();
                
//#line 36
final x10.
                  lang.
                  FinishState x10$__var0 =
                  x10.
                  lang.
                  Runtime.startFinish();
                
//#line 36
try {try {{
                    {
                        
//#line 36
this.start();
                    }
                }}catch (x10.core.Throwable __t__) {throw __t__;}catch (java.lang.RuntimeException __e__) {throw x10.core.ThrowableUtilities.getCorrespondingX10Exception(__e__);}catch (java.lang.Error __e__) {throw x10.core.ThrowableUtilities.getCorrespondingX10Error(__e__);}}catch (x10.core.Throwable __desugarer__var__0__) {
                    
//#line 36
x10.
                      lang.
                      Runtime.pushException(((x10.core.Throwable)(__desugarer__var__0__)));
                    
//#line 36
throw new x10.
                      lang.
                      RuntimeException();
                }finally {{
                     
//#line 36
x10.
                       lang.
                       Runtime.stopFinish(((x10.
                                            lang.
                                            FinishState)(x10$__var0)));
                 }}
                }
            
//#line 37
final int result =
              ((int)(((x10.
              array.
              DistArray<java.lang.Integer>)(results)).reduce_0_$_x10$array$DistArray_T_$_$_x10$array$DistArray_T_$_$_x10$array$DistArray_T_$_1_$$x10$array$DistArray_T$G(((x10.core.fun.Fun_0_2)(new x10.core.fun.Fun_0_2<java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply(final java.lang.Integer x, x10.rtt.Type t1,final java.lang.Integer y, x10.rtt.Type t2) { return apply((int)x,(int)y);}
                                                                                                                                                                         public final int apply(final int x, final int y) { {
                                                                                                                                                                             
//#line 37
return ((((int)(x))) + (((int)(y))));
                                                                                                                                                                         }}
                                                                                                                                                                         public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;return null;
                                                                                                                                                                         }
                                                                                                                                                                         })),
                                                                                                                                                                         (java.lang.Integer)(0))));
            
//#line 38
return result;
            }
        
        
//#line 44
public static x10.
                      array.
                      Array<x10.
                      array.
                      Region>
                      block(
                      final x10.
                        array.
                        Region R,
                      final int P){
            
//#line 45
assert ((((int)(P))) >= (((int)(0))));
            
//#line 46
final int low =
              ((java.lang.Integer)((x10.core.fun.Fun_0_1<java.lang.Integer,java.lang.Integer>)R.min()).apply(0,x10.rtt.Types.INT));
            
//#line 46
final int high =
              ((java.lang.Integer)((x10.core.fun.Fun_0_1<java.lang.Integer,java.lang.Integer>)R.max()).apply(0,x10.rtt.Types.INT));
            
//#line 46
final int count =
              ((((int)(((((int)(high))) - (((int)(low))))))) + (((int)(1))));
            
//#line 47
final int baseSize =
              ((((int)(count))) / (((int)(P))));
            
//#line 47
final int extra =
              ((((int)(count))) - (((int)(((((int)(baseSize))) * (((int)(P))))))));
            
//#line 48
return new x10.
              array.
              Array<x10.
              array.
              Region>(x10.array.Region._RTT,
                      P,
                      new x10.core.fun.Fun_0_1<java.lang.Integer, x10.
                        array.
                        Region>() {public final x10.
                        array.
                        Region apply(final java.lang.Integer i, x10.rtt.Type t1) { return apply((int)i);}
                      public final x10.
                        array.
                        Region apply(final int i) { {
                          
//#line 49
final int start =
                            ((((int)(((((int)(low))) + (((int)(((((int)(i))) * (((int)(baseSize))))))))))) + (((int)((((((int)(i))) < (((int)(extra))))
                                                                                                                        ? i
                                                                                                                        : extra)))));
                          
//#line 50
return x10.
                            array.
                            Region.makeRectangular((int)(start),
                                                   (int)(((((int)(((((int)(start))) + (((int)(baseSize))))))) + (((int)((((((int)(i))) < (((int)(extra))))
                                                                                                                           ? 0
                                                                                                                           : -1)))))));
                      }}
                      public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;if (i ==1) return x10.array.Region._RTT;return null;
                      }
                      },(java.lang.Class[][]) null);
        }
        
        
//#line 54
public static class Board
                    extends x10.core.Ref
                    {
            public static final x10.rtt.RuntimeType<Board> _RTT = new x10.rtt.RuntimeType<Board>(
            /* base class */Board.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
            ) {public String typeName() {return "NQueensDist.Board";}};
            public x10.rtt.RuntimeType<?> getRTT() {return _RTT;}
            
            
            
                
//#line 19
public NQueensDist
                  out$;
                
//#line 55
public x10.
                  array.
                  Array<java.lang.Integer>
                  q;
                
                
//#line 56
public Board(final NQueensDist out$) {
                    super();
                    
//#line 19
this.out$ = out$;
                    {
                        
                    }
                    
//#line 57
this.q = ((x10.
                      array.
                      Array)(new x10.
                      array.
                      Array<java.lang.Integer>(x10.rtt.Types.INT,
                                               0)));
                }
                
                
//#line 59
public Board(final NQueensDist out$,
                                         final x10.
                                           array.
                                           Array<java.lang.Integer> old,
                                         final int newItem,java.lang.Class $dummy0) {
                    super();
                    
//#line 19
this.out$ = out$;
                    {
                        
                    }
                    
//#line 60
final int n =
                      ((x10.
                        array.
                        Array<java.lang.Integer>)old).
                        size;
                    
//#line 61
this.q = ((x10.
                      array.
                      Array)(new x10.
                      array.
                      Array<java.lang.Integer>(x10.rtt.Types.INT,
                                               ((((int)(n))) + (((int)(1)))),
                                               new x10.core.fun.Fun_0_1<java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply(final java.lang.Integer i, x10.rtt.Type t1) { return apply((int)i);}
                                               public final int apply(final int i) { {
                                                   
//#line 61
return (((((int)(i))) < (((int)(n))))
                                                                         ? ((x10.
                                                                         array.
                                                                         Array<java.lang.Integer>)(old)).apply$G((int)(i))
                                                                         : newItem);
                                               }}
                                               public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;if (i ==1) return x10.rtt.Types.INT;return null;
                                               }
                                               },(java.lang.Class[][]) null)));
                }
                
                
//#line 63
public boolean
                              safe(
                              final int j){
                    
//#line 64
final int n =
                      ((x10.
                        array.
                        Array<java.lang.Integer>)q).
                        size;
                    {
                        
//#line 65
final int k123min124 =
                          0;
                        
//#line 65
final int k123max125 =
                          ((((int)(n))) - (((int)(1))));
                        
//#line 65
for (
//#line 65
int k123 =
                                           k123min124;
                                         ((((int)(k123))) <= (((int)(k123max125))));
                                         
//#line 65
k123 = ((((int)(k123))) + (((int)(1))))) {
                            
//#line 65
final int k =
                              k123;
                            {
                                
//#line 66
if (((int) j) ==
                                                ((int) ((x10.
                                                  array.
                                                  Array<java.lang.Integer>)(q)).apply$G((int)(k))) ||
                                                ((int) x10.
                                                  lang.
                                                  Math.abs((int)(((((int)(n))) - (((int)(k))))))) ==
                                                ((int) x10.
                                                  lang.
                                                  Math.abs((int)(((((int)(j))) - (((int)(((x10.
                                                             array.
                                                             Array<java.lang.Integer>)(q)).apply$G((int)(k)))))))))) {
                                    
//#line 67
return false;
                                }
                            }
                        }
                    }
                    
//#line 69
return true;
                }
                
                
//#line 74
public void
                              search(
                              final x10.
                                array.
                                Region R){
                    {
                        
//#line 75
final x10.
                          array.
                          Region p127 =
                          ((x10.
                          array.
                          Region)(R));
                        
//#line 75
final int k128min129 =
                          p127.min((int)(0));
                        
//#line 75
final int k128max130 =
                          p127.max((int)(0));
                        
//#line 75
for (
//#line 75
int k128 =
                                           k128min129;
                                         ((((int)(k128))) <= (((int)(k128max130))));
                                         
//#line 75
k128 = ((((int)(k128))) + (((int)(1))))) {
                            
//#line 75
final int k =
                              k128;
                            {
                                
//#line 76
if (this.safe((int)(k))) {
                                    
//#line 77
new NQueensDist.
                                      Board(this.
                                              out$,
                                            q,
                                            k,(java.lang.Class) null).search();
                                }
                            }
                        }
                    }
                }
                
                
//#line 80
public void
                              search(
                              ){
                    
//#line 81
if (((int) ((x10.
                                             array.
                                             Array<java.lang.Integer>)q).
                                             size) ==
                                    ((int) this.
                                             out$.
                                             N)) {
                        
//#line 82
try {{
                            
//#line 82
x10.
                              lang.
                              Runtime.enterAtomic();
                            {
                                
//#line 82
NQueensDist.
                                  Board.__$closure$apply$__135_0_$_x10$lang$Int_$(((x10.
                                                                                    array.
                                                                                    DistArray)(this.
                                                                                                 out$.
                                                                                                 results)),
                                                                                  (int)(x10.
                                                                                          lang.
                                                                                          Runtime.here().
                                                                                          id),
                                                                                  (int)(1));
                            }
                        }}finally {{
                              
//#line 82
x10.
                                lang.
                                Runtime.exitAtomic();
                          }}
                        
//#line 83
return;
                        }
                    
//#line 85
if (((int) ((x10.
                                             array.
                                             Array<java.lang.Integer>)q).
                                             size) ==
                                    ((int) 0)) {
                        
//#line 86
final x10.
                          array.
                          Array<x10.
                          array.
                          Region> R =
                          ((x10.
                          array.
                          Array)(NQueensDist.block(((x10.
                                                     array.
                                                     Region)(x10.
                                                     array.
                                                     Region.makeRectangular((int)(0),
                                                                            (int)(((((int)(this.
                                                                                             out$.
                                                                                             N))) - (((int)(1)))))))),
                                                   (int)(this.
                                                           out$.
                                                           P))));
                        {
                            
//#line 87
x10.
                              lang.
                              Runtime.ensureNotInAtomic();
                            
//#line 87
final x10.
                              array.
                              Dist __desugarer__var__1__ =
                              ((x10.
                              array.
                              Dist)(x10.
                              array.
                              Dist.makeUnique()));
                            
//#line 87
for (x10.lang.Iterator __desugarer__var__2____ = (__desugarer__var__1__.places()).iterator(); __desugarer__var__2____.hasNext(); ) { final  x10.
                              lang.
                              Place __desugarer__var__2__ = (x10.
                              lang.
                              Place) __desugarer__var__2____.next$G(); 
{
                                
//#line 89
x10.
                                  lang.
                                  Runtime.runAsync(((x10.
                                                     lang.
                                                     Place)(__desugarer__var__2__)),
                                                   new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                       
//#line 87
for (x10.lang.Iterator id3__ = (__desugarer__var__1__.restriction(((x10.
                                                                                                                                       lang.
                                                                                                                                       Place)(x10.
                                                                                                                                       lang.
                                                                                                                                       Runtime.here())))).iterator(); id3__.hasNext(); ) { final  x10.
                                                         array.
                                                         Point id3 = (x10.
                                                         array.
                                                         Point) id3__.next$G(); final int q =
                                                         id3.apply((int)(0));
{
                                                           
//#line 89
x10.
                                                             lang.
                                                             Runtime.runAsync(((x10.
                                                                                lang.
                                                                                Place)(x10.
                                                                                lang.
                                                                                Runtime.here())),
                                                                              new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                  
//#line 89
NQueensDist.
                                                                                    Board.this.search(((x10.
                                                                                                        array.
                                                                                                        Region)(((x10.
                                                                                                        array.
                                                                                                        Array<x10.
                                                                                                        array.
                                                                                                        Region>)(R)).apply$G((int)(q)))));
                                                                              }}catch (x10.runtime.impl.java.X10WrappedThrowable ex) {x10.lang.Runtime.pushException(ex);}}
                                                                              public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {return null;
                                                                              }
                                                                              });
                                                       } }
                                                   }}catch (x10.runtime.impl.java.X10WrappedThrowable ex) {x10.lang.Runtime.pushException(ex);}}
                                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {return null;
                                                   }
                                                   });
                            } }
                        }
                    } else {
                        
//#line 90
this.search(((x10.
                                                  array.
                                                  Region)(x10.
                                                  array.
                                                  Region.makeRectangular((int)(0),
                                                                         (int)(((((int)(this.
                                                                                          out$.
                                                                                          N))) - (((int)(1)))))))));
                    }
                    }
                
                final private static int
                  __$closure$apply$__135_0_$_x10$lang$Int_$(
                  final x10.
                    array.
                    DistArray<java.lang.Integer> x,
                  final int y0,
                  final int z){
                    
//#line 82
return ((x10.
                      array.
                      DistArray<java.lang.Integer>)(x)).set_0_$$x10$array$DistArray_T$G((java.lang.Integer)(((((int)(((x10.
                                                                                          array.
                                                                                          DistArray<java.lang.Integer>)(x)).apply$G((int)(y0))))) + (((int)(z))))),
                                                                                        (int)(y0));
                }
                
                final public static int
                  __$closure$apply$__135$P_0_$_x10$lang$Int_$(
                  final x10.
                    array.
                    DistArray<java.lang.Integer> x,
                  final int y0,
                  final int z){
                    return NQueensDist.
                      Board.__$closure$apply$__135_0_$_x10$lang$Int_$(((x10.
                                                                        array.
                                                                        DistArray)(x)),
                                                                      (int)(y0),
                                                                      (int)(z));
                }
                
            }
            
        
        
//#line 94
public static class Main extends x10.runtime.impl.java.Runtime {
        public static void main(java.lang.String[] args) {
        // start native runtime
        new Main().start(args);
        }
        
        // called by native runtime inside main x10 thread
        public void runtimeCallback(final x10.array.Array<java.lang.String> args) {
        // call the original app-main method
        NQueensDist.main(args);
        }
        }
        
        // the original app-main method
        public static void main(final x10.
          array.
          Array<java.lang.String> args)  {
            
//#line 95
final int n =
              ((((int)(((x10.
                         array.
                         Array<java.lang.String>)args).
                         size))) > (((int)(0))))
              ? new Object() { int eval(String s) { try { return java.lang.Integer.parseInt(s); } catch (java.lang.NumberFormatException e) { throw x10.core.ThrowableUtilities.getCorrespondingX10Exception(e); } } }.eval(((x10.
              array.
              Array<java.lang.String>)(args)).apply$G((int)(0)))
              : 8;
            
//#line 96
NQueensDist.println(((java.lang.String)((("N=".toString()) + (((java.lang.Integer)(n)))))));
            
//#line 99
final int P =
              x10.runtime.impl.java.Runtime.MAX_PLACES;
            
//#line 100
final NQueensDist nq =
              ((NQueensDist)(new NQueensDist(n,
                                             P)));
            
//#line 101
long start =
              (-(((long)(x10.
              lang.
              System.nanoTime()))));
            
//#line 102
final int answer =
              nq.run();
            
//#line 103
final boolean result =
              ((int) answer) ==
            ((int) ((x10.
              array.
              Array<java.lang.Integer>)(NQueensDist.expectedSolutions)).apply$G((int)(n)));
            
//#line 104
start = ((((long)(start))) + (((long)(x10.
              lang.
              System.nanoTime()))));
            
//#line 105
start = ((((long)(start))) / (((long)(((long)(((int)(1000000))))))));
            
//#line 106
NQueensDist.println(((java.lang.String)((((((((((((((((((((("NQueensPar ".toString()) + (((java.lang.Integer)(nq.
                                                                                                                                         N))))) + ("(P=".toString()))) + (((java.lang.Integer)(P))))) + (") has ".toString()))) + (((java.lang.Integer)(answer))))) + (" solutions".toString()))) + ((result
                                                                                                                                                                                                                                                                                                        ? " (ok).".toString()
                                                                                                                                                                                                                                                                                                        : " (wrong).".toString())))) + ("time=".toString()))) + (((java.lang.Long)(start))))) + ("ms".toString())))));
        }
        
        
//#line 111
public static void
                       println(
                       final java.lang.String s){
            {
                
//#line 111
x10.
                  io.
                  Console.OUT.println(((java.lang.Object)(s)));
                
//#line 111
return;
            }
        }
        
        }
        