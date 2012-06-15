package x10.io;

@x10.core.X10Generated public class FilterReader extends x10.io.Reader implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, FilterReader.class);
    
    public static final x10.rtt.RuntimeType<FilterReader> $RTT = x10.rtt.NamedType.<FilterReader> make(
    "x10.io.FilterReader", /* base class */FilterReader.class
    , /* parents */ new x10.rtt.Type[] {x10.io.Reader.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(FilterReader $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + FilterReader.class + " calling"); } 
        x10.io.Reader.$_deserialize_body($_obj, $deserializer);
        x10.io.Reader r = (x10.io.Reader) $deserializer.readRef();
        $_obj.r = r;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        FilterReader $_obj = new FilterReader((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (r instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.r);
        } else {
        $serializer.write(this.r);
        }
        
    }
    
    // constructor just for allocation
    public FilterReader(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
public x10.io.Reader r;
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
public x10.io.Reader
                                                                                                   inner(
                                                                                                   ){
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
final x10.io.Reader t49511 =
              ((x10.io.Reader)(r));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
return t49511;
        }
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
// creation method for java code (1-phase java constructor)
        public FilterReader(final x10.io.Reader r){this((java.lang.System[]) null);
                                                       $init(r);}
        
        // constructor for non-virtual call
        final public x10.io.FilterReader x10$io$FilterReader$$init$S(final x10.io.Reader r) { {
                                                                                                     
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
super.$init();
                                                                                                     
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"

                                                                                                     
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
this.r = ((x10.io.Reader)(r));
                                                                                                 }
                                                                                                 return this;
                                                                                                 }
        
        // constructor
        public x10.io.FilterReader $init(final x10.io.Reader r){return x10$io$FilterReader$$init$S(r);}
        
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
public void
                                                                                                   close(
                                                                                                   ){
            
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
final x10.io.Reader t49512 =
              ((x10.io.Reader)(r));
            
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
t49512.close();
        }
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
public byte
                                                                                                   read$O(
                                                                                                   ){
            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
final x10.io.Reader t49513 =
              ((x10.io.Reader)(r));
            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
final byte t49514 =
              t49513.read$O();
            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
return t49514;
        }
        
        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
public int
                                                                                                   available$O(
                                                                                                   ){
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
final x10.io.Reader t49515 =
              ((x10.io.Reader)(r));
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
final int t49516 =
              t49515.available$O();
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
return t49516;
        }
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
public void
                                                                                                   skip(
                                                                                                   final int off){
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
final x10.io.Reader t49517 =
              ((x10.io.Reader)(r));
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
t49517.skip((int)(off));
        }
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
public void
                                                                                                   mark(
                                                                                                   final int off){
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
final x10.io.Reader t49518 =
              ((x10.io.Reader)(r));
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
t49518.mark((int)(off));
        }
        
        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
public void
                                                                                                   reset(
                                                                                                   ){
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
final x10.io.Reader t49519 =
              ((x10.io.Reader)(r));
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
t49519.reset();
        }
        
        
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
public boolean
                                                                                                   markSupported$O(
                                                                                                   ){
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
final x10.io.Reader t49520 =
              ((x10.io.Reader)(r));
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
final boolean t49521 =
              t49520.markSupported$O();
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
return t49521;
        }
        
        
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
final public x10.io.FilterReader
                                                                                                   x10$io$FilterReader$$x10$io$FilterReader$this(
                                                                                                   ){
            
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FilterReader.x10"
return x10.io.FilterReader.this;
        }
    
}
