package x10.util;


@x10.core.X10Generated public class Team extends x10.core.Struct implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Team.class);
    
    public static final x10.rtt.RuntimeType<Team> $RTT = x10.rtt.NamedType.<Team> make(
    "x10.util.Team", /* base class */Team.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.STRUCT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Team $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Team.class + " calling"); } 
        $_obj.id = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Team $_obj = new Team((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write(this.id);
        
    }
    
    // zero value constructor
    public Team(final java.lang.System $dummy) { this.id = 0; }
    // constructor just for allocation
    public Team(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
@x10.core.X10Generated public static class DoubleIdx extends x10.core.Struct implements x10.x10rt.X10JavaSerializable
                                                                                           {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, DoubleIdx.class);
            
            public static final x10.rtt.RuntimeType<DoubleIdx> $RTT = x10.rtt.NamedType.<DoubleIdx> make(
            "x10.util.Team.DoubleIdx", /* base class */DoubleIdx.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.STRUCT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(DoubleIdx $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + DoubleIdx.class + " calling"); } 
                $_obj.value = $deserializer.readDouble();
                $_obj.idx = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                DoubleIdx $_obj = new DoubleIdx((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write(this.value);
                $serializer.write(this.idx);
                
            }
            
            // zero value constructor
            public DoubleIdx(final java.lang.System $dummy) { this.value = 0.0; this.idx = 0; }
            // constructor just for allocation
            public DoubleIdx(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
public double value;
                
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
public int idx;
                
                
                
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public java.lang.String
                                                                                                     typeName(
                                                                                                     ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
                
                
                
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public java.lang.String
                                                                                                     toString(
                                                                                                     ){
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final java.lang.String t63466 =
                      "struct x10.util.Team.DoubleIdx: value=";
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final double t63467 =
                      this.
                        value;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final java.lang.String t63468 =
                      ((t63466) + ((x10.core.Double.$box(t63467))));
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final java.lang.String t63469 =
                      ((t63468) + (" idx="));
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63470 =
                      this.
                        idx;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final java.lang.String t63471 =
                      ((t63469) + ((x10.core.Int.$box(t63470))));
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63471;
                }
                
                
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public int
                                                                                                     hashCode(
                                                                                                     ){
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
int result =
                      1;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63472 =
                      result;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63474 =
                      ((8191) * (((int)(t63472))));
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final double t63473 =
                      this.
                        value;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63475 =
                      x10.rtt.Types.hashCode(t63473);
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63476 =
                      ((t63474) + (((int)(t63475))));
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
result = t63476;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63477 =
                      result;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63479 =
                      ((8191) * (((int)(t63477))));
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63478 =
                      this.
                        idx;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63480 =
                      x10.rtt.Types.hashCode(t63478);
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63481 =
                      ((t63479) + (((int)(t63480))));
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
result = t63481;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63482 =
                      result;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63482;
                }
                
                
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public boolean
                                                                                                     equals(
                                                                                                     java.lang.Object other){
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final java.lang.Object t63483 =
                      other;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final boolean t63484 =
                      x10.util.Team.DoubleIdx.$RTT.isInstance(t63483);
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final boolean t63485 =
                      !(t63484);
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
if (t63485) {
                        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return false;
                    }
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final java.lang.Object t63486 =
                      other;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.util.Team.DoubleIdx t63487 =
                      ((x10.util.Team.DoubleIdx)x10.rtt.Types.asStruct(x10.util.Team.DoubleIdx.$RTT,t63486));
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final boolean t63488 =
                      this.equals$O(((x10.util.Team.DoubleIdx)(t63487)));
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63488;
                }
                
                
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public boolean
                                                                                                     equals$O(
                                                                                                     x10.util.Team.DoubleIdx other){
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final double t63490 =
                      this.
                        value;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.util.Team.DoubleIdx t63489 =
                      other;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final double t63491 =
                      t63489.
                        value;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
boolean t63495 =
                      ((double) t63490) ==
                    ((double) t63491);
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
if (t63495) {
                        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63493 =
                          this.
                            idx;
                        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.util.Team.DoubleIdx t63492 =
                          other;
                        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63494 =
                          t63492.
                            idx;
                        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
t63495 = ((int) t63493) ==
                        ((int) t63494);
                    }
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final boolean t63496 =
                      t63495;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63496;
                }
                
                
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public boolean
                                                                                                     _struct_equals$O(
                                                                                                     java.lang.Object other){
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final java.lang.Object t63497 =
                      other;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final boolean t63498 =
                      x10.util.Team.DoubleIdx.$RTT.isInstance(t63497);
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final boolean t63499 =
                      !(t63498);
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
if (t63499) {
                        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return false;
                    }
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final java.lang.Object t63500 =
                      other;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.util.Team.DoubleIdx t63501 =
                      ((x10.util.Team.DoubleIdx)x10.rtt.Types.asStruct(x10.util.Team.DoubleIdx.$RTT,t63500));
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final boolean t63502 =
                      this._struct_equals$O(((x10.util.Team.DoubleIdx)(t63501)));
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63502;
                }
                
                
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public boolean
                                                                                                     _struct_equals$O(
                                                                                                     x10.util.Team.DoubleIdx other){
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final double t63504 =
                      this.
                        value;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.util.Team.DoubleIdx t63503 =
                      other;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final double t63505 =
                      t63503.
                        value;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
boolean t63509 =
                      ((double) t63504) ==
                    ((double) t63505);
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
if (t63509) {
                        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63507 =
                          this.
                            idx;
                        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.util.Team.DoubleIdx t63506 =
                          other;
                        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63508 =
                          t63506.
                            idx;
                        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
t63509 = ((int) t63507) ==
                        ((int) t63508);
                    }
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final boolean t63510 =
                      t63509;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63510;
                }
                
                
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public x10.util.Team.DoubleIdx
                                                                                                     x10$util$Team$DoubleIdx$$x10$util$Team$DoubleIdx$this(
                                                                                                     ){
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return x10.util.Team.DoubleIdx.this;
                }
                
                
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
// creation method for java code (1-phase java constructor)
                public DoubleIdx(final double value,
                                 final int idx){this((java.lang.System[]) null);
                                                    $init(value,idx);}
                
                // constructor for non-virtual call
                final public x10.util.Team.DoubleIdx x10$util$Team$DoubleIdx$$init$S(final double value,
                                                                                     final int idx) { {
                                                                                                             
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
;
                                                                                                             
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
this.value = value;
                                                                                                             this.idx = idx;
                                                                                                             
                                                                                                         }
                                                                                                         return this;
                                                                                                         }
                
                // constructor
                public x10.util.Team.DoubleIdx $init(final double value,
                                                     final int idx){return x10$util$Team$DoubleIdx$$init$S(value,idx);}
                
            
        }
        
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
public static x10.util.Team WORLD;
        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
/** The underlying representation of a team's identity.
     */
        public int id;
        
        
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public int
                                                                                             id$O(
                                                                                             ){
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63511 =
              id;
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63511;
        }
        
        
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
// creation method for java code (1-phase java constructor)
        public Team(final int id){this((java.lang.System[]) null);
                                      $init(id);}
        
        // constructor for non-virtual call
        final public x10.util.Team x10$util$Team$$init$S(final int id) { {
                                                                                
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
;
                                                                                
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"

                                                                                
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
this.id = id;
                                                                            }
                                                                            return this;
                                                                            }
        
        // constructor
        public x10.util.Team $init(final int id){return x10$util$Team$$init$S(id);}
        
        
        
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
// creation method for java code (1-phase java constructor)
        public Team(final x10.array.Array<x10.lang.Place> places, __0$1x10$lang$Place$2 $dummy){this((java.lang.System[]) null);
                                                                                                    $init(places, (x10.util.Team.__0$1x10$lang$Place$2) null);}
        
        // constructor for non-virtual call
        final public x10.util.Team x10$util$Team$$init$S(final x10.array.Array<x10.lang.Place> places, __0$1x10$lang$Place$2 $dummy) { {
                                                                                                                                              
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Place> t63512 =
                                                                                                                                                ((x10.core.IndexedMemoryChunk)(((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, x10.lang.Place.$RTT),((x10.array.Array<x10.lang.Place>)places).raw()))));
                                                                                                                                              
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63513 =
                                                                                                                                                ((x10.array.Array<x10.lang.Place>)places).
                                                                                                                                                  size;
                                                                                                                                              
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
this.$init(((x10.core.IndexedMemoryChunk<x10.lang.Place>)(t63512)),
                                                                                                                                                                                                                                            ((int)(t63513)), (x10.util.Team.__0$1x10$lang$Place$2) null);
                                                                                                                                          }
                                                                                                                                          return this;
                                                                                                                                          }
        
        // constructor
        public x10.util.Team $init(final x10.array.Array<x10.lang.Place> places, __0$1x10$lang$Place$2 $dummy){return x10$util$Team$$init$S(places, $dummy);}
        
        
        
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
// creation method for java code (1-phase java constructor)
        public Team(final x10.core.IndexedMemoryChunk<x10.lang.Place> places,
                    final int count, __0$1x10$lang$Place$2 $dummy){this((java.lang.System[]) null);
                                                                       $init(places,count, (x10.util.Team.__0$1x10$lang$Place$2) null);}
        
        // constructor for non-virtual call
        final public x10.util.Team x10$util$Team$$init$S(final x10.core.IndexedMemoryChunk<x10.lang.Place> places,
                                                         final int count, __0$1x10$lang$Place$2 $dummy) { {
                                                                                                                 
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
;
                                                                                                                 
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"

                                                                                                                 
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> result =
                                                                                                                   x10.core.IndexedMemoryChunk.<x10.core.Int>allocate(x10.rtt.Types.INT, ((int)(1)), false);
                                                                                                                 {
                                                                                                                     
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.ensureNotInAtomic();
                                                                                                                     
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.lang.FinishState x10$__var13 =
                                                                                                                       x10.lang.Runtime.startFinish();
                                                                                                                     
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
try {try {{
                                                                                                                         {
                                                                                                                             
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.util.Team.nativeMake__0$1x10$lang$Place$2__2$1x10$lang$Int$2(((x10.core.IndexedMemoryChunk)(places)),
                                                                                                                                                                                                                                                                                 (int)(count),
                                                                                                                                                                                                                                                                                 ((x10.core.IndexedMemoryChunk)(result)));
                                                                                                                         }
                                                                                                                     }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                                                                                                                         
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                                                                                                                         
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
throw new x10.lang.Exception();
                                                                                                                     }finally {{
                                                                                                                          
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var13)));
                                                                                                                      }}
                                                                                                                     }
                                                                                                                 
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63514 =
                                                                                                                   ((int[])result.value)[0];
                                                                                                                 
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
this.id = t63514;
                                                                                                                 }
                                                                                                                 return this;
                                                                                                                 }
                                                                                                             
                                                                                                             // constructor
                                                                                                             public x10.util.Team $init(final x10.core.IndexedMemoryChunk<x10.lang.Place> places,
                                                                                                                                        final int count, __0$1x10$lang$Place$2 $dummy){return x10$util$Team$$init$S(places,count, $dummy);}
                                                                                                             
        
        
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final private static void
                                                                                             nativeMake__0$1x10$lang$Place$2__2$1x10$lang$Int$2(
                                                                                             final x10.core.IndexedMemoryChunk<x10.lang.Place> places,
                                                                                             final int count,
                                                                                             final x10.core.IndexedMemoryChunk<x10.core.Int> result){
            try {x10.x10rt.TeamSupport.nativeMake(places, count, result);} catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); }
        }
        
        final public static void
          nativeMake$P__0$1x10$lang$Place$2__2$1x10$lang$Int$2(
          final x10.core.IndexedMemoryChunk<x10.lang.Place> places,
          final int count,
          final x10.core.IndexedMemoryChunk<x10.core.Int> result){
            x10.util.Team.nativeMake__0$1x10$lang$Place$2__2$1x10$lang$Int$2(((x10.core.IndexedMemoryChunk)(places)),
                                                                             (int)(count),
                                                                             ((x10.core.IndexedMemoryChunk)(result)));
        }
        
        
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public int
                                                                                             size$O(
                                                                                             ){
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63515 =
              id;
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63516 =
              x10.util.Team.nativeSize$O((int)(t63515));
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63516;
        }
        
        
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final private static int
                                                                                             nativeSize$O(
                                                                                             final int id){
            try {return x10.x10rt.TeamSupport.nativeSize(id);} catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); }
        }
        
        final public static int
          nativeSize$P$O(
          final int id){
            return x10.util.Team.nativeSize$O((int)(id));
        }
        
        
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public void
                                                                                             barrier(
                                                                                             final int role){
            {
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.lang.FinishState x10$__var14 =
                  x10.lang.Runtime.startFinish();
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
try {try {{
                    {
                        
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63517 =
                          id;
                        
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.util.Team.nativeBarrier((int)(t63517),
                                                                                                                                       (int)(role));
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                    
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                    
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
throw new x10.lang.Exception();
                }finally {{
                     
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var14)));
                 }}
                }
            }
        
        
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final private static void
                                                                                             nativeBarrier(
                                                                                             final int id,
                                                                                             final int role){
            try {x10.x10rt.TeamSupport.nativeBarrier(id, role);} catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); }
        }
        
        final public static void
          nativeBarrier$P(
          final int id,
          final int role){
            x10.util.Team.nativeBarrier((int)(id),
                                        (int)(role));
        }
        
        
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public <$T>void
                                                                                              scatter__2$1x10$util$Team$$T$2__4$1x10$util$Team$$T$2(
                                                                                              final x10.rtt.Type $T,
                                                                                              final int role,
                                                                                              final int root,
                                                                                              final x10.array.Array src,
                                                                                              final int src_off,
                                                                                              final x10.array.Array dst,
                                                                                              final int dst_off,
                                                                                              final int count){
            {
                
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.lang.FinishState x10$__var15 =
                  x10.lang.Runtime.startFinish();
                
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
try {try {{
                    {
                        
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63518 =
                          id;
                        
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.core.IndexedMemoryChunk<$T> t63519 =
                          ((x10.core.IndexedMemoryChunk)(((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.Array<$T>)src).raw()))));
                        
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.core.IndexedMemoryChunk<$T> t63520 =
                          ((x10.core.IndexedMemoryChunk)(((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.Array<$T>)dst).raw()))));
                        
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.util.Team.<$T>nativeScatter__3$1x10$util$Team$$T$2__5$1x10$util$Team$$T$2($T, (int)(t63518),
                                                                                                                                                                                          (int)(role),
                                                                                                                                                                                          (int)(root),
                                                                                                                                                                                          ((x10.core.IndexedMemoryChunk)(t63519)),
                                                                                                                                                                                          (int)(src_off),
                                                                                                                                                                                          ((x10.core.IndexedMemoryChunk)(t63520)),
                                                                                                                                                                                          (int)(dst_off),
                                                                                                                                                                                          (int)(count));
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                    
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                    
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
throw new x10.lang.Exception();
                }finally {{
                     
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var15)));
                 }}
                }
            }
        
        
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final private static <$T>void
                                                                                              nativeScatter__3$1x10$util$Team$$T$2__5$1x10$util$Team$$T$2(
                                                                                              final x10.rtt.Type $T,
                                                                                              final int id,
                                                                                              final int role,
                                                                                              final int root,
                                                                                              final x10.core.IndexedMemoryChunk<$T> src,
                                                                                              final int src_off,
                                                                                              final x10.core.IndexedMemoryChunk<$T> dst,
                                                                                              final int dst_off,
                                                                                              final int count){
            try {x10.x10rt.TeamSupport.nativeScatter(id, role, root, src, src_off, dst, dst_off, count);} catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); }
        }
        
        final public static <$T>void
          nativeScatter$P__3$1x10$util$Team$$T$2__5$1x10$util$Team$$T$2(
          final x10.rtt.Type $T,
          final int id,
          final int role,
          final int root,
          final x10.core.IndexedMemoryChunk<$T> src,
          final int src_off,
          final x10.core.IndexedMemoryChunk<$T> dst,
          final int dst_off,
          final int count){
            x10.util.Team.<$T>nativeScatter__3$1x10$util$Team$$T$2__5$1x10$util$Team$$T$2($T, (int)(id),
                                                                                          (int)(role),
                                                                                          (int)(root),
                                                                                          ((x10.core.IndexedMemoryChunk)(src)),
                                                                                          (int)(src_off),
                                                                                          ((x10.core.IndexedMemoryChunk)(dst)),
                                                                                          (int)(dst_off),
                                                                                          (int)(count));
        }
        
        
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public <$T>void
                                                                                              bcast__2$1x10$util$Team$$T$2__4$1x10$util$Team$$T$2(
                                                                                              final x10.rtt.Type $T,
                                                                                              final int role,
                                                                                              final int root,
                                                                                              final x10.array.Array src,
                                                                                              final int src_off,
                                                                                              final x10.array.Array dst,
                                                                                              final int dst_off,
                                                                                              final int count){
            {
                
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.lang.FinishState x10$__var16 =
                  x10.lang.Runtime.startFinish();
                
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
try {try {{
                    {
                        
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63521 =
                          id;
                        
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.core.IndexedMemoryChunk<$T> t63522 =
                          ((x10.core.IndexedMemoryChunk)(((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.Array<$T>)src).raw()))));
                        
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.core.IndexedMemoryChunk<$T> t63523 =
                          ((x10.core.IndexedMemoryChunk)(((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.Array<$T>)dst).raw()))));
                        
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.util.Team.<$T>nativeBcast__3$1x10$util$Team$$T$2__5$1x10$util$Team$$T$2($T, (int)(t63521),
                                                                                                                                                                                        (int)(role),
                                                                                                                                                                                        (int)(root),
                                                                                                                                                                                        ((x10.core.IndexedMemoryChunk)(t63522)),
                                                                                                                                                                                        (int)(src_off),
                                                                                                                                                                                        ((x10.core.IndexedMemoryChunk)(t63523)),
                                                                                                                                                                                        (int)(dst_off),
                                                                                                                                                                                        (int)(count));
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                    
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                    
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
throw new x10.lang.Exception();
                }finally {{
                     
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var16)));
                 }}
                }
            }
        
        
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final private static <$T>void
                                                                                              nativeBcast__3$1x10$util$Team$$T$2__5$1x10$util$Team$$T$2(
                                                                                              final x10.rtt.Type $T,
                                                                                              final int id,
                                                                                              final int role,
                                                                                              final int root,
                                                                                              final x10.core.IndexedMemoryChunk<$T> src,
                                                                                              final int src_off,
                                                                                              final x10.core.IndexedMemoryChunk<$T> dst,
                                                                                              final int dst_off,
                                                                                              final int count){
            try {x10.x10rt.TeamSupport.nativeBcast(id, role, root, src, src_off, dst, dst_off, count);} catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); }
        }
        
        final public static <$T>void
          nativeBcast$P__3$1x10$util$Team$$T$2__5$1x10$util$Team$$T$2(
          final x10.rtt.Type $T,
          final int id,
          final int role,
          final int root,
          final x10.core.IndexedMemoryChunk<$T> src,
          final int src_off,
          final x10.core.IndexedMemoryChunk<$T> dst,
          final int dst_off,
          final int count){
            x10.util.Team.<$T>nativeBcast__3$1x10$util$Team$$T$2__5$1x10$util$Team$$T$2($T, (int)(id),
                                                                                        (int)(role),
                                                                                        (int)(root),
                                                                                        ((x10.core.IndexedMemoryChunk)(src)),
                                                                                        (int)(src_off),
                                                                                        ((x10.core.IndexedMemoryChunk)(dst)),
                                                                                        (int)(dst_off),
                                                                                        (int)(count));
        }
        
        
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public <$T>void
                                                                                              alltoall__1$1x10$util$Team$$T$2__3$1x10$util$Team$$T$2(
                                                                                              final x10.rtt.Type $T,
                                                                                              final int role,
                                                                                              final x10.array.Array src,
                                                                                              final int src_off,
                                                                                              final x10.array.Array dst,
                                                                                              final int dst_off,
                                                                                              final int count){
            {
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.lang.FinishState x10$__var17 =
                  x10.lang.Runtime.startFinish();
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
try {try {{
                    {
                        
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63524 =
                          id;
                        
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.core.IndexedMemoryChunk<$T> t63525 =
                          ((x10.core.IndexedMemoryChunk)(((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.Array<$T>)src).raw()))));
                        
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.core.IndexedMemoryChunk<$T> t63526 =
                          ((x10.core.IndexedMemoryChunk)(((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.Array<$T>)dst).raw()))));
                        
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.util.Team.<$T>nativeAlltoall__2$1x10$util$Team$$T$2__4$1x10$util$Team$$T$2($T, (int)(t63524),
                                                                                                                                                                                           (int)(role),
                                                                                                                                                                                           ((x10.core.IndexedMemoryChunk)(t63525)),
                                                                                                                                                                                           (int)(src_off),
                                                                                                                                                                                           ((x10.core.IndexedMemoryChunk)(t63526)),
                                                                                                                                                                                           (int)(dst_off),
                                                                                                                                                                                           (int)(count));
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                    
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                    
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
throw new x10.lang.Exception();
                }finally {{
                     
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var17)));
                 }}
                }
            }
        
        
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final private static <$T>void
                                                                                              nativeAlltoall__2$1x10$util$Team$$T$2__4$1x10$util$Team$$T$2(
                                                                                              final x10.rtt.Type $T,
                                                                                              final int id,
                                                                                              final int role,
                                                                                              final x10.core.IndexedMemoryChunk<$T> src,
                                                                                              final int src_off,
                                                                                              final x10.core.IndexedMemoryChunk<$T> dst,
                                                                                              final int dst_off,
                                                                                              final int count){
            try {x10.x10rt.TeamSupport.nativeAllToAll(id, role, src, src_off, dst, dst_off, count);} catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); }
        }
        
        final public static <$T>void
          nativeAlltoall$P__2$1x10$util$Team$$T$2__4$1x10$util$Team$$T$2(
          final x10.rtt.Type $T,
          final int id,
          final int role,
          final x10.core.IndexedMemoryChunk<$T> src,
          final int src_off,
          final x10.core.IndexedMemoryChunk<$T> dst,
          final int dst_off,
          final int count){
            x10.util.Team.<$T>nativeAlltoall__2$1x10$util$Team$$T$2__4$1x10$util$Team$$T$2($T, (int)(id),
                                                                                           (int)(role),
                                                                                           ((x10.core.IndexedMemoryChunk)(src)),
                                                                                           (int)(src_off),
                                                                                           ((x10.core.IndexedMemoryChunk)(dst)),
                                                                                           (int)(dst_off),
                                                                                           (int)(count));
        }
        
        
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
/** Indicates the operation to perform when reducing. */
        final public static int ADD = 0;
        
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
/** Indicates the operation to perform when reducing. */
        final public static int MUL = 1;
        
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
/** Indicates the operation to perform when reducing. */
        final public static int AND = 3;
        
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
/** Indicates the operation to perform when reducing. */
        final public static int OR = 4;
        
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
/** Indicates the operation to perform when reducing. */
        final public static int XOR = 5;
        
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
/** Indicates the operation to perform when reducing. */
        final public static int MAX = 6;
        
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
/** Indicates the operation to perform when reducing. */
        final public static int MIN = 7;
        
        
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public <$T>void
                                                                                              allreduce__1$1x10$util$Team$$T$2__3$1x10$util$Team$$T$2(
                                                                                              final x10.rtt.Type $T,
                                                                                              final int role,
                                                                                              final x10.array.Array src,
                                                                                              final int src_off,
                                                                                              final x10.array.Array dst,
                                                                                              final int dst_off,
                                                                                              final int count,
                                                                                              final int op){
            {
                
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.lang.FinishState x10$__var18 =
                  x10.lang.Runtime.startFinish();
                
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
try {try {{
                    {
                        
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63527 =
                          id;
                        
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.core.IndexedMemoryChunk<$T> t63528 =
                          ((x10.core.IndexedMemoryChunk)(((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.Array<$T>)src).raw()))));
                        
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.core.IndexedMemoryChunk<$T> t63529 =
                          ((x10.core.IndexedMemoryChunk)(((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.Array<$T>)dst).raw()))));
                        
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.util.Team.<$T>nativeAllreduce__2$1x10$util$Team$$T$2__4$1x10$util$Team$$T$2($T, (int)(t63527),
                                                                                                                                                                                            (int)(role),
                                                                                                                                                                                            ((x10.core.IndexedMemoryChunk)(t63528)),
                                                                                                                                                                                            (int)(src_off),
                                                                                                                                                                                            ((x10.core.IndexedMemoryChunk)(t63529)),
                                                                                                                                                                                            (int)(dst_off),
                                                                                                                                                                                            (int)(count),
                                                                                                                                                                                            (int)(op));
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                    
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                    
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
throw new x10.lang.Exception();
                }finally {{
                     
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var18)));
                 }}
                }
            }
        
        
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final private static <$T>void
                                                                                              nativeAllreduce__2$1x10$util$Team$$T$2__4$1x10$util$Team$$T$2(
                                                                                              final x10.rtt.Type $T,
                                                                                              final int id,
                                                                                              final int role,
                                                                                              final x10.core.IndexedMemoryChunk<$T> src,
                                                                                              final int src_off,
                                                                                              final x10.core.IndexedMemoryChunk<$T> dst,
                                                                                              final int dst_off,
                                                                                              final int count,
                                                                                              final int op){
            try {x10.x10rt.TeamSupport.nativeAllReduce(id, role, src, src_off, dst, dst_off, count, op);} catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); }
        }
        
        final public static <$T>void
          nativeAllreduce$P__2$1x10$util$Team$$T$2__4$1x10$util$Team$$T$2(
          final x10.rtt.Type $T,
          final int id,
          final int role,
          final x10.core.IndexedMemoryChunk<$T> src,
          final int src_off,
          final x10.core.IndexedMemoryChunk<$T> dst,
          final int dst_off,
          final int count,
          final int op){
            x10.util.Team.<$T>nativeAllreduce__2$1x10$util$Team$$T$2__4$1x10$util$Team$$T$2($T, (int)(id),
                                                                                            (int)(role),
                                                                                            ((x10.core.IndexedMemoryChunk)(src)),
                                                                                            (int)(src_off),
                                                                                            ((x10.core.IndexedMemoryChunk)(dst)),
                                                                                            (int)(dst_off),
                                                                                            (int)(count),
                                                                                            (int)(op));
        }
        
        
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public byte
                                                                                              allreduce$O(
                                                                                              final int role,
                                                                                              final byte src,
                                                                                              final int op){
            
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final byte t63530 =
              x10.core.Byte.$unbox(this.<x10.core.Byte>genericAllreduce__1x10$util$Team$$T$G(x10.rtt.Types.BYTE, (int)(role),
                                                                                             x10.core.Byte.$box(src),
                                                                                             (int)(op)));
            
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63530;
        }
        
        
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public byte
                                                                                              allreduce__1$u$O(
                                                                                              final int role,
                                                                                              final byte src,
                                                                                              final int op){
            
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final byte t63531 =
              x10.core.UByte.$unbox(this.<x10.core.UByte>genericAllreduce__1x10$util$Team$$T$G(x10.rtt.Types.UBYTE, (int)(role),
                                                                                               x10.core.UByte.$box(src),
                                                                                               (int)(op)));
            
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63531;
        }
        
        
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public short
                                                                                              allreduce$O(
                                                                                              final int role,
                                                                                              final short src,
                                                                                              final int op){
            
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final short t63532 =
              x10.core.Short.$unbox(this.<x10.core.Short>genericAllreduce__1x10$util$Team$$T$G(x10.rtt.Types.SHORT, (int)(role),
                                                                                               x10.core.Short.$box(src),
                                                                                               (int)(op)));
            
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63532;
        }
        
        
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public short
                                                                                              allreduce__1$u$O(
                                                                                              final int role,
                                                                                              final short src,
                                                                                              final int op){
            
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final short t63533 =
              x10.core.UShort.$unbox(this.<x10.core.UShort>genericAllreduce__1x10$util$Team$$T$G(x10.rtt.Types.USHORT, (int)(role),
                                                                                                 x10.core.UShort.$box(src),
                                                                                                 (int)(op)));
            
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63533;
        }
        
        
