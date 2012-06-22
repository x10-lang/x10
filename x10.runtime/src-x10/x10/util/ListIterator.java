package x10.util;

@x10.core.X10Generated public interface ListIterator<$T> extends x10.util.CollectionIterator, x10.x10rt.X10JavaSerializable
{
    public static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, ListIterator.class);
    
    public static final x10.rtt.RuntimeType<ListIterator> $RTT = x10.rtt.NamedType.<ListIterator> make(
    "x10.util.ListIterator", /* base class */ListIterator.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.util.CollectionIterator.$RTT, x10.rtt.UnresolvedType.PARAM(0))}
    );
    
        
        
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ListIterator.x10"
boolean
                                                                                                     hasNext$O(
                                                                                                     );
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ListIterator.x10"
$T
                                                                                                     next$G(
                                                                                                     );
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ListIterator.x10"
int
                                                                                                     nextIndex$O(
                                                                                                     );
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ListIterator.x10"
boolean
                                                                                                     hasPrevious$O(
                                                                                                     );
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ListIterator.x10"
$T
                                                                                                     previous$G(
                                                                                                     );
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ListIterator.x10"
int
                                                                                                     previousIndex$O(
                                                                                                     );
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ListIterator.x10"
java.lang.Object
                                                                                                     set(
                                                                                                     final java.lang.Object id$173,x10.rtt.Type t1);
        
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ListIterator.x10"
java.lang.Object
                                                                                                     add(
                                                                                                     final java.lang.Object id$174,x10.rtt.Type t1);
    
}
