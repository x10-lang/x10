package x10.util;


@x10.core.X10Generated public class Random extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Random.class);
    
    public static final x10.rtt.RuntimeType<Random> $RTT = x10.rtt.NamedType.<Random> make(
    "x10.util.Random", /* base class */Random.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Random $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Random.class + " calling"); } 
        $_obj.index = $deserializer.readInt();
        x10.array.Array MT = (x10.array.Array) $deserializer.readRef();
        $_obj.MT = MT;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Random $_obj = new Random((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write(this.index);
        if (MT instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.MT);
        } else {
        $serializer.write(this.MT);
        }
        
    }
    
    // constructor just for allocation
    public Random(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
// creation method for java code (1-phase java constructor)
        public Random(){this((java.lang.System[]) null);
                            $init();}
        
        // constructor for non-virtual call
        final public x10.util.Random x10$util$Random$$init$S() { {
                                                                        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62781 =
                                                                          java.lang.System.currentTimeMillis();
                                                                        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
this.$init(t62781);
                                                                    }
                                                                    return this;
                                                                    }
        
        // constructor
        public x10.util.Random $init(){return x10$util$Random$$init$S();}
        
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
// creation method for java code (1-phase java constructor)
        public Random(final long seed){this((java.lang.System[]) null);
                                           $init(seed);}
        
        // constructor for non-virtual call
        final public x10.util.Random x10$util$Random$$init$S(final long seed) { {
                                                                                       
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"

                                                                                       
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"

                                                                                       
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
this.__fieldInitializers62216();
                                                                                       
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
this.setSeed((long)(seed));
                                                                                   }
                                                                                   return this;
                                                                                   }
        
        // constructor
        public x10.util.Random $init(final long seed){return x10$util$Random$$init$S(seed);}
        
        
        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final public void
                                                                                               setSeed(
                                                                                               final long seed){
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
this.init((long)(seed));
        }
        
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
public int
                                                                                               nextInt$O(
                                                                                               ){
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62782 =
              this.random$O();
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
return t62782;
        }
        
        
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
public int
                                                                                               nextInt$O(
                                                                                               final int maxPlus1){
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final boolean t62783 =
              ((maxPlus1) <= (((int)(0))));
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
if (t62783) {
                
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
return 0;
            }
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
int n =
              maxPlus1;
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62785 =
              n;
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62784 =
              n;
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62786 =
              (-(t62784));
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62787 =
              ((t62785) & (((int)(t62786))));
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62788 =
              n;
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final boolean t62793 =
              ((int) t62787) ==
            ((int) t62788);
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
if (t62793) {
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62790 =
                  this.nextInt$O();
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62789 =
                  n;
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62791 =
                  ((t62789) - (((int)(1))));
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62792 =
                  ((t62790) & (((int)(t62791))));
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
return t62792;
            }
            
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
int mask =
              1;
            
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
while (true) {
                
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62795 =
                  n;
                
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62794 =
                  mask;
                
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62796 =
                  (~(t62794));
                
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62797 =
                  ((t62795) & (((int)(t62796))));
                
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final boolean t62802 =
                  ((int) t62797) !=
                ((int) 0);
                
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
if (!(t62802)) {
                    
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
break;
                }
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63003 =
                  mask;
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63004 =
                  ((t63003) << (((int)(1))));
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
mask = t63004;
                
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63005 =
                  mask;
                
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63006 =
                  ((t63005) | (((int)(1))));
                
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
mask = t63006;
            }
            
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
int x =
               0;
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
boolean t63010 =
              false;
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
do  {
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63007 =
                  this.nextInt$O();
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63008 =
                  mask;
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63009 =
                  ((t63007) & (((int)(t63008))));
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
x = t63009;
                
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63011 =
                  x;
                
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63012 =
                  n;
                
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final boolean t63013 =
                  ((t63011) >= (((int)(t63012))));
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
t63010 = t63013;
            }while(t63010); 
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62810 =
              x;
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
return t62810;
        }
        
        
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
public void
                                                                                               nextBytes__0$1x10$lang$Byte$2(
                                                                                               final x10.array.Array buf){
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
int i =
              0;
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
while (true) {
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
int x =
                  this.nextInt$O();
                
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
int j63027 =
                  0;
                
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
for (;
                                                                                                          true;
                                                                                                          ) {
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63028 =
                      j63027;
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final boolean t63029 =
                      ((t63028) < (((int)(4))));
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
if (!(t63029)) {
                        
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
break;
                    }
                    
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63014 =
                      i;
                    
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63015 =
                      ((x10.array.Array<x10.core.Byte>)buf).
                        size;
                    
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final boolean t63016 =
                      ((t63014) >= (((int)(t63015))));
                    
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
if (t63016) {
                        
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
return;
                    }
                    
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63017 =
                      i;
                    
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63018 =
                      x;
                    
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63019 =
                      ((t63018) & (((int)(255))));
                    
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final byte t63020 =
                      ((byte)(int)(((int)(t63019))));
                    
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
((x10.array.Array<x10.core.Byte>)buf).$set__1x10$array$Array$$T$G((int)(t63017),
                                                                                                                                                                           x10.core.Byte.$box(t63020));
                    
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63021 =
                      i;
                    
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63022 =
                      ((t63021) + (((int)(1))));
                    
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
i = t63022;
                    
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63023 =
                      x;
                    
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63024 =
                      ((t63023) >> (((int)(8))));
                    
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
x = t63024;
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63025 =
                      j63027;
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63026 =
                      ((t63025) + (((int)(1))));
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
j63027 = t63026;
                }
            }
        }
        
        
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
public long
                                                                                               nextLong$O(
                                                                                               ){
            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62827 =
              this.nextInt$O();
            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62828 =
              ((long)(((int)(t62827))));
            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62831 =
              ((t62828) << (((int)(32))));
            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62829 =
              this.nextInt$O();
            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62830 =
              ((long)(((int)(t62829))));
            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62832 =
              ((t62830) & (((long)(4294967295L))));
            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62833 =
              ((t62831) | (((long)(t62832))));
            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
return t62833;
        }
        
        
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
public long
                                                                                               nextLong$O(
                                                                                               final long maxPlus1){
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62834 =
              ((long)(((int)(0))));
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final boolean t62835 =
              ((maxPlus1) <= (((long)(t62834))));
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
if (t62835) {
                
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
return 0L;
            }
            
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
long n =
              maxPlus1;
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62837 =
              n;
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62836 =
              n;
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62838 =
              (-(t62836));
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62839 =
              ((t62837) & (((long)(t62838))));
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62840 =
              n;
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final boolean t62846 =
              ((long) t62839) ==
            ((long) t62840);
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
if (t62846) {
                
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62843 =
                  this.nextLong$O();
                
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62841 =
                  n;
                
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62842 =
                  ((long)(((int)(1))));
                
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62844 =
                  ((t62841) - (((long)(t62842))));
                
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62845 =
                  ((t62843) & (((long)(t62844))));
                
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
return t62845;
            }
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
long mask =
              1L;
            
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
while (true) {
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62848 =
                  n;
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62847 =
                  mask;
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62849 =
                  ((long) ~(t62847));
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62850 =
                  ((t62848) & (((long)(t62849))));
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final boolean t62855 =
                  ((long) t62850) !=
                ((long) 0L);
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
if (!(t62855)) {
                    
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
break;
                }
                
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t63030 =
                  mask;
                
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t63031 =
                  ((t63030) << (((int)(1))));
                
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
mask = t63031;
                
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t63032 =
                  mask;
                
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t63033 =
                  ((t63032) | (((long)(1L))));
                
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
mask = t63033;
            }
            
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
long x =
               0;
            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
boolean t63037 =
              false;
            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
do  {
                
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t63034 =
                  this.nextLong$O();
                
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t63035 =
                  mask;
                
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t63036 =
                  ((t63034) & (((long)(t63035))));
                
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
x = t63036;
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t63038 =
                  x;
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t63039 =
                  n;
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final boolean t63040 =
                  ((t63038) >= (((long)(t63039))));
                
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
t63037 = t63040;
            }while(t63037); 
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62863 =
              x;
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
return t62863;
        }
        
        
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
public boolean
                                                                                                nextBoolean$O(
                                                                                                ){
            
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62864 =
              this.nextInt$O();
            
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final boolean t62865 =
              ((t62864) < (((int)(0))));
            
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
return t62865;
        }
        
        
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
public float
                                                                                                nextFloat$O(
                                                                                                ){
            
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62866 =
              this.nextInt$O();
            
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62867 =
              8;
            
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62868 =
              ((t62866) >>> (((int)(t62867))));
            
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final float t62870 =
              ((float)(int)(((int)(t62868))));
            
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62869 =
              16777216;
            
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final float t62871 =
              ((float)(int)(((int)(t62869))));
            
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final float t62872 =
              ((t62870) / (((float)(t62871))));
            
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
return t62872;
        }
        
        
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
public double
                                                                                                nextDouble$O(
                                                                                                ){
            
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62873 =
              this.nextLong$O();
            
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62874 =
              11;
            
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62875 =
              ((t62873) >>> (((int)(t62874))));
            
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final double t62877 =
              ((double)(long)(((long)(t62875))));
            
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62876 =
              9007199254740992L;
            
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final double t62878 =
              ((double)(long)(((long)(t62876))));
            
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final double t62879 =
              ((t62877) / (((double)(t62878))));
            
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
return t62879;
        }
        
        
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final public static int N = 624;
        
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final public static int M = 397;
        
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
public int index;
        
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
public x10.array.Array<x10.core.Int> MT;
        
        
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final public void
                                                                                                init(
                                                                                                final long seed){
            
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62880 =
              x10.util.Random.N;
            
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final x10.array.Array<x10.core.Int> mt =
              ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t62880)))));
            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