//#line 222 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public int
                                                                                              allreduce__1$u$O(
                                                                                              final int role,
                                                                                              final int src,
                                                                                              final int op){
            
//#line 222 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63534 =
              x10.core.UInt.$unbox(this.<x10.core.UInt>genericAllreduce__1x10$util$Team$$T$G(x10.rtt.Types.UINT, (int)(role),
                                                                                             x10.core.UInt.$box(src),
                                                                                             (int)(op)));
            
//#line 222 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63534;
        }
        
        
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public int
                                                                                              allreduce$O(
                                                                                              final int role,
                                                                                              final int src,
                                                                                              final int op){
            
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63535 =
              x10.core.Int.$unbox(this.<x10.core.Int>genericAllreduce__1x10$util$Team$$T$G(x10.rtt.Types.INT, (int)(role),
                                                                                           x10.core.Int.$box(src),
                                                                                           (int)(op)));
            
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63535;
        }
        
        
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public long
                                                                                              allreduce$O(
                                                                                              final int role,
                                                                                              final long src,
                                                                                              final int op){
            
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final long t63536 =
              x10.core.Long.$unbox(this.<x10.core.Long>genericAllreduce__1x10$util$Team$$T$G(x10.rtt.Types.LONG, (int)(role),
                                                                                             x10.core.Long.$box(src),
                                                                                             (int)(op)));
            
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63536;
        }
        
        
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public long
                                                                                              allreduce__1$u$O(
                                                                                              final int role,
                                                                                              final long src,
                                                                                              final int op){
            
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final long t63537 =
              x10.core.ULong.$unbox(this.<x10.core.ULong>genericAllreduce__1x10$util$Team$$T$G(x10.rtt.Types.ULONG, (int)(role),
                                                                                               x10.core.ULong.$box(src),
                                                                                               (int)(op)));
            
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63537;
        }
        
        
