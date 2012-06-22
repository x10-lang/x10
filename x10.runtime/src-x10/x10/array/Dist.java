package x10.array;


@x10.core.X10Generated abstract public class Dist extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.lang.Iterable, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Dist.class);
    
    public static final x10.rtt.RuntimeType<Dist> $RTT = x10.rtt.NamedType.<Dist> make(
    "x10.array.Dist", /* base class */Dist.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.array.Point.$RTT, x10.lang.Place.$RTT), x10.rtt.ParameterizedType.make(x10.lang.Iterable.$RTT, x10.array.Point.$RTT), x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Dist $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Dist.class + " calling"); } 
        x10.array.Region region = (x10.array.Region) $deserializer.readRef();
        $_obj.region = region;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        return null;
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (region instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.region);
        } else {
        $serializer.write(this.region);
        }
        
    }
    
    // constructor just for allocation
    public Dist(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1){}:U
    public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
    return $apply((x10.array.Point)a1);
    }
    
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public x10.array.Region region;
        
        
        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final public int
                                                                                              rank$O(
                                                                                              ){
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Region t36592 =
              ((x10.array.Region)(region));
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final int t36593 =
              t36592.
                rank;
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36593;
        }
        
        
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static x10.array.Dist
                                                                                              makeUnique(
                                                                                              ){
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.UniqueDist t36594 =
              ((x10.array.UniqueDist)(x10.array.Dist.getInitialized$UNIQUE()));
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Dist __desugarer__var__8__36586 =
              ((x10.array.Dist)(((x10.array.Dist)
                                  t36594)));
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
x10.array.Dist ret36587 =
               null;
            
//#line 52 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Region t36760 =
              ((x10.array.Region)(__desugarer__var__8__36586.
                                    region));
            
//#line 52 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final int t36761 =
              t36760.
                rank;
            
//#line 52 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36762 =
              ((int) t36761) ==
            ((int) 1);
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36763 =
              !(t36762);
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
if (t36763) {
                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36764 =
                  true;
                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
if (t36764) {
                    
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.FailedDynamicCheckException t36765 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Dist{self.region.rank==1}");
                    
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
throw t36765;
                }
            }
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
ret36587 = ((x10.array.Dist)(__desugarer__var__8__36586));
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Dist t36601 =
              ((x10.array.Dist)(ret36587));
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36601;
        }
        
        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static x10.array.UniqueDist UNIQUE;
        
        
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static x10.array.Dist
                                                                                              makeConstant(
                                                                                              final x10.array.Region r){
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.ConstantDist t36602 =
              ((x10.array.ConstantDist)(new x10.array.ConstantDist((java.lang.System[]) null).$init(((x10.array.Region)(r)),
                                                                                                    x10.lang.Runtime.home())));
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36602;
        }
        
        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static x10.array.Dist
                                                                                              make(
                                                                                              final x10.array.Region r){
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Dist t36603 =
              ((x10.array.Dist)(x10.array.Dist.makeConstant(((x10.array.Region)(r)))));
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36603;
        }
        
        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static x10.array.Dist
                                                                                              makeCyclic(
                                                                                              final x10.array.Region r,
                                                                                              final int axis){
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Dist t36604 =
              ((x10.array.Dist)(x10.array.Dist.makeBlockCyclic(((x10.array.Region)(r)),
                                                               (int)(axis),
                                                               (int)(1))));
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36604;
        }
        
        
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static x10.array.Dist
                                                                                               makeBlock(
                                                                                               final x10.array.Region r,
                                                                                               final int axis){
            
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.PlaceGroup.WorldPlaceGroup t36605 =
              ((x10.array.PlaceGroup.WorldPlaceGroup)(x10.array.PlaceGroup.getInitialized$WORLD()));
            
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Dist t36606 =
              ((x10.array.Dist)(x10.array.Dist.makeBlock(((x10.array.Region)(r)),
                                                         (int)(axis),
                                                         ((x10.array.PlaceGroup)(t36605)))));
            
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36606;
        }
        
        
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static x10.array.Dist
                                                                                               makeBlockBlock(
                                                                                               final x10.array.Region r,
                                                                                               final int axis0,
                                                                                               final int axis1){
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.PlaceGroup.WorldPlaceGroup t36607 =
              ((x10.array.PlaceGroup.WorldPlaceGroup)(x10.array.PlaceGroup.getInitialized$WORLD()));
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.BlockBlockDist t36608 =
              ((x10.array.BlockBlockDist)(new x10.array.BlockBlockDist((java.lang.System[]) null).$init(((x10.array.Region)(r)),
                                                                                                        ((int)(axis0)),
                                                                                                        ((int)(axis1)),
                                                                                                        ((x10.array.PlaceGroup)(t36607)))));
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36608;
        }
        
        
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static x10.array.Dist
                                                                                               makeBlock(
                                                                                               final x10.array.Region r){
            
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.PlaceGroup.WorldPlaceGroup t36609 =
              ((x10.array.PlaceGroup.WorldPlaceGroup)(x10.array.PlaceGroup.getInitialized$WORLD()));
            
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Dist t36610 =
              ((x10.array.Dist)(x10.array.Dist.makeBlock(((x10.array.Region)(r)),
                                                         (int)(0),
                                                         ((x10.array.PlaceGroup)(t36609)))));
            
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36610;
        }
        
        
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static x10.array.Dist
                                                                                               makeBlockCyclic(
                                                                                               final x10.array.Region r,
                                                                                               final int axis,
                                                                                               final int blockSize){
            
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.UnsupportedOperationException t36611 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
            
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
throw t36611;
        }
        
        
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static x10.array.Dist
                                                                                               makeUnique(
                                                                                               final x10.array.PlaceGroup pg){
            
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.PlaceGroup.WorldPlaceGroup t36612 =
              ((x10.array.PlaceGroup.WorldPlaceGroup)(x10.array.PlaceGroup.getInitialized$WORLD()));
            
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36622 =
              pg.equals(((java.lang.Object)(t36612)));
            
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
if (t36622) {
                
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Dist t36613 =
                  ((x10.array.Dist)(x10.array.Dist.makeUnique()));
                
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36613;
            } else {
                
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.UniqueDist t36614 =
                  ((x10.array.UniqueDist)(new x10.array.UniqueDist((java.lang.System[]) null).$init(((x10.array.PlaceGroup)(pg)))));
                
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Dist __desugarer__var__9__36589 =
                  ((x10.array.Dist)(((x10.array.Dist)
                                      t36614)));
                
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
x10.array.Dist ret36590 =
                   null;
                
//#line 176 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Region t36766 =
                  ((x10.array.Region)(__desugarer__var__9__36589.
                                        region));
                
//#line 176 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final int t36767 =
                  t36766.
                    rank;
                
//#line 176 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36768 =
                  ((int) t36767) ==
                ((int) 1);
                
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36769 =
                  !(t36768);
                
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
if (t36769) {
                    
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36770 =
                      true;
                    
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
if (t36770) {
                        
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.FailedDynamicCheckException t36771 =
                          new x10.lang.FailedDynamicCheckException("x10.array.Dist{self.region.rank==1}");
                        
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
throw t36771;
                    }
                }
                
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
ret36590 = ((x10.array.Dist)(__desugarer__var__9__36589));
                
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Dist t36621 =
                  ((x10.array.Dist)(ret36590));
                
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36621;
            }
        }
        
        
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static x10.array.Dist
                                                                                               makeConstant(
                                                                                               final x10.array.Region r,
                                                                                               final x10.lang.Place p){
            
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.ConstantDist t36623 =
              ((x10.array.ConstantDist)(new x10.array.ConstantDist((java.lang.System[]) null).$init(((x10.array.Region)(r)),
                                                                                                    ((x10.lang.Place)(p)))));
            
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36623;
        }
        
        
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static x10.array.Dist
                                                                                               makeCyclic(
                                                                                               final x10.array.Region r,
                                                                                               final int axis,
                                                                                               final x10.array.PlaceGroup pg){
            
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.UnsupportedOperationException t36624 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
            
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
throw t36624;
        }
        
        
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static x10.array.Dist
                                                                                               makeBlock(
                                                                                               final x10.array.Region r,
                                                                                               final int axis,
                                                                                               final x10.array.PlaceGroup pg){
            
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.BlockDist t36625 =
              ((x10.array.BlockDist)(new x10.array.BlockDist((java.lang.System[]) null).$init(((x10.array.Region)(r)),
                                                                                              ((int)(axis)),
                                                                                              ((x10.array.PlaceGroup)(pg)))));
            
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36625;
        }
        
        
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static void
                                                                                               makeBlockCyclic(
                                                                                               final x10.array.Region r,
                                                                                               final int axis,
                                                                                               final int blockSize,
                                                                                               final x10.array.PlaceGroup pg){
            
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.UnsupportedOperationException t36626 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
            
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
throw t36626;
        }
        
        
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
abstract public x10.array.PlaceGroup
                                                                                               places(
                                                                                               );
        
        
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
abstract public int
                                                                                               numPlaces$O(
                                                                                               );
        
        
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
abstract public x10.lang.Iterable<x10.array.Region>
                                                                                               regions(
                                                                                               );
        
        
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
abstract public x10.array.Region
                                                                                               get(
                                                                                               final x10.lang.Place p);
        
        
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public x10.array.Region
                                                                                               $apply(
                                                                                               final x10.lang.Place p){
            
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Region t36627 =
              ((x10.array.Region)(this.get(((x10.lang.Place)(p)))));
            
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36627;
        }
        
        
