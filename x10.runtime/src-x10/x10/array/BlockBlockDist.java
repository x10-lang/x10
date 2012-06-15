package x10.array;


@x10.core.X10Generated final public class BlockBlockDist extends x10.array.Dist implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, BlockBlockDist.class);
    
    public static final x10.rtt.RuntimeType<BlockBlockDist> $RTT = x10.rtt.NamedType.<BlockBlockDist> make(
    "x10.array.BlockBlockDist", /* base class */BlockBlockDist.class
    , /* parents */ new x10.rtt.Type[] {x10.array.Dist.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(BlockBlockDist $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + BlockBlockDist.class + " calling"); } 
        x10.array.Dist.$_deserialize_body($_obj, $deserializer);
        x10.array.PlaceGroup pg = (x10.array.PlaceGroup) $deserializer.readRef();
        $_obj.pg = pg;
        $_obj.axis0 = $deserializer.readInt();
        $_obj.axis1 = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        BlockBlockDist $_obj = new BlockBlockDist((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (pg instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.pg);
        } else {
        $serializer.write(this.pg);
        }
        $serializer.write(this.axis0);
        $serializer.write(this.axis1);
        
    }
    
    // constructor just for allocation
    public BlockBlockDist(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
/**
     * The place group for this distribution
     */
        public x10.array.PlaceGroup pg;
        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
/**
     * The first axis along which the region is distributed
     */
        public int axis0;
        
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
/**
     * The second axis along which the region is distributed
     */
        public int axis1;
        
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
/**
     * Cached restricted region for the current place.
     */
        public transient x10.array.Region regionForHere;
        
        
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
// creation method for java code (1-phase java constructor)
        public BlockBlockDist(final x10.array.Region r,
                              final int axis0,
                              final int axis1,
                              final x10.array.PlaceGroup pg){this((java.lang.System[]) null);
                                                                 $init(r,axis0,axis1,pg);}
        
        // constructor for non-virtual call
        final public x10.array.BlockBlockDist x10$array$BlockBlockDist$$init$S(final x10.array.Region r,
                                                                               final int axis0,
                                                                               final int axis1,
                                                                               final x10.array.PlaceGroup pg) { {
                                                                                                                       
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
super.$init(((x10.array.Region)(r)));
                                                                                                                       
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"

                                                                                                                       
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
this.__fieldInitializers34057();
                                                                                                                       
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
this.axis0 = axis0;
                                                                                                                       
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
this.axis1 = axis1;
                                                                                                                       
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
this.pg = ((x10.array.PlaceGroup)(pg));
                                                                                                                   }
                                                                                                                   return this;
                                                                                                                   }
        
        // constructor
        public x10.array.BlockBlockDist $init(final x10.array.Region r,
                                              final int axis0,
                                              final int axis1,
                                              final x10.array.PlaceGroup pg){return x10$array$BlockBlockDist$$init$S(r,axis0,axis1,pg);}
        
        
        
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
private x10.array.Region
                                                                                                        blockBlockRegionForPlace(
                                                                                                        final x10.lang.Place place){
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34537 =
              ((x10.array.Region)(region));
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region b =
              ((x10.array.Region)(t34537.boundingBox()));
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34538 =
              axis0;
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int min0 =
              b.min$O((int)(t34538));
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34539 =
              axis0;
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int max0 =
              b.max$O((int)(t34539));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34540 =
              axis1;
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int min1 =
              b.min$O((int)(t34540));
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34541 =
              axis1;
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int max1 =
              b.max$O((int)(t34541));
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34542 =
              ((max0) - (((int)(min0))));
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int size0 =
              ((t34542) + (((int)(1))));
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34543 =
              ((max1) - (((int)(min1))));
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int size1 =
              ((t34543) + (((int)(1))));
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34544 =
              ((size0) % (((int)(2))));
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34545 =
              ((int) t34544) ==
            ((int) 0);
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
int t34546 =
               0;
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34545) {
                
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34546 = size0;
            } else {
                
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34546 = ((size0) - (((int)(1))));
            }
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int size0Even =
              t34546;
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.PlaceGroup t34547 =
              ((x10.array.PlaceGroup)(pg));
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34548 =
              t34547.numPlaces$O();
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34549 =
              ((size0Even) * (((int)(size1))));
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int P =
              x10.lang.Math.min$O((int)(t34548),
                                  (int)(t34549));
            
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34550 =
              ((double)(int)(((int)(P))));
            
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34551 =
              java.lang.Math.log(((double)(t34550)));
            
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34552 =
              java.lang.Math.log(((double)(2.0)));
            
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34553 =
              ((t34551) / (((double)(t34552))));
            
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34554 =
              ((t34553) / (((double)(2.0))));
            
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34555 =
              java.lang.Math.ceil(((double)(t34554)));
            
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34556 =
              ((int)(double)(((double)(t34555))));
            
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34557 =
              x10.lang.Math.pow2$O((int)(t34556));
            
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int divisions0 =
              x10.lang.Math.min$O((int)(size0Even),
                                  (int)(t34557));
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34558 =
              ((double)(int)(((int)(P))));
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34559 =
              ((double)(int)(((int)(divisions0))));
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34560 =
              ((t34558) / (((double)(t34559))));
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34561 =
              java.lang.Math.ceil(((double)(t34560)));
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34562 =
              ((int)(double)(((double)(t34561))));
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int divisions1 =
              x10.lang.Math.min$O((int)(size1),
                                  (int)(t34562));
            
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34563 =
              ((divisions0) * (((int)(divisions1))));
            
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int leftOver =
              ((t34563) - (((int)(P))));
            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.PlaceGroup t34564 =
              ((x10.array.PlaceGroup)(pg));
            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int i =
              t34564.indexOf$O(((x10.lang.Place)(place)));
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34567 =
              ((i) >= (((int)(P))));
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34567) {
                
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34565 =
                  this.rank$O();
                
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34566 =
                  ((x10.array.Region)(x10.array.Region.makeEmpty((int)(t34565))));
                
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34566;
            }
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34568 =
              ((divisions0) % (((int)(2))));
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34571 =
              ((int) t34568) ==
            ((int) 0);
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
int t34572 =
               0;
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34571) {
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34572 = 0;
            } else {
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34569 =
                  ((i) * (((int)(2))));
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34570 =
                  ((divisions0) + (((int)(1))));
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34572 = ((t34569) / (((int)(t34570))));
            }
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int leftOverOddOffset =
              t34572;
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34576 =
              ((i) < (((int)(leftOver))));
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
int t34577 =
               0;
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34576) {
                
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34573 =
                  ((i) * (((int)(2))));
                
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34574 =
                  ((t34573) - (((int)(leftOverOddOffset))));
                
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34577 = ((t34574) % (((int)(divisions0))));
            } else {
                
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34575 =
                  ((i) + (((int)(leftOver))));
                
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34577 = ((t34575) % (((int)(divisions0))));
            }
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int blockIndex0 =
              t34577;
            
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34580 =
              ((i) < (((int)(leftOver))));
            
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
int t34581 =
               0;
            
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34580) {
                
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34578 =
                  ((i) * (((int)(2))));
                
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34581 = ((t34578) / (((int)(divisions0))));
            } else {
                
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34579 =
                  ((i) + (((int)(leftOver))));
                
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34581 = ((t34579) / (((int)(divisions0))));
            }
            
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int blockIndex1 =
              t34581;
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34582 =
              ((blockIndex0) * (((int)(size0))));
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34583 =
              ((double)(int)(((int)(t34582))));
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34584 =
              ((double)(int)(((int)(divisions0))));
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34585 =
              ((t34583) / (((double)(t34584))));
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34586 =
              java.lang.Math.ceil(((double)(t34585)));
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34587 =
              ((int)(double)(((double)(t34586))));
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int low0 =
              ((min0) + (((int)(t34587))));
            
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34588 =
              ((i) < (((int)(leftOver))));
            
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
int t34589 =
               0;
            
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34588) {
                
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34589 = 2;
            } else {
                
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34589 = 1;
            }
            
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34590 =
              t34589;
            
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int blockHi0 =
              ((blockIndex0) + (((int)(t34590))));
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34591 =
              ((blockHi0) * (((int)(size0))));
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34592 =
              ((double)(int)(((int)(t34591))));
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34593 =
              ((double)(int)(((int)(divisions0))));
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34594 =
              ((t34592) / (((double)(t34593))));
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34595 =
              java.lang.Math.ceil(((double)(t34594)));
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34596 =
              ((int)(double)(((double)(t34595))));
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34597 =
              ((min0) + (((int)(t34596))));
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int hi0 =
              ((t34597) - (((int)(1))));
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34598 =
              ((blockIndex1) * (((int)(size1))));
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34599 =
              ((double)(int)(((int)(t34598))));
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34600 =
              ((double)(int)(((int)(divisions1))));
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34601 =
              ((t34599) / (((double)(t34600))));
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34602 =
              java.lang.Math.ceil(((double)(t34601)));
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34603 =
              ((int)(double)(((double)(t34602))));
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int low1 =
              ((min1) + (((int)(t34603))));
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34604 =
              ((blockIndex1) + (((int)(1))));
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34605 =
              ((t34604) * (((int)(size1))));
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34606 =
              ((double)(int)(((int)(t34605))));
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34607 =
              ((double)(int)(((int)(divisions1))));
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34608 =
              ((t34606) / (((double)(t34607))));
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34609 =
              java.lang.Math.ceil(((double)(t34608)));
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34610 =
              ((int)(double)(((double)(t34609))));
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34611 =
              ((min1) + (((int)(t34610))));
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int hi1 =
              ((t34611) - (((int)(1))));
            
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34612 =
              ((x10.array.Region)(region));
            
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34686 =
              x10.array.RectRegion.$RTT.isInstance(t34612);
            
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34686) {
                
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34615 =
                  this.rank$O();
                
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t34616 =
                  ((x10.core.fun.Fun_0_1)(new x10.array.BlockBlockDist.$Closure$4(this,
                                                                                  region)));
                
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Array<x10.core.Int> newMin =
                  ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t34615)),
                                                                                                                           ((x10.core.fun.Fun_0_1)(t34616)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34619 =
                  this.rank$O();
                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t34620 =
                  ((x10.core.fun.Fun_0_1)(new x10.array.BlockBlockDist.$Closure$5(this,
                                                                                  region)));
                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Array<x10.core.Int> newMax =
                  ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t34619)),
                                                                                                                           ((x10.core.fun.Fun_0_1)(t34620)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34621 =
                  axis0;
                
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
((x10.array.Array<x10.core.Int>)newMin).$set__1x10$array$Array$$T$G((int)(t34621),
                                                                                                                                                                                  x10.core.Int.$box(low0));
                
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34622 =
                  axis1;
                
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
((x10.array.Array<x10.core.Int>)newMin).$set__1x10$array$Array$$T$G((int)(t34622),
                                                                                                                                                                                  x10.core.Int.$box(low1));
                
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34623 =
                  axis0;
                
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
((x10.array.Array<x10.core.Int>)newMax).$set__1x10$array$Array$$T$G((int)(t34623),
                                                                                                                                                                                   x10.core.Int.$box(hi0));
                
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34624 =
                  axis1;
                
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
((x10.array.Array<x10.core.Int>)newMax).$set__1x10$array$Array$$T$G((int)(t34624),
                                                                                                                                                                                   x10.core.Int.$box(hi1));
                
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.RectRegion t34625 =
                  ((x10.array.RectRegion)(new x10.array.RectRegion((java.lang.System[]) null).$init(((x10.array.Array)(newMin)),
                                                                                                    ((x10.array.Array)(newMax)), (x10.array.RectRegion.__0$1x10$lang$Int$2__1$1x10$lang$Int$2) null)));
                
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region __desugarer__var__4__34531 =
                  ((x10.array.Region)(((x10.array.Region)
                                        t34625)));
                
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
x10.array.Region ret34532 =
                   null;
                
//#line 102 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34844 =
                  __desugarer__var__4__34531.
                    rank;
                
//#line 102 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34845 =
                  ((x10.array.Region)(x10.array.BlockBlockDist.this.
                                        region));
                
//#line 102 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34846 =
                  t34845.
                    rank;
                
//#line 102 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34847 =
                  ((int) t34844) ==
                ((int) t34846);
                
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34848 =
                  !(t34847);
                
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34848) {
                    
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34849 =
                      true;
                    
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34849) {
                        
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.lang.FailedDynamicCheckException t34850 =
                          new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==this(:x10.array.BlockBlockDist).region.rank}");
                        
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
throw t34850;
                    }
                }
                
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
ret34532 = ((x10.array.Region)(__desugarer__var__4__34531));
                
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34633 =
                  ((x10.array.Region)(ret34532));
                
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34633;
            } else {
                
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34634 =
                  axis1;
                
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34635 =
                  axis0;
                
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34638 =
                  ((t34634) > (((int)(t34635))));
                
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
x10.array.Region t34639 =
                   null;
                
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34638) {
                    
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34636 =
                      axis0;
                    
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34639 = ((x10.array.Region)(x10.array.Region.makeFull((int)(t34636))));
                } else {
                    
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34637 =
                      axis1;
                    
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34639 = ((x10.array.Region)(x10.array.Region.makeFull((int)(t34637))));
                }
                
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region beforeAxes =
                  ((x10.array.Region)(t34639));
                
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34640 =
                  axis1;
                
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34641 =
                  axis0;
                
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34650 =
                  ((t34640) > (((int)(t34641))));
                
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
x10.array.Region t34651 =
                   null;
                
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34650) {
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34642 =
                      axis1;
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34643 =
                      axis0;
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34644 =
                      ((t34642) - (((int)(t34643))));
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34645 =
                      ((t34644) - (((int)(1))));
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34651 = ((x10.array.Region)(x10.array.Region.makeFull((int)(t34645))));
                } else {
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34646 =
                      axis0;
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34647 =
                      axis1;
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34648 =
                      ((t34646) - (((int)(t34647))));
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34649 =
                      ((t34648) - (((int)(1))));
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34651 = ((x10.array.Region)(x10.array.Region.makeFull((int)(t34649))));
                }
                
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region betweenAxes =
                  ((x10.array.Region)(t34651));
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34652 =
                  axis1;
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34653 =
                  axis0;
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34664 =
                  ((t34652) > (((int)(t34653))));
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
x10.array.Region t34665 =
                   null;
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34664) {
                    
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34654 =
                      ((x10.array.Region)(region));
                    
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34655 =
                      t34654.
                        rank;
                    
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34656 =
                      axis1;
                    
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34657 =
                      ((t34655) - (((int)(t34656))));
                    
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34658 =
                      ((t34657) - (((int)(1))));
                    
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34665 = ((x10.array.Region)(x10.array.Region.makeFull((int)(t34658))));
                } else {
                    
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34659 =
                      ((x10.array.Region)(region));
                    
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34660 =
                      t34659.
                        rank;
                    
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34661 =
                      axis0;
                    
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34662 =
                      ((t34660) - (((int)(t34661))));
                    
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34663 =
                      ((t34662) - (((int)(1))));
                    
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34665 = ((x10.array.Region)(x10.array.Region.makeFull((int)(t34663))));
                }
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region afterAxes =
                  ((x10.array.Region)(t34665));
                
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
int lowFirst =
                   0;
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int hiFirst;
                
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int lowSecond;
                
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int hiSecond;
                
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34666 =
                  axis1;
                
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34667 =
                  axis0;
                
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34668 =
                  ((t34666) > (((int)(t34667))));
                
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34668) {
                    
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
lowFirst = low0;
                    
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
lowSecond = low1;
                    
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
hiFirst = hi0;
                    
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
hiSecond = hi1;
                } else {
                    
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
lowFirst = low1;
                    
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
lowSecond = low0;
                    
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
hiFirst = hi1;
                    
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
hiSecond = hi0;
                }
                
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34669 =
                  lowFirst;
                
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.lang.IntRange rFirst =
                  ((x10.lang.IntRange)(new x10.lang.IntRange((java.lang.System[]) null).x10$lang$IntRange$$init$S(((int)(t34669)), ((int)(hiFirst)))));
                
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.lang.IntRange rSecond =
                  ((x10.lang.IntRange)(new x10.lang.IntRange((java.lang.System[]) null).x10$lang$IntRange$$init$S(((int)(lowSecond)), ((int)(hiSecond)))));
                
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34670 =
                  ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(rFirst)))));
                
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34671 =
                  ((x10.array.Region)(beforeAxes.product(((x10.array.Region)(t34670)))));
                
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34672 =
                  ((x10.array.Region)(t34671.product(((x10.array.Region)(betweenAxes)))));
                
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34673 =
                  ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(rSecond)))));
                
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34674 =
                  ((x10.array.Region)(t34672.product(((x10.array.Region)(t34673)))));
                
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34675 =
                  ((x10.array.Region)(t34674.product(((x10.array.Region)(afterAxes)))));
                
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region __desugarer__var__5__34534 =
                  ((x10.array.Region)(((x10.array.Region)
                                        t34675)));
                
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
x10.array.Region ret34535 =
                   null;
                
