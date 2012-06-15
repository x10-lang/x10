package x10.io;


@x10.core.X10Generated public class OutputStreamWriter extends x10.io.Writer implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, OutputStreamWriter.class);
    
    public static final x10.rtt.RuntimeType<OutputStreamWriter> $RTT = x10.rtt.NamedType.<OutputStreamWriter> make(
    "x10.io.OutputStreamWriter", /* base class */OutputStreamWriter.class
    , /* parents */ new x10.rtt.Type[] {x10.io.Writer.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(OutputStreamWriter $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + OutputStreamWriter.class + " calling"); } 
        x10.io.Writer.$_deserialize_body($_obj, $deserializer);
        x10.core.io.OutputStream out = (x10.core.io.OutputStream) $deserializer.readRef();
        $_obj.out = out;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        OutputStreamWriter $_obj = new OutputStreamWriter((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (out instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.out);
        } else {
        $serializer.write(this.out);
        }
        
    }
    
    // constructor just for allocation
    public OutputStreamWriter(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
;
        
        
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
public x10.core.io.OutputStream out;
        
        
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
public x10.core.io.OutputStream
                                                                                                         stream(
                                                                                                         ){
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
final x10.core.io.OutputStream t50027 =
              ((x10.core.io.OutputStream)(out));
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
return t50027;
        }
        
        
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
// creation method for java code (1-phase java constructor)
        public OutputStreamWriter(final x10.core.io.OutputStream out){this((java.lang.System[]) null);
                                                                          $init(out);}
        
        // constructor for non-virtual call
        final public x10.io.OutputStreamWriter x10$io$OutputStreamWriter$$init$S(final x10.core.io.OutputStream out) { {
                                                                                                                              
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
super.$init();
                                                                                                                              
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"

                                                                                                                              
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
this.out = ((x10.core.io.OutputStream)(out));
                                                                                                                          }
                                                                                                                          return this;
                                                                                                                          }
        
        // constructor
        public x10.io.OutputStreamWriter $init(final x10.core.io.OutputStream out){return x10$io$OutputStreamWriter$$init$S(out);}
        
        
        
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
public void
                                                                                                         flush(
                                                                                                         ){
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
final x10.core.io.OutputStream t50028 =
              ((x10.core.io.OutputStream)(out));
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
t50028.flush();
        }
        
        
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
public void
                                                                                                         close(
                                                                                                         ){
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
final x10.core.io.OutputStream t50029 =
              ((x10.core.io.OutputStream)(out));
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
t50029.close();
        }
        
        
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
public void
                                                                                                         write(
                                                                                                         final byte x){
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
final x10.core.io.OutputStream t50030 =
              ((x10.core.io.OutputStream)(out));
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
final int t50031 =
              ((int)(byte)(((byte)(x))));
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
t50030.write((int)(t50031));
        }
        
        
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
public void
                                                                                                         write__0$1x10$lang$Byte$2(
                                                                                                         final x10.array.Array buf){
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
final x10.core.io.OutputStream t50032 =
              ((x10.core.io.OutputStream)(out));
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
t50032.write__0$1x10$lang$Byte$2(((x10.array.Array)(buf)));
        }
        
        
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
public void
                                                                                                         write__0$1x10$lang$Byte$2(
                                                                                                         final x10.array.Array buf,
                                                                                                         final int off,
                                                                                                         final int len){
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
final x10.core.io.OutputStream t50033 =
              ((x10.core.io.OutputStream)(out));
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
t50033.write__0$1x10$lang$Byte$2(((x10.array.Array)(buf)),
                                                                                                                                            (int)(off),
                                                                                                                                            (int)(len));
        }
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
final public x10.io.OutputStreamWriter
                                                                                                         x10$io$OutputStreamWriter$$x10$io$OutputStreamWriter$this(
                                                                                                         ){
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/OutputStreamWriter.x10"
return x10.io.OutputStreamWriter.this;
        }
    
}
