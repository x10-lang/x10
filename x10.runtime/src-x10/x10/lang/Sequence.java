package x10.lang;


@x10.core.X10Generated public interface Sequence<$T> extends x10.core.fun.Fun_0_1, x10.lang.Iterable, x10.x10rt.X10JavaSerializable
{
    public static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Sequence.class);
    
    public static final x10.rtt.RuntimeType<Sequence> $RTT = x10.rtt.NamedType.<Sequence> make(
    "x10.lang.Sequence", /* base class */Sequence.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.ParameterizedType.make(x10.lang.Iterable.$RTT, x10.rtt.UnresolvedType.PARAM(0))}
    );
    
        
        
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Sequence.x10"
$T
                                                                                                 $apply$G(
                                                                                                 final int id$125);
        
        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Sequence.x10"
int
                                                                                                 size$O(
                                                                                                 );
    
}
