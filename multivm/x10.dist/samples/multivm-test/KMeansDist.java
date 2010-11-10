
public class KMeansDist
extends x10.core.Ref
{
    public static final x10.rtt.RuntimeType<KMeansDist> _RTT = new x10.rtt.RuntimeType<KMeansDist>(
    /* base class */KMeansDist.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    ) {public String typeName() {return "KMeansDist";}};
    public x10.rtt.RuntimeType<?> getRTT() {return _RTT;}
    
    
    
        public static x10.
          array.
          Region
          initVal$points_region;
        final public static x10.core.atomic.AtomicBoolean
          initStatus$points_region =
          new x10.core.atomic.AtomicBoolean(false);
        
//#line 17
final public static int
          DIM =
          2;
        
//#line 17
final public static int
          CLUSTERS =
          4;
        
//#line 17
final public static int
          POINTS =
          2000;
        
//#line 17
final public static int
          ITERATIONS =
          50;
        
//#line 19
final public static x10.
          array.
          Region
          points_region =
          ((x10.
          array.
          Region)(KMeansDist.getInitialized$points_region()));
        
        
//#line 21
public static class Main extends x10.runtime.impl.java.Runtime {
        public static void main(java.lang.String[] args) {
        // start native runtime
        new Main().start(args);
        }
        
        // called by native runtime inside main x10 thread
        public void runtimeCallback(final x10.array.Array<java.lang.String> args) {
        // call the original app-main method
        KMeansDist.main(args);
        }
        }
        
        // the original app-main method
        public static void main(final x10.
          array.
          Array<java.lang.String> id$0)  {
            
//#line 22
final x10.
              lang.
              PlaceLocalHandle<x10.
              util.
              Random> rnd =
              x10.
              lang.
              PlaceLocalHandle.<x10.
              util.
              Random>make_1_$_x10$lang$PlaceLocalHandle_T_$(x10.util.Random._RTT,
                                                            ((x10.
                                                              array.
                                                              Dist)(x10.
                                                              array.
                                                              Dist.makeUnique())),
                                                            ((x10.core.fun.Fun_0_0)(new x10.core.fun.Fun_0_0<x10.
                                                              util.
                                                              Random>() {public final x10.
                                                              util.
                                                              Random apply$G() { return apply();}
                                                            public final x10.
                                                              util.
                                                              Random apply() { {
                                                                
//#line 22
return new x10.
                                                                  util.
                                                                  Random(((long)(((int)(0)))));
                                                            }}
                                                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.util.Random._RTT;return null;
                                                            }
                                                            })));
            
//#line 23
final x10.
              lang.
              PlaceLocalHandle<x10.
              array.
              Array<java.lang.Float>> local_curr_clusters =
              x10.
              lang.
              PlaceLocalHandle.<x10.
              array.
              Array<java.lang.Float>>make_1_$_x10$lang$PlaceLocalHandle_T_$(new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.FLOAT),
                                                                            ((x10.
                                                                              array.
                                                                              Dist)(x10.
                                                                              array.
                                                                              Dist.makeUnique())),
                                                                            ((x10.core.fun.Fun_0_0)(new x10.core.fun.Fun_0_0<x10.
                                                                              array.
                                                                              Array<java.lang.Float>>() {public final x10.
                                                                              array.
                                                                              Array<java.lang.Float> apply$G() { return apply();}
                                                                            public final x10.
                                                                              array.
                                                                              Array<java.lang.Float> apply() { {
                                                                                
//#line 24
return new x10.
                                                                                  array.
                                                                                  Array<java.lang.Float>(x10.rtt.Types.FLOAT,
                                                                                                         ((((int)(KMeansDist.CLUSTERS))) * (((int)(KMeansDist.DIM)))));
                                                                            }}
                                                                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.FLOAT);return null;
                                                                            }
                                                                            })));
            
//#line 25
final x10.
              lang.
              PlaceLocalHandle<x10.
              array.
              Array<java.lang.Float>> local_new_clusters =
              x10.
              lang.
              PlaceLocalHandle.<x10.
              array.
              Array<java.lang.Float>>make_1_$_x10$lang$PlaceLocalHandle_T_$(new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.FLOAT),
                                                                            ((x10.
                                                                              array.
                                                                              Dist)(x10.
                                                                              array.
                                                                              Dist.makeUnique())),
                                                                            ((x10.core.fun.Fun_0_0)(new x10.core.fun.Fun_0_0<x10.
                                                                              array.
                                                                              Array<java.lang.Float>>() {public final x10.
                                                                              array.
                                                                              Array<java.lang.Float> apply$G() { return apply();}
                                                                            public final x10.
                                                                              array.
                                                                              Array<java.lang.Float> apply() { {
                                                                                
//#line 26
return new x10.
                                                                                  array.
                                                                                  Array<java.lang.Float>(x10.rtt.Types.FLOAT,
                                                                                                         ((((int)(KMeansDist.CLUSTERS))) * (((int)(KMeansDist.DIM)))));
                                                                            }}
                                                                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.FLOAT);return null;
                                                                            }
                                                                            })));
            
