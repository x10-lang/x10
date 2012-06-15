package x10.io;


@x10.core.X10Generated public class ReaderIterator<$T> extends x10.core.Ref implements x10.lang.Iterator, x10.lang.Iterable, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, ReaderIterator.class);
    
    public static final x10.rtt.RuntimeType<ReaderIterator> $RTT = x10.rtt.NamedType.<ReaderIterator> make(
    "x10.io.ReaderIterator", /* base class */ReaderIterator.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterator.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.ParameterizedType.make(x10.lang.Iterable.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(ReaderIterator $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + ReaderIterator.class + " calling"); } 
        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
        x10.io.Reader r = (x10.io.Reader) $deserializer.readRef();
        $_obj.r = r;
        x10.io.Marshal m = (x10.io.Marshal) $deserializer.readRef();
        $_obj.m = m;
        x10.util.Box next = (x10.util.Box) $deserializer.readRef();
        $_obj.next = next;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        ReaderIterator $_obj = new ReaderIterator((java.lang.System[]) null, (x10.rtt.Type) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
        if (r instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.r);
        } else {
        $serializer.write(this.r);
        }
        if (m instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.m);
        } else {
        $serializer.write(this.m);
        }
        if (next instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.next);
        } else {
        $serializer.write(this.next);
        }
        
    }
    
    // constructor just for allocation
    public ReaderIterator(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
    super($dummy);
    x10.io.ReaderIterator.$initParams(this, $T);
    }
    
        private x10.rtt.Type $T;
        // initializer of type parameters
        public static void $initParams(final ReaderIterator $this, final x10.rtt.Type $T) {
        $this.$T = $T;
        }
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
public x10.io.Reader r;
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
public x10.io.Marshal<$T> m;
        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
public x10.util.Box<$T> next;
        
        
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
// creation method for java code (1-phase java constructor)
        public ReaderIterator(final x10.rtt.Type $T,
                              final x10.io.Marshal<$T> m,
                              final x10.io.Reader r, __0$1x10$io$ReaderIterator$$T$2 $dummy){this((java.lang.System[]) null, $T);
                                                                                                 $init(m,r, (x10.io.ReaderIterator.__0$1x10$io$ReaderIterator$$T$2) null);}
        
        // constructor for non-virtual call
        final public x10.io.ReaderIterator<$T> x10$io$ReaderIterator$$init$S(final x10.io.Marshal<$T> m,
                                                                             final x10.io.Reader r, __0$1x10$io$ReaderIterator$$T$2 $dummy) { {
                                                                                                                                                     
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"

                                                                                                                                                     
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"

                                                                                                                                                     
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
this.__fieldInitializers50239();
                                                                                                                                                     
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
this.m = ((x10.io.Marshal)(m));
                                                                                                                                                     
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
this.r = ((x10.io.Reader)(r));
                                                                                                                                                 }
                                                                                                                                                 return this;
                                                                                                                                                 }
        
        // constructor
        public x10.io.ReaderIterator<$T> $init(final x10.io.Marshal<$T> m,
                                               final x10.io.Reader r, __0$1x10$io$ReaderIterator$$T$2 $dummy){return x10$io$ReaderIterator$$init$S(m,r, $dummy);}
        
        
        
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
public x10.lang.Iterator<$T>
                                                                                                     iterator(
                                                                                                     ){
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
return this;
        }
        
        
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
public $T
                                                                                                     next$G(
                                                                                                     ){
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
final boolean t50240 =
              this.hasNext$O();
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
final boolean t50242 =
              !(t50240);
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
if (t50242) {
                
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
final x10.util.NoSuchElementException t50241 =
                  ((x10.util.NoSuchElementException)(new x10.util.NoSuchElementException()));
                
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
throw t50241;
            }
            
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
final x10.util.Box<$T> t50243 =
              ((x10.util.Box)(next));
            
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
final $T x =
              (($T)(((x10.util.Box<$T>)t50243).
                      value));
            
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
this.next = null;
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
return x;
        }
        
        
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
public boolean
                                                                                                     hasNext$O(
                                                                                                     ){
            
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
final x10.util.Box<$T> t50244 =
              ((x10.util.Box)(next));
            
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
final boolean t50248 =
              ((t50244) == (null));
            
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
if (t50248) {
                
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
try {try {{
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
final x10.io.Reader t50245 =
                      ((x10.io.Reader)(r));
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
final x10.io.Marshal<$T> t50246 =
                      ((x10.io.Marshal)(m));
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
final $T x =
                      (($T)(((x10.io.Reader)t50245).<$T>read__0$1x10$io$Reader$$T$2$G($T, ((x10.io.Marshal)(t50246)))));
                    
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
final x10.util.Box<$T> t50247 =
                      ((x10.util.Box)(new x10.util.Box<$T>((java.lang.System[]) null, $T).$init((($T)(x)), (x10.util.Box.__0x10$util$Box$$T) null)));
                    
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
this.next = ((x10.util.Box)(t50247));
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.io.IOException id$107) {
                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
return false;
                }
            }
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
return true;
        }
        
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
final public x10.io.ReaderIterator<$T>
                                                                                                     x10$io$ReaderIterator$$x10$io$ReaderIterator$this(
                                                                                                     ){
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
return x10.io.ReaderIterator.this;
        }
        
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
final public void
                                                                                                     __fieldInitializers50239(
                                                                                                     ){
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/ReaderIterator.x10"
this.next = null;
        }
    // synthetic type for parameter mangling
    public abstract static class __0$1x10$io$ReaderIterator$$T$2 {}
    
}
