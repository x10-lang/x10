package x10.util;


@x10.core.X10Generated final public class OptionsParser extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, OptionsParser.class);
    
    public static final x10.rtt.RuntimeType<OptionsParser> $RTT = x10.rtt.NamedType.<OptionsParser> make(
    "x10.util.OptionsParser", /* base class */OptionsParser.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(OptionsParser $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + OptionsParser.class + " calling"); } 
        x10.util.HashMap map = (x10.util.HashMap) $deserializer.readRef();
        $_obj.map = map;
        x10.util.HashMap set = (x10.util.HashMap) $deserializer.readRef();
        $_obj.set = set;
        x10.array.Array flags = (x10.array.Array) $deserializer.readRef();
        $_obj.flags = flags;
        x10.array.Array specs = (x10.array.Array) $deserializer.readRef();
        $_obj.specs = specs;
        x10.util.GrowableIndexedMemoryChunk filteredArgs = (x10.util.GrowableIndexedMemoryChunk) $deserializer.readRef();
        $_obj.filteredArgs = filteredArgs;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        OptionsParser $_obj = new OptionsParser((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (map instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.map);
        } else {
        $serializer.write(this.map);
        }
        if (set instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.set);
        } else {
        $serializer.write(this.set);
        }
        if (flags instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.flags);
        } else {
        $serializer.write(this.flags);
        }
        if (specs instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.specs);
        } else {
        $serializer.write(this.specs);
        }
        if (filteredArgs instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.filteredArgs);
        } else {
        $serializer.write(this.filteredArgs);
        }
        
    }
    
    // constructor just for allocation
    public OptionsParser(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
@x10.core.X10Generated final public static class Err extends x10.lang.Exception implements x10.x10rt.X10JavaSerializable
                                                                                                    {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Err.class);
            
            public static final x10.rtt.RuntimeType<Err> $RTT = x10.rtt.NamedType.<Err> make(
            "x10.util.OptionsParser.Err", /* base class */Err.class
            , /* parents */ new x10.rtt.Type[] {x10.lang.Exception.$RTT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(Err $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Err.class + " calling"); } 
                x10.lang.Exception.$_deserialize_body($_obj, $deserializer);
                java.lang.String msg = (java.lang.String) $deserializer.readRef();
                $_obj.msg = msg;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                Err $_obj = new Err((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                super.$_serialize($serializer);
                $serializer.write(this.msg);
                
            }
            
            // constructor just for allocation
            public Err(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public java.lang.String msg;
                
                
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public Err(final java.lang.String m) {super();
                                                                                                                                                       {
                                                                                                                                                          
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"

                                                                                                                                                          
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
this.msg = ((java.lang.String)(m));
                                                                                                                                                      }}
                
                
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public Err() {super();
                                                                                                                               {
                                                                                                                                  
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"

                                                                                                                                  
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
this.msg = null;
                                                                                                                              }}
                
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public java.lang.String
                                                                                                              toString(
                                                                                                              ){
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61465 =
                      ((java.lang.String)(msg));
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61466 =
                      (("Commandline error: ") + (t61465));
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return t61466;
                }
                
                
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final public x10.util.OptionsParser.Err
                                                                                                              x10$util$OptionsParser$Err$$x10$util$OptionsParser$Err$this(
                                                                                                              ){
                    
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return x10.util.OptionsParser.Err.this;
                }
                
                }
                
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public x10.util.HashMap<java.lang.String, java.lang.String> map;
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public x10.util.HashMap<java.lang.String, x10.core.Boolean> set;
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public x10.array.Array<x10.util.Option> flags;
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public x10.array.Array<x10.util.Option> specs;
            
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public x10.util.GrowableIndexedMemoryChunk<java.lang.String> filteredArgs;
            
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
// creation method for java code (1-phase java constructor)
            public OptionsParser(final x10.array.Array<java.lang.String> args,
                                 final x10.array.Array<x10.util.Option> flags,
                                 final x10.array.Array<x10.util.Option> specs, __0$1x10$lang$String$2__1$1x10$util$Option$2__2$1x10$util$Option$2 $dummy){this((java.lang.System[]) null);
                                                                                                                                                              $init(args,flags,specs, (x10.util.OptionsParser.__0$1x10$lang$String$2__1$1x10$util$Option$2__2$1x10$util$Option$2) null);}
            
            // constructor for non-virtual call
            final public x10.util.OptionsParser x10$util$OptionsParser$$init$S(final x10.array.Array<java.lang.String> args,
                                                                               final x10.array.Array<x10.util.Option> flags,
                                                                               final x10.array.Array<x10.util.Option> specs, __0$1x10$lang$String$2__1$1x10$util$Option$2__2$1x10$util$Option$2 $dummy) { {
                                                                                                                                                                                                                 
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"

                                                                                                                                                                                                                 
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"

                                                                                                                                                                                                                 
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> map =
                                                                                                                                                                                                                   ((x10.util.HashMap)(new x10.util.HashMap<java.lang.String, java.lang.String>((java.lang.System[]) null, x10.rtt.Types.STRING, x10.rtt.Types.STRING).$init()));
                                                                                                                                                                                                                 
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, x10.core.Boolean> set =
                                                                                                                                                                                                                   ((x10.util.HashMap)(new x10.util.HashMap<java.lang.String, x10.core.Boolean>((java.lang.System[]) null, x10.rtt.Types.STRING, x10.rtt.Types.BOOLEAN).$init()));
                                                                                                                                                                                                                 
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.GrowableIndexedMemoryChunk<java.lang.String> filteredArgs =
                                                                                                                                                                                                                   ((x10.util.GrowableIndexedMemoryChunk)(new x10.util.GrowableIndexedMemoryChunk<java.lang.String>((java.lang.System[]) null, x10.rtt.Types.STRING).$init()));
                                                                                                                                                                                                                 
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
int offset =
                                                                                                                                                                                                                   0;
                                                                                                                                                                                                                 
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
boolean ended =
                                                                                                                                                                                                                   false;
                                                                                                                                                                                                                 
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
int i61907 =
                                                                                                                                                                                                                   0;
                                                                                                                                                                                                                 
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
for (;
                                                                                                                                                                                                                                                                                                                  true;
                                                                                                                                                                                                                                                                                                                  ) {
                                                                                                                                                                                                                     
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61908 =
                                                                                                                                                                                                                       i61907;
                                                                                                                                                                                                                     
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61909 =
                                                                                                                                                                                                                       ((x10.array.Array<java.lang.String>)args).
                                                                                                                                                                                                                         size;
                                                                                                                                                                                                                     
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61910 =
                                                                                                                                                                                                                       ((t61908) < (((int)(t61909))));
                                                                                                                                                                                                                     
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (!(t61910)) {
                                                                                                                                                                                                                         
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
break;
                                                                                                                                                                                                                     }
                                                                                                                                                                                                                     
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
L61911: {
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61912 =
                                                                                                                                                                                                                       i61907;
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String s61913 =
                                                                                                                                                                                                                       ((x10.array.Array<java.lang.String>)args).$apply$G((int)(t61912));
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
boolean recognised61914 =
                                                                                                                                                                                                                       false;
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61915 =
                                                                                                                                                                                                                       (s61913).equals("--");
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61915) {
                                                                                                                                                                                                                         
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
ended = true;
                                                                                                                                                                                                                         
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
break L61911;
                                                                                                                                                                                                                     }
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61916 =
                                                                                                                                                                                                                       (s61913).length();
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
boolean t61917 =
                                                                                                                                                                                                                       ((t61916) <= (((int)(1))));
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (!(t61917)) {
                                                                                                                                                                                                                         
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final char t61918 =
                                                                                                                                                                                                                           (s61913).charAt(((int)(0)));
                                                                                                                                                                                                                         
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
t61917 = ((char) t61918) !=
                                                                                                                                                                                                                         ((char) '-');
                                                                                                                                                                                                                     }
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61919 =
                                                                                                                                                                                                                       t61917;
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61919) {
                                                                                                                                                                                                                         
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
((x10.util.GrowableIndexedMemoryChunk<java.lang.String>)filteredArgs).add__0x10$util$GrowableIndexedMemoryChunk$$T(((java.lang.String)(s61913)));
                                                                                                                                                                                                                         
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
break L61911;
                                                                                                                                                                                                                     }
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61920 =
                                                                                                                                                                                                                       ended;
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61921 =
                                                                                                                                                                                                                       !(t61920);
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61921) {
                                                                                                                                                                                                                         
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final char t61922 =
                                                                                                                                                                                                                           (s61913).charAt(((int)(1)));
                                                                                                                                                                                                                         
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61923 =
                                                                                                                                                                                                                           ((char) t61922) !=
                                                                                                                                                                                                                         ((char) '-');
                                                                                                                                                                                                                         
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61923) {
                                                                                                                                                                                                                             
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
recognised61914 = true;
                                                                                                                                                                                                                             
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61871 =
                                                                                                                                                                                                                               (s61913).length();
                                                                                                                                                                                                                             
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61872 =
                                                                                                                                                                                                                               ((t61871) - (((int)(1))));
                                                                                                                                                                                                                             
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.IntRange t61873 =
                                                                                                                                                                                                                               ((x10.lang.IntRange)(new x10.lang.IntRange((java.lang.System[]) null).x10$lang$IntRange$$init$S(((int)(1)), ((int)(t61872)))));
                                                                                                                                                                                                                             
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.array.Region p61874 =
                                                                                                                                                                                                                               ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t61873)))));
                                                                                                                                                                                                                             
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int index61390min61875 =
                                                                                                                                                                                                                               p61874.min$O((int)(0));
                                                                                                                                                                                                                             
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int index61390max61876 =
                                                                                                                                                                                                                               p61874.max$O((int)(0));
                                                                                                                                                                                                                             
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
int index61818 =
                                                                                                                                                                                                                               index61390min61875;
                                                                                                                                                                                                                             
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
for (;
                                                                                                                                                                                                                                                                                                                              true;
                                                                                                                                                                                                                                                                                                                              ) {
                                                                                                                                                                                                                                 
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61819 =
                                                                                                                                                                                                                                   index61818;
                                                                                                                                                                                                                                 
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61820 =
                                                                                                                                                                                                                                   ((t61819) <= (((int)(index61390max61876))));
                                                                                                                                                                                                                                 
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (!(t61820)) {
                                                                                                                                                                                                                                     
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
break;
                                                                                                                                                                                                                                 }
                                                                                                                                                                                                                                 
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int index61815 =
                                                                                                                                                                                                                                   index61818;
                                                                                                                                                                                                                                 
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final char char61800 =
                                                                                                                                                                                                                                   (s61913).charAt(((int)(index61815)));
                                                                                                                                                                                                                                 
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
boolean char_recognised61801 =
                                                                                                                                                                                                                                   false;
                                                                                                                                                                                                                                 
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61802 =
                                                                                                                                                                                                                                   ((flags) != (null));
                                                                                                                                                                                                                                 
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61802) {
                                                                                                                                                                                                                                     
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterable<x10.util.Option> t61803 =
                                                                                                                                                                                                                                       ((x10.lang.Iterable<x10.util.Option>)
                                                                                                                                                                                                                                         ((x10.array.Array<x10.util.Option>)flags).values());
                                                                                                                                                                                                                                     
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterator<x10.util.Option> flag61804 =
                                                                                                                                                                                                                                       ((x10.lang.Iterator<x10.util.Option>)
                                                                                                                                                                                                                                         ((x10.lang.Iterable<x10.util.Option>)t61803).iterator());
                                                                                                                                                                                                                                     
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
for (;
                                                                                                                                                                                                                                                                                                                                      true;
                                                                                                                                                                                                                                                                                                                                      ) {
                                                                                                                                                                                                                                         
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61805 =
                                                                                                                                                                                                                                           ((x10.lang.Iterator<x10.util.Option>)flag61804).hasNext$O();
                                                                                                                                                                                                                                         
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (!(t61805)) {
                                                                                                                                                                                                                                             
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
break;
                                                                                                                                                                                                                                         }
                                                                                                                                                                                                                                         
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.Option flag61788 =
                                                                                                                                                                                                                                           ((x10.util.Option)(((x10.lang.Iterator<x10.util.Option>)flag61804).next$G()));
                                                                                                                                                                                                                                         
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61789 =
                                                                                                                                                                                                                                           ((java.lang.String)(flag61788.
                                                                                                                                                                                                                                                                 short_));
                                                                                                                                                                                                                                         
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
boolean t61790 =
                                                                                                                                                                                                                                           ((t61789) != (null));
                                                                                                                                                                                                                                         
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61790) {
                                                                                                                                                                                                                                             
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61791 =
                                                                                                                                                                                                                                               ((java.lang.String)(flag61788.
                                                                                                                                                                                                                                                                     short_));
                                                                                                                                                                                                                                             
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final char t61792 =
                                                                                                                                                                                                                                               (t61791).charAt(((int)(0)));
                                                                                                                                                                                                                                             
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
t61790 = x10.rtt.Equality.equalsequals(char61800, ((char)(t61792)));
                                                                                                                                                                                                                                         }
                                                                                                                                                                                                                                         
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61793 =
                                                                                                                                                                                                                                           t61790;
                                                                                                                                                                                                                                         
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61793) {
                                                                                                                                                                                                                                             
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
char_recognised61801 = true;
                                                                                                                                                                                                                                         }
                                                                                                                                                                                                                                     }
                                                                                                                                                                                                                                 }
                                                                                                                                                                                                                                 
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
boolean t61806 =
                                                                                                                                                                                                                                   ((specs) != (null));
                                                                                                                                                                                                                                 
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61806) {
                                                                                                                                                                                                                                     
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61807 =
                                                                                                                                                                                                                                       (s61913).length();
                                                                                                                                                                                                                                     
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61808 =
                                                                                                                                                                                                                                       ((t61807) - (((int)(1))));
                                                                                                                                                                                                                                     
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
t61806 = ((int) index61815) ==
                                                                                                                                                                                                                                     ((int) t61808);
                                                                                                                                                                                                                                 }
                                                                                                                                                                                                                                 
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61809 =
                                                                                                                                                                                                                                   t61806;
                                                                                                                                                                                                                                 
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61809) {
                                                                                                                                                                                                                                     
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterable<x10.util.Option> t61810 =
                                                                                                                                                                                                                                       ((x10.lang.Iterable<x10.util.Option>)
                                                                                                                                                                                                                                         ((x10.array.Array<x10.util.Option>)specs).values());
                                                                                                                                                                                                                                     
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterator<x10.util.Option> spec61811 =
                                                                                                                                                                                                                                       ((x10.lang.Iterator<x10.util.Option>)
                                                                                                                                                                                                                                         ((x10.lang.Iterable<x10.util.Option>)t61810).iterator());
                                                                                                                                                                                                                                     
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
for (;
                                                                                                                                                                                                                                                                                                                                      true;
                                                                                                                                                                                                                                                                                                                                      ) {
                                                                                                                                                                                                                                         
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61812 =
                                                                                                                                                                                                                                           ((x10.lang.Iterator<x10.util.Option>)spec61811).hasNext$O();
                                                                                                                                                                                                                                         
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (!(t61812)) {
                                                                                                                                                                                                                                             
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
break;
                                                                                                                                                                                                                                         }
                                                                                                                                                                                                                                         
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.Option spec61794 =
                                                                                                                                                                                                                                           ((x10.util.Option)(((x10.lang.Iterator<x10.util.Option>)spec61811).next$G()));
                                                                                                                                                                                                                                         
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61795 =
                                                                                                                                                                                                                                           ((java.lang.String)(spec61794.
                                                                                                                                                                                                                                                                 short_));
                                                                                                                                                                                                                                         
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
boolean t61796 =
                                                                                                                                                                                                                                           ((t61795) != (null));
                                                                                                                                                                                                                                         
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61796) {
                                                                                                                                                                                                                                             
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61797 =
                                                                                                                                                                                                                                               ((java.lang.String)(spec61794.
                                                                                                                                                                                                                                                                     short_));
                                                                                                                                                                                                                                             
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final char t61798 =
                                                                                                                                                                                                                                               (t61797).charAt(((int)(0)));
                                                                                                                                                                                                                                             
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
t61796 = x10.rtt.Equality.equalsequals(char61800, ((char)(t61798)));
                                                                                                                                                                                                                                         }
                                                                                                                                                                                                                                         
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61799 =
                                                                                                                                                                                                                                           t61796;
                                                                                                                                                                                                                                         
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61799) {
                                                                                                                                                                                                                                             
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
char_recognised61801 = true;
                                                                                                                                                                                                                                         }
                                                                                                                                                                                                                                     }
                                                                                                                                                                                                                                 }
                                                                                                                                                                                                                                 
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61813 =
                                                                                                                                                                                                                                   char_recognised61801;
                                                                                                                                                                                                                                 
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61814 =
                                                                                                                                                                                                                                   !(t61813);
                                                                                                                                                                                                                                 
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61814) {
                                                                                                                                                                                                                                     
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
recognised61914 = false;
                                                                                                                                                                                                                                     
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
break;
                                                                                                                                                                                                                                 }
                                                                                                                                                                                                                                 
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61816 =
                                                                                                                                                                                                                                   index61818;
                                                                                                                                                                                                                                 
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61817 =
                                                                                                                                                                                                                                   ((t61816) + (((int)(1))));
                                                                                                                                                                                                                                 
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
index61818 = t61817;
                                                                                                                                                                                                                             }
                                                                                                                                                                                                                             
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61924 =
                                                                                                                                                                                                                               recognised61914;
                                                                                                                                                                                                                             
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61924) {
                                                                                                                                                                                                                                 
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61925 =
                                                                                                                                                                                                                                   (s61913).length();
                                                                                                                                                                                                                                 
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61926 =
                                                                                                                                                                                                                                   ((t61925) - (((int)(1))));
                                                                                                                                                                                                                                 
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.IntRange t61927 =
                                                                                                                                                                                                                                   ((x10.lang.IntRange)(new x10.lang.IntRange((java.lang.System[]) null).x10$lang$IntRange$$init$S(((int)(1)), ((int)(t61926)))));
                                                                                                                                                                                                                                 
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.array.Region p61928 =
                                                                                                                                                                                                                                   ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t61927)))));
                                                                                                                                                                                                                                 
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int index61413min61929 =
                                                                                                                                                                                                                                   p61928.min$O((int)(0));
                                                                                                                                                                                                                                 
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int index61413max61930 =
                                                                                                                                                                                                                                   p61928.max$O((int)(0));
                                                                                                                                                                                                                                 
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
int index61868 =
                                                                                                                                                                                                                                   index61413min61929;
                                                                                                                                                                                                                                 
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
for (;
                                                                                                                                                                                                                                                                                                                                  true;
                                                                                                                                                                                                                                                                                                                                  ) {
                                                                                                                                                                                                                                     
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61869 =
                                                                                                                                                                                                                                       index61868;
                                                                                                                                                                                                                                     
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61870 =
                                                                                                                                                                                                                                       ((t61869) <= (((int)(index61413max61930))));
                                                                                                                                                                                                                                     
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (!(t61870)) {
                                                                                                                                                                                                                                         
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
break;
                                                                                                                                                                                                                                     }
                                                                                                                                                                                                                                     
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int index61865 =
                                                                                                                                                                                                                                       index61868;
                                                                                                                                                                                                                                     
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final char char61853 =
                                                                                                                                                                                                                                       (s61913).charAt(((int)(index61865)));
                                                                                                                                                                                                                                     
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61854 =
                                                                                                                                                                                                                                       ((flags) != (null));
                                                                                                                                                                                                                                     
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61854) {
                                                                                                                                                                                                                                         
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterable<x10.util.Option> t61855 =
                                                                                                                                                                                                                                           ((x10.lang.Iterable<x10.util.Option>)
                                                                                                                                                                                                                                             ((x10.array.Array<x10.util.Option>)flags).values());
                                                                                                                                                                                                                                         
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterator<x10.util.Option> flag61856 =
                                                                                                                                                                                                                                           ((x10.lang.Iterator<x10.util.Option>)
                                                                                                                                                                                                                                             ((x10.lang.Iterable<x10.util.Option>)t61855).iterator());
                                                                                                                                                                                                                                         
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
for (;
                                                                                                                                                                                                                                                                                                                                          true;
                                                                                                                                                                                                                                                                                                                                          ) {
                                                                                                                                                                                                                                             
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61857 =
                                                                                                                                                                                                                                               ((x10.lang.Iterator<x10.util.Option>)flag61856).hasNext$O();
                                                                                                                                                                                                                                             
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (!(t61857)) {
                                                                                                                                                                                                                                                 
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
break;
                                                                                                                                                                                                                                             }
                                                                                                                                                                                                                                             
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.Option flag61821 =
                                                                                                                                                                                                                                               ((x10.util.Option)(((x10.lang.Iterator<x10.util.Option>)flag61856).next$G()));
                                                                                                                                                                                                                                             
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61822 =
                                                                                                                                                                                                                                               ((java.lang.String)(flag61821.
                                                                                                                                                                                                                                                                     short_));
                                                                                                                                                                                                                                             
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
boolean t61823 =
                                                                                                                                                                                                                                               ((t61822) != (null));
                                                                                                                                                                                                                                             
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61823) {
                                                                                                                                                                                                                                                 
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61824 =
                                                                                                                                                                                                                                                   ((java.lang.String)(flag61821.
                                                                                                                                                                                                                                                                         short_));
                                                                                                                                                                                                                                                 
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final char t61825 =
                                                                                                                                                                                                                                                   (t61824).charAt(((int)(0)));
                                                                                                                                                                                                                                                 
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
t61823 = x10.rtt.Equality.equalsequals(char61853, ((char)(t61825)));
                                                                                                                                                                                                                                             }
                                                                                                                                                                                                                                             
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61826 =
                                                                                                                                                                                                                                               t61823;
                                                                                                                                                                                                                                             
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61826) {
                                                                                                                                                                                                                                                 
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61827 =
                                                                                                                                                                                                                                                   ((java.lang.String)(flag61821.
                                                                                                                                                                                                                                                                         short_));
                                                                                                                                                                                                                                                 
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61828 =
                                                                                                                                                                                                                                                   (("-") + (t61827));
                                                                                                                                                                                                                                                 
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
((x10.util.HashMap<java.lang.String, x10.core.Boolean>)set).put__0x10$util$HashMap$$K__1x10$util$HashMap$$V(((java.lang.String)(t61828)),
                                                                                                                                                                                                                                                                                                                                                                                                                                                         x10.core.Boolean.$box(true));
                                                                                                                                                                                                                                                 
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61829 =
                                                                                                                                                                                                                                                   ((java.lang.String)(flag61821.
                                                                                                                                                                                                                                                                         long_));
                                                                                                                                                                                                                                                 
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61830 =
                                                                                                                                                                                                                                                   ((t61829) != (null));
                                                                                                                                                                                                                                                 
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61830) {
                                                                                                                                                                                                                                                     
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61831 =
                                                                                                                                                                                                                                                       ((java.lang.String)(flag61821.
                                                                                                                                                                                                                                                                             long_));
                                                                                                                                                                                                                                                     
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
((x10.util.HashMap<java.lang.String, x10.core.Boolean>)set).put__0x10$util$HashMap$$K__1x10$util$HashMap$$V(((java.lang.String)(t61831)),
                                                                                                                                                                                                                                                                                                                                                                                                                                                             x10.core.Boolean.$box(true));
                                                                                                                                                                                                                                                 }
                                                                                                                                                                                                                                             }
                                                                                                                                                                                                                                         }
                                                                                                                                                                                                                                     }
                                                                                                                                                                                                                                     
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
boolean t61858 =
                                                                                                                                                                                                                                       ((specs) != (null));
                                                                                                                                                                                                                                     
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61858) {
                                                                                                                                                                                                                                         
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61859 =
                                                                                                                                                                                                                                           (s61913).length();
                                                                                                                                                                                                                                         
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61860 =
                                                                                                                                                                                                                                           ((t61859) - (((int)(1))));
                                                                                                                                                                                                                                         
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
t61858 = ((int) index61865) ==
                                                                                                                                                                                                                                         ((int) t61860);
                                                                                                                                                                                                                                     }
                                                                                                                                                                                                                                     
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61861 =
                                                                                                                                                                                                                                       t61858;
                                                                                                                                                                                                                                     
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61861) {
                                                                                                                                                                                                                                         
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterable<x10.util.Option> t61862 =
                                                                                                                                                                                                                                           ((x10.lang.Iterable<x10.util.Option>)
                                                                                                                                                                                                                                             ((x10.array.Array<x10.util.Option>)specs).values());
                                                                                                                                                                                                                                         
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterator<x10.util.Option> spec61863 =
                                                                                                                                                                                                                                           ((x10.lang.Iterator<x10.util.Option>)
                                                                                                                                                                                                                                             ((x10.lang.Iterable<x10.util.Option>)t61862).iterator());
                                                                                                                                                                                                                                         
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
for (;
                                                                                                                                                                                                                                                                                                                                          true;
                                                                                                                                                                                                                                                                                                                                          ) {
                                                                                                                                                                                                                                             
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61864 =
                                                                                                                                                                                                                                               ((x10.lang.Iterator<x10.util.Option>)spec61863).hasNext$O();
                                                                                                                                                                                                                                             
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (!(t61864)) {
                                                                                                                                                                                                                                                 
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
break;
                                                                                                                                                                                                                                             }
                                                                                                                                                                                                                                             
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.Option spec61832 =
                                                                                                                                                                                                                                               ((x10.util.Option)(((x10.lang.Iterator<x10.util.Option>)spec61863).next$G()));
                                                                                                                                                                                                                                             
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61833 =
                                                                                                                                                                                                                                               ((java.lang.String)(spec61832.
                                                                                                                                                                                                                                                                     short_));
                                                                                                                                                                                                                                             
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
boolean t61834 =
                                                                                                                                                                                                                                               ((t61833) != (null));
                                                                                                                                                                                                                                             
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61834) {
                                                                                                                                                                                                                                                 
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61835 =
                                                                                                                                                                                                                                                   ((java.lang.String)(spec61832.
                                                                                                                                                                                                                                                                         short_));
                                                                                                                                                                                                                                                 
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final char t61836 =
                                                                                                                                                                                                                                                   (t61835).charAt(((int)(0)));
                                                                                                                                                                                                                                                 
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
t61834 = x10.rtt.Equality.equalsequals(char61853, ((char)(t61836)));
                                                                                                                                                                                                                                             }
                                                                                                                                                                                                                                             
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61837 =
                                                                                                                                                                                                                                               t61834;
                                                                                                                                                                                                                                             
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61837) {
                                                                                                                                                                                                                                                 
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61838 =
                                                                                                                                                                                                                                                   i61907;
                                                                                                                                                                                                                                                 
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61839 =
                                                                                                                                                                                                                                                   ((t61838) + (((int)(1))));
                                                                                                                                                                                                                                                 
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
i61907 = t61839;
                                                                                                                                                                                                                                                 
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61840 =
                                                                                                                                                                                                                                                   i61907;
                                                                                                                                                                                                                                                 
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61841 =
                                                                                                                                                                                                                                                   ((x10.array.Array<java.lang.String>)args).
                                                                                                                                                                                                                                                     size;
                                                                                                                                                                                                                                                 
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61842 =
                                                                                                                                                                                                                                                   ((t61840) >= (((int)(t61841))));
                                                                                                                                                                                                                                                 
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61842) {
                                                                                                                                                                                                                                                     
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61843 =
                                                                                                                                                                                                                                                       (("Expected another arg after: \"") + (s61913));
                                                                                                                                                                                                                                                     
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61844 =
                                                                                                                                                                                                                                                       ((t61843) + ("\""));
                                                                                                                                                                                                                                                     
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.OptionsParser.Err t61845 =
                                                                                                                                                                                                                                                       ((x10.util.OptionsParser.Err)(new x10.util.OptionsParser.Err(t61844)));
                                                                                                                                                                                                                                                     
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
throw t61845;
                                                                                                                                                                                                                                                 }
                                                                                                                                                                                                                                                 
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61846 =
                                                                                                                                                                                                                                                   i61907;
                                                                                                                                                                                                                                                 
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String s61847 =
                                                                                                                                                                                                                                                   ((x10.array.Array<java.lang.String>)args).$apply$G((int)(t61846));
                                                                                                                                                                                                                                                 
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61848 =
                                                                                                                                                                                                                                                   ((java.lang.String)(spec61832.
                                                                                                                                                                                                                                                                         short_));
                                                                                                                                                                                                                                                 
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61849 =
                                                                                                                                                                                                                                                   (("-") + (t61848));
                                                                                                                                                                                                                                                 
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
((x10.util.HashMap<java.lang.String, java.lang.String>)map).put__0x10$util$HashMap$$K__1x10$util$HashMap$$V(((java.lang.String)(t61849)),
                                                                                                                                                                                                                                                                                                                                                                                                                                                         ((java.lang.String)(s61847)));
                                                                                                                                                                                                                                                 
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61850 =
                                                                                                                                                                                                                                                   ((java.lang.String)(spec61832.
                                                                                                                                                                                                                                                                         long_));
                                                                                                                                                                                                                                                 
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61851 =
                                                                                                                                                                                                                                                   ((t61850) != (null));
                                                                                                                                                                                                                                                 
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61851) {
                                                                                                                                                                                                                                                     
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61852 =
                                                                                                                                                                                                                                                       ((java.lang.String)(spec61832.
                                                                                                                                                                                                                                                                             long_));
                                                                                                                                                                                                                                                     
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
((x10.util.HashMap<java.lang.String, java.lang.String>)map).put__0x10$util$HashMap$$K__1x10$util$HashMap$$V(((java.lang.String)(t61852)),
                                                                                                                                                                                                                                                                                                                                                                                                                                                             ((java.lang.String)(s61847)));
                                                                                                                                                                                                                                                 }
                                                                                                                                                                                                                                             }
                                                                                                                                                                                                                                         }
                                                                                                                                                                                                                                     }
                                                                                                                                                                                                                                     
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61866 =
                                                                                                                                                                                                                                       index61868;
                                                                                                                                                                                                                                     
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61867 =
                                                                                                                                                                                                                                       ((t61866) + (((int)(1))));
                                                                                                                                                                                                                                     
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
index61868 = t61867;
                                                                                                                                                                                                                                 }
                                                                                                                                                                                                                             }
                                                                                                                                                                                                                         } else {
                                                                                                                                                                                                                             
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61931 =
                                                                                                                                                                                                                               ((flags) != (null));
                                                                                                                                                                                                                             
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61931) {
                                                                                                                                                                                                                                 
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterable<x10.util.Option> t61932 =
                                                                                                                                                                                                                                   ((x10.lang.Iterable<x10.util.Option>)
                                                                                                                                                                                                                                     ((x10.array.Array<x10.util.Option>)flags).values());
                                                                                                                                                                                                                                 
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterator<x10.util.Option> flag61933 =
                                                                                                                                                                                                                                   ((x10.lang.Iterator<x10.util.Option>)
                                                                                                                                                                                                                                     ((x10.lang.Iterable<x10.util.Option>)t61932).iterator());
                                                                                                                                                                                                                                 
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
for (;
                                                                                                                                                                                                                                                                                                                                  true;
                                                                                                                                                                                                                                                                                                                                  ) {
                                                                                                                                                                                                                                     
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61934 =
                                                                                                                                                                                                                                       ((x10.lang.Iterator<x10.util.Option>)flag61933).hasNext$O();
                                                                                                                                                                                                                                     
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (!(t61934)) {
                                                                                                                                                                                                                                         
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
break;
                                                                                                                                                                                                                                     }
                                                                                                                                                                                                                                     
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.Option flag61877 =
                                                                                                                                                                                                                                       ((x10.util.Option)(((x10.lang.Iterator<x10.util.Option>)flag61933).next$G()));
                                                                                                                                                                                                                                     
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61878 =
                                                                                                                                                                                                                                       recognised61914;
                                                                                                                                                                                                                                     
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61878) {
                                                                                                                                                                                                                                         
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
break;
                                                                                                                                                                                                                                     }
                                                                                                                                                                                                                                     
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61879 =
                                                                                                                                                                                                                                       ((java.lang.String)(flag61877.
                                                                                                                                                                                                                                                             long_));
                                                                                                                                                                                                                                     
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61880 =
                                                                                                                                                                                                                                       (s61913).equals(t61879);
                                                                                                                                                                                                                                     
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61880) {
                                                                                                                                                                                                                                         
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61881 =
                                                                                                                                                                                                                                           ((java.lang.String)(flag61877.
                                                                                                                                                                                                                                                                 short_));
                                                                                                                                                                                                                                         
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61882 =
                                                                                                                                                                                                                                           ((t61881) != (null));
                                                                                                                                                                                                                                         
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61882) {
                                                                                                                                                                                                                                             
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61883 =
                                                                                                                                                                                                                                               ((java.lang.String)(flag61877.
                                                                                                                                                                                                                                                                     short_));
                                                                                                                                                                                                                                             
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61884 =
                                                                                                                                                                                                                                               (("-") + (t61883));
                                                                                                                                                                                                                                             
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
((x10.util.HashMap<java.lang.String, x10.core.Boolean>)set).put__0x10$util$HashMap$$K__1x10$util$HashMap$$V(((java.lang.String)(t61884)),
                                                                                                                                                                                                                                                                                                                                                                                                                                                      x10.core.Boolean.$box(true));
                                                                                                                                                                                                                                         }
                                                                                                                                                                                                                                         
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61885 =
                                                                                                                                                                                                                                           ((java.lang.String)(flag61877.
                                                                                                                                                                                                                                                                 long_));
                                                                                                                                                                                                                                         
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
((x10.util.HashMap<java.lang.String, x10.core.Boolean>)set).put__0x10$util$HashMap$$K__1x10$util$HashMap$$V(((java.lang.String)(t61885)),
                                                                                                                                                                                                                                                                                                                                                                                                                                                  x10.core.Boolean.$box(true));
                                                                                                                                                                                                                                         
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
recognised61914 = true;
                                                                                                                                                                                                                                     }
                                                                                                                                                                                                                                 }
                                                                                                                                                                                                                             }
                                                                                                                                                                                                                             
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61935 =
                                                                                                                                                                                                                               ((specs) != (null));
                                                                                                                                                                                                                             
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61935) {
                                                                                                                                                                                                                                 
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterable<x10.util.Option> t61936 =
                                                                                                                                                                                                                                   ((x10.lang.Iterable<x10.util.Option>)
                                                                                                                                                                                                                                     ((x10.array.Array<x10.util.Option>)specs).values());
                                                                                                                                                                                                                                 
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterator<x10.util.Option> spec61937 =
                                                                                                                                                                                                                                   ((x10.lang.Iterator<x10.util.Option>)
                                                                                                                                                                                                                                     ((x10.lang.Iterable<x10.util.Option>)t61936).iterator());
                                                                                                                                                                                                                                 
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
for (;
                                                                                                                                                                                                                                                                                                                                   true;
                                                                                                                                                                                                                                                                                                                                   ) {
                                                                                                                                                                                                                                     
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61938 =
                                                                                                                                                                                                                                       ((x10.lang.Iterator<x10.util.Option>)spec61937).hasNext$O();
                                                                                                                                                                                                                                     
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (!(t61938)) {
                                                                                                                                                                                                                                         
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
break;
                                                                                                                                                                                                                                     }
                                                                                                                                                                                                                                     
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.Option spec61886 =
                                                                                                                                                                                                                                       ((x10.util.Option)(((x10.lang.Iterator<x10.util.Option>)spec61937).next$G()));
                                                                                                                                                                                                                                     
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61887 =
                                                                                                                                                                                                                                       recognised61914;
                                                                                                                                                                                                                                     
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61887) {
                                                                                                                                                                                                                                         
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
break;
                                                                                                                                                                                                                                     }
                                                                                                                                                                                                                                     
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61888 =
                                                                                                                                                                                                                                       ((java.lang.String)(spec61886.
                                                                                                                                                                                                                                                             long_));
                                                                                                                                                                                                                                     
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61889 =
                                                                                                                                                                                                                                       (s61913).equals(t61888);
                                                                                                                                                                                                                                     
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61889) {
                                                                                                                                                                                                                                         
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
recognised61914 = true;
                                                                                                                                                                                                                                         
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61890 =
                                                                                                                                                                                                                                           i61907;
                                                                                                                                                                                                                                         
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61891 =
                                                                                                                                                                                                                                           ((t61890) + (((int)(1))));
                                                                                                                                                                                                                                         
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
i61907 = t61891;
                                                                                                                                                                                                                                         
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61892 =
                                                                                                                                                                                                                                           i61907;
                                                                                                                                                                                                                                         
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61893 =
                                                                                                                                                                                                                                           ((x10.array.Array<java.lang.String>)args).
                                                                                                                                                                                                                                             size;
                                                                                                                                                                                                                                         
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61894 =
                                                                                                                                                                                                                                           ((t61892) >= (((int)(t61893))));
                                                                                                                                                                                                                                         
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61894) {
                                                                                                                                                                                                                                             
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61895 =
                                                                                                                                                                                                                                               (("Expected another arg after: \"") + (s61913));
                                                                                                                                                                                                                                             
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61896 =
                                                                                                                                                                                                                                               ((t61895) + ("\""));
                                                                                                                                                                                                                                             
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.OptionsParser.Err t61897 =
                                                                                                                                                                                                                                               ((x10.util.OptionsParser.Err)(new x10.util.OptionsParser.Err(t61896)));
                                                                                                                                                                                                                                             
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
throw t61897;
                                                                                                                                                                                                                                         }
                                                                                                                                                                                                                                         
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61898 =
                                                                                                                                                                                                                                           i61907;
                                                                                                                                                                                                                                         
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String s61899 =
                                                                                                                                                                                                                                           ((x10.array.Array<java.lang.String>)args).$apply$G((int)(t61898));
                                                                                                                                                                                                                                         
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61900 =
                                                                                                                                                                                                                                           ((java.lang.String)(spec61886.
                                                                                                                                                                                                                                                                 short_));
                                                                                                                                                                                                                                         
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61901 =
                                                                                                                                                                                                                                           ((t61900) != (null));
                                                                                                                                                                                                                                         
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61901) {
                                                                                                                                                                                                                                             
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61902 =
                                                                                                                                                                                                                                               ((java.lang.String)(spec61886.
                                                                                                                                                                                                                                                                     short_));
                                                                                                                                                                                                                                             
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61903 =
                                                                                                                                                                                                                                               (("-") + (t61902));
                                                                                                                                                                                                                                             
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
((x10.util.HashMap<java.lang.String, java.lang.String>)map).put__0x10$util$HashMap$$K__1x10$util$HashMap$$V(((java.lang.String)(t61903)),
                                                                                                                                                                                                                                                                                                                                                                                                                                                      ((java.lang.String)(s61899)));
                                                                                                                                                                                                                                         }
                                                                                                                                                                                                                                         
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61904 =
                                                                                                                                                                                                                                           ((java.lang.String)(spec61886.
                                                                                                                                                                                                                                                                 long_));
                                                                                                                                                                                                                                         
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
((x10.util.HashMap<java.lang.String, java.lang.String>)map).put__0x10$util$HashMap$$K__1x10$util$HashMap$$V(((java.lang.String)(t61904)),
                                                                                                                                                                                                                                                                                                                                                                                                                                                  ((java.lang.String)(s61899)));
                                                                                                                                                                                                                                     }
                                                                                                                                                                                                                                 }
                                                                                                                                                                                                                             }
                                                                                                                                                                                                                         }
                                                                                                                                                                                                                     }
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61939 =
                                                                                                                                                                                                                       recognised61914;
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61940 =
                                                                                                                                                                                                                       !(t61939);
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61940) {
                                                                                                                                                                                                                         
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
((x10.util.GrowableIndexedMemoryChunk<java.lang.String>)filteredArgs).add__0x10$util$GrowableIndexedMemoryChunk$$T(((java.lang.String)(s61913)));
                                                                                                                                                                                                                     }}
                                                                                                                                                                                                                     
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61905 =
                                                                                                                                                                                                                       i61907;
                                                                                                                                                                                                                     
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61906 =
                                                                                                                                                                                                                       ((t61905) + (((int)(1))));
                                                                                                                                                                                                                     
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
i61907 = t61906;
                                                                                                                                                                                                                 }
                                                                                                                                                                                                                 
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
this.map = ((x10.util.HashMap)(map));
                                                                                                                                                                                                                 
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
this.set = ((x10.util.HashMap)(set));
                                                                                                                                                                                                                 
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
this.flags = ((x10.array.Array)(flags));
                                                                                                                                                                                                                 
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
this.specs = ((x10.array.Array)(specs));
                                                                                                                                                                                                                 
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
this.filteredArgs = ((x10.util.GrowableIndexedMemoryChunk)(filteredArgs));
                                                                                                                                                                                                             }
                                                                                                                                                                                                             return this;
                                                                                                                                                                                                             }
            
            // constructor
            public x10.util.OptionsParser $init(final x10.array.Array<java.lang.String> args,
                                                final x10.array.Array<x10.util.Option> flags,
                                                final x10.array.Array<x10.util.Option> specs, __0$1x10$lang$String$2__1$1x10$util$Option$2__2$1x10$util$Option$2 $dummy){return x10$util$OptionsParser$$init$S(args,flags,specs, $dummy);}
            
            
            
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public x10.array.Array<java.lang.String>
                                                                                                           filteredArgs(
                                                                                                           ){
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.GrowableIndexedMemoryChunk<java.lang.String> t61598 =
                  ((x10.util.GrowableIndexedMemoryChunk)(filteredArgs));
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.array.Array<java.lang.String> t61599 =
                  ((x10.array.Array)(((x10.array.Array<java.lang.String>)
                                       ((x10.util.GrowableIndexedMemoryChunk<java.lang.String>)t61598).toArray())));
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return t61599;
            }
            
            
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public static java.lang.String
                                                                                                           padding$O(
                                                                                                           final int p){
                
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
java.lang.String r =
                  new java.lang.String();
                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.IntRange t61949 =
                  ((x10.lang.IntRange)(new x10.lang.IntRange((java.lang.System[]) null).x10$lang$IntRange$$init$S(((int)(1)), ((int)(p)))));
                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.array.Region p61950 =
                  ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t61949)))));
                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int i61436min61951 =
                  p61950.min$O((int)(0));
                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int i61436max61952 =
                  p61950.max$O((int)(0));
                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