//#line 27
final x10.
              lang.
              PlaceLocalHandle<x10.
              array.
              Array<java.lang.Integer>> local_cluster_counts =
              x10.
              lang.
              PlaceLocalHandle.<x10.
              array.
              Array<java.lang.Integer>>make_1_$_x10$lang$PlaceLocalHandle_T_$(new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.INT),
                                                                              ((x10.
                                                                                array.
                                                                                Dist)(x10.
                                                                                array.
                                                                                Dist.makeUnique())),
                                                                              ((x10.core.fun.Fun_0_0)(new x10.core.fun.Fun_0_0<x10.
                                                                                array.
                                                                                Array<java.lang.Integer>>() {public final x10.
                                                                                array.
                                                                                Array<java.lang.Integer> apply$G() { return apply();}
                                                                              public final x10.
                                                                                array.
                                                                                Array<java.lang.Integer> apply() { {
                                                                                  
//#line 28
return new x10.
                                                                                    array.
                                                                                    Array<java.lang.Integer>(x10.rtt.Types.INT,
                                                                                                             KMeansDist.CLUSTERS);
                                                                              }}
                                                                              public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.INT);return null;
                                                                              }
                                                                              })));
            
//#line 30
final x10.
              array.
              Dist points_dist =
              ((x10.
              array.
              Dist)(x10.
              array.
              Dist.makeBlock(((x10.
                               array.
                               Region)(KMeansDist.points_region)),
                             (int)(0))));
            
//#line 31
final x10.
              array.
              DistArray<java.lang.Float> points =
              ((x10.
              array.
              DistArray)(x10.
              array.
              DistArray.<java.lang.Float>make_1_$_x10$array$Point_$_$_x10$array$DistArray_T_$(x10.rtt.Types.FLOAT,
                                                                                              ((x10.
                                                                                                array.
                                                                                                Dist)(points_dist)),
                                                                                              ((x10.core.fun.Fun_0_1)(new x10.core.fun.Fun_0_1<x10.
                                                                                                array.
                                                                                                Point, java.lang.Float>() {public final java.lang.Float apply(final x10.
                                                                                                array.
                                                                                                Point p, x10.rtt.Type t1) { return apply(p);}
                                                                                              public final float apply(final x10.
                                                                                                array.
                                                                                                Point p) { {
                                                                                                  
//#line 31
return ((x10.
                                                                                                    lang.
                                                                                                    PlaceLocalHandle<x10.
                                                                                                    util.
                                                                                                    Random>)(rnd)).apply$G().nextFloat();
                                                                                              }}
                                                                                              public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.FLOAT;return null;
                                                                                              }
                                                                                              })))));
            
//#line 33
final x10.
              array.
              Array<java.lang.Float> central_clusters =
              ((x10.
              array.
              Array)(new x10.
              array.
              Array<java.lang.Float>(x10.rtt.Types.FLOAT,
                                     ((((int)(KMeansDist.CLUSTERS))) * (((int)(KMeansDist.DIM)))),
                                     new x10.core.fun.Fun_0_1<java.lang.Integer, java.lang.Float>() {public final java.lang.Float apply(final java.lang.Integer i, x10.rtt.Type t1) { return apply((int)i);}
                                     public final float apply(final int i) { {
                                         
//#line 34
final x10.
                                           array.
                                           Point p =
                                           ((x10.
                                           array.
                                           Point)(x10.
                                           array.
                                           Point.make_0_$_x10$lang$Int_$(((x10.
                                                                           array.
                                                                           Array)(x10.core.RailFactory.<java.lang.Integer>makeArrayFromJavaArray(x10.rtt.Types.INT, new int[] {((((int)(i))) / (((int)(KMeansDist.DIM)))), ((((int)(i))) % (((int)(KMeansDist.DIM))))}))))));
                                         
//#line 35
final x10.
                                           lang.
                                           Place t81 =
                                           points_dist.apply(((x10.
                                                               array.
                                                               Point)(p)));
                                         
//#line 35
final float t82 =
                                           ((float)(x10.
                                           lang.
                                           Runtime.<java.lang.Float>evalAt_1_$_x10$lang$Runtime_T_$$G(x10.rtt.Types.FLOAT,
                                                                                                      ((x10.
                                                                                                        lang.
                                                                                                        Place)(t81)),
                                                                                                      ((x10.core.fun.Fun_0_0)(new x10.core.fun.Fun_0_0<java.lang.Float>() {public final java.lang.Float apply$G() { return apply();}
                                                                                                      public final float apply() { {
                                                                                                          
//#line 35
final float t80 =
                                                                                                            ((float)(((x10.
                                                                                                            array.
                                                                                                            DistArray<java.lang.Float>)(points)).apply$G(((x10.
                                                                                                                                                           array.
                                                                                                                                                           Point)(p)))));
                                                                                                          
//#line 35
return t80;
                                                                                                      }}
                                                                                                      public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.FLOAT;return null;
                                                                                                      }
                                                                                                      })))));
                                         
//#line 35
return t82;
                                     }}
                                     public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;if (i ==1) return x10.rtt.Types.FLOAT;return null;
                                     }
                                     },(java.lang.Class[][]) null)));
            
