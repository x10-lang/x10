package x10.util.concurrent;

@x10.core.X10Generated final public class AtomicDouble extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, AtomicDouble.class);
    
    public static final x10.rtt.RuntimeType<AtomicDouble> $RTT = x10.rtt.NamedType.<AtomicDouble> make(
    "x10.util.concurrent.AtomicDouble", /* base class */AtomicDouble.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(AtomicDouble $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + AtomicDouble.class + " calling"); } 
        x10.core.concurrent.AtomicLong v = (x10.core.concurrent.AtomicLong) $deserializer.readRef();
        $_obj.v = v;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        AtomicDouble $_obj = new AtomicDouble((java.lang.System[]) null);
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
    public AtomicDouble(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
public x10.core.concurrent.AtomicLong v;
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
// creation method for java code (1-phase java constructor)
        public AtomicDouble(){this((java.lang.System[]) null);
                                  $init();}
        
        // constructor for non-virtual call
        final public x10.util.concurrent.AtomicDouble x10$util$concurrent$AtomicDouble$$init$S() { {
                                                                                                          
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"

                                                                                                          
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"

                                                                                                          
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final long t63685 =
                                                                                                            java.lang.Double.doubleToRawLongBits(0.0);
                                                                                                          
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final x10.core.concurrent.AtomicLong t63686 =
                                                                                                            ((x10.core.concurrent.AtomicLong)(new x10.core.concurrent.AtomicLong(t63685)));
                                                                                                          
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
this.v = ((x10.core.concurrent.AtomicLong)(t63686));
                                                                                                      }
                                                                                                      return this;
                                                                                                      }
        
        // constructor
        public x10.util.concurrent.AtomicDouble $init(){return x10$util$concurrent$AtomicDouble$$init$S();}
        
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
// creation method for java code (1-phase java constructor)
        public AtomicDouble(final double v){this((java.lang.System[]) null);
                                                $init(v);}
        
        // constructor for non-virtual call
        final public x10.util.concurrent.AtomicDouble x10$util$concurrent$AtomicDouble$$init$S(final double v) { {
                                                                                                                        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"

                                                                                                                        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"

                                                                                                                        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final long t63687 =
                                                                                                                          java.lang.Double.doubleToRawLongBits(v);
                                                                                                                        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final x10.core.concurrent.AtomicLong t63688 =
                                                                                                                          ((x10.core.concurrent.AtomicLong)(new x10.core.concurrent.AtomicLong(t63687)));
                                                                                                                        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
this.v = ((x10.core.concurrent.AtomicLong)(t63688));
                                                                                                                    }
                                                                                                                    return this;
                                                                                                                    }
        
        // constructor
        public x10.util.concurrent.AtomicDouble $init(final double v){return x10$util$concurrent$AtomicDouble$$init$S(v);}
        
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
public double
                                                                                                                get(
                                                                                                                ){
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final x10.core.concurrent.AtomicLong t63689 =
              ((x10.core.concurrent.AtomicLong)(v));
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final long t63690 =
              t63689.get();
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final double t63691 =
              java.lang.Double.longBitsToDouble(((long)(t63690)));
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
return t63691;
        }
        
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
public void
                                                                                                                set(
                                                                                                                final double v){
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final x10.core.concurrent.AtomicLong t63692 =
              ((x10.core.concurrent.AtomicLong)(this.
                                                  v));
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final long t63693 =
              java.lang.Double.doubleToRawLongBits(v);
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
t63692.set(((long)(t63693)));
        }
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
public boolean
                                                                                                                compareAndSet(
                                                                                                                final double expect,
                                                                                                                final double update){
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final long exp =
              java.lang.Double.doubleToRawLongBits(expect);
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final long upd =
              java.lang.Double.doubleToRawLongBits(update);
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final x10.core.concurrent.AtomicLong t63694 =
              ((x10.core.concurrent.AtomicLong)(v));
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final boolean t63695 =
              t63694.compareAndSet(((long)(exp)),((long)(upd)));
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
return t63695;
        }
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
public boolean
                                                                                                                weakCompareAndSet(
                                                                                                                final double expect,
                                                                                                                final double update){
            
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final long exp =
              java.lang.Double.doubleToRawLongBits(expect);
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final long upd =
              java.lang.Double.doubleToRawLongBits(update);
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final x10.core.concurrent.AtomicLong t63696 =
              ((x10.core.concurrent.AtomicLong)(v));
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final boolean t63697 =
              t63696.weakCompareAndSet(((long)(exp)),((long)(upd)));
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
return t63697;
        }
        
        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
public double
                                                                                                                getAndIncrement(
                                                                                                                ){
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final double t63698 =
              ((double)(int)(((int)(1))));
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final double t63699 =
              this.getAndAdd((double)(t63698));
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
return t63699;
        }
        
        
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
public double
                                                                                                                getAndDecrement(
                                                                                                                ){
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final double t63700 =
              ((double)(int)(((int)(-1))));
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final double t63701 =
              this.getAndAdd((double)(t63700));
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
return t63701;
        }
        
        
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
public double
                                                                                                                getAndAdd(
                                                                                                                final double delta){
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
do  {
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final x10.core.concurrent.AtomicLong t63702 =
                  ((x10.core.concurrent.AtomicLong)(v));
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final long exp =
                  t63702.get();
                
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final double expect =
                  java.lang.Double.longBitsToDouble(((long)(exp)));
                
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final double update =
                  ((expect) + (((double)(delta))));
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final long upd =
                  java.lang.Double.doubleToRawLongBits(update);
                
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final x10.core.concurrent.AtomicLong t63703 =
                  ((x10.core.concurrent.AtomicLong)(v));
                
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final boolean t63704 =
                  t63703.weakCompareAndSet(((long)(exp)),((long)(upd)));
                
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
if (t63704) {
                    
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
return expect;
                }
            }while(true); 
        }
        
        
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
public double
                                                                                                                incrementAndGet(
                                                                                                                ){
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final double t63705 =
              ((double)(int)(((int)(1))));
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final double t63706 =
              this.addAndGet((double)(t63705));
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
return t63706;
        }
        
        
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
public double
                                                                                                                decrementAndGet(
                                                                                                                ){
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final double t63707 =
              ((double)(int)(((int)(-1))));
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final double t63708 =
              this.addAndGet((double)(t63707));
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
return t63708;
        }
        
        
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
public double
                                                                                                                addAndGet(
                                                                                                                final double delta){
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
do  {
                
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final x10.core.concurrent.AtomicLong t63709 =
                  ((x10.core.concurrent.AtomicLong)(v));
                
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final long exp =
                  t63709.get();
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final double expect =
                  java.lang.Double.longBitsToDouble(((long)(exp)));
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final double update =
                  ((expect) + (((double)(delta))));
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final long upd =
                  java.lang.Double.doubleToRawLongBits(update);
                
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final x10.core.concurrent.AtomicLong t63710 =
                  ((x10.core.concurrent.AtomicLong)(v));
                
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final boolean t63711 =
                  t63710.weakCompareAndSet(((long)(exp)),((long)(upd)));
                
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
if (t63711) {
                    
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
return update;
                }
            }while(true); 
        }
        
        
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
public java.lang.String
                                                                                                                toString(
                                                                                                                ){
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final double t63712 =
              this.get();
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final java.lang.String t63713 =
              java.lang.Double.toString(t63712);
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
return t63713;
        }
        
        
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
public int
                                                                                                                intValue(
                                                                                                                ){
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final double t63714 =
              this.get();
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final int t63715 =
              ((int)(double)(((double)(t63714))));
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
return t63715;
        }
        
        
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
public long
                                                                                                                longValue(
                                                                                                                ){
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final double t63716 =
              this.get();
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final long t63717 =
              ((long)(double)(((double)(t63716))));
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
return t63717;
        }
        
        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
public float
                                                                                                                floatValue(
                                                                                                                ){
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final double t63718 =
              this.get();
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final float t63719 =
              ((float)(double)(((double)(t63718))));
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
return t63719;
        }
        
        
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
public double
                                                                                                                doubleValue(
                                                                                                                ){
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final double t63720 =
              this.get();
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final double t63721 =
              t63720;
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
return t63721;
        }
        
        
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
final public x10.util.concurrent.AtomicDouble
                                                                                                                x10$util$concurrent$AtomicDouble$$x10$util$concurrent$AtomicDouble$this(
                                                                                                                ){
            
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/AtomicDouble.x10"
return x10.util.concurrent.AtomicDouble.this;
        }
    
}