int i61946 =
                  i61436min61951;
                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
for (;
                                                                                                                  true;
                                                                                                                  ) {
                    
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61947 =
                      i61946;
                    
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61948 =
                      ((t61947) <= (((int)(i61436max61952))));
                    
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (!(t61948)) {
                        
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
break;
                    }
                    
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int i61943 =
                      i61946;
                    
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61941 =
                      r;
                    
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61942 =
                      ((t61941) + (" "));
                    
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
r = t61942;
                    
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61944 =
                      i61946;
                    
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61945 =
                      ((t61944) + (((int)(1))));
                    
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
i61946 = t61945;
                }
                
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61608 =
                  r;
                
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return t61608;
            }
            
            
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public java.lang.String
                                                                                                           usage$O(
                                                                                                           ){
                
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
java.lang.String r =
                  new java.lang.String();
                
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61609 =
                  r;
                
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61610 =
                  ((t61609) + ("Usage:"));
                
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
r = t61610;
                
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
int shortWidth =
                  0;
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.array.Array<x10.util.Option> t62029 =
                  ((x10.array.Array)(flags));
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterable<x10.util.Option> t62030 =
                  ((x10.lang.Iterable<x10.util.Option>)
                    ((x10.array.Array<x10.util.Option>)t62029).values());
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterator<x10.util.Option> opt62031 =
                  ((x10.lang.Iterator<x10.util.Option>)
                    ((x10.lang.Iterable<x10.util.Option>)t62030).iterator());
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
for (;
                                                                                                                  true;
                                                                                                                  ) {
                    
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t62032 =
                      ((x10.lang.Iterator<x10.util.Option>)opt62031).hasNext$O();
                    
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (!(t62032)) {
                        
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
break;
                    }
                    
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.Option opt61953 =
                      ((x10.util.Option)(((x10.lang.Iterator<x10.util.Option>)opt62031).next$G()));
                    
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61954 =
                      ((java.lang.String)(opt61953.
                                            short_));
                    
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61955 =
                      ((t61954) != (null));
                    
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61955) {
                        
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61956 =
                          shortWidth;
                        
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61957 =
                          ((java.lang.String)(opt61953.
                                                short_));
                        
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61958 =
                          (t61957).length();
                        
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61959 =
                          x10.lang.Math.max$O((int)(t61956),
                                              (int)(t61958));
                        
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
shortWidth = t61959;
                    }
                }
                
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.array.Array<x10.util.Option> t62033 =
                  ((x10.array.Array)(specs));
                
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterable<x10.util.Option> t62034 =
                  ((x10.lang.Iterable<x10.util.Option>)
                    ((x10.array.Array<x10.util.Option>)t62033).values());
                
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterator<x10.util.Option> opt62035 =
                  ((x10.lang.Iterator<x10.util.Option>)
                    ((x10.lang.Iterable<x10.util.Option>)t62034).iterator());
                
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
for (;
                                                                                                                  true;
                                                                                                                  ) {
                    
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t62036 =
                      ((x10.lang.Iterator<x10.util.Option>)opt62035).hasNext$O();
                    
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (!(t62036)) {
                        
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
break;
                    }
                    
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.Option opt61960 =
                      ((x10.util.Option)(((x10.lang.Iterator<x10.util.Option>)opt62035).next$G()));
                    
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61961 =
                      ((java.lang.String)(opt61960.
                                            short_));
                    
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61962 =
                      ((t61961) != (null));
                    
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61962) {
                        
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61963 =
                          shortWidth;
                        
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61964 =
                          ((java.lang.String)(opt61960.
                                                short_));
                        
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61965 =
                          (t61964).length();
                        
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61966 =
                          x10.lang.Math.max$O((int)(t61963),
                                              (int)(t61965));
                        
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
shortWidth = t61966;
                    }
                }
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
int longWidth =
                  0;
                
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.array.Array<x10.util.Option> t62037 =
                  ((x10.array.Array)(flags));
                
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterable<x10.util.Option> t62038 =
                  ((x10.lang.Iterable<x10.util.Option>)
                    ((x10.array.Array<x10.util.Option>)t62037).values());
                
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterator<x10.util.Option> opt62039 =
                  ((x10.lang.Iterator<x10.util.Option>)
                    ((x10.lang.Iterable<x10.util.Option>)t62038).iterator());
                
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
for (;
                                                                                                                  true;
                                                                                                                  ) {
                    
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t62040 =
                      ((x10.lang.Iterator<x10.util.Option>)opt62039).hasNext$O();
                    
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (!(t62040)) {
                        
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
break;
                    }
                    
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.Option opt61967 =
                      ((x10.util.Option)(((x10.lang.Iterator<x10.util.Option>)opt62039).next$G()));
                    
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61968 =
                      ((java.lang.String)(opt61967.
                                            long_));
                    
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61969 =
                      ((t61968) != (null));
                    
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61969) {
                        
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61970 =
                          longWidth;
                        
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61971 =
                          ((java.lang.String)(opt61967.
                                                long_));
                        
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61972 =
                          (t61971).length();
                        
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61973 =
                          x10.lang.Math.max$O((int)(t61970),
                                              (int)(t61972));
                        
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
longWidth = t61973;
                    }
                }
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.array.Array<x10.util.Option> t62041 =
                  ((x10.array.Array)(specs));
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterable<x10.util.Option> t62042 =
                  ((x10.lang.Iterable<x10.util.Option>)
                    ((x10.array.Array<x10.util.Option>)t62041).values());
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterator<x10.util.Option> opt62043 =
                  ((x10.lang.Iterator<x10.util.Option>)
                    ((x10.lang.Iterable<x10.util.Option>)t62042).iterator());
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
for (;
                                                                                                                  true;
                                                                                                                  ) {
                    
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t62044 =
                      ((x10.lang.Iterator<x10.util.Option>)opt62043).hasNext$O();
                    
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (!(t62044)) {
                        
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
break;
                    }
                    
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.Option opt61974 =
                      ((x10.util.Option)(((x10.lang.Iterator<x10.util.Option>)opt62043).next$G()));
                    
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61975 =
                      ((java.lang.String)(opt61974.
                                            long_));
                    
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61976 =
                      ((t61975) != (null));
                    
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61976) {
                        
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61977 =
                          longWidth;
                        
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61978 =
                          ((java.lang.String)(opt61974.
                                                long_));
                        
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61979 =
                          (t61978).length();
                        
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61980 =
                          x10.lang.Math.max$O((int)(t61977),
                                              (int)(t61979));
                        
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
longWidth = t61980;
                    }
                }
                
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.array.Array<x10.util.Option> t62045 =
                  ((x10.array.Array)(flags));
                
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterable<x10.util.Option> t62046 =
                  ((x10.lang.Iterable<x10.util.Option>)
                    ((x10.array.Array<x10.util.Option>)t62045).values());
                
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterator<x10.util.Option> opt62047 =
                  ((x10.lang.Iterator<x10.util.Option>)
                    ((x10.lang.Iterable<x10.util.Option>)t62046).iterator());
                
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
for (;
                                                                                                                  true;
                                                                                                                  ) {
                    
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t62048 =
                      ((x10.lang.Iterator<x10.util.Option>)opt62047).hasNext$O();
                    
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (!(t62048)) {
                        
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
break;
                    }
                    
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.Option opt61981 =
                      ((x10.util.Option)(((x10.lang.Iterator<x10.util.Option>)opt62047).next$G()));
                    
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61982 =
                      shortWidth;
                    
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61983 =
                      ((8) + (((int)(t61982))));
                    
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61984 =
                      ((java.lang.String)(opt61981.
                                            short_));
                    
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61985 =
                      (t61984).length();
                    
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int shortPadding61986 =
                      ((t61983) - (((int)(t61985))));
                    
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61987 =
                      longWidth;
                    
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61988 =
                      ((java.lang.String)(opt61981.
                                            long_));
                    
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61989 =
                      (t61988).length();
                    
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int longPadding61990 =
                      ((t61987) - (((int)(t61989))));
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61991 =
                      r;
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61992 =
                      ((java.lang.String)(opt61981.
                                            short_));
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61993 =
                      (("\n    ") + (t61992));
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61994 =
                      x10.util.OptionsParser.padding$O((int)(shortPadding61986));
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61995 =
                      ((t61993) + (t61994));
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61996 =
                      ((t61995) + (" ("));
                    
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61997 =
                      ((java.lang.String)(opt61981.
                                            long_));
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61998 =
                      ((t61996) + (t61997));
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61999 =
                      ((t61998) + (") "));
                    
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t62000 =
                      x10.util.OptionsParser.padding$O((int)(longPadding61990));
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t62001 =
                      ((t61999) + (t62000));
                    
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t62002 =
                      ((java.lang.String)(opt61981.
                                            description));
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t62003 =
                      ((t62001) + (t62002));
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t62004 =
                      ((t61991) + (t62003));
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
r = t62004;
                }
                
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.array.Array<x10.util.Option> t62049 =
                  ((x10.array.Array)(specs));
                
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterable<x10.util.Option> t62050 =
                  ((x10.lang.Iterable<x10.util.Option>)
                    ((x10.array.Array<x10.util.Option>)t62049).values());
                
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.lang.Iterator<x10.util.Option> opt62051 =
                  ((x10.lang.Iterator<x10.util.Option>)
                    ((x10.lang.Iterable<x10.util.Option>)t62050).iterator());
                
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
for (;
                                                                                                                  true;
                                                                                                                  ) {
                    
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t62052 =
                      ((x10.lang.Iterator<x10.util.Option>)opt62051).hasNext$O();
                    
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (!(t62052)) {
                        
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
break;
                    }
                    
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.Option opt62005 =
                      ((x10.util.Option)(((x10.lang.Iterator<x10.util.Option>)opt62051).next$G()));
                    
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t62006 =
                      shortWidth;
                    
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t62007 =
                      ((java.lang.String)(opt62005.
                                            short_));
                    
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t62008 =
                      (t62007).length();
                    
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int shortPadding62009 =
                      ((t62006) - (((int)(t62008))));
                    
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t62010 =
                      longWidth;
                    
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t62011 =
                      ((java.lang.String)(opt62005.
                                            long_));
                    
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t62012 =
                      (t62011).length();
                    
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int longPadding62013 =
                      ((t62010) - (((int)(t62012))));
                    
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t62014 =
                      r;
                    
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t62015 =
                      ((java.lang.String)(opt62005.
                                            short_));
                    
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t62016 =
                      (("\n    ") + (t62015));
                    
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t62017 =
                      ((t62016) + (" <param>"));
                    
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t62018 =
                      x10.util.OptionsParser.padding$O((int)(shortPadding62009));
                    
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t62019 =
                      ((t62017) + (t62018));
                    
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t62020 =
                      ((t62019) + (" ("));
                    
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t62021 =
                      ((java.lang.String)(opt62005.
                                            long_));
                    
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t62022 =
                      ((t62020) + (t62021));
                    
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t62023 =
                      ((t62022) + (") "));
                    
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t62024 =
                      x10.util.OptionsParser.padding$O((int)(longPadding62013));
                    
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t62025 =
                      ((t62023) + (t62024));
                    
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t62026 =
                      ((java.lang.String)(opt62005.
                                            description));
                    
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t62027 =
                      ((t62025) + (t62026));
                    
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t62028 =
                      ((t62014) + (t62027));
                    
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
r = t62028;
                }
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61701 =
                  r;
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return t61701;
            }
            
            
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public boolean
                                                                                                           $apply$O(
                                                                                                           final java.lang.String key){
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, x10.core.Boolean> t61702 =
                  ((x10.util.HashMap)(set));
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
boolean t61704 =
                  ((x10.util.HashMap<java.lang.String, x10.core.Boolean>)t61702).containsKey__0x10$util$HashMap$$K$O(((java.lang.String)(key)));
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (!(t61704)) {
                    
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t61703 =
                      ((x10.util.HashMap)(map));
                    
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
t61704 = ((x10.util.HashMap<java.lang.String, java.lang.String>)t61703).containsKey__0x10$util$HashMap$$K$O(((java.lang.String)(key)));
                }
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61705 =
                  t61704;
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return t61705;
            }
            
            
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public java.lang.String
                                                                                                           $apply$O(
                                                                                                           final java.lang.String key,
                                                                                                           final java.lang.String d){
                
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t61706 =
                  ((x10.util.HashMap)(map));
                
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61707 =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t61706).getOrElse__0x10$util$HashMap$$K__1x10$util$HashMap$$V$G(((java.lang.String)(key)),
                                                                                                                                         ((java.lang.String)(d)));
                
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return t61707;
            }
            
            
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public byte
                                                                                                           $apply__1$u$O(
                                                                                                           final java.lang.String key,
                                                                                                           final byte d){
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t61708 =
                  ((x10.util.HashMap)(map));
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61709 =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t61708).containsKey__0x10$util$HashMap$$K$O(((java.lang.String)(key)));
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61710 =
                  !(t61709);
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61710) {
                    
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return d;
                }
                
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t61711 =
                  ((x10.util.HashMap)(map));
                
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String v =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t61711).getOrElse__0x10$util$HashMap$$K__1x10$util$HashMap$$V$G(((java.lang.String)(key)),
                                                                                                                                         ((java.lang.String)("???")));
                
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
try {try {{
                    
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final byte t61712 =
                      ((byte)(java.lang.Integer.parseInt(v) & 0xff));
                    
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return t61712;
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.lang.NumberFormatException e) {
                    
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61713 =
                      (("Expected UByte, got: \"") + (v));
                    
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61714 =
                      ((t61713) + ("\""));
                    
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.OptionsParser.Err t61715 =
                      ((x10.util.OptionsParser.Err)(new x10.util.OptionsParser.Err(t61714)));
                    
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
throw t61715;
                }
            }
            
            
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public byte
                                                                                                           $apply$O(
                                                                                                           final java.lang.String key,
                                                                                                           final byte d){
                
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t61716 =
                  ((x10.util.HashMap)(map));
                
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61717 =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t61716).containsKey__0x10$util$HashMap$$K$O(((java.lang.String)(key)));
                
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61718 =
                  !(t61717);
                
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61718) {
                    
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return d;
                }
                
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t61719 =
                  ((x10.util.HashMap)(map));
                
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String v =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t61719).getOrElse__0x10$util$HashMap$$K__1x10$util$HashMap$$V$G(((java.lang.String)(key)),
                                                                                                                                         ((java.lang.String)("???")));
                
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
try {try {{
                    
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final byte t61720 =
                      java.lang.Byte.parseByte(v);
                    
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return t61720;
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.lang.NumberFormatException e) {
                    
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61721 =
                      (("Expected Byte, got: \"") + (v));
                    
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61722 =
                      ((t61721) + ("\""));
                    
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.OptionsParser.Err t61723 =
                      ((x10.util.OptionsParser.Err)(new x10.util.OptionsParser.Err(t61722)));
                    
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
throw t61723;
                }
            }
            
            
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public short
                                                                                                           $apply__1$u$O(
                                                                                                           final java.lang.String key,
                                                                                                           final short d){
                
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t61724 =
                  ((x10.util.HashMap)(map));
                
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61725 =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t61724).containsKey__0x10$util$HashMap$$K$O(((java.lang.String)(key)));
                
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61726 =
                  !(t61725);
                
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61726) {
                    
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return d;
                }
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t61727 =
                  ((x10.util.HashMap)(map));
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String v =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t61727).getOrElse__0x10$util$HashMap$$K__1x10$util$HashMap$$V$G(((java.lang.String)(key)),
                                                                                                                                         ((java.lang.String)("???")));
                
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
try {try {{
                    
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final short t61728 =
                      ((short)(java.lang.Integer.parseInt(v) & 0xffff));
                    
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return t61728;
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.lang.NumberFormatException e) {
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61729 =
                      (("Expected UShort, got: \"") + (v));
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61730 =
                      ((t61729) + ("\""));
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.OptionsParser.Err t61731 =
                      ((x10.util.OptionsParser.Err)(new x10.util.OptionsParser.Err(t61730)));
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
throw t61731;
                }
            }
            
            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public short
                                                                                                           $apply$O(
                                                                                                           final java.lang.String key,
                                                                                                           final short d){
                
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t61732 =
                  ((x10.util.HashMap)(map));
                
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61733 =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t61732).containsKey__0x10$util$HashMap$$K$O(((java.lang.String)(key)));
                
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61734 =
                  !(t61733);
                
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61734) {
                    
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return d;
                }
                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t61735 =
                  ((x10.util.HashMap)(map));
                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String v =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t61735).getOrElse__0x10$util$HashMap$$K__1x10$util$HashMap$$V$G(((java.lang.String)(key)),
                                                                                                                                         ((java.lang.String)("???")));
                
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
try {try {{
                    
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final short t61736 =
                      java.lang.Short.parseShort(v);
                    
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return t61736;
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.lang.NumberFormatException e) {
                    
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61737 =
                      (("Expected Short, got: \"") + (v));
                    
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61738 =
                      ((t61737) + ("\""));
                    
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.OptionsParser.Err t61739 =
                      ((x10.util.OptionsParser.Err)(new x10.util.OptionsParser.Err(t61738)));
                    
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
throw t61739;
                }
            }
            
            
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public int
                                                                                                           $apply__1$u$O(
                                                                                                           final java.lang.String key,
                                                                                                           final int d){
                
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t61740 =
                  ((x10.util.HashMap)(map));
                
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61741 =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t61740).containsKey__0x10$util$HashMap$$K$O(((java.lang.String)(key)));
                
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61742 =
                  !(t61741);
                
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61742) {
                    
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return d;
                }
                
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t61743 =
                  ((x10.util.HashMap)(map));
                
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String v =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t61743).getOrElse__0x10$util$HashMap$$K__1x10$util$HashMap$$V$G(((java.lang.String)(key)),
                                                                                                                                         ((java.lang.String)("???")));
                
