package x10.io;


@x10.core.X10Generated abstract public class FileSystem extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, FileSystem.class);
    
    public static final x10.rtt.RuntimeType<FileSystem> $RTT = x10.rtt.NamedType.<FileSystem> make(
    "x10.io.FileSystem", /* base class */FileSystem.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(FileSystem $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + FileSystem.class + " calling"); } 
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
    public FileSystem(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
final public static char SEPARATOR_CHAR = '/';
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
final public static java.lang.String SEPARATOR = "/";
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
final public static char PATH_SEPARATOR_CHAR = ':';
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
final public static java.lang.String PATH_SEPARATOR = ":";
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
public void
                                                                                                 delete(
                                                                                                 final x10.io.File id$92){
            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
final x10.lang.UnsupportedOperationException t49497 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
throw t49497;
        }
        
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
public void
                                                                                                 deleteOnExit(
                                                                                                 final x10.io.File id$93){
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
final x10.lang.UnsupportedOperationException t49498 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
throw t49498;
        }
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
public void
                                                                                                 rename(
                                                                                                 final x10.io.File f,
                                                                                                 final x10.io.File t){
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
final x10.lang.UnsupportedOperationException t49499 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
throw t49499;
        }
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
public void
                                                                                                 mkdir(
                                                                                                 final x10.io.File id$94){
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
final x10.lang.UnsupportedOperationException t49500 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
throw t49500;
        }
        
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
public void
                                                                                                 mkdirs(
                                                                                                 final x10.io.File id$95){
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
final x10.lang.UnsupportedOperationException t49501 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
throw t49501;
        }
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
public boolean
                                                                                                 exists$O(
                                                                                                 final x10.io.File id$96){
            
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
final x10.lang.UnsupportedOperationException t49502 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
            
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
throw t49502;
        }
        
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
public long
                                                                                                 size$O(
                                                                                                 final x10.io.File id$97){
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
final x10.lang.UnsupportedOperationException t49503 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
throw t49503;
        }
        
        
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
public x10.array.Array<x10.io.File>
                                                                                                 listFiles(
                                                                                                 final x10.io.File id$98){
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
final x10.lang.UnsupportedOperationException t49504 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
throw t49504;
        }
        
        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
public x10.array.Array<x10.io.File>
                                                                                                 listFiles__1$1x10$io$File$3x10$lang$Boolean$2(
                                                                                                 final x10.io.File id$99,
                                                                                                 final x10.core.fun.Fun_0_1 id$101){
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
final x10.lang.UnsupportedOperationException t49505 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
throw t49505;
        }
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
final public x10.io.FileSystem
                                                                                                 x10$io$FileSystem$$x10$io$FileSystem$this(
                                                                                                 ){
            
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"
return x10.io.FileSystem.this;
        }
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"

        // constructor for non-virtual call
        final public x10.io.FileSystem x10$io$FileSystem$$init$S() { {
                                                                            
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"

                                                                            
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/FileSystem.x10"

                                                                        }
                                                                        return this;
                                                                        }
        
        // constructor
        public x10.io.FileSystem $init(){return x10$io$FileSystem$$init$S();}
        
        
        public static char
          getInitialized$SEPARATOR_CHAR(
          ){
            return x10.io.FileSystem.SEPARATOR_CHAR;
        }
        
        public static java.lang.String
          getInitialized$SEPARATOR(
          ){
            return x10.io.FileSystem.SEPARATOR;
        }
        
        public static char
          getInitialized$PATH_SEPARATOR_CHAR(
          ){
            return x10.io.FileSystem.PATH_SEPARATOR_CHAR;
        }
        
        public static java.lang.String
          getInitialized$PATH_SEPARATOR(
          ){
            return x10.io.FileSystem.PATH_SEPARATOR;
        }
    
}
