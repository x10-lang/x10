package x10.lang;


@x10.core.X10Generated final public class GlobalCounters extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, GlobalCounters.class);
    
    public static final x10.rtt.RuntimeType<GlobalCounters> $RTT = x10.rtt.NamedType.<GlobalCounters> make(
    "x10.lang.GlobalCounters", /* base class */GlobalCounters.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(GlobalCounters $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + GlobalCounters.class + " calling"); } 
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        GlobalCounters $_obj = new GlobalCounters((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        
    }
    
    // constructor just for allocation
    public GlobalCounters(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public static boolean PRINT_STATS = false;
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public static long
                                                                                                       getAsyncsSent$O(
                                                                                                       ){
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return 0L;
        }
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public static void
                                                                                                       setAsyncsSent(
                                                                                                       final long v){
            
        }
        
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public static long
                                                                                                       getAsyncsReceived$O(
                                                                                                       ){
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return 0L;
        }
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public static void
                                                                                                       setAsyncsReceived(
                                                                                                       final long v){
            
        }
        
        
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public static long
                                                                                                       getSerializedBytes$O(
                                                                                                       ){
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return 0L;
        }
        
        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public static void
                                                                                                       setSerializedBytes(
                                                                                                       final long v){
            
        }
        
        
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public static long
                                                                                                       getDeserializedBytes$O(
                                                                                                       ){
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return 0L;
        }
        
        
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public static void
                                                                                                       setDeserializedBytes(
                                                                                                       final long v){
            
        }
        
        
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public static <$T>long
                                                                                                       serializedSize__0x10$lang$GlobalCounters$$T$O(
                                                                                                       final x10.rtt.Type $T,
                                                                                                       final $T v){
            
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
long r =
               0;
            try {r = x10.runtime.impl.java.Runtime.serialize(v).length;} catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); }
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53912 =
              r;
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return t53912;
        }
        
        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