//#line 290 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
abstract public x10.lang.Place
                                                                                               $apply(
                                                                                               final x10.array.Point pt);
        
        
//#line 301 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public x10.lang.Place
                                                                                               $apply(
                                                                                               final int i0){
            
//#line 301 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Point t36629 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0))));
            
//#line 301 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.Place t36630 =
              this.$apply(((x10.array.Point)(t36629)));
            
//#line 301 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36630;
        }
        
        
//#line 313 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public x10.lang.Place
                                                                                               $apply(
                                                                                               final int i0,
                                                                                               final int i1){
            
//#line 313 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Point t36632 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0),
                                                      (int)(i1))));
            
//#line 313 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.Place t36633 =
              this.$apply(((x10.array.Point)(t36632)));
            
//#line 313 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36633;
        }
        
        
//#line 326 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public x10.lang.Place
                                                                                               $apply(
                                                                                               final int i0,
                                                                                               final int i1,
                                                                                               final int i2){
            
//#line 326 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Point t36635 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0),
                                                      (int)(i1),
                                                      (int)(i2))));
            
//#line 326 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.Place t36636 =
              this.$apply(((x10.array.Point)(t36635)));
            
//#line 326 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36636;
        }
        
        
//#line 340 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public x10.lang.Place
                                                                                               $apply(
                                                                                               final int i0,
                                                                                               final int i1,
                                                                                               final int i2,
                                                                                               final int i3){
            
//#line 340 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Point t36638 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0),
                                                      (int)(i1),
                                                      (int)(i2),
                                                      (int)(i3))));
            
