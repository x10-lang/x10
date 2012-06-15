package x10.lang;


@x10.core.X10Generated public interface Settable<$I, $V> extends x10.core.Any
{
    public static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Settable.class);
    
    public static final x10.rtt.RuntimeType<Settable> $RTT = x10.rtt.NamedType.<Settable> make(
    "x10.lang.Settable", /* base class */Settable.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
    , /* parents */ new x10.rtt.Type[] {}
    );
    
        
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Settable.x10"
java.lang.Object
                                                                                                 $set(
                                                                                                 final java.lang.Object i,x10.rtt.Type t1,
                                                                                                 final java.lang.Object v,x10.rtt.Type t2);
    
}