//#line 222 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
try {try {{
                    
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61744 =
                      java.lang.Integer.parseInt(v);
                    
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return t61744;
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.lang.NumberFormatException e) {
                    
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61745 =
                      (("Expected UInt, got: \"") + (v));
                    
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61746 =
                      ((t61745) + ("\""));
                    
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.OptionsParser.Err t61747 =
                      ((x10.util.OptionsParser.Err)(new x10.util.OptionsParser.Err(t61746)));
                    
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
throw t61747;
                }
            }
            
            
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public int
                                                                                                           $apply$O(
                                                                                                           final java.lang.String key,
                                                                                                           final int d){
                
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t61748 =
                  ((x10.util.HashMap)(map));
                
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61749 =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t61748).containsKey__0x10$util$HashMap$$K$O(((java.lang.String)(key)));
                
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61750 =
                  !(t61749);
                
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61750) {
                    
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return d;
                }
                
//#line 230 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t61751 =
                  ((x10.util.HashMap)(map));
                
//#line 230 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String v =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t61751).getOrElse__0x10$util$HashMap$$K__1x10$util$HashMap$$V$G(((java.lang.String)(key)),
                                                                                                                                         ((java.lang.String)("???")));
                
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
try {try {{
                    
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final int t61752 =
                      java.lang.Integer.parseInt(v);
                    
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return t61752;
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.lang.NumberFormatException e) {
                    
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61753 =
                      (("Expected Int, got: \"") + (v));
                    
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61754 =
                      ((t61753) + ("\""));
                    
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.OptionsParser.Err t61755 =
                      ((x10.util.OptionsParser.Err)(new x10.util.OptionsParser.Err(t61754)));
                    
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
throw t61755;
                }
            }
            
            
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public long
                                                                                                           $apply__1$u$O(
                                                                                                           final java.lang.String key,
                                                                                                           final long d){
                
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t61756 =
                  ((x10.util.HashMap)(map));
                
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61757 =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t61756).containsKey__0x10$util$HashMap$$K$O(((java.lang.String)(key)));
                
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61758 =
                  !(t61757);
                
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61758) {
                    
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return d;
                }
                