this.MT = ((x10.array.Array)(mt));
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final boolean t62881 =
              ((long) seed) ==
            ((long) 0L);
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
if (t62881) {
                
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
this.init((long)(4357L));
                
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
return;
            }
            
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t62882 =
              seed;
            
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62883 =
              ((int)(long)(((long)(t62882))));
            
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
((x10.array.Array<x10.core.Int>)mt).$set__1x10$array$Array$$T$G((int)(0),
                                                                                                                                                                  x10.core.Int.$box(t62883));
            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
int i63052 =
              1;
            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
for (;
                                                                                                       true;
                                                                                                       ) {
                
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63053 =
                  i63052;
                
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63054 =
                  x10.util.Random.N;
                
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final boolean t63055 =
                  ((t63053) < (((int)(t63054))));
                
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
if (!(t63055)) {
                    
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
break;
                }
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63041 =
                  i63052;
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63042 =
                  i63052;
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63043 =
                  ((t63042) - (((int)(1))));
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63044 =
                  x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)mt).$apply$G((int)(t63043)));
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t63045 =
                  ((long)(((int)(t63044))));
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t63046 =
                  ((69069L) * (((long)(t63045))));
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t63047 =
                  ((long)(((int)(1))));
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final long t63048 =
                  ((t63046) + (((long)(t63047))));
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63049 =
                  ((int)(long)(((long)(t63048))));
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
((x10.array.Array<x10.core.Int>)mt).$set__1x10$array$Array$$T$G((int)(t63041),
                                                                                                                                                                      x10.core.Int.$box(t63049));
                
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63050 =
                  i63052;
                
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63051 =
                  ((t63050) + (((int)(1))));
                
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
i63052 = t63051;
            }
            
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
this.index = 0;
            
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
x10.util.Random.twist__0$1x10$lang$Int$2(((x10.array.Array)(mt)));
        }
        
        
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
public int
                                                                                                random$O(
                                                                                                ){
            
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62899 =
              index;
            
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62900 =
              x10.util.Random.N;
            
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final boolean t62902 =
              ((int) t62899) ==
            ((int) t62900);
            
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
if (t62902) {
                
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
this.index = 0;
                
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final x10.array.Array<x10.core.Int> t62901 =
                  ((x10.array.Array)(MT));
                
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
x10.util.Random.twist__0$1x10$lang$Int$2(((x10.array.Array)(t62901)));
            }
            
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final x10.array.Array<x10.core.Int> t62906 =
              ((x10.array.Array)(MT));
            
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final x10.util.Random x62779 =
              ((x10.util.Random)(this));
            
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
;
            
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62903 =
              x62779.
                index;
            
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62904 =
              ((t62903) + (((int)(1))));
            
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62905 =
              x62779.index = t62904;
            
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62907 =
              ((t62905) - (((int)(1))));
            
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
int y =
              x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t62906).$apply$G((int)(t62907)));
            
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62909 =
              y;
            
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62908 =
              y;
            
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62910 =
              ((t62908) >>> (((int)(11))));
            
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62911 =
              ((t62909) ^ (((int)(t62910))));
            
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
y = t62911;
            
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62914 =
              y;
            
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62912 =
              y;
            
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62913 =
              ((t62912) << (((int)(7))));
            
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62915 =
              ((t62913) & (((int)(-1658038656))));
            
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62916 =
              ((t62914) ^ (((int)(t62915))));
            
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
y = t62916;
            
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62919 =
              y;
            
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62917 =
              y;
            
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62918 =
              ((t62917) << (((int)(15))));
            
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62920 =
              ((t62918) & (((int)(-272236544))));
            
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62921 =
              ((t62919) ^ (((int)(t62920))));
            
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
y = t62921;
            
