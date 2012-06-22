package x10.io;


@x10.core.X10Generated public class InputStreamReader extends x10.io.Reader implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, InputStreamReader.class);
    
    public static final x10.rtt.RuntimeType<InputStreamReader> $RTT = x10.rtt.NamedType.<InputStreamReader> make(
    "x10.io.InputStreamReader", /* base class */InputStreamReader.class
    , /* parents */ new x10.rtt.Type[] {x10.io.Reader.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(InputStreamReader $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + InputStreamReader.class + " calling"); } 
        x10.io.Reader.$_deserialize_body($_obj, $deserializer);
        x10.core.io.InputStream stream = (x10.core.io.InputStream) $deserializer.readRef();
        $_obj.stream = stream;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        InputStreamReader $_obj = new InputStreamReader((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (stream instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.stream);
        } else {
        $serializer.write(this.stream);
        }
        
    }
    
    // constructor just for allocation
    public InputStreamReader(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
public x10.core.io.InputStream stream;
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
;
        
        
        
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
// creation method for java code (1-phase java constructor)
        public InputStreamReader(final x10.core.io.InputStream stream){this((java.lang.System[]) null);
                                                                           $init(stream);}
        
        // constructor for non-virtual call
        final public x10.io.InputStreamReader x10$io$InputStreamReader$$init$S(final x10.core.io.InputStream stream) { {
                                                                                                                              
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
super.$init();
                                                                                                                              
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"

                                                                                                                              
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
this.stream = ((x10.core.io.InputStream)(stream));
                                                                                                                          }
                                                                                                                          return this;
                                                                                                                          }
        
        // constructor
        public x10.io.InputStreamReader $init(final x10.core.io.InputStream stream){return x10$io$InputStreamReader$$init$S(stream);}
        
        
        
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
public x10.core.io.InputStream
                                                                                                        stream(
                                                                                                        ){
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
final x10.core.io.InputStream t49526 =
              ((x10.core.io.InputStream)(stream));
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
return t49526;
        }
        
        
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
public void
                                                                                                        close(
                                                                                                        ){
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
final x10.core.io.InputStream t49527 =
              ((x10.core.io.InputStream)(stream));
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
t49527.close();
        }
        
        
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
public byte
                                                                                                        read$O(
                                                                                                        ){
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
final x10.core.io.InputStream t49528 =
              ((x10.core.io.InputStream)(stream));
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
final int n =
              t49528.read$O();
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
final boolean t49530 =
              ((int) n) ==
            ((int) -1);
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
if (t49530) {
                
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
final x10.io.EOFException t49529 =
                  ((x10.io.EOFException)(new x10.io.EOFException()));
                
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
throw t49529;
            }
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
final byte t49531 =
              ((byte)(int)(((int)(n))));
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
return t49531;
        }
        
        
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
public int
                                                                                                        available$O(
                                                                                                        ){
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
final x10.core.io.InputStream t49532 =
              ((x10.core.io.InputStream)(stream));
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
final int t49533 =
              t49532.available$O();
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
return t49533;
        }
        
        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
public void
                                                                                                        skip(
                                                                                                        final int off){
            
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
final x10.core.io.InputStream t49534 =
              ((x10.core.io.InputStream)(stream));
            
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
t49534.skip((int)(off));
        }
        
        
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
public void
                                                                                                        mark(
                                                                                                        final int off){
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
final x10.core.io.InputStream t49535 =
              ((x10.core.io.InputStream)(stream));
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
t49535.mark((int)(off));
        }
        
        
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
public void
                                                                                                        reset(
                                                                                                        ){
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
final x10.core.io.InputStream t49536 =
              ((x10.core.io.InputStream)(stream));
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
t49536.reset();
        }
        
        
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
public boolean
                                                                                                        markSupported$O(
                                                                                                        ){
            
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
final x10.core.io.InputStream t49537 =
              ((x10.core.io.InputStream)(stream));
            
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
final boolean t49538 =
              t49537.markSupported$O();
            
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
return t49538;
        }
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
final public x10.io.InputStreamReader
                                                                                                        x10$io$InputStreamReader$$x10$io$InputStreamReader$this(
                                                                                                        ){
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/InputStreamReader.x10"
return x10.io.InputStreamReader.this;
        }
    
}
