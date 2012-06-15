package x10.compiler;


@x10.core.X10Generated public class Finalization extends x10.core.X10Throwable implements x10.io.CustomSerialization
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Finalization.class);
    
    public static final x10.rtt.RuntimeType<Finalization> $RTT = x10.rtt.NamedType.<Finalization> make(
    "x10.compiler.Finalization", /* base class */Finalization.class
    , /* parents */ new x10.rtt.Type[] {x10.io.CustomSerialization.$RTT, x10.core.X10Throwable.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    // custom serializer
    private transient x10.io.SerialData $$serialdata;
    private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
    private Object readResolve() { return new Finalization($$serialdata); }
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
    oos.writeObject($$serialdata); }
    private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
    $$serialdata = (x10.io.SerialData) ois.readObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Finalization $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + Finalization.class + " calling"); } 
        x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
        $_obj.$init($$serialdata);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Finalization $_obj = new Finalization((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println(" CustomSerialization : $_serialize of " + this + " calling"); } 
        $$serialdata = serialize(); 
        $serializer.write($$serialdata);
        
    }
    
    // dummy 2nd-phase constructor for non-splittable type
    public void $init(x10.io.SerialData $$serialdata) {
    
        throw new x10.lang.RuntimeException("dummy 2nd-phase constructor for non-splittable type should never be called.");
        
    }
    
    // constructor just for allocation
    public Finalization(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
public java.lang.Object value;
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
public java.lang.String label;
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
public boolean isReturn;
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
public boolean isBreak;
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
public boolean isContinue;
        
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
public static void
                                                                                                         throwReturn(
                                                                                                         ){
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
final x10.compiler.Finalization f =
              ((x10.compiler.Finalization)(new x10.compiler.Finalization()));
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
f.isReturn = true;
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
throw f;
        }
        
        
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
public static void
                                                                                                         throwReturn(
                                                                                                         final java.lang.Object v){
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
final x10.compiler.Finalization f =
              ((x10.compiler.Finalization)(new x10.compiler.Finalization()));
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
f.value = ((java.lang.Object)(v));
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
f.isReturn = true;
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
throw f;
        }
        
        
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
public static void
                                                                                                         throwBreak(
                                                                                                         ){
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
final x10.compiler.Finalization f =
              ((x10.compiler.Finalization)(new x10.compiler.Finalization()));
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
f.isBreak = true;
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
throw f;
        }
        
        
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
public static void
                                                                                                         throwBreak(
                                                                                                         final java.lang.String l){
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
final x10.compiler.Finalization f =
              ((x10.compiler.Finalization)(new x10.compiler.Finalization()));
            
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
f.label = ((java.lang.String)(l));
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
f.isBreak = true;
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
throw f;
        }
        
        
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
public static void
                                                                                                         throwContinue(
                                                                                                         ){
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
final x10.compiler.Finalization f =
              ((x10.compiler.Finalization)(new x10.compiler.Finalization()));
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
f.isContinue = true;
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
throw f;
        }
        
        
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
public static void
                                                                                                         throwContinue(
                                                                                                         final java.lang.String l){
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
final x10.compiler.Finalization f =
              ((x10.compiler.Finalization)(new x10.compiler.Finalization()));
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
f.label = ((java.lang.String)(l));
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
f.isContinue = true;
            
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
throw f;
        }
        
        
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
public static void
                                                                                                         plausibleThrow(
                                                                                                         ){
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
final boolean t48807 =
              true;
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
if (t48807) {
                
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
return;
            }
            
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
final x10.compiler.Finalization t48808 =
              ((x10.compiler.Finalization)(new x10.compiler.Finalization()));
            
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
throw t48808;
        }
        
        
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
public x10.io.SerialData
                                                                                                         serialize(
                                                                                                         ){
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
final java.lang.String t48809 =
              x10.rtt.Types.typeName(this);
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
final java.lang.String t48810 =
              (("Cannot serialize ") + (t48809));
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
final x10.lang.UnsupportedOperationException t48811 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(t48810)));
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
throw t48811;
        }
        
        
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
public Finalization(final x10.io.SerialData id$83) {super();
                                                                                                                                                                {
                                                                                                                                                                   
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"

                                                                                                                                                                   
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
this.__fieldInitializers48722();
                                                                                                                                                                   
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
throw new x10.lang.UnsupportedOperationException((("Cannot deserialize ") + (x10.rtt.Types.typeName(this))));
                                                                                                                                                               }}
        
        
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
public Finalization() {super();
                                                                                                                                    {
                                                                                                                                       
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"

                                                                                                                                       
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
this.__fieldInitializers48722();
                                                                                                                                   }}
        
        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
final public x10.compiler.Finalization
                                                                                                         x10$compiler$Finalization$$x10$compiler$Finalization$this(
                                                                                                         ){
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
return x10.compiler.Finalization.this;
        }
        
        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
final public void
                                                                                                         __fieldInitializers48722(
                                                                                                         ){
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
this.value = null;
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
this.label = null;
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
this.isReturn = false;
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
this.isBreak = false;
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Finalization.x10"
this.isContinue = false;
        }
        
        }
        