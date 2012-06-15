package x10.util;


@x10.core.X10Generated public interface Ordered<$T> extends x10.core.Any
{
    public static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Ordered.class);
    
    public static final x10.rtt.RuntimeType<Ordered> $RTT = x10.rtt.NamedType.<Ordered> make(
    "x10.util.Ordered", /* base class */Ordered.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {}
    );
    
        
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Ordered.x10"
java.lang.Object
                                                                                                $lt(
                                                                                                final java.lang.Object that,x10.rtt.Type t1);
        
        
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Ordered.x10"
java.lang.Object
                                                                                                $gt(
                                                                                                final java.lang.Object that,x10.rtt.Type t1);
        
        
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Ordered.x10"
java.lang.Object
                                                                                                $le(
                                                                                                final java.lang.Object that,x10.rtt.Type t1);
        
        
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Ordered.x10"
java.lang.Object
                                                                                                $ge(
                                                                                                final java.lang.Object that,x10.rtt.Type t1);
    
}
