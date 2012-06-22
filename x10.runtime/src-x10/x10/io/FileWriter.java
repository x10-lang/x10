package x10.io;


@x10.core.X10Generated public class FileWriter extends x10.io.OutputStreamWriter implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, FileWriter.class);
    
    public static final x10.rtt.RuntimeType<FileWriter> $RTT = x10.rtt.NamedType.<FileWriter> make(
    "x10.io.FileWriter", /* base class */FileWriter.class
    , /* parents */ new x10.rtt.Type[] {x10.io.OutputStreamWriter.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(FileWriter $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + FileWriter.class + " calling"); } 
        x10.io.OutputStreamWriter.$_deserialize_body($_obj, $deserializer);
        x10.io.File file = (x10.io.File) $deserializer.readRef();
        $_obj.file = file;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        FileWriter $_obj = new FileWriter((java.lang.System[]) null);
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
    public FileWriter(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileWriter.x10"
;
        
        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileWriter.x10"
public x10.io.File file;
        
        
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileWriter.x10"
private static x10.core.io.OutputStream
                                                                                                 make(
                                                                                                 final java.lang.String path,
                                                                                                 final boolean append){
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileWriter.x10"
final x10.core.io.FileOutputStream t49506 =
              ((x10.core.io.FileOutputStream)(new x10.core.io.FileOutputStream((java.lang.System[]) null).x10$io$FileReader$FileOutputStream$$init$S(path, ((boolean)(append)))));
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileWriter.x10"
return t49506;
        }
        
        public static x10.core.io.OutputStream
          make$P(
          final java.lang.String path,
          final boolean append){
            return x10.io.FileWriter.make(((java.lang.String)(path)),
                                          (boolean)(append));
        }
        
        
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileWriter.x10"
// creation method for java code (1-phase java constructor)
        public FileWriter(final x10.io.File file){this((java.lang.System[]) null);
                                                      $init(file);}
        
        // constructor for non-virtual call
        final public x10.io.FileWriter x10$io$FileWriter$$init$S(final x10.io.File file) { {
                                                                                                  
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileWriter.x10"
this.$init(((x10.io.File)(file)),
                                                                                                                                                                                                    ((boolean)(false)));
                                                                                              }
                                                                                              return this;
                                                                                              }
        
        // constructor
        public x10.io.FileWriter $init(final x10.io.File file){return x10$io$FileWriter$$init$S(file);}
        
        
        
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileWriter.x10"
// creation method for java code (1-phase java constructor)
        public FileWriter(final x10.io.File file,
                          final boolean append){this((java.lang.System[]) null);
                                                    $init(file,append);}
        
        // constructor for non-virtual call
        final public x10.io.FileWriter x10$io$FileWriter$$init$S(final x10.io.File file,
                                                                 final boolean append) { {
                                                                                                
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileWriter.x10"
final java.lang.String t49509 =
                                                                                                  file.getPath$O();
                                                                                                
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileWriter.x10"
final x10.core.io.OutputStream t49510 =
                                                                                                  x10.io.FileWriter.make(((java.lang.String)(t49509)),
                                                                                                                         (boolean)(append));
                                                                                                
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileWriter.x10"
super.$init(t49510);
                                                                                                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileWriter.x10"

                                                                                                
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileWriter.x10"
this.file = ((x10.io.File)(file));
                                                                                            }
                                                                                            return this;
                                                                                            }
        
        // constructor
        public x10.io.FileWriter $init(final x10.io.File file,
                                       final boolean append){return x10$io$FileWriter$$init$S(file,append);}
        
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileWriter.x10"
final public x10.io.FileWriter
                                                                                                 x10$io$FileWriter$$x10$io$FileWriter$this(
                                                                                                 ){
            
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileWriter.x10"
return x10.io.FileWriter.this;
        }
    
}