@x10.core.X10Generated public static class X10RTMessageStats extends x10.core.Struct implements x10.x10rt.X10JavaSerializable
                                                                                                     {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, X10RTMessageStats.class);
            
            public static final x10.rtt.RuntimeType<X10RTMessageStats> $RTT = x10.rtt.NamedType.<X10RTMessageStats> make(
            "x10.lang.GlobalCounters.X10RTMessageStats", /* base class */X10RTMessageStats.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.STRUCT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(X10RTMessageStats $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + X10RTMessageStats.class + " calling"); } 
                $_obj.bytesSent = $deserializer.readLong();
                $_obj.messagesSent = $deserializer.readLong();
                $_obj.bytesReceived = $deserializer.readLong();
                $_obj.messagesReceived = $deserializer.readLong();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                X10RTMessageStats $_obj = new X10RTMessageStats((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write(this.bytesSent);
                $serializer.write(this.messagesSent);
                $serializer.write(this.bytesReceived);
                $serializer.write(this.messagesReceived);
                
            }
            
            // zero value constructor
            public X10RTMessageStats(final java.lang.System $dummy) { this.bytesSent = 0L; this.messagesSent = 0L; this.bytesReceived = 0L; this.messagesReceived = 0L; }
            // constructor just for allocation
            public X10RTMessageStats(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
// creation method for java code (1-phase java constructor)
                public X10RTMessageStats(){this((java.lang.System[]) null);
                                               $init();}
                
                // constructor for non-virtual call
                final public x10.lang.GlobalCounters.X10RTMessageStats x10$lang$GlobalCounters$X10RTMessageStats$$init$S() { {
                                                                                                                                    
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
;
                                                                                                                                    
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"

                                                                                                                                    
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53913 =
                                                                                                                                      ((long)(((int)(0))));
                                                                                                                                    
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
this.bytesSent = t53913;
                                                                                                                                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53914 =
                                                                                                                                      ((long)(((int)(0))));
                                                                                                                                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
this.messagesSent = t53914;
                                                                                                                                    
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53915 =
                                                                                                                                      ((long)(((int)(0))));
                                                                                                                                    
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
this.bytesReceived = t53915;
                                                                                                                                    
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53916 =
                                                                                                                                      ((long)(((int)(0))));
                                                                                                                                    
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
this.messagesReceived = t53916;
                                                                                                                                }
                                                                                                                                return this;
                                                                                                                                }
                
                // constructor
                public x10.lang.GlobalCounters.X10RTMessageStats $init(){return x10$lang$GlobalCounters$X10RTMessageStats$$init$S();}
                
                
                
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
// creation method for java code (1-phase java constructor)
                public X10RTMessageStats(final long bytesSent,
                                         final long messagesSent,
                                         final long bytesReceived,
                                         final long messagesReceived){this((java.lang.System[]) null);
                                                                          $init(bytesSent,messagesSent,bytesReceived,messagesReceived);}
                
                // constructor for non-virtual call
                final public x10.lang.GlobalCounters.X10RTMessageStats x10$lang$GlobalCounters$X10RTMessageStats$$init$S(final long bytesSent,
                                                                                                                         final long messagesSent,
                                                                                                                         final long bytesReceived,
                                                                                                                         final long messagesReceived) { {
                                                                                                                                                               
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
;
                                                                                                                                                               
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"

                                                                                                                                                               
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
this.bytesSent = bytesSent;
                                                                                                                                                               
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
this.messagesSent = messagesSent;
                                                                                                                                                               
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
this.bytesReceived = bytesReceived;
                                                                                                                                                               
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
this.messagesReceived = messagesReceived;
                                                                                                                                                           }
                                                                                                                                                           return this;
                                                                                                                                                           }
                
                // constructor
                public x10.lang.GlobalCounters.X10RTMessageStats $init(final long bytesSent,
                                                                       final long messagesSent,
                                                                       final long bytesReceived,
                                                                       final long messagesReceived){return x10$lang$GlobalCounters$X10RTMessageStats$$init$S(bytesSent,messagesSent,bytesReceived,messagesReceived);}
                
                
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public long bytesSent;
                
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public long messagesSent;
                
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public long bytesReceived;
                
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public long messagesReceived;
                
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public x10.lang.GlobalCounters.X10RTMessageStats
                                                                                                               $plus(
                                                                                                               ){
                    
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return this;
                }
                
                
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public x10.lang.GlobalCounters.X10RTMessageStats
                                                                                                               $minus(
                                                                                                               ){
                    
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53917 =
                      bytesSent;
                    
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53921 =
                      (-(t53917));
                    
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53918 =
                      messagesSent;
                    
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53922 =
                      (-(t53918));
                    
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53919 =
                      bytesReceived;
                    
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53923 =
                      (-(t53919));
                    
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53920 =
                      messagesReceived;
                    
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53924 =
                      (-(t53920));
                    
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t53925 =
                      new x10.lang.GlobalCounters.X10RTMessageStats((java.lang.System[]) null).$init(t53921,
                                                                                                     t53922,
                                                                                                     t53923,
                                                                                                     t53924);
                    
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return t53925;
                }
                
                
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public x10.lang.GlobalCounters.X10RTMessageStats
                                                                                                               $plus(
                                                                                                               final x10.lang.GlobalCounters.X10RTMessageStats that){
                    
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53926 =
                      bytesSent;
                    
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53927 =
                      that.
                        bytesSent;
                    
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53934 =
                      ((t53926) + (((long)(t53927))));
                    
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53928 =
                      messagesSent;
                    
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53929 =
                      that.
                        messagesSent;
                    
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53935 =
                      ((t53928) + (((long)(t53929))));
                    
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53930 =
                      bytesReceived;
                    
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53931 =
                      that.
                        bytesReceived;
                    
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53936 =
                      ((t53930) + (((long)(t53931))));
                    
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53932 =
                      messagesReceived;
                    
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53933 =
                      that.
                        messagesReceived;
                    
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53937 =
                      ((t53932) + (((long)(t53933))));
                    
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t53938 =
                      new x10.lang.GlobalCounters.X10RTMessageStats((java.lang.System[]) null).$init(t53934,
                                                                                                     t53935,
                                                                                                     t53936,
                                                                                                     t53937);
                    
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return t53938;
                }
                
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public java.lang.Object
                                                                                                               $minus(
                                                                                                               final x10.lang.GlobalCounters.X10RTMessageStats that){
                    
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t53939 =
                      that.$minus();
                    
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t53940 =
                      this.$plus(((x10.lang.GlobalCounters.X10RTMessageStats)(t53939)));
                    
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return t53940;
                }
                
                
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public java.lang.String
                                                                                                               toString(
                                                                                                               ){
                    
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53941 =
                      bytesSent;
                    
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t53942 =
                      (("[out:") + ((x10.core.Long.$box(t53941))));
                    
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t53943 =
                      ((t53942) + ("/"));
                    
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53944 =
                      messagesSent;
                    
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t53945 =
                      ((t53943) + ((x10.core.Long.$box(t53944))));
                    
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t53946 =
                      ((t53945) + (" in:"));
                    
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53947 =
                      bytesReceived;
                    
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t53948 =
                      ((t53946) + ((x10.core.Long.$box(t53947))));
                    
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t53949 =
                      ((t53948) + ("/"));
                    
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53950 =
                      messagesReceived;
                    
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t53951 =
                      ((t53949) + ((x10.core.Long.$box(t53950))));
                    
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t53952 =
                      ((t53951) + ("]"));
                    
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return t53952;
                }
                
                
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public java.lang.String
                                                                                                               typeName(
                                                                                                               ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
                
                
                
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public int
                                                                                                               hashCode(
                                                                                                               ){
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
int result =
                      1;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t53953 =
                      result;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t53955 =
                      ((8191) * (((int)(t53953))));
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53954 =
                      this.
                        bytesSent;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t53956 =
                      x10.rtt.Types.hashCode(t53954);
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t53957 =
                      ((t53955) + (((int)(t53956))));
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
result = t53957;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t53958 =
                      result;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t53960 =
                      ((8191) * (((int)(t53958))));
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53959 =
                      this.
                        messagesSent;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t53961 =
                      x10.rtt.Types.hashCode(t53959);
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t53962 =
                      ((t53960) + (((int)(t53961))));
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
result = t53962;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t53963 =
                      result;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t53965 =
                      ((8191) * (((int)(t53963))));
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53964 =
                      this.
                        bytesReceived;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t53966 =
                      x10.rtt.Types.hashCode(t53964);
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t53967 =
                      ((t53965) + (((int)(t53966))));
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
result = t53967;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t53968 =
                      result;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t53970 =
                      ((8191) * (((int)(t53968))));
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53969 =
                      this.
                        messagesReceived;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t53971 =
                      x10.rtt.Types.hashCode(t53969);
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t53972 =
                      ((t53970) + (((int)(t53971))));
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
result = t53972;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t53973 =
                      result;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return t53973;
                }
                
                
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public boolean
                                                                                                               equals(
                                                                                                               java.lang.Object other){
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.Object t53974 =
                      other;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final boolean t53975 =
                      x10.lang.GlobalCounters.X10RTMessageStats.$RTT.isInstance(t53974);
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final boolean t53976 =
                      !(t53975);
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t53976) {
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return false;
                    }
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.Object t53977 =
                      other;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t53978 =
                      ((x10.lang.GlobalCounters.X10RTMessageStats)x10.rtt.Types.asStruct(x10.lang.GlobalCounters.X10RTMessageStats.$RTT,t53977));
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final boolean t53979 =
                      this.equals$O(((x10.lang.GlobalCounters.X10RTMessageStats)(t53978)));
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return t53979;
                }
                
                
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public boolean
                                                                                                               equals$O(
                                                                                                               x10.lang.GlobalCounters.X10RTMessageStats other){
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53981 =
                      this.
                        bytesSent;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t53980 =
                      other;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53982 =
                      t53980.
                        bytesSent;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
boolean t53986 =
                      ((long) t53981) ==
                    ((long) t53982);
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t53986) {
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53984 =
                          this.
                            messagesSent;
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t53983 =
                          other;
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53985 =
                          t53983.
                            messagesSent;
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
t53986 = ((long) t53984) ==
                        ((long) t53985);
                    }
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
boolean t53990 =
                      t53986;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t53990) {
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53988 =
                          this.
                            bytesReceived;
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t53987 =
                          other;
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53989 =
                          t53987.
                            bytesReceived;
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
t53990 = ((long) t53988) ==
                        ((long) t53989);
                    }
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
boolean t53994 =
                      t53990;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t53994) {
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53992 =
                          this.
                            messagesReceived;
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t53991 =
                          other;
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t53993 =
                          t53991.
                            messagesReceived;
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
t53994 = ((long) t53992) ==
                        ((long) t53993);
                    }
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final boolean t53995 =
                      t53994;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return t53995;
                }
                
                
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public boolean
                                                                                                               _struct_equals$O(
                                                                                                               java.lang.Object other){
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.Object t53996 =
                      other;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final boolean t53997 =
                      x10.lang.GlobalCounters.X10RTMessageStats.$RTT.isInstance(t53996);
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final boolean t53998 =
                      !(t53997);
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t53998) {
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return false;
                    }
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.Object t53999 =
                      other;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54000 =
                      ((x10.lang.GlobalCounters.X10RTMessageStats)x10.rtt.Types.asStruct(x10.lang.GlobalCounters.X10RTMessageStats.$RTT,t53999));
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final boolean t54001 =
                      this._struct_equals$O(((x10.lang.GlobalCounters.X10RTMessageStats)(t54000)));
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return t54001;
                }
                
                
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public boolean
                                                                                                               _struct_equals$O(
                                                                                                               x10.lang.GlobalCounters.X10RTMessageStats other){
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54003 =
                      this.
                        bytesSent;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54002 =
                      other;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54004 =
                      t54002.
                        bytesSent;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
boolean t54008 =
                      ((long) t54003) ==
                    ((long) t54004);
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t54008) {
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54006 =
                          this.
                            messagesSent;
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54005 =
                          other;
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54007 =
                          t54005.
                            messagesSent;
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
t54008 = ((long) t54006) ==
                        ((long) t54007);
                    }
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
boolean t54012 =
                      t54008;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t54012) {
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54010 =
                          this.
                            bytesReceived;
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54009 =
                          other;
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54011 =
                          t54009.
                            bytesReceived;
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
t54012 = ((long) t54010) ==
                        ((long) t54011);
                    }
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
boolean t54016 =
                      t54012;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t54016) {
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54014 =
                          this.
                            messagesReceived;
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54013 =
                          other;
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54015 =
                          t54013.
                            messagesReceived;
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
t54016 = ((long) t54014) ==
                        ((long) t54015);
                    }
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final boolean t54017 =
                      t54016;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return t54017;
                }
                
                
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public x10.lang.GlobalCounters.X10RTMessageStats
                                                                                                               x10$lang$GlobalCounters$X10RTMessageStats$$x10$lang$GlobalCounters$X10RTMessageStats$this(
                                                                                                               ){
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return x10.lang.GlobalCounters.X10RTMessageStats.this;
                }
            
        }
        
        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
