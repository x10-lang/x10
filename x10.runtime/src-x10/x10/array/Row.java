package x10.array;


@x10.core.X10Generated abstract public class Row extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Row.class);
    
    public static final x10.rtt.RuntimeType<Row> $RTT = x10.rtt.NamedType.<Row> make(
    "x10.array.Row", /* base class */Row.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Row $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Row.class + " calling"); } 
        $_obj.cols = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        return null;
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write(this.cols);
        
    }
    
    // constructor just for allocation
    public Row(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1){}:U
    public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
    return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
    }
    
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
public int cols;
        
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
abstract public int
                                                                                             $apply$O(
                                                                                             final int i);
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
abstract public int
                                                                                             $set$O(
                                                                                             final int i,
                                                                                             final int v);
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"

        // constructor for non-virtual call
        final public x10.array.Row x10$array$Row$$init$S(final int cols) { {
                                                                                  
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"

                                                                                  
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
this.cols = cols;
                                                                                  
                                                                              }
                                                                              return this;
                                                                              }
        
        // constructor
        public x10.array.Row $init(final int cols){return x10$array$Row$$init$S(cols);}
        
        
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
public void
                                                                                             printInfo(
                                                                                             final x10.io.Printer ps,
                                                                                             final int row){
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
ps.print(((java.lang.String)("[")));
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
int i47936 =
              0;
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
for (;
                                                                                                    true;
                                                                                                    ) {
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int t47937 =
                  i47936;
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int t47938 =
                  cols;
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final boolean t47939 =
                  ((t47937) < (((int)(t47938))));
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
if (!(t47939)) {
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
break;
                }
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int t47928 =
                  i47936;
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int t47929 =
                  this.$apply$O((int)(t47928));
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
ps.print(x10.core.Int.$box(t47929));
                
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int t47930 =
                  i47936;
                
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int t47931 =
                  cols;
                
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int t47932 =
                  ((t47931) - (((int)(2))));
                
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final boolean t47933 =
                  ((int) t47930) ==
                ((int) t47932);
                
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
if (t47933) {
                    
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
ps.print(((java.lang.String)(" |")));
                }
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int t47934 =
                  i47936;
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int t47935 =
                  ((t47934) + (((int)(1))));
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
i47936 = t47935;
            }
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
ps.print(((java.lang.String)(" ]   ")));
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
this.printEqn(((x10.io.Printer)(ps)),
                                                                                                             ((java.lang.String)(" ")),
                                                                                                             (int)(row));
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
ps.println();
        }
        
        
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
public void
                                                                                             printEqn(
                                                                                             final x10.io.Printer ps,
                                                                                             final java.lang.String spc,
                                                                                             final int row){
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
boolean first =
              true;
            
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final java.lang.String t47886 =
              (("y") + ((x10.core.Int.$box(row))));
            
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final java.lang.String t47887 =
              ((t47886) + (" = "));
            
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
ps.print(((java.lang.String)(t47887)));
            
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
int i47965 =
              0;
            
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
for (;
                                                                                                    true;
                                                                                                    ) {
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int t47966 =
                  i47965;
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int t47967 =
                  cols;
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int t47968 =
                  ((t47967) - (((int)(1))));
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final boolean t47969 =
                  ((t47966) < (((int)(t47968))));
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
if (!(t47969)) {
                    
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
break;
                }
                
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int t47940 =
                  i47965;
                
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int c47941 =
                  this.$apply$O((int)(t47940));
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final boolean t47942 =
                  ((int) c47941) ==
                ((int) 1);
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
if (t47942) {
                    
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final boolean t47943 =
                      first;
                    
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
if (t47943) {
                        
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int t47944 =
                          i47965;
                        
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final java.lang.String t47945 =
                          (("x") + ((x10.core.Int.$box(t47944))));
                        
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
ps.print(((java.lang.String)(t47945)));
                    } else {
                        
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int t47946 =
                          i47965;
                        
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final java.lang.String t47947 =
                          (("+x") + ((x10.core.Int.$box(t47946))));
                        
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
ps.print(((java.lang.String)(t47947)));
                    }
                } else {
                    
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final boolean t47948 =
                      ((int) c47941) ==
                    ((int) -1);
                    
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
if (t47948) {
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int t47949 =
                          i47965;
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final java.lang.String t47950 =
                          (("-x") + ((x10.core.Int.$box(t47949))));
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
ps.print(((java.lang.String)(t47950)));
                    } else {
                        
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final boolean t47951 =
                          ((int) c47941) !=
                        ((int) 0);
                        
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
if (t47951) {
                            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
boolean t47952 =
                              ((c47941) >= (((int)(0))));
                            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
if (t47952) {
                                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final boolean t47953 =
                                  first;
                                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
t47952 = !(t47953);
                            }
                            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final boolean t47954 =
                              t47952;
                            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
java.lang.String t47955 =
                               null;
                            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
if (t47954) {
                                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
t47955 = "+";
                            } else {
                                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
t47955 = "";
                            }
                            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final java.lang.String t47956 =
                              t47955;
                            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final java.lang.String t47957 =
                              ((t47956) + ((x10.core.Int.$box(c47941))));
                            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final java.lang.String t47958 =
                              ((t47957) + ("*x"));
                            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int t47959 =
                              i47965;
                            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final java.lang.String t47960 =
                              ((t47958) + ((x10.core.Int.$box(t47959))));
                            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final java.lang.String t47961 =
                              ((t47960) + (" "));
                            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
ps.print(((java.lang.String)(t47961)));
                        }
                    }
                }
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final boolean t47962 =
                  ((int) c47941) !=
                ((int) 0);
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
if (t47962) {
                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
first = false;
                }
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int t47963 =
                  i47965;
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int t47964 =
                  ((t47963) + (((int)(1))));
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
i47965 = t47964;
            }
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int t47917 =
              cols;
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int t47918 =
              ((t47917) - (((int)(1))));
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final int c =
              this.$apply$O((int)(t47918));
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
boolean t47919 =
              ((int) c) !=
            ((int) 0);
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
if (!(t47919)) {
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
t47919 = first;
            }
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final boolean t47926 =
              t47919;
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
if (t47926) {
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
boolean t47921 =
                  ((c) >= (((int)(0))));
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
if (t47921) {
                    
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final boolean t47920 =
                      first;
                    
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
t47921 = !(t47920);
                }
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final boolean t47922 =
                  t47921;
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
java.lang.String t47923 =
                   null;
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
if (t47922) {
                    
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
t47923 = "+";
                } else {
                    
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
t47923 = "";
                }
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final java.lang.String t47924 =
                  t47923;
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final java.lang.String t47925 =
                  ((t47924) + ((x10.core.Int.$box(c))));
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
ps.print(((java.lang.String)(t47925)));
            }
        }
        
        
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
public java.lang.String
                                                                                             toString(
                                                                                             ){
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final x10.io.StringWriter os =
              ((x10.io.StringWriter)(new x10.io.StringWriter((java.lang.System[]) null).$init()));
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final x10.io.Printer ps =
              ((x10.io.Printer)(new x10.io.Printer((java.lang.System[]) null).$init(((x10.io.Writer)(os)))));
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
this.printEqn(((x10.io.Printer)(ps)),
                                                                                                             ((java.lang.String)("")),
                                                                                                             (int)(0));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final java.lang.String t47927 =
              os.result$O();
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
return t47927;
        }
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
final public x10.array.Row
                                                                                             x10$array$Row$$x10$array$Row$this(
                                                                                             ){
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Row.x10"
return x10.array.Row.this;
        }
    
}
