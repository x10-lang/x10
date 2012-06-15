package x10.lang;


@x10.core.X10Generated public interface Iterator<$T> extends x10.core.Any
{
    public static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Iterator.class);
    
    public static final x10.rtt.RuntimeType<Iterator> $RTT = x10.rtt.NamedType.<Iterator> make(
    "x10.lang.Iterator", /* base class */Iterator.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {}
    );
    
        
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Iterator.x10"
boolean
                                                                                                 hasNext$O(
                                                                                                 );
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Iterator.x10"
$T
                                                                                                 next$G(
                                                                                                 );
    
}