//#line 230 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public float
                                                                                              allreduce$O(
                                                                                              final int role,
                                                                                              final float src,
                                                                                              final int op){
            
//#line 230 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final float t63538 =
              x10.core.Float.$unbox(this.<x10.core.Float>genericAllreduce__1x10$util$Team$$T$G(x10.rtt.Types.FLOAT, (int)(role),
                                                                                               x10.core.Float.$box(src),
                                                                                               (int)(op)));
            
//#line 230 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63538;
        }
        
        
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public double
                                                                                              allreduce$O(
                                                                                              final int role,
                                                                                              final double src,
                                                                                              final int op){
            
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final double t63539 =
              x10.core.Double.$unbox(this.<x10.core.Double>genericAllreduce__1x10$util$Team$$T$G(x10.rtt.Types.DOUBLE, (int)(role),
                                                                                                 x10.core.Double.$box(src),
                                                                                                 (int)(op)));
            
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63539;
        }
        
        
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final private <$T>$T
                                                                                              genericAllreduce__1x10$util$Team$$T$G(
                                                                                              final x10.rtt.Type $T,
                                                                                              final int role,
                                                                                              final $T src,
                                                                                              final int op){
            
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.core.IndexedMemoryChunk<$T> chk =
              x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(1)), false);
            
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.core.IndexedMemoryChunk<$T> dst =
              x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(1)), false);
            
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
(((x10.core.IndexedMemoryChunk<$T>)(chk))).$set(((int)(0)), src);
            {
                
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.lang.FinishState x10$__var19 =
                  x10.lang.Runtime.startFinish();
                
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
try {try {{
                    {
                        
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63540 =
                          id;
                        
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.util.Team.<$T>nativeAllreduce__2$1x10$util$Team$$T$2__3$1x10$util$Team$$T$2($T, (int)(t63540),
                                                                                                                                                                                            (int)(role),
                                                                                                                                                                                            ((x10.core.IndexedMemoryChunk)(chk)),
                                                                                                                                                                                            ((x10.core.IndexedMemoryChunk)(dst)),
                                                                                                                                                                                            (int)(op));
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                    
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                    
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
throw new x10.lang.Exception();
                }finally {{
                     
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var19)));
                 }}
                }
            
