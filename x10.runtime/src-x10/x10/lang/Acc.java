package x10.lang;

@x10.core.X10Generated abstract public class Acc extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Acc.class);
    
    public static final x10.rtt.RuntimeType<Acc> $RTT = x10.rtt.NamedType.<Acc> make(
    "x10.lang.Acc", /* base class */Acc.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Acc $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Acc.class + " calling"); } 
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
    public Acc(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Acc.x10"
abstract public void
                                                                                            supply(
                                                                                            final java.lang.Object t);
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Acc.x10"
abstract public void
                                                                                            reset(
                                                                                            final java.lang.Object t);
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Acc.x10"
abstract public java.lang.Object
                                                                                            result(
                                                                                            );
        
        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Acc.x10"
abstract public java.lang.Object
                                                                                            calcResult(
                                                                                            );
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Acc.x10"
abstract public void
                                                                                            acceptResult(
                                                                                            final java.lang.Object a);
        
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Acc.x10"
abstract public x10.core.GlobalRef<x10.lang.Acc>
                                                                                            getRoot(
                                                                                            );
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Acc.x10"
abstract public x10.lang.Place
                                                                                            home(
                                                                                            );
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Acc.x10"
final public x10.lang.Acc
                                                                                            x10$lang$Acc$$x10$lang$Acc$this(
                                                                                            ){
            
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Acc.x10"
return x10.lang.Acc.this;
        }
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Acc.x10"

        // constructor for non-virtual call
        final public x10.lang.Acc x10$lang$Acc$$init$S() { {
                                                                  
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Acc.x10"

                                                                  
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Acc.x10"

                                                              }
                                                              return this;
                                                              }
        
        // constructor
        public x10.lang.Acc $init(){return x10$lang$Acc$$init$S();}
        
    
}