//#line 126 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34851 =
                  __desugarer__var__5__34534.
                    rank;
                
//#line 126 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34852 =
                  ((x10.array.Region)(x10.array.BlockBlockDist.this.
                                        region));
                
//#line 126 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34853 =
                  t34852.
                    rank;
                
//#line 126 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34854 =
                  ((int) t34851) ==
                ((int) t34853);
                
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34855 =
                  !(t34854);
                
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34855) {
                    
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34856 =
                      true;
                    
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34856) {
                        
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.lang.FailedDynamicCheckException t34857 =
                          new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==this(:x10.array.BlockBlockDist).region.rank}");
                        
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
throw t34857;
                    }
                }
                
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
ret34535 = ((x10.array.Region)(__desugarer__var__5__34534));
                
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34683 =
                  ((x10.array.Region)(ret34535));
                
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34684 =
                  ((x10.array.Region)(region));
                
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34685 =
                  ((x10.array.Region)(t34683.intersection(((x10.array.Region)(t34684)))));
                
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34685;
            }
        }
        
        public static x10.array.Region
          blockBlockRegionForPlace$P(
          final x10.lang.Place place,
          final x10.array.BlockBlockDist BlockBlockDist){
            return BlockBlockDist.blockBlockRegionForPlace(((x10.lang.Place)(place)));
        }
        
        
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
private x10.lang.Place
                                                                                                         mapIndexToPlace(
                                                                                                         final int index0,
                                                                                                         final int index1){
            
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34687 =
              ((x10.array.Region)(region));
            
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region b =
              ((x10.array.Region)(t34687.boundingBox()));
            
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34688 =
              axis0;
            
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int min0 =
              b.min$O((int)(t34688));
            
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34689 =
              axis0;
            
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int max0 =
              b.max$O((int)(t34689));
            
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34690 =
              axis1;
            
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int min1 =
              b.min$O((int)(t34690));
            
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34691 =
              axis1;
            
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int max1 =
              b.max$O((int)(t34691));
            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34692 =
              ((max0) - (((int)(min0))));
            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int size0 =
              ((t34692) + (((int)(1))));
            
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34693 =
              ((max1) - (((int)(min1))));
            
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int size1 =
              ((t34693) + (((int)(1))));
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34694 =
              ((size0) % (((int)(2))));
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34695 =
              ((int) t34694) ==
            ((int) 0);
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
int t34696 =
               0;
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34695) {
                
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34696 = size0;
            } else {
                
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34696 = ((size0) - (((int)(1))));
            }
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int size0Even =
              t34696;
            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.PlaceGroup t34697 =
              ((x10.array.PlaceGroup)(pg));
            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34698 =
              t34697.numPlaces$O();
            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34699 =
              ((size0Even) * (((int)(size1))));
            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int P =
              x10.lang.Math.min$O((int)(t34698),
                                  (int)(t34699));
            
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34700 =
              ((double)(int)(((int)(P))));
            
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34701 =
              java.lang.Math.log(((double)(t34700)));
            
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34702 =
              java.lang.Math.log(((double)(2.0)));
            
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34703 =
              ((t34701) / (((double)(t34702))));
            
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34704 =
              ((t34703) / (((double)(2.0))));
            
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34705 =
              java.lang.Math.ceil(((double)(t34704)));
            
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34706 =
              ((int)(double)(((double)(t34705))));
            
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34707 =
              x10.lang.Math.pow2$O((int)(t34706));
            
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int divisions0 =
              x10.lang.Math.min$O((int)(size0Even),
                                  (int)(t34707));
            
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34708 =
              ((double)(int)(((int)(P))));
            
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34709 =
              ((double)(int)(((int)(divisions0))));
            
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34710 =
              ((t34708) / (((double)(t34709))));
            
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final double t34711 =
              java.lang.Math.ceil(((double)(t34710)));
            
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34712 =
              ((int)(double)(((double)(t34711))));
            
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int divisions1 =
              x10.lang.Math.min$O((int)(size1),
                                  (int)(t34712));
            
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int numBlocks =
              ((divisions0) * (((int)(divisions1))));
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int leftOver =
              ((numBlocks) - (((int)(P))));
            
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34715 =
              ((int) divisions0) ==
            ((int) 1);
            
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
int t34716 =
               0;
            
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34715) {
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34716 = 0;
            } else {
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34713 =
                  ((index0) - (((int)(min0))));
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34714 =
                  ((t34713) * (((int)(divisions0))));
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34716 = ((t34714) / (((int)(size0))));
            }
            
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int blockIndex0 =
              t34716;
            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34719 =
              ((int) divisions1) ==
            ((int) 1);
            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
