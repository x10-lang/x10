package x10.array;


@x10.core.X10Generated public class PolyRow extends x10.array.ValRow implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, PolyRow.class);
    
    public static final x10.rtt.RuntimeType<PolyRow> $RTT = x10.rtt.NamedType.<PolyRow> make(
    "x10.array.PolyRow", /* base class */PolyRow.class
    , /* parents */ new x10.rtt.Type[] {x10.array.ValRow.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(PolyRow $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + PolyRow.class + " calling"); } 
        x10.array.ValRow.$_deserialize_body($_obj, $deserializer);
        $_obj.rank = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        PolyRow $_obj = new PolyRow((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        $serializer.write(this.rank);
        
    }
    
    // constructor just for allocation
    public PolyRow(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
public int rank;
        
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
// creation method for java code (1-phase java constructor)
        public PolyRow(final x10.array.Array<x10.core.Int> as_, __0$1x10$lang$Int$2 $dummy){this((java.lang.System[]) null);
                                                                                                $init(as_, (x10.array.PolyRow.__0$1x10$lang$Int$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.PolyRow x10$array$PolyRow$$init$S(final x10.array.Array<x10.core.Int> as_, __0$1x10$lang$Int$2 $dummy) { {
                                                                                                                                               
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42679 =
                                                                                                                                                 ((x10.array.Array<x10.core.Int>)as_).
                                                                                                                                                   size;
                                                                                                                                               
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42680 =
                                                                                                                                                 ((t42679) - (((int)(1))));
                                                                                                                                               
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
this.$init(((x10.array.Array)(as_)),
                                                                                                                                                                                                                                                 t42680, (x10.array.PolyRow.__0$1x10$lang$Int$2) null);
                                                                                                                                           }
                                                                                                                                           return this;
                                                                                                                                           }
        
        // constructor
        public x10.array.PolyRow $init(final x10.array.Array<x10.core.Int> as_, __0$1x10$lang$Int$2 $dummy){return x10$array$PolyRow$$init$S(as_, $dummy);}
        
        
        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
// creation method for java code (1-phase java constructor)
        public PolyRow(final x10.array.Array<x10.core.Int> as_,
                       final int n, __0$1x10$lang$Int$2 $dummy){this((java.lang.System[]) null);
                                                                    $init(as_,n, (x10.array.PolyRow.__0$1x10$lang$Int$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.PolyRow x10$array$PolyRow$$init$S(final x10.array.Array<x10.core.Int> as_,
                                                                 final int n, __0$1x10$lang$Int$2 $dummy) { {
                                                                                                                   
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
super.$init(((x10.array.Array)(as_)), (x10.array.ValRow.__0$1x10$lang$Int$2) null);
                                                                                                                   
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
this.rank = n;
                                                                                                                   
                                                                                                               }
                                                                                                               return this;
                                                                                                               }
        
        // constructor
        public x10.array.PolyRow $init(final x10.array.Array<x10.core.Int> as_,
                                       final int n, __0$1x10$lang$Int$2 $dummy){return x10$array$PolyRow$$init$S(as_,n, $dummy);}
        
        
        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
// creation method for java code (1-phase java constructor)
        public PolyRow(final x10.array.Point p,
                       final int k){this((java.lang.System[]) null);
                                        $init(p,k);}
        
        // constructor for non-virtual call
        final public x10.array.PolyRow x10$array$PolyRow$$init$S(final x10.array.Point p,
                                                                 final int k) { {
                                                                                       
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42811 =
                                                                                         p.
                                                                                           rank;
                                                                                       
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42812 =
                                                                                         ((t42811) + (((int)(1))));
                                                                                       
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t42813 =
                                                                                         ((x10.core.fun.Fun_0_1)(new x10.array.PolyRow.$Closure$58(p,
                                                                                                                                                   k)));
                                                                                       
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
super.$init(t42812,
                                                                                                                                                                                          ((x10.core.fun.Fun_0_1)(t42813)), (x10.array.ValRow.__1$1x10$lang$Int$3x10$lang$Int$2) null);
                                                                                       
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42810 =
                                                                                         p.
                                                                                           rank;
                                                                                       
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
this.rank = t42810;
                                                                                       
                                                                                   }
                                                                                   return this;
                                                                                   }
        
        // constructor
        public x10.array.PolyRow $init(final x10.array.Point p,
                                       final int k){return x10$array$PolyRow$$init$S(p,k);}
        
        
        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
// creation method for java code (1-phase java constructor)
        public PolyRow(final int cols,
                       final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> init, __1$1x10$lang$Int$3x10$lang$Int$2 $dummy){this((java.lang.System[]) null);
                                                                                                                                 $init(cols,init, (x10.array.PolyRow.__1$1x10$lang$Int$3x10$lang$Int$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.PolyRow x10$array$PolyRow$$init$S(final int cols,
                                                                 final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> init, __1$1x10$lang$Int$3x10$lang$Int$2 $dummy) { {
                                                                                                                                                                                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
super.$init(((int)(cols)),
                                                                                                                                                                                                                                                                                   ((x10.core.fun.Fun_0_1)(init)), (x10.array.ValRow.__1$1x10$lang$Int$3x10$lang$Int$2) null);
                                                                                                                                                                                
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int cols1 =
                                                                                                                                                                                  ((cols) - (((int)(1))));
                                                                                                                                                                                
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
this.rank = cols1;
                                                                                                                                                                                
                                                                                                                                                                            }
                                                                                                                                                                            return this;
                                                                                                                                                                            }
        
        // constructor
        public x10.array.PolyRow $init(final int cols,
                                       final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> init, __1$1x10$lang$Int$3x10$lang$Int$2 $dummy){return x10$array$PolyRow$$init$S(cols,init, $dummy);}
        
        
        
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
public static int
                                                                                                 compare$O(
                                                                                                 final x10.array.Row a,
                                                                                                 final x10.array.Row b){
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
int i42831 =
              0;
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
for (;
                                                                                                        true;
                                                                                                        ) {
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42832 =
                  i42831;
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42833 =
                  a.
                    cols;
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42834 =
                  ((t42832) < (((int)(t42833))));
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (!(t42834)) {
                    
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
break;
                }
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42819 =
                  i42831;
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42820 =
                  a.$apply$O((int)(t42819));
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42821 =
                  i42831;
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42822 =
                  b.$apply$O((int)(t42821));
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42823 =
                  ((t42820) < (((int)(t42822))));
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (t42823) {
                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
return -1;
                } else {
                    
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42824 =
                      i42831;
                    
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42825 =
                      a.$apply$O((int)(t42824));
                    
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42826 =
                      i42831;
                    
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42827 =
                      b.$apply$O((int)(t42826));
                    
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42828 =
                      ((t42825) > (((int)(t42827))));
                    
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (t42828) {
                        
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
return 1;
                    }
                }
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42829 =
                  i42831;
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42830 =
                  ((t42829) + (((int)(1))));
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
i42831 = t42830;
            }
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
return 0;
        }
        
        
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
public boolean
                                                                                                 isParallel$O(
                                                                                                 final x10.array.PolyRow that){
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
int i42842 =
              0;
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
for (;
                                                                                                        true;
                                                                                                        ) {
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42843 =
                  i42842;
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42844 =
                  cols;
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42845 =
                  ((t42844) - (((int)(1))));
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42846 =
                  ((t42843) < (((int)(t42845))));
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (!(t42846)) {
                    
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
break;
                }
                
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42835 =
                  i42842;
                
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42836 =
                  this.$apply$O((int)(t42835));
                
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42837 =
                  i42842;
                
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42838 =
                  that.$apply$O((int)(t42837));
                
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42839 =
                  ((int) t42836) !=
                ((int) t42838);
                
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (t42839) {
                    
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
return false;
                }
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42840 =
                  i42842;
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42841 =
                  ((t42840) + (((int)(1))));
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
i42842 = t42841;
            }
            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
return true;
        }
        
        
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
public boolean
                                                                                                 isRect$O(
                                                                                                 ){
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
boolean nz =
              false;
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
int i42853 =
              0;
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
for (;
                                                                                                        true;
                                                                                                        ) {
                
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42854 =
                  i42853;
                
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42855 =
                  cols;
                
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42856 =
                  ((t42855) - (((int)(1))));
                
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42857 =
                  ((t42854) < (((int)(t42856))));
                
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (!(t42857)) {
                    
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
break;
                }
                
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42847 =
                  i42853;
                
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42848 =
                  this.$apply$O((int)(t42847));
                
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42849 =
                  ((int) t42848) !=
                ((int) 0);
                
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (t42849) {
                    
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42850 =
                      nz;
                    
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (t42850) {
                        
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
return false;
                    }
                    
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
nz = true;
                }
                
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42851 =
                  i42853;
                
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42852 =
                  ((t42851) + (((int)(1))));
                
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
i42853 = t42852;
            }
            
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
return true;
        }
        
        
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
public boolean
                                                                                                  contains$O(
                                                                                                  final x10.array.Point p){
            
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42728 =
              rank;
            
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
int sum =
              this.$apply$O((int)(t42728));
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
int i42867 =
              0;
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
for (;
                                                                                                         true;
                                                                                                         ) {
                
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42868 =
                  i42867;
                
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42869 =
                  rank;
                
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42870 =
                  ((t42868) < (((int)(t42869))));
                
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (!(t42870)) {
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
break;
                }
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42858 =
                  sum;
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42859 =
                  i42867;
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42860 =
                  this.$apply$O((int)(t42859));
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42861 =
                  i42867;
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42862 =
                  p.$apply$O((int)(t42861));
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42863 =
                  ((t42860) * (((int)(t42862))));
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42864 =
                  ((t42858) + (((int)(t42863))));
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
sum = t42864;
                
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42865 =
                  i42867;
                
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42866 =
                  ((t42865) + (((int)(1))));
                
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
i42867 = t42866;
            }
            
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42742 =
              sum;
            
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42743 =
              ((t42742) <= (((int)(0))));
            
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
return t42743;
        }
        
        
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
public x10.array.PolyRow
                                                                                                  complement(
                                                                                                  ){
            
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> init =
              ((x10.core.fun.Fun_0_1)(new x10.array.PolyRow.$Closure$59(this,
                                                                        rank)));
            
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42752 =
              rank;
            
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42753 =
              ((t42752) + (((int)(1))));
            
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final x10.array.Array<x10.core.Int> as_ =
              ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(t42753,
                                                                                                                       ((x10.core.fun.Fun_0_1)(init)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
            
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final x10.array.PolyRow t42754 =
              ((x10.array.PolyRow)(new x10.array.PolyRow((java.lang.System[]) null).$init(((x10.array.Array)(as_)), (x10.array.PolyRow.__0$1x10$lang$Int$2) null)));
            
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
return t42754;
        }
        
        
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
public void
                                                                                                  printEqn(
                                                                                                  final x10.io.Printer ps,
                                                                                                  final java.lang.String spc,
                                                                                                  final int row){
            
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
int sgn =
              0;
            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
boolean first =
              true;
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
int i42906 =
              0;
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
for (;
                                                                                                         true;
                                                                                                         ) {
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42907 =
                  i42906;
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42908 =
                  cols;
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42909 =
                  ((t42908) - (((int)(1))));
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42910 =
                  ((t42907) < (((int)(t42909))));
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (!(t42910)) {
                    
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
break;
                }
                
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42871 =
                  sgn;
                
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42872 =
                  ((int) t42871) ==
                ((int) 0);
                
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (t42872) {
                    
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42873 =
                      i42906;
                    
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42874 =
                      this.$apply$O((int)(t42873));
                    
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42875 =
                      ((t42874) < (((int)(0))));
                    
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (t42875) {
                        
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
sgn = -1;
                    } else {
                        
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42876 =
                          i42906;
                        
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42877 =
                          this.$apply$O((int)(t42876));
                        
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42878 =
                          ((t42877) > (((int)(0))));
                        
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (t42878) {
                            
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
sgn = 1;
                        }
                    }
                }
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42879 =
                  sgn;
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42880 =
                  i42906;
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42881 =
                  this.$apply$O((int)(t42880));
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int c42882 =
                  ((t42879) * (((int)(t42881))));
                
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42883 =
                  ((int) c42882) ==
                ((int) 1);
                
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (t42883) {
                    
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42884 =
                      first;
                    
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (t42884) {
                        
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42885 =
                          i42906;
                        
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final java.lang.String t42886 =
                          (("x") + ((x10.core.Int.$box(t42885))));
                        
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
ps.print(((java.lang.String)(t42886)));
                    } else {
                        
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42887 =
                          i42906;
                        
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final java.lang.String t42888 =
                          (("+x") + ((x10.core.Int.$box(t42887))));
                        
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
ps.print(((java.lang.String)(t42888)));
                    }
                } else {
                    
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42889 =
                      ((int) c42882) ==
                    ((int) -1);
                    
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (t42889) {
                        
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42890 =
                          i42906;
                        
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final java.lang.String t42891 =
                          (("-x") + ((x10.core.Int.$box(t42890))));
                        
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
ps.print(((java.lang.String)(t42891)));
                    } else {
                        
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42892 =
                          ((int) c42882) !=
                        ((int) 0);
                        
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (t42892) {
                            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
boolean t42893 =
                              ((c42882) >= (((int)(0))));
                            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (t42893) {
                                
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42894 =
                                  first;
                                
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
t42893 = !(t42894);
                            }
                            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42895 =
                              t42893;
                            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
java.lang.String t42896 =
                               null;
                            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (t42895) {
                                
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
t42896 = "+";
                            } else {
                                
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
t42896 = "";
                            }
                            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final java.lang.String t42897 =
                              t42896;
                            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final java.lang.String t42898 =
                              ((t42897) + ((x10.core.Int.$box(c42882))));
                            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final java.lang.String t42899 =
                              ((t42898) + ("*x"));
                            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42900 =
                              i42906;
                            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final java.lang.String t42901 =
                              ((t42899) + ((x10.core.Int.$box(t42900))));
                            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final java.lang.String t42902 =
                              ((t42901) + (" "));
                            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
ps.print(((java.lang.String)(t42902)));
                        }
                    }
                }
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42903 =
                  ((int) c42882) !=
                ((int) 0);
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (t42903) {
                    
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
first = false;
                }
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42904 =
                  i42906;
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42905 =
                  ((t42904) + (((int)(1))));
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
i42906 = t42905;
            }
            
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42794 =
              first;
            
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (t42794) {
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
ps.print(((java.lang.String)("0")));
            }
            
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42795 =
              sgn;
            
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42809 =
              ((t42795) > (((int)(0))));
            
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (t42809) {
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final java.lang.String t42796 =
                  ((spc) + ("<="));
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final java.lang.String t42800 =
                  ((t42796) + (spc));
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42797 =
                  cols;
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42798 =
                  ((t42797) - (((int)(1))));
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42799 =
                  this.$apply$O((int)(t42798));
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42801 =
                  (-(t42799));
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final java.lang.String t42802 =
                  ((t42800) + ((x10.core.Int.$box(t42801))));
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
ps.print(((java.lang.String)(t42802)));
            } else {
                
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final java.lang.String t42803 =
                  ((spc) + (">="));
                
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final java.lang.String t42806 =
                  ((t42803) + (spc));
                
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42804 =
                  cols;
                
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42805 =
                  ((t42804) - (((int)(1))));
                
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42807 =
                  this.$apply$O((int)(t42805));
                
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final java.lang.String t42808 =
                  ((t42806) + ((x10.core.Int.$box(t42807))));
                
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
ps.print(((java.lang.String)(t42808)));
            }
        }
        
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final public x10.array.PolyRow
                                                                                                 x10$array$PolyRow$$x10$array$PolyRow$this(
                                                                                                 ){
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
return x10.array.PolyRow.this;
        }
        
        @x10.core.X10Generated public static class $Closure$58 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$58.class);
            
            public static final x10.rtt.RuntimeType<$Closure$58> $RTT = x10.rtt.StaticFunType.<$Closure$58> make(
            /* base class */$Closure$58.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$58 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$58.class + " calling"); } 
                x10.array.Point p = (x10.array.Point) $deserializer.readRef();
                $_obj.p = p;
                $_obj.k = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$58 $_obj = new $Closure$58((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (p instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.p);
                } else {
                $serializer.write(this.p);
                }
                $serializer.write(this.k);
                
            }
            
            // constructor just for allocation
            public $Closure$58(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i42814){
                    
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42815 =
                      this.
                        p.
                        rank;
                    
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42816 =
                      ((i42814) < (((int)(t42815))));
                    
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
int t42817 =
                       0;
                    
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (t42816) {
                        
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
t42817 = this.
                                                                                                                          p.$apply$O((int)(i42814));
                    } else {
                        
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
t42817 = this.
                                                                                                                          k;
                    }
                    
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42818 =
                      t42817;
                    
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
return t42818;
                }
                
                public x10.array.Point p;
                public int k;
                
                public $Closure$58(final x10.array.Point p,
                                   final int k) { {
                                                         this.p = ((x10.array.Point)(p));
                                                         this.k = k;
                                                     }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$59 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$59.class);
            
            public static final x10.rtt.RuntimeType<$Closure$59> $RTT = x10.rtt.StaticFunType.<$Closure$59> make(
            /* base class */$Closure$59.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$59 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$59.class + " calling"); } 
                x10.array.PolyRow out$$ = (x10.array.PolyRow) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.rank = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$59 $_obj = new $Closure$59((java.lang.System[]) null);
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
                $serializer.write(this.rank);
                
            }
            
            // constructor just for allocation
            public $Closure$59(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42744 =
                      this.
                        rank;
                    
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final boolean t42749 =
                      ((i) < (((int)(t42744))));
                    
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
int t42750 =
                       0;
                    
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
if (t42749) {
                        
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42745 =
                          this.
                            out$$.$apply$O((int)(i));
                        
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
t42750 = (-(t42745));
                    } else {
                        
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42746 =
                          this.
                            rank;
                        
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42747 =
                          this.
                            out$$.$apply$O((int)(t42746));
                        
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42748 =
                          (-(t42747));
                        
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
t42750 = ((t42748) + (((int)(1))));
                    }
                    
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
final int t42751 =
                      t42750;
                    
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRow.x10"
return t42751;
                }
                
                public x10.array.PolyRow out$$;
                public int rank;
                
                public $Closure$59(final x10.array.PolyRow out$$,
                                   final int rank) { {
                                                            this.out$$ = out$$;
                                                            this.rank = rank;
                                                        }}
                
            }
            
        // synthetic type for parameter mangling
        public abstract static class __0$1x10$lang$Int$2 {}
        // synthetic type for parameter mangling
        public abstract static class __1$1x10$lang$Int$3x10$lang$Int$2 {}
        
        }
        
        