//#line 170 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62923 =
              y;
            
//#line 170 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62922 =
              y;
            
//#line 170 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62924 =
              ((t62922) >>> (((int)(18))));
            
//#line 170 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62925 =
              ((t62923) ^ (((int)(t62924))));
            
//#line 170 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
y = t62925;
            
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62926 =
              y;
            
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
return t62926;
        }
        
        
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
private static void
                                                                                                twist__0$1x10$lang$Int$2(
                                                                                                final x10.array.Array<x10.core.Int> MT){
            
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
int i =
              0;
            
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
int s =
               0;
            
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
for (;
                                                                                                       true;
                                                                                                       ) {
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63102 =
                  i;
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63103 =
                  x10.util.Random.N;
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63104 =
                  x10.util.Random.M;
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63105 =
                  227;
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final boolean t63106 =
                  ((t63102) < (((int)(t63105))));
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
if (!(t63106)) {
                    
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
break;
                }
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63056 =
                  i;
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63057 =
                  x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)MT).$apply$G((int)(t63056)));
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63058 =
                  ((t63057) & (((int)(-2147483648))));
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63059 =
                  i;
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63060 =
                  ((t63059) + (((int)(1))));
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63061 =
                  x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)MT).$apply$G((int)(t63060)));
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63062 =
                  ((t63061) & (((int)(2147483647))));
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63063 =
                  ((t63058) | (((int)(t63062))));
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
s = t63063;
                
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63064 =
                  i;
                
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63065 =
                  i;
                
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63066 =
                  x10.util.Random.M;
                
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63067 =
                  ((t63065) + (((int)(t63066))));
                
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63068 =
                  x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)MT).$apply$G((int)(t63067)));
                
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63069 =
                  s;
                
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63070 =
                  ((t63069) >>> (((int)(1))));
                
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63071 =
                  ((t63068) ^ (((int)(t63070))));
                
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63072 =
                  s;
                
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63073 =
                  ((t63072) & (((int)(1))));
                
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63074 =
                  ((t63073) * (((int)(-1727483681))));
                
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63075 =
                  ((t63071) ^ (((int)(t63074))));
                
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
((x10.array.Array<x10.core.Int>)MT).$set__1x10$array$Array$$T$G((int)(t63064),
                                                                                                                                                                      x10.core.Int.$box(t63075));
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63076 =
                  i;
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63077 =
                  ((t63076) + (((int)(1))));
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
i = t63077;
            }
            
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
for (;
                                                                                                       true;
                                                                                                       ) {
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63107 =
                  i;
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63108 =
                  x10.util.Random.N;
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63109 =
                  623;
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final boolean t63110 =
                  ((t63107) < (((int)(t63109))));
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
if (!(t63110)) {
                    
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
break;
                }
                
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63078 =
                  i;
                
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63079 =
                  x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)MT).$apply$G((int)(t63078)));
                
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63080 =
                  ((t63079) & (((int)(-2147483648))));
                
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63081 =
                  i;
                
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63082 =
                  ((t63081) + (((int)(1))));
                
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63083 =
                  x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)MT).$apply$G((int)(t63082)));
                
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63084 =
                  ((t63083) & (((int)(2147483647))));
                
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63085 =
                  ((t63080) | (((int)(t63084))));
                
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
s = t63085;
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63086 =
                  i;
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63087 =
                  i;
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63088 =
                  x10.util.Random.N;
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63089 =
                  x10.util.Random.M;
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63090 =
                  227;
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63091 =
                  ((t63087) - (((int)(t63090))));
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63092 =
                  x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)MT).$apply$G((int)(t63091)));
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63093 =
                  s;
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63094 =
                  ((t63093) >>> (((int)(1))));
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63095 =
                  ((t63092) ^ (((int)(t63094))));
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63096 =
                  s;
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63097 =
                  ((t63096) & (((int)(1))));
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63098 =
                  ((t63097) * (((int)(-1727483681))));
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63099 =
                  ((t63095) ^ (((int)(t63098))));
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
((x10.array.Array<x10.core.Int>)MT).$set__1x10$array$Array$$T$G((int)(t63086),
                                                                                                                                                                      x10.core.Int.$box(t63099));
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63100 =
                  i;
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63101 =
                  ((t63100) + (((int)(1))));
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
i = t63101;
            }
            
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62984 =
              x10.util.Random.N;
            
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62985 =
              623;
            
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62986 =
              x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)MT).$apply$G((int)(t62985)));
            
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62988 =
              ((t62986) & (((int)(-2147483648))));
            
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62987 =
              x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)MT).$apply$G((int)(0)));
            
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62989 =
              ((t62987) & (((int)(2147483647))));
            
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62990 =
              ((t62988) | (((int)(t62989))));
            
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
s = t62990;
            
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62991 =
              x10.util.Random.N;
            
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63001 =
              623;
            
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62992 =
              x10.util.Random.M;
            
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62993 =
              396;
            
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62995 =
              x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)MT).$apply$G((int)(t62993)));
            
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62994 =
              s;
            
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62996 =
              ((t62994) >>> (((int)(1))));
            
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62999 =
              ((t62995) ^ (((int)(t62996))));
            
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62997 =
              s;
            
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t62998 =
              ((t62997) & (((int)(1))));
            
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63000 =
              ((t62998) * (((int)(-1727483681))));
            
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final int t63002 =
              ((t62999) ^ (((int)(t63000))));
            
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
((x10.array.Array<x10.core.Int>)MT).$set__1x10$array$Array$$T$G((int)(t63001),
                                                                                                                                                                  x10.core.Int.$box(t63002));
        }
        
        public static void
          twist$P__0$1x10$lang$Int$2(
          final x10.array.Array<x10.core.Int> MT){
            x10.util.Random.twist__0$1x10$lang$Int$2(((x10.array.Array)(MT)));
        }
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final public x10.util.Random
                                                                                               x10$util$Random$$x10$util$Random$this(
                                                                                               ){
            
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
return x10.util.Random.this;
        }
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
final public void
                                                                                               __fieldInitializers62216(
                                                                                               ){
            
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
this.index = 0;
            
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Random.x10"
this.MT = null;
        }
        
        public static int
          getInitialized$N(
          ){
            return x10.util.Random.N;
        }
        
        public static int
          getInitialized$M(
          ){
            return x10.util.Random.M;
        }
    
}