//#line 239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final $T t63541 =
              (($T)((((x10.core.IndexedMemoryChunk<$T>)(dst))).$apply$G(((int)(0)))));
            
//#line 239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63541;
            }
        
        final public static <$T>$T
          genericAllreduce$P__1x10$util$Team$$T$G(
          final x10.rtt.Type $T,
          final int role,
          final $T src,
          final int op,
          final x10.util.Team Team){
            return ((x10.util.Team)Team).<$T>genericAllreduce__1x10$util$Team$$T$G($T, (int)(role),
                                                                                   (($T)(src)),
                                                                                   (int)(op));
        }
        
        
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final private static <$T>void
                                                                                              nativeAllreduce__2$1x10$util$Team$$T$2__3$1x10$util$Team$$T$2(
                                                                                              final x10.rtt.Type $T,
                                                                                              final int id,
                                                                                              final int role,
                                                                                              final x10.core.IndexedMemoryChunk<$T> src,
                                                                                              final x10.core.IndexedMemoryChunk<$T> dst,
                                                                                              final int op){
            try {x10.x10rt.TeamSupport.nativeAllReduce(id, role, src, 0, dst, 0, 1, op);} catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); }
        }
        
        final public static <$T>void
          nativeAllreduce$P__2$1x10$util$Team$$T$2__3$1x10$util$Team$$T$2(
          final x10.rtt.Type $T,
          final int id,
          final int role,
          final x10.core.IndexedMemoryChunk<$T> src,
          final x10.core.IndexedMemoryChunk<$T> dst,
          final int op){
            x10.util.Team.<$T>nativeAllreduce__2$1x10$util$Team$$T$2__3$1x10$util$Team$$T$2($T, (int)(id),
                                                                                            (int)(role),
                                                                                            ((x10.core.IndexedMemoryChunk)(src)),
                                                                                            ((x10.core.IndexedMemoryChunk)(dst)),
                                                                                            (int)(op));
        }
        
        