//#line 38
final x10.
              array.
              Array<java.lang.Float> old_central_clusters =
              ((x10.
              array.
              Array)(new x10.
              array.
              Array<java.lang.Float>(x10.rtt.Types.FLOAT,
                                     ((((int)(KMeansDist.CLUSTERS))) * (((int)(KMeansDist.DIM)))))));
            
//#line 40
final x10.
              array.
              Array<java.lang.Integer> central_cluster_counts =
              ((x10.
              array.
              Array)(new x10.
              array.
              Array<java.lang.Integer>(x10.rtt.Types.INT,
                                       KMeansDist.CLUSTERS)));
            {
                
//#line 42
final int i89min90 =
                  1;
                
//#line 42
final int i89max91 =
                  KMeansDist.ITERATIONS;
                
//#line 42
for (
//#line 42
int i89 =
                                   i89min90;
                                 ((((int)(i89))) <= (((int)(i89max91))));
                                 
//#line 42
i89 = ((((int)(i89))) + (((int)(1))))) {
                    
//#line 42
final x10.
                      array.
                      Point i =
                      ((x10.
                      array.
                      Point)(x10.
                      array.
                      Point.make((int)(i89))));
                    {
                        
//#line 44
x10.
                          io.
                          Console.OUT.println(((java.lang.Object)((("Iteration: ".toString()) + (i)))));
                        
//#line 46
for (
//#line 46
int j =
                                           0;
                                         ((((int)(j))) < (((int)(KMeansDist.CLUSTERS))));
                                         
//#line 46
j = ((((int)(j))) + (((int)(1))))) {
                            
//#line 47
((x10.
                              array.
                              Array<java.lang.Integer>)(((x10.
                              lang.
                              PlaceLocalHandle<x10.
                              array.
                              Array<java.lang.Integer>>)(local_cluster_counts)).apply$G())).set_0_$$x10$array$Array_T$G((java.lang.Integer)(0),
                                                                                                                        (int)(j));
                        }
                        {
                            
//#line 50
x10.
                              lang.
                              Runtime.ensureNotInAtomic();
                            
//#line 50
final x10.
                              lang.
                              FinishState x10$__var0 =
                              x10.
                              lang.
                              Runtime.startFinish();
                            
//#line 50
try {try {{
                                {
                                    
//#line 52
for (
//#line 52
final x10.
                                                       lang.
                                                       Iterator<x10.
                                                       lang.
                                                       Place> d85 =
                                                       ((x10.
                                                         lang.
                                                         Iterator<x10.
                                                         lang.
                                                         Place>)(x10.
                                                         lang.
                                                         Iterator)
                                                         (((x10.
                                                         lang.
                                                         Iterable<x10.
                                                         lang.
                                                         Place>)(points_dist.places()))).iterator());
                                                     ((x10.
                                                       lang.
                                                       Iterator<x10.
                                                       lang.
                                                       Place>)(d85)).hasNext();
                                                     ) {
                                        
//#line 52
final x10.
                                          lang.
                                          Place d =
                                          ((x10.
                                          lang.
                                          Iterator<x10.
                                          lang.
                                          Place>)(d85)).next$G();
                                        
//#line 52
x10.
                                          lang.
                                          Runtime.runAsync(((x10.
                                                             lang.
                                                             Place)(d)),
                                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                               
//#line 53
for (
//#line 53
int j =
                                                                                  0;
                                                                                ((((int)(j))) < (((int)(((((int)(KMeansDist.DIM))) * (((int)(KMeansDist.CLUSTERS))))))));
                                                                                
//#line 53
j = ((((int)(j))) + (((int)(1))))) {
                                                                   
//#line 54
((x10.
                                                                     array.
                                                                     Array<java.lang.Float>)(((x10.
                                                                     lang.
                                                                     PlaceLocalHandle<x10.
                                                                     array.
                                                                     Array<java.lang.Float>>)(local_curr_clusters)).apply$G())).set_0_$$x10$array$Array_T$G((java.lang.Float)(((x10.
                                                                                                                                                              array.
                                                                                                                                                              Array<java.lang.Float>)(central_clusters)).apply$G((int)(j))),
                                                                                                                                                            (int)(j));
                                                                   
//#line 55
((x10.
                                                                     array.
                                                                     Array<java.lang.Float>)(((x10.
                                                                     lang.
                                                                     PlaceLocalHandle<x10.
                                                                     array.
                                                                     Array<java.lang.Float>>)(local_new_clusters)).apply$G())).set_0_$$x10$array$Array_T$G((java.lang.Float)(((float)(int)(((int)(0))))),
                                                                                                                                                           (int)(j));
                                                               }
                                                               
//#line 57
for (
//#line 57
int j =
                                                                                  0;
                                                                                ((((int)(j))) < (((int)(KMeansDist.CLUSTERS))));
                                                                                
//#line 57
j = ((((int)(j))) + (((int)(1))))) {
                                                                   
//#line 58
((x10.
                                                                     array.
                                                                     Array<java.lang.Integer>)(((x10.
                                                                     lang.
                                                                     PlaceLocalHandle<x10.
                                                                     array.
                                                                     Array<java.lang.Integer>>)(local_cluster_counts)).apply$G())).set_0_$$x10$array$Array_T$G((java.lang.Integer)(0),
                                                                                                                                                               (int)(j));
                                                               }
                                                           }}catch (x10.runtime.impl.java.X10WrappedThrowable ex) {x10.lang.Runtime.pushException(ex);}}
                                                           public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {return null;
                                                           }
                                                           });
                                    }
                                }
                            }}catch (x10.core.Throwable __t__) {throw __t__;}catch (java.lang.RuntimeException __e__) {throw x10.core.ThrowableUtilities.getCorrespondingX10Exception(__e__);}catch (java.lang.Error __e__) {throw x10.core.ThrowableUtilities.getCorrespondingX10Error(__e__);}}catch (x10.core.Throwable __desugarer__var__0__) {
                                
//#line 50
x10.
                                  lang.
                                  Runtime.pushException(((x10.core.Throwable)(__desugarer__var__0__)));
                                
//#line 50
throw new x10.
                                  lang.
                                  RuntimeException();
                            }finally {{
                                 
//#line 50
x10.
                                   lang.
                                   Runtime.stopFinish(((x10.
                                                        lang.
                                                        FinishState)(x10$__var0)));
                             }}
                            }
                        {
                            
//#line 63
x10.
                              lang.
                              Runtime.ensureNotInAtomic();
                            
//#line 63
final x10.
                              lang.
                              FinishState x10$__var1 =
                              x10.
                              lang.
                              Runtime.startFinish();
                            
//#line 63
try {try {{
                                {
                                    
//#line 65
for (
//#line 65
int p_ =
                                                       0;
                                                     ((((int)(p_))) < (((int)(KMeansDist.POINTS))));
                                                     
//#line 65
p_ = ((((int)(p_))) + (((int)(1))))) {
                                        
//#line 66
final int p =
                                          p_;
                                        
//#line 67
x10.
                                          lang.
                                          Runtime.runAsync(((x10.
                                                             lang.
                                                             Place)(points_dist.apply((int)(p),
                                                                                      (int)(0)))),
                                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                               
//#line 68
int closest =
                                                                 -1;
                                                               
//#line 69
float closest_dist =
                                                                 java.lang.Float.MAX_VALUE;
                                                               
//#line 70
for (
//#line 70
int k =
                                                                                  0;
                                                                                ((((int)(k))) < (((int)(KMeansDist.CLUSTERS))));
                                                                                
//#line 70
k = ((((int)(k))) + (((int)(1))))) {
                                                                   
//#line 71
float dist =
                                                                     ((float)(int)(((int)(0))));
                                                                   
//#line 72
for (
//#line 72
int d =
                                                                                      0;
                                                                                    ((((int)(d))) < (((int)(KMeansDist.DIM))));
                                                                                    
//#line 72
d = ((((int)(d))) + (((int)(1))))) {
                                                                       
//#line 73
final float tmp =
                                                                         ((((float)(((x10.
                                                                         array.
                                                                         DistArray<java.lang.Float>)(points)).apply$G(((x10.
                                                                                                                        array.
                                                                                                                        Point)(x10.
                                                                                                                        array.
                                                                                                                        Point.make((int)(p),
                                                                                                                                   (int)(d)))))))) - (((float)(((x10.
                                                                         array.
                                                                         Array<java.lang.Float>)(((x10.
                                                                         lang.
                                                                         PlaceLocalHandle<x10.
                                                                         array.
                                                                         Array<java.lang.Float>>)(local_curr_clusters)).apply$G())).apply$G((int)(((((int)(((((int)(k))) * (((int)(KMeansDist.DIM))))))) + (((int)(d))))))))));
                                                                       
//#line 74
dist = ((((float)(dist))) + (((float)(((((float)(tmp))) * (((float)(tmp))))))));
                                                                   }
                                                                   
//#line 76
if (((((float)(dist))) < (((float)(closest_dist))))) {
                                                                       
//#line 77
closest_dist = dist;
                                                                       
//#line 78
closest = k;
                                                                   }
                                                               }
                                                               
//#line 81
for (
//#line 81
int d =
                                                                                  0;
                                                                                ((((int)(d))) < (((int)(KMeansDist.DIM))));
                                                                                
//#line 81
d = ((((int)(d))) + (((int)(1))))) {
                                                                   
//#line 82
new x10.core.fun.Fun_0_3<x10.
                                                                     array.
                                                                     Array<java.lang.Float>, java.lang.Integer, java.lang.Float, java.lang.Float>() {public final java.lang.Float apply(final x10.
                                                                     array.
                                                                     Array<java.lang.Float> x, x10.rtt.Type t1,final java.lang.Integer y0, x10.rtt.Type t2,final java.lang.Float z, x10.rtt.Type t3) { return apply(x,(int)y0,(float)z);}
                                                                   public final float apply(final x10.
                                                                     array.
                                                                     Array<java.lang.Float> x, final int y0, final float z) { {
                                                                       
//#line 82
return ((x10.
                                                                         array.
                                                                         Array<java.lang.Float>)(x)).set_0_$$x10$array$Array_T$G((java.lang.Float)(((((float)(((x10.
                                                                                                                                   array.
                                                                                                                                   Array<java.lang.Float>)(x)).apply$G((int)(y0))))) + (((float)(z))))),
                                                                                                                                 (int)(y0));
                                                                   }}
                                                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.FLOAT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.FLOAT;if (i ==3) return x10.rtt.Types.FLOAT;return null;
                                                                   }
                                                                   }.apply(((x10.
                                                                             lang.
                                                                             PlaceLocalHandle<x10.
                                                                             array.
                                                                             Array<java.lang.Float>>)(local_new_clusters)).apply$G(),
                                                                           ((((int)(((((int)(closest))) * (((int)(KMeansDist.DIM))))))) + (((int)(d)))),
                                                                           ((x10.
                                                                             array.
                                                                             DistArray<java.lang.Float>)(points)).apply$G(((x10.
                                                                                                                            array.
                                                                                                                            Point)(x10.
                                                                                                                            array.
                                                                                                                            Point.make((int)(p),
                                                                                                                                       (int)(d))))));
                                                               }
                                                               
//#line 84
new x10.core.fun.Fun_0_3<x10.
                                                                 array.
                                                                 Array<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply(final x10.
                                                                 array.
                                                                 Array<java.lang.Integer> x, x10.rtt.Type t1,final java.lang.Integer y0, x10.rtt.Type t2,final java.lang.Integer z, x10.rtt.Type t3) { return apply(x,(int)y0,(int)z);}
                                                               public final int apply(final x10.
                                                                 array.
                                                                 Array<java.lang.Integer> x, final int y0, final int z) { {
                                                                   
//#line 84
return ((x10.
                                                                     array.
                                                                     Array<java.lang.Integer>)(x)).set_0_$$x10$array$Array_T$G((java.lang.Integer)(((((int)(((x10.
                                                                                                                                 array.
                                                                                                                                 Array<java.lang.Integer>)(x)).apply$G((int)(y0))))) + (((int)(z))))),
                                                                                                                               (int)(y0));
                                                               }}
                                                               public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;return null;
                                                               }
                                                               }.apply(((x10.
                                                                         lang.
                                                                         PlaceLocalHandle<x10.
                                                                         array.
                                                                         Array<java.lang.Integer>>)(local_cluster_counts)).apply$G(),
                                                                       closest,
                                                                       1);
                                                           }}catch (x10.runtime.impl.java.X10WrappedThrowable ex) {x10.lang.Runtime.pushException(ex);}}
                                                           public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {return null;
                                                           }
                                                           });
                                    }
                                }
                            }}catch (x10.core.Throwable __t__) {throw __t__;}catch (java.lang.RuntimeException __e__) {throw x10.core.ThrowableUtilities.getCorrespondingX10Exception(__e__);}catch (java.lang.Error __e__) {throw x10.core.ThrowableUtilities.getCorrespondingX10Error(__e__);}}catch (x10.core.Throwable __desugarer__var__1__) {
                                
//#line 63
x10.
                                  lang.
                                  Runtime.pushException(((x10.core.Throwable)(__desugarer__var__1__)));
                                
//#line 63
throw new x10.
                                  lang.
                                  RuntimeException();
                            }finally {{
                                 
//#line 63
x10.
                                   lang.
                                   Runtime.stopFinish(((x10.
                                                        lang.
                                                        FinishState)(x10$__var1)));
                             }}
                            }
                        
