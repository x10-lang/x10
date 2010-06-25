
public class ConditionalAtomicQueue
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ConditionalAtomicQueue>_RTT = new x10.rtt.RuntimeType<ConditionalAtomicQueue>(
/* base class */ConditionalAtomicQueue.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 24
final private int
      siz;
    
//#line 25
final private x10.core.Rail<ConditionalAtomicQueue.
      T>
      Q;
    
//#line 26
private int
      nelems;
    
//#line 27
private int
      tail;
    
//#line 29
private int
      head;
    
    
//#line 31
public ConditionalAtomicQueue() {
        
//#line 31
super();
        
//#line 26
this.nelems = 0;
        
//#line 27
this.tail = 0;
        
//#line 29
this.head = 0;
        
//#line 32
final int sz =
          3;
        
//#line 33
this.Q = ((x10.core.Rail)(x10.core.RailFactory.<ConditionalAtomicQueue.
          T>makeVarRail(ConditionalAtomicQueue.T._RTT, ((int)(sz)))));
        
//#line 34
this.siz = sz;
        
//#line 35
this.nelems = 0;
        
//#line 36
this.tail = 0;
        
//#line 37
this.head = 0;
    }
    
    
//#line 43
void
                  insert(
                  ConditionalAtomicQueue.
                    T i){
        
//#line 44
((Object[])Q.value)[tail] = i;
        
//#line 45
this.tail = ConditionalAtomicQueue.inc((int)(tail),
                                                           (int)(siz));
        
//#line 46
this.nelems += 1;
    }
    
    
//#line 52
ConditionalAtomicQueue.
                  T
                  remove(
                  ){
        
//#line 53
ConditionalAtomicQueue.
          T t =
          ((ConditionalAtomicQueue.
          T)((Object[])Q.value)[head]);
        
//#line 54
this.head = ConditionalAtomicQueue.inc((int)(head),
                                                           (int)(siz));
        
//#line 55
this.nelems -= 1;
        
//#line 56
return t;
    }
    
    
//#line 61
static int
                  inc(
                  int x,
                  int n){
        
//#line 62
int y =
          ((((int)(x))) + (((int)(1))));
        
//#line 63
return ((int) y) ==
        ((int) n)
          ? 0
          : y;
    }
    
    
//#line 69
boolean
                  empty(
                  ){
        
//#line 70
harness.
          x10Test.chk((boolean)(((((int)(nelems))) > (((int)(-1))))));
        
//#line 71
return ((((int)(nelems))) <= (((int)(0))));
    }
    
    
//#line 77
boolean
                  full(
                  ){
        
//#line 78
harness.
          x10Test.chk((boolean)(((((int)(nelems))) < (((int)(((((int)(siz))) + (((int)(1))))))))));
        
//#line 79
return ((((int)(nelems))) >= (((int)(siz))));
    }
    
    
//#line 82
public boolean
                  run(
                  ){
        
//#line 83
final int N =
          ConditionalAtomicQueue.
          T.N;
        
//#line 84
final int NP =
          x10.runtime.impl.java.Runtime.MAX_PLACES;
        
//#line 85
final x10.
          array.
          Dist D2 =
          ((x10.
          array.
          Dist)(ConditionalAtomicQueue.
          MyDist.val((int)(((((int)(N))) * (((int)(NP))))))));
        
//#line 86
final x10.
          array.
          DistArray<java.lang.Integer> received =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Integer>make(x10.rtt.Types.INT,
                                            D2)));
        