//#line 248 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public int
                                                                                              indexOfMax$O(
                                                                                              final int role,
                                                                                              final double v,
                                                                                              final int idx){
            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.core.IndexedMemoryChunk<x10.util.Team.DoubleIdx> src =
              x10.core.IndexedMemoryChunk.<x10.util.Team.DoubleIdx>allocate(x10.util.Team.DoubleIdx.$RTT, ((int)(1)), false);
            
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.core.IndexedMemoryChunk<x10.util.Team.DoubleIdx> dst =
              x10.core.IndexedMemoryChunk.<x10.util.Team.DoubleIdx>allocate(x10.util.Team.DoubleIdx.$RTT, ((int)(1)), false);
            
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.util.Team.DoubleIdx t63542 =
              ((x10.util.Team.DoubleIdx)(new x10.util.Team.DoubleIdx((java.lang.System[]) null).$init(((double)(v)),
                                                                                                      ((int)(idx)))));
            
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
((x10.util.Team.DoubleIdx[])src.value)[0] = t63542;
            {
                
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.lang.FinishState x10$__var20 =
                  x10.lang.Runtime.startFinish();
                
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
try {try {{
                    {
                        
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63543 =
                          id;
                        
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.util.Team.nativeIndexOfMax__2$1x10$util$Team$DoubleIdx$2__3$1x10$util$Team$DoubleIdx$2((int)(t63543),
                                                                                                                                                                                                       (int)(role),
                                                                                                                                                                                                       ((x10.core.IndexedMemoryChunk)(src)),
                                                                                                                                                                                                       ((x10.core.IndexedMemoryChunk)(dst)));
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                    
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                    
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
throw new x10.lang.Exception();
                }finally {{
                     
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var20)));
                 }}
                }
            
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.util.Team.DoubleIdx t63544 =
              ((x10.util.Team.DoubleIdx[])dst.value)[0];
            
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63545 =
              t63544.
                idx;
            
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63545;
            }
        
        
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final private static void
                                                                                              nativeIndexOfMax__2$1x10$util$Team$DoubleIdx$2__3$1x10$util$Team$DoubleIdx$2(
                                                                                              final int id,
                                                                                              final int role,
                                                                                              final x10.core.IndexedMemoryChunk<x10.util.Team.DoubleIdx> src,
                                                                                              final x10.core.IndexedMemoryChunk<x10.util.Team.DoubleIdx> dst){
            try {x10.x10rt.TeamSupport.nativeIndexOfMax(id, role, src, dst);} catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); }
        }
        
        final public static void
          nativeIndexOfMax$P__2$1x10$util$Team$DoubleIdx$2__3$1x10$util$Team$DoubleIdx$2(
          final int id,
          final int role,
          final x10.core.IndexedMemoryChunk<x10.util.Team.DoubleIdx> src,
          final x10.core.IndexedMemoryChunk<x10.util.Team.DoubleIdx> dst){
            x10.util.Team.nativeIndexOfMax__2$1x10$util$Team$DoubleIdx$2__3$1x10$util$Team$DoubleIdx$2((int)(id),
                                                                                                       (int)(role),
                                                                                                       ((x10.core.IndexedMemoryChunk)(src)),
                                                                                                       ((x10.core.IndexedMemoryChunk)(dst)));
        }
        
        
