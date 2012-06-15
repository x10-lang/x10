package x10.io;


@x10.core.X10Generated public class File extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, File.class);
    
    public static final x10.rtt.RuntimeType<File> $RTT = x10.rtt.NamedType.<File> make(
    "x10.io.File", /* base class */File.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(File $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + File.class + " calling"); } 
        x10.io.File parent = (x10.io.File) $deserializer.readRef();
        $_obj.parent = parent;
        java.lang.String name = (java.lang.String) $deserializer.readRef();
        $_obj.name = name;
        $_obj.absolute = $deserializer.readBoolean();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        File $_obj = new File((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (parent instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.parent);
        } else {
        $serializer.write(this.parent);
        }
        $serializer.write(this.name);
        $serializer.write(this.absolute);
        
    }
    
    // constructor just for allocation
    public File(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
;
        
        
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final public static char SEPARATOR = '/';
        
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final public static char PATH_SEPARATOR = ':';
        
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public x10.io.File parent;
        
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public java.lang.String name;
        
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public boolean absolute;
        
        
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
// creation method for java code (1-phase java constructor)
        public File(final java.lang.String fullName){this((java.lang.System[]) null);
                                                         $init(fullName);}
        
        // constructor for non-virtual call
        final public x10.io.File x10$io$File$$init$S(final java.lang.String fullName) { {
                                                                                               
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"

                                                                                               
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"

                                                                                               
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final char t49413 =
                                                                                                 x10.io.File.SEPARATOR;
                                                                                               
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final int i =
                                                                                                 (fullName).lastIndexOf(((char)(t49413)));
                                                                                               
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final boolean t49423 =
                                                                                                 ((int) i) ==
                                                                                               ((int) 0);
                                                                                               
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
if (t49423) {
                                                                                                   
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
this.parent = null;
                                                                                                   
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
this.name = ((java.lang.String)(fullName));
                                                                                                   
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
this.absolute = true;
                                                                                               } else {
                                                                                                   
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final boolean t49422 =
                                                                                                     ((i) >= (((int)(0))));
                                                                                                   
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
if (t49422) {
                                                                                                       
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final java.lang.String t49414 =
                                                                                                         (fullName).substring(((int)(0)), ((int)(i)));
                                                                                                       
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.io.File t49415 =
                                                                                                         ((x10.io.File)(new x10.io.File((java.lang.System[]) null).$init(t49414)));
                                                                                                       
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
this.parent = ((x10.io.File)(t49415));
                                                                                                       
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final int t49416 =
                                                                                                         ((i) + (((int)(1))));
                                                                                                       
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final int t49417 =
                                                                                                         (fullName).length();
                                                                                                       
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final java.lang.String t49418 =
                                                                                                         (fullName).substring(((int)(t49416)), ((int)(t49417)));
                                                                                                       
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
this.name = ((java.lang.String)(t49418));
                                                                                                       
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final char t49419 =
                                                                                                         (fullName).charAt(((int)(0)));
                                                                                                       
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final char t49420 =
                                                                                                         x10.io.File.PATH_SEPARATOR;
                                                                                                       
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final boolean t49421 =
                                                                                                         ((char) t49419) ==
                                                                                                       ((char) t49420);
                                                                                                       
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
this.absolute = t49421;
                                                                                                   } else {
                                                                                                       
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
this.parent = null;
                                                                                                       
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
this.name = ((java.lang.String)(fullName));
                                                                                                       
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
this.absolute = false;
                                                                                                   }
                                                                                               }
                                                                                           }
                                                                                           return this;
                                                                                           }
        
        // constructor
        public x10.io.File $init(final java.lang.String fullName){return x10$io$File$$init$S(fullName);}
        
        
        
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
// creation method for java code (1-phase java constructor)
        public File(final x10.io.File p,
                    final java.lang.String n){this((java.lang.System[]) null);
                                                  $init(p,n);}
        
        // constructor for non-virtual call
        final public x10.io.File x10$io$File$$init$S(final x10.io.File p,
                                                     final java.lang.String n) { {
                                                                                        
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"

                                                                                        
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"

                                                                                        
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
assert ((p) != (null));
                                                                                        
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
this.parent = ((x10.io.File)(p));
                                                                                        
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
this.name = ((java.lang.String)(n));
                                                                                        
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
boolean t49424 =
                                                                                          ((p) != (null));
                                                                                        
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
if (t49424) {
                                                                                            
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
t49424 = p.
                                                                                                                                                                                         absolute;
                                                                                        }
                                                                                        
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final boolean t49425 =
                                                                                          t49424;
                                                                                        
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
this.absolute = t49425;
                                                                                    }
                                                                                    return this;
                                                                                    }
        
        // constructor
        public x10.io.File $init(final x10.io.File p,
                                 final java.lang.String n){return x10$io$File$$init$S(p,n);}
        
        
        
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public x10.io.ReaderIterator<java.lang.String>
                                                                                            lines(
                                                                                            ){
            
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.io.FileReader t49426 =
              this.openRead();
            
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.io.ReaderIterator<java.lang.String> t49427 =
              t49426.lines();
            
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49427;
        }
        
        
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public x10.io.ReaderIterator<x10.core.Char>
                                                                                            chars(
                                                                                            ){
            
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.io.FileReader t49428 =
              this.openRead();
            
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.io.ReaderIterator<x10.core.Char> t49429 =
              t49428.chars();
            
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49429;
        }
        
        
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public x10.io.ReaderIterator<x10.core.Byte>
                                                                                            bytes(
                                                                                            ){
            
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.io.FileReader t49430 =
              this.openRead();
            
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.io.ReaderIterator<x10.core.Byte> t49431 =
              t49430.bytes();
            
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49431;
        }
        
        
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public x10.io.FileReader
                                                                                            openRead(
                                                                                            ){
            
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.io.FileReader t49432 =
              ((x10.io.FileReader)(new x10.io.FileReader((java.lang.System[]) null).$init(((x10.io.File)(this)))));
            
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49432;
        }
        
        
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public x10.io.FileWriter
                                                                                            openWrite(
                                                                                            ){
            
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.io.FileWriter t49433 =
              this.openWrite((boolean)(false));
            
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49433;
        }
        
        
//#line 170 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public x10.io.FileWriter
                                                                                            openWrite(
                                                                                            final boolean append){
            
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.io.FileWriter t49434 =
              ((x10.io.FileWriter)(new x10.io.FileWriter((java.lang.System[]) null).$init(((x10.io.File)(this)),
                                                                                          ((boolean)(append)))));
            
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49434;
        }
        
        
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public x10.io.Printer
                                                                                            printer(
                                                                                            ){
            
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.io.Printer t49435 =
              this.printer((boolean)(false));
            
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49435;
        }
        
        
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public x10.io.Printer
                                                                                            printer(
                                                                                            final boolean append){
            
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.io.FileWriter t49436 =
              this.openWrite((boolean)(append));
            
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.io.Printer t49437 =
              ((x10.io.Printer)(new x10.io.Printer((java.lang.System[]) null).$init(((x10.io.Writer)(t49436)))));
            
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49437;
        }
        
        
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public java.lang.String
                                                                                            getName$O(
                                                                                            ){
            
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final java.lang.String t49438 =
              ((java.lang.String)(name));
            
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49438;
        }
        
        
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public x10.io.File
                                                                                            getParentFile(
                                                                                            ){
            
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.io.File t49439 =
              ((x10.io.File)(parent));
            
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49439;
        }
        
        
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public java.lang.String
                                                                                            getPath$O(
                                                                                            ){
            
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.io.File t49440 =
              ((x10.io.File)(parent));
            
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final boolean t49446 =
              ((t49440) == (null));
            
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
java.lang.String t49447 =
               null;
            
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
if (t49446) {
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
t49447 = name;
            } else {
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.io.File t49441 =
                  ((x10.io.File)(parent));
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final java.lang.String t49442 =
                  t49441.getPath$O();
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final char t49443 =
                  x10.io.File.SEPARATOR;
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final java.lang.String t49444 =
                  ((t49442) + ((x10.core.Char.$box(t49443))));
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final java.lang.String t49445 =
                  ((java.lang.String)(name));
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
t49447 = ((t49444) + (t49445));
            }
            
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final java.lang.String t49448 =
              t49447;
            
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49448;
        }
        
        
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public boolean
                                                                                            isAbsolute$O(
                                                                                            ){
            
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final boolean t49449 =
              absolute;
            
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49449;
        }
        
        
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public x10.core.io.NativeFile
                                                                                            nativeFile(
                                                                                            ){
            
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final java.lang.String t49450 =
              this.getPath$O();
            
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.core.io.NativeFile t49451 =
              ((x10.core.io.NativeFile)(new x10.core.io.NativeFile(t49450)));
            
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49451;
        }
        
        
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public x10.io.File
                                                                                            getAbsoluteFile(
                                                                                            ){
            
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.core.io.NativeFile t49452 =
              ((x10.core.io.NativeFile)(this.nativeFile()));
            
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final java.lang.String t49453 =
              t49452.getAbsolutePath();
            
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.io.File t49454 =
              ((x10.io.File)(new x10.io.File((java.lang.System[]) null).$init(t49453)));
            
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49454;
        }
        
        
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public x10.io.File
                                                                                            getCanonicalFile(
                                                                                            ){
            
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.core.io.NativeFile t49455 =
              ((x10.core.io.NativeFile)(this.nativeFile()));
            
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final java.lang.String t49456 =
              t49455.getCanonicalPath();
            
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.io.File t49457 =
              ((x10.io.File)(new x10.io.File((java.lang.System[]) null).$init(t49456)));
            
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49457;
        }
        
        
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public boolean
                                                                                            exists$O(
                                                                                            ){
            
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.core.io.NativeFile t49458 =
              ((x10.core.io.NativeFile)(this.nativeFile()));
            
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final boolean t49459 =
              t49458.exists();
            
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49459;
        }
        
        
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public boolean
                                                                                            isSymbolicLink$O(
                                                                                            ){
            
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.lang.UnsupportedOperationException t49460 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
            
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
throw t49460;
        }
        
        
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public boolean
                                                                                            isAlias$O(
                                                                                            ){
            
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.lang.UnsupportedOperationException t49461 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
            
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
throw t49461;
        }
        
        
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public boolean
                                                                                            hardLinkCount$O(
                                                                                            ){
            
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.lang.UnsupportedOperationException t49462 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
            
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
throw t49462;
        }
        
        
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public long
                                                                                            inodeNumber$O(
                                                                                            ){
            
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.lang.UnsupportedOperationException t49463 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
            
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
throw t49463;
        }
        
        
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public int
                                                                                            permissions$O(
                                                                                            ){
            
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.lang.UnsupportedOperationException t49464 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
            
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
throw t49464;
        }
        
        
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public boolean
                                                                                            isDirectory$O(
                                                                                            ){
            
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.core.io.NativeFile t49465 =
              ((x10.core.io.NativeFile)(this.nativeFile()));
            
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final boolean t49466 =
              t49465.isDirectory();
            
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49466;
        }
        
        
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public boolean
                                                                                            isFile$O(
                                                                                            ){
            
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.core.io.NativeFile t49467 =
              ((x10.core.io.NativeFile)(this.nativeFile()));
            
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final boolean t49468 =
              t49467.isFile();
            
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49468;
        }
        
        
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public boolean
                                                                                            isHidden$O(
                                                                                            ){
            
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.core.io.NativeFile t49469 =
              ((x10.core.io.NativeFile)(this.nativeFile()));
            
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final boolean t49470 =
              t49469.isHidden();
            
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49470;
        }
        
        
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public long
                                                                                            lastModified$O(
                                                                                            ){
            
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.core.io.NativeFile t49471 =
              ((x10.core.io.NativeFile)(this.nativeFile()));
            
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final long t49472 =
              t49471.lastModified();
            
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49472;
        }
        
        
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public boolean
                                                                                            setLastModified$O(
                                                                                            final long t){
            
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.core.io.NativeFile t49473 =
              ((x10.core.io.NativeFile)(this.nativeFile()));
            
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final boolean t49474 =
              t49473.setLastModified(((long)(t)));
            
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49474;
        }
        
        
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public long
                                                                                            size$O(
                                                                                            ){
            
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.core.io.NativeFile t49475 =
              ((x10.core.io.NativeFile)(this.nativeFile()));
            
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final long t49476 =
              t49475.length();
            
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49476;
        }
        
        
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public int
                                                                                            compareTo$O(
                                                                                            final x10.io.File id$91){
            
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.lang.UnsupportedOperationException t49477 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
            
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
throw t49477;
        }
        
        
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public boolean
                                                                                            canRead$O(
                                                                                            ){
            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.core.io.NativeFile t49478 =
              ((x10.core.io.NativeFile)(this.nativeFile()));
            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final boolean t49479 =
              t49478.canRead();
            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49479;
        }
        
        
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public boolean
                                                                                            canWrite$O(
                                                                                            ){
            
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.core.io.NativeFile t49480 =
              ((x10.core.io.NativeFile)(this.nativeFile()));
            
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final boolean t49481 =
              t49480.canWrite();
            
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49481;
        }
        
        
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public boolean
                                                                                            delete$O(
                                                                                            ){
            
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.core.io.NativeFile t49482 =
              ((x10.core.io.NativeFile)(this.nativeFile()));
            
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final boolean t49483 =
              t49482.delete();
            
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49483;
        }
        
        
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public x10.array.Array<java.lang.String>
                                                                                            list(
                                                                                            ){
            
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.core.io.NativeFile t49484 =
              ((x10.core.io.NativeFile)(this.nativeFile()));
            
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.array.Array<java.lang.String> t49485 =
              ((x10.array.Array)(t49484.listInternal()));
            
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49485;
        }
        
        
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public boolean
                                                                                            mkdir$O(
                                                                                            ){
            
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.core.io.NativeFile t49486 =
              ((x10.core.io.NativeFile)(this.nativeFile()));
            
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final boolean t49487 =
              t49486.mkdir();
            
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49487;
        }
        
        
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public boolean
                                                                                            mkdirs$O(
                                                                                            ){
            
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.core.io.NativeFile t49488 =
              ((x10.core.io.NativeFile)(this.nativeFile()));
            
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final boolean t49489 =
              t49488.mkdirs();
            
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49489;
        }
        
        
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
public boolean
                                                                                            renameTo$O(
                                                                                            final x10.io.File dest){
            
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.core.io.NativeFile t49490 =
              ((x10.core.io.NativeFile)(this.nativeFile()));
            
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final x10.core.io.NativeFile t49491 =
              ((x10.core.io.NativeFile)(dest.nativeFile()));
            
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final boolean t49492 =
              t49490.renameTo(t49491);
            
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return t49492;
        }
        
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
final public x10.io.File
                                                                                           x10$io$File$$x10$io$File$this(
                                                                                           ){
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/File.x10"
return x10.io.File.this;
        }
        
        public static char
          getInitialized$SEPARATOR(
          ){
            return x10.io.File.SEPARATOR;
        }
        
        public static char
          getInitialized$PATH_SEPARATOR(
          ){
            return x10.io.File.PATH_SEPARATOR;
        }
    
}
