package x10.util.concurrent;

@x10.core.X10Generated final public class AtomicFloat extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, AtomicFloat.class);
    
    public static final x10.rtt.RuntimeType<AtomicFloat> $RTT = x10.rtt.NamedType.<AtomicFloat> make(
    "x10.util.concurrent.AtomicFloat", /* base class */AtomicFloat.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(AtomicFloat $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + AtomicFloat.class + " calling"); } 
        x10.core.concurrent.AtomicInteger v = (x10.core.concurrent.AtomicInteger) $deserializer.readRef();
        $_obj.v = v;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        AtomicFloat $_obj = new AtomicFloat((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (v instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.v);
        } else {
        $serializer.write(this.v);
        }
        
    }
    
    // constructor just for allocation
    public AtomicFloat(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
public x10.core.concurrent.AtomicInteger v;
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
// creation method for java code (1-phase java constructor)
        public AtomicFloat(){this((java.lang.System[]) null);
                                 $init();}
        
        // constructor for non-virtual call
        final public x10.util.concurrent.AtomicFloat x10$util$concurrent$AtomicFloat$$init$S() { {
                                                                                                        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"

                                                                                                        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"

                                                                                                        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final int t63734 =
                                                                                                          java.lang.Float.floatToRawIntBits(0.0F);
                                                                                                        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final x10.core.concurrent.AtomicInteger t63735 =
                                                                                                          ((x10.core.concurrent.AtomicInteger)(new x10.core.concurrent.AtomicInteger(t63734)));
                                                                                                        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
this.v = ((x10.core.concurrent.AtomicInteger)(t63735));
                                                                                                    }
                                                                                                    return this;
                                                                                                    }
        
        // constructor
        public x10.util.concurrent.AtomicFloat $init(){return x10$util$concurrent$AtomicFloat$$init$S();}
        
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
// creation method for java code (1-phase java constructor)
        public AtomicFloat(final float v){this((java.lang.System[]) null);
                                              $init(v);}
        
        // constructor for non-virtual call
        final public x10.util.concurrent.AtomicFloat x10$util$concurrent$AtomicFloat$$init$S(final float v) { {
                                                                                                                     
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"

                                                                                                                     
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"

                                                                                                                     
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final int t63736 =
                                                                                                                       java.lang.Float.floatToRawIntBits(v);
                                                                                                                     
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final x10.core.concurrent.AtomicInteger t63737 =
                                                                                                                       ((x10.core.concurrent.AtomicInteger)(new x10.core.concurrent.AtomicInteger(t63736)));
                                                                                                                     
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
this.v = ((x10.core.concurrent.AtomicInteger)(t63737));
                                                                                                                 }
                                                                                                                 return this;
                                                                                                                 }
        
        // constructor
        public x10.util.concurrent.AtomicFloat $init(final float v){return x10$util$concurrent$AtomicFloat$$init$S(v);}
        
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
public float
                                                                                                               get(
                                                                                                               ){
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final x10.core.concurrent.AtomicInteger t63738 =
              ((x10.core.concurrent.AtomicInteger)(v));
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final int t63739 =
              t63738.get();
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final float t63740 =
              java.lang.Float.intBitsToFloat(((int)(t63739)));
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
return t63740;
        }
        
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
public void
                                                                                                               set(
                                                                                                               final float v){
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final x10.core.concurrent.AtomicInteger t63741 =
              ((x10.core.concurrent.AtomicInteger)(this.
                                                     v));
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final int t63742 =
              java.lang.Float.floatToRawIntBits(v);
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
t63741.set(((int)(t63742)));
        }
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
public boolean
                                                                                                               compareAndSet(
                                                                                                               final float expect,
                                                                                                               final float update){
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final int exp =
              java.lang.Float.floatToRawIntBits(expect);
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final int upd =
              java.lang.Float.floatToRawIntBits(update);
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final x10.core.concurrent.AtomicInteger t63743 =
              ((x10.core.concurrent.AtomicInteger)(v));
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final boolean t63744 =
              t63743.compareAndSet(((int)(exp)),((int)(upd)));
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
return t63744;
        }
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
public boolean
                                                                                                               weakCompareAndSet(
                                                                                                               final float expect,
                                                                                                               final float update){
            
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final int exp =
              java.lang.Float.floatToRawIntBits(expect);
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final int upd =
              java.lang.Float.floatToRawIntBits(update);
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final x10.core.concurrent.AtomicInteger t63745 =
              ((x10.core.concurrent.AtomicInteger)(v));
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final boolean t63746 =
              t63745.weakCompareAndSet(((int)(exp)),((int)(upd)));
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
return t63746;
        }
        
        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
public float
                                                                                                               getAndIncrement(
                                                                                                               ){
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final float t63747 =
              ((float)(int)(((int)(1))));
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final float t63748 =
              this.getAndAdd((float)(t63747));
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
return t63748;
        }
        
        
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
public float
                                                                                                               getAndDecrement(
                                                                                                               ){
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final float t63749 =
              ((float)(int)(((int)(-1))));
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final float t63750 =
              this.getAndAdd((float)(t63749));
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
return t63750;
        }
        
        
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
public float
                                                                                                               getAndAdd(
                                                                                                               final float delta){
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
do  {
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final x10.core.concurrent.AtomicInteger t63751 =
                  ((x10.core.concurrent.AtomicInteger)(v));
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final int exp =
                  t63751.get();
                
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final float expect =
                  java.lang.Float.intBitsToFloat(((int)(exp)));
                
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final float update =
                  ((expect) + (((float)(delta))));
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final int upd =
                  java.lang.Float.floatToRawIntBits(update);
                
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final x10.core.concurrent.AtomicInteger t63752 =
                  ((x10.core.concurrent.AtomicInteger)(v));
                
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final boolean t63753 =
                  t63752.weakCompareAndSet(((int)(exp)),((int)(upd)));
                
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
if (t63753) {
                    
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
return expect;
                }
            }while(true); 
        }
        
        
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
public float
                                                                                                               incrementAndGet(
                                                                                                               ){
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final float t63754 =
              ((float)(int)(((int)(1))));
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final float t63755 =
              this.addAndGet((float)(t63754));
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
return t63755;
        }
        
        
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
public float
                                                                                                               decrementAndGet(
                                                                                                               ){
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final float t63756 =
              ((float)(int)(((int)(-1))));
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final float t63757 =
              this.addAndGet((float)(t63756));
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
return t63757;
        }
        
        
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
public float
                                                                                                               addAndGet(
                                                                                                               final float delta){
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
do  {
                
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final x10.core.concurrent.AtomicInteger t63758 =
                  ((x10.core.concurrent.AtomicInteger)(v));
                
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final int exp =
                  t63758.get();
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final float expect =
                  java.lang.Float.intBitsToFloat(((int)(exp)));
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final float update =
                  ((expect) + (((float)(delta))));
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final int upd =
                  java.lang.Float.floatToRawIntBits(update);
                
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final x10.core.concurrent.AtomicInteger t63759 =
                  ((x10.core.concurrent.AtomicInteger)(v));
                
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final boolean t63760 =
                  t63759.weakCompareAndSet(((int)(exp)),((int)(upd)));
                
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
if (t63760) {
                    
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
return update;
                }
            }while(true); 
        }
        
        
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
public java.lang.String
                                                                                                               toString(
                                                                                                               ){
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final float t63761 =
              this.get();
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final java.lang.String t63762 =
              java.lang.Float.toString(t63761);
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
return t63762;
        }
        
        
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
public int
                                                                                                               intValue(
                                                                                                               ){
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final float t63763 =
              this.get();
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final int t63764 =
              ((int)(float)(((float)(t63763))));
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
return t63764;
        }
        
        
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
public long
                                                                                                               longValue(
                                                                                                               ){
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final float t63765 =
              this.get();
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final long t63766 =
              ((long)(float)(((float)(t63765))));
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
return t63766;
        }
        
        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
public float
                                                                                                               floatValue(
                                                                                                               ){
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final float t63767 =
              this.get();
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final float t63768 =
              t63767;
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
return t63768;
        }
        
        
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
public double
                                                                                                               doubleValue(
                                                                                                               ){
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final float t63769 =
              this.get();
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final double t63770 =
              ((double)(float)(((float)(t63769))));
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
return t63770;
        }
        
        
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
final public x10.util.concurrent.AtomicFloat
                                                                                                               x10$util$concurrent$AtomicFloat$$x10$util$concurrent$AtomicFloat$this(
                                                                                                               ){
            
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicFloat.x10"
return x10.util.concurrent.AtomicFloat.this;
        }
    
}
