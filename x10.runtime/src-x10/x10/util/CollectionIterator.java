package x10.util;

@x10.core.X10Generated public interface CollectionIterator<$T> extends x10.lang.Iterator, x10.x10rt.X10JavaSerializable
{
    public static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, CollectionIterator.class);
    
    public static final x10.rtt.RuntimeType<CollectionIterator> $RTT = x10.rtt.NamedType.<CollectionIterator> make(
    "x10.util.CollectionIterator", /* base class */CollectionIterator.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterator.$RTT, x10.rtt.UnresolvedType.PARAM(0))}
    );
    
        
        
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/CollectionIterator.x10"
void
                                                                                                           remove(
                                                                                                           );
    
}