//#line 340 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.Place t36639 =
              this.$apply(((x10.array.Point)(t36638)));
            
//#line 340 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36639;
        }
        
        
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
abstract public int
                                                                                               offset$O(
                                                                                               final x10.array.Point pt);
        
        
//#line 369 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public int
                                                                                               offset$O(
                                                                                               final int i0){
            
//#line 369 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Point t36641 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0))));
            
//#line 369 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final int t36642 =
              this.offset$O(((x10.array.Point)(t36641)));
            
//#line 369 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36642;
        }
        
        
//#line 385 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public int
                                                                                               offset$O(
                                                                                               final int i0,
                                                                                               final int i1){
            
//#line 385 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Point t36644 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0),
                                                      (int)(i1))));
            
//#line 385 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final int t36645 =
              this.offset$O(((x10.array.Point)(t36644)));
            
//#line 385 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36645;
        }
        
        
//#line 402 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public int
                                                                                               offset$O(
                                                                                               final int i0,
                                                                                               final int i1,
                                                                                               final int i2){
            
//#line 402 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Point t36647 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0),
                                                      (int)(i1),
                                                      (int)(i2))));
            
//#line 402 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final int t36648 =
              this.offset$O(((x10.array.Point)(t36647)));
            
//#line 402 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36648;
        }
        
        
