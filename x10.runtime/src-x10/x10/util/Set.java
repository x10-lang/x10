package x10.util;

@x10.core.X10Generated public interface Set<$T> extends x10.util.Collection, x10.x10rt.X10JavaSerializable
{
    public static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Set.class);
    
    public static final x10.rtt.RuntimeType<Set> $RTT = x10.rtt.NamedType.<Set> make(
    "x10.util.Set", /* base class */Set.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.util.Collection.$RTT, x10.rtt.UnresolvedType.PARAM(0))}
    );
    
        
}