int t34720 =
               0;
            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34719) {
                
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34720 = 0;
            } else {
                
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34717 =
                  ((index1) - (((int)(min1))));
                
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34718 =
                  ((t34717) * (((int)(divisions1))));
                
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34720 = ((t34718) / (((int)(size1))));
            }
            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int blockIndex1 =
              t34720;
            
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34721 =
              ((blockIndex1) * (((int)(divisions0))));
            
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int blockIndex =
              ((t34721) + (((int)(blockIndex0))));
            
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34722 =
              ((leftOver) * (((int)(2))));
            
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34730 =
              ((blockIndex) <= (((int)(t34722))));
            
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34730) {
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.PlaceGroup t34724 =
                  ((x10.array.PlaceGroup)(pg));
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34723 =
                  ((blockIndex) / (((int)(2))));
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34725 =
                  t34723;
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.lang.Place t34726 =
                  t34724.$apply((int)(t34725));
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34726;
            } else {
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.PlaceGroup t34727 =
                  ((x10.array.PlaceGroup)(pg));
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34728 =
                  ((blockIndex) - (((int)(leftOver))));
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.lang.Place t34729 =
                  t34727.$apply((int)(t34728));
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34729;
            }
        }
        
        public static x10.lang.Place
          mapIndexToPlace$P(
          final int index0,
          final int index1,
          final x10.array.BlockBlockDist BlockBlockDist){
            return BlockBlockDist.mapIndexToPlace((int)(index0),
                                                  (int)(index1));
        }
        
        
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
public x10.array.PlaceGroup
                                                                                                         places(
                                                                                                         ){
            
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.PlaceGroup t34731 =
              ((x10.array.PlaceGroup)(pg));
            
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34731;
        }
        
        
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
public int
                                                                                                         numPlaces$O(
                                                                                                         ){
            
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.PlaceGroup t34732 =
              ((x10.array.PlaceGroup)(pg));
            
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34733 =
              t34732.numPlaces$O();
            
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34733;
        }
        
        
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
public x10.lang.Sequence<x10.array.Region>
                                                                                                         regions(
                                                                                                         ){
            
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.PlaceGroup t34734 =
              ((x10.array.PlaceGroup)(pg));
            
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34738 =
              t34734.numPlaces$O();
            
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.array.Region> t34739 =
              ((x10.core.fun.Fun_0_1)(new x10.array.BlockBlockDist.$Closure$6(this,
                                                                              pg)));
            
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Array<x10.array.Region> t34740 =
              ((x10.array.Array)(new x10.array.Array<x10.array.Region>((java.lang.System[]) null, x10.array.Region.$RTT).$init(t34738,
                                                                                                                               ((x10.core.fun.Fun_0_1)(t34739)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
            
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.lang.Sequence<x10.array.Region> t34741 =
              ((x10.lang.Sequence<x10.array.Region>)
                ((x10.array.Array<x10.array.Region>)t34740).sequence());
            
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34741;
        }
        
        
//#line 170 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
public x10.array.Region
                                                                                                         get(
                                                                                                         final x10.lang.Place p){
            
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34747 =
              x10.rtt.Equality.equalsequals((p),(x10.lang.Runtime.home()));
            
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34747) {
                
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34742 =
                  ((x10.array.Region)(regionForHere));
                
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34744 =
                  ((t34742) == (null));
                
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34744) {
                    
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34743 =
                      ((x10.array.Region)(this.blockBlockRegionForPlace(((x10.lang.Place)(x10.lang.Runtime.home())))));
                    
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
this.regionForHere = ((x10.array.Region)(t34743));
                }
                
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34745 =
                  ((x10.array.Region)(regionForHere));
                
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34745;
            } else {
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34746 =
                  ((x10.array.Region)(this.blockBlockRegionForPlace(((x10.lang.Place)(p)))));
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34746;
            }
        }
        
        
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
public boolean
                                                                                                         containsLocally$O(
                                                                                                         final x10.array.Point p){
            
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34748 =
              ((x10.array.Region)(this.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
            
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34749 =
              t34748.contains$O(((x10.array.Point)(p)));
            
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34749;
        }
        
        
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
public x10.array.Region
                                                                                                         $apply(
                                                                                                         final x10.lang.Place p){
            
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34750 =
              ((x10.array.Region)(this.get(((x10.lang.Place)(p)))));
            
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34750;
        }
        
        
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
public x10.lang.Place
                                                                                                         $apply(
                                                                                                         final x10.array.Point pt){
            
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
boolean t34753 =
              true;
            
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34753) {
                
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34751 =
                  ((x10.array.Region)(region));
                
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34752 =
                  t34751.contains$O(((x10.array.Point)(pt)));
                
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34753 = !(t34752);
            }
            
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34754 =
              t34753;
            
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34754) {
                
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
x10.array.Dist.raiseBoundsError(((x10.array.Point)(pt)));
            }
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34755 =
              axis0;
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34757 =
              pt.$apply$O((int)(t34755));
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34756 =
              axis1;
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34758 =
              pt.$apply$O((int)(t34756));
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.lang.Place t34759 =
              this.mapIndexToPlace((int)(t34757),
                                   (int)(t34758));
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34759;
        }
        
        
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
public x10.lang.Place
                                                                                                         $apply(
                                                                                                         final int i0){
            
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.lang.UnsupportedOperationException t34761 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("operator(i0:int)")))));
            
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
throw t34761;
        }
        
        
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
public x10.lang.Place
                                                                                                         $apply(
                                                                                                         final int i0,
                                                                                                         final int i1){
            
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
boolean t34765 =
              true;
            
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34765) {
                
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34763 =
                  ((x10.array.Region)(region));
                
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34764 =
                  t34763.contains$O((int)(i0),
                                    (int)(i1));
                
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34765 = !(t34764);
            }
            
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34766 =
              t34765;
            
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34766) {
                
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0),
                                                                                                                                               (int)(i1));
            }
            
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34769 =
              axis0;
            
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
switch (t34769) {
                
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
case 0:
                    
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.lang.Place t34767 =
                      this.mapIndexToPlace((int)(i0),
                                           (int)(i1));
                    
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34767;
                
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
case 1:
                    
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.lang.Place t34768 =
                      this.mapIndexToPlace((int)(i1),
                                           (int)(i0));
                    
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34768;
                
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
default:
                    
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return x10.lang.Runtime.home();
            }
        }
        
        
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
public x10.lang.Place
                                                                                                         $apply(
                                                                                                         final int i0,
                                                                                                         final int i1,
                                                                                                         final int i2){
            
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
boolean t34773 =
              true;
            
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34773) {
                
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34771 =
                  ((x10.array.Region)(region));
                
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34772 =
                  t34771.contains$O((int)(i0),
                                    (int)(i1),
                                    (int)(i2));
                
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34773 = !(t34772);
            }
            
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34774 =
              t34773;
            
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34774) {
                
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0),
                                                                                                                                               (int)(i1),
                                                                                                                                               (int)(i2));
            }
            
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34784 =
              axis0;
            
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
switch (t34784) {
                
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
case 0:
                    
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34777 =
                      axis1;
                    
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
switch (t34777) {
                        
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
case 1:
                            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.lang.Place t34775 =
                              this.mapIndexToPlace((int)(i0),
                                                   (int)(i1));
                            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34775;
                        
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
case 2:
                            
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.lang.Place t34776 =
                              this.mapIndexToPlace((int)(i0),
                                                   (int)(i2));
                            
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34776;
                        
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
default:
                            
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return x10.lang.Runtime.home();
                    }
                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
case 1:
                    
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34780 =
                      axis1;
                    
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
switch (t34780) {
                        
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
case 0:
                            
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.lang.Place t34778 =
                              this.mapIndexToPlace((int)(i1),
                                                   (int)(i0));
                            
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34778;
                        
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
case 2:
                            
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.lang.Place t34779 =
                              this.mapIndexToPlace((int)(i1),
                                                   (int)(i2));
                            
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34779;
                        
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
default:
                            
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return x10.lang.Runtime.home();
                    }
                
//#line 222 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
case 2:
                    
//#line 222 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34783 =
                      axis1;
                    
//#line 222 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
switch (t34783) {
                        
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
case 0:
                            
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.lang.Place t34781 =
                              this.mapIndexToPlace((int)(i2),
                                                   (int)(i0));
                            
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34781;
                        
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
case 1:
                            
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.lang.Place t34782 =
                              this.mapIndexToPlace((int)(i2),
                                                   (int)(i1));
                            
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34782;
                        
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
default:
                            
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return x10.lang.Runtime.home();
                    }
                
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
default:
                    
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return x10.lang.Runtime.home();
            }
        }
        
        
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
public x10.lang.Place
                                                                                                         $apply(
                                                                                                         final int i0,
                                                                                                         final int i1,
                                                                                                         final int i2,
                                                                                                         final int i3){
            
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Point pt =
              ((x10.array.Point)(x10.array.Point.make((int)(i0),
                                                      (int)(i1),
                                                      (int)(i2),
                                                      (int)(i3))));
            
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
boolean t34788 =
              true;
            
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34788) {
                
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34786 =
                  ((x10.array.Region)(region));
                
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34787 =
                  t34786.contains$O(((x10.array.Point)(pt)));
                
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34788 = !(t34787);
            }
            
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34789 =
              t34788;
            
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34789) {
                
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
x10.array.Dist.raiseBoundsError(((x10.array.Point)(pt)));
            }
            
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34790 =
              axis0;
            
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34792 =
              pt.$apply$O((int)(t34790));
            
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34791 =
              axis1;
            
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34793 =
              pt.$apply$O((int)(t34791));
            
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.lang.Place t34794 =
              this.mapIndexToPlace((int)(t34792),
                                   (int)(t34793));
            
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34794;
        }
        
        
