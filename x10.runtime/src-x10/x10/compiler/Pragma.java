package x10.compiler;


@x10.core.X10Generated public interface Pragma extends x10.lang.annotations.StatementAnnotation, x10.x10rt.X10JavaSerializable
{
    public static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Pragma.class);
    
    public static final x10.rtt.RuntimeType<Pragma> $RTT = x10.rtt.NamedType.<Pragma> make(
    "x10.compiler.Pragma", /* base class */Pragma.class
    , /* parents */ new x10.rtt.Type[] {x10.lang.annotations.StatementAnnotation.$RTT}
    );
    
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Pragma.x10"
int FINISH_ASYNC = 1;
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Pragma.x10"
int FINISH_HERE = 2;
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Pragma.x10"
int FINISH_SPMD = 3;
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Pragma.x10"
int FINISH_LOCAL = 4;
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Pragma.x10"
int FINISH_ASYNC_AND_BACK = x10.compiler.Pragma.FINISH_HERE;
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Pragma.x10"
int FINISH_ATEACH_UNIQUE = x10.compiler.Pragma.FINISH_SPMD;
        @x10.core.X10Generated abstract public class Shadow extends java.lang.Object implements x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Shadow.class);
            
            public static final x10.rtt.RuntimeType<Shadow> $RTT = x10.rtt.NamedType.<Shadow> make(
            "x10.compiler.Pragma.Shadow", /* base class */Shadow.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.ANY}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(Shadow $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Shadow.class + " calling"); } 
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
            public Shadow(final java.lang.System[] $dummy) { 
            }
            
                
                public static int
                  getInitialized$FINISH_ASYNC(
                  ){
                    return x10.compiler.Pragma.FINISH_ASYNC;
                }
                
                public static int
                  getInitialized$FINISH_HERE(
                  ){
                    return x10.compiler.Pragma.FINISH_HERE;
                }
                
                public static int
                  getInitialized$FINISH_SPMD(
                  ){
                    return x10.compiler.Pragma.FINISH_SPMD;
                }
                
                public static int
                  getInitialized$FINISH_LOCAL(
                  ){
                    return x10.compiler.Pragma.FINISH_LOCAL;
                }
                
                public static int
                  getInitialized$FINISH_ASYNC_AND_BACK(
                  ){
                    return x10.compiler.Pragma.FINISH_ASYNC_AND_BACK;
                }
                
                public static int
                  getInitialized$FINISH_ATEACH_UNIQUE(
                  ){
                    return x10.compiler.Pragma.FINISH_ATEACH_UNIQUE;
                }
            
        }
        
    
}