//#line 90
for (
//#line 90
int j =
                                           0;
                                         ((((int)(j))) < (((int)(((((int)(KMeansDist.DIM))) * (((int)(KMeansDist.CLUSTERS))))))));
                                         
//#line 90
j = ((((int)(j))) + (((int)(1))))) {
                            
//#line 91
((x10.
                              array.
                              Array<java.lang.Float>)(old_central_clusters)).set_0_$$x10$array$Array_T$G((java.lang.Float)(((x10.
                                                                                                           array.
                                                                                                           Array<java.lang.Float>)(central_clusters)).apply$G((int)(j))),
                                                                                                         (int)(j));
                            
//#line 92
((x10.
                              array.
                              Array<java.lang.Float>)(central_clusters)).set_0_$$x10$array$Array_T$G((java.lang.Float)(((float)(int)(((int)(0))))),
                                                                                                     (int)(j));
                        }
                        
//#line 95
for (
//#line 95
int j =
                                           0;
                                         ((((int)(j))) < (((int)(KMeansDist.CLUSTERS))));
                                         
//#line 95
j = ((((int)(j))) + (((int)(1))))) {
                            
//#line 96
((x10.
                              array.
                              Array<java.lang.Integer>)(central_cluster_counts)).set_0_$$x10$array$Array_T$G((java.lang.Integer)(0),
                                                                                                             (int)(j));
                        }
                        {
                            
//#line 99
x10.
                              lang.
                              Runtime.ensureNotInAtomic();
                            
//#line 99
final x10.
                              lang.
                              FinishState x10$__var2 =
                              x10.
                              lang.
                              Runtime.startFinish();
                            
//#line 99
try {try {{
                                {
                                    
//#line 100
final x10.core.GlobalRef<x10.
                                      array.
                                      Array<java.lang.Float>> central_clusters_gr =
                                      ((x10.core.GlobalRef)(new x10.core.GlobalRef<x10.
                                      array.
                                      Array<java.lang.Float>>(new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.FLOAT),
                                                              central_clusters,(java.lang.Class) null)));
                                    
//#line 101
final x10.core.GlobalRef<x10.
                                      array.
                                      Array<java.lang.Integer>> central_cluster_counts_gr =
                                      ((x10.core.GlobalRef)(new x10.core.GlobalRef<x10.
                                      array.
                                      Array<java.lang.Integer>>(new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.INT),
                                                                central_cluster_counts,(java.lang.Class) null)));
                                    
//#line 102
final x10.
                                      lang.
                                      Place there =
                                      ((x10.
                                      lang.
                                      Place)(x10.
                                      lang.
                                      Runtime.here()));
                                    
//#line 103
for (
//#line 103
final x10.
                                                        lang.
                                                        Iterator<x10.
                                                        lang.
                                                        Place> d87 =
                                                        ((x10.
                                                          lang.
                                                          Iterator<x10.
                                                          lang.
                                                          Place>)(x10.
                                                          lang.
                                                          Iterator)
                                                          (((x10.
                                                          lang.
                                                          Iterable<x10.
                                                          lang.
                                                          Place>)(points_dist.places()))).iterator());
                                                      ((x10.
                                                        lang.
                                                        Iterator<x10.
                                                        lang.
                                                        Place>)(d87)).hasNext();
                                                      ) {
                                        
//#line 103
final x10.
                                          lang.
                                          Place d =
                                          ((x10.
                                          lang.
                                          Iterator<x10.
                                          lang.
                                          Place>)(d87)).next$G();
                                        
//#line 103
x10.
                                          lang.
                                          Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                               
//#line 105
final x10.
                                                                 array.
                                                                 Array<java.lang.Float> tmp_new_clusters =
                                                                 ((x10.
                                                                 array.
                                                                 Array)(((x10.
                                                                 lang.
                                                                 PlaceLocalHandle<x10.
                                                                 array.
                                                                 Array<java.lang.Float>>)(local_new_clusters)).apply$G()));
                                                               
//#line 106
final x10.
                                                                 array.
                                                                 Array<java.lang.Integer> tmp_cluster_counts =
                                                                 ((x10.
                                                                 array.
                                                                 Array)(((x10.
                                                                 lang.
                                                                 PlaceLocalHandle<x10.
                                                                 array.
                                                                 Array<java.lang.Integer>>)(local_cluster_counts)).apply$G()));
                                                               
//#line 107
x10.
                                                                 lang.
                                                                 Runtime.runAt(((x10.
                                                                                 lang.
                                                                                 Place)(there)),
                                                                               ((x10.core.fun.VoidFun_0_0)(new x10.core.fun.VoidFun_0_0() {public final void apply() { {
                                                                                   
//#line 107
try {{
                                                                                       
//#line 107
x10.
                                                                                         lang.
                                                                                         Runtime.enterAtomic();
                                                                                       {
                                                                                           
//#line 108
for (
//#line 108
int j =
                                                                                                               0;
                                                                                                             ((((int)(j))) < (((int)(((((int)(KMeansDist.DIM))) * (((int)(KMeansDist.CLUSTERS))))))));
                                                                                                             
//#line 108
j = ((((int)(j))) + (((int)(1))))) {
                                                                                               
//#line 109
new x10.core.fun.Fun_0_3<x10.
                                                                                                 array.
                                                                                                 Array<java.lang.Float>, java.lang.Integer, java.lang.Float, java.lang.Float>() {public final java.lang.Float apply(final x10.
                                                                                                 array.
                                                                                                 Array<java.lang.Float> x, x10.rtt.Type t1,final java.lang.Integer y0, x10.rtt.Type t2,final java.lang.Float z, x10.rtt.Type t3) { return apply(x,(int)y0,(float)z);}
                                                                                               public final float apply(final x10.
                                                                                                 array.
                                                                                                 Array<java.lang.Float> x, final int y0, final float z) { {
                                                                                                   
//#line 109
return ((x10.
                                                                                                     array.
                                                                                                     Array<java.lang.Float>)(x)).set_0_$$x10$array$Array_T$G((java.lang.Float)(((((float)(((x10.
                                                                                                                                                               array.
                                                                                                                                                               Array<java.lang.Float>)(x)).apply$G((int)(y0))))) + (((float)(z))))),
                                                                                                                                                             (int)(y0));
                                                                                               }}
                                                                                               public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.FLOAT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.FLOAT;if (i ==3) return x10.rtt.Types.FLOAT;return null;
                                                                                               }
                                                                                               }.apply((((x10.core.GlobalRef<x10.
                                                                                                         array.
                                                                                                         Array<java.lang.Float>>)(central_clusters_gr))).apply$G(),
                                                                                                       j,
                                                                                                       ((x10.
                                                                                                         array.
                                                                                                         Array<java.lang.Float>)(tmp_new_clusters)).apply$G((int)(j)));
                                                                                           }
                                                                                           
//#line 111
for (
//#line 111
int j =
                                                                                                               0;
                                                                                                             ((((int)(j))) < (((int)(KMeansDist.CLUSTERS))));
                                                                                                             
//#line 111
j = ((((int)(j))) + (((int)(1))))) {
                                                                                               
//#line 112
new x10.core.fun.Fun_0_3<x10.
                                                                                                 array.
                                                                                                 Array<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply(final x10.
                                                                                                 array.
                                                                                                 Array<java.lang.Integer> x, x10.rtt.Type t1,final java.lang.Integer y0, x10.rtt.Type t2,final java.lang.Integer z, x10.rtt.Type t3) { return apply(x,(int)y0,(int)z);}
                                                                                               public final int apply(final x10.
                                                                                                 array.
                                                                                                 Array<java.lang.Integer> x, final int y0, final int z) { {
                                                                                                   
//#line 112
return ((x10.
                                                                                                     array.
                                                                                                     Array<java.lang.Integer>)(x)).set_0_$$x10$array$Array_T$G((java.lang.Integer)(((((int)(((x10.
                                                                                                                                                                 array.
                                                                                                                                                                 Array<java.lang.Integer>)(x)).apply$G((int)(y0))))) + (((int)(z))))),
                                                                                                                                                               (int)(y0));
                                                                                               }}
                                                                                               public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;return null;
                                                                                               }
                                                                                               }.apply((((x10.core.GlobalRef<x10.
                                                                                                         array.
                                                                                                         Array<java.lang.Integer>>)(central_cluster_counts_gr))).apply$G(),
                                                                                                       j,
                                                                                                       ((x10.
                                                                                                         array.
                                                                                                         Array<java.lang.Integer>)(tmp_cluster_counts)).apply$G((int)(j)));
                                                                                           }
                                                                                       }
                                                                                   }}finally {{
                                                                                         
//#line 107
x10.
                                                                                           lang.
                                                                                           Runtime.exitAtomic();
                                                                                     }}
                                                                                   }}
                                                                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {return null;
                                                                                   }
                                                                                   })));
                                                               }}catch (x10.runtime.impl.java.X10WrappedThrowable ex) {x10.lang.Runtime.pushException(ex);}}
                                                               public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {return null;
                                                               }
                                                               });
                                        }
                                    }
                                }}catch (x10.core.Throwable __t__) {throw __t__;}catch (java.lang.RuntimeException __e__) {throw x10.core.ThrowableUtilities.getCorrespondingX10Exception(__e__);}catch (java.lang.Error __e__) {throw x10.core.ThrowableUtilities.getCorrespondingX10Error(__e__);}}catch (x10.core.Throwable __desugarer__var__2__) {
                                    
//#line 99
x10.
                                      lang.
                                      Runtime.pushException(((x10.core.Throwable)(__desugarer__var__2__)));
                                    
//#line 99
throw new x10.
                                      lang.
                                      RuntimeException();
                                }finally {{
                                     
//#line 99
x10.
                                       lang.
                                       Runtime.stopFinish(((x10.
                                                            lang.
                                                            FinishState)(x10$__var2)));
                                 }}
                            }
                            
