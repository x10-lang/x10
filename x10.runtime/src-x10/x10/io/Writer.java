package x10.io;


@x10.core.X10Generated abstract public class Writer extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Writer.class);
    
    public static final x10.rtt.RuntimeType<Writer> $RTT = x10.rtt.NamedType.<Writer> make(
    "x10.io.Writer", /* base class */Writer.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Writer $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Writer.class + " calling"); } 
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
    public Writer(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
abstract public void
                                                                                             close(
                                                                                             );
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
abstract public void
                                                                                             flush(
                                                                                             );
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
abstract public void
                                                                                             write(
                                                                                             final byte x);
        
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
public void
                                                                                             writeByte(
                                                                                             final byte x){
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final x10.io.Marshal.ByteMarshal t50374 =
              ((x10.io.Marshal.ByteMarshal)(x10.io.Marshal.Shadow.getInitialized$BYTE()));
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
t50374.write(((x10.io.Writer)(this)),
                                                                                                            (byte)(x));
        }
        
        
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
public void
                                                                                             writeUByte__0$u(
                                                                                             final byte x){
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final x10.io.Marshal.UByteMarshal t50375 =
              ((x10.io.Marshal.UByteMarshal)(x10.io.Marshal.Shadow.getInitialized$UBYTE()));
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
t50375.write__1$u(((x10.io.Writer)(this)),
                                                                                                                 (byte)(x));
        }
        
        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
public void
                                                                                             writeChar(
                                                                                             final char x){
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final x10.io.Marshal.CharMarshal t50376 =
              ((x10.io.Marshal.CharMarshal)(x10.io.Marshal.Shadow.getInitialized$CHAR()));
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
t50376.write(((x10.io.Writer)(this)),
                                                                                                            (char)(x));
        }
        
        
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
public void
                                                                                             writeShort(
                                                                                             final short x){
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final x10.io.Marshal.ShortMarshal t50377 =
              ((x10.io.Marshal.ShortMarshal)(x10.io.Marshal.Shadow.getInitialized$SHORT()));
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
t50377.write(((x10.io.Writer)(this)),
                                                                                                            (short)(x));
        }
        
        
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
public void
                                                                                             writeUShort__0$u(
                                                                                             final short x){
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final x10.io.Marshal.UShortMarshal t50378 =
              ((x10.io.Marshal.UShortMarshal)(x10.io.Marshal.Shadow.getInitialized$USHORT()));
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
t50378.write__1$u(((x10.io.Writer)(this)),
                                                                                                                 (short)(x));
        }
        
        
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
public void
                                                                                             writeInt(
                                                                                             final int x){
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final x10.io.Marshal.IntMarshal t50379 =
              ((x10.io.Marshal.IntMarshal)(x10.io.Marshal.Shadow.getInitialized$INT()));
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
t50379.write(((x10.io.Writer)(this)),
                                                                                                            (int)(x));
        }
        
        
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
public void
                                                                                             writeUInt__0$u(
                                                                                             final int x){
            
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final x10.io.Marshal.UIntMarshal t50380 =
              ((x10.io.Marshal.UIntMarshal)(x10.io.Marshal.Shadow.getInitialized$UINT()));
            
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
t50380.write__1$u(((x10.io.Writer)(this)),
                                                                                                                 (int)(x));
        }
        
        
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
public void
                                                                                             writeLong(
                                                                                             final long x){
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final x10.io.Marshal.LongMarshal t50381 =
              ((x10.io.Marshal.LongMarshal)(x10.io.Marshal.Shadow.getInitialized$LONG()));
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
t50381.write(((x10.io.Writer)(this)),
                                                                                                            (long)(x));
        }
        
        
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
public void
                                                                                             writeULong__0$u(
                                                                                             final long x){
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final x10.io.Marshal.ULongMarshal t50382 =
              ((x10.io.Marshal.ULongMarshal)(x10.io.Marshal.Shadow.getInitialized$ULONG()));
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
t50382.write__1$u(((x10.io.Writer)(this)),
                                                                                                                 (long)(x));
        }
        
        
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
public void
                                                                                             writeFloat(
                                                                                             final float x){
            
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final x10.io.Marshal.FloatMarshal t50383 =
              ((x10.io.Marshal.FloatMarshal)(x10.io.Marshal.Shadow.getInitialized$FLOAT()));
            
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
t50383.write(((x10.io.Writer)(this)),
                                                                                                            (float)(x));
        }
        
        
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
public void
                                                                                             writeDouble(
                                                                                             final double x){
            
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final x10.io.Marshal.DoubleMarshal t50384 =
              ((x10.io.Marshal.DoubleMarshal)(x10.io.Marshal.Shadow.getInitialized$DOUBLE()));
            
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
t50384.write(((x10.io.Writer)(this)),
                                                                                                            (double)(x));
        }
        
        
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
public void
                                                                                             writeBoolean(
                                                                                             final boolean x){
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final x10.io.Marshal.BooleanMarshal t50385 =
              ((x10.io.Marshal.BooleanMarshal)(x10.io.Marshal.Shadow.getInitialized$BOOLEAN()));
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
t50385.write(((x10.io.Writer)(this)),
                                                                                                            (boolean)(x));
        }
        
        
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final public <$T>void
                                                                                             write__0$1x10$io$Writer$$T$2__1x10$io$Writer$$T(
                                                                                             final x10.rtt.Type $T,
                                                                                             final x10.io.Marshal m,
                                                                                             final $T x){
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
((x10.io.Marshal<$T>)m).write(((x10.io.Writer)(this)),
                                                                                                                             (($T)(x)),$T);
        }
        
        
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
public void
                                                                                             write__0$1x10$lang$Byte$2(
                                                                                             final x10.array.Array buf){
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final int t50386 =
              ((x10.array.Array<x10.core.Byte>)buf).
                size;
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
this.write__0$1x10$lang$Byte$2(((x10.array.Array)(buf)),
                                                                                                                              (int)(0),
                                                                                                                              (int)(t50386));
        }
        
        
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
public void
                                                                                             write__0$1x10$lang$Byte$2(
                                                                                             final x10.array.Array buf,
                                                                                             final int off,
                                                                                             final int len){
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
int i =
              off;
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
for (;
                                                                                                    true;
                                                                                                    ) {
                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final int t50388 =
                  i;
                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final int t50389 =
                  ((off) + (((int)(len))));
                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final boolean t50394 =
                  ((t50388) < (((int)(t50389))));
                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
if (!(t50394)) {
                    
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
break;
                }
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final int t50395 =
                  i;
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final byte t50396 =
                  x10.core.Byte.$unbox(((x10.array.Array<x10.core.Byte>)buf).$apply$G((int)(t50395)));
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
this.write((byte)(t50396));
                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final int t50397 =
                  i;
                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final int t50398 =
                  ((t50397) + (((int)(1))));
                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
i = t50398;
            }
        }
        
        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final public x10.core.io.OutputStream
                                                                                             getNativeOutputStream(
                                                                                             ){try {return x10.core.io.OutputStream.getNativeOutputStream(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
final public x10.io.Writer
                                                                                             x10$io$Writer$$x10$io$Writer$this(
                                                                                             ){
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"
return x10.io.Writer.this;
        }
        
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"

        // constructor for non-virtual call
        final public x10.io.Writer x10$io$Writer$$init$S() { {
                                                                    
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"

                                                                    
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writer.x10"

                                                                }
                                                                return this;
                                                                }
        
        // constructor
        public x10.io.Writer $init(){return x10$io$Writer$$init$S();}
        
    
}