//#line 420 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public int
                                                                                               offset$O(
                                                                                               final int i0,
                                                                                               final int i1,
                                                                                               final int i2,
                                                                                               final int i3){
            
//#line 420 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Point t36650 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0),
                                                      (int)(i1),
                                                      (int)(i2),
                                                      (int)(i3))));
            
//#line 420 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final int t36651 =
              this.offset$O(((x10.array.Point)(t36650)));
            
//#line 420 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36651;
        }
        
        
//#line 427 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
abstract public int
                                                                                               maxOffset$O(
                                                                                               );
        
        
//#line 444 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public x10.lang.Iterator<x10.array.Point>
                                                                                               iterator(
                                                                                               ){
            
//#line 444 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Region t36652 =
              ((x10.array.Region)(region));
            
//#line 444 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.Iterator<x10.array.Point> t36653 =
              t36652.iterator();
            
//#line 444 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36653;
        }
        
        
//#line 481 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
abstract public x10.array.Dist
                                                                                               restriction(
                                                                                               final x10.array.Region r);
        
        
//#line 496 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public boolean
                                                                                               isSubdistribution$O(
                                                                                               final x10.array.Dist that){
            
//#line 497 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.Sequence<x10.lang.Place> t36777 =
              x10.lang.Place.places();
            
//#line 497 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.Iterator<x10.lang.Place> p36778 =
              ((x10.lang.Iterator<x10.lang.Place>)
                ((x10.lang.Iterable<x10.lang.Place>)t36777).iterator());
            
//#line 497 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
for (;
                                                                                                      true;
                                                                                                      ) {
                
//#line 497 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36779 =
                  ((x10.lang.Iterator<x10.lang.Place>)p36778).hasNext$O();
                
//#line 497 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
if (!(t36779)) {
                    
//#line 497 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
break;
                }
                
//#line 497 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.Place p36772 =
                  ((x10.lang.Iterator<x10.lang.Place>)p36778).next$G();
                
//#line 498 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Region t36773 =
                  ((x10.array.Region)(that.get(((x10.lang.Place)(p36772)))));
                
//#line 498 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Region t36774 =
                  ((x10.array.Region)(this.get(((x10.lang.Place)(p36772)))));
                
//#line 498 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36775 =
                  t36773.contains$O(((x10.array.Region)(t36774)));
                
//#line 498 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36776 =
                  !(t36775);
                
//#line 498 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
if (t36776) {
                    
//#line 499 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return false;
                }
            }
            
//#line 500 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return true;
        }
        
        
