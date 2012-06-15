package x10.io;

@x10.core.X10Generated final public class SerialData extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, SerialData.class);
    
    public static final x10.rtt.RuntimeType<SerialData> $RTT = x10.rtt.NamedType.<SerialData> make(
    "x10.io.SerialData", /* base class */SerialData.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(SerialData $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + SerialData.class + " calling"); } 
        java.lang.Object data = (java.lang.Object) $deserializer.readRef();
        $_obj.data = data;
        x10.io.SerialData superclassData = (x10.io.SerialData) $deserializer.readRef();
        $_obj.superclassData = superclassData;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        SerialData $_obj = new SerialData((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (data instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.data);
        } else {
        $serializer.write(this.data);
        }
        if (superclassData instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.superclassData);
        } else {
        $serializer.write(this.superclassData);
        }
        
    }
    
    // constructor just for allocation
    public SerialData(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/SerialData.x10"
public java.lang.Object data;
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/SerialData.x10"
public x10.io.SerialData superclassData;
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/SerialData.x10"
// creation method for java code (1-phase java constructor)
        public SerialData(final java.lang.Object data,
                          final x10.io.SerialData superclassData){this((java.lang.System[]) null);
                                                                      $init(data,superclassData);}
        
        // constructor for non-virtual call
        final public x10.io.SerialData x10$io$SerialData$$init$S(final java.lang.Object data,
                                                                 final x10.io.SerialData superclassData) { {
                                                                                                                  
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/SerialData.x10"

                                                                                                                  
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/SerialData.x10"

                                                                                                                  
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/SerialData.x10"
this.data = ((java.lang.Object)(data));
                                                                                                                  
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/SerialData.x10"
this.superclassData = ((x10.io.SerialData)(superclassData));
                                                                                                              }
                                                                                                              return this;
                                                                                                              }
        
        // constructor
        public x10.io.SerialData $init(final java.lang.Object data,
                                       final x10.io.SerialData superclassData){return x10$io$SerialData$$init$S(data,superclassData);}
        
        
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/SerialData.x10"
public java.lang.String
                                                                                                 toString(
                                                                                                 ){
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/SerialData.x10"
final java.lang.Object t50343 =
              ((java.lang.Object)(data));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/SerialData.x10"
final java.lang.String t50344 =
              (("SerialData(") + (t50343));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/SerialData.x10"
final java.lang.String t50345 =
              ((t50344) + (","));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/SerialData.x10"
final x10.io.SerialData t50346 =
              ((x10.io.SerialData)(superclassData));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/SerialData.x10"
final java.lang.String t50347 =
              ((t50345) + (t50346));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/SerialData.x10"
final java.lang.String t50348 =
              ((t50347) + (")"));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/SerialData.x10"
return t50348;
        }
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/SerialData.x10"
final public x10.io.SerialData
                                                                                                 x10$io$SerialData$$x10$io$SerialData$this(
                                                                                                 ){
            
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/SerialData.x10"
return x10.io.SerialData.this;
        }
    
}