//#line 118
for (
//#line 118
int k =
                                                0;
                                              ((((int)(k))) < (((int)(KMeansDist.CLUSTERS))));
                                              
//#line 118
k = ((((int)(k))) + (((int)(1))))) {
                                
//#line 119
for (
//#line 119
int d =
                                                    0;
                                                  ((((int)(d))) < (((int)(KMeansDist.DIM))));
                                                  
//#line 119
d = ((((int)(d))) + (((int)(1))))) {
                                    
//#line 120
new x10.core.fun.Fun_0_3<x10.
                                      array.
                                      Array<java.lang.Float>, java.lang.Integer, java.lang.Float, java.lang.Float>() {public final java.lang.Float apply(final x10.
                                      array.
                                      Array<java.lang.Float> x, x10.rtt.Type t1,final java.lang.Integer y0, x10.rtt.Type t2,final java.lang.Float z, x10.rtt.Type t3) { return apply(x,(int)y0,(float)z);}
                                    public final float apply(final x10.
                                      array.
                                      Array<java.lang.Float> x, final int y0, final float z) { {
                                        
//#line 120
return ((x10.
                                          array.
                                          Array<java.lang.Float>)(x)).set_0_$$x10$array$Array_T$G((java.lang.Float)(((((float)(((x10.
                                                                                                    array.
                                                                                                    Array<java.lang.Float>)(x)).apply$G((int)(y0))))) / (((float)(z))))),
                                                                                                  (int)(y0));
                                    }}
                                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.FLOAT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.FLOAT;if (i ==3) return x10.rtt.Types.FLOAT;return null;
                                    }
                                    }.apply(central_clusters,
                                            ((((int)(((((int)(k))) * (((int)(KMeansDist.DIM))))))) + (((int)(d)))),
                                            ((float)(int)(((int)(((x10.
                                              array.
                                              Array<java.lang.Integer>)(central_cluster_counts)).apply$G((int)(k)))))));
                                }
                            }
                            