//#line 262 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public int
                                                                                              indexOfMin$O(
                                                                                              final int role,
                                                                                              final double v,
                                                                                              final int idx){
            
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.core.IndexedMemoryChunk<x10.util.Team.DoubleIdx> src =
              x10.core.IndexedMemoryChunk.<x10.util.Team.DoubleIdx>allocate(x10.util.Team.DoubleIdx.$RTT, ((int)(1)), false);
            
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.core.IndexedMemoryChunk<x10.util.Team.DoubleIdx> dst =
              x10.core.IndexedMemoryChunk.<x10.util.Team.DoubleIdx>allocate(x10.util.Team.DoubleIdx.$RTT, ((int)(1)), false);
            
//#line 265 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.util.Team.DoubleIdx t63546 =
              ((x10.util.Team.DoubleIdx)(new x10.util.Team.DoubleIdx((java.lang.System[]) null).$init(((double)(v)),
                                                                                                      ((int)(idx)))));
            
//#line 265 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
((x10.util.Team.DoubleIdx[])src.value)[0] = t63546;
            {
                
//#line 266 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 266 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.lang.FinishState x10$__var21 =
                  x10.lang.Runtime.startFinish();
                
//#line 266 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
try {try {{
                    {
                        
//#line 266 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63547 =
                          id;
                        
//#line 266 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.util.Team.nativeIndexOfMin__2$1x10$util$Team$DoubleIdx$2__3$1x10$util$Team$DoubleIdx$2((int)(t63547),
                                                                                                                                                                                                       (int)(role),
                                                                                                                                                                                                       ((x10.core.IndexedMemoryChunk)(src)),
                                                                                                                                                                                                       ((x10.core.IndexedMemoryChunk)(dst)));
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                    
//#line 266 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                    
//#line 266 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
throw new x10.lang.Exception();
                }finally {{
                     
//#line 266 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var21)));
                 }}
                }
            
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.util.Team.DoubleIdx t63548 =
              ((x10.util.Team.DoubleIdx[])dst.value)[0];
            
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63549 =
              t63548.
                idx;
            
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63549;
            }
        
        
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final private static void
                                                                                              nativeIndexOfMin__2$1x10$util$Team$DoubleIdx$2__3$1x10$util$Team$DoubleIdx$2(
                                                                                              final int id,
                                                                                              final int role,
                                                                                              final x10.core.IndexedMemoryChunk<x10.util.Team.DoubleIdx> src,
                                                                                              final x10.core.IndexedMemoryChunk<x10.util.Team.DoubleIdx> dst){
            try {x10.x10rt.TeamSupport.nativeIndexOfMin(id, role, src, dst);} catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); }
        }
        
        final public static void
          nativeIndexOfMin$P__2$1x10$util$Team$DoubleIdx$2__3$1x10$util$Team$DoubleIdx$2(
          final int id,
          final int role,
          final x10.core.IndexedMemoryChunk<x10.util.Team.DoubleIdx> src,
          final x10.core.IndexedMemoryChunk<x10.util.Team.DoubleIdx> dst){
            x10.util.Team.nativeIndexOfMin__2$1x10$util$Team$DoubleIdx$2__3$1x10$util$Team$DoubleIdx$2((int)(id),
                                                                                                       (int)(role),
                                                                                                       ((x10.core.IndexedMemoryChunk)(src)),
                                                                                                       ((x10.core.IndexedMemoryChunk)(dst)));
        }
        
        
