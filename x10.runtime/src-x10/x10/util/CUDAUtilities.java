package x10.util;


@x10.core.X10Generated public class CUDAUtilities extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, CUDAUtilities.class);
    
    public static final x10.rtt.RuntimeType<CUDAUtilities> $RTT = x10.rtt.NamedType.<CUDAUtilities> make(
    "x10.util.CUDAUtilities", /* base class */CUDAUtilities.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(CUDAUtilities $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + CUDAUtilities.class + " calling"); } 
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        CUDAUtilities $_obj = new CUDAUtilities((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        
    }
    
    // constructor just for allocation
    public CUDAUtilities(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
public static int
                                                                                                      autoBlocks$O(
                                                                                                      ){
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
return 8;
        }
        
        
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
public static int
                                                                                                      autoThreads$O(
                                                                                                      ){
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
return 1;
        }
        
        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
private static <$T>void
                                                                                                      initCUDAArray__0$1x10$util$CUDAUtilities$$T$2__1$1x10$util$CUDAUtilities$$T$2(
                                                                                                      final x10.rtt.Type $T,
                                                                                                      final x10.core.IndexedMemoryChunk<$T> local,
                                                                                                      final x10.core.RemoteIndexedMemoryChunk<$T> remote,
                                                                                                      final int numElements){
            {
                
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.lang.FinishState x10$__var12 =
                  x10.lang.Runtime.startFinish();
                
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
try {try {{
                    {
                        
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
x10.core.IndexedMemoryChunk.<$T>asyncCopy(local,((int)(0)),remote,((int)(0)),((int)(numElements)));
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                    
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                    
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
throw new x10.lang.Exception();
                }finally {{
                     
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var12)));
                 }}
                }
            }
        
        public static <$T>void
          initCUDAArray$P__0$1x10$util$CUDAUtilities$$T$2__1$1x10$util$CUDAUtilities$$T$2(
          final x10.rtt.Type $T,
          final x10.core.IndexedMemoryChunk<$T> local,
          final x10.core.RemoteIndexedMemoryChunk<$T> remote,
          final int numElements){
            x10.util.CUDAUtilities.<$T>initCUDAArray__0$1x10$util$CUDAUtilities$$T$2__1$1x10$util$CUDAUtilities$$T$2($T, ((x10.core.IndexedMemoryChunk)(local)),
                                                                                                                     ((x10.core.RemoteIndexedMemoryChunk)(remote)),
                                                                                                                     (int)(numElements));
        }
        
        
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
private static <$T>x10.array.RemoteArray<$T>
                                                                                                      makeCUDAArray__2$1x10$util$CUDAUtilities$$T$2(
                                                                                                      final x10.rtt.Type $T,
                                                                                                      final x10.lang.Place gpu,
                                                                                                      final int numElements,
                                                                                                      final x10.core.IndexedMemoryChunk<$T> init){
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final int t57885 =
              ((numElements) - (((int)(1))));
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.lang.IntRange t57886 =
              ((x10.lang.IntRange)(new x10.lang.IntRange((java.lang.System[]) null).x10$lang$IntRange$$init$S(((int)(0)), ((int)(t57885)))));
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.Region reg =
              ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t57886)))));
            {
                
            }
            
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.lang.UnsupportedOperationException t57887 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
            
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
throw t57887;
        }
        
        public static <$T>x10.array.RemoteArray<$T>
          makeCUDAArray$P__2$1x10$util$CUDAUtilities$$T$2(
          final x10.rtt.Type $T,
          final x10.lang.Place gpu,
          final int numElements,
          final x10.core.IndexedMemoryChunk<$T> init){
            return x10.util.CUDAUtilities.<$T>makeCUDAArray__2$1x10$util$CUDAUtilities$$T$2($T, ((x10.lang.Place)(gpu)),
                                                                                            (int)(numElements),
                                                                                            ((x10.core.IndexedMemoryChunk)(init)));
        }
        
        
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
public static <$T>x10.array.RemoteArray<$T>
                                                                                                      makeRemoteArray__2$1x10$util$CUDAUtilities$$T$2(
                                                                                                      final x10.rtt.Type $T,
                                                                                                      final x10.lang.Place place,
                                                                                                      final int numElements,
                                                                                                      final x10.array.Array<$T> init){
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final boolean t57899 =
              place.isCUDA$O();
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
if (t57899) {
                
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.core.IndexedMemoryChunk<$T> t57888 =
                  ((x10.core.IndexedMemoryChunk)(((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.Array<$T>)init).raw()))));
                
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.RemoteArray<$T> t57889 =
                  ((x10.array.RemoteArray)(x10.util.CUDAUtilities.<$T>makeCUDAArray__2$1x10$util$CUDAUtilities$$T$2($T, ((x10.lang.Place)(place)),
                                                                                                                    (int)(numElements),
                                                                                                                    ((x10.core.IndexedMemoryChunk)(t57888)))));
                
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
return t57889;
            } else {
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.RemoteArray<$T> t57781 =
                  ((x10.array.RemoteArray)(x10.lang.Runtime.<x10.array.RemoteArray<$T>>evalAt__1$1x10$lang$Runtime$$T$2$G(x10.rtt.ParameterizedType.make(x10.array.RemoteArray.$RTT, $T), ((x10.lang.Place)(place)),
                                                                                                                          ((x10.core.fun.Fun_0_0)(new x10.util.CUDAUtilities.$Closure$167<$T>($T, init,
                                                                                                                                                                                              numElements, (x10.util.CUDAUtilities.$Closure$167.__0$1x10$util$CUDAUtilities$$Closure$167$$T$2) null))))));
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.RemoteArray<$T> __desugarer__var__58__57876 =
                  ((x10.array.RemoteArray)(((x10.array.RemoteArray<$T>)
                                             t57781)));
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
x10.array.RemoteArray<$T> ret57877 =
                   null;
                
//#line 67 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.Region t57941 =
                  ((x10.array.Region)(((x10.array.RemoteArray<$T>)__desugarer__var__58__57876).
                                        region));
                
//#line 67 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final int t57942 =
                  t57941.
                    rank;
                
//#line 67 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
boolean t57943 =
                  ((int) t57942) ==
                ((int) 1);
                
//#line 67 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
if (t57943) {
                    
//#line 67 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.core.GlobalRef<x10.array.Array<$T>> t57944 =
                      ((x10.core.GlobalRef)(((x10.array.RemoteArray<$T>)__desugarer__var__58__57876).
                                              array));
                    
//#line 67 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.lang.Place t57945 =
                      ((x10.lang.Place)((t57944).home));
                    
//#line 67 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
t57943 = x10.rtt.Equality.equalsequals((t57945),(place));
                }
                
//#line 67 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final boolean t57946 =
                  t57943;
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final boolean t57947 =
                  !(t57946);
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
if (t57947) {
                    
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final boolean t57948 =
                      true;
                    
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
if (t57948) {
                        
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.lang.FailedDynamicCheckException t57949 =
                          new x10.lang.FailedDynamicCheckException("x10.array.RemoteArray[T]{self.region.rank==1, self.array.home==place}");
                        
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
throw t57949;
                    }
                }
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
ret57877 = ((x10.array.RemoteArray)(__desugarer__var__58__57876));
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.RemoteArray<$T> t57782 =
                  ((x10.array.RemoteArray)(ret57877));
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
return t57782;
            }
        }
        
        
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
public static <$T>x10.array.RemoteArray<$T>
                                                                                                      makeRemoteArray(
                                                                                                      final x10.rtt.Type $T,
                                                                                                      final x10.lang.Place place,
                                                                                                      final int numElements){
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final $T t57900 =
              (($T)(($T) x10.rtt.Types.zeroValue($T)));
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.RemoteArray<$T> t57901 =
              ((x10.array.RemoteArray)(x10.util.CUDAUtilities.<$T>makeRemoteArray__2x10$util$CUDAUtilities$$T($T, ((x10.lang.Place)(place)),
                                                                                                              (int)(numElements),
                                                                                                              (($T)(t57900)))));
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
return t57901;
        }
        
        
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
public static <$T>x10.array.RemoteArray<$T>
                                                                                                      makeRemoteArray__2x10$util$CUDAUtilities$$T(
                                                                                                      final x10.rtt.Type $T,
                                                                                                      final x10.lang.Place place,
                                                                                                      final int numElements,
                                                                                                      final $T init){
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final boolean t57919 =
              place.isCUDA$O();
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
if (t57919) {
                
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.core.IndexedMemoryChunk<$T> chunk =
                  x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(numElements)), false);
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final int t57956 =
                  ((numElements) - (((int)(1))));
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.lang.IntRange t57957 =
                  ((x10.lang.IntRange)(new x10.lang.IntRange((java.lang.System[]) null).x10$lang$IntRange$$init$S(((int)(0)), ((int)(t57956)))));
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.Region p57958 =
                  ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t57957)))));
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final int i57840min57959 =
                  p57958.min$O((int)(0));
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final int i57840max57960 =
                  p57958.max$O((int)(0));
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
int i57953 =
                  i57840min57959;
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
for (;
                                                                                                                 true;
                                                                                                                 ) {
                    
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final int t57954 =
                      i57953;
                    
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final boolean t57955 =
                      ((t57954) <= (((int)(i57840max57960))));
                    
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
if (!(t57955)) {
                        
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
break;
                    }
                    
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final int i57950 =
                      i57953;
                    
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
(((x10.core.IndexedMemoryChunk<$T>)(chunk))).$set(((int)(i57950)), init);
                    
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final int t57951 =
                      i57953;
                    
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final int t57952 =
                      ((t57951) + (((int)(1))));
                    
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
i57953 = t57952;
                }
                
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.RemoteArray<$T> t57909 =
                  ((x10.array.RemoteArray)(x10.util.CUDAUtilities.<$T>makeCUDAArray__2$1x10$util$CUDAUtilities$$T$2($T, ((x10.lang.Place)(place)),
                                                                                                                    (int)(numElements),
                                                                                                                    ((x10.core.IndexedMemoryChunk)(chunk)))));
                
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
return t57909;
            } else {
                
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.RemoteArray<$T> t57785 =
                  ((x10.array.RemoteArray)(x10.lang.Runtime.<x10.array.RemoteArray<$T>>evalAt__1$1x10$lang$Runtime$$T$2$G(x10.rtt.ParameterizedType.make(x10.array.RemoteArray.$RTT, $T), ((x10.lang.Place)(place)),
                                                                                                                          ((x10.core.fun.Fun_0_0)(new x10.util.CUDAUtilities.$Closure$168<$T>($T, numElements,
                                                                                                                                                                                              init, (x10.util.CUDAUtilities.$Closure$168.__1x10$util$CUDAUtilities$$Closure$168$$T) null))))));
                
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.RemoteArray<$T> __desugarer__var__59__57879 =
                  ((x10.array.RemoteArray)(((x10.array.RemoteArray<$T>)
                                             t57785)));
                
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
x10.array.RemoteArray<$T> ret57880 =
                   null;
                
//#line 85 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.Region t57961 =
                  ((x10.array.Region)(((x10.array.RemoteArray<$T>)__desugarer__var__59__57879).
                                        region));
                
//#line 85 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final int t57962 =
                  t57961.
                    rank;
                
//#line 85 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
boolean t57963 =
                  ((int) t57962) ==
                ((int) 1);
                
//#line 85 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
if (t57963) {
                    
//#line 85 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.core.GlobalRef<x10.array.Array<$T>> t57964 =
                      ((x10.core.GlobalRef)(((x10.array.RemoteArray<$T>)__desugarer__var__59__57879).
                                              array));
                    
//#line 85 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.lang.Place t57965 =
                      ((x10.lang.Place)((t57964).home));
                    
//#line 85 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
t57963 = x10.rtt.Equality.equalsequals((t57965),(place));
                }
                
//#line 85 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final boolean t57966 =
                  t57963;
                
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final boolean t57967 =
                  !(t57966);
                
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
if (t57967) {
                    
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final boolean t57968 =
                      true;
                    
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
if (t57968) {
                        
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.lang.FailedDynamicCheckException t57969 =
                          new x10.lang.FailedDynamicCheckException("x10.array.RemoteArray[T]{self.region.rank==1, self.array.home==place}");
                        
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
throw t57969;
                    }
                }
                
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
ret57880 = ((x10.array.RemoteArray)(__desugarer__var__59__57879));
                
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.RemoteArray<$T> t57786 =
                  ((x10.array.RemoteArray)(ret57880));
                
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
return t57786;
            }
        }
        
        
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
public static <$T>x10.array.RemoteArray<$T>
                                                                                                      makeRemoteArray__2$1x10$lang$Int$3x10$util$CUDAUtilities$$T$2(
                                                                                                      final x10.rtt.Type $T,
                                                                                                      final x10.lang.Place place,
                                                                                                      final int numElements,
                                                                                                      final x10.core.fun.Fun_0_1<x10.core.Int,$T> init){
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final boolean t57938 =
              place.isCUDA$O();
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
if (t57938) {
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.core.IndexedMemoryChunk<$T> chunk =
                  x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(numElements)), false);
                
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final int t57977 =
                  ((numElements) - (((int)(1))));
                
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.lang.IntRange t57978 =
                  ((x10.lang.IntRange)(new x10.lang.IntRange((java.lang.System[]) null).x10$lang$IntRange$$init$S(((int)(0)), ((int)(t57977)))));
                
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.Region p57979 =
                  ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t57978)))));
                
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final int i57859min57980 =
                  p57979.min$O((int)(0));
                
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final int i57859max57981 =
                  p57979.max$O((int)(0));
                
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
int i57974 =
                  i57859min57980;
                
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
for (;
                                                                                                                 true;
                                                                                                                 ) {
                    
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final int t57975 =
                      i57974;
                    
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final boolean t57976 =
                      ((t57975) <= (((int)(i57859max57981))));
                    
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
if (!(t57976)) {
                        
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
break;
                    }
                    
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final int i57971 =
                      i57974;
                    
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final $T t57970 =
                      (($T)((($T)
                              ((x10.core.fun.Fun_0_1<x10.core.Int,$T>)init).$apply(x10.core.Int.$box(i57971),x10.rtt.Types.INT))));
                    
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
(((x10.core.IndexedMemoryChunk<$T>)(chunk))).$set(((int)(i57971)), t57970);
                    
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final int t57972 =
                      i57974;
                    
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final int t57973 =
                      ((t57972) + (((int)(1))));
                    
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
i57974 = t57973;
                }
                
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.RemoteArray<$T> t57928 =
                  ((x10.array.RemoteArray)(x10.util.CUDAUtilities.<$T>makeCUDAArray__2$1x10$util$CUDAUtilities$$T$2($T, ((x10.lang.Place)(place)),
                                                                                                                    (int)(numElements),
                                                                                                                    ((x10.core.IndexedMemoryChunk)(chunk)))));
                
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
return t57928;
            } else {
                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.RemoteArray<$T> t57791 =
                  ((x10.array.RemoteArray)(x10.lang.Runtime.<x10.array.RemoteArray<$T>>evalAt__1$1x10$lang$Runtime$$T$2$G(x10.rtt.ParameterizedType.make(x10.array.RemoteArray.$RTT, $T), ((x10.lang.Place)(place)),
                                                                                                                          ((x10.core.fun.Fun_0_0)(new x10.util.CUDAUtilities.$Closure$170<$T>($T, init,
                                                                                                                                                                                              numElements, (x10.util.CUDAUtilities.$Closure$170.__0$1x10$lang$Int$3x10$util$CUDAUtilities$$Closure$170$$T$2) null))))));
                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.RemoteArray<$T> __desugarer__var__60__57882 =
                  ((x10.array.RemoteArray)(((x10.array.RemoteArray<$T>)
                                             t57791)));
                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
x10.array.RemoteArray<$T> ret57883 =
                   null;
                
//#line 97 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.Region t57982 =
                  ((x10.array.Region)(((x10.array.RemoteArray<$T>)__desugarer__var__60__57882).
                                        region));
                
//#line 97 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final int t57983 =
                  t57982.
                    rank;
                
//#line 97 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
boolean t57984 =
                  ((int) t57983) ==
                ((int) 1);
                
//#line 97 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
if (t57984) {
                    
//#line 97 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.core.GlobalRef<x10.array.Array<$T>> t57985 =
                      ((x10.core.GlobalRef)(((x10.array.RemoteArray<$T>)__desugarer__var__60__57882).
                                              array));
                    
//#line 97 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.lang.Place t57986 =
                      ((x10.lang.Place)((t57985).home));
                    
//#line 97 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
t57984 = x10.rtt.Equality.equalsequals((t57986),(place));
                }
                
//#line 97 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final boolean t57987 =
                  t57984;
                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final boolean t57988 =
                  !(t57987);
                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
if (t57988) {
                    
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final boolean t57989 =
                      true;
                    
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
if (t57989) {
                        
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.lang.FailedDynamicCheckException t57990 =
                          new x10.lang.FailedDynamicCheckException("x10.array.RemoteArray[T]{self.region.rank==1, self.array.home==place}");
                        
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
throw t57990;
                    }
                }
                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
ret57883 = ((x10.array.RemoteArray)(__desugarer__var__60__57882));
                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.RemoteArray<$T> t57792 =
                  ((x10.array.RemoteArray)(ret57883));
                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
return t57792;
            }
        }
        
        
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
public static <$T>void
                                                                                                       deleteRemoteArray__0$1x10$util$CUDAUtilities$$T$2(
                                                                                                       final x10.rtt.Type $T,
                                                                                                       final x10.array.RemoteArray<$T> arr){
            
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.lang.Place place =
              ((x10.lang.Place)(((x10.array.RemoteArray<$T>)arr).home()));
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final boolean t57939 =
              place.isCUDA$O();
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
if (t57939) {
                {
                    
                }
            }
        }
        
        
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
public static int
                                                                                                       mul24$O(
                                                                                                       final int a,
                                                                                                       final int b){
            
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final int t57940 =
              ((a) * (((int)(b))));
            
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
return t57940;
        }
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final public x10.util.CUDAUtilities
                                                                                                      x10$util$CUDAUtilities$$x10$util$CUDAUtilities$this(
                                                                                                      ){
            
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
return x10.util.CUDAUtilities.this;
        }
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
// creation method for java code (1-phase java constructor)
        public CUDAUtilities(){this((java.lang.System[]) null);
                                   $init();}
        
        // constructor for non-virtual call
        final public x10.util.CUDAUtilities x10$util$CUDAUtilities$$init$S() { {
                                                                                      
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"

                                                                                      
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"

                                                                                  }
                                                                                  return this;
                                                                                  }
        
        // constructor
        public x10.util.CUDAUtilities $init(){return x10$util$CUDAUtilities$$init$S();}
        
        
        @x10.core.X10Generated public static class $Closure$166<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$166.class);
            
            public static final x10.rtt.RuntimeType<$Closure$166> $RTT = x10.rtt.StaticFunType.<$Closure$166> make(
            /* base class */$Closure$166.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$166 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$166.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.Array init = (x10.array.Array) $deserializer.readRef();
                $_obj.init = init;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$166 $_obj = new $Closure$166((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (init instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.init);
                } else {
                $serializer.write(this.init);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$166(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.util.CUDAUtilities.$Closure$166.$initParams(this, $T);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply$G(x10.core.Int.$unbox(a1));
            }
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$166 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public $T
                  $apply$G(
                  final int p){
                    
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final $T t57777 =
                      (($T)(((x10.array.Array<$T>)this.
                                                    init).$apply$G((int)(p))));
                    
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
return t57777;
                }
                
                public x10.array.Array<$T> init;
                
                public $Closure$166(final x10.rtt.Type $T,
                                    final x10.array.Array<$T> init, __0$1x10$util$CUDAUtilities$$Closure$166$$T$2 $dummy) {x10.util.CUDAUtilities.$Closure$166.$initParams(this, $T);
                                                                                                                                {
                                                                                                                                   this.init = ((x10.array.Array)(init));
                                                                                                                               }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$util$CUDAUtilities$$Closure$166$$T$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$167<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$167.class);
            
            public static final x10.rtt.RuntimeType<$Closure$167> $RTT = x10.rtt.StaticFunType.<$Closure$167> make(
            /* base class */$Closure$167.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.ParameterizedType.make(x10.array.RemoteArray.$RTT, x10.rtt.UnresolvedType.PARAM(0))), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$167 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$167.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.Array init = (x10.array.Array) $deserializer.readRef();
                $_obj.init = init;
                $_obj.numElements = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$167 $_obj = new $Closure$167((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (init instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.init);
                } else {
                $serializer.write(this.init);
                }
                $serializer.write(this.numElements);
                
            }
            
            // constructor just for allocation
            public $Closure$167(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.util.CUDAUtilities.$Closure$167.$initParams(this, $T);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.array.RemoteArray
              $apply$G(){return $apply();}
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$167 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public x10.array.RemoteArray<$T>
                  $apply(
                  ){
                    
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,$T> t57778 =
                      ((x10.core.fun.Fun_0_1)(new x10.util.CUDAUtilities.$Closure$166<$T>($T, ((x10.array.Array)(this.
                                                                                                                   init)), (x10.util.CUDAUtilities.$Closure$166.__0$1x10$util$CUDAUtilities$$Closure$166$$T$2) null)));
                    
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.Array<$T> t57779 =
                      ((x10.array.Array)(new x10.array.Array<$T>((java.lang.System[]) null, $T).$init(this.
                                                                                                        numElements,
                                                                                                      ((x10.core.fun.Fun_0_1)(t57778)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                    
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.RemoteArray<$T> t57780 =
                      ((x10.array.RemoteArray)(new x10.array.RemoteArray<$T>((java.lang.System[]) null, $T).$init(((x10.array.Array)(t57779)), (x10.array.RemoteArray.__0$1x10$array$RemoteArray$$T$2) null)));
                    
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
return t57780;
                }
                
                public x10.array.Array<$T> init;
                public int numElements;
                
                public $Closure$167(final x10.rtt.Type $T,
                                    final x10.array.Array<$T> init,
                                    final int numElements, __0$1x10$util$CUDAUtilities$$Closure$167$$T$2 $dummy) {x10.util.CUDAUtilities.$Closure$167.$initParams(this, $T);
                                                                                                                       {
                                                                                                                          this.init = ((x10.array.Array)(init));
                                                                                                                          this.numElements = numElements;
                                                                                                                      }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$util$CUDAUtilities$$Closure$167$$T$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$168<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$168.class);
            
            public static final x10.rtt.RuntimeType<$Closure$168> $RTT = x10.rtt.StaticFunType.<$Closure$168> make(
            /* base class */$Closure$168.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.ParameterizedType.make(x10.array.RemoteArray.$RTT, x10.rtt.UnresolvedType.PARAM(0))), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$168 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$168.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.numElements = $deserializer.readInt();
                $_obj.init = $deserializer.readRef();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$168 $_obj = new $Closure$168((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                $serializer.write(this.numElements);
                if (init instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.init);
                } else {
                $serializer.write(this.init);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$168(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.util.CUDAUtilities.$Closure$168.$initParams(this, $T);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.array.RemoteArray
              $apply$G(){return $apply();}
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$168 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public x10.array.RemoteArray<$T>
                  $apply(
                  ){
                    
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.Array<$T> t57783 =
                      ((x10.array.Array)(new x10.array.Array<$T>((java.lang.System[]) null, $T).$init(this.
                                                                                                        numElements,
                                                                                                      this.
                                                                                                        init, (x10.array.Array.__1x10$array$Array$$T) null)));
                    
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.RemoteArray<$T> t57784 =
                      ((x10.array.RemoteArray)(new x10.array.RemoteArray<$T>((java.lang.System[]) null, $T).$init(((x10.array.Array)(t57783)), (x10.array.RemoteArray.__0$1x10$array$RemoteArray$$T$2) null)));
                    
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
return t57784;
                }
                
                public int numElements;
                public $T init;
                
                public $Closure$168(final x10.rtt.Type $T,
                                    final int numElements,
                                    final $T init, __1x10$util$CUDAUtilities$$Closure$168$$T $dummy) {x10.util.CUDAUtilities.$Closure$168.$initParams(this, $T);
                                                                                                           {
                                                                                                              this.numElements = numElements;
                                                                                                              this.init = (($T)(init));
                                                                                                          }}
                // synthetic type for parameter mangling
                public abstract static class __1x10$util$CUDAUtilities$$Closure$168$$T {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$169<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$169.class);
            
            public static final x10.rtt.RuntimeType<$Closure$169> $RTT = x10.rtt.StaticFunType.<$Closure$169> make(
            /* base class */$Closure$169.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$169 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$169.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.core.fun.Fun_0_1 init = (x10.core.fun.Fun_0_1) $deserializer.readRef();
                $_obj.init = init;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$169 $_obj = new $Closure$169((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (init instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.init);
                } else {
                $serializer.write(this.init);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$169(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.util.CUDAUtilities.$Closure$169.$initParams(this, $T);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply$G(x10.core.Int.$unbox(a1));
            }
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$169 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public $T
                  $apply$G(
                  final int p){
                    
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final $T t57787 =
                      (($T)((($T)
                              ((x10.core.fun.Fun_0_1<x10.core.Int,$T>)this.
                                                                        init).$apply(x10.core.Int.$box(p),x10.rtt.Types.INT))));
                    
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
return t57787;
                }
                
                public x10.core.fun.Fun_0_1<x10.core.Int,$T> init;
                
                public $Closure$169(final x10.rtt.Type $T,
                                    final x10.core.fun.Fun_0_1<x10.core.Int,$T> init, __0$1x10$lang$Int$3x10$util$CUDAUtilities$$Closure$169$$T$2 $dummy) {x10.util.CUDAUtilities.$Closure$169.$initParams(this, $T);
                                                                                                                                                                {
                                                                                                                                                                   this.init = ((x10.core.fun.Fun_0_1)(init));
                                                                                                                                                               }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$Int$3x10$util$CUDAUtilities$$Closure$169$$T$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$170<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$170.class);
            
            public static final x10.rtt.RuntimeType<$Closure$170> $RTT = x10.rtt.StaticFunType.<$Closure$170> make(
            /* base class */$Closure$170.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.ParameterizedType.make(x10.array.RemoteArray.$RTT, x10.rtt.UnresolvedType.PARAM(0))), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$170 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$170.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.core.fun.Fun_0_1 init = (x10.core.fun.Fun_0_1) $deserializer.readRef();
                $_obj.init = init;
                $_obj.numElements = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$170 $_obj = new $Closure$170((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (init instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.init);
                } else {
                $serializer.write(this.init);
                }
                $serializer.write(this.numElements);
                
            }
            
            // constructor just for allocation
            public $Closure$170(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.util.CUDAUtilities.$Closure$170.$initParams(this, $T);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.array.RemoteArray
              $apply$G(){return $apply();}
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$170 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public x10.array.RemoteArray<$T>
                  $apply(
                  ){
                    
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,$T> t57788 =
                      ((x10.core.fun.Fun_0_1)(new x10.util.CUDAUtilities.$Closure$169<$T>($T, ((x10.core.fun.Fun_0_1)(this.
                                                                                                                        init)), (x10.util.CUDAUtilities.$Closure$169.__0$1x10$lang$Int$3x10$util$CUDAUtilities$$Closure$169$$T$2) null)));
                    
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.Array<$T> t57789 =
                      ((x10.array.Array)(new x10.array.Array<$T>((java.lang.System[]) null, $T).$init(this.
                                                                                                        numElements,
                                                                                                      ((x10.core.fun.Fun_0_1)(t57788)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                    
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
final x10.array.RemoteArray<$T> t57790 =
                      ((x10.array.RemoteArray)(new x10.array.RemoteArray<$T>((java.lang.System[]) null, $T).$init(((x10.array.Array)(t57789)), (x10.array.RemoteArray.__0$1x10$array$RemoteArray$$T$2) null)));
                    
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CUDAUtilities.x10"
return t57790;
                }
                
                public x10.core.fun.Fun_0_1<x10.core.Int,$T> init;
                public int numElements;
                
                public $Closure$170(final x10.rtt.Type $T,
                                    final x10.core.fun.Fun_0_1<x10.core.Int,$T> init,
                                    final int numElements, __0$1x10$lang$Int$3x10$util$CUDAUtilities$$Closure$170$$T$2 $dummy) {x10.util.CUDAUtilities.$Closure$170.$initParams(this, $T);
                                                                                                                                     {
                                                                                                                                        this.init = ((x10.core.fun.Fun_0_1)(init));
                                                                                                                                        this.numElements = numElements;
                                                                                                                                    }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$Int$3x10$util$CUDAUtilities$$Closure$170$$T$2 {}
                
            }
            
        
        }
        