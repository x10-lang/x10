package x10.util;

@x10.core.X10Generated public interface Container<$T> extends x10.lang.Iterable, x10.x10rt.X10JavaSerializable
{
    public static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Container.class);
    
    public static final x10.rtt.RuntimeType<Container> $RTT = x10.rtt.NamedType.<Container> make(
    "x10.util.Container", /* base class */Container.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterable.$RTT, x10.rtt.UnresolvedType.PARAM(0))}
    );
    
        
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Container.x10"
int
                                                                                                  size$O(
                                                                                                  );
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Container.x10"
boolean
                                                                                                  isEmpty$O(
                                                                                                  );
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Container.x10"
java.lang.Object
                                                                                                  contains(
                                                                                                  final java.lang.Object id$160,x10.rtt.Type t1);
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Container.x10"
java.lang.Object
                                                                                                  containsAll(
                                                                                                  final x10.util.Container id$161,x10.rtt.Type t1);
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Container.x10"
x10.util.Container<$T>
                                                                                                  clone(
                                                                                                  );
    
}
