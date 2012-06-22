package x10.io;


@x10.core.X10Generated public class StringWriter extends x10.io.Writer implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, StringWriter.class);
    
    public static final x10.rtt.RuntimeType<StringWriter> $RTT = x10.rtt.NamedType.<StringWriter> make(
    "x10.io.StringWriter", /* base class */StringWriter.class
    , /* parents */ new x10.rtt.Type[] {x10.io.Writer.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(StringWriter $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + StringWriter.class + " calling"); } 
        x10.io.Writer.$_deserialize_body($_obj, $deserializer);
        x10.util.StringBuilder b = (x10.util.StringBuilder) $deserializer.readRef();
        $_obj.b = b;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        StringWriter $_obj = new StringWriter((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (b instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.b);
        } else {
        $serializer.write(this.b);
        }
        
    }
    
    // constructor just for allocation
    public StringWriter(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/StringWriter.x10"
public x10.util.StringBuilder b;
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/StringWriter.x10"
// creation method for java code (1-phase java constructor)
        public StringWriter(){this((java.lang.System[]) null);
                                  $init();}
        
        // constructor for non-virtual call
        final public x10.io.StringWriter x10$io$StringWriter$$init$S() { {
                                                                                
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/StringWriter.x10"
super.$init();
                                                                                
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/StringWriter.x10"

                                                                                
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/StringWriter.x10"
final x10.util.StringBuilder t50349 =
                                                                                  ((x10.util.StringBuilder)(new x10.util.StringBuilder((java.lang.System[]) null).$init()));
                                                                                
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/StringWriter.x10"
this.b = ((x10.util.StringBuilder)(t50349));
                                                                            }
                                                                            return this;
                                                                            }
        
        // constructor
        public x10.io.StringWriter $init(){return x10$io$StringWriter$$init$S();}
        
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/StringWriter.x10"
public void
                                                                                                   write(
                                                                                                   final byte x){
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/StringWriter.x10"
final x10.util.StringBuilder t50350 =
              ((x10.util.StringBuilder)(b));
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/StringWriter.x10"
final char t50351 =
              ((char) (((byte)(x))));
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/StringWriter.x10"
t50350.add((char)(t50351));
        }
        
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/StringWriter.x10"
public int
                                                                                                   size$O(
                                                                                                   ){
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/StringWriter.x10"
final x10.util.StringBuilder t50352 =
              ((x10.util.StringBuilder)(b));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/StringWriter.x10"
final int t50353 =
              t50352.length$O();
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/StringWriter.x10"
return t50353;
        }
        
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/StringWriter.x10"
public java.lang.String
                                                                                                   result$O(
                                                                                                   ){
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/StringWriter.x10"
final x10.util.StringBuilder t50354 =
              ((x10.util.StringBuilder)(b));
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/StringWriter.x10"
final java.lang.String t50355 =
              t50354.result$O();
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/StringWriter.x10"
return t50355;
        }
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/StringWriter.x10"
public void
                                                                                                   flush(
                                                                                                   ){
            
        }
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/StringWriter.x10"
public void
                                                                                                   close(
                                                                                                   ){
            
        }
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/StringWriter.x10"
final public x10.io.StringWriter
                                                                                                   x10$io$StringWriter$$x10$io$StringWriter$this(
                                                                                                   ){
            
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/StringWriter.x10"
return x10.io.StringWriter.this;
        }
    
}