//#line 555 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public boolean
                                                                                               equals(
                                                                                               final java.lang.Object thatObj){
            
//#line 556 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36661 =
              x10.rtt.Equality.equalsequals((this),(thatObj));
            
//#line 556 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
if (t36661) {
                
//#line 556 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return true;
            }
            
//#line 557 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36662 =
              x10.array.Dist.$RTT.isInstance(thatObj);
            
//#line 557 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36663 =
              !(t36662);
            
//#line 557 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
if (t36663) {
                
//#line 557 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return false;
            }
            
//#line 558 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Dist that =
              ((x10.array.Dist)(x10.rtt.Types.<x10.array.Dist> cast(thatObj,x10.array.Dist.$RTT)));
            
//#line 559 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final int t36664 =
              this.rank$O();
            
//#line 559 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final int t36665 =
              that.rank$O();
            
//#line 559 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36666 =
              ((int) t36664) !=
            ((int) t36665);
            
//#line 559 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
if (t36666) {
                
//#line 559 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return false;
            }
            
//#line 560 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Region t36667 =
              ((x10.array.Region)(region));
            
//#line 560 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Region t36668 =
              ((x10.array.Region)(that.
                                    region));
            
//#line 560 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36669 =
              t36667.equals(((java.lang.Object)(t36668)));
            
//#line 560 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36670 =
              !(t36669);
            
//#line 560 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
if (t36670) {
                
//#line 560 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return false;
            }
            
//#line 561 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.PlaceGroup pg =
              this.places();
            
//#line 562 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.Iterator<x10.lang.Place> p36785 =
              pg.iterator();
            
//#line 562 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
for (;
                                                                                                      true;
                                                                                                      ) {
                
//#line 562 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36786 =
                  ((x10.lang.Iterator<x10.lang.Place>)p36785).hasNext$O();
                
//#line 562 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
if (!(t36786)) {
                    
//#line 562 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
break;
                }
                
//#line 562 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.Place p36780 =
                  ((x10.lang.Iterator<x10.lang.Place>)p36785).next$G();
                
//#line 563 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Region t36781 =
                  ((x10.array.Region)(this.get(((x10.lang.Place)(p36780)))));
                
//#line 563 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Region t36782 =
                  ((x10.array.Region)(that.get(((x10.lang.Place)(p36780)))));
                
//#line 563 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36783 =
                  t36781.equals(((java.lang.Object)(t36782)));
                
//#line 563 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36784 =
                  !(t36783);
                
//#line 563 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
if (t36784) {
                    
//#line 563 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return false;
                }
            }
            
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return true;
        }
        
        
//#line 579 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
abstract public x10.array.Dist
                                                                                               restriction(
                                                                                               final x10.lang.Place p);
        
        
//#line 587 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public boolean
                                                                                               contains$O(
                                                                                               final x10.array.Point p){
            
//#line 587 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Region t36677 =
              ((x10.array.Region)(region));
            
//#line 587 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36678 =
              t36677.contains$O(((x10.array.Point)(p)));
            
//#line 587 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36678;
        }
        
        
//#line 595 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public boolean
                                                                                               containsLocally$O(
                                                                                               final x10.array.Point p){
            
//#line 595 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
boolean t36680 =
              this.contains$O(((x10.array.Point)(p)));
            
//#line 595 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
if (t36680) {
                
//#line 595 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Region t36679 =
                  ((x10.array.Region)(this.$apply(((x10.lang.Place)(x10.lang.Runtime.home())))));
                
//#line 595 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
t36680 = t36679.contains$O(((x10.array.Point)(p)));
            }
            
//#line 595 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36681 =
              t36680;
            
//#line 595 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36681;
        }
        
        
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public x10.array.Dist
                                                                                               $bar(
                                                                                               final x10.array.Region r){
            
//#line 608 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Dist t36682 =
              ((x10.array.Dist)(this.restriction(((x10.array.Region)(r)))));
            
//#line 608 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36682;
        }
        
        
//#line 616 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public x10.array.Dist
                                                                                               $bar(
                                                                                               final x10.lang.Place p){
            
//#line 616 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Dist t36683 =
              ((x10.array.Dist)(this.restriction(((x10.lang.Place)(p)))));
            
//#line 616 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36683;
        }
        
        
