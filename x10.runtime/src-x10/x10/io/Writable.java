package x10.io;

@x10.core.X10Generated public interface Writable extends x10.core.Any
{
    public static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Writable.class);
    
    public static final x10.rtt.RuntimeType<Writable> $RTT = x10.rtt.NamedType.<Writable> make(
    "x10.io.Writable", /* base class */Writable.class
    , /* parents */ new x10.rtt.Type[] {}
    );
    
        
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Writable.x10"
void
                                                                                               write(
                                                                                               final x10.io.Writer w);
    
}
