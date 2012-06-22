package x10.io;


@x10.core.X10Generated public class FileReader extends x10.io.InputStreamReader implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, FileReader.class);
    
    public static final x10.rtt.RuntimeType<FileReader> $RTT = x10.rtt.NamedType.<FileReader> make(
    "x10.io.FileReader", /* base class */FileReader.class
    , /* parents */ new x10.rtt.Type[] {x10.io.InputStreamReader.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(FileReader $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + FileReader.class + " calling"); } 
        x10.io.InputStreamReader.$_deserialize_body($_obj, $deserializer);
        x10.io.File file = (x10.io.File) $deserializer.readRef();
        $_obj.file = file;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        FileReader $_obj = new FileReader((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (file instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.file);
        } else {
        $serializer.write(this.file);
        }
        
    }
    
    // constructor just for allocation
    public FileReader(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileReader.x10"
public x10.io.File file;
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileReader.x10"
;
        
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileReader.x10"
// creation method for java code (1-phase java constructor)
        public FileReader(final x10.io.File file){this((java.lang.System[]) null);
                                                      $init(file);}
        
        // constructor for non-virtual call
        final public x10.io.FileReader x10$io$FileReader$$init$S(final x10.io.File file) { {
                                                                                                  
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileReader.x10"
final java.lang.String t49495 =
                                                                                                    file.getPath$O();
                                                                                                  
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileReader.x10"
final x10.core.io.FileInputStream t49496 =
                                                                                                    ((x10.core.io.FileInputStream)(new x10.core.io.FileInputStream((java.lang.System[]) null).x10$io$FileReader$FileInputStream$$init$S(t49495)));
                                                                                                  
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileReader.x10"
super.$init(((x10.core.io.InputStream)(t49496)));
                                                                                                  
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileReader.x10"

                                                                                                  
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileReader.x10"
this.file = ((x10.io.File)(file));
                                                                                              }
                                                                                              return this;
                                                                                              }
        
        // constructor
        public x10.io.FileReader $init(final x10.io.File file){return x10$io$FileReader$$init$S(file);}
        
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileReader.x10"
final public x10.io.FileReader
                                                                                                 x10$io$FileReader$$x10$io$FileReader$this(
                                                                                                 ){
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileReader.x10"
return x10.io.FileReader.this;
        }
    
}