//#line 125
boolean b =
                              true;
                            
//#line 126
for (
//#line 126
int j =
                                                0;
                                              ((((int)(j))) < (((int)(((((int)(KMeansDist.CLUSTERS))) * (((int)(KMeansDist.DIM))))))));
                                              
//#line 126
j = ((((int)(j))) + (((int)(1))))) {
                                
//#line 127
if (((((double)(((double)(float)(((float)(x10.
                                                   lang.
                                                   Math.abs((float)(((((float)(((x10.
                                                              array.
                                                              Array<java.lang.Float>)(old_central_clusters)).apply$G((int)(j))))) - (((float)(((x10.
                                                              array.
                                                              Array<java.lang.Float>)(central_clusters)).apply$G((int)(j))))))))))))))) > (((double)(1.0E-4))))) {
                                    
//#line 128
b = false;
                                    
//#line 129
break;
                                }
                            }
                            
//#line 132
if (b) {
                                
//#line 132
break;
                            }
                        }
                        }
                    }
                
//#line 136
for (
//#line 136
int d =
                                    0;
                                  ((((int)(d))) < (((int)(KMeansDist.DIM))));
                                  
//#line 136
d = ((((int)(d))) + (((int)(1))))) {
                    
//#line 137
for (
//#line 137
int k =
                                        0;
                                      ((((int)(k))) < (((int)(KMeansDist.CLUSTERS))));
                                      
//#line 137
k = ((((int)(k))) + (((int)(1))))) {
                        
//#line 138
if (((((int)(k))) > (((int)(0))))) {
                            
//#line 139
x10.
                              io.
                              Console.OUT.print(((java.lang.String)(" ".toString())));
                        }
                        
//#line 140
x10.
                          io.
                          Console.OUT.print((float)(java.lang.Float)(((x10.
                                              array.
                                              Array<java.lang.Float>)(central_clusters)).apply$G((int)(((((int)(((((int)(k))) * (((int)(KMeansDist.DIM))))))) + (((int)(d))))))));
                    }
                    
//#line 142
x10.
                      io.
                      Console.OUT.println();
                }
                }
            
            
//#line 15
public KMeansDist() {
                
//#line 15
super();
                {
                    
                }
            }
            
            public static x10.
              array.
              Region
              getInitialized$points_region(
              ){
                if (!KMeansDist.initStatus$points_region.getAndSet((boolean)(true))) {
                    KMeansDist.initVal$points_region = ((x10.
                      array.
                      Region)((x10.
                                 array.
                                 Region.makeRectangular((int)(0),
                                                        (int)(((((int)(KMeansDist.POINTS))) - (((int)(1))))))).$times(((x10.
                                                                                                                        array.
                                                                                                                        Region)((x10.
                                                                                                                                   array.
                                                                                                                                   Region.makeRectangular((int)(0),
                                                                                                                                                          (int)(((((int)(KMeansDist.DIM))) - (((int)(1))))))))))));
                }
                return KMeansDist.initVal$points_region;
            }
            
            }
            