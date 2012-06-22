package x10.util;

@x10.core.X10Generated public interface Collection<$T> extends x10.util.Container, x10.x10rt.X10JavaSerializable
{
    public static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Collection.class);
    
    public static final x10.rtt.RuntimeType<Collection> $RTT = x10.rtt.NamedType.<Collection> make(
    "x10.util.Collection", /* base class */Collection.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.util.Container.$RTT, x10.rtt.UnresolvedType.PARAM(0))}
    );
    
        
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Collection.x10"
java.lang.Object
                                                                                                   add(
                                                                                                   final java.lang.Object id$150,x10.rtt.Type t1);
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Collection.x10"
java.lang.Object
                                                                                                   remove(
                                                                                                   final java.lang.Object id$151,x10.rtt.Type t1);
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Collection.x10"
java.lang.Object
                                                                                                   addAll(
                                                                                                   final x10.util.Container id$152,x10.rtt.Type t1);
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Collection.x10"
java.lang.Object
                                                                                                   retainAll(
                                                                                                   final x10.util.Container id$153,x10.rtt.Type t1);
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Collection.x10"
java.lang.Object
                                                                                                   removeAll(
                                                                                                   final x10.util.Container id$154,x10.rtt.Type t1);
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Collection.x10"
java.lang.Object
                                                                                                   addAllWhere(
                                                                                                   final x10.util.Container id$155,x10.rtt.Type t1,
                                                                                                   final x10.core.fun.Fun_0_1 id$157,x10.rtt.Type t2);
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Collection.x10"
java.lang.Object
                                                                                                   removeAllWhere(
                                                                                                   final x10.core.fun.Fun_0_1 id$159,x10.rtt.Type t1);
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Collection.x10"
void
                                                                                                   clear(
                                                                                                   );
        
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Collection.x10"
x10.util.Collection<$T>
                                                                                                   clone(
                                                                                                   );
    
}
