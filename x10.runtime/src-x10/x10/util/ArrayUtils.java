package x10.util;

@x10.core.X10Generated public class ArrayUtils extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, ArrayUtils.class);
    
    public static final x10.rtt.RuntimeType<ArrayUtils> $RTT = x10.rtt.NamedType.<ArrayUtils> make(
    "x10.util.ArrayUtils", /* base class */ArrayUtils.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(ArrayUtils $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + ArrayUtils.class + " calling"); } 
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        ArrayUtils $_obj = new ArrayUtils((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        
    }
    
    // constructor just for allocation
    public ArrayUtils(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
public static <$T>void
                                                                                                   sort__0$1x10$util$ArrayUtils$$T$2__1$1x10$util$ArrayUtils$$T$3x10$util$ArrayUtils$$T$3x10$lang$Int$2(
                                                                                                   final x10.rtt.Type $T,
                                                                                                   final x10.array.Array<$T> a,
                                                                                                   final x10.core.fun.Fun_0_2<$T,$T,x10.core.Int> cmp){
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final x10.core.IndexedMemoryChunk<$T> t57683 =
              ((x10.core.IndexedMemoryChunk)(((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.Array<$T>)a).raw()))));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57682 =
              ((x10.array.Array<$T>)a).
                size;
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57684 =
              ((t57682) - (((int)(1))));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
x10.util.ArrayUtils.<$T>qsort__0$1x10$util$ArrayUtils$$T$2__3$1x10$util$ArrayUtils$$T$3x10$util$ArrayUtils$$T$3x10$lang$Int$2($T, ((x10.core.IndexedMemoryChunk)(t57683)),
                                                                                                                                                                                                                                   (int)(0),
                                                                                                                                                                                                                                   (int)(t57684),
                                                                                                                                                                                                                                   ((x10.core.fun.Fun_0_2)(cmp)));
        }
        
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
public static <$T>void
                                                                                                   sort__0$1x10$util$ArrayUtils$$T$2(
                                                                                                   final x10.rtt.Type $T,
                                                                                                   final x10.array.Array<$T> a){
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final x10.core.fun.Fun_0_2<$T,$T,x10.core.Int> t57686 =
              ((x10.core.fun.Fun_0_2)(new x10.util.ArrayUtils.$Closure$163<$T>($T)));
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
x10.util.ArrayUtils.<$T>sort__0$1x10$util$ArrayUtils$$T$2__1$1x10$util$ArrayUtils$$T$3x10$util$ArrayUtils$$T$3x10$lang$Int$2($T, ((x10.array.Array)(a)),
                                                                                                                                                                                                                                  ((x10.core.fun.Fun_0_2)(t57686)));
        }
        
        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
public static <$T>void
                                                                                                   qsort__0$1x10$util$ArrayUtils$$T$2__3$1x10$util$ArrayUtils$$T$3x10$util$ArrayUtils$$T$3x10$lang$Int$2(
                                                                                                   final x10.rtt.Type $T,
                                                                                                   final x10.core.IndexedMemoryChunk<$T> a,
                                                                                                   final int lo,
                                                                                                   final int hi,
                                                                                                   final x10.core.fun.Fun_0_2<$T,$T,x10.core.Int> cmp){
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final boolean t57687 =
              ((hi) <= (((int)(lo))));
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
if (t57687) {
                
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
return;
            }
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
int l =
              ((lo) - (((int)(1))));
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
int h =
              hi;
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
while (true) {
                
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
while (true) {
                    
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57688 =
                      l;
                    
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57689 =
                      ((t57688) + (((int)(1))));
                    
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57690 =
                      l = t57689;
                    
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final $T t57691 =
                      (($T)((((x10.core.IndexedMemoryChunk<$T>)(a))).$apply$G(((int)(t57690)))));
                    
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final $T t57692 =
                      (($T)((((x10.core.IndexedMemoryChunk<$T>)(a))).$apply$G(((int)(hi)))));
                    
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57693 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_2<$T,$T,x10.core.Int>)cmp).$apply(t57691,$T,
                                                                                                 t57692,$T));
                    
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final boolean t57694 =
                      ((t57693) < (((int)(0))));
                    
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
if (!(t57694)) {
                        
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
break;
                    }
                    
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
;
                }
                
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
while (true) {
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final $T t57698 =
                      (($T)((((x10.core.IndexedMemoryChunk<$T>)(a))).$apply$G(((int)(hi)))));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57695 =
                      h;
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57696 =
                      ((t57695) - (((int)(1))));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57697 =
                      h = t57696;
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final $T t57699 =
                      (($T)((((x10.core.IndexedMemoryChunk<$T>)(a))).$apply$G(((int)(t57697)))));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57700 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_2<$T,$T,x10.core.Int>)cmp).$apply(t57698,$T,
                                                                                                 t57699,$T));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