//#line 239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t61759 =
                  ((x10.util.HashMap)(map));
                
//#line 239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String v =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t61759).getOrElse__0x10$util$HashMap$$K__1x10$util$HashMap$$V$G(((java.lang.String)(key)),
                                                                                                                                         ((java.lang.String)("???")));
                
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
try {try {{
                    
//#line 241 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final long t61760 =
                      x10.core.Unsigned.parseULong(v);
                    
//#line 241 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return t61760;
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.lang.NumberFormatException e) {
                    
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61761 =
                      (("Expected ULong, got: \"") + (v));
                    
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61762 =
                      ((t61761) + ("\""));
                    
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.OptionsParser.Err t61763 =
                      ((x10.util.OptionsParser.Err)(new x10.util.OptionsParser.Err(t61762)));
                    
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
throw t61763;
                }
            }
            
            
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public long
                                                                                                           $apply$O(
                                                                                                           final java.lang.String key,
                                                                                                           final long d){
                
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t61764 =
                  ((x10.util.HashMap)(map));
                
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61765 =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t61764).containsKey__0x10$util$HashMap$$K$O(((java.lang.String)(key)));
                
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61766 =
                  !(t61765);
                
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61766) {
                    
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return d;
                }
                
//#line 248 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t61767 =
                  ((x10.util.HashMap)(map));
                