//#line 88
try {{
            
//#line 88
x10.
              lang.
              Runtime.startFinish();
            {
                
//#line 90
x10.
                  lang.
                  Runtime.runAsync(x10.lang.Place.place(x10.core.Ref.home(this)),
                                   new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                       {
                                           
//#line 91
final x10.
                                             array.
                                             Dist __desugarer__var__362__ =
                                             ((x10.
                                             array.
                                             Dist)(ConditionalAtomicQueue.
                                             MyDist.unique()));
                                           
//#line 91
/* template:forloop { */for (x10.core.Iterator __desugarer__var__363____ = (__desugarer__var__362__.places()).iterator(); __desugarer__var__363____.hasNext(); ) {
                                           	final  x10.
                                             lang.
                                             Place __desugarer__var__363__ = (x10.
                                             lang.
                                             Place) __desugarer__var__363____.next$G();
                                           	
{
                                               
//#line 91
x10.
                                                 lang.
                                                 Runtime.runAsync(__desugarer__var__363__,
                                                                  new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                      
//#line 91
/* template:forloop { */for (x10.core.Iterator id34024__ = (__desugarer__var__362__.restriction(x10.
                                                                                                                                                                                    lang.
                                                                                                                                                                                    Runtime.here())).iterator(); id34024__.hasNext(); ) {
                                                                      	final  x10.
                                                                        array.
                                                                        Point id34024 = (x10.
                                                                        array.
                                                                        Point) id34024__.next$G();
                                                                      	final int i =
                                                                        id34024.apply((int)(0));
{
                                                                          
//#line 91
x10.
                                                                            lang.
                                                                            Runtime.runAsync(x10.
                                                                                               lang.
                                                                                               Runtime.here(),
                                                                                             new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                                 {
                                                                                                     
//#line 92
final x10.
                                                                                                       array.
                                                                                                       Region p34123 =
                                                                                                       ((x10.
                                                                                                       array.
                                                                                                       Region)(x10.
                                                                                                       array.
                                                                                                       Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                                                                                  array.
                                                                                                                                  Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                                                                                                                  array.
                                                                                                                                  Region[] { x10.
                                                                                                                                  array.
                                                                                                                                  Region.makeRectangular((int)(0),
                                                                                                                                                         (int)(((((int)(N))) - (((int)(1)))))) })/* } */)));
                                                                                                     
//#line 92
final int j34124min34125 =
                                                                                                       p34123.min((int)(0));
                                                                                                     
//#line 92
final int j34124max34126 =
                                                                                                       p34123.max((int)(0));
                                                                                                     
//#line 92
for (
//#line 92
int j34124 =
                                                                                                                        j34124min34125;
                                                                                                                      ((((int)(j34124))) <= (((int)(j34124max34126))));
                                                                                                                      
//#line 92
j34124 += 1) {
                                                                                                         
//#line 92
final int j =
                                                                                                           j34124;
                                                                                                         {
                                                                                                             
//#line 93
final ConditionalAtomicQueue.
                                                                                                               T t =
                                                                                                               ((ConditionalAtomicQueue.
                                                                                                               T)(new ConditionalAtomicQueue.
                                                                                                               T(i,
                                                                                                                 j)));
                                                                                                             
//#line 94
x10.
                                                                                                               lang.
                                                                                                               Runtime.runAsync(x10.lang.Place.place(x10.core.Ref.home(ConditionalAtomicQueue.this)),
                                                                                                                                new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                                                                    
//#line 95
try {{
                                                                                                                                        
//#line 95
x10.
                                                                                                                                          lang.
                                                                                                                                          Runtime.lock();
                                                                                                                                        
//#line 95
while (true) {
                                                                                                                                            
//#line 95
if ((!(((boolean)(ConditionalAtomicQueue.this.full()))))) {
                                                                                                                                                {
                                                                                                                                                    
//#line 95
ConditionalAtomicQueue.this.insert(t);
                                                                                                                                                }
                                                                                                                                                
//#line 95
break;
                                                                                                                                            }
                                                                                                                                            
//#line 95
x10.
                                                                                                                                              lang.
                                                                                                                                              Runtime.await();
                                                                                                                                        }
                                                                                                                                    }}finally {{
                                                                                                                                          
//#line 95
x10.
                                                                                                                                            lang.
                                                                                                                                            Runtime.release();
                                                                                                                                      }}
                                                                                                                                    }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                                                                    });
                                                                                                             }
                                                                                                         }
                                                                                                     }
                                                                                                 }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                                 });
                                                                          }
                                                                          }/* } */
                                                                      }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                      });
                                               }
                                               }/* } */
                                           }
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                       });
                
//#line 100
x10.
                  lang.
                  Runtime.runAsync(x10.lang.Place.place(x10.core.Ref.home(this)),
                                   new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                       
//#line 101
for (
//#line 101
final x10.core.Iterator<x10.
                                                           array.
                                                           Point> p34127 =
                                                           D2.
                                                             region.iterator();
                                                         p34127.hasNext();
                                                         ) {
                                           
//#line 101
final x10.
                                             array.
                                             Point p =
                                             ((x10.
                                             array.
                                             Point)(p34127.next$G()));
                                           
//#line 102
x10.
                                             util.
                                             Box<ConditionalAtomicQueue.
                                             T> t;
                                           
//#line 103
try {{
                                               
//#line 103
x10.
                                                 lang.
                                                 Runtime.lock();
                                               
//#line 103
while (true) {
                                                   
//#line 103
if ((!(((boolean)(ConditionalAtomicQueue.this.empty()))))) {
                                                       {
                                                           
//#line 103
t = x10.
                                                             util.
                                                             Box.<ConditionalAtomicQueue.
                                                             T>$implicit_convert(ConditionalAtomicQueue.T._RTT,
                                                                                 ConditionalAtomicQueue.this.remove());
                                                       }
                                                       
//#line 103
break;
                                                   }
                                                   
//#line 103
x10.
                                                     lang.
                                                     Runtime.await();
                                               }
                                           }}finally {{
                                                 
//#line 103
x10.
                                                   lang.
                                                   Runtime.release();
                                             }}
                                           
//#line 104
final ConditionalAtomicQueue.
                                             T t1 =
                                             ((ConditionalAtomicQueue.
                                             T)(t.
                                                  value));
                                           
//#line 105
x10.
                                             lang.
                                             Runtime.runAsync(x10.lang.Place.place(x10.core.Ref.home(t1)),
                                                              new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                  
//#line 105
t1.consume();
                                                              }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                              });
                                           