//#line 289 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public x10.util.Team
                                                                                              split(
                                                                                              final int role,
                                                                                              final int color,
                                                                                              final int new_role){
            
//#line 290 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> result =
              x10.core.IndexedMemoryChunk.<x10.core.Int>allocate(x10.rtt.Types.INT, ((int)(1)), false);
            {
                
//#line 291 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 291 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.lang.FinishState x10$__var22 =
                  x10.lang.Runtime.startFinish();
                
//#line 291 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
try {try {{
                    {
                        
//#line 291 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63550 =
                          id;
                        
//#line 291 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.util.Team.nativeSplit__4$1x10$lang$Int$2((int)(t63550),
                                                                                                                                                         (int)(role),
                                                                                                                                                         (int)(color),
                                                                                                                                                         (int)(new_role),
                                                                                                                                                         ((x10.core.IndexedMemoryChunk)(result)));
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                    
//#line 291 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                    
//#line 291 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
throw new x10.lang.Exception();
                }finally {{
                     
//#line 291 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var22)));
                 }}
                }
            
//#line 292 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63551 =
              ((int[])result.value)[0];
            
//#line 292 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.util.Team t63552 =
              new x10.util.Team((java.lang.System[]) null).$init(t63551);
            
//#line 292 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63552;
            }
        
        
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final private static void
                                                                                              nativeSplit__4$1x10$lang$Int$2(
                                                                                              final int id,
                                                                                              final int role,
                                                                                              final int color,
                                                                                              final int new_role,
                                                                                              final x10.core.IndexedMemoryChunk<x10.core.Int> result){
            try {x10.x10rt.TeamSupport.nativeSplit(id, role, color, new_role, result);} catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); }
        }
        
        final public static void
          nativeSplit$P__4$1x10$lang$Int$2(
          final int id,
          final int role,
          final int color,
          final int new_role,
          final x10.core.IndexedMemoryChunk<x10.core.Int> result){
            x10.util.Team.nativeSplit__4$1x10$lang$Int$2((int)(id),
                                                         (int)(role),
                                                         (int)(color),
                                                         (int)(new_role),
                                                         ((x10.core.IndexedMemoryChunk)(result)));
        }
        
        
