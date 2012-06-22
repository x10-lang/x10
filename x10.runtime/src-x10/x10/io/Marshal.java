package x10.io;


@x10.core.X10Generated public interface Marshal<$T> extends x10.core.Any
{
    public static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Marshal.class);
    
    public static final x10.rtt.RuntimeType<Marshal> $RTT = x10.rtt.NamedType.<Marshal> make(
    "x10.io.Marshal", /* base class */Marshal.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {}
    );
    
        
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
$T
                                                                                              read$G(
                                                                                              final x10.io.Reader r);
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
java.lang.Object
                                                                                              write(
                                                                                              final x10.io.Writer w,
                                                                                              final java.lang.Object id$102,x10.rtt.Type t1);
        
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
@x10.core.X10Generated public static class LineMarshal extends x10.core.Ref implements x10.io.Marshal, x10.x10rt.X10JavaSerializable
                                                                                            {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, LineMarshal.class);
            
            public static final x10.rtt.RuntimeType<LineMarshal> $RTT = x10.rtt.NamedType.<LineMarshal> make(
            "x10.io.Marshal.LineMarshal", /* base class */LineMarshal.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.io.Marshal.$RTT, x10.rtt.Types.STRING), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(LineMarshal $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + LineMarshal.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                LineMarshal $_obj = new LineMarshal((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public LineMarshal(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public x10.io.Marshal.write(w:x10.io.Writer,T):void
            public java.lang.Object write(final x10.io.Writer a1, final java.lang.Object a2, final x10.rtt.Type t2) {
            write((x10.io.Writer)a1, (java.lang.String)a2);return null;
            }
            // bridge for method abstract public x10.io.Marshal.read(r:x10.io.Reader):T
            public java.lang.String
              read$G(x10.io.Reader a1){return read$O(a1);}
            
                
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public java.lang.String
                                                                                                      read$O(
                                                                                                      final x10.io.Reader r){
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final x10.util.StringBuilder sb =
                      ((x10.util.StringBuilder)(new x10.util.StringBuilder((java.lang.System[]) null).$init()));
                    
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
char ch =
                       0;
                    
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
try {try {{
                        
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
while (true) {
                            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
ch = x10.io.Marshal.Shadow.getInitialized$CHAR().read$O(((x10.io.Reader)(r)));
                            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
if (((char) ch) ==
                                                                                                                    ((char) '\n')) {
                                
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
break;
                            }
                            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
sb.add((char)(ch));
                        }
                        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
;
                    }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.io.IOException e) {
                        
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
if (((int) sb.length$O()) ==
                                                                                                                ((int) 0)) {
                            
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
throw e;
                        }
                    }
                    
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return sb.result$O();
                }
                
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public void
                                                                                                      write(
                                                                                                      final x10.io.Writer w,
                                                                                                      final java.lang.String s){
                    
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
for (
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
int i =
                                                                                                               0;
                                                                                                             ((i) < (((int)((s).length()))));
                                                                                                             
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
i = ((i) + (((int)(1))))) {
                        
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final char ch =
                          (s).charAt(((int)(i)));
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
x10.io.Marshal.Shadow.getInitialized$CHAR().write(((x10.io.Writer)(w)),
                                                                                                                                                              (char)(ch));
                    }
                }
                
                
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final public x10.io.Marshal.LineMarshal
                                                                                                      x10$io$Marshal$LineMarshal$$x10$io$Marshal$LineMarshal$this(
                                                                                                      ){
                    
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return x10.io.Marshal.LineMarshal.this;
                }
                
                
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
// creation method for java code (1-phase java constructor)
                public LineMarshal(){this((java.lang.System[]) null);
                                         $init();}
                
                // constructor for non-virtual call
                final public x10.io.Marshal.LineMarshal x10$io$Marshal$LineMarshal$$init$S() { {
                                                                                                      
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                      
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                  }
                                                                                                  return this;
                                                                                                  }
                
                // constructor
                public x10.io.Marshal.LineMarshal $init(){return x10$io$Marshal$LineMarshal$$init$S();}
                
            
        }
        
        
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
@x10.core.X10Generated public static class BooleanMarshal extends x10.core.Ref implements x10.io.Marshal, x10.x10rt.X10JavaSerializable
                                                                                            {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, BooleanMarshal.class);
            
            public static final x10.rtt.RuntimeType<BooleanMarshal> $RTT = x10.rtt.NamedType.<BooleanMarshal> make(
            "x10.io.Marshal.BooleanMarshal", /* base class */BooleanMarshal.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.io.Marshal.$RTT, x10.rtt.Types.BOOLEAN), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(BooleanMarshal $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + BooleanMarshal.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                BooleanMarshal $_obj = new BooleanMarshal((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public BooleanMarshal(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public x10.io.Marshal.write(w:x10.io.Writer,T):void
            public java.lang.Object write(final x10.io.Writer a1, final java.lang.Object a2, final x10.rtt.Type t2) {
            write((x10.io.Writer)a1, x10.core.Boolean.$unbox(a2));return null;
            }
            // bridge for method abstract public x10.io.Marshal.read(r:x10.io.Reader):T
            public x10.core.Boolean
              read$G(x10.io.Reader a1){return x10.core.Boolean.$box(read$O(a1));}
            
                
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public boolean
                                                                                                      read$O(
                                                                                                      final x10.io.Reader r){
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return ((byte) r.read$O()) !=
                    ((byte) 0);
                }
                
                
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public void
                                                                                                      write(
                                                                                                      final x10.io.Writer w,
                                                                                                      final boolean b){
                    
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.write((byte)(((byte)(int)(((int)(b
                                                                                                                  ? 0
                                                                                                                  : 1))))));
                }
                
                
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final public x10.io.Marshal.BooleanMarshal
                                                                                                      x10$io$Marshal$BooleanMarshal$$x10$io$Marshal$BooleanMarshal$this(
                                                                                                      ){
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return x10.io.Marshal.BooleanMarshal.this;
                }
                
                
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
// creation method for java code (1-phase java constructor)
                public BooleanMarshal(){this((java.lang.System[]) null);
                                            $init();}
                
                // constructor for non-virtual call
                final public x10.io.Marshal.BooleanMarshal x10$io$Marshal$BooleanMarshal$$init$S() { {
                                                                                                            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                        }
                                                                                                        return this;
                                                                                                        }
                
                // constructor
                public x10.io.Marshal.BooleanMarshal $init(){return x10$io$Marshal$BooleanMarshal$$init$S();}
                
            
        }
        
        
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
@x10.core.X10Generated public static class ByteMarshal extends x10.core.Ref implements x10.io.Marshal, x10.x10rt.X10JavaSerializable
                                                                                            {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, ByteMarshal.class);
            
            public static final x10.rtt.RuntimeType<ByteMarshal> $RTT = x10.rtt.NamedType.<ByteMarshal> make(
            "x10.io.Marshal.ByteMarshal", /* base class */ByteMarshal.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.io.Marshal.$RTT, x10.rtt.Types.BYTE), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(ByteMarshal $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + ByteMarshal.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                ByteMarshal $_obj = new ByteMarshal((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public ByteMarshal(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public x10.io.Marshal.write(w:x10.io.Writer,T):void
            public java.lang.Object write(final x10.io.Writer a1, final java.lang.Object a2, final x10.rtt.Type t2) {
            write((x10.io.Writer)a1, x10.core.Byte.$unbox(a2));return null;
            }
            // bridge for method abstract public x10.io.Marshal.read(r:x10.io.Reader):T
            public x10.core.Byte
              read$G(x10.io.Reader a1){return x10.core.Byte.$box(read$O(a1));}
            
                
                
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public byte
                                                                                                      read$O(
                                                                                                      final x10.io.Reader r){
                    
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return r.read$O();
                }
                
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public void
                                                                                                      write(
                                                                                                      final x10.io.Writer w,
                                                                                                      final byte b){
                    
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.write((byte)(b));
                }
                
                
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final public x10.io.Marshal.ByteMarshal
                                                                                                      x10$io$Marshal$ByteMarshal$$x10$io$Marshal$ByteMarshal$this(
                                                                                                      ){
                    
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return x10.io.Marshal.ByteMarshal.this;
                }
                
                
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
// creation method for java code (1-phase java constructor)
                public ByteMarshal(){this((java.lang.System[]) null);
                                         $init();}
                
                // constructor for non-virtual call
                final public x10.io.Marshal.ByteMarshal x10$io$Marshal$ByteMarshal$$init$S() { {
                                                                                                      
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                      
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                  }
                                                                                                  return this;
                                                                                                  }
                
                // constructor
                public x10.io.Marshal.ByteMarshal $init(){return x10$io$Marshal$ByteMarshal$$init$S();}
                
            
        }
        
        
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
@x10.core.X10Generated public static class UByteMarshal extends x10.core.Ref implements x10.io.Marshal, x10.x10rt.X10JavaSerializable
                                                                                            {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, UByteMarshal.class);
            
            public static final x10.rtt.RuntimeType<UByteMarshal> $RTT = x10.rtt.NamedType.<UByteMarshal> make(
            "x10.io.Marshal.UByteMarshal", /* base class */UByteMarshal.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.io.Marshal.$RTT, x10.rtt.Types.UBYTE), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(UByteMarshal $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + UByteMarshal.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                UByteMarshal $_obj = new UByteMarshal((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public UByteMarshal(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public x10.io.Marshal.write(w:x10.io.Writer,T):void
            public java.lang.Object write(final x10.io.Writer a1, final java.lang.Object a2, final x10.rtt.Type t2) {
            write__1$u((x10.io.Writer)a1, x10.core.UByte.$unbox(a2));return null;
            }
            // bridge for method abstract public x10.io.Marshal.read(r:x10.io.Reader):T
            public x10.core.UByte
              read$G(x10.io.Reader a1){return x10.core.UByte.$box(read$O(a1));}
            
                
                
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public byte
                                                                                                      read$O(
                                                                                                      final x10.io.Reader r){
                    
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return ((byte)(((byte)(r.readByte$O()))));
                }
                
                
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public void
                                                                                                      write__1$u(
                                                                                                      final x10.io.Writer w,
                                                                                                      final byte ub){
                    
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.write((byte)(((byte)(((byte)(ub))))));
                }
                
                
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final public x10.io.Marshal.UByteMarshal
                                                                                                      x10$io$Marshal$UByteMarshal$$x10$io$Marshal$UByteMarshal$this(
                                                                                                      ){
                    
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return x10.io.Marshal.UByteMarshal.this;
                }
                
                
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
// creation method for java code (1-phase java constructor)
                public UByteMarshal(){this((java.lang.System[]) null);
                                          $init();}
                
                // constructor for non-virtual call
                final public x10.io.Marshal.UByteMarshal x10$io$Marshal$UByteMarshal$$init$S() { {
                                                                                                        
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                        
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                    }
                                                                                                    return this;
                                                                                                    }
                
                // constructor
                public x10.io.Marshal.UByteMarshal $init(){return x10$io$Marshal$UByteMarshal$$init$S();}
                
            
        }
        
        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
@x10.core.X10Generated public static class CharMarshal extends x10.core.Ref implements x10.io.Marshal, x10.x10rt.X10JavaSerializable
                                                                                            {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, CharMarshal.class);
            
            public static final x10.rtt.RuntimeType<CharMarshal> $RTT = x10.rtt.NamedType.<CharMarshal> make(
            "x10.io.Marshal.CharMarshal", /* base class */CharMarshal.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.io.Marshal.$RTT, x10.rtt.Types.CHAR), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(CharMarshal $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + CharMarshal.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                CharMarshal $_obj = new CharMarshal((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public CharMarshal(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public x10.io.Marshal.write(w:x10.io.Writer,T):void
            public java.lang.Object write(final x10.io.Writer a1, final java.lang.Object a2, final x10.rtt.Type t2) {
            write((x10.io.Writer)a1, x10.core.Char.$unbox(a2));return null;
            }
            // bridge for method abstract public x10.io.Marshal.read(r:x10.io.Reader):T
            public x10.core.Char
              read$G(x10.io.Reader a1){return x10.core.Char.$box(read$O(a1));}
            
                
                
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public char
                                                                                                      read$O(
                                                                                                      final x10.io.Reader r){
                    
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final byte b1 =
                      r.read$O();
                    
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
if (((byte) b1) ==
                                                                                                            ((byte) -1)) {
                        
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
throw new x10.io.EOFException();
                    }
                    
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
if (((int) ((((int)(byte)(((byte)(b1))))) & (((int)(248))))) ==
                                                                                                            ((int) 240)) {
                        
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final byte b2 =
                          r.read$O();
                        
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final byte b3 =
                          r.read$O();
                        
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final byte b4 =
                          r.read$O();
                        
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return ((char) (((int)(((int)(byte)(((byte)(((byte) ((((byte) ((((byte) ((((byte) ((((byte) ((b1) & (((byte)(((byte)(int)(((int)(3)))))))))) << (0x7 & (((int)(18))))))) | (((byte)(((byte) ((((byte) ((b2) & (((byte)(((byte)(int)(((int)(63)))))))))) << (0x7 & (((int)(12)))))))))))) | (((byte)(((byte) ((((byte) ((b3) & (((byte)(((byte)(int)(((int)(63)))))))))) << (0x7 & (((int)(6)))))))))))) | (((byte)(((byte) ((b4) & (((byte)(((byte)(int)(((int)(63))))))))))))))))))))));
                    }
                    
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
if (((int) ((((int)(byte)(((byte)(b1))))) & (((int)(240))))) ==
                                                                                                            ((int) 224)) {
                        
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final byte b2 =
                          r.read$O();
                        
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final byte b3 =
                          r.read$O();
                        
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return ((char) (((int)(((int)(byte)(((byte)(((byte) ((((byte) ((((byte) ((((byte) ((b1) & (((byte)(((byte)(int)(((int)(31)))))))))) << (0x7 & (((int)(12))))))) | (((byte)(((byte) ((((byte) ((b2) & (((byte)(((byte)(int)(((int)(63)))))))))) << (0x7 & (((int)(6)))))))))))) | (((byte)(((byte) ((b3) & (((byte)(((byte)(int)(((int)(63))))))))))))))))))))));
                    }
                    
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
if (((int) ((((int)(byte)(((byte)(b1))))) & (((int)(224))))) ==
                                                                                                             ((int) 192)) {
                        
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final byte b2 =
                          r.read$O();
                        
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return ((char) (((int)(((int)(byte)(((byte)(((byte) ((((byte) ((((byte) ((b1) & (((byte)(((byte)(int)(((int)(31)))))))))) << (0x7 & (((int)(6))))))) | (((byte)(((byte) ((b2) & (((byte)(((byte)(int)(((int)(63))))))))))))))))))))));
                    }
                    
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return ((char) (((int)(((int)(byte)(((byte)(b1))))))));
                }
                
                
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public void
                                                                                                       write(
                                                                                                       final x10.io.Writer w,
                                                                                                       final char c){
                    
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final int i =
                      ((int) (c));
                    
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
if (((int) ((i) & (((int)(-128))))) ==
                                                                                                             ((int) 0)) {
                        
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.write((byte)(((byte)(int)(((int)(i))))));
                        
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return;
                    }
                    
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
if (((int) ((i) & (((int)(-2048))))) ==
                                                                                                             ((int) 0)) {
                        
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.write((byte)(((byte)(int)(((int)(((((((i) >> (((int)(6))))) & (((int)(31))))) | (((int)(192))))))))));
                        
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.write((byte)(((byte)(int)(((int)(((((i) & (((int)(63))))) | (((int)(128))))))))));
                        
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return;
                    }
                    
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
if (((int) ((i) & (((int)(-65536))))) ==
                                                                                                             ((int) 0)) {
                        
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.write((byte)(((byte)(int)(((int)(((((((i) >> (((int)(12))))) & (((int)(15))))) | (((int)(224))))))))));
                        
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.write((byte)(((byte)(int)(((int)(((((((i) >> (((int)(6))))) & (((int)(63))))) | (((int)(128))))))))));
                        
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.write((byte)(((byte)(int)(((int)(((((i) & (((int)(63))))) | (((int)(128))))))))));
                        
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return;
                    }
                    
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
if (((int) ((i) & (((int)(-2097152))))) ==
                                                                                                             ((int) 0)) {
                        
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.write((byte)(((byte)(int)(((int)(((((((i) >> (((int)(18))))) & (((int)(7))))) | (((int)(240))))))))));
                        
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.write((byte)(((byte)(int)(((int)(((((((i) >> (((int)(12))))) & (((int)(63))))) | (((int)(128))))))))));
                        
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.write((byte)(((byte)(int)(((int)(((((((i) >> (((int)(6))))) & (((int)(63))))) | (((int)(128))))))))));
                        
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.write((byte)(((byte)(int)(((int)(((((i) & (((int)(63))))) | (((int)(128))))))))));
                        
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return;
                    }
                }
                
                
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final public x10.io.Marshal.CharMarshal
                                                                                                      x10$io$Marshal$CharMarshal$$x10$io$Marshal$CharMarshal$this(
                                                                                                      ){
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return x10.io.Marshal.CharMarshal.this;
                }
                
                
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
// creation method for java code (1-phase java constructor)
                public CharMarshal(){this((java.lang.System[]) null);
                                         $init();}
                
                // constructor for non-virtual call
                final public x10.io.Marshal.CharMarshal x10$io$Marshal$CharMarshal$$init$S() { {
                                                                                                      
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                      
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                  }
                                                                                                  return this;
                                                                                                  }
                
                // constructor
                public x10.io.Marshal.CharMarshal $init(){return x10$io$Marshal$CharMarshal$$init$S();}
                
            
        }
        
        
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
@x10.core.X10Generated public static class ShortMarshal extends x10.core.Ref implements x10.io.Marshal, x10.x10rt.X10JavaSerializable
                                                                                             {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, ShortMarshal.class);
            
            public static final x10.rtt.RuntimeType<ShortMarshal> $RTT = x10.rtt.NamedType.<ShortMarshal> make(
            "x10.io.Marshal.ShortMarshal", /* base class */ShortMarshal.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.io.Marshal.$RTT, x10.rtt.Types.SHORT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(ShortMarshal $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + ShortMarshal.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                ShortMarshal $_obj = new ShortMarshal((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public ShortMarshal(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public x10.io.Marshal.write(w:x10.io.Writer,T):void
            public java.lang.Object write(final x10.io.Writer a1, final java.lang.Object a2, final x10.rtt.Type t2) {
            write((x10.io.Writer)a1, x10.core.Short.$unbox(a2));return null;
            }
            // bridge for method abstract public x10.io.Marshal.read(r:x10.io.Reader):T
            public x10.core.Short
              read$G(x10.io.Reader a1){return x10.core.Short.$box(read$O(a1));}
            
                
                
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public short
                                                                                                       read$O(
                                                                                                       final x10.io.Reader r){
                    
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final byte b1 =
                      r.read$O();
                    
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final byte b2 =
                      r.read$O();
                    
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return ((short)(int)(((int)(((((((((int)(byte)(((byte)(b1))))) & (((int)(255))))) << (((int)(8))))) | (((int)(((((int)(byte)(((byte)(b2))))) & (((int)(255))))))))))));
                }
                
                
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public void
                                                                                                       write(
                                                                                                       final x10.io.Writer w,
                                                                                                       final short s){
                    
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final int i =
                      ((int)(short)(((short)(s))));
                    
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final byte b1 =
                      ((byte)(int)(((int)(((i) >> (((int)(8))))))));
                    
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final byte b2 =
                      ((byte)(int)(((int)(i))));
                    
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.write((byte)(b1));
                    
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.write((byte)(b2));
                }
                
                
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final public x10.io.Marshal.ShortMarshal
                                                                                                       x10$io$Marshal$ShortMarshal$$x10$io$Marshal$ShortMarshal$this(
                                                                                                       ){
                    
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return x10.io.Marshal.ShortMarshal.this;
                }
                
                
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
// creation method for java code (1-phase java constructor)
                public ShortMarshal(){this((java.lang.System[]) null);
                                          $init();}
                
                // constructor for non-virtual call
                final public x10.io.Marshal.ShortMarshal x10$io$Marshal$ShortMarshal$$init$S() { {
                                                                                                        
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                        
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                    }
                                                                                                    return this;
                                                                                                    }
                
                // constructor
                public x10.io.Marshal.ShortMarshal $init(){return x10$io$Marshal$ShortMarshal$$init$S();}
                
            
        }
        
        
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
@x10.core.X10Generated public static class UShortMarshal extends x10.core.Ref implements x10.io.Marshal, x10.x10rt.X10JavaSerializable
                                                                                             {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, UShortMarshal.class);
            
            public static final x10.rtt.RuntimeType<UShortMarshal> $RTT = x10.rtt.NamedType.<UShortMarshal> make(
            "x10.io.Marshal.UShortMarshal", /* base class */UShortMarshal.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.io.Marshal.$RTT, x10.rtt.Types.USHORT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(UShortMarshal $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + UShortMarshal.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                UShortMarshal $_obj = new UShortMarshal((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public UShortMarshal(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public x10.io.Marshal.write(w:x10.io.Writer,T):void
            public java.lang.Object write(final x10.io.Writer a1, final java.lang.Object a2, final x10.rtt.Type t2) {
            write__1$u((x10.io.Writer)a1, x10.core.UShort.$unbox(a2));return null;
            }
            // bridge for method abstract public x10.io.Marshal.read(r:x10.io.Reader):T
            public x10.core.UShort
              read$G(x10.io.Reader a1){return x10.core.UShort.$box(read$O(a1));}
            
                
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public short
                                                                                                       read$O(
                                                                                                       final x10.io.Reader r){
                    
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return ((short)(short)(((short)(r.readShort$O()))));
                }
                
                
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public void
                                                                                                       write__1$u(
                                                                                                       final x10.io.Writer w,
                                                                                                       final short us){
                    
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.writeShort((short)(((short)(((short)(us))))));
                }
                
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final public x10.io.Marshal.UShortMarshal
                                                                                                       x10$io$Marshal$UShortMarshal$$x10$io$Marshal$UShortMarshal$this(
                                                                                                       ){
                    
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return x10.io.Marshal.UShortMarshal.this;
                }
                
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
// creation method for java code (1-phase java constructor)
                public UShortMarshal(){this((java.lang.System[]) null);
                                           $init();}
                
                // constructor for non-virtual call
                final public x10.io.Marshal.UShortMarshal x10$io$Marshal$UShortMarshal$$init$S() { {
                                                                                                          
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                          
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                      }
                                                                                                      return this;
                                                                                                      }
                
                // constructor
                public x10.io.Marshal.UShortMarshal $init(){return x10$io$Marshal$UShortMarshal$$init$S();}
                
            
        }
        
        
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
@x10.core.X10Generated public static class IntMarshal extends x10.core.Ref implements x10.io.Marshal, x10.x10rt.X10JavaSerializable
                                                                                             {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, IntMarshal.class);
            
            public static final x10.rtt.RuntimeType<IntMarshal> $RTT = x10.rtt.NamedType.<IntMarshal> make(
            "x10.io.Marshal.IntMarshal", /* base class */IntMarshal.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.io.Marshal.$RTT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(IntMarshal $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + IntMarshal.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                IntMarshal $_obj = new IntMarshal((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public IntMarshal(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public x10.io.Marshal.write(w:x10.io.Writer,T):void
            public java.lang.Object write(final x10.io.Writer a1, final java.lang.Object a2, final x10.rtt.Type t2) {
            write((x10.io.Writer)a1, x10.core.Int.$unbox(a2));return null;
            }
            // bridge for method abstract public x10.io.Marshal.read(r:x10.io.Reader):T
            public x10.core.Int
              read$G(x10.io.Reader a1){return x10.core.Int.$box(read$O(a1));}
            
                
                
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public int
                                                                                                       read$O(
                                                                                                       final x10.io.Reader r){
                    
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final byte b1 =
                      r.read$O();
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final byte b2 =
                      r.read$O();
                    
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final byte b3 =
                      r.read$O();
                    
//#line 170 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final byte b4 =
                      r.read$O();
                    
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return ((((((((((((int)(byte)(((byte)(b1))))) & (((int)(255))))) << (((int)(24))))) | (((int)(((((((int)(byte)(((byte)(b2))))) & (((int)(255))))) << (((int)(16))))))))) | (((int)(((((((int)(byte)(((byte)(b3))))) & (((int)(255))))) << (((int)(8))))))))) | (((int)(((((int)(byte)(((byte)(b4))))) & (((int)(255))))))));
                }
                
                
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public void
                                                                                                       write(
                                                                                                       final x10.io.Writer w,
                                                                                                       final int i){
                    
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final byte b1 =
                      ((byte)(int)(((int)(((i) >> (((int)(24))))))));
                    
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final byte b2 =
                      ((byte)(int)(((int)(((i) >> (((int)(16))))))));
                    
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final byte b3 =
                      ((byte)(int)(((int)(((i) >> (((int)(8))))))));
                    
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final byte b4 =
                      ((byte)(int)(((int)(i))));
                    
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.write((byte)(b1));
                    
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.write((byte)(b2));
                    
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.write((byte)(b3));
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.write((byte)(b4));
                }
                
                
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final public x10.io.Marshal.IntMarshal
                                                                                                       x10$io$Marshal$IntMarshal$$x10$io$Marshal$IntMarshal$this(
                                                                                                       ){
                    
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return x10.io.Marshal.IntMarshal.this;
                }
                
                
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
// creation method for java code (1-phase java constructor)
                public IntMarshal(){this((java.lang.System[]) null);
                                        $init();}
                
                // constructor for non-virtual call
                final public x10.io.Marshal.IntMarshal x10$io$Marshal$IntMarshal$$init$S() { {
                                                                                                    
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                    
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                }
                                                                                                return this;
                                                                                                }
                
                // constructor
                public x10.io.Marshal.IntMarshal $init(){return x10$io$Marshal$IntMarshal$$init$S();}
                
            
        }
        
        
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
@x10.core.X10Generated public static class UIntMarshal extends x10.core.Ref implements x10.io.Marshal, x10.x10rt.X10JavaSerializable
                                                                                             {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, UIntMarshal.class);
            
            public static final x10.rtt.RuntimeType<UIntMarshal> $RTT = x10.rtt.NamedType.<UIntMarshal> make(
            "x10.io.Marshal.UIntMarshal", /* base class */UIntMarshal.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.io.Marshal.$RTT, x10.rtt.Types.UINT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(UIntMarshal $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + UIntMarshal.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                UIntMarshal $_obj = new UIntMarshal((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public UIntMarshal(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public x10.io.Marshal.write(w:x10.io.Writer,T):void
            public java.lang.Object write(final x10.io.Writer a1, final java.lang.Object a2, final x10.rtt.Type t2) {
            write__1$u((x10.io.Writer)a1, x10.core.UInt.$unbox(a2));return null;
            }
            // bridge for method abstract public x10.io.Marshal.read(r:x10.io.Reader):T
            public x10.core.UInt
              read$G(x10.io.Reader a1){return x10.core.UInt.$box(read$O(a1));}
            
                
                
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public int
                                                                                                       read$O(
                                                                                                       final x10.io.Reader r){
                    
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return ((int)(((int)(r.readInt$O()))));
                }
                
                
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public void
                                                                                                       write__1$u(
                                                                                                       final x10.io.Writer w,
                                                                                                       final int ui){
                    
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.writeInt((int)(((int)((int)(ui)))));
                }
                
                
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final public x10.io.Marshal.UIntMarshal
                                                                                                       x10$io$Marshal$UIntMarshal$$x10$io$Marshal$UIntMarshal$this(
                                                                                                       ){
                    
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return x10.io.Marshal.UIntMarshal.this;
                }
                
                
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
// creation method for java code (1-phase java constructor)
                public UIntMarshal(){this((java.lang.System[]) null);
                                         $init();}
                
                // constructor for non-virtual call
                final public x10.io.Marshal.UIntMarshal x10$io$Marshal$UIntMarshal$$init$S() { {
                                                                                                      
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                      
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                  }
                                                                                                  return this;
                                                                                                  }
                
                // constructor
                public x10.io.Marshal.UIntMarshal $init(){return x10$io$Marshal$UIntMarshal$$init$S();}
                
            
        }
        
        
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
@x10.core.X10Generated public static class LongMarshal extends x10.core.Ref implements x10.io.Marshal, x10.x10rt.X10JavaSerializable
                                                                                             {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, LongMarshal.class);
            
            public static final x10.rtt.RuntimeType<LongMarshal> $RTT = x10.rtt.NamedType.<LongMarshal> make(
            "x10.io.Marshal.LongMarshal", /* base class */LongMarshal.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.io.Marshal.$RTT, x10.rtt.Types.LONG), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(LongMarshal $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + LongMarshal.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                LongMarshal $_obj = new LongMarshal((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public LongMarshal(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public x10.io.Marshal.write(w:x10.io.Writer,T):void
            public java.lang.Object write(final x10.io.Writer a1, final java.lang.Object a2, final x10.rtt.Type t2) {
            write((x10.io.Writer)a1, x10.core.Long.$unbox(a2));return null;
            }
            // bridge for method abstract public x10.io.Marshal.read(r:x10.io.Reader):T
            public x10.core.Long
              read$G(x10.io.Reader a1){return x10.core.Long.$box(read$O(a1));}
            
                
                
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public long
                                                                                                       read$O(
                                                                                                       final x10.io.Reader r){
                    
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
long l =
                      0L;
                    
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
for (
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
int i =
                                                                                                                0;
                                                                                                              ((i) < (((int)(8))));
                                                                                                              
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
i = ((i) + (((int)(1))))) {
                        
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final byte b =
                          r.read$O();
                        
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
l = ((((l) << (((int)(8))))) | (((long)(((long)(((int)(((((int)(byte)(((byte)(b))))) & (((int)(255))))))))))));
                    }
                    
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return l;
                }
                
                
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public void
                                                                                                       write(
                                                                                                       final x10.io.Writer w,
                                                                                                       final long l){
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
int shift =
                      64;
                    
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
while (((shift) > (((int)(0))))) {
                        
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
shift = ((shift) - (((int)(8))));
                        
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final byte b =
                          ((byte)(long)(((long)(((l) >> (((int)(shift))))))));
                        
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.write((byte)(b));
                    }
                }
                
                
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final public x10.io.Marshal.LongMarshal
                                                                                                       x10$io$Marshal$LongMarshal$$x10$io$Marshal$LongMarshal$this(
                                                                                                       ){
                    
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return x10.io.Marshal.LongMarshal.this;
                }
                
                
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
// creation method for java code (1-phase java constructor)
                public LongMarshal(){this((java.lang.System[]) null);
                                         $init();}
                
                // constructor for non-virtual call
                final public x10.io.Marshal.LongMarshal x10$io$Marshal$LongMarshal$$init$S() { {
                                                                                                      
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                      
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                  }
                                                                                                  return this;
                                                                                                  }
                
                // constructor
                public x10.io.Marshal.LongMarshal $init(){return x10$io$Marshal$LongMarshal$$init$S();}
                
            
        }
        
        
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
@x10.core.X10Generated public static class ULongMarshal extends x10.core.Ref implements x10.io.Marshal, x10.x10rt.X10JavaSerializable
                                                                                             {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, ULongMarshal.class);
            
            public static final x10.rtt.RuntimeType<ULongMarshal> $RTT = x10.rtt.NamedType.<ULongMarshal> make(
            "x10.io.Marshal.ULongMarshal", /* base class */ULongMarshal.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.io.Marshal.$RTT, x10.rtt.Types.ULONG), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(ULongMarshal $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + ULongMarshal.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                ULongMarshal $_obj = new ULongMarshal((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public ULongMarshal(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public x10.io.Marshal.write(w:x10.io.Writer,T):void
            public java.lang.Object write(final x10.io.Writer a1, final java.lang.Object a2, final x10.rtt.Type t2) {
            write__1$u((x10.io.Writer)a1, x10.core.ULong.$unbox(a2));return null;
            }
            // bridge for method abstract public x10.io.Marshal.read(r:x10.io.Reader):T
            public x10.core.ULong
              read$G(x10.io.Reader a1){return x10.core.ULong.$box(read$O(a1));}
            
                
                
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public long
                                                                                                       read$O(
                                                                                                       final x10.io.Reader r){
                    
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return ((long)(((long)(r.readLong$O()))));
                }
                
                
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public void
                                                                                                       write__1$u(
                                                                                                       final x10.io.Writer w,
                                                                                                       final long ul){
                    
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
w.writeLong((long)(((long)(((long)(ul))))));
                }
                
                
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final public x10.io.Marshal.ULongMarshal
                                                                                                       x10$io$Marshal$ULongMarshal$$x10$io$Marshal$ULongMarshal$this(
                                                                                                       ){
                    
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return x10.io.Marshal.ULongMarshal.this;
                }
                
                
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
// creation method for java code (1-phase java constructor)
                public ULongMarshal(){this((java.lang.System[]) null);
                                          $init();}
                
                // constructor for non-virtual call
                final public x10.io.Marshal.ULongMarshal x10$io$Marshal$ULongMarshal$$init$S() { {
                                                                                                        
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                        
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                    }
                                                                                                    return this;
                                                                                                    }
                
                // constructor
                public x10.io.Marshal.ULongMarshal $init(){return x10$io$Marshal$ULongMarshal$$init$S();}
                
            
        }
        
        
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
@x10.core.X10Generated public static class FloatMarshal extends x10.core.Ref implements x10.io.Marshal, x10.x10rt.X10JavaSerializable
                                                                                             {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, FloatMarshal.class);
            
            public static final x10.rtt.RuntimeType<FloatMarshal> $RTT = x10.rtt.NamedType.<FloatMarshal> make(
            "x10.io.Marshal.FloatMarshal", /* base class */FloatMarshal.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.io.Marshal.$RTT, x10.rtt.Types.FLOAT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(FloatMarshal $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + FloatMarshal.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                FloatMarshal $_obj = new FloatMarshal((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public FloatMarshal(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public x10.io.Marshal.write(w:x10.io.Writer,T):void
            public java.lang.Object write(final x10.io.Writer a1, final java.lang.Object a2, final x10.rtt.Type t2) {
            write((x10.io.Writer)a1, x10.core.Float.$unbox(a2));return null;
            }
            // bridge for method abstract public x10.io.Marshal.read(r:x10.io.Reader):T
            public x10.core.Float
              read$G(x10.io.Reader a1){return x10.core.Float.$box(read$O(a1));}
            
                
                
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public float
                                                                                                       read$O(
                                                                                                       final x10.io.Reader r){
                    
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final int i =
                      x10.io.Marshal.Shadow.getInitialized$INT().read$O(((x10.io.Reader)(r)));
                    
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return java.lang.Float.intBitsToFloat(((int)(i)));
                }
                
                
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public void
                                                                                                       write(
                                                                                                       final x10.io.Writer w,
                                                                                                       final float f){
                    
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final int i =
                      java.lang.Float.floatToIntBits(f);
                    
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
x10.io.Marshal.Shadow.getInitialized$INT().write(((x10.io.Writer)(w)),
                                                                                                                                                          (int)(i));
                }
                
                
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final public x10.io.Marshal.FloatMarshal
                                                                                                       x10$io$Marshal$FloatMarshal$$x10$io$Marshal$FloatMarshal$this(
                                                                                                       ){
                    
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return x10.io.Marshal.FloatMarshal.this;
                }
                
                
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
// creation method for java code (1-phase java constructor)
                public FloatMarshal(){this((java.lang.System[]) null);
                                          $init();}
                
                // constructor for non-virtual call
                final public x10.io.Marshal.FloatMarshal x10$io$Marshal$FloatMarshal$$init$S() { {
                                                                                                        
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                        
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                    }
                                                                                                    return this;
                                                                                                    }
                
                // constructor
                public x10.io.Marshal.FloatMarshal $init(){return x10$io$Marshal$FloatMarshal$$init$S();}
                
            
        }
        
        
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
@x10.core.X10Generated public static class DoubleMarshal extends x10.core.Ref implements x10.io.Marshal, x10.x10rt.X10JavaSerializable
                                                                                             {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, DoubleMarshal.class);
            
            public static final x10.rtt.RuntimeType<DoubleMarshal> $RTT = x10.rtt.NamedType.<DoubleMarshal> make(
            "x10.io.Marshal.DoubleMarshal", /* base class */DoubleMarshal.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.io.Marshal.$RTT, x10.rtt.Types.DOUBLE), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(DoubleMarshal $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + DoubleMarshal.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                DoubleMarshal $_obj = new DoubleMarshal((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public DoubleMarshal(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public x10.io.Marshal.write(w:x10.io.Writer,T):void
            public java.lang.Object write(final x10.io.Writer a1, final java.lang.Object a2, final x10.rtt.Type t2) {
            write((x10.io.Writer)a1, x10.core.Double.$unbox(a2));return null;
            }
            // bridge for method abstract public x10.io.Marshal.read(r:x10.io.Reader):T
            public x10.core.Double
              read$G(x10.io.Reader a1){return x10.core.Double.$box(read$O(a1));}
            
                
                
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public double
                                                                                                       read$O(
                                                                                                       final x10.io.Reader r){
                    
//#line 239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final long l =
                      x10.io.Marshal.Shadow.getInitialized$LONG().read$O(((x10.io.Reader)(r)));
                    
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return java.lang.Double.longBitsToDouble(((long)(l)));
                }
                
                
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public void
                                                                                                       write(
                                                                                                       final x10.io.Writer w,
                                                                                                       final double d){
                    
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final long l =
                      java.lang.Double.doubleToLongBits(d);
                    
//#line 245 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
x10.io.Marshal.Shadow.getInitialized$LONG().write(((x10.io.Writer)(w)),
                                                                                                                                                           (long)(l));
                }
                
                
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
final public x10.io.Marshal.DoubleMarshal
                                                                                                       x10$io$Marshal$DoubleMarshal$$x10$io$Marshal$DoubleMarshal$this(
                                                                                                       ){
                    
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
return x10.io.Marshal.DoubleMarshal.this;
                }
                
                
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
// creation method for java code (1-phase java constructor)
                public DoubleMarshal(){this((java.lang.System[]) null);
                                           $init();}
                
                // constructor for non-virtual call
                final public x10.io.Marshal.DoubleMarshal x10$io$Marshal$DoubleMarshal$$init$S() { {
                                                                                                          
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                          
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"

                                                                                                      }
                                                                                                      return this;
                                                                                                      }
                
                // constructor
                public x10.io.Marshal.DoubleMarshal $init(){return x10$io$Marshal$DoubleMarshal$$init$S();}
                
            
        }
        
        @x10.core.X10Generated abstract public class Shadow extends java.lang.Object implements x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Shadow.class);
            
            public static final x10.rtt.RuntimeType<Shadow> $RTT = x10.rtt.NamedType.<Shadow> make(
            "x10.io.Marshal.Shadow", /* base class */Shadow.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.ANY}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(Shadow $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Shadow.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                return null;
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public Shadow(final java.lang.System[] $dummy) { 
            }
            
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public static x10.io.Marshal.LineMarshal LINE;
                public static short fieldId$LINE;
                final public static x10.core.concurrent.AtomicInteger initStatus$LINE = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public static x10.io.Marshal.DoubleMarshal DOUBLE;
                public static short fieldId$DOUBLE;
                final public static x10.core.concurrent.AtomicInteger initStatus$DOUBLE = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
                
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public static x10.io.Marshal.FloatMarshal FLOAT;
                public static short fieldId$FLOAT;
                final public static x10.core.concurrent.AtomicInteger initStatus$FLOAT = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
                
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public static x10.io.Marshal.ULongMarshal ULONG;
                public static short fieldId$ULONG;
                final public static x10.core.concurrent.AtomicInteger initStatus$ULONG = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
                
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public static x10.io.Marshal.LongMarshal LONG;
                public static short fieldId$LONG;
                final public static x10.core.concurrent.AtomicInteger initStatus$LONG = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
                
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public static x10.io.Marshal.UIntMarshal UINT;
                public static short fieldId$UINT;
                final public static x10.core.concurrent.AtomicInteger initStatus$UINT = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public static x10.io.Marshal.IntMarshal INT;
                public static short fieldId$INT;
                final public static x10.core.concurrent.AtomicInteger initStatus$INT = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
                
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public static x10.io.Marshal.UShortMarshal USHORT;
                public static short fieldId$USHORT;
                final public static x10.core.concurrent.AtomicInteger initStatus$USHORT = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
                
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public static x10.io.Marshal.ShortMarshal SHORT;
                public static short fieldId$SHORT;
                final public static x10.core.concurrent.AtomicInteger initStatus$SHORT = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
                
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public static x10.io.Marshal.CharMarshal CHAR;
                public static short fieldId$CHAR;
                final public static x10.core.concurrent.AtomicInteger initStatus$CHAR = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
                
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public static x10.io.Marshal.UByteMarshal UBYTE;
                public static short fieldId$UBYTE;
                final public static x10.core.concurrent.AtomicInteger initStatus$UBYTE = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
                
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public static x10.io.Marshal.ByteMarshal BYTE;
                public static short fieldId$BYTE;
                final public static x10.core.concurrent.AtomicInteger initStatus$BYTE = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
                
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Marshal.x10"
public static x10.io.Marshal.BooleanMarshal BOOLEAN;
                public static short fieldId$BOOLEAN;
                final public static x10.core.concurrent.AtomicInteger initStatus$BOOLEAN = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
                
                public static void
                  getDeserialized$BOOLEAN(
                  x10.x10rt.X10JavaDeserializer deserializer){
                    x10.io.Marshal.Shadow.BOOLEAN = ((x10.io.Marshal.BooleanMarshal)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
                    x10.io.Marshal.Shadow.initStatus$BOOLEAN.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
                
                public static x10.io.Marshal.BooleanMarshal
                  getInitialized$BOOLEAN(
                  ){
                    if (((int) x10.io.Marshal.Shadow.initStatus$BOOLEAN.get()) ==
                        ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        return x10.io.Marshal.Shadow.BOOLEAN;
                    }
                    if (((int) x10.lang.Runtime.hereInt$O()) ==
                        ((int) 0) &&
                          x10.io.Marshal.Shadow.initStatus$BOOLEAN.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                                 (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                        x10.io.Marshal.Shadow.BOOLEAN = ((x10.io.Marshal.BooleanMarshal)(new x10.io.Marshal.BooleanMarshal((java.lang.System[]) null).$init()));
                        if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                              ((boolean) true)) {
                            x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.io.Marshal$Shadow.BOOLEAN")));
                        }
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.io.Marshal.Shadow.BOOLEAN)),
                                                                                  (short)(x10.io.Marshal.Shadow.fieldId$BOOLEAN));
                        x10.io.Marshal.Shadow.initStatus$BOOLEAN.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                        x10.runtime.impl.java.InitDispatcher.lockInitialized();
                        x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                    } else {
                        if (((int) x10.io.Marshal.Shadow.initStatus$BOOLEAN.get()) !=
                            ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                            x10.runtime.impl.java.InitDispatcher.lockInitialized();
                            while (((int) x10.io.Marshal.Shadow.initStatus$BOOLEAN.get()) !=
                                   ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                                x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                            }
                            x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                        }
                    }
                    return x10.io.Marshal.Shadow.BOOLEAN;
                }
                
                public static void
                  getDeserialized$BYTE(
                  x10.x10rt.X10JavaDeserializer deserializer){
                    x10.io.Marshal.Shadow.BYTE = ((x10.io.Marshal.ByteMarshal)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
                    x10.io.Marshal.Shadow.initStatus$BYTE.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
                
                public static x10.io.Marshal.ByteMarshal
                  getInitialized$BYTE(
                  ){
                    if (((int) x10.io.Marshal.Shadow.initStatus$BYTE.get()) ==
                        ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        return x10.io.Marshal.Shadow.BYTE;
                    }
                    if (((int) x10.lang.Runtime.hereInt$O()) ==
                        ((int) 0) &&
                          x10.io.Marshal.Shadow.initStatus$BYTE.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                              (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                        x10.io.Marshal.Shadow.BYTE = ((x10.io.Marshal.ByteMarshal)(new x10.io.Marshal.ByteMarshal((java.lang.System[]) null).$init()));
                        if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                              ((boolean) true)) {
                            x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.io.Marshal$Shadow.BYTE")));
                        }
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.io.Marshal.Shadow.BYTE)),
                                                                                  (short)(x10.io.Marshal.Shadow.fieldId$BYTE));
                        x10.io.Marshal.Shadow.initStatus$BYTE.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                        x10.runtime.impl.java.InitDispatcher.lockInitialized();
                        x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                    } else {
                        if (((int) x10.io.Marshal.Shadow.initStatus$BYTE.get()) !=
                            ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                            x10.runtime.impl.java.InitDispatcher.lockInitialized();
                            while (((int) x10.io.Marshal.Shadow.initStatus$BYTE.get()) !=
                                   ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                                x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                            }
                            x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                        }
                    }
                    return x10.io.Marshal.Shadow.BYTE;
                }
                
                public static void
                  getDeserialized$UBYTE(
                  x10.x10rt.X10JavaDeserializer deserializer){
                    x10.io.Marshal.Shadow.UBYTE = ((x10.io.Marshal.UByteMarshal)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
                    x10.io.Marshal.Shadow.initStatus$UBYTE.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
                
                public static x10.io.Marshal.UByteMarshal
                  getInitialized$UBYTE(
                  ){
                    if (((int) x10.io.Marshal.Shadow.initStatus$UBYTE.get()) ==
                        ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        return x10.io.Marshal.Shadow.UBYTE;
                    }
                    if (((int) x10.lang.Runtime.hereInt$O()) ==
                        ((int) 0) &&
                          x10.io.Marshal.Shadow.initStatus$UBYTE.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                               (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                        x10.io.Marshal.Shadow.UBYTE = ((x10.io.Marshal.UByteMarshal)(new x10.io.Marshal.UByteMarshal((java.lang.System[]) null).$init()));
                        if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                              ((boolean) true)) {
                            x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.io.Marshal$Shadow.UBYTE")));
                        }
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.io.Marshal.Shadow.UBYTE)),
                                                                                  (short)(x10.io.Marshal.Shadow.fieldId$UBYTE));
                        x10.io.Marshal.Shadow.initStatus$UBYTE.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                        x10.runtime.impl.java.InitDispatcher.lockInitialized();
                        x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                    } else {
                        if (((int) x10.io.Marshal.Shadow.initStatus$UBYTE.get()) !=
                            ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                            x10.runtime.impl.java.InitDispatcher.lockInitialized();
                            while (((int) x10.io.Marshal.Shadow.initStatus$UBYTE.get()) !=
                                   ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                                x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                            }
                            x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                        }
                    }
                    return x10.io.Marshal.Shadow.UBYTE;
                }
                
                public static void
                  getDeserialized$CHAR(
                  x10.x10rt.X10JavaDeserializer deserializer){
                    x10.io.Marshal.Shadow.CHAR = ((x10.io.Marshal.CharMarshal)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
                    x10.io.Marshal.Shadow.initStatus$CHAR.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
                
                public static x10.io.Marshal.CharMarshal
                  getInitialized$CHAR(
                  ){
                    if (((int) x10.io.Marshal.Shadow.initStatus$CHAR.get()) ==
                        ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        return x10.io.Marshal.Shadow.CHAR;
                    }
                    if (((int) x10.lang.Runtime.hereInt$O()) ==
                        ((int) 0) &&
                          x10.io.Marshal.Shadow.initStatus$CHAR.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                              (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                        x10.io.Marshal.Shadow.CHAR = ((x10.io.Marshal.CharMarshal)(new x10.io.Marshal.CharMarshal((java.lang.System[]) null).$init()));
                        if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                              ((boolean) true)) {
                            x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.io.Marshal$Shadow.CHAR")));
                        }
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.io.Marshal.Shadow.CHAR)),
                                                                                  (short)(x10.io.Marshal.Shadow.fieldId$CHAR));
                        x10.io.Marshal.Shadow.initStatus$CHAR.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                        x10.runtime.impl.java.InitDispatcher.lockInitialized();
                        x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                    } else {
                        if (((int) x10.io.Marshal.Shadow.initStatus$CHAR.get()) !=
                            ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                            x10.runtime.impl.java.InitDispatcher.lockInitialized();
                            while (((int) x10.io.Marshal.Shadow.initStatus$CHAR.get()) !=
                                   ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                                x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                            }
                            x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                        }
                    }
                    return x10.io.Marshal.Shadow.CHAR;
                }
                
                public static void
                  getDeserialized$SHORT(
                  x10.x10rt.X10JavaDeserializer deserializer){
                    x10.io.Marshal.Shadow.SHORT = ((x10.io.Marshal.ShortMarshal)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
                    x10.io.Marshal.Shadow.initStatus$SHORT.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
                
                public static x10.io.Marshal.ShortMarshal
                  getInitialized$SHORT(
                  ){
                    if (((int) x10.io.Marshal.Shadow.initStatus$SHORT.get()) ==
                        ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        return x10.io.Marshal.Shadow.SHORT;
                    }
                    if (((int) x10.lang.Runtime.hereInt$O()) ==
                        ((int) 0) &&
                          x10.io.Marshal.Shadow.initStatus$SHORT.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                               (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                        x10.io.Marshal.Shadow.SHORT = ((x10.io.Marshal.ShortMarshal)(new x10.io.Marshal.ShortMarshal((java.lang.System[]) null).$init()));
                        if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                              ((boolean) true)) {
                            x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.io.Marshal$Shadow.SHORT")));
                        }
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.io.Marshal.Shadow.SHORT)),
                                                                                  (short)(x10.io.Marshal.Shadow.fieldId$SHORT));
                        x10.io.Marshal.Shadow.initStatus$SHORT.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                        x10.runtime.impl.java.InitDispatcher.lockInitialized();
                        x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                    } else {
                        if (((int) x10.io.Marshal.Shadow.initStatus$SHORT.get()) !=
                            ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                            x10.runtime.impl.java.InitDispatcher.lockInitialized();
                            while (((int) x10.io.Marshal.Shadow.initStatus$SHORT.get()) !=
                                   ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                                x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                            }
                            x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                        }
                    }
                    return x10.io.Marshal.Shadow.SHORT;
                }
                
                public static void
                  getDeserialized$USHORT(
                  x10.x10rt.X10JavaDeserializer deserializer){
                    x10.io.Marshal.Shadow.USHORT = ((x10.io.Marshal.UShortMarshal)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
                    x10.io.Marshal.Shadow.initStatus$USHORT.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
                
                public static x10.io.Marshal.UShortMarshal
                  getInitialized$USHORT(
                  ){
                    if (((int) x10.io.Marshal.Shadow.initStatus$USHORT.get()) ==
                        ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        return x10.io.Marshal.Shadow.USHORT;
                    }
                    if (((int) x10.lang.Runtime.hereInt$O()) ==
                        ((int) 0) &&
                          x10.io.Marshal.Shadow.initStatus$USHORT.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                                (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                        x10.io.Marshal.Shadow.USHORT = ((x10.io.Marshal.UShortMarshal)(new x10.io.Marshal.UShortMarshal((java.lang.System[]) null).$init()));
                        if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                              ((boolean) true)) {
                            x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.io.Marshal$Shadow.USHORT")));
                        }
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.io.Marshal.Shadow.USHORT)),
                                                                                  (short)(x10.io.Marshal.Shadow.fieldId$USHORT));
                        x10.io.Marshal.Shadow.initStatus$USHORT.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                        x10.runtime.impl.java.InitDispatcher.lockInitialized();
                        x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                    } else {
                        if (((int) x10.io.Marshal.Shadow.initStatus$USHORT.get()) !=
                            ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                            x10.runtime.impl.java.InitDispatcher.lockInitialized();
                            while (((int) x10.io.Marshal.Shadow.initStatus$USHORT.get()) !=
                                   ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                                x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                            }
                            x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                        }
                    }
                    return x10.io.Marshal.Shadow.USHORT;
                }
                
                public static void
                  getDeserialized$INT(
                  x10.x10rt.X10JavaDeserializer deserializer){
                    x10.io.Marshal.Shadow.INT = ((x10.io.Marshal.IntMarshal)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
                    x10.io.Marshal.Shadow.initStatus$INT.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
                
                public static x10.io.Marshal.IntMarshal
                  getInitialized$INT(
                  ){
                    if (((int) x10.io.Marshal.Shadow.initStatus$INT.get()) ==
                        ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        return x10.io.Marshal.Shadow.INT;
                    }
                    if (((int) x10.lang.Runtime.hereInt$O()) ==
                        ((int) 0) &&
                          x10.io.Marshal.Shadow.initStatus$INT.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                             (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                        x10.io.Marshal.Shadow.INT = ((x10.io.Marshal.IntMarshal)(new x10.io.Marshal.IntMarshal((java.lang.System[]) null).$init()));
                        if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                              ((boolean) true)) {
                            x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.io.Marshal$Shadow.INT")));
                        }
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.io.Marshal.Shadow.INT)),
                                                                                  (short)(x10.io.Marshal.Shadow.fieldId$INT));
                        x10.io.Marshal.Shadow.initStatus$INT.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                        x10.runtime.impl.java.InitDispatcher.lockInitialized();
                        x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                    } else {
                        if (((int) x10.io.Marshal.Shadow.initStatus$INT.get()) !=
                            ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                            x10.runtime.impl.java.InitDispatcher.lockInitialized();
                            while (((int) x10.io.Marshal.Shadow.initStatus$INT.get()) !=
                                   ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                                x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                            }
                            x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                        }
                    }
                    return x10.io.Marshal.Shadow.INT;
                }
                
                public static void
                  getDeserialized$UINT(
                  x10.x10rt.X10JavaDeserializer deserializer){
                    x10.io.Marshal.Shadow.UINT = ((x10.io.Marshal.UIntMarshal)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
                    x10.io.Marshal.Shadow.initStatus$UINT.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
                
                public static x10.io.Marshal.UIntMarshal
                  getInitialized$UINT(
                  ){
                    if (((int) x10.io.Marshal.Shadow.initStatus$UINT.get()) ==
                        ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        return x10.io.Marshal.Shadow.UINT;
                    }
                    if (((int) x10.lang.Runtime.hereInt$O()) ==
                        ((int) 0) &&
                          x10.io.Marshal.Shadow.initStatus$UINT.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                              (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                        x10.io.Marshal.Shadow.UINT = ((x10.io.Marshal.UIntMarshal)(new x10.io.Marshal.UIntMarshal((java.lang.System[]) null).$init()));
                        if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                              ((boolean) true)) {
                            x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.io.Marshal$Shadow.UINT")));
                        }
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.io.Marshal.Shadow.UINT)),
                                                                                  (short)(x10.io.Marshal.Shadow.fieldId$UINT));
                        x10.io.Marshal.Shadow.initStatus$UINT.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                        x10.runtime.impl.java.InitDispatcher.lockInitialized();
                        x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                    } else {
                        if (((int) x10.io.Marshal.Shadow.initStatus$UINT.get()) !=
                            ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                            x10.runtime.impl.java.InitDispatcher.lockInitialized();
                            while (((int) x10.io.Marshal.Shadow.initStatus$UINT.get()) !=
                                   ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                                x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                            }
                            x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                        }
                    }
                    return x10.io.Marshal.Shadow.UINT;
                }
                
                public static void
                  getDeserialized$LONG(
                  x10.x10rt.X10JavaDeserializer deserializer){
                    x10.io.Marshal.Shadow.LONG = ((x10.io.Marshal.LongMarshal)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
                    x10.io.Marshal.Shadow.initStatus$LONG.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
                
                public static x10.io.Marshal.LongMarshal
                  getInitialized$LONG(
                  ){
                    if (((int) x10.io.Marshal.Shadow.initStatus$LONG.get()) ==
                        ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        return x10.io.Marshal.Shadow.LONG;
                    }
                    if (((int) x10.lang.Runtime.hereInt$O()) ==
                        ((int) 0) &&
                          x10.io.Marshal.Shadow.initStatus$LONG.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                              (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                        x10.io.Marshal.Shadow.LONG = ((x10.io.Marshal.LongMarshal)(new x10.io.Marshal.LongMarshal((java.lang.System[]) null).$init()));
                        if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                              ((boolean) true)) {
                            x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.io.Marshal$Shadow.LONG")));
                        }
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.io.Marshal.Shadow.LONG)),
                                                                                  (short)(x10.io.Marshal.Shadow.fieldId$LONG));
                        x10.io.Marshal.Shadow.initStatus$LONG.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                        x10.runtime.impl.java.InitDispatcher.lockInitialized();
                        x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                    } else {
                        if (((int) x10.io.Marshal.Shadow.initStatus$LONG.get()) !=
                            ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                            x10.runtime.impl.java.InitDispatcher.lockInitialized();
                            while (((int) x10.io.Marshal.Shadow.initStatus$LONG.get()) !=
                                   ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                                x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                            }
                            x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                        }
                    }
                    return x10.io.Marshal.Shadow.LONG;
                }
                
                public static void
                  getDeserialized$ULONG(
                  x10.x10rt.X10JavaDeserializer deserializer){
                    x10.io.Marshal.Shadow.ULONG = ((x10.io.Marshal.ULongMarshal)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
                    x10.io.Marshal.Shadow.initStatus$ULONG.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
                
                public static x10.io.Marshal.ULongMarshal
                  getInitialized$ULONG(
                  ){
                    if (((int) x10.io.Marshal.Shadow.initStatus$ULONG.get()) ==
                        ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        return x10.io.Marshal.Shadow.ULONG;
                    }
                    if (((int) x10.lang.Runtime.hereInt$O()) ==
                        ((int) 0) &&
                          x10.io.Marshal.Shadow.initStatus$ULONG.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                               (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                        x10.io.Marshal.Shadow.ULONG = ((x10.io.Marshal.ULongMarshal)(new x10.io.Marshal.ULongMarshal((java.lang.System[]) null).$init()));
                        if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                              ((boolean) true)) {
                            x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.io.Marshal$Shadow.ULONG")));
                        }
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.io.Marshal.Shadow.ULONG)),
                                                                                  (short)(x10.io.Marshal.Shadow.fieldId$ULONG));
                        x10.io.Marshal.Shadow.initStatus$ULONG.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                        x10.runtime.impl.java.InitDispatcher.lockInitialized();
                        x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                    } else {
                        if (((int) x10.io.Marshal.Shadow.initStatus$ULONG.get()) !=
                            ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                            x10.runtime.impl.java.InitDispatcher.lockInitialized();
                            while (((int) x10.io.Marshal.Shadow.initStatus$ULONG.get()) !=
                                   ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                                x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                            }
                            x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                        }
                    }
                    return x10.io.Marshal.Shadow.ULONG;
                }
                
                public static void
                  getDeserialized$FLOAT(
                  x10.x10rt.X10JavaDeserializer deserializer){
                    x10.io.Marshal.Shadow.FLOAT = ((x10.io.Marshal.FloatMarshal)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
                    x10.io.Marshal.Shadow.initStatus$FLOAT.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
                
                public static x10.io.Marshal.FloatMarshal
                  getInitialized$FLOAT(
                  ){
                    if (((int) x10.io.Marshal.Shadow.initStatus$FLOAT.get()) ==
                        ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        return x10.io.Marshal.Shadow.FLOAT;
                    }
                    if (((int) x10.lang.Runtime.hereInt$O()) ==
                        ((int) 0) &&
                          x10.io.Marshal.Shadow.initStatus$FLOAT.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                               (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                        x10.io.Marshal.Shadow.FLOAT = ((x10.io.Marshal.FloatMarshal)(new x10.io.Marshal.FloatMarshal((java.lang.System[]) null).$init()));
                        if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                              ((boolean) true)) {
                            x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.io.Marshal$Shadow.FLOAT")));
                        }
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.io.Marshal.Shadow.FLOAT)),
                                                                                  (short)(x10.io.Marshal.Shadow.fieldId$FLOAT));
                        x10.io.Marshal.Shadow.initStatus$FLOAT.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                        x10.runtime.impl.java.InitDispatcher.lockInitialized();
                        x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                    } else {
                        if (((int) x10.io.Marshal.Shadow.initStatus$FLOAT.get()) !=
                            ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                            x10.runtime.impl.java.InitDispatcher.lockInitialized();
                            while (((int) x10.io.Marshal.Shadow.initStatus$FLOAT.get()) !=
                                   ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                                x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                            }
                            x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                        }
                    }
                    return x10.io.Marshal.Shadow.FLOAT;
                }
                
                public static void
                  getDeserialized$DOUBLE(
                  x10.x10rt.X10JavaDeserializer deserializer){
                    x10.io.Marshal.Shadow.DOUBLE = ((x10.io.Marshal.DoubleMarshal)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
                    x10.io.Marshal.Shadow.initStatus$DOUBLE.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
                
                public static x10.io.Marshal.DoubleMarshal
                  getInitialized$DOUBLE(
                  ){
                    if (((int) x10.io.Marshal.Shadow.initStatus$DOUBLE.get()) ==
                        ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        return x10.io.Marshal.Shadow.DOUBLE;
                    }
                    if (((int) x10.lang.Runtime.hereInt$O()) ==
                        ((int) 0) &&
                          x10.io.Marshal.Shadow.initStatus$DOUBLE.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                                (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                        x10.io.Marshal.Shadow.DOUBLE = ((x10.io.Marshal.DoubleMarshal)(new x10.io.Marshal.DoubleMarshal((java.lang.System[]) null).$init()));
                        if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                              ((boolean) true)) {
                            x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.io.Marshal$Shadow.DOUBLE")));
                        }
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.io.Marshal.Shadow.DOUBLE)),
                                                                                  (short)(x10.io.Marshal.Shadow.fieldId$DOUBLE));
                        x10.io.Marshal.Shadow.initStatus$DOUBLE.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                        x10.runtime.impl.java.InitDispatcher.lockInitialized();
                        x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                    } else {
                        if (((int) x10.io.Marshal.Shadow.initStatus$DOUBLE.get()) !=
                            ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                            x10.runtime.impl.java.InitDispatcher.lockInitialized();
                            while (((int) x10.io.Marshal.Shadow.initStatus$DOUBLE.get()) !=
                                   ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                                x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                            }
                            x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                        }
                    }
                    return x10.io.Marshal.Shadow.DOUBLE;
                }
                
                public static void
                  getDeserialized$LINE(
                  x10.x10rt.X10JavaDeserializer deserializer){
                    x10.io.Marshal.Shadow.LINE = ((x10.io.Marshal.LineMarshal)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
                    x10.io.Marshal.Shadow.initStatus$LINE.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
                
                public static x10.io.Marshal.LineMarshal
                  getInitialized$LINE(
                  ){
                    if (((int) x10.io.Marshal.Shadow.initStatus$LINE.get()) ==
                        ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        return x10.io.Marshal.Shadow.LINE;
                    }
                    if (((int) x10.lang.Runtime.hereInt$O()) ==
                        ((int) 0) &&
                          x10.io.Marshal.Shadow.initStatus$LINE.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                              (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                        x10.io.Marshal.Shadow.LINE = ((x10.io.Marshal.LineMarshal)(new x10.io.Marshal.LineMarshal((java.lang.System[]) null).$init()));
                        if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                              ((boolean) true)) {
                            x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.io.Marshal$Shadow.LINE")));
                        }
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.io.Marshal.Shadow.LINE)),
                                                                                  (short)(x10.io.Marshal.Shadow.fieldId$LINE));
                        x10.io.Marshal.Shadow.initStatus$LINE.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                        x10.runtime.impl.java.InitDispatcher.lockInitialized();
                        x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                    } else {
                        if (((int) x10.io.Marshal.Shadow.initStatus$LINE.get()) !=
                            ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                            x10.runtime.impl.java.InitDispatcher.lockInitialized();
                            while (((int) x10.io.Marshal.Shadow.initStatus$LINE.get()) !=
                                   ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                                x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                            }
                            x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                        }
                    }
                    return x10.io.Marshal.Shadow.LINE;
                }
                
                static {
                           x10.io.Marshal.Shadow.fieldId$BOOLEAN = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.io.Marshal$Shadow")),
                                                                                                                                       ((java.lang.String)("BOOLEAN")))))));
                           x10.io.Marshal.Shadow.fieldId$BYTE = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.io.Marshal$Shadow")),
                                                                                                                                    ((java.lang.String)("BYTE")))))));
                           x10.io.Marshal.Shadow.fieldId$UBYTE = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.io.Marshal$Shadow")),
                                                                                                                                     ((java.lang.String)("UBYTE")))))));
                           x10.io.Marshal.Shadow.fieldId$CHAR = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.io.Marshal$Shadow")),
                                                                                                                                    ((java.lang.String)("CHAR")))))));
                           x10.io.Marshal.Shadow.fieldId$SHORT = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.io.Marshal$Shadow")),
                                                                                                                                     ((java.lang.String)("SHORT")))))));
                           x10.io.Marshal.Shadow.fieldId$USHORT = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.io.Marshal$Shadow")),
                                                                                                                                      ((java.lang.String)("USHORT")))))));
                           x10.io.Marshal.Shadow.fieldId$INT = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.io.Marshal$Shadow")),
                                                                                                                                   ((java.lang.String)("INT")))))));
                           x10.io.Marshal.Shadow.fieldId$UINT = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.io.Marshal$Shadow")),
                                                                                                                                    ((java.lang.String)("UINT")))))));
                           x10.io.Marshal.Shadow.fieldId$LONG = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.io.Marshal$Shadow")),
                                                                                                                                    ((java.lang.String)("LONG")))))));
                           x10.io.Marshal.Shadow.fieldId$ULONG = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.io.Marshal$Shadow")),
                                                                                                                                     ((java.lang.String)("ULONG")))))));
                           x10.io.Marshal.Shadow.fieldId$FLOAT = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.io.Marshal$Shadow")),
                                                                                                                                     ((java.lang.String)("FLOAT")))))));
                           x10.io.Marshal.Shadow.fieldId$DOUBLE = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.io.Marshal$Shadow")),
                                                                                                                                      ((java.lang.String)("DOUBLE")))))));
                           x10.io.Marshal.Shadow.fieldId$LINE = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.io.Marshal$Shadow")),
                                                                                                                                    ((java.lang.String)("LINE")))))));
                       }
            
        }
        
    
}
