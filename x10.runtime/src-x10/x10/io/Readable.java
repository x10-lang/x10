package x10.io;

@x10.core.X10Generated public interface Readable extends x10.core.Any
{
    public static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Readable.class);
    
    public static final x10.rtt.RuntimeType<Readable> $RTT = x10.rtt.NamedType.<Readable> make(
    "x10.io.Readable", /* base class */Readable.class
    , /* parents */ new x10.rtt.Type[] {}
    );
    
        
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Readable.x10"
x10.io.Readable
                                                                                               read(
                                                                                               final x10.io.Reader r);
    
}