//#line 248 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String v =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t61767).getOrElse__0x10$util$HashMap$$K__1x10$util$HashMap$$V$G(((java.lang.String)(key)),
                                                                                                                                         ((java.lang.String)("???")));
                
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
try {try {{
                    
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final long t61768 =
                      java.lang.Long.parseLong(v);
                    
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return t61768;
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.lang.NumberFormatException e) {
                    
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61769 =
                      (("Expected Long, got: \"") + (v));
                    
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61770 =
                      ((t61769) + ("\""));
                    
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.OptionsParser.Err t61771 =
                      ((x10.util.OptionsParser.Err)(new x10.util.OptionsParser.Err(t61770)));
                    
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
throw t61771;
                }
            }
            
            
//#line 257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public double
                                                                                                           $apply$O(
                                                                                                           final java.lang.String key,
                                                                                                           final double d){
                
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t61772 =
                  ((x10.util.HashMap)(map));
                
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61773 =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t61772).containsKey__0x10$util$HashMap$$K$O(((java.lang.String)(key)));
                
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61774 =
                  !(t61773);
                
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61774) {
                    
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return d;
                }
                
//#line 259 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t61775 =
                  ((x10.util.HashMap)(map));
                
//#line 259 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String v =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t61775).getOrElse__0x10$util$HashMap$$K__1x10$util$HashMap$$V$G(((java.lang.String)(key)),
                                                                                                                                         ((java.lang.String)("???")));
                