boolean t57702 =
                      ((t57700) < (((int)(0))));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
if (t57702) {
                        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57701 =
                          h;
                        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
t57702 = ((t57701) > (((int)(lo))));
                    }
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final boolean t57703 =
                      t57702;
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
if (!(t57703)) {
                        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
break;
                    }
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
;
                }
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57704 =
                  l;
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57705 =
                  h;
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final boolean t57706 =
                  ((t57704) >= (((int)(t57705))));
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
if (t57706) {
                    
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
break;
                }
                
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57707 =
                  l;
                
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57708 =
                  h;
                
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
x10.util.ArrayUtils.<$T>exch__0$1x10$util$ArrayUtils$$T$2($T, ((x10.core.IndexedMemoryChunk)(a)),
                                                                                                                                                                   (int)(t57707),
                                                                                                                                                                   (int)(t57708));
            }
            
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57709 =
              l;
            
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
x10.util.ArrayUtils.<$T>exch__0$1x10$util$ArrayUtils$$T$2($T, ((x10.core.IndexedMemoryChunk)(a)),
                                                                                                                                                               (int)(t57709),
                                                                                                                                                               (int)(hi));
            
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57710 =
              l;
            
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57711 =
              ((t57710) - (((int)(1))));
            
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
x10.util.ArrayUtils.<$T>qsort__0$1x10$util$ArrayUtils$$T$2__3$1x10$util$ArrayUtils$$T$3x10$util$ArrayUtils$$T$3x10$lang$Int$2($T, ((x10.core.IndexedMemoryChunk)(a)),
                                                                                                                                                                                                                                   (int)(lo),
                                                                                                                                                                                                                                   (int)(t57711),
                                                                                                                                                                                                                                   ((x10.core.fun.Fun_0_2)(cmp)));
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57712 =
              l;
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57713 =
              ((t57712) + (((int)(1))));
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
x10.util.ArrayUtils.<$T>qsort__0$1x10$util$ArrayUtils$$T$2__3$1x10$util$ArrayUtils$$T$3x10$util$ArrayUtils$$T$3x10$lang$Int$2($T, ((x10.core.IndexedMemoryChunk)(a)),
                                                                                                                                                                                                                                   (int)(t57713),
                                                                                                                                                                                                                                   (int)(hi),
                                                                                                                                                                                                                                   ((x10.core.fun.Fun_0_2)(cmp)));
        }
        
        
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
private static <$T>void
                                                                                                   exch__0$1x10$util$ArrayUtils$$T$2(
                                                                                                   final x10.rtt.Type $T,
                                                                                                   final x10.core.IndexedMemoryChunk<$T> a,
                                                                                                   final int i,
                                                                                                   final int j){
            
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final $T temp =
              (($T)((((x10.core.IndexedMemoryChunk<$T>)(a))).$apply$G(((int)(i)))));
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final $T t57714 =
              (($T)((((x10.core.IndexedMemoryChunk<$T>)(a))).$apply$G(((int)(j)))));
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
(((x10.core.IndexedMemoryChunk<$T>)(a))).$set(((int)(i)), t57714);
            
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
(((x10.core.IndexedMemoryChunk<$T>)(a))).$set(((int)(j)), temp);
        }
        
        public static <$T>void
          exch$P__0$1x10$util$ArrayUtils$$T$2(
          final x10.rtt.Type $T,
          final x10.core.IndexedMemoryChunk<$T> a,
          final int i,
          final int j){
            x10.util.ArrayUtils.<$T>exch__0$1x10$util$ArrayUtils$$T$2($T, ((x10.core.IndexedMemoryChunk)(a)),
                                                                      (int)(i),
                                                                      (int)(j));
        }
        
        
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
public static <$T>int
                                                                                                   binarySearch__0$1x10$util$ArrayUtils$$T$2__1x10$util$ArrayUtils$$T__2$1x10$util$ArrayUtils$$T$3x10$util$ArrayUtils$$T$3x10$lang$Int$2$O(
                                                                                                   final x10.rtt.Type $T,
                                                                                                   final x10.array.Array<$T> a,
                                                                                                   final $T key,
                                                                                                   final x10.core.fun.Fun_0_2<$T,$T,x10.core.Int> cmp){
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final x10.core.IndexedMemoryChunk<$T> t57715 =
              ((x10.core.IndexedMemoryChunk)(((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.Array<$T>)a).raw()))));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57716 =
              ((x10.array.Array<$T>)a).
                size;
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57717 =
              x10.util.ArrayUtils.<$T>binarySearch__0$1x10$util$ArrayUtils$$T$2__1x10$util$ArrayUtils$$T__4$1x10$util$ArrayUtils$$T$3x10$util$ArrayUtils$$T$3x10$lang$Int$2$O($T, ((x10.core.IndexedMemoryChunk)(t57715)),
                                                                                                                                                                              (($T)(key)),
                                                                                                                                                                              (int)(0),
                                                                                                                                                                              (int)(t57716),
                                                                                                                                                                              ((x10.core.fun.Fun_0_2)(cmp)));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
