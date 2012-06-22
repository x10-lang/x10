package x10.io;

@x10.core.X10Generated public class FilterWriter extends x10.io.Writer implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, FilterWriter.class);
    
    public static final x10.rtt.RuntimeType<FilterWriter> $RTT = x10.rtt.NamedType.<FilterWriter> make(
    "x10.io.FilterWriter", /* base class */FilterWriter.class
    , /* parents */ new x10.rtt.Type[] {x10.io.Writer.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(FilterWriter $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + FilterWriter.class + " calling"); } 
        x10.io.Writer.$_deserialize_body($_obj, $deserializer);
        x10.io.Writer w = (x10.io.Writer) $deserializer.readRef();
        $_obj.w = w;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        FilterWriter $_obj = new FilterWriter((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (w instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.w);
        } else {
        $serializer.write(this.w);
        }
        
    }
    
    // constructor just for allocation
    public FilterWriter(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterWriter.x10"
public x10.io.Writer w;
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterWriter.x10"
public x10.io.Writer
                                                                                                   inner(
                                                                                                   ){
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterWriter.x10"
final x10.io.Writer t49522 =
              ((x10.io.Writer)(w));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterWriter.x10"
return t49522;
        }
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterWriter.x10"
// creation method for java code (1-phase java constructor)
        public FilterWriter(final x10.io.Writer w){this((java.lang.System[]) null);
                                                       $init(w);}
        
        // constructor for non-virtual call
        final public x10.io.FilterWriter x10$io$FilterWriter$$init$S(final x10.io.Writer w) { {
                                                                                                     
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterWriter.x10"
super.$init();
                                                                                                     
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterWriter.x10"

                                                                                                     
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterWriter.x10"
this.w = ((x10.io.Writer)(w));
                                                                                                 }
                                                                                                 return this;
                                                                                                 }
        
        // constructor
        public x10.io.FilterWriter $init(final x10.io.Writer w){return x10$io$FilterWriter$$init$S(w);}
        
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterWriter.x10"
public void
                                                                                                   close(
                                                                                                   ){
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterWriter.x10"
final x10.io.Writer t49523 =
              ((x10.io.Writer)(w));
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterWriter.x10"
t49523.close();
        }
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterWriter.x10"
public void
                                                                                                   flush(
                                                                                                   ){
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterWriter.x10"
final x10.io.Writer t49524 =
              ((x10.io.Writer)(w));
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterWriter.x10"
t49524.flush();
        }
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterWriter.x10"
public void
                                                                                                   write(
                                                                                                   final byte b){
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterWriter.x10"
final x10.io.Writer t49525 =
              ((x10.io.Writer)(w));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterWriter.x10"
t49525.write((byte)(b));
        }
        
        
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterWriter.x10"
final public x10.io.FilterWriter
                                                                                                   x10$io$FilterWriter$$x10$io$FilterWriter$this(
                                                                                                   ){
            
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterWriter.x10"
return x10.io.FilterWriter.this;
        }
    
}