//#line 260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
try {try {{
                    
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final double t61776 =
                      java.lang.Double.parseDouble(v);
                    
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return t61776;
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.lang.NumberFormatException e) {
                    
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61777 =
                      (("Expected Double, got: \"") + (v));
                    
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61778 =
                      ((t61777) + ("\""));
                    
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.OptionsParser.Err t61779 =
                      ((x10.util.OptionsParser.Err)(new x10.util.OptionsParser.Err(t61778)));
                    
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
throw t61779;
                }
            }
            
            
//#line 266 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
public float
                                                                                                           $apply$O(
                                                                                                           final java.lang.String key,
                                                                                                           final float d){
                
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t61780 =
                  ((x10.util.HashMap)(map));
                
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61781 =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t61780).containsKey__0x10$util$HashMap$$K$O(((java.lang.String)(key)));
                
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final boolean t61782 =
                  !(t61781);
                
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
if (t61782) {
                    
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return d;
                }
                
//#line 268 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t61783 =
                  ((x10.util.HashMap)(map));
                
//#line 268 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String v =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t61783).getOrElse__0x10$util$HashMap$$K__1x10$util$HashMap$$V$G(((java.lang.String)(key)),
                                                                                                                                         ((java.lang.String)("???")));
                
//#line 269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
try {try {{
                    
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final float t61784 =
                      java.lang.Float.parseFloat(v);
                    
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return t61784;
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.lang.NumberFormatException e) {
                    
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61785 =
                      (("Expected Float, got: \"") + (v));
                    
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final java.lang.String t61786 =
                      ((t61785) + ("\""));
                    
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final x10.util.OptionsParser.Err t61787 =
                      ((x10.util.OptionsParser.Err)(new x10.util.OptionsParser.Err(t61786)));
                    
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
throw t61787;
                }
            }
            
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
final public x10.util.OptionsParser
                                                                                                          x10$util$OptionsParser$$x10$util$OptionsParser$this(
                                                                                                          ){
                
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/OptionsParser.x10"
return x10.util.OptionsParser.this;
            }
        // synthetic type for parameter mangling
        public abstract static class __0$1x10$lang$String$2__1$1x10$util$Option$2__2$1x10$util$Option$2 {}
        
        }
        