//#line 239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
public int
                                                                                                         offset$O(
                                                                                                         final x10.array.Point pt){
            
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region r =
              ((x10.array.Region)(this.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
            
//#line 241 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int offset =
              r.indexOf$O(((x10.array.Point)(pt)));
            
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34800 =
              ((int) offset) ==
            ((int) -1);
            
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34800) {
                
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
boolean t34797 =
                  true;
                
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34797) {
                    
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34795 =
                      ((x10.array.Region)(region));
                    
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34796 =
                      t34795.contains$O(((x10.array.Point)(pt)));
                    
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34797 = !(t34796);
                }
                
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34798 =
                  t34797;
                
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34798) {
                    
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
x10.array.Dist.raiseBoundsError(((x10.array.Point)(pt)));
                }
                
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34799 =
                  true;
                
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34799) {
                    
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
x10.array.Dist.raisePlaceError(((x10.array.Point)(pt)));
                }
            }
            
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return offset;
        }
        
        
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
public int
                                                                                                         offset$O(
                                                                                                         final int i0){
            
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region r =
              ((x10.array.Region)(this.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
            
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int offset =
              r.indexOf$O((int)(i0));
            
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34807 =
              ((int) offset) ==
            ((int) -1);
            
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34807) {
                
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
boolean t34804 =
                  true;
                
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34804) {
                    
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34802 =
                      ((x10.array.Region)(region));
                    
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34803 =
                      t34802.contains$O((int)(i0));
                    
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34804 = !(t34803);
                }
                
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34805 =
                  t34804;
                
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34805) {
                    
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0));
                }
                
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34806 =
                  true;
                
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34806) {
                    
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
x10.array.Dist.raisePlaceError((int)(i0));
                }
            }
            
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return offset;
        }
        
        