//#line 305 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public void
                                                                                              del(
                                                                                              final int role){
            
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.util.Team t63553 =
              ((x10.util.Team)(x10.util.Team.getInitialized$WORLD()));
            
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final boolean t63555 =
              x10.rtt.Equality.equalsequals((this),(t63553));
            
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
if (t63555) {
                
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.lang.IllegalArgumentException t63554 =
                  ((x10.lang.IllegalArgumentException)(new x10.lang.IllegalArgumentException(((java.lang.String)("Cannot delete Team.WORLD")))));
                
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
throw t63554;
            }
            {
                
//#line 307 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 307 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.lang.FinishState x10$__var23 =
                  x10.lang.Runtime.startFinish();
                
//#line 307 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
try {try {{
                    {
                        
//#line 307 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63556 =
                          id;
                        
//#line 307 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.util.Team.nativeDel((int)(t63556),
                                                                                                                                    (int)(role));
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                    
//#line 307 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                    
//#line 307 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
throw new x10.lang.Exception();
                }finally {{
                     
//#line 307 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var23)));
                 }}
                }
            }
        
        
//#line 310 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final private static void
                                                                                              nativeDel(
                                                                                              final int id,
                                                                                              final int role){
            try {x10.x10rt.TeamSupport.nativeDel(id, role);} catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); }
        }
        
        final public static void
          nativeDel$P(
          final int id,
          final int role){
            x10.util.Team.nativeDel((int)(id),
                                    (int)(role));
        }
        
        
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public java.lang.String
                                                                                              toString(
                                                                                              ){
            
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63557 =
              this.
                id;
            
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final java.lang.String t63558 =
              (("Team(") + ((x10.core.Int.$box(t63557))));
            
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final java.lang.String t63559 =
              ((t63558) + (")"));
            
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63559;
        }
        
        
//#line 316 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public boolean
                                                                                              equals$O(
                                                                                              final x10.util.Team that){
            
//#line 316 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63560 =
              that.
                id;
            
//#line 316 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63561 =
              this.
                id;
            
//#line 316 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final boolean t63562 =
              ((int) t63560) ==
            ((int) t63561);
            
//#line 316 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63562;
        }
        
        
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public boolean
                                                                                              equals(
                                                                                              final java.lang.Object that){
            
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
boolean t63566 =
              x10.util.Team.$RTT.isInstance(that);
            
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
if (t63566) {
                
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.util.Team t63563 =
                  ((x10.util.Team)(((x10.util.Team)x10.rtt.Types.asStruct(x10.util.Team.$RTT,that))));
                
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63564 =
                  t63563.
                    id;
                
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63565 =
                  this.
                    id;
                
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
t63566 = ((int) t63564) ==
                ((int) t63565);
            }
            
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final boolean t63567 =
              t63566;
            
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63567;
        }
        
        
//#line 318 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public int
                                                                                              hashCode(
                                                                                              ){
            
//#line 318 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63568 =
              id;
            
//#line 318 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63568;
        }
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public java.lang.String
                                                                                             typeName(
                                                                                             ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public boolean
                                                                                             _struct_equals$O(
                                                                                             java.lang.Object other){
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final java.lang.Object t63569 =
              other;
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final boolean t63570 =
              x10.util.Team.$RTT.isInstance(t63569);
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final boolean t63571 =
              !(t63570);
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
if (t63571) {
                
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return false;
            }
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final java.lang.Object t63572 =
              other;
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.util.Team t63573 =
              ((x10.util.Team)x10.rtt.Types.asStruct(x10.util.Team.$RTT,t63572));
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final boolean t63574 =
              this._struct_equals$O(((x10.util.Team)(t63573)));
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63574;
        }
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public boolean
                                                                                             _struct_equals$O(
                                                                                             x10.util.Team other){
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63576 =
              this.
                id;
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final x10.util.Team t63575 =
              other;
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final int t63577 =
              t63575.
                id;
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final boolean t63578 =
              ((int) t63576) ==
            ((int) t63577);
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return t63578;
        }
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
final public x10.util.Team
                                                                                             x10$util$Team$$x10$util$Team$this(
                                                                                             ){
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Team.x10"
return x10.util.Team.this;
        }
        
        public static short fieldId$WORLD;
        final public static x10.core.concurrent.AtomicInteger initStatus$WORLD = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        
        public static void
          getDeserialized$WORLD(
          x10.x10rt.X10JavaDeserializer deserializer){
            x10.util.Team.WORLD = ((x10.util.Team)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
            x10.util.Team.initStatus$WORLD.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10.util.Team
          getInitialized$WORLD(
          ){
            if (((int) x10.util.Team.initStatus$WORLD.get()) ==
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                return x10.util.Team.WORLD;
            }
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0) &&
                  x10.util.Team.initStatus$WORLD.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                               (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                x10.util.Team.WORLD = new x10.util.Team((java.lang.System[]) null).$init(((int)(0)));
                if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                      ((boolean) true)) {
                    x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.util.Team.WORLD")));
                }
                x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.util.Team.WORLD)),
                                                                          (short)(x10.util.Team.fieldId$WORLD));
                x10.util.Team.initStatus$WORLD.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            } else {
                if (((int) x10.util.Team.initStatus$WORLD.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) x10.util.Team.initStatus$WORLD.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
            }
            return x10.util.Team.WORLD;
        }
        
        public static int
          getInitialized$ADD(
          ){
            return x10.util.Team.ADD;
        }
        
        public static int
          getInitialized$MUL(
          ){
            return x10.util.Team.MUL;
        }
        
        public static int
          getInitialized$AND(
          ){
            return x10.util.Team.AND;
        }
        
        public static int
          getInitialized$OR(
          ){
            return x10.util.Team.OR;
        }
        
        public static int
          getInitialized$XOR(
          ){
            return x10.util.Team.XOR;
        }
        
        public static int
          getInitialized$MAX(
          ){
            return x10.util.Team.MAX;
        }
        
        public static int
          getInitialized$MIN(
          ){
            return x10.util.Team.MIN;
        }
        
        static {
                   x10.util.Team.fieldId$WORLD = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.util.Team")),
                                                                                                                     ((java.lang.String)("WORLD")))))));
               }
        // synthetic type for parameter mangling
        public abstract static class __0$1x10$lang$Place$2 {}
        
        }
        