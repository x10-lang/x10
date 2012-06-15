package x10.array;

@x10.core.X10Generated final public class EmptyRegion extends x10.array.Region implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, EmptyRegion.class);
    
    public static final x10.rtt.RuntimeType<EmptyRegion> $RTT = x10.rtt.NamedType.<EmptyRegion> make(
    "x10.array.EmptyRegion", /* base class */EmptyRegion.class
    , /* parents */ new x10.rtt.Type[] {x10.array.Region.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(EmptyRegion $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + EmptyRegion.class + " calling"); } 
        x10.array.Region.$_deserialize_body($_obj, $deserializer);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        EmptyRegion $_obj = new EmptyRegion((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        
    }
    
    // constructor just for allocation
    public EmptyRegion(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
// creation method for java code (1-phase java constructor)
        public EmptyRegion(final int rank){this((java.lang.System[]) null);
                                               $init(rank);}
        
        // constructor for non-virtual call
        final public x10.array.EmptyRegion x10$array$EmptyRegion$$init$S(final int rank) { {
                                                                                                  
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
super.$init(((int)(rank)),
                                                                                                                                                                                                         ((boolean)(true)),
                                                                                                                                                                                                         ((boolean)(false)));
                                                                                                  
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"

                                                                                                  
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final boolean t37495 =
                                                                                                    ((rank) < (((int)(0))));
                                                                                                  
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
if (t37495) {
                                                                                                      
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final java.lang.String t37492 =
                                                                                                        (("Rank is negative (") + ((x10.core.Int.$box(rank))));
                                                                                                      
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final java.lang.String t37493 =
                                                                                                        ((t37492) + (")"));
                                                                                                      
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final x10.lang.IllegalArgumentException t37494 =
                                                                                                        ((x10.lang.IllegalArgumentException)(new x10.lang.IllegalArgumentException(t37493)));
                                                                                                      
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
throw t37494;
                                                                                                  }
                                                                                              }
                                                                                              return this;
                                                                                              }
        
        // constructor
        public x10.array.EmptyRegion $init(final int rank){return x10$array$EmptyRegion$$init$S(rank);}
        
        
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
public boolean
                                                                                                     isConvex$O(
                                                                                                     ){
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
return true;
        }
        
        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
public boolean
                                                                                                     isEmpty$O(
                                                                                                     ){
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
return true;
        }
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
public int
                                                                                                     size$O(
                                                                                                     ){
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
return 0;
        }
        
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
public int
                                                                                                     indexOf$O(
                                                                                                     final x10.array.Point id$43){
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
return -1;
        }
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
public x10.array.Region
                                                                                                     intersection(
                                                                                                     final x10.array.Region that){
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
return this;
        }
        
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
public x10.array.EmptyRegion
                                                                                                     product(
                                                                                                     final x10.array.Region that){
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final int t37496 =
              this.
                rank;
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final int t37497 =
              that.
                rank;
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final int t37498 =
              ((t37496) + (((int)(t37497))));
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final x10.array.EmptyRegion t37499 =
              ((x10.array.EmptyRegion)(new x10.array.EmptyRegion((java.lang.System[]) null).$init(t37498)));
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
return t37499;
        }
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
public x10.array.Region
                                                                                                     projection(
                                                                                                     final int axis){
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final x10.array.EmptyRegion t37500 =
              ((x10.array.EmptyRegion)(new x10.array.EmptyRegion((java.lang.System[]) null).$init(((int)(1)))));
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
return t37500;
        }
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
public x10.array.EmptyRegion
                                                                                                     translate(
                                                                                                     final x10.array.Point p){
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
return this;
        }
        
        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
public x10.array.EmptyRegion
                                                                                                     eliminate(
                                                                                                     final int i){
            
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final int t37501 =
              rank;
            
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final int t37502 =
              ((t37501) - (((int)(1))));
            
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final x10.array.EmptyRegion t37503 =
              ((x10.array.EmptyRegion)(new x10.array.EmptyRegion((java.lang.System[]) null).$init(t37502)));
            
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
return t37503;
        }
        
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
public x10.array.Region
                                                                                                     computeBoundingBox(
                                                                                                     ){
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final x10.lang.IllegalOperationException t37504 =
              ((x10.lang.IllegalOperationException)(new x10.lang.IllegalOperationException(((java.lang.String)("bounding box not not defined for empty region")))));
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
throw t37504;
        }
        
        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>
                                                                                                     min(
                                                                                                     ){
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final x10.lang.IllegalOperationException t37505 =
              ((x10.lang.IllegalOperationException)(new x10.lang.IllegalOperationException(((java.lang.String)("min not not defined for empty region")))));
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
throw t37505;
        }
        
        
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>
                                                                                                     max(
                                                                                                     ){
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final x10.lang.IllegalOperationException t37506 =
              ((x10.lang.IllegalOperationException)(new x10.lang.IllegalOperationException(((java.lang.String)("max not not defined for empty region")))));
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
throw t37506;
        }
        
        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
public boolean
                                                                                                     contains$O(
                                                                                                     final x10.array.Region that){
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final boolean t37507 =
              that.isEmpty$O();
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
return t37507;
        }
        
        
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
public boolean
                                                                                                     contains$O(
                                                                                                     final x10.array.Point p){
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
return false;
        }
        
        
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
@x10.core.X10Generated public static class ERIterator extends x10.core.Ref implements x10.lang.Iterator, x10.x10rt.X10JavaSerializable
                                                                                                   {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, ERIterator.class);
            
            public static final x10.rtt.RuntimeType<ERIterator> $RTT = x10.rtt.NamedType.<ERIterator> make(
            "x10.array.EmptyRegion.ERIterator", /* base class */ERIterator.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterator.$RTT, x10.array.Point.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(ERIterator $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + ERIterator.class + " calling"); } 
                $_obj.myRank = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                ERIterator $_obj = new ERIterator((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write(this.myRank);
                
            }
            
            // constructor just for allocation
            public ERIterator(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // bridge for method abstract public x10.lang.Iterator.next():T
            public x10.array.Point
              next$G(){return next();}
            
                
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
public int myRank;
                
                
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
// creation method for java code (1-phase java constructor)
                public ERIterator(final int r){this((java.lang.System[]) null);
                                                   $init(r);}
                
                // constructor for non-virtual call
                final public x10.array.EmptyRegion.ERIterator x10$array$EmptyRegion$ERIterator$$init$S(final int r) { {
                                                                                                                             
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"

                                                                                                                             
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
this.myRank = r;
                                                                                                                             
                                                                                                                         }
                                                                                                                         return this;
                                                                                                                         }
                
                // constructor
                public x10.array.EmptyRegion.ERIterator $init(final int r){return x10$array$EmptyRegion$ERIterator$$init$S(r);}
                
                
                
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
public boolean
                                                                                                             hasNext$O(
                                                                                                             ){
                    
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
return false;
                }
                
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
public x10.array.Point
                                                                                                             next(
                                                                                                             ){
                    
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final x10.util.NoSuchElementException t37508 =
                      ((x10.util.NoSuchElementException)(new x10.util.NoSuchElementException()));
                    
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
throw t37508;
                }
                
                
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final public x10.array.EmptyRegion.ERIterator
                                                                                                             x10$array$EmptyRegion$ERIterator$$x10$array$EmptyRegion$ERIterator$this(
                                                                                                             ){
                    
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
return x10.array.EmptyRegion.ERIterator.this;
                }
            
        }
        
        
        
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
public x10.lang.Iterator<x10.array.Point>
                                                                                                     iterator(
                                                                                                     ){
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final int t37509 =
              rank;
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final x10.array.EmptyRegion.ERIterator t37510 =
              ((x10.array.EmptyRegion.ERIterator)(new x10.array.EmptyRegion.ERIterator((java.lang.System[]) null).$init(((int)(t37509)))));
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
return t37510;
        }
        
        
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
public java.lang.String
                                                                                                     toString(
                                                                                                     ){
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final int t37511 =
              rank;
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final java.lang.String t37512 =
              (("empty(") + ((x10.core.Int.$box(t37511))));
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final java.lang.String t37513 =
              ((t37512) + (")"));
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
return t37513;
        }
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
final public x10.array.EmptyRegion
                                                                                                     x10$array$EmptyRegion$$x10$array$EmptyRegion$this(
                                                                                                     ){
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/EmptyRegion.x10"
return x10.array.EmptyRegion.this;
        }
    
}
