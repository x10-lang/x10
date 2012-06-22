package x10.io;


@x10.core.X10Generated public class Printer extends x10.io.FilterWriter implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Printer.class);
    
    public static final x10.rtt.RuntimeType<Printer> $RTT = x10.rtt.NamedType.<Printer> make(
    "x10.io.Printer", /* base class */Printer.class
    , /* parents */ new x10.rtt.Type[] {x10.io.FilterWriter.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Printer $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Printer.class + " calling"); } 
        x10.io.FilterWriter.$_deserialize_body($_obj, $deserializer);
        x10.util.concurrent.Lock lock = (x10.util.concurrent.Lock) $deserializer.readRef();
        $_obj.lock = lock;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Printer $_obj = new Printer((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (lock instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.lock);
        } else {
        $serializer.write(this.lock);
        }
        
    }
    
    // constructor just for allocation
    public Printer(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
// creation method for java code (1-phase java constructor)
        public Printer(final x10.io.Writer w){this((java.lang.System[]) null);
                                                  $init(w);}
        
        // constructor for non-virtual call
        final public x10.io.Printer x10$io$Printer$$init$S(final x10.io.Writer w) { {
                                                                                           
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
super.$init(((x10.io.Writer)(w)));
                                                                                           
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"

                                                                                           
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
this.__fieldInitializers50034();
                                                                                       }
                                                                                       return this;
                                                                                       }
        
        // constructor
        public x10.io.Printer $init(final x10.io.Writer w){return x10$io$Printer$$init$S(w);}
        
        
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final public static char NEWLINE = '\n';
        
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
public x10.util.concurrent.Lock lock;
        
        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
public void
                                                                                              println(
                                                                                              ){
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final char t50119 =
              x10.io.Printer.NEWLINE;
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
this.print(x10.core.Char.$box(t50119));
        }
        
        
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final public void
                                                                                              println(
                                                                                              final java.lang.Object o){
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final boolean t50121 =
              ((o) == (null));
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
java.lang.String t50122 =
               null;
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
if (t50121) {
                
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
t50122 = "null\n";
            } else {
                
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final java.lang.String t50120 =
                  x10.rtt.Types.toString(o);
                
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
t50122 = ((t50120) + ("\n"));
            }
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final java.lang.String t50123 =
              t50122;
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
this.print(((java.lang.String)(t50123)));
        }
        
        
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final public void
                                                                                              println(
                                                                                              final java.lang.String s){
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final boolean t50124 =
              ((s) == (null));
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
java.lang.String t50125 =
               null;
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
if (t50124) {
                
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
t50125 = "null\n";
            } else {
                
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
t50125 = ((s) + ("\n"));
            }
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final java.lang.String t50126 =
              t50125;
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
this.print(((java.lang.String)(t50126)));
        }
        
        
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final public void
                                                                                              print(
                                                                                              final java.lang.Object o){
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final boolean t50127 =
              ((o) == (null));
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
java.lang.String t50128 =
               null;
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
if (t50127) {
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
t50128 = "null";
            } else {
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
t50128 = x10.rtt.Types.toString(o);
            }
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final java.lang.String t50129 =
              t50128;
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
this.print(((java.lang.String)(t50129)));
        }
        
        
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
public void
                                                                                              print(
                                                                                              final java.lang.String s){
            
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final x10.util.concurrent.Lock t50130 =
              ((x10.util.concurrent.Lock)(lock));
            
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
t50130.lock();
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
try {{
                
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final boolean t50131 =
                  ((s) != (null));
                
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
java.lang.String t50132 =
                   null;
                
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
if (t50131) {
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
t50132 = s;
                } else {
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
t50132 = "null";
                }
                
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final java.lang.String ss =
                  t50132;
                
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final x10.array.Array<x10.core.Byte> b =
                  ((x10.array.Array)(x10.core.ArrayFactory.<java.lang.Byte>makeArrayFromJavaArray(x10.rtt.Types.BYTE, (ss).getBytes())));
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final int t50133 =
                  ((x10.array.Array<x10.core.Byte>)b).
                    size;
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
this.write__0$1x10$lang$Byte$2(((x10.array.Array)(b)),
                                                                                                                                   (int)(0),
                                                                                                                                   (int)(t50133));
            }}finally {{
                  
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final x10.util.concurrent.Lock t50134 =
                    ((x10.util.concurrent.Lock)(lock));
                  
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
t50134.unlock();
              }}
            }
        
        
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
public void
                                                                                              printf(
                                                                                              final java.lang.String fmt){
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final x10.array.Array<java.lang.Object> t50135 =
              ((x10.array.Array)(new x10.array.Array<java.lang.Object>((java.lang.System[]) null, x10.rtt.Types.ANY).$init(((int)(0)))));
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
this.printfArray__1$1x10$lang$Any$2(((java.lang.String)(fmt)),
                                                                                                                                    ((x10.array.Array)(t50135)));
        }
        
        
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
public void
                                                                                              printf(
                                                                                              final java.lang.String fmt,
                                                                                              final java.lang.Object o1){
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final java.lang.Object t50136 =
              ((java.lang.Object)
                o1);
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final x10.array.Array<java.lang.Object> t50137 =
              ((x10.array.Array)(x10.core.ArrayFactory.<java.lang.Object> makeArrayFromJavaArray(x10.rtt.Types.ANY, new java.lang.Object[] {t50136})));
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
this.printfArray__1$1x10$lang$Any$2(((java.lang.String)(fmt)),
                                                                                                                                    ((x10.array.Array)(t50137)));
        }
        
        
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
public void
                                                                                              printf(
                                                                                              final java.lang.String fmt,
                                                                                              final java.lang.Object o1,
                                                                                              final java.lang.Object o2){
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final java.lang.Object t50138 =
              ((java.lang.Object)
                o1);
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final x10.array.Array<java.lang.Object> t50139 =
              ((x10.array.Array)(x10.core.ArrayFactory.<java.lang.Object> makeArrayFromJavaArray(x10.rtt.Types.ANY, new java.lang.Object[] {t50138, o2})));
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
this.printfArray__1$1x10$lang$Any$2(((java.lang.String)(fmt)),
                                                                                                                                    ((x10.array.Array)(t50139)));
        }
        
        
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
public void
                                                                                              printf(
                                                                                              final java.lang.String fmt,
                                                                                              final java.lang.Object o1,
                                                                                              final java.lang.Object o2,
                                                                                              final java.lang.Object o3){
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final java.lang.Object t50140 =
              ((java.lang.Object)
                o1);
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final x10.array.Array<java.lang.Object> t50141 =
              ((x10.array.Array)(x10.core.ArrayFactory.<java.lang.Object> makeArrayFromJavaArray(x10.rtt.Types.ANY, new java.lang.Object[] {t50140, o2, o3})));
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
this.printfArray__1$1x10$lang$Any$2(((java.lang.String)(fmt)),
                                                                                                                                    ((x10.array.Array)(t50141)));
        }
        
        
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
public void
                                                                                              printf(
                                                                                              final java.lang.String fmt,
                                                                                              final java.lang.Object o1,
                                                                                              final java.lang.Object o2,
                                                                                              final java.lang.Object o3,
                                                                                              final java.lang.Object o4){
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final x10.array.Array<java.lang.Object> t50142 =
              ((x10.array.Array)(x10.core.ArrayFactory.<java.lang.Object> makeArrayFromJavaArray(x10.rtt.Types.ANY, new java.lang.Object[] {o1, o2, o3, o4})));
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
this.printfArray__1$1x10$lang$Any$2(((java.lang.String)(fmt)),
                                                                                                                                    ((x10.array.Array)(t50142)));
        }
        
        
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
public void
                                                                                              printf(
                                                                                              final java.lang.String fmt,
                                                                                              final java.lang.Object o1,
                                                                                              final java.lang.Object o2,
                                                                                              final java.lang.Object o3,
                                                                                              final java.lang.Object o4,
                                                                                              final java.lang.Object o5){
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final x10.array.Array<java.lang.Object> t50143 =
              ((x10.array.Array)(x10.core.ArrayFactory.<java.lang.Object> makeArrayFromJavaArray(x10.rtt.Types.ANY, new java.lang.Object[] {o1, o2, o3, o4, o5})));
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
this.printfArray__1$1x10$lang$Any$2(((java.lang.String)(fmt)),
                                                                                                                                    ((x10.array.Array)(t50143)));
        }
        
        
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
public void
                                                                                              printf(
                                                                                              final java.lang.String fmt,
                                                                                              final java.lang.Object o1,
                                                                                              final java.lang.Object o2,
                                                                                              final java.lang.Object o3,
                                                                                              final java.lang.Object o4,
                                                                                              final java.lang.Object o5,
                                                                                              final java.lang.Object o6){
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final x10.array.Array<java.lang.Object> t50144 =
              ((x10.array.Array)(x10.core.ArrayFactory.<java.lang.Object> makeArrayFromJavaArray(x10.rtt.Types.ANY, new java.lang.Object[] {o1, o2, o3, o4, o5, o6})));
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
this.printfArray__1$1x10$lang$Any$2(((java.lang.String)(fmt)),
                                                                                                                                    ((x10.array.Array)(t50144)));
        }
        
        
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
public void
                                                                                              printf__1$1x10$lang$Any$2(
                                                                                              final java.lang.String fmt,
                                                                                              final x10.array.Array args){
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final int t50146 =
              ((x10.array.Array<java.lang.Object>)args).
                size;
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,java.lang.Object> t50147 =
              ((x10.core.fun.Fun_0_1)(new x10.io.Printer.$Closure$99(args, (x10.io.Printer.$Closure$99.__0$1x10$lang$Any$2) null)));
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final x10.array.Array<java.lang.Object> t50148 =
              ((x10.array.Array)(new x10.array.Array<java.lang.Object>((java.lang.System[]) null, x10.rtt.Types.ANY).$init(((int)(t50146)),
                                                                                                                           ((x10.core.fun.Fun_0_1)(t50147)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final java.lang.String t50149 =
              x10.core.String.format(fmt,(Object[]) (t50148).raw().value);
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
this.print(((java.lang.String)(t50149)));
        }
        
        
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
public void
                                                                                              printfArray__1$1x10$lang$Any$2(
                                                                                              final java.lang.String fmt,
                                                                                              final x10.array.Array args){
            
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final java.lang.String t50150 =
              x10.core.String.format(fmt,(Object[]) (args).raw().value);
            
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
this.print(((java.lang.String)(t50150)));
        }
        
        
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
public void
                                                                                              flush(
                                                                                              ){
            
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
try {try {{
                
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
super.flush();
            }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.io.IOException id$103) {
                
            }
        }
        
        
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
public void
                                                                                              close(
                                                                                              ){
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
try {try {{
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
super.close();
            }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.io.IOException id$104) {
                
            }
        }
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final public x10.io.Printer
                                                                                              x10$io$Printer$$x10$io$Printer$this(
                                                                                              ){
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
return x10.io.Printer.this;
        }
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final public void
                                                                                              __fieldInitializers50034(
                                                                                              ){
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final x10.util.concurrent.Lock t50151 =
              ((x10.util.concurrent.Lock)(new x10.util.concurrent.Lock()));
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
this.lock = ((x10.util.concurrent.Lock)(t50151));
        }
        
        public static char
          getInitialized$NEWLINE(
          ){
            return x10.io.Printer.NEWLINE;
        }
        
        @x10.core.X10Generated public static class $Closure$99 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$99.class);
            
            public static final x10.rtt.RuntimeType<$Closure$99> $RTT = x10.rtt.StaticFunType.<$Closure$99> make(
            /* base class */$Closure$99.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.ANY), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$99 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$99.class + " calling"); } 
                x10.array.Array args = (x10.array.Array) $deserializer.readRef();
                $_obj.args = args;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$99 $_obj = new $Closure$99((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (args instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.args);
                } else {
                $serializer.write(this.args);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$99(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply(x10.core.Int.$unbox(a1));
            }
            
                
                public java.lang.Object
                  $apply(
                  final int i){
                    
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
final java.lang.Object t50145 =
                      ((x10.array.Array<java.lang.Object>)this.
                                                            args).$apply$G((int)(i));
                    
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Printer.x10"
return t50145;
                }
                
                public x10.array.Array<java.lang.Object> args;
                
                public $Closure$99(final x10.array.Array<java.lang.Object> args, __0$1x10$lang$Any$2 $dummy) { {
                                                                                                                      this.args = ((x10.array.Array)(args));
                                                                                                                  }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$Any$2 {}
                
            }
            
        
        public void
          x10$io$FilterWriter$flush$S(
          ){
            super.flush();
        }
        
        public void
          x10$io$FilterWriter$close$S(
          ){
            super.close();
        }
        
        }
        