//#line 654 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public java.lang.String
                                                                                               toString(
                                                                                               ){
            
//#line 655 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
java.lang.String s =
              "Dist(";
            
//#line 656 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
boolean first =
              true;
            
//#line 657 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.PlaceGroup t36799 =
              this.places();
            
//#line 657 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.Iterator<x10.lang.Place> p36800 =
              t36799.iterator();
            
//#line 657 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
for (;
                                                                                                      true;
                                                                                                      ) {
                
//#line 657 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36801 =
                  ((x10.lang.Iterator<x10.lang.Place>)p36800).hasNext$O();
                
//#line 657 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
if (!(t36801)) {
                    
//#line 657 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
break;
                }
                
//#line 657 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.Place p36787 =
                  ((x10.lang.Iterator<x10.lang.Place>)p36800).next$G();
                
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36788 =
                  first;
                
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final boolean t36789 =
                  !(t36788);
                
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
if (t36789) {
                    
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36790 =
                      s;
                    
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36791 =
                      ((t36790) + (","));
                    
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
s = t36791;
                }
                
//#line 659 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36792 =
                  s;
                
//#line 659 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.array.Region t36793 =
                  ((x10.array.Region)(this.get(((x10.lang.Place)(p36787)))));
                
//#line 659 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36794 =
                  (("") + (t36793));
                
//#line 659 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36795 =
                  ((t36794) + ("->"));
                
//#line 659 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final int t36796 =
                  p36787.
                    id;
                
//#line 659 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36797 =
                  ((t36795) + ((x10.core.Int.$box(t36796))));
                
//#line 659 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36798 =
                  ((t36792) + (t36797));
                
//#line 659 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
s = t36798;
                
//#line 660 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
first = false;
            }
            
//#line 662 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36698 =
              s;
            
//#line 662 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36699 =
              ((t36698) + (")"));
            
//#line 662 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
s = t36699;
            
//#line 663 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36700 =
              s;
            
//#line 663 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return t36700;
        }
        
        
//#line 675 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"

        // constructor for non-virtual call
        final public x10.array.Dist x10$array$Dist$$init$S(final x10.array.Region region) { {
                                                                                                   
//#line 675 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"

                                                                                                   
//#line 676 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
this.region = region;
                                                                                                   
                                                                                               }
                                                                                               return this;
                                                                                               }
        
        // constructor
        public x10.array.Dist $init(final x10.array.Region region){return x10$array$Dist$$init$S(region);}
        
        
        
//#line 679 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static void
                                                                                               raiseBoundsError(
                                                                                               final int i0){
            
//#line 680 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36701 =
              (("point (") + ((x10.core.Int.$box(i0))));
            
//#line 680 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36702 =
              ((t36701) + (") not contained in distribution"));
            
//#line 680 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.ArrayIndexOutOfBoundsException t36703 =
              ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t36702)));
            
//#line 680 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
throw t36703;
        }
        
        
//#line 682 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static void
                                                                                               raiseBoundsError(
                                                                                               final int i0,
                                                                                               final int i1){
            
//#line 683 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36704 =
              (("point (") + ((x10.core.Int.$box(i0))));
            
//#line 683 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36705 =
              ((t36704) + (", "));
            
//#line 683 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36706 =
              ((t36705) + ((x10.core.Int.$box(i1))));
            
//#line 683 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36707 =
              ((t36706) + (") not contained in distribution"));
            
//#line 683 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.ArrayIndexOutOfBoundsException t36708 =
              ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t36707)));
            
//#line 683 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
throw t36708;
        }
        
        