@x10.core.X10Generated public static class X10RTStats extends x10.core.Struct implements x10.x10rt.X10JavaSerializable
                                                                                                     {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, X10RTStats.class);
            
            public static final x10.rtt.RuntimeType<X10RTStats> $RTT = x10.rtt.NamedType.<X10RTStats> make(
            "x10.lang.GlobalCounters.X10RTStats", /* base class */X10RTStats.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.STRUCT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(X10RTStats $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + X10RTStats.class + " calling"); } 
                x10.lang.GlobalCounters.X10RTMessageStats msg = (x10.lang.GlobalCounters.X10RTMessageStats) $deserializer.readRef();
                $_obj.msg = msg;
                x10.lang.GlobalCounters.X10RTMessageStats put = (x10.lang.GlobalCounters.X10RTMessageStats) $deserializer.readRef();
                $_obj.put = put;
                $_obj.putCopiedBytesSent = $deserializer.readLong();
                $_obj.putCopiedBytesReceived = $deserializer.readLong();
                x10.lang.GlobalCounters.X10RTMessageStats get = (x10.lang.GlobalCounters.X10RTMessageStats) $deserializer.readRef();
                $_obj.get = get;
                $_obj.getCopiedBytesSent = $deserializer.readLong();
                $_obj.getCopiedBytesReceived = $deserializer.readLong();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                X10RTStats $_obj = new X10RTStats((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (msg instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.msg);
                } else {
                $serializer.write(this.msg);
                }
                if (put instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.put);
                } else {
                $serializer.write(this.put);
                }
                $serializer.write(this.putCopiedBytesSent);
                $serializer.write(this.putCopiedBytesReceived);
                if (get instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.get);
                } else {
                $serializer.write(this.get);
                }
                $serializer.write(this.getCopiedBytesSent);
                $serializer.write(this.getCopiedBytesReceived);
                
            }
            
            // zero value constructor
            public X10RTStats(final java.lang.System $dummy) { this.msg = new x10.lang.GlobalCounters.X10RTMessageStats($dummy); this.put = new x10.lang.GlobalCounters.X10RTMessageStats($dummy); this.putCopiedBytesSent = 0L; this.putCopiedBytesReceived = 0L; this.get = new x10.lang.GlobalCounters.X10RTMessageStats($dummy); this.getCopiedBytesSent = 0L; this.getCopiedBytesReceived = 0L; }
            // constructor just for allocation
            public X10RTStats(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
                
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
// creation method for java code (1-phase java constructor)
                public X10RTStats(){this((java.lang.System[]) null);
                                        $init();}
                
                // constructor for non-virtual call
                final public x10.lang.GlobalCounters.X10RTStats x10$lang$GlobalCounters$X10RTStats$$init$S() { {
                                                                                                                      
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
;
                                                                                                                      
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"

                                                                                                                      
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54018 =
                                                                                                                        new x10.lang.GlobalCounters.X10RTMessageStats((java.lang.System[]) null).$init();
                                                                                                                      
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
this.msg = ((x10.lang.GlobalCounters.X10RTMessageStats)(t54018));
                                                                                                                      
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54019 =
                                                                                                                        new x10.lang.GlobalCounters.X10RTMessageStats((java.lang.System[]) null).$init();
                                                                                                                      
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
this.put = ((x10.lang.GlobalCounters.X10RTMessageStats)(t54019));
                                                                                                                      
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54020 =
                                                                                                                        ((long)(((int)(0))));
                                                                                                                      
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
this.putCopiedBytesSent = t54020;
                                                                                                                      
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54021 =
                                                                                                                        ((long)(((int)(0))));
                                                                                                                      
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
this.putCopiedBytesReceived = t54021;
                                                                                                                      
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54022 =
                                                                                                                        new x10.lang.GlobalCounters.X10RTMessageStats((java.lang.System[]) null).$init();
                                                                                                                      
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
this.get = ((x10.lang.GlobalCounters.X10RTMessageStats)(t54022));
                                                                                                                      
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54023 =
                                                                                                                        ((long)(((int)(0))));
                                                                                                                      
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
this.getCopiedBytesSent = t54023;
                                                                                                                      
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54024 =
                                                                                                                        ((long)(((int)(0))));
                                                                                                                      
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
this.getCopiedBytesReceived = t54024;
                                                                                                                  }
                                                                                                                  return this;
                                                                                                                  }
                
                // constructor
                public x10.lang.GlobalCounters.X10RTStats $init(){return x10$lang$GlobalCounters$X10RTStats$$init$S();}
                
                
                
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
// creation method for java code (1-phase java constructor)
                public X10RTStats(final x10.lang.GlobalCounters.X10RTMessageStats msg,
                                  final x10.lang.GlobalCounters.X10RTMessageStats put,
                                  final long putCopiedBytesSent,
                                  final long putCopiedBytesReceived,
                                  final x10.lang.GlobalCounters.X10RTMessageStats get,
                                  final long getCopiedBytesSent,
                                  final long getCopiedBytesReceived){this((java.lang.System[]) null);
                                                                         $init(msg,put,putCopiedBytesSent,putCopiedBytesReceived,get,getCopiedBytesSent,getCopiedBytesReceived);}
                
                // constructor for non-virtual call
                final public x10.lang.GlobalCounters.X10RTStats x10$lang$GlobalCounters$X10RTStats$$init$S(final x10.lang.GlobalCounters.X10RTMessageStats msg,
                                                                                                           final x10.lang.GlobalCounters.X10RTMessageStats put,
                                                                                                           final long putCopiedBytesSent,
                                                                                                           final long putCopiedBytesReceived,
                                                                                                           final x10.lang.GlobalCounters.X10RTMessageStats get,
                                                                                                           final long getCopiedBytesSent,
                                                                                                           final long getCopiedBytesReceived) { {
                                                                                                                                                       
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
;
                                                                                                                                                       
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"

                                                                                                                                                       
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
this.msg = ((x10.lang.GlobalCounters.X10RTMessageStats)(msg));
                                                                                                                                                       
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
this.put = ((x10.lang.GlobalCounters.X10RTMessageStats)(put));
                                                                                                                                                       
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
this.putCopiedBytesSent = putCopiedBytesSent;
                                                                                                                                                       
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
this.putCopiedBytesReceived = putCopiedBytesReceived;
                                                                                                                                                       
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
;
                                                                                                                                                       
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
this.get = ((x10.lang.GlobalCounters.X10RTMessageStats)(get));
                                                                                                                                                       
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
this.getCopiedBytesSent = getCopiedBytesSent;
                                                                                                                                                       
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
this.getCopiedBytesReceived = getCopiedBytesReceived;
                                                                                                                                                   }
                                                                                                                                                   return this;
                                                                                                                                                   }
                
                // constructor
                public x10.lang.GlobalCounters.X10RTStats $init(final x10.lang.GlobalCounters.X10RTMessageStats msg,
                                                                final x10.lang.GlobalCounters.X10RTMessageStats put,
                                                                final long putCopiedBytesSent,
                                                                final long putCopiedBytesReceived,
                                                                final x10.lang.GlobalCounters.X10RTMessageStats get,
                                                                final long getCopiedBytesSent,
                                                                final long getCopiedBytesReceived){return x10$lang$GlobalCounters$X10RTStats$$init$S(msg,put,putCopiedBytesSent,putCopiedBytesReceived,get,getCopiedBytesSent,getCopiedBytesReceived);}
                
                
                
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public x10.lang.GlobalCounters.X10RTMessageStats msg;
                
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public x10.lang.GlobalCounters.X10RTMessageStats put;
                
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public long putCopiedBytesSent;
                
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public long putCopiedBytesReceived;
                
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public x10.lang.GlobalCounters.X10RTMessageStats get;
                
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public long getCopiedBytesSent;
                
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public long getCopiedBytesReceived;
                
                
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public x10.lang.GlobalCounters.X10RTStats
                                                                                                                $plus(
                                                                                                                ){
                    
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return this;
                }
                
                
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public x10.lang.GlobalCounters.X10RTStats
                                                                                                                $minus(
                                                                                                                ){
                    
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54025 =
                      ((x10.lang.GlobalCounters.X10RTMessageStats)(msg));
                    
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54032 =
                      t54025.$minus();
                    
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54026 =
                      ((x10.lang.GlobalCounters.X10RTMessageStats)(put));
                    
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54033 =
                      t54026.$minus();
                    
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54027 =
                      putCopiedBytesSent;
                    
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54034 =
                      (-(t54027));
                    
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54028 =
                      putCopiedBytesReceived;
                    
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54035 =
                      (-(t54028));
                    
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54029 =
                      ((x10.lang.GlobalCounters.X10RTMessageStats)(get));
                    
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54036 =
                      t54029.$minus();
                    
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54030 =
                      getCopiedBytesSent;
                    
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54037 =
                      (-(t54030));
                    
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54031 =
                      getCopiedBytesReceived;
                    
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54038 =
                      (-(t54031));
                    
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTStats t54039 =
                      new x10.lang.GlobalCounters.X10RTStats((java.lang.System[]) null).$init(t54032,
                                                                                              t54033,
                                                                                              t54034,
                                                                                              t54035,
                                                                                              t54036,
                                                                                              t54037,
                                                                                              t54038);
                    
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return t54039;
                }
                
                
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public x10.lang.GlobalCounters.X10RTStats
                                                                                                                $plus(
                                                                                                                final x10.lang.GlobalCounters.X10RTStats that){
                    
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54040 =
                      ((x10.lang.GlobalCounters.X10RTMessageStats)(msg));
                    
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54041 =
                      ((x10.lang.GlobalCounters.X10RTMessageStats)(that.
                                                                     msg));
                    
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54054 =
                      t54040.$plus(((x10.lang.GlobalCounters.X10RTMessageStats)(t54041)));
                    
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54042 =
                      ((x10.lang.GlobalCounters.X10RTMessageStats)(put));
                    
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54043 =
                      ((x10.lang.GlobalCounters.X10RTMessageStats)(that.
                                                                     put));
                    
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54055 =
                      t54042.$plus(((x10.lang.GlobalCounters.X10RTMessageStats)(t54043)));
                    
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54044 =
                      putCopiedBytesSent;
                    
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54045 =
                      that.
                        putCopiedBytesSent;
                    
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54056 =
                      ((t54044) + (((long)(t54045))));
                    
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54046 =
                      putCopiedBytesReceived;
                    
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54047 =
                      that.
                        putCopiedBytesReceived;
                    
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54057 =
                      ((t54046) + (((long)(t54047))));
                    
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54048 =
                      ((x10.lang.GlobalCounters.X10RTMessageStats)(get));
                    
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54049 =
                      ((x10.lang.GlobalCounters.X10RTMessageStats)(that.
                                                                     get));
                    
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54058 =
                      t54048.$plus(((x10.lang.GlobalCounters.X10RTMessageStats)(t54049)));
                    
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54050 =
                      getCopiedBytesSent;
                    
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54051 =
                      that.
                        getCopiedBytesSent;
                    
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54059 =
                      ((t54050) + (((long)(t54051))));
                    
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54052 =
                      getCopiedBytesReceived;
                    
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54053 =
                      that.
                        getCopiedBytesReceived;
                    
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54060 =
                      ((t54052) + (((long)(t54053))));
                    
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTStats t54061 =
                      new x10.lang.GlobalCounters.X10RTStats((java.lang.System[]) null).$init(t54054,
                                                                                              t54055,
                                                                                              t54056,
                                                                                              t54057,
                                                                                              t54058,
                                                                                              t54059,
                                                                                              t54060);
                    
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return t54061;
                }
                
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public java.lang.Object
                                                                                                                $minus(
                                                                                                                final x10.lang.GlobalCounters.X10RTStats that){
                    
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTStats t54062 =
                      that.$minus();
                    
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTStats t54063 =
                      this.$plus(((x10.lang.GlobalCounters.X10RTStats)(t54062)));
                    
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return t54063;
                }
                
                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public java.lang.String
                                                                                                                toString(
                                                                                                                ){
                    
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54064 =
                      ((x10.lang.GlobalCounters.X10RTMessageStats)(msg));
                    
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t54065 =
                      (("msg:") + (t54064));
                    
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t54066 =
                      ((t54065) + (" put:"));
                    
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54067 =
                      ((x10.lang.GlobalCounters.X10RTMessageStats)(put));
                    
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t54068 =
                      ((t54066) + (t54067));
                    
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t54069 =
                      ((t54068) + (" putCopiedBytesSent:"));
                    
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54070 =
                      putCopiedBytesSent;
                    
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t54071 =
                      ((t54069) + ((x10.core.Long.$box(t54070))));
                    
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t54072 =
                      ((t54071) + (" putCopiedBytesReceived:"));
                    
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54073 =
                      putCopiedBytesReceived;
                    
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t54074 =
                      ((t54072) + ((x10.core.Long.$box(t54073))));
                    
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t54075 =
                      ((t54074) + (" get:"));
                    
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54076 =
                      ((x10.lang.GlobalCounters.X10RTMessageStats)(get));
                    
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t54077 =
                      ((t54075) + (t54076));
                    
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t54078 =
                      ((t54077) + (" getCopiedBytesSent:"));
                    
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54079 =
                      getCopiedBytesSent;
                    
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t54080 =
                      ((t54078) + ((x10.core.Long.$box(t54079))));
                    
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t54081 =
                      ((t54080) + (" getCopiedBytesReceived:"));
                    
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54082 =
                      getCopiedBytesReceived;
                    
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t54083 =
                      ((t54081) + ((x10.core.Long.$box(t54082))));
                    
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return t54083;
                }
                
                
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public java.lang.String
                                                                                                               typeName(
                                                                                                               ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
                
                
                
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public int
                                                                                                               hashCode(
                                                                                                               ){
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
int result =
                      1;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54084 =
                      result;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54086 =
                      ((8191) * (((int)(t54084))));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54085 =
                      ((x10.lang.GlobalCounters.X10RTMessageStats)(this.
                                                                     msg));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54087 =
                      t54085.hashCode();
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54088 =
                      ((t54086) + (((int)(t54087))));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
result = t54088;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54089 =
                      result;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54091 =
                      ((8191) * (((int)(t54089))));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54090 =
                      ((x10.lang.GlobalCounters.X10RTMessageStats)(this.
                                                                     put));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54092 =
                      t54090.hashCode();
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54093 =
                      ((t54091) + (((int)(t54092))));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
result = t54093;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54094 =
                      result;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54096 =
                      ((8191) * (((int)(t54094))));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54095 =
                      this.
                        putCopiedBytesSent;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54097 =
                      x10.rtt.Types.hashCode(t54095);
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54098 =
                      ((t54096) + (((int)(t54097))));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
result = t54098;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54099 =
                      result;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54101 =
                      ((8191) * (((int)(t54099))));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54100 =
                      this.
                        putCopiedBytesReceived;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54102 =
                      x10.rtt.Types.hashCode(t54100);
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54103 =
                      ((t54101) + (((int)(t54102))));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
result = t54103;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54104 =
                      result;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54106 =
                      ((8191) * (((int)(t54104))));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54105 =
                      ((x10.lang.GlobalCounters.X10RTMessageStats)(this.
                                                                     get));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54107 =
                      t54105.hashCode();
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54108 =
                      ((t54106) + (((int)(t54107))));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
result = t54108;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54109 =
                      result;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54111 =
                      ((8191) * (((int)(t54109))));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54110 =
                      this.
                        getCopiedBytesSent;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54112 =
                      x10.rtt.Types.hashCode(t54110);
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54113 =
                      ((t54111) + (((int)(t54112))));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
result = t54113;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54114 =
                      result;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54116 =
                      ((8191) * (((int)(t54114))));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54115 =
                      this.
                        getCopiedBytesReceived;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54117 =
                      x10.rtt.Types.hashCode(t54115);
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54118 =
                      ((t54116) + (((int)(t54117))));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
result = t54118;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54119 =
                      result;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return t54119;
                }
                
                
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public boolean
                                                                                                               equals(
                                                                                                               java.lang.Object other){
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.Object t54120 =
                      other;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final boolean t54121 =
                      x10.lang.GlobalCounters.X10RTStats.$RTT.isInstance(t54120);
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final boolean t54122 =
                      !(t54121);
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t54122) {
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return false;
                    }
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.Object t54123 =
                      other;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTStats t54124 =
                      ((x10.lang.GlobalCounters.X10RTStats)x10.rtt.Types.asStruct(x10.lang.GlobalCounters.X10RTStats.$RTT,t54123));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final boolean t54125 =
                      this.equals$O(((x10.lang.GlobalCounters.X10RTStats)(t54124)));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return t54125;
                }
                
                
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public boolean
                                                                                                               equals$O(
                                                                                                               x10.lang.GlobalCounters.X10RTStats other){
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54127 =
                      ((x10.lang.GlobalCounters.X10RTMessageStats)(this.
                                                                     msg));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTStats t54126 =
                      other;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54128 =
                      ((x10.lang.GlobalCounters.X10RTMessageStats)(t54126.
                                                                     msg));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
boolean t54132 =
                      x10.rtt.Equality.equalsequals((t54127),(t54128));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t54132) {
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54130 =
                          ((x10.lang.GlobalCounters.X10RTMessageStats)(this.
                                                                         put));
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTStats t54129 =
                          other;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54131 =
                          ((x10.lang.GlobalCounters.X10RTMessageStats)(t54129.
                                                                         put));
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
t54132 = x10.rtt.Equality.equalsequals((t54130),(t54131));
                    }
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
boolean t54136 =
                      t54132;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t54136) {
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54134 =
                          this.
                            putCopiedBytesSent;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTStats t54133 =
                          other;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54135 =
                          t54133.
                            putCopiedBytesSent;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
t54136 = ((long) t54134) ==
                        ((long) t54135);
                    }
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
boolean t54140 =
                      t54136;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t54140) {
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54138 =
                          this.
                            putCopiedBytesReceived;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTStats t54137 =
                          other;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54139 =
                          t54137.
                            putCopiedBytesReceived;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
t54140 = ((long) t54138) ==
                        ((long) t54139);
                    }
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
boolean t54144 =
                      t54140;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t54144) {
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54142 =
                          ((x10.lang.GlobalCounters.X10RTMessageStats)(this.
                                                                         get));
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTStats t54141 =
                          other;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54143 =
                          ((x10.lang.GlobalCounters.X10RTMessageStats)(t54141.
                                                                         get));
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
t54144 = x10.rtt.Equality.equalsequals((t54142),(t54143));
                    }
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
boolean t54148 =
                      t54144;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t54148) {
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54146 =
                          this.
                            getCopiedBytesSent;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTStats t54145 =
                          other;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54147 =
                          t54145.
                            getCopiedBytesSent;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
t54148 = ((long) t54146) ==
                        ((long) t54147);
                    }
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
boolean t54152 =
                      t54148;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t54152) {
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54150 =
                          this.
                            getCopiedBytesReceived;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTStats t54149 =
                          other;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54151 =
                          t54149.
                            getCopiedBytesReceived;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
t54152 = ((long) t54150) ==
                        ((long) t54151);
                    }
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final boolean t54153 =
                      t54152;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return t54153;
                }
                
                
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public boolean
                                                                                                               _struct_equals$O(
                                                                                                               java.lang.Object other){
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.Object t54154 =
                      other;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final boolean t54155 =
                      x10.lang.GlobalCounters.X10RTStats.$RTT.isInstance(t54154);
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final boolean t54156 =
                      !(t54155);
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t54156) {
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return false;
                    }
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.Object t54157 =
                      other;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTStats t54158 =
                      ((x10.lang.GlobalCounters.X10RTStats)x10.rtt.Types.asStruct(x10.lang.GlobalCounters.X10RTStats.$RTT,t54157));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final boolean t54159 =
                      this._struct_equals$O(((x10.lang.GlobalCounters.X10RTStats)(t54158)));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return t54159;
                }
                
                
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public boolean
                                                                                                               _struct_equals$O(
                                                                                                               x10.lang.GlobalCounters.X10RTStats other){
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54161 =
                      ((x10.lang.GlobalCounters.X10RTMessageStats)(this.
                                                                     msg));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTStats t54160 =
                      other;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54162 =
                      ((x10.lang.GlobalCounters.X10RTMessageStats)(t54160.
                                                                     msg));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
boolean t54166 =
                      x10.rtt.Equality.equalsequals((t54161),(t54162));
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t54166) {
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54164 =
                          ((x10.lang.GlobalCounters.X10RTMessageStats)(this.
                                                                         put));
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTStats t54163 =
                          other;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54165 =
                          ((x10.lang.GlobalCounters.X10RTMessageStats)(t54163.
                                                                         put));
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
t54166 = x10.rtt.Equality.equalsequals((t54164),(t54165));
                    }
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
boolean t54170 =
                      t54166;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t54170) {
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54168 =
                          this.
                            putCopiedBytesSent;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTStats t54167 =
                          other;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54169 =
                          t54167.
                            putCopiedBytesSent;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
t54170 = ((long) t54168) ==
                        ((long) t54169);
                    }
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
boolean t54174 =
                      t54170;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t54174) {
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54172 =
                          this.
                            putCopiedBytesReceived;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTStats t54171 =
                          other;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54173 =
                          t54171.
                            putCopiedBytesReceived;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
t54174 = ((long) t54172) ==
                        ((long) t54173);
                    }
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
boolean t54178 =
                      t54174;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t54178) {
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54176 =
                          ((x10.lang.GlobalCounters.X10RTMessageStats)(this.
                                                                         get));
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTStats t54175 =
                          other;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54177 =
                          ((x10.lang.GlobalCounters.X10RTMessageStats)(t54175.
                                                                         get));
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
t54178 = x10.rtt.Equality.equalsequals((t54176),(t54177));
                    }
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
boolean t54182 =
                      t54178;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t54182) {
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54180 =
                          this.
                            getCopiedBytesSent;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTStats t54179 =
                          other;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54181 =
                          t54179.
                            getCopiedBytesSent;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
t54182 = ((long) t54180) ==
                        ((long) t54181);
                    }
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
boolean t54186 =
                      t54182;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t54186) {
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54184 =
                          this.
                            getCopiedBytesReceived;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTStats t54183 =
                          other;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54185 =
                          t54183.
                            getCopiedBytesReceived;
                        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
t54186 = ((long) t54184) ==
                        ((long) t54185);
                    }
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final boolean t54187 =
                      t54186;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return t54187;
                }
                
                
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public x10.lang.GlobalCounters.X10RTStats
                                                                                                               x10$lang$GlobalCounters$X10RTStats$$x10$lang$GlobalCounters$X10RTStats$this(
                                                                                                               ){
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return x10.lang.GlobalCounters.X10RTStats.this;
                }
            
        }
        
        
        
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public static x10.lang.GlobalCounters.X10RTStats
                                                                                                        getX10RTStats(
                                                                                                        ){
            {
                
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTStats t54188 =
                  new x10.lang.GlobalCounters.X10RTStats((java.lang.System[]) null).$init();
                
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return t54188;
            }
        }
        
        
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public static x10.lang.GlobalCounters.X10RTMessageStats
                                                                                                        getX10RTMessageStats(
                                                                                                        ){
            
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTStats t54189 =
              x10.lang.GlobalCounters.getX10RTStats();
            
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final x10.lang.GlobalCounters.X10RTMessageStats t54190 =
              ((x10.lang.GlobalCounters.X10RTMessageStats)(t54189.
                                                             msg));
            
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return t54190;
        }
        
        
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
public static void
                                                                                                        printStats(
                                                                                                        ){
            
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final boolean t54201 =
              x10.lang.GlobalCounters.PRINT_STATS;
            
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
if (t54201) {
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54191 =
                  x10.lang.Runtime.home().
                    id;
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t54192 =
                  (("ASYNC SENT AT PLACE ") + ((x10.core.Int.$box(t54191))));
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t54193 =
                  ((t54192) + (" = "));
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54194 =
                  x10.lang.GlobalCounters.getAsyncsSent$O();
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t54195 =
                  ((t54193) + ((x10.core.Long.$box(t54194))));
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
java.lang.System.err.println(t54195);
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final int t54196 =
                  x10.lang.Runtime.home().
                    id;
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t54197 =
                  (("ASYNC RECV AT PLACE ") + ((x10.core.Int.$box(t54196))));
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t54198 =
                  ((t54197) + (" = "));
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final long t54199 =
                  x10.lang.GlobalCounters.getAsyncsReceived$O();
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final java.lang.String t54200 =
                  ((t54198) + ((x10.core.Long.$box(t54199))));
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
java.lang.System.err.println(t54200);
            }
        }
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
final public x10.lang.GlobalCounters
                                                                                                       x10$lang$GlobalCounters$$x10$lang$GlobalCounters$this(
                                                                                                       ){
            
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
return x10.lang.GlobalCounters.this;
        }
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"
// creation method for java code (1-phase java constructor)
        public GlobalCounters(){this((java.lang.System[]) null);
                                    $init();}
        
        // constructor for non-virtual call
        final public x10.lang.GlobalCounters x10$lang$GlobalCounters$$init$S() { {
                                                                                        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"

                                                                                        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCounters.x10"

                                                                                    }
                                                                                    return this;
                                                                                    }
        
        // constructor
        public x10.lang.GlobalCounters $init(){return x10$lang$GlobalCounters$$init$S();}
        
        
        public static boolean
          getInitialized$PRINT_STATS(
          ){
            return x10.lang.GlobalCounters.PRINT_STATS;
        }
    
}
