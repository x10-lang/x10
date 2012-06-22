package x10.util;

@x10.core.X10Generated public interface List<$T> extends x10.util.Collection, x10.util.Indexed, x10.lang.Settable, x10.x10rt.X10JavaSerializable
{
    public static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, List.class);
    
    public static final x10.rtt.RuntimeType<List> $RTT = x10.rtt.NamedType.<List> make(
    "x10.util.List", /* base class */List.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.util.Collection.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.ParameterizedType.make(x10.util.Indexed.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.ParameterizedType.make(x10.lang.Settable.$RTT, x10.rtt.Types.INT, x10.rtt.UnresolvedType.PARAM(0))}
    );
    
        
        
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/List.x10"
x10.util.List<x10.core.Int>
                                                                                             indices(
                                                                                             );
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/List.x10"
java.lang.Object
                                                                                             addBefore(
                                                                                             final int i,
                                                                                             final java.lang.Object id$164,x10.rtt.Type t1);
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/List.x10"
$T
                                                                                             removeAt$G(
                                                                                             final int i);
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/List.x10"
java.lang.Object
                                                                                             indexOf(
                                                                                             final java.lang.Object id$165,x10.rtt.Type t1);
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/List.x10"
java.lang.Object
                                                                                             lastIndexOf(
                                                                                             final java.lang.Object id$166,x10.rtt.Type t1);
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/List.x10"
java.lang.Object
                                                                                             indexOf(
                                                                                             final int id$167,
                                                                                             final java.lang.Object id$168,x10.rtt.Type t1);
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/List.x10"
java.lang.Object
                                                                                             lastIndexOf(
                                                                                             final int id$169,
                                                                                             final java.lang.Object id$170,x10.rtt.Type t1);
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/List.x10"
x10.util.ListIterator<$T>
                                                                                             iterator(
                                                                                             );
        
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/List.x10"
x10.util.ListIterator<$T>
                                                                                             iteratorFrom(
                                                                                             final int i);
        
        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/List.x10"
x10.util.List<$T>
                                                                                             subList(
                                                                                             final int fromIndex,
                                                                                             final int toIndex);
        
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/List.x10"
$T
                                                                                             removeFirst$G(
                                                                                             );
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/List.x10"
$T
                                                                                             removeLast$G(
                                                                                             );
        
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/List.x10"
$T
                                                                                             getFirst$G(
                                                                                             );
        
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/List.x10"
$T
                                                                                             getLast$G(
                                                                                             );
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/List.x10"
void
                                                                                             reverse(
                                                                                             );
        
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/List.x10"
void
                                                                                             sort(
                                                                                             );
        
        
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/List.x10"
java.lang.Object
                                                                                             sort(
                                                                                             final x10.core.fun.Fun_0_2 cmp,x10.rtt.Type t1);
    
}