//#line 259 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
public int
                                                                                                         offset$O(
                                                                                                         final int i0,
                                                                                                         final int i1){
            
//#line 260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region r =
              ((x10.array.Region)(this.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
            
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int offset =
              r.indexOf$O((int)(i0),
                          (int)(i1));
            
//#line 262 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34814 =
              ((int) offset) ==
            ((int) -1);
            
//#line 262 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34814) {
                
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
boolean t34811 =
                  true;
                
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34811) {
                    
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34809 =
                      ((x10.array.Region)(region));
                    
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34810 =
                      t34809.contains$O((int)(i0),
                                        (int)(i1));
                    
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34811 = !(t34810);
                }
                
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34812 =
                  t34811;
                
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34812) {
                    
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0),
                                                                                                                                                   (int)(i1));
                }
                
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34813 =
                  true;
                
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34813) {
                    
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
x10.array.Dist.raisePlaceError((int)(i0),
                                                                                                                                                  (int)(i1));
                }
            }
            
//#line 266 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return offset;
        }
        
        
//#line 269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
public int
                                                                                                         offset$O(
                                                                                                         final int i0,
                                                                                                         final int i1,
                                                                                                         final int i2){
            
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region r =
              ((x10.array.Region)(this.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
            
//#line 271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int offset =
              r.indexOf$O((int)(i0),
                          (int)(i1),
                          (int)(i2));
            
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34821 =
              ((int) offset) ==
            ((int) -1);
            
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34821) {
                
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
boolean t34818 =
                  true;
                
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34818) {
                    
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34816 =
                      ((x10.array.Region)(region));
                    
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34817 =
                      t34816.contains$O((int)(i0),
                                        (int)(i1),
                                        (int)(i2));
                    
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34818 = !(t34817);
                }
                
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34819 =
                  t34818;
                
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34819) {
                    
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0),
                                                                                                                                                   (int)(i1),
                                                                                                                                                   (int)(i2));
                }
                
