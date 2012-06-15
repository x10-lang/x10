package x10.util;

@x10.core.X10Generated public interface Map<$K, $V> extends x10.core.Any
{
    public static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Map.class);
    
    public static final x10.rtt.RuntimeType<Map> $RTT = x10.rtt.NamedType.<Map> make(
    "x10.util.Map", /* base class */Map.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
    , /* parents */ new x10.rtt.Type[] {}
    );
    
        
        
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Map.x10"
java.lang.Object
                                                                                            containsKey(
                                                                                            final java.lang.Object k,x10.rtt.Type t1);
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Map.x10"
java.lang.Object
                                                                                            get(
                                                                                            final java.lang.Object k,x10.rtt.Type t1);
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Map.x10"
java.lang.Object
                                                                                            getOrElse(
                                                                                            final java.lang.Object k,x10.rtt.Type t1,
                                                                                            final java.lang.Object orelse,x10.rtt.Type t2);
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Map.x10"
java.lang.Object
                                                                                            getOrThrow(
                                                                                            final java.lang.Object k,x10.rtt.Type t1);
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Map.x10"
java.lang.Object
                                                                                            put(
                                                                                            final java.lang.Object k,x10.rtt.Type t1,
                                                                                            final java.lang.Object v,x10.rtt.Type t2);
        
        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Map.x10"
java.lang.Object
                                                                                            remove(
                                                                                            final java.lang.Object k,x10.rtt.Type t1);
        
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Map.x10"
x10.util.Set<$K>
                                                                                            keySet(
                                                                                            );
        
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Map.x10"
void
                                                                                            clear(
                                                                                            );
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Map.x10"
x10.util.Set<x10.util.Map.Entry<$K, $V>>
                                                                                            entries(
                                                                                            );
        
        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Map.x10"
@x10.core.X10Generated public static interface Entry<$Key, $Val> extends x10.core.Any
                                                                                          {
            public static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Entry.class);
            
            public static final x10.rtt.RuntimeType<Entry> $RTT = x10.rtt.NamedType.<Entry> make(
            "x10.util.Map.Entry", /* base class */Entry.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
            , /* parents */ new x10.rtt.Type[] {}
            );
            
                
                
                
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Map.x10"
$Key
                                                                                                    getKey$G(
                                                                                                    );
                
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Map.x10"
$Val
                                                                                                    getValue$G(
                                                                                                    );
                
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Map.x10"
java.lang.Object
                                                                                                    setValue(
                                                                                                    final java.lang.Object id$175,x10.rtt.Type t1);
            
        }
        
    
}
