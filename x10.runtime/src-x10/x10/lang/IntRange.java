package x10.lang;

@x10.core.X10Generated public class IntRange extends x10.core.Struct implements x10.lang.Iterable, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, IntRange.class);
    
    public static final x10.rtt.RuntimeType<IntRange> $RTT = x10.rtt.NamedType.<IntRange> make(
    "x10.lang.IntRange", /* base class */IntRange.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterable.$RTT, x10.rtt.Types.INT), x10.rtt.Types.STRUCT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(IntRange $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + IntRange.class + " calling"); } 
        $_obj.min = $deserializer.readInt();
        $_obj.max = $deserializer.readInt();
        $_obj.zeroBased = $deserializer.readBoolean();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        IntRange $_obj = new IntRange((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write(this.min);
        $serializer.write(this.max);
        $serializer.write(this.zeroBased);
        
    }
    
    // zero value constructor
    public IntRange(final java.lang.System $dummy) { this.min = 0; this.max = 0; this.zeroBased = false; }
    // constructor just for allocation
    public IntRange(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
public int min;
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
public int max;
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
public boolean zeroBased;
        
        
        
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
// creation method for java code (1-phase java constructor)
        public IntRange(final int min,
                        final int max){this((java.lang.System[]) null);
                                           $init(min,max);}
        
        // constructor for non-virtual call
        final public x10.lang.IntRange x10$lang$IntRange$$init$S(final int min,
                                                                 final int max) { {
                                                                                         
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
;
                                                                                         
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final boolean zero =
                                                                                           ((int) min) ==
                                                                                         ((int) 0);
                                                                                         
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
this.min = min;
                                                                                         this.max = max;
                                                                                         this.zeroBased = zero;
                                                                                         
                                                                                     }
                                                                                     return this;
                                                                                     }
        
        // constructor
        public x10.lang.IntRange $init(final int min,
                                       final int max){return x10$lang$IntRange$$init$S(min,max);}
        
        
        
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final public x10.array.Region
                                                                                                 $times(
                                                                                                 final x10.lang.IntRange that){
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54301 =
              min;
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54302 =
              that.
                min;
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final x10.array.Array<x10.core.Int> t54305 =
              ((x10.array.Array)(x10.core.ArrayFactory.<x10.core.Int> makeArrayFromJavaArray(x10.rtt.Types.INT, new int[] {t54301, t54302})));
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54303 =
              max;
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54304 =
              that.
                max;
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final x10.array.Array<x10.core.Int> t54306 =
              ((x10.array.Array)(x10.core.ArrayFactory.<x10.core.Int> makeArrayFromJavaArray(x10.rtt.Types.INT, new int[] {t54303, t54304})));
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final x10.array.Region t54307 =
              ((x10.array.Region)(x10.array.Region.<x10.core.Int,
            x10.core.Int>makeRectangular__0$1x10$array$Region$$S$2__1$1x10$array$Region$$T$2(x10.rtt.Types.INT, x10.rtt.Types.INT, ((x10.array.Array)(t54305)),
                                                                                             ((x10.array.Array)(t54306)))));
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
return t54307;
        }
        
        
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final public x10.lang.IntRange
                                                                                                 translate(
                                                                                                 final int i){
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54308 =
              min;
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54310 =
              ((t54308) + (((int)(i))));
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54309 =
              max;
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54311 =
              ((t54309) + (((int)(i))));
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final x10.lang.IntRange t54312 =
              ((x10.lang.IntRange)(new x10.lang.IntRange((java.lang.System[]) null).$init(t54310,
                                                                                          t54311)));
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
return t54312;
        }
        
        
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final public x10.lang.IntRange
                                                                                                 translate(
                                                                                                 final x10.array.Point p){
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54313 =
              min;
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54314 =
              p.$apply$O((int)(0));
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54317 =
              ((t54313) + (((int)(t54314))));
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54315 =
              max;
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54316 =
              p.$apply$O((int)(0));
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54318 =
              ((t54315) + (((int)(t54316))));
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final x10.lang.IntRange t54319 =
              ((x10.lang.IntRange)(new x10.lang.IntRange((java.lang.System[]) null).$init(t54317,
                                                                                          t54318)));
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
return t54319;
        }
        
        
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final public x10.array.Region
                                                                                                 $and(
                                                                                                 final x10.array.Region that){
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final x10.array.Region t54320 =
              ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(this)))));
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final x10.array.Region t54321 =
              ((x10.array.Region)(t54320.$and(((x10.array.Region)(that)))));
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
return t54321;
        }
        
        
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final public x10.array.Dist
                                                                                                 $arrow(
                                                                                                 final x10.lang.Place p){
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final x10.array.Region t54322 =
              ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(this)))));
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final x10.array.Dist t54323 =
              ((x10.array.Dist)(x10.array.Dist.makeConstant(((x10.array.Region)(t54322)),
                                                            ((x10.lang.Place)(p)))));
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
return t54323;
        }
        
        
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final public java.lang.String
                                                                                                 toString(
                                                                                                 ){
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54324 =
              min;
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final java.lang.String t54325 =
              (((x10.core.Int.$box(t54324))) + (".."));
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54326 =
              max;
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final java.lang.String t54327 =
              ((t54325) + ((x10.core.Int.$box(t54326))));
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
return t54327;
        }
        
        
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final public boolean
                                                                                                 equals(
                                                                                                 final java.lang.Object that){
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final boolean t54334 =
              x10.lang.IntRange.$RTT.isInstance(that);
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
if (t54334) {
                
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final x10.lang.IntRange other =
                  ((x10.lang.IntRange)(((x10.lang.IntRange)x10.rtt.Types.asStruct(x10.lang.IntRange.$RTT,that))));
                
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54328 =
                  min;
                
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54329 =
                  other.
                    min;
                
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
boolean t54332 =
                  ((int) t54328) ==
                ((int) t54329);
                
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
if (t54332) {
                    
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54330 =
                      max;
                    
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54331 =
                      other.
                        max;
                    
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
t54332 = ((int) t54330) ==
                    ((int) t54331);
                }
                
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final boolean t54333 =
                  t54332;
                
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
return t54333;
            }
            
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
return false;
        }
        
        
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final public int
                                                                                                 hashCode(
                                                                                                 ){
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54335 =
              max;
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54336 =
              min;
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54337 =
              ((t54335) - (((int)(t54336))));
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54338 =
              x10.rtt.Types.hashCode(t54337);
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
return t54338;
        }
        
        
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final public x10.lang.Iterator<x10.core.Int>
                                                                                                 iterator(
                                                                                                 ){
            
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54339 =
              min;
            
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54340 =
              max;
            
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final x10.lang.IntRange.IntRangeIt t54341 =
              ((x10.lang.IntRange.IntRangeIt)(new x10.lang.IntRange.IntRangeIt((java.lang.System[]) null).$init(((int)(t54339)),
                                                                                                                ((int)(t54340)))));
            
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
return t54341;
        }
        
        
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
@x10.core.X10Generated public static class IntRangeIt extends x10.core.Ref implements x10.lang.Iterator, x10.x10rt.X10JavaSerializable
                                                                                               {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, IntRangeIt.class);
            
            public static final x10.rtt.RuntimeType<IntRangeIt> $RTT = x10.rtt.NamedType.<IntRangeIt> make(
            "x10.lang.IntRange.IntRangeIt", /* base class */IntRangeIt.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterator.$RTT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(IntRangeIt $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + IntRangeIt.class + " calling"); } 
                $_obj.cur = $deserializer.readInt();
                $_obj.max = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                IntRangeIt $_obj = new IntRangeIt((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write(this.cur);
                $serializer.write(this.max);
                
            }
            
            // constructor just for allocation
            public IntRangeIt(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // bridge for method abstract public x10.lang.Iterator.next():T
            public x10.core.Int
              next$G(){return x10.core.Int.$box(next$O());}
            
                
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
public int cur;
                
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
public int max;
                
                
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
// creation method for java code (1-phase java constructor)
                public IntRangeIt(final int min,
                                  final int max){this((java.lang.System[]) null);
                                                     $init(min,max);}
                
                // constructor for non-virtual call
                final public x10.lang.IntRange.IntRangeIt x10$lang$IntRange$IntRangeIt$$init$S(final int min,
                                                                                               final int max) { {
                                                                                                                       
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"

                                                                                                                       
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"

                                                                                                                       
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
this.__fieldInitializers54237();
                                                                                                                       
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
this.cur = min;
                                                                                                                       
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
this.max = max;
                                                                                                                   }
                                                                                                                   return this;
                                                                                                                   }
                
                // constructor
                public x10.lang.IntRange.IntRangeIt $init(final int min,
                                                          final int max){return x10$lang$IntRange$IntRangeIt$$init$S(min,max);}
                
                
                
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
public boolean
                                                                                                         hasNext$O(
                                                                                                         ){
                    
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54342 =
                      cur;
                    
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54343 =
                      max;
                    
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final boolean t54344 =
                      ((t54342) <= (((int)(t54343))));
                    
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
return t54344;
                }
                
                
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
public int
                                                                                                         next$O(
                                                                                                         ){
                    
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final x10.lang.IntRange.IntRangeIt x54299 =
                      ((x10.lang.IntRange.IntRangeIt)(this));
                    
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
;
                    
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54345 =
                      x54299.
                        cur;
                    
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54346 =
                      ((t54345) + (((int)(1))));
                    
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54347 =
                      x54299.cur = t54346;
                    
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54348 =
                      ((t54347) - (((int)(1))));
                    
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
return t54348;
                }
                
                
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final public x10.lang.IntRange.IntRangeIt
                                                                                                         x10$lang$IntRange$IntRangeIt$$x10$lang$IntRange$IntRangeIt$this(
                                                                                                         ){
                    
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
return x10.lang.IntRange.IntRangeIt.this;
                }
                
                
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final public void
                                                                                                         __fieldInitializers54237(
                                                                                                         ){
                    
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
this.cur = 0;
                }
            
        }
        
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final public java.lang.String
                                                                                                 typeName(
                                                                                                 ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final public boolean
                                                                                                 _struct_equals$O(
                                                                                                 java.lang.Object other){
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final java.lang.Object t54349 =
              other;
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final boolean t54350 =
              x10.lang.IntRange.$RTT.isInstance(t54349);
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final boolean t54351 =
              !(t54350);
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
if (t54351) {
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
return false;
            }
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final java.lang.Object t54352 =
              other;
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final x10.lang.IntRange t54353 =
              ((x10.lang.IntRange)x10.rtt.Types.asStruct(x10.lang.IntRange.$RTT,t54352));
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final boolean t54354 =
              this._struct_equals$O(((x10.lang.IntRange)(t54353)));
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
return t54354;
        }
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final public boolean
                                                                                                 _struct_equals$O(
                                                                                                 x10.lang.IntRange other){
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54356 =
              this.
                min;
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final x10.lang.IntRange t54355 =
              other;
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54357 =
              t54355.
                min;
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
boolean t54361 =
              ((int) t54356) ==
            ((int) t54357);
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
if (t54361) {
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54359 =
                  this.
                    max;
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final x10.lang.IntRange t54358 =
                  other;
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final int t54360 =
                  t54358.
                    max;
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
t54361 = ((int) t54359) ==
                ((int) t54360);
            }
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
boolean t54365 =
              t54361;
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
if (t54365) {
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final boolean t54363 =
                  this.
                    zeroBased;
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final x10.lang.IntRange t54362 =
                  other;
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final boolean t54364 =
                  t54362.
                    zeroBased;
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
t54365 = ((boolean) t54363) ==
                ((boolean) t54364);
            }
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final boolean t54366 =
              t54365;
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
return t54366;
        }
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
final public x10.lang.IntRange
                                                                                                 x10$lang$IntRange$$x10$lang$IntRange$this(
                                                                                                 ){
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IntRange.x10"
return x10.lang.IntRange.this;
        }
    
}