//#line 274 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34820 =
                  true;
                
//#line 274 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34820) {
                    
//#line 274 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
x10.array.Dist.raisePlaceError((int)(i0),
                                                                                                                                                  (int)(i1),
                                                                                                                                                  (int)(i2));
                }
            }
            
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return offset;
        }
        
        
//#line 279 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
public int
                                                                                                         offset$O(
                                                                                                         final int i0,
                                                                                                         final int i1,
                                                                                                         final int i2,
                                                                                                         final int i3){
            
//#line 280 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region r =
              ((x10.array.Region)(this.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
            
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int offset =
              r.indexOf$O((int)(i0),
                          (int)(i1),
                          (int)(i2),
                          (int)(i3));
            
//#line 282 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34828 =
              ((int) offset) ==
            ((int) -1);
            
//#line 282 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34828) {
                
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
boolean t34825 =
                  true;
                
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34825) {
                    
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34823 =
                      ((x10.array.Region)(region));
                    
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34824 =
                      t34823.contains$O((int)(i0),
                                        (int)(i1),
                                        (int)(i2),
                                        (int)(i3));
                    
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34825 = !(t34824);
                }
                
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34826 =
                  t34825;
                
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34826) {
                    
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0),
                                                                                                                                                   (int)(i1),
                                                                                                                                                   (int)(i2),
                                                                                                                                                   (int)(i3));
                }
                
