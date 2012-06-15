package x10.lang;

@x10.core.X10Generated public class LongRange extends x10.core.Struct implements x10.lang.Iterable, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, LongRange.class);
    
    public static final x10.rtt.RuntimeType<LongRange> $RTT = x10.rtt.NamedType.<LongRange> make(
    "x10.lang.LongRange", /* base class */LongRange.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterable.$RTT, x10.rtt.Types.LONG), x10.rtt.Types.STRUCT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(LongRange $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + LongRange.class + " calling"); } 
        $_obj.min = $deserializer.readLong();
        $_obj.max = $deserializer.readLong();
        $_obj.zeroBased = $deserializer.readBoolean();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        LongRange $_obj = new LongRange((java.lang.System[]) null);
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
    public LongRange(final java.lang.System $dummy) { this.min = 0L; this.max = 0L; this.zeroBased = false; }
    // constructor just for allocation
    public LongRange(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
public long min;
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
public long max;
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
public boolean zeroBased;
        
        
        
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
// creation method for java code (1-phase java constructor)
        public LongRange(final long min,
                         final long max){this((java.lang.System[]) null);
                                             $init(min,max);}
        
        // constructor for non-virtual call
        final public x10.lang.LongRange x10$lang$LongRange$$init$S(final long min,
                                                                   final long max) { {
                                                                                            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
;
                                                                                            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final boolean x =
                                                                                              ((long) min) ==
                                                                                            ((long) 0L);
                                                                                            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
this.min = min;
                                                                                            this.max = max;
                                                                                            this.zeroBased = x;
                                                                                            
                                                                                        }
                                                                                        return this;
                                                                                        }
        
        // constructor
        public x10.lang.LongRange $init(final long min,
                                        final long max){return x10$lang$LongRange$$init$S(min,max);}
        
        
        
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final public java.lang.String
                                                                                                  toString(
                                                                                                  ){
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final long t54412 =
              min;
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final java.lang.String t54413 =
              (((x10.core.Long.$box(t54412))) + (".."));
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final long t54414 =
              max;
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final java.lang.String t54415 =
              ((t54413) + ((x10.core.Long.$box(t54414))));
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
return t54415;
        }
        
        
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final public boolean
                                                                                                  equals(
                                                                                                  final java.lang.Object that){
            
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final boolean t54422 =
              x10.lang.LongRange.$RTT.isInstance(that);
            
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
if (t54422) {
                
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final x10.lang.LongRange other =
                  ((x10.lang.LongRange)(((x10.lang.LongRange)x10.rtt.Types.asStruct(x10.lang.LongRange.$RTT,that))));
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final long t54416 =
                  min;
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final long t54417 =
                  other.
                    min;
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
boolean t54420 =
                  ((long) t54416) ==
                ((long) t54417);
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
if (t54420) {
                    
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final long t54418 =
                      max;
                    
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final long t54419 =
                      other.
                        max;
                    
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
t54420 = ((long) t54418) ==
                    ((long) t54419);
                }
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final boolean t54421 =
                  t54420;
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
return t54421;
            }
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
return false;
        }
        
        
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final public int
                                                                                                  hashCode(
                                                                                                  ){
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final long t54423 =
              max;
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final long t54424 =
              min;
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final long t54425 =
              ((t54423) - (((long)(t54424))));
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final int t54426 =
              x10.rtt.Types.hashCode(t54425);
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
return t54426;
        }
        
        
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final public x10.lang.Iterator<x10.core.Long>
                                                                                                  iterator(
                                                                                                  ){
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final long t54427 =
              min;
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final long t54428 =
              max;
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final x10.lang.LongRange.LongRangeIt t54429 =
              ((x10.lang.LongRange.LongRangeIt)(new x10.lang.LongRange.LongRangeIt((java.lang.System[]) null).$init(((long)(t54427)),
                                                                                                                    ((long)(t54428)))));
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
return t54429;
        }
        
        
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
@x10.core.X10Generated public static class LongRangeIt extends x10.core.Ref implements x10.lang.Iterator, x10.x10rt.X10JavaSerializable
                                                                                                {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, LongRangeIt.class);
            
            public static final x10.rtt.RuntimeType<LongRangeIt> $RTT = x10.rtt.NamedType.<LongRangeIt> make(
            "x10.lang.LongRange.LongRangeIt", /* base class */LongRangeIt.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterator.$RTT, x10.rtt.Types.LONG), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(LongRangeIt $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + LongRangeIt.class + " calling"); } 
                $_obj.cur = $deserializer.readLong();
                $_obj.max = $deserializer.readLong();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                LongRangeIt $_obj = new LongRangeIt((java.lang.System[]) null);
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
            public LongRangeIt(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // bridge for method abstract public x10.lang.Iterator.next():T
            public x10.core.Long
              next$G(){return x10.core.Long.$box(next$O());}
            
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
public long cur;
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
public long max;
                
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
// creation method for java code (1-phase java constructor)
                public LongRangeIt(final long min,
                                   final long max){this((java.lang.System[]) null);
                                                       $init(min,max);}
                
                // constructor for non-virtual call
                final public x10.lang.LongRange.LongRangeIt x10$lang$LongRange$LongRangeIt$$init$S(final long min,
                                                                                                   final long max) { {
                                                                                                                            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"

                                                                                                                            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"

                                                                                                                            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
this.__fieldInitializers54373();
                                                                                                                            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
this.cur = min;
                                                                                                                            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
this.max = max;
                                                                                                                        }
                                                                                                                        return this;
                                                                                                                        }
                
                // constructor
                public x10.lang.LongRange.LongRangeIt $init(final long min,
                                                            final long max){return x10$lang$LongRange$LongRangeIt$$init$S(min,max);}
                
                
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
public boolean
                                                                                                          hasNext$O(
                                                                                                          ){
                    
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final long t54430 =
                      cur;
                    
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final long t54431 =
                      max;
                    
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final boolean t54432 =
                      ((t54430) <= (((long)(t54431))));
                    
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
return t54432;
                }
                
                
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
public long
                                                                                                          next$O(
                                                                                                          ){
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final x10.lang.LongRange.LongRangeIt x54410 =
                      ((x10.lang.LongRange.LongRangeIt)(this));
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
;
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final long t54433 =
                      x54410.
                        cur;
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final long t54434 =
                      ((t54433) + (((long)(1L))));
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final long t54435 =
                      x54410.cur = t54434;
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final long t54436 =
                      ((t54435) - (((long)(1L))));
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
return t54436;
                }
                
                
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final public x10.lang.LongRange.LongRangeIt
                                                                                                          x10$lang$LongRange$LongRangeIt$$x10$lang$LongRange$LongRangeIt$this(
                                                                                                          ){
                    
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
return x10.lang.LongRange.LongRangeIt.this;
                }
                
                
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final public void
                                                                                                          __fieldInitializers54373(
                                                                                                          ){
                    
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
this.cur = 0L;
                }
            
        }
        
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final public java.lang.String
                                                                                                  typeName(
                                                                                                  ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final public boolean
                                                                                                  _struct_equals$O(
                                                                                                  java.lang.Object other){
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final java.lang.Object t54437 =
              other;
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final boolean t54438 =
              x10.lang.LongRange.$RTT.isInstance(t54437);
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final boolean t54439 =
              !(t54438);
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
if (t54439) {
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
return false;
            }
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final java.lang.Object t54440 =
              other;
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final x10.lang.LongRange t54441 =
              ((x10.lang.LongRange)x10.rtt.Types.asStruct(x10.lang.LongRange.$RTT,t54440));
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final boolean t54442 =
              this._struct_equals$O(((x10.lang.LongRange)(t54441)));
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
return t54442;
        }
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final public boolean
                                                                                                  _struct_equals$O(
                                                                                                  x10.lang.LongRange other){
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final long t54444 =
              this.
                min;
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final x10.lang.LongRange t54443 =
              other;
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final long t54445 =
              t54443.
                min;
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
boolean t54449 =
              ((long) t54444) ==
            ((long) t54445);
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
if (t54449) {
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final long t54447 =
                  this.
                    max;
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final x10.lang.LongRange t54446 =
                  other;
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final long t54448 =
                  t54446.
                    max;
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
t54449 = ((long) t54447) ==
                ((long) t54448);
            }
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
boolean t54453 =
              t54449;
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
if (t54453) {
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final boolean t54451 =
                  this.
                    zeroBased;
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final x10.lang.LongRange t54450 =
                  other;
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final boolean t54452 =
                  t54450.
                    zeroBased;
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
t54453 = ((boolean) t54451) ==
                ((boolean) t54452);
            }
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final boolean t54454 =
              t54453;
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
return t54454;
        }
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
final public x10.lang.LongRange
                                                                                                  x10$lang$LongRange$$x10$lang$LongRange$this(
                                                                                                  ){
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/LongRange.x10"
return x10.lang.LongRange.this;
        }
    
}
