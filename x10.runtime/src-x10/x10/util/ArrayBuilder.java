package x10.util;

@x10.core.X10Generated public class ArrayBuilder<$T> extends x10.core.Ref implements x10.util.Builder, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, ArrayBuilder.class);
    
    public static final x10.rtt.RuntimeType<ArrayBuilder> $RTT = x10.rtt.NamedType.<ArrayBuilder> make(
    "x10.util.ArrayBuilder", /* base class */ArrayBuilder.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.util.Builder.$RTT, x10.rtt.UnresolvedType.PARAM(0), x10.rtt.ParameterizedType.make(x10.array.Array.$RTT, x10.rtt.UnresolvedType.PARAM(0))), x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(ArrayBuilder $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + ArrayBuilder.class + " calling"); } 
        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
        x10.util.GrowableIndexedMemoryChunk buf = (x10.util.GrowableIndexedMemoryChunk) $deserializer.readRef();
        $_obj.buf = buf;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        ArrayBuilder $_obj = new ArrayBuilder((java.lang.System[]) null, (x10.rtt.Type) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
        if (buf instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.buf);
        } else {
        $serializer.write(this.buf);
        }
        
    }
    
    // constructor just for allocation
    public ArrayBuilder(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
    super($dummy);
    x10.util.ArrayBuilder.$initParams(this, $T);
    }
    // dispatcher for method abstract public x10.util.Builder.add(Element):x10.util.Builder[Element, Collection]
    public java.lang.Object add(final java.lang.Object a1, final x10.rtt.Type t1) {
    return add__0x10$util$ArrayBuilder$$T(($T)a1);
    }
    // bridge for method abstract public x10.util.Builder.result():Collection
    public x10.array.Array
      result$G(){return result();}
    
        private x10.rtt.Type $T;
        // initializer of type parameters
        public static void $initParams(final ArrayBuilder $this, final x10.rtt.Type $T) {
        $this.$T = $T;
        }
        
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
public x10.util.GrowableIndexedMemoryChunk<$T> buf;
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
// creation method for java code (1-phase java constructor)
        public ArrayBuilder(final x10.rtt.Type $T){this((java.lang.System[]) null, $T);
                                                       $init();}
        
        // constructor for non-virtual call
        final public x10.util.ArrayBuilder<$T> x10$util$ArrayBuilder$$init$S() { {
                                                                                        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"

                                                                                        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"

                                                                                        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t56897 =
                                                                                          ((x10.util.GrowableIndexedMemoryChunk)(new x10.util.GrowableIndexedMemoryChunk<$T>((java.lang.System[]) null, $T).$init()));
                                                                                        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
this.buf = ((x10.util.GrowableIndexedMemoryChunk)(t56897));
                                                                                    }
                                                                                    return this;
                                                                                    }
        
        // constructor
        public x10.util.ArrayBuilder<$T> $init(){return x10$util$ArrayBuilder$$init$S();}
        
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
// creation method for java code (1-phase java constructor)
        public ArrayBuilder(final x10.rtt.Type $T,
                            final int size){this((java.lang.System[]) null, $T);
                                                $init(size);}
        
        // constructor for non-virtual call
        final public x10.util.ArrayBuilder<$T> x10$util$ArrayBuilder$$init$S(final int size) { {
                                                                                                      
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"

                                                                                                      
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"

                                                                                                      
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t56898 =
                                                                                                        ((x10.util.GrowableIndexedMemoryChunk)(new x10.util.GrowableIndexedMemoryChunk<$T>((java.lang.System[]) null, $T).$init(((int)(size)))));
                                                                                                      
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
this.buf = ((x10.util.GrowableIndexedMemoryChunk)(t56898));
                                                                                                  }
                                                                                                  return this;
                                                                                                  }
        
        // constructor
        public x10.util.ArrayBuilder<$T> $init(final int size){return x10$util$ArrayBuilder$$init$S(size);}
        
        
        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
public x10.util.ArrayBuilder<$T>
                                                                                                     add__0x10$util$ArrayBuilder$$T(
                                                                                                     final $T x){
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t56899 =
              ((x10.util.GrowableIndexedMemoryChunk)(buf));
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
((x10.util.GrowableIndexedMemoryChunk<$T>)t56899).add__0x10$util$GrowableIndexedMemoryChunk$$T((($T)(x)));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
return this;
        }
        
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
public x10.util.ArrayBuilder<$T>
                                                                                                     insert__1$1x10$util$ArrayBuilder$$T$2(
                                                                                                     final int loc,
                                                                                                     final x10.array.Array items){
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t56900 =
              ((x10.util.GrowableIndexedMemoryChunk)(buf));
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
final x10.core.IndexedMemoryChunk<$T> t56901 =
              ((x10.core.IndexedMemoryChunk)(((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.Array<$T>)items).raw()))));
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
((x10.util.GrowableIndexedMemoryChunk<$T>)t56900).insert__1$1x10$util$GrowableIndexedMemoryChunk$$T$2((int)(loc),
                                                                                                                                                                                                             ((x10.core.IndexedMemoryChunk)(t56901)));
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
return this;
        }
        
        
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
public int
                                                                                                     length$O(
                                                                                                     ){
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t56902 =
              ((x10.util.GrowableIndexedMemoryChunk)(buf));
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
final int t56903 =
              ((x10.util.GrowableIndexedMemoryChunk<$T>)t56902).length$O();
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
return t56903;
        }
        
        
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
public x10.array.Array<$T>
                                                                                                     result(
                                                                                                     ){
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t56904 =
              ((x10.util.GrowableIndexedMemoryChunk)(buf));
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
final x10.array.Array<$T> t56905 =
              ((x10.array.Array)(((x10.array.Array<$T>)
                                   ((x10.util.GrowableIndexedMemoryChunk<$T>)t56904).toArray())));
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
return t56905;
        }
        
        
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
final public x10.util.ArrayBuilder<$T>
                                                                                                     x10$util$ArrayBuilder$$x10$util$ArrayBuilder$this(
                                                                                                     ){
            
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayBuilder.x10"
return x10.util.ArrayBuilder.this;
        }
    
}