return t57717;
        }
        
        
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
public static <$T>int
                                                                                                   binarySearch__0$1x10$util$ArrayUtils$$T$2__1x10$util$ArrayUtils$$T$O(
                                                                                                   final x10.rtt.Type $T,
                                                                                                   final x10.array.Array<$T> a,
                                                                                                   final $T key){
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final x10.core.IndexedMemoryChunk<$T> t57719 =
              ((x10.core.IndexedMemoryChunk)(((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.Array<$T>)a).raw()))));
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57720 =
              ((x10.array.Array<$T>)a).
                size;
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final x10.core.fun.Fun_0_2<$T,$T,x10.core.Int> t57721 =
              ((x10.core.fun.Fun_0_2)(new x10.util.ArrayUtils.$Closure$164<$T>($T)));
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57722 =
              x10.util.ArrayUtils.<$T>binarySearch__0$1x10$util$ArrayUtils$$T$2__1x10$util$ArrayUtils$$T__4$1x10$util$ArrayUtils$$T$3x10$util$ArrayUtils$$T$3x10$lang$Int$2$O($T, ((x10.core.IndexedMemoryChunk)(t57719)),
                                                                                                                                                                              (($T)(key)),
                                                                                                                                                                              (int)(0),
                                                                                                                                                                              (int)(t57720),
                                                                                                                                                                              ((x10.core.fun.Fun_0_2)(t57721)));
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
return t57722;
        }
        
        
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
public static <$T>int
                                                                                                   binarySearch__0$1x10$util$ArrayUtils$$T$2__1x10$util$ArrayUtils$$T__4$1x10$util$ArrayUtils$$T$3x10$util$ArrayUtils$$T$3x10$lang$Int$2$O(
                                                                                                   final x10.rtt.Type $T,
                                                                                                   final x10.array.Array<$T> a,
                                                                                                   final $T key,
                                                                                                   final int min,
                                                                                                   final int max,
                                                                                                   final x10.core.fun.Fun_0_2<$T,$T,x10.core.Int> cmp){
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final x10.core.IndexedMemoryChunk<$T> t57723 =
              ((x10.core.IndexedMemoryChunk)(((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.Array<$T>)a).raw()))));
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57724 =
              ((x10.array.Array<$T>)a).
                size;
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57725 =
              x10.util.ArrayUtils.<$T>binarySearch__0$1x10$util$ArrayUtils$$T$2__1x10$util$ArrayUtils$$T__4$1x10$util$ArrayUtils$$T$3x10$util$ArrayUtils$$T$3x10$lang$Int$2$O($T, ((x10.core.IndexedMemoryChunk)(t57723)),
                                                                                                                                                                              (($T)(key)),
                                                                                                                                                                              (int)(0),
                                                                                                                                                                              (int)(t57724),
                                                                                                                                                                              ((x10.core.fun.Fun_0_2)(cmp)));
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
return t57725;
        }
        
        
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
public static <$T>int
                                                                                                   binarySearch__0$1x10$util$ArrayUtils$$T$2__1x10$util$ArrayUtils$$T$O(
                                                                                                   final x10.rtt.Type $T,
                                                                                                   final x10.array.Array<$T> a,
                                                                                                   final $T key,
                                                                                                   final int min,
                                                                                                   final int max){
            
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final x10.core.IndexedMemoryChunk<$T> t57727 =
              ((x10.core.IndexedMemoryChunk)(((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.Array<$T>)a).raw()))));
            
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57728 =
              ((x10.array.Array<$T>)a).
                size;
            
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final x10.core.fun.Fun_0_2<$T,$T,x10.core.Int> t57729 =
              ((x10.core.fun.Fun_0_2)(new x10.util.ArrayUtils.$Closure$165<$T>($T)));
            
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57730 =
              x10.util.ArrayUtils.<$T>binarySearch__0$1x10$util$ArrayUtils$$T$2__1x10$util$ArrayUtils$$T__4$1x10$util$ArrayUtils$$T$3x10$util$ArrayUtils$$T$3x10$lang$Int$2$O($T, ((x10.core.IndexedMemoryChunk)(t57727)),
                                                                                                                                                                              (($T)(key)),
                                                                                                                                                                              (int)(0),
                                                                                                                                                                              (int)(t57728),
                                                                                                                                                                              ((x10.core.fun.Fun_0_2)(t57729)));
            
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
return t57730;
        }
        
        
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
public static <$T>int
                                                                                                   binarySearch__0$1x10$util$ArrayUtils$$T$2__1x10$util$ArrayUtils$$T__4$1x10$util$ArrayUtils$$T$3x10$util$ArrayUtils$$T$3x10$lang$Int$2$O(
                                                                                                   final x10.rtt.Type $T,
                                                                                                   final x10.core.IndexedMemoryChunk<$T> a,
                                                                                                   final $T key,
                                                                                                   int min,
                                                                                                   int max,
                                                                                                   final x10.core.fun.Fun_0_2<$T,$T,x10.core.Int> cmp){
            
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
while (true) {
                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57731 =
                  min;
                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57732 =
                  max;
                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final boolean t57743 =
                  ((t57731) < (((int)(t57732))));
                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
if (!(t57743)) {
                    
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
break;
                }
                
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57753 =
                  min;
                
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57754 =
                  max;
                
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57755 =
                  ((t57753) + (((int)(t57754))));
                
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
int mid57756 =
                  ((t57755) / (((int)(2))));
                
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57757 =
                  mid57756;
                
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final $T t57758 =
                  (($T)((((x10.core.IndexedMemoryChunk<$T>)(a))).$apply$G(((int)(t57757)))));
                
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57759 =
                  x10.core.Int.$unbox(((x10.core.fun.Fun_0_2<$T,$T,x10.core.Int>)cmp).$apply(t57758,$T,
                                                                                             key,$T));
                
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final boolean t57760 =
                  ((t57759) < (((int)(0))));
                
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
if (t57760) {
                    
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57761 =
                      mid57756;
                    
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57762 =
                      ((t57761) + (((int)(1))));
                    
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
min = t57762;
                } else {
                    
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57763 =
                      mid57756;
                    
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
max = t57763;
                }
            }
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57744 =
              max;
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57745 =
              min;
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
boolean t57748 =
              ((int) t57744) ==
            ((int) t57745);
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
if (t57748) {
                
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57746 =
                  min;
                
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final $T t57747 =
                  (($T)((((x10.core.IndexedMemoryChunk<$T>)(a))).$apply$G(((int)(t57746)))));
                
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
t57748 = ((java.lang.Object)(((java.lang.Object)(t57747)))).equals(key);
            }
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final boolean t57752 =
              t57748;
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
if (t57752) {
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57749 =
                  min;
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
return t57749;
            } else {
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57750 =
                  max;
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57751 =
                  (-(t57750));
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
return t57751;
            }
        }
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final public x10.util.ArrayUtils
                                                                                                   x10$util$ArrayUtils$$x10$util$ArrayUtils$this(
                                                                                                   ){
            
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
return x10.util.ArrayUtils.this;
        }
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
// creation method for java code (1-phase java constructor)
        public ArrayUtils(){this((java.lang.System[]) null);
                                $init();}
        
        // constructor for non-virtual call
        final public x10.util.ArrayUtils x10$util$ArrayUtils$$init$S() { {
                                                                                
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"

                                                                                
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"

                                                                            }
                                                                            return this;
                                                                            }
        
        // constructor
        public x10.util.ArrayUtils $init(){return x10$util$ArrayUtils$$init$S();}
        
        
        @x10.core.X10Generated public static class $Closure$163<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_2, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$163.class);
            
            public static final x10.rtt.RuntimeType<$Closure$163> $RTT = x10.rtt.StaticFunType.<$Closure$163> make(
            /* base class */$Closure$163.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_2.$RTT, x10.rtt.UnresolvedType.PARAM(0), x10.rtt.UnresolvedType.PARAM(0), x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$163 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$163.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$163 $_obj = new $Closure$163((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                
            }
            
            // constructor just for allocation
            public $Closure$163(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.util.ArrayUtils.$Closure$163.$initParams(this, $T);
            }
            // dispatcher for method abstract public (a1:Z1, a2:Z2)=>U.operator()(a1:Z1,a2:Z2):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1, final java.lang.Object a2, final x10.rtt.Type t2) {
            return x10.core.Int.$box($apply__0x10$util$ArrayUtils$$Closure$163$$T__1x10$util$ArrayUtils$$Closure$163$$T$O(($T)a1, ($T)a2));
            }
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$163 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public int
                  $apply__0x10$util$ArrayUtils$$Closure$163$$T__1x10$util$ArrayUtils$$Closure$163$$T$O(
                  final $T x,
                  final $T y){
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57685 =
                      ((java.lang.Comparable<$T>)(x)).compareTo(y);
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
return t57685;
                }
                
                public $Closure$163(final x10.rtt.Type $T) {x10.util.ArrayUtils.$Closure$163.$initParams(this, $T);
                                                                 {
                                                                    
                                                                }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$164<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_2, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$164.class);
            
            public static final x10.rtt.RuntimeType<$Closure$164> $RTT = x10.rtt.StaticFunType.<$Closure$164> make(
            /* base class */$Closure$164.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_2.$RTT, x10.rtt.UnresolvedType.PARAM(0), x10.rtt.UnresolvedType.PARAM(0), x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$164 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$164.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$164 $_obj = new $Closure$164((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                
            }
            
            // constructor just for allocation
            public $Closure$164(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.util.ArrayUtils.$Closure$164.$initParams(this, $T);
            }
            // dispatcher for method abstract public (a1:Z1, a2:Z2)=>U.operator()(a1:Z1,a2:Z2):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1, final java.lang.Object a2, final x10.rtt.Type t2) {
            return x10.core.Int.$box($apply__0x10$util$ArrayUtils$$Closure$164$$T__1x10$util$ArrayUtils$$Closure$164$$T$O(($T)a1, ($T)a2));
            }
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$164 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public int
                  $apply__0x10$util$ArrayUtils$$Closure$164$$T__1x10$util$ArrayUtils$$Closure$164$$T$O(
                  final $T x,
                  final $T y){
                    
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57718 =
                      ((java.lang.Comparable<$T>)(x)).compareTo(y);
                    
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
return t57718;
                }
                
                public $Closure$164(final x10.rtt.Type $T) {x10.util.ArrayUtils.$Closure$164.$initParams(this, $T);
                                                                 {
                                                                    
                                                                }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$165<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_2, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$165.class);
            
            public static final x10.rtt.RuntimeType<$Closure$165> $RTT = x10.rtt.StaticFunType.<$Closure$165> make(
            /* base class */$Closure$165.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_2.$RTT, x10.rtt.UnresolvedType.PARAM(0), x10.rtt.UnresolvedType.PARAM(0), x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$165 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$165.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$165 $_obj = new $Closure$165((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                
            }
            
            // constructor just for allocation
            public $Closure$165(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.util.ArrayUtils.$Closure$165.$initParams(this, $T);
            }
            // dispatcher for method abstract public (a1:Z1, a2:Z2)=>U.operator()(a1:Z1,a2:Z2):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1, final java.lang.Object a2, final x10.rtt.Type t2) {
            return x10.core.Int.$box($apply__0x10$util$ArrayUtils$$Closure$165$$T__1x10$util$ArrayUtils$$Closure$165$$T$O(($T)a1, ($T)a2));
            }
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$165 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public int
                  $apply__0x10$util$ArrayUtils$$Closure$165$$T__1x10$util$ArrayUtils$$Closure$165$$T$O(
                  final $T x,
                  final $T y){
                    
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
final int t57726 =
                      ((java.lang.Comparable<$T>)(x)).compareTo(y);
                    
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayUtils.x10"
return t57726;
                }
                
                public $Closure$165(final x10.rtt.Type $T) {x10.util.ArrayUtils.$Closure$165.$initParams(this, $T);
                                                                 {
                                                                    
                                                                }}
                
            }
            
        
        }
        