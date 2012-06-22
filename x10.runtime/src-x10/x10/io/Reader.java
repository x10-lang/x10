package x10.io;

@x10.core.X10Generated abstract public class Reader extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Reader.class);
    
    public static final x10.rtt.RuntimeType<Reader> $RTT = x10.rtt.NamedType.<Reader> make(
    "x10.io.Reader", /* base class */Reader.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Reader $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Reader.class + " calling"); } 
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        return null;
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        
    }
    
    // constructor just for allocation
    public Reader(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
abstract public void
                                                                                             close(
                                                                                             );
        
        
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
abstract public byte
                                                                                             read$O(
                                                                                             );
        
        
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
abstract public int
                                                                                             available$O(
                                                                                             );
        
        
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
abstract public void
                                                                                             skip(
                                                                                             final int id$105);
        
        
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
abstract public void
                                                                                             mark(
                                                                                             final int id$106);
        
        
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
abstract public void
                                                                                             reset(
                                                                                             );
        
        
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
abstract public boolean
                                                                                             markSupported$O(
                                                                                             );
        
        
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
public boolean
                                                                                             readBoolean$O(
                                                                                             ){
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final x10.io.Marshal.BooleanMarshal t50193 =
              ((x10.io.Marshal.BooleanMarshal)(x10.io.Marshal.Shadow.getInitialized$BOOLEAN()));
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final boolean t50194 =
              t50193.read$O(((x10.io.Reader)(this)));
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
return t50194;
        }
        
        
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
public byte
                                                                                             readByte$O(
                                                                                             ){
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final x10.io.Marshal.ByteMarshal t50195 =
              ((x10.io.Marshal.ByteMarshal)(x10.io.Marshal.Shadow.getInitialized$BYTE()));
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final byte t50196 =
              t50195.read$O(((x10.io.Reader)(this)));
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
return t50196;
        }
        
        
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
public byte
                                                                                             readUByte$O(
                                                                                             ){
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final x10.io.Marshal.UByteMarshal t50197 =
              ((x10.io.Marshal.UByteMarshal)(x10.io.Marshal.Shadow.getInitialized$UBYTE()));
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final byte t50198 =
              t50197.read$O(((x10.io.Reader)(this)));
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
return t50198;
        }
        
        
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
public char
                                                                                             readChar$O(
                                                                                             ){
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final x10.io.Marshal.CharMarshal t50199 =
              ((x10.io.Marshal.CharMarshal)(x10.io.Marshal.Shadow.getInitialized$CHAR()));
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final char t50200 =
              t50199.read$O(((x10.io.Reader)(this)));
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
return t50200;
        }
        
        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
public short
                                                                                             readShort$O(
                                                                                             ){
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final x10.io.Marshal.ShortMarshal t50201 =
              ((x10.io.Marshal.ShortMarshal)(x10.io.Marshal.Shadow.getInitialized$SHORT()));
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final short t50202 =
              t50201.read$O(((x10.io.Reader)(this)));
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
return t50202;
        }
        
        
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
public short
                                                                                             readUShort$O(
                                                                                             ){
            
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final x10.io.Marshal.UShortMarshal t50203 =
              ((x10.io.Marshal.UShortMarshal)(x10.io.Marshal.Shadow.getInitialized$USHORT()));
            
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final short t50204 =
              t50203.read$O(((x10.io.Reader)(this)));
            
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
return t50204;
        }
        
        
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
public int
                                                                                             readInt$O(
                                                                                             ){
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final x10.io.Marshal.IntMarshal t50205 =
              ((x10.io.Marshal.IntMarshal)(x10.io.Marshal.Shadow.getInitialized$INT()));
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final int t50206 =
              t50205.read$O(((x10.io.Reader)(this)));
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
return t50206;
        }
        
        
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
public int
                                                                                             readUInt$O(
                                                                                             ){
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final x10.io.Marshal.UIntMarshal t50207 =
              ((x10.io.Marshal.UIntMarshal)(x10.io.Marshal.Shadow.getInitialized$UINT()));
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final int t50208 =
              t50207.read$O(((x10.io.Reader)(this)));
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
return t50208;
        }
        
        
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
public long
                                                                                             readLong$O(
                                                                                             ){
            
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final x10.io.Marshal.LongMarshal t50209 =
              ((x10.io.Marshal.LongMarshal)(x10.io.Marshal.Shadow.getInitialized$LONG()));
            
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final long t50210 =
              t50209.read$O(((x10.io.Reader)(this)));
            
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
return t50210;
        }
        
        
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
public long
                                                                                              readULong$O(
                                                                                              ){
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final x10.io.Marshal.ULongMarshal t50211 =
              ((x10.io.Marshal.ULongMarshal)(x10.io.Marshal.Shadow.getInitialized$ULONG()));
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final long t50212 =
              t50211.read$O(((x10.io.Reader)(this)));
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
return t50212;
        }
        
        
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
public float
                                                                                              readFloat$O(
                                                                                              ){
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final x10.io.Marshal.FloatMarshal t50213 =
              ((x10.io.Marshal.FloatMarshal)(x10.io.Marshal.Shadow.getInitialized$FLOAT()));
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final float t50214 =
              t50213.read$O(((x10.io.Reader)(this)));
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
return t50214;
        }
        
        
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
public double
                                                                                              readDouble$O(
                                                                                              ){
            
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final x10.io.Marshal.DoubleMarshal t50215 =
              ((x10.io.Marshal.DoubleMarshal)(x10.io.Marshal.Shadow.getInitialized$DOUBLE()));
            
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final double t50216 =
              t50215.read$O(((x10.io.Reader)(this)));
            
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
return t50216;
        }
        
        
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
public java.lang.String
                                                                                              readLine$O(
                                                                                              ){
            
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final x10.io.Marshal.LineMarshal t50217 =
              ((x10.io.Marshal.LineMarshal)(x10.io.Marshal.Shadow.getInitialized$LINE()));
            
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final java.lang.String t50218 =
              t50217.read$O(((x10.io.Reader)(this)));
            
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
return t50218;
        }
        
        
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final public <$T>$T
                                                                                              read__0$1x10$io$Reader$$T$2$G(
                                                                                              final x10.rtt.Type $T,
                                                                                              final x10.io.Marshal m){
            
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final $T t50219 =
              (($T)(((x10.io.Marshal<$T>)m).read$G(((x10.io.Reader)(this)))));
            
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
return t50219;
        }
        
        
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final public <$T>void
                                                                                              read__0$1x10$io$Reader$$T$2__1$1x10$io$Reader$$T$2(
                                                                                              final x10.rtt.Type $T,
                                                                                              final x10.io.Marshal m,
                                                                                              final x10.array.Array a){
            
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final int t50220 =
              ((x10.array.Array<$T>)a).
                size;
            
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
this.<$T>read__0$1x10$io$Reader$$T$2__1$1x10$io$Reader$$T$2($T, ((x10.io.Marshal)(m)),
                                                                                                                                                            ((x10.array.Array)(a)),
                                                                                                                                                            (int)(0),
                                                                                                                                                            (int)(t50220));
        }
        
        
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final public <$T>void
                                                                                              read__0$1x10$io$Reader$$T$2__1$1x10$io$Reader$$T$2(
                                                                                              final x10.rtt.Type $T,
                                                                                              final x10.io.Marshal m,
                                                                                              final x10.array.Array a,
                                                                                              final int off,
                                                                                              final int len){
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
int i =
              off;
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
for (;
                                                                                                     true;
                                                                                                     ) {
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final int t50222 =
                  i;
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final int t50223 =
                  ((off) + (((int)(len))));
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final boolean t50228 =
                  ((t50222) < (((int)(t50223))));
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
if (!(t50228)) {
                    
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
break;
                }
                
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final int t50235 =
                  i;
                
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final $T t50236 =
                  (($T)(this.<$T>read__0$1x10$io$Reader$$T$2$G($T, ((x10.io.Marshal)(m)))));
                
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
((x10.array.Array<$T>)a).$set__1x10$array$Array$$T$G((int)(t50235),
                                                                                                                                                         (($T)(t50236)));
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final int t50237 =
                  i;
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final int t50238 =
                  ((t50237) + (((int)(1))));
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
i = t50238;
            }
        }
        
        
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
public x10.io.ReaderIterator<java.lang.String>
                                                                                              lines(
                                                                                              ){
            
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final x10.io.Marshal.LineMarshal t50229 =
              ((x10.io.Marshal.LineMarshal)(x10.io.Marshal.Shadow.getInitialized$LINE()));
            
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final x10.io.ReaderIterator<java.lang.String> t50230 =
              ((x10.io.ReaderIterator)(new x10.io.ReaderIterator<java.lang.String>((java.lang.System[]) null, x10.rtt.Types.STRING).$init(((x10.io.Marshal<java.lang.String>)(t50229)),
                                                                                                                                          ((x10.io.Reader)(this)), (x10.io.ReaderIterator.__0$1x10$io$ReaderIterator$$T$2) null)));
            
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
return t50230;
        }
        
        
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
public x10.io.ReaderIterator<x10.core.Char>
                                                                                              chars(
                                                                                              ){
            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final x10.io.Marshal.CharMarshal t50231 =
              ((x10.io.Marshal.CharMarshal)(x10.io.Marshal.Shadow.getInitialized$CHAR()));
            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final x10.io.ReaderIterator<x10.core.Char> t50232 =
              ((x10.io.ReaderIterator)(new x10.io.ReaderIterator<x10.core.Char>((java.lang.System[]) null, x10.rtt.Types.CHAR).$init(((x10.io.Marshal<x10.core.Char>)(t50231)),
                                                                                                                                     ((x10.io.Reader)(this)), (x10.io.ReaderIterator.__0$1x10$io$ReaderIterator$$T$2) null)));
            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
return t50232;
        }
        
        
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
public x10.io.ReaderIterator<x10.core.Byte>
                                                                                              bytes(
                                                                                              ){
            
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final x10.io.Marshal.ByteMarshal t50233 =
              ((x10.io.Marshal.ByteMarshal)(x10.io.Marshal.Shadow.getInitialized$BYTE()));
            
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final x10.io.ReaderIterator<x10.core.Byte> t50234 =
              ((x10.io.ReaderIterator)(new x10.io.ReaderIterator<x10.core.Byte>((java.lang.System[]) null, x10.rtt.Types.BYTE).$init(((x10.io.Marshal<x10.core.Byte>)(t50233)),
                                                                                                                                     ((x10.io.Reader)(this)), (x10.io.ReaderIterator.__0$1x10$io$ReaderIterator$$T$2) null)));
            
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
return t50234;
        }
        
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
final public x10.io.Reader
                                                                                             x10$io$Reader$$x10$io$Reader$this(
                                                                                             ){
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"
return x10.io.Reader.this;
        }
        
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"

        // constructor for non-virtual call
        final public x10.io.Reader x10$io$Reader$$init$S() { {
                                                                    
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"

                                                                    
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Reader.x10"

                                                                }
                                                                return this;
                                                                }
        
        // constructor
        public x10.io.Reader $init(){return x10$io$Reader$$init$S();}
        
    
}
