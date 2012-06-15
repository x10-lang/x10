package x10.io;


@x10.core.X10Generated public class PutbackReader extends x10.io.FilterReader implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, PutbackReader.class);
    
    public static final x10.rtt.RuntimeType<PutbackReader> $RTT = x10.rtt.NamedType.<PutbackReader> make(
    "x10.io.PutbackReader", /* base class */PutbackReader.class
    , /* parents */ new x10.rtt.Type[] {x10.io.FilterReader.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(PutbackReader $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + PutbackReader.class + " calling"); } 
        x10.io.FilterReader.$_deserialize_body($_obj, $deserializer);
        x10.util.GrowableIndexedMemoryChunk putback = (x10.util.GrowableIndexedMemoryChunk) $deserializer.readRef();
        $_obj.putback = putback;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        PutbackReader $_obj = new PutbackReader((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (putback instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.putback);
        } else {
        $serializer.write(this.putback);
        }
        
    }
    
    // constructor just for allocation
    public PutbackReader(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
public x10.util.GrowableIndexedMemoryChunk<x10.core.Byte> putback;
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
// creation method for java code (1-phase java constructor)
        public PutbackReader(final x10.io.Reader r){this((java.lang.System[]) null);
                                                        $init(r);}
        
        // constructor for non-virtual call
        final public x10.io.PutbackReader x10$io$PutbackReader$$init$S(final x10.io.Reader r) { {
                                                                                                       
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
super.$init(((x10.io.Reader)(r)));
                                                                                                       
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"

                                                                                                       
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
final x10.util.GrowableIndexedMemoryChunk<x10.core.Byte> t50164 =
                                                                                                         ((x10.util.GrowableIndexedMemoryChunk)(new x10.util.GrowableIndexedMemoryChunk<x10.core.Byte>((java.lang.System[]) null, x10.rtt.Types.BYTE).$init()));
                                                                                                       
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
this.putback = ((x10.util.GrowableIndexedMemoryChunk)(t50164));
                                                                                                   }
                                                                                                   return this;
                                                                                                   }
        
        // constructor
        public x10.io.PutbackReader $init(final x10.io.Reader r){return x10$io$PutbackReader$$init$S(r);}
        
        
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
public byte
                                                                                                    read$O(
                                                                                                    ){
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
final x10.util.GrowableIndexedMemoryChunk<x10.core.Byte> t50165 =
              ((x10.util.GrowableIndexedMemoryChunk)(putback));
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
final int t50166 =
              ((x10.util.GrowableIndexedMemoryChunk<x10.core.Byte>)t50165).length$O();
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
final boolean t50172 =
              ((t50166) > (((int)(0))));
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
if (t50172) {
                
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
final x10.util.GrowableIndexedMemoryChunk<x10.core.Byte> t50169 =
                  ((x10.util.GrowableIndexedMemoryChunk)(putback));
                
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
final x10.util.GrowableIndexedMemoryChunk<x10.core.Byte> t50167 =
                  ((x10.util.GrowableIndexedMemoryChunk)(putback));
                
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
final int t50168 =
                  ((x10.util.GrowableIndexedMemoryChunk<x10.core.Byte>)t50167).length$O();
                
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
final int t50170 =
                  ((t50168) - (((int)(1))));
                
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
final byte p =
                  x10.core.Byte.$unbox(((x10.util.GrowableIndexedMemoryChunk<x10.core.Byte>)t50169).$apply$G((int)(t50170)));
                
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
final x10.util.GrowableIndexedMemoryChunk<x10.core.Byte> t50171 =
                  ((x10.util.GrowableIndexedMemoryChunk)(putback));
                
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
((x10.util.GrowableIndexedMemoryChunk<x10.core.Byte>)t50171).removeLast();
                
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
return p;
            }
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
final byte t50173 =
              super.read$O();
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
return t50173;
        }
        
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
public void
                                                                                                    putback(
                                                                                                    final byte p){
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
final x10.util.GrowableIndexedMemoryChunk<x10.core.Byte> t50174 =
              ((x10.util.GrowableIndexedMemoryChunk)(putback));
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
((x10.util.GrowableIndexedMemoryChunk<x10.core.Byte>)t50174).add__0x10$util$GrowableIndexedMemoryChunk$$T(x10.core.Byte.$box(p));
        }
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
final public x10.io.PutbackReader
                                                                                                    x10$io$PutbackReader$$x10$io$PutbackReader$this(
                                                                                                    ){
            
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/PutbackReader.x10"
return x10.io.PutbackReader.this;
        }
        
        public byte
          x10$io$FilterReader$read$S$O(
          ){
            return super.read$O();
        }
    
}