//#line 106
final int m =
                                             x10.
                                             lang.
                                             Runtime.<java.lang.Integer>evalAt$G(x10.rtt.Types.INT,
                                                                                 x10.lang.Place.place(x10.core.Ref.home(t1)),
                                                                                 new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                                                 public final int apply() { {
                                                                                     
//#line 106
return t1.getval();
                                                                                 }}
                                                                                 public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                                                 }
                                                                                 });
                                           
//#line 107
new x10.core.fun.Fun_0_3<x10.
                                             array.
                                             DistArray<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                             array.
                                             DistArray<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer z) { return apply(x,(int)y0,(int)z);}
                                           public final int apply(final x10.
                                             array.
                                             DistArray<java.lang.Integer> x, final int y0, final int z) { {
                                               
//#line 107
return x.set$G((int)(((((int)(x.apply$G((int)(y0))))) + (((int)(z))))),
                                                                           (int)(y0));
                                           }}
                                           public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;return null;
                                           }
                                           }.apply(received,
                                                   m,
                                                   1);
                                           }
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                       });
                }
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
            java.lang.Throwable __desugarer__var__364__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 88
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__364__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__364__) {
                
//#line 88
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__364__);
            }finally {{
                 
//#line 88
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            
//#line 115
for (
//#line 115
final x10.core.Iterator<x10.
                                array.
                                Point> p34128 =
                                D2.
                                  region.iterator();
                              p34128.hasNext();
                              ) {
                
//#line 115
final x10.
                  array.
                  Point p =
                  ((x10.
                  array.
                  Point)(p34128.next$G()));
                
//#line 115
harness.
                  x10Test.chk((boolean)(((int) received.apply$G(p)) ==
                              ((int) 1)));
            }
            
//#line 118
harness.
              x10Test.chk((boolean)(this.empty()));
            
//#line 120
return true;
        }
        
        
//#line 123
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
        							ConditionalAtomicQueue.main(args);
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
            
//#line 124
new ConditionalAtomicQueue().execute();
        }/* } */
        
        
//#line 130
static class T
                     extends x10.core.Ref
                     {public static final x10.rtt.RuntimeType<ConditionalAtomicQueue.
          T>_RTT = new x10.rtt.RuntimeType<ConditionalAtomicQueue.
          T>(
        /* base class */ConditionalAtomicQueue.
          T.class
        , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
        );
        public x10.rtt.RuntimeType getRTT() {return _RTT;}
        
        
        
            
//#line 132
final public static int
              N =
              2;
            
//#line 134
int
              val;
            
            
//#line 136
T(int i,
                           int j) {
                
//#line 136
super();
                
//#line 134
this.val = 0;
                
//#line 137
this.val = ((((int)(((((int)(ConditionalAtomicQueue.
                  T.N))) * (((int)(i))))))) + (((int)(j))));
            }
            
            
//#line 140
public void
                           consume(
                           ){
                
            }
            
            
//#line 143
public int
                           getval(
                           ){
                
//#line 143
return val;
            }
        
        }
        
        
//#line 149
static class MyDist
                     extends x10.core.Ref
                     {public static final x10.rtt.RuntimeType<ConditionalAtomicQueue.
          MyDist>_RTT = new x10.rtt.RuntimeType<ConditionalAtomicQueue.
          MyDist>(
        /* base class */ConditionalAtomicQueue.
          MyDist.class
        , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
        );
        public x10.rtt.RuntimeType getRTT() {return _RTT;}
        
        
        
            
            
//#line 153
static x10.
                           array.
                           Dist
                           block(
                           int arraySize){
                
//#line 154
return x10.
                  array.
                  Dist.makeBlock(x10.
                                   array.
                                   Region.makeRectangular((int)(0),
                                                          (int)((((((int)(arraySize))) - (((int)(1))))))),
                                 (int)(0));
            }
            
            
//#line 159
static x10.
                           array.
                           Dist
                           unique(
                           ){
                
//#line 160
return x10.
                  array.
                  Dist.makeUnique(x10.core.RailFactory.<x10.
                                    lang.
                                    Place>makeRailFromValRail(x10.lang.Place._RTT, x10.
                                    lang.
                                    Place.places));
            }
            
            
//#line 166
static x10.
                           array.
                           Dist
                           val(
                           int arraySize){
                
//#line 167
return x10.
                  array.
                  Dist.makeConstant(x10.
                                      array.
                                      Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                 array.
                                                                 Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                                                 array.
                                                                 Region[] { x10.
                                                                 array.
                                                                 Region.makeRectangular((int)(0),
                                                                                        (int)((((((int)(arraySize))) - (((int)(1))))))) })/* } */),
                                    x10.
                                      lang.
                                      Runtime.here());
            }
            
            public MyDist() {
                super();
            }
        
        }
        
        
        }
        