//#line 685 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static void
                                                                                               raiseBoundsError(
                                                                                               final int i0,
                                                                                               final int i1,
                                                                                               final int i2){
            
//#line 686 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36709 =
              (("point (") + ((x10.core.Int.$box(i0))));
            
//#line 686 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36710 =
              ((t36709) + (", "));
            
//#line 686 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36711 =
              ((t36710) + ((x10.core.Int.$box(i1))));
            
//#line 686 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36712 =
              ((t36711) + (", "));
            
//#line 686 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36713 =
              ((t36712) + ((x10.core.Int.$box(i2))));
            
//#line 686 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36714 =
              ((t36713) + (") not contained in distribution"));
            
//#line 686 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.ArrayIndexOutOfBoundsException t36715 =
              ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t36714)));
            
//#line 686 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
throw t36715;
        }
        
        
//#line 688 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static void
                                                                                               raiseBoundsError(
                                                                                               final int i0,
                                                                                               final int i1,
                                                                                               final int i2,
                                                                                               final int i3){
            
//#line 689 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36716 =
              (("point (") + ((x10.core.Int.$box(i0))));
            
//#line 689 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36717 =
              ((t36716) + (", "));
            
//#line 689 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36718 =
              ((t36717) + ((x10.core.Int.$box(i1))));
            
//#line 689 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36719 =
              ((t36718) + (", "));
            
//#line 689 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36720 =
              ((t36719) + ((x10.core.Int.$box(i2))));
            
//#line 689 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36721 =
              ((t36720) + (", "));
            
//#line 689 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36722 =
              ((t36721) + ((x10.core.Int.$box(i3))));
            
//#line 689 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36723 =
              ((t36722) + (") not contained in distribution"));
            
//#line 689 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.ArrayIndexOutOfBoundsException t36724 =
              ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t36723)));
            
//#line 689 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
throw t36724;
        }
        
        
//#line 691 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static void
                                                                                               raiseBoundsError(
                                                                                               final x10.array.Point pt){
            
//#line 692 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36725 =
              (("point ") + (pt));
            
//#line 692 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36726 =
              ((t36725) + (" not contained in distribution"));
            
//#line 692 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.ArrayIndexOutOfBoundsException t36727 =
              ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t36726)));
            
//#line 692 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
throw t36727;
        }
        
        
//#line 695 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static void
                                                                                               raisePlaceError(
                                                                                               final int i0){
            
//#line 696 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36728 =
              (("point (") + ((x10.core.Int.$box(i0))));
            
//#line 696 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36729 =
              ((t36728) + (") not defined at "));
            
//#line 696 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36730 =
              ((t36729) + (x10.lang.Runtime.home()));
            
//#line 696 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.BadPlaceException t36731 =
              ((x10.lang.BadPlaceException)(new x10.lang.BadPlaceException(t36730)));
            
//#line 696 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
throw t36731;
        }
        
        
//#line 698 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static void
                                                                                               raisePlaceError(
                                                                                               final int i0,
                                                                                               final int i1){
            
//#line 699 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36732 =
              (("point (") + ((x10.core.Int.$box(i0))));
            
//#line 699 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36733 =
              ((t36732) + (", "));
            
//#line 699 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36734 =
              ((t36733) + ((x10.core.Int.$box(i1))));
            
//#line 699 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36735 =
              ((t36734) + (") not defined at "));
            
//#line 699 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36736 =
              ((t36735) + (x10.lang.Runtime.home()));
            
//#line 699 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.BadPlaceException t36737 =
              ((x10.lang.BadPlaceException)(new x10.lang.BadPlaceException(t36736)));
            
//#line 699 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
throw t36737;
        }
        
        
//#line 701 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static void
                                                                                               raisePlaceError(
                                                                                               final int i0,
                                                                                               final int i1,
                                                                                               final int i2){
            
//#line 702 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36738 =
              (("point (") + ((x10.core.Int.$box(i0))));
            
//#line 702 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36739 =
              ((t36738) + (", "));
            
//#line 702 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36740 =
              ((t36739) + ((x10.core.Int.$box(i1))));
            
//#line 702 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36741 =
              ((t36740) + (", "));
            
//#line 702 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36742 =
              ((t36741) + ((x10.core.Int.$box(i2))));
            
//#line 702 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36743 =
              ((t36742) + (") not defined at "));
            
//#line 702 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36744 =
              ((t36743) + (x10.lang.Runtime.home()));
            
//#line 702 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.BadPlaceException t36745 =
              ((x10.lang.BadPlaceException)(new x10.lang.BadPlaceException(t36744)));
            
//#line 702 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
throw t36745;
        }
        
        
