package x10.lang;


@x10.core.X10Generated public interface Iterable<$T> extends x10.core.Any
{
    public static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Iterable.class);
    
    public static final x10.rtt.RuntimeType<Iterable> $RTT = x10.rtt.NamedType.<Iterable> make(
    "x10.lang.Iterable", /* base class */Iterable.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {}
    );
    
        
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Iterable.x10"
x10.lang.Iterator<$T>
                                                                                                 iterator(
                                                                                                 );
    
}