//#line 284 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34827 =
                  true;
                
//#line 284 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34827) {
                    
//#line 284 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
x10.array.Dist.raisePlaceError((int)(i0),
                                                                                                                                                  (int)(i1),
                                                                                                                                                  (int)(i2),
                                                                                                                                                  (int)(i3));
                }
            }
            
//#line 286 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return offset;
        }
        
        
//#line 289 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
public int
                                                                                                         maxOffset$O(
                                                                                                         ){
            
//#line 290 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region r =
              ((x10.array.Region)(this.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
            
//#line 291 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34829 =
              r.size$O();
            
//#line 291 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34830 =
              ((t34829) - (((int)(1))));
            
//#line 291 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34830;
        }
        
        
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
public x10.array.Dist
                                                                                                         restriction(
                                                                                                         final x10.array.Region r){
            
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.WrappedDistRegionRestricted t34831 =
              ((x10.array.WrappedDistRegionRestricted)(new x10.array.WrappedDistRegionRestricted((java.lang.System[]) null).$init(((x10.array.Dist)(this)),
                                                                                                                                  ((x10.array.Region)(r)))));
            
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34831;
        }
        
        
//#line 298 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
public x10.array.Dist
                                                                                                         restriction(
                                                                                                         final x10.lang.Place p){
            
//#line 299 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.WrappedDistPlaceRestricted t34832 =
              ((x10.array.WrappedDistPlaceRestricted)(new x10.array.WrappedDistPlaceRestricted((java.lang.System[]) null).$init(((x10.array.Dist)(this)),
                                                                                                                                ((x10.lang.Place)(p)))));
            
//#line 299 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34832;
        }
        
        
//#line 303 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
public boolean
                                                                                                         equals(
                                                                                                         final java.lang.Object thatObj){
            
//#line 304 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34833 =
              x10.array.BlockBlockDist.$RTT.isInstance(thatObj);
            
//#line 304 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34834 =
              !(t34833);
            
//#line 304 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34834) {
                
//#line 304 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return false;
            }
            
//#line 305 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.BlockBlockDist that =
              ((x10.array.BlockBlockDist)(x10.rtt.Types.<x10.array.BlockBlockDist> cast(thatObj,x10.array.BlockBlockDist.$RTT)));
            
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34835 =
              this.
                axis0;
            
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34836 =
              that.
                axis0;
            
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
boolean t34839 =
              x10.rtt.Equality.equalsequals(t34835, ((int)(t34836)));
            
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34839) {
                
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34837 =
                  this.
                    axis1;
                
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34838 =
                  that.
                    axis1;
                
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34839 = x10.rtt.Equality.equalsequals(t34837, ((int)(t34838)));
            }
            
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
boolean t34842 =
              t34839;
            
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
if (t34842) {
                
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34840 =
                  ((x10.array.Region)(this.
                                        region));
                
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34841 =
                  ((x10.array.Region)(that.
                                        region));
                
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
t34842 = t34840.equals(((java.lang.Object)(t34841)));
            }
            
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final boolean t34843 =
              t34842;
            
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34843;
        }
        
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final public x10.array.BlockBlockDist
                                                                                                        x10$array$BlockBlockDist$$x10$array$BlockBlockDist$this(
                                                                                                        ){
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return x10.array.BlockBlockDist.this;
        }
        
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final public void
                                                                                                        __fieldInitializers34057(
                                                                                                        ){
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
this.regionForHere = null;
        }
        
        @x10.core.X10Generated public static class $Closure$4 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$4.class);
            
            public static final x10.rtt.RuntimeType<$Closure$4> $RTT = x10.rtt.StaticFunType.<$Closure$4> make(
            /* base class */$Closure$4.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$4 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$4.class + " calling"); } 
                x10.array.BlockBlockDist out$$ = (x10.array.BlockBlockDist) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Region region = (x10.array.Region) $deserializer.readRef();
                $_obj.region = region;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$4 $_obj = new $Closure$4((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                } else {
                $serializer.write(this.out$$);
                }
                if (region instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.region);
                } else {
                $serializer.write(this.region);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$4(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34613 =
                      ((x10.array.Region)(this.
                                            region));
                    
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34614 =
                      t34613.min$O((int)(i));
                    
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34614;
                }
                
                public x10.array.BlockBlockDist out$$;
                public x10.array.Region region;
                
                public $Closure$4(final x10.array.BlockBlockDist out$$,
                                  final x10.array.Region region) { {
                                                                          this.out$$ = out$$;
                                                                          this.region = ((x10.array.Region)(region));
                                                                      }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$5 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$5.class);
            
            public static final x10.rtt.RuntimeType<$Closure$5> $RTT = x10.rtt.StaticFunType.<$Closure$5> make(
            /* base class */$Closure$5.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$5 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$5.class + " calling"); } 
                x10.array.BlockBlockDist out$$ = (x10.array.BlockBlockDist) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Region region = (x10.array.Region) $deserializer.readRef();
                $_obj.region = region;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$5 $_obj = new $Closure$5((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                } else {
                $serializer.write(this.out$$);
                }
                if (region instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.region);
                } else {
                $serializer.write(this.region);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$5(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34617 =
                      ((x10.array.Region)(this.
                                            region));
                    
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final int t34618 =
                      t34617.max$O((int)(i));
                    
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34618;
                }
                
                public x10.array.BlockBlockDist out$$;
                public x10.array.Region region;
                
                public $Closure$5(final x10.array.BlockBlockDist out$$,
                                  final x10.array.Region region) { {
                                                                          this.out$$ = out$$;
                                                                          this.region = ((x10.array.Region)(region));
                                                                      }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$6 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$6.class);
            
            public static final x10.rtt.RuntimeType<$Closure$6> $RTT = x10.rtt.StaticFunType.<$Closure$6> make(
            /* base class */$Closure$6.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.array.Region.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$6 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$6.class + " calling"); } 
                x10.array.BlockBlockDist out$$ = (x10.array.BlockBlockDist) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.PlaceGroup pg = (x10.array.PlaceGroup) $deserializer.readRef();
                $_obj.pg = pg;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$6 $_obj = new $Closure$6((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                } else {
                $serializer.write(this.out$$);
                }
                if (pg instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.pg);
                } else {
                $serializer.write(this.pg);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$6(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply(x10.core.Int.$unbox(a1));
            }
            
                
                public x10.array.Region
                  $apply(
                  final int i){
                    
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.PlaceGroup t34735 =
                      ((x10.array.PlaceGroup)(this.
                                                pg));
                    
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.lang.Place t34736 =
                      t34735.$apply((int)(i));
                    
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
final x10.array.Region t34737 =
                      ((x10.array.Region)(this.
                                            out$$.blockBlockRegionForPlace(((x10.lang.Place)(t34736)))));
                    
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockBlockDist.x10"
return t34737;
                }
                
                public x10.array.BlockBlockDist out$$;
                public x10.array.PlaceGroup pg;
                
                public $Closure$6(final x10.array.BlockBlockDist out$$,
                                  final x10.array.PlaceGroup pg) { {
                                                                          this.out$$ = out$$;
                                                                          this.pg = ((x10.array.PlaceGroup)(pg));
                                                                      }}
                
            }
            
        
        }
        