//#line 704 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static void
                                                                                               raisePlaceError(
                                                                                               final int i0,
                                                                                               final int i1,
                                                                                               final int i2,
                                                                                               final int i3){
            
//#line 705 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36746 =
              (("point (") + ((x10.core.Int.$box(i0))));
            
//#line 705 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36747 =
              ((t36746) + (", "));
            
//#line 705 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36748 =
              ((t36747) + ((x10.core.Int.$box(i1))));
            
//#line 705 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36749 =
              ((t36748) + (", "));
            
//#line 705 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36750 =
              ((t36749) + ((x10.core.Int.$box(i2))));
            
//#line 705 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36751 =
              ((t36750) + (", "));
            
//#line 705 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36752 =
              ((t36751) + ((x10.core.Int.$box(i3))));
            
//#line 705 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36753 =
              ((t36752) + (") not defined at "));
            
//#line 705 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36754 =
              ((t36753) + (x10.lang.Runtime.home()));
            
//#line 705 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.BadPlaceException t36755 =
              ((x10.lang.BadPlaceException)(new x10.lang.BadPlaceException(t36754)));
            
//#line 705 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
throw t36755;
        }
        
        
//#line 707 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
public static void
                                                                                               raisePlaceError(
                                                                                               final x10.array.Point pt){
            
//#line 708 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36756 =
              (("point ") + (pt));
            
//#line 708 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36757 =
              ((t36756) + (" not defined at "));
            
//#line 708 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final java.lang.String t36758 =
              ((t36757) + (x10.lang.Runtime.home()));
            
//#line 708 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final x10.lang.BadPlaceException t36759 =
              ((x10.lang.BadPlaceException)(new x10.lang.BadPlaceException(t36758)));
            
//#line 708 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
throw t36759;
        }
        
        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
final public x10.array.Dist
                                                                                              x10$array$Dist$$x10$array$Dist$this(
                                                                                              ){
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Dist.x10"
return x10.array.Dist.this;
        }
        
        public static short fieldId$UNIQUE;
        final public static x10.core.concurrent.AtomicInteger initStatus$UNIQUE = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        
        public static void
          getDeserialized$UNIQUE(
          x10.x10rt.X10JavaDeserializer deserializer){
            x10.array.Dist.UNIQUE = ((x10.array.UniqueDist)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
            x10.array.Dist.initStatus$UNIQUE.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10.array.UniqueDist
          getInitialized$UNIQUE(
          ){
            if (((int) x10.array.Dist.initStatus$UNIQUE.get()) ==
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                return x10.array.Dist.UNIQUE;
            }
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0) &&
                  x10.array.Dist.initStatus$UNIQUE.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                 (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                x10.array.Dist.UNIQUE = ((x10.array.UniqueDist)(new x10.array.UniqueDist((java.lang.System[]) null).$init()));
                if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                      ((boolean) true)) {
                    x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.array.Dist.UNIQUE")));
                }
                x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.array.Dist.UNIQUE)),
                                                                          (short)(x10.array.Dist.fieldId$UNIQUE));
                x10.array.Dist.initStatus$UNIQUE.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            } else {
                if (((int) x10.array.Dist.initStatus$UNIQUE.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) x10.array.Dist.initStatus$UNIQUE.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
            }
            return x10.array.Dist.UNIQUE;
        }
        
        static {
                   x10.array.Dist.fieldId$UNIQUE = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.array.Dist")),
                                                                                                                       ((java.lang.String)("UNIQUE")))))));
               }